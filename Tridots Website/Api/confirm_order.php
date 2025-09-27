<?php
// Set content type to JSON
header('Content-Type: application/json');
// Allow cross-origin requests for development. In production, restrict this.
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Handle OPTIONS request for CORS preflight
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

$dbHost = 'localhost:3306';
$dbName = 'tridots';
$dbUser = 'tridots';
$dbPass = 'tridots12369';


// Read the raw POST data (JSON sent from Android)
$input = file_get_contents('php://input');
$data = json_decode($input, true);

// --- 1. Validate Input Data ---
$requiredTopLevelFields = [
    'buyer_id', // Now required based on your schema
    'total_amount', 'subtotal', 'delivery_fee', 'payment_method', 'payment_status',
    'shipping_address', 'cart_items'
];

foreach ($requiredTopLevelFields as $field) {
    if (!isset($data[$field])) {
        http_response_code(400); // Bad Request
        echo json_encode(['status' => 'error', 'message' => "Missing required field: {$field}"]);
        exit;
    }
}

$shippingAddress = $data['shipping_address'];
$requiredShippingFields = [
    'full_name',        // Expected by app, but missing in your DB schema (see note above)
    'address_line1',
    'address_line2',    // Optional
    'city',
    'district',         // New column from your schema
    'postal_code',
    'country',          // New column from your schema
    'phone_number'      // Expected by app, but missing in your DB schema (see note above)
];

foreach ($requiredShippingFields as $field) {
    // Only check mandatory fields, address_line2 is optional in app and DB
    if ($field !== 'address_line2' && !isset($shippingAddress[$field])) {
        http_response_code(400);
        echo json_encode(['status' => 'error', 'message' => "Missing required shipping address field: {$field}"]);
        exit;
    }
}

if (!is_array($data['cart_items']) || count($data['cart_items']) === 0) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Cart items are missing or empty.']);
    exit;
}

// Basic sanitization/validation for amounts (ensure they are numeric and positive)
$buyerId = filter_var($data['buyer_id'], FILTER_VALIDATE_INT);
$totalAmount = filter_var($data['total_amount'], FILTER_VALIDATE_FLOAT);
$subtotal = filter_var($data['subtotal'], FILTER_VALIDATE_FLOAT);
$deliveryFee = filter_var($data['delivery_fee'], FILTER_VALIDATE_FLOAT);

if ($buyerId === false || $buyerId <= 0) { // buyer_id must be a positive integer
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Invalid buyer ID.']);
    exit;
}
if ($totalAmount === false || $totalAmount < 0 ||
    $subtotal === false || $subtotal < 0 ||
    $deliveryFee === false || $deliveryFee < 0) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Invalid amount format for total, subtotal, or delivery fee.']);
    exit;
}


// --- 2. Database Connection ---
try {
    $pdo = new PDO("mysql:host={$dbHost};dbname={$dbName};charset=utf8mb4", $dbUser, $dbPass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $pdo->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
} catch (PDOException $e) {
    http_response_code(500); // Internal Server Error
    echo json_encode(['status' => 'error', 'message' => 'Database connection failed: ' . $e->getMessage()]);
    exit;
}

// --- 3. Start Transaction ---
$pdo->beginTransaction();

try {
    // --- 4. Insert into 'Orders' table (using your provided schema) ---
    $stmt = $pdo->prepare("INSERT INTO Orders (
        buyer_id, order_delivery_fee, total_amount, order_status, payment_status,
        shipping_address_line1, shipping_address_line2, shipping_city,
        shipping_district, shipping_postal_code, shipping_country
        -- Missing columns that app sends but DB doesn't have: shipping_full_name, shipping_phone_number, stripe_payment_intent_id
    ) VALUES (
        :buyer_id, :order_delivery_fee, :total_amount, :order_status, :payment_status,
        :shipping_address_line1, :shipping_address_line2, :shipping_city,
        :shipping_district, :shipping_postal_code, :shipping_country
    )");

    // Map app payment_status to DB enum values
    $dbPaymentStatus = 'Unpaid';
    if ($data['payment_status'] === 'paid') {
        $dbPaymentStatus = 'Paid';
    } elseif ($data['payment_status'] === 'pending') {
        $dbPaymentStatus = 'Pending';
    }
    // You might also consider 'Refunded' if that's a status the app sends or you set here.

    // Map initial order_status based on payment method/status
    $dbOrderStatus = 'Pending'; // Default
    if ($data['payment_method'] === 'stripe' && $data['payment_status'] === 'paid') {
        $dbOrderStatus = 'Processing'; // Payment successful, start processing
    } elseif ($data['payment_method'] === 'cod') {
        $dbOrderStatus = 'Pending'; // COD orders are often 'Pending' until delivery
    }
    // Ensure the status matches your ENUM ('Pending','Processing','Shipped','Delivered','Cancelled','Refunded')

    $stmt->execute([
        'buyer_id' => $buyerId,
        'order_delivery_fee' => $deliveryFee,
        'total_amount' => $totalAmount,
        'order_status' => $dbOrderStatus,
        'payment_status' => $dbPaymentStatus,
        'shipping_address_line1' => $shippingAddress['address_line1'],
        'shipping_address_line2' => $shippingAddress['address_line2'] ?? null, // Use null for optional
        'shipping_city' => $shippingAddress['city'],
        'shipping_district' => $shippingAddress['district'],
        'shipping_postal_code' => $shippingAddress['postal_code'],
        'shipping_country' => $shippingAddress['country']
        // Data for missing columns are not inserted here:
        // 'shipping_full_name' => $shippingAddress['full_name'],
        // 'shipping_phone_number' => $shippingAddress['phone_number'],
        // 'stripe_payment_intent_id' => $data['stripe_payment_intent_id'] ?? null
    ]);

    $orderId = $pdo->lastInsertId(); // Get the ID of the newly inserted order

    // --- 5. Insert into 'order_items' table ---
    $itemStmt = $pdo->prepare("INSERT INTO order_items (
        order_id, product_id, variant_id, quantity, unit_price, line_total, product_name, variant_name
    ) VALUES (
        :order_id, :product_id, :variant_id, :quantity, :unit_price, :line_total, :product_name, :variant_name
    )");

    foreach ($data['cart_items'] as $item) {
        $itemProductId = $item['product_id']; // This is productID from your cart item structure
        $itemQuantity = $item['quantity'];
        $itemUnitPrice = filter_var($item['unit_price'], FILTER_VALIDATE_FLOAT); // Use productPrice from cart
        $itemLineTotal = filter_var($item['line_total'], FILTER_VALIDATE_FLOAT);
        $itemVariantId = $item['variant_id'] ?? null;
        $itemVariantName = $item['variant_name'] ?? null;
        $productName = $item['product_name'] ?? 'Unknown Product'; // Use productName from cart

        if ($itemUnitPrice === false || $itemLineTotal === false || $itemQuantity === false || $itemQuantity <= 0) {
             throw new Exception("Invalid format or quantity for order item.");
        }

        $itemStmt->execute([
            'order_id' => $orderId,
            'product_id' => $itemProductId,
            'variant_id' => $itemVariantId,
            'quantity' => $itemQuantity,
            'unit_price' => $itemUnitPrice,
            'line_total' => $itemLineTotal,
            'product_name' => $productName,
            'variant_name' => $itemVariantName
        ]);

        // --- (Optional) Update Product Inventory ---
        // This is crucial for real-world applications.
        // Make sure to handle stock deduction carefully.
        // Example (assuming a 'products' table with 'stock' and 'product_variants' with 'stock'):
        /*
        if ($itemVariantId) {
            $updateStockStmt = $pdo->prepare("UPDATE product_variants SET stock = stock - :quantity WHERE id = :variant_id AND stock >= :quantity");
            $updateStockStmt->execute(['quantity' => $itemQuantity, 'variant_id' => $itemVariantId]);
        } else {
            $updateStockStmt = $pdo->prepare("UPDATE products SET stock = stock - :quantity WHERE id = :product_id AND stock >= :quantity");
            $updateStockStmt->execute(['quantity' => $itemQuantity, 'product_id' => $itemProductId]);
        }
        // Check affected rows to ensure stock was updated (and wasn't negative)
        if ($updateStockStmt->rowCount() === 0) {
            // If rowCount is 0, it means item was not found or stock went below 0
            // You might want to rollback the transaction and inform the user.
            throw new Exception("Insufficient stock for product ID {$itemProductId}" . ($itemVariantId ? " variant ID {$itemVariantId}" : ""));
        }
        */
    }

    // --- 6. Commit the transaction ---
    $pdo->commit();

    http_response_code(200); // OK
    echo json_encode([
        'status' => 'success',
        'message' => 'Order placed successfully!',
        'order_id' => $orderId,
        'payment_status' => $data['payment_status'],
        'order_status' => $dbOrderStatus
    ]);

} catch (Exception $e) {
    // --- 7. Rollback on error ---
    $pdo->rollBack();
    http_response_code(500); // Internal Server Error
    echo json_encode(['status' => 'error', 'message' => 'Order placement failed: ' . $e->getMessage()]);
    error_log("Order Placement Error: " . $e->getMessage());
}
?>