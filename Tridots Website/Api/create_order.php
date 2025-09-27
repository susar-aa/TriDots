<?php
// create_order.php
header('Content-Type: application/json');

// --- IMPORTANT: Enable error reporting for debugging (REMOVE IN PRODUCTION) ---
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
// ----------------------------------------------------------------------------

// --- Database connection details and logic (self-contained) ---
$db_host = 'localhost';
$db_name = 'tridots';
$db_user = 'tridots';
$db_pass = 'tridots12369';

$pdo = null; // Initialize PDO object
try {
    $pdo = new PDO("mysql:host=$db_host;dbname=$db_name;charset=utf8mb4", $db_user, $db_pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);
} catch (PDOException $e) {
    error_log("DB Connection Error in create_order.php: " . $e->getMessage());
    echo json_encode(array(
        "success" => false,
        "message" => "Database connection failed: " . $e->getMessage()
    ));
    exit();
}
// --- End of database connection logic ---

// --- Stripe Configuration ---
// !!! IMPORTANT: Adjust path for manual Stripe PHP library installation !!!
// Correct path based on your input: 'stripe-php' folder is directly inside 'Api/' directory
require_once(__DIR__ . '/stripe-php/init.php'); // Corrected path to 'stripe-php'
\Stripe\Stripe::setApiKey('sk_test_51RcpEUQtOPR6g7JfG3vtB8nAUEu21rLSQyaoWP1dr7zNJnwHWGqUHsQwOwujoFpDcZyvEaf7WDSSPUub7OIFxB0C00ZD1vLvhI'); // Replace with your Stripe Secret Key
// Define your currency and minimum amount (e.g., 50.00 LKR = 5000 in cents)
$currency = 'lkr'; // Change to your currency (e.g., 'usd', 'eur', 'lkr')
$minimum_amount_for_stripe = 50.00; // Minimum amount for a transaction in your base currency

$response = array();

// Ensure it's a POST request and necessary data is present
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405); // Method Not Allowed
    $response['success'] = false;
    $response['message'] = "Invalid request method.";
    echo json_encode($response);
    exit();
}

$required_params = ['user_id', 'address_id', 'total_amount', 'delivery_fee', 'payment_method', 'cart_items_json'];
foreach ($required_params as $param) {
    if (!isset($_POST[$param]) || (empty($_POST[$param]) && $param !== 'delivery_fee')) { // delivery_fee can be 0
        http_response_code(400); // Bad Request
        $response['success'] = false;
        $response['message'] = "Missing required parameter: " . $param;
        echo json_encode($response);
        exit();
    }
}

$user_id = (int)$_POST['user_id'];
$address_id = (int)$_POST['address_id'];
$total_amount_decimal = (float)$_POST['total_amount']; // Use a float for calculation before converting to int for Stripe
$delivery_fee = (float)$_POST['delivery_fee'];
$payment_method = $_POST['payment_method'];
$cart_items_json = $_POST['cart_items_json'];

// Convert total_amount to integer in smallest currency unit (e.g., cents) for Stripe
$amount_for_stripe = (int)($total_amount_decimal * 100);

// Validate minimum amount for Stripe
if ($payment_method === 'Stripe' && $total_amount_decimal < $minimum_amount_for_stripe) {
    http_response_code(400);
    $response['success'] = false;
    $response['message'] = "Total amount must be at least LKR " . number_format($minimum_amount_for_stripe, 2) . " for Stripe payments.";
    echo json_encode($response);
    exit();
}
// Also validate that total_amount_decimal is positive if payment method is Stripe
if ($payment_method === 'Stripe' && $total_amount_decimal <= 0) {
    http_response_code(400);
    $response['success'] = false;
    $response['message'] = "Total amount must be positive for Stripe payments.";
    echo json_encode($response);
    exit();
}


// Decode cart items JSON
$cart_items = json_decode($cart_items_json, true);

if (json_last_error() !== JSON_ERROR_NONE) {
    http_response_code(400);
    $response['success'] = false;
    $response['message'] = "Invalid JSON for cart items: " . json_last_error_msg();
    echo json_encode($response);
    exit();
}

if (empty($cart_items)) {
    http_response_code(400);
    $response['success'] = false;
    $response['message'] = "Cart items array is empty.";
    echo json_encode($response);
    exit();
}

// Start a transaction for atomicity
$pdo->beginTransaction();

try {
    // 1. Insert into Orders table
    // Initial payment_status is 'Unpaid' or 'Pending' for all orders.
    // It will be updated after Stripe payment confirmation.
    $initial_payment_status = ($payment_method === 'CashOnDelivery') ? 'Pending Cash' : 'Unpaid'; // Custom status for COD
    $order_status = 'Pending'; // All orders start as pending

    $stmt_order = $pdo->prepare("INSERT INTO Orders
        (user_id, address_id, total_amount, delivery_fee, payment_method, order_status, payment_status)
        VALUES (:user_id, :address_id, :total_amount, :delivery_fee, :payment_method, :order_status, :payment_status)");

    $stmt_order->bindParam(':user_id', $user_id, PDO::PARAM_INT);
    $stmt_order->bindParam(':address_id', $address_id, PDO::PARAM_INT);
    $stmt_order->bindParam(':total_amount', $total_amount_decimal); // Store actual decimal value
    $stmt_order->bindParam(':delivery_fee', $delivery_fee);
    $stmt_order->bindParam(':payment_method', $payment_method);
    $stmt_order->bindParam(':order_status', $order_status);
    $stmt_order->bindParam(':payment_status', $initial_payment_status);

    $stmt_order->execute();
    $order_id = $pdo->lastInsertId(); // Get the ID of the newly created order

    // 2. Insert into Order_Items table
    $stmt_item = $pdo->prepare("INSERT INTO Order_Items
        (order_id, product_id, variant_id, quantity, unit_price)
        VALUES (:order_id, :product_id, :variant_id, :quantity, :unit_price)");

    foreach ($cart_items as $item) {
        $product_id = (int)$item['product_id'];
        $quantity = (int)$item['quantity'];
        $unit_price = (float)$item['price'];
        $variant_id = isset($item['variant_id']) && !empty($item['variant_id']) ? (int)$item['variant_id'] : null;

        // Optional: You might want to fetch product details (like stock) from Marketplace table
        // and validate/deduct stock here before inserting order items.
        // For now, we're trusting the app's cart.

        $stmt_item->bindParam(':order_id', $order_id, PDO::PARAM_INT);
        $stmt_item->bindParam(':product_id', $product_id, PDO::PARAM_INT);
        // PDO handles null for INT, but explicitly set type for safety if $variant_id can be null
        $stmt_item->bindParam(':variant_id', $variant_id, PDO::PARAM_INT);
        $stmt_item->bindParam(':quantity', $quantity, PDO::PARAM_INT);
        $stmt_item->bindParam(':unit_price', $unit_price);
        $stmt_item->execute();
    }

    // --- 3. Create Stripe Payment Intent (if payment_method is Stripe) ---
    $client_secret = null;
    $payment_intent_id = null;

    if ($payment_method === 'Stripe') {
        try {
            // Validate amount for Stripe
            if ($amount_for_stripe <= 0) {
                 throw new Exception("Stripe amount is not positive: " . $amount_for_stripe);
            }

            $paymentIntent = \Stripe\PaymentIntent::create([
                'amount' => $amount_for_stripe, // Amount in smallest currency unit
                'currency' => $currency,
                'payment_method_types' => ['card'], // 'card' or other types you enable
                'description' => "Order #{$order_id} for user {$user_id}",
                'metadata' => [
                    'order_id' => $order_id,
                    'user_id' => $user_id,
                    'customer_name' => $user_id, // Fetch actual user name if available
                    'address_id' => $address_id,
                ],
                // Optional: Customer ID if you integrate with Stripe Customers for saved cards
                // 'customer' => $stripe_customer_id, // e.g., 'cus_xyz'
                // 'setup_future_usage' => 'off_session', // if you want to save card details
            ]);

            $client_secret = $paymentIntent->client_secret;
            $payment_intent_id = $paymentIntent->id; // Get Stripe's PaymentIntent ID

            // Update the order in DB with Stripe PaymentIntent ID
            $stmt_update_order = $pdo->prepare("UPDATE Orders SET stripe_payment_intent_id = :pi_id WHERE order_id = :order_id");
            $stmt_update_order->bindParam(':pi_id', $payment_intent_id);
            $stmt_update_order->bindParam(':order_id', $order_id, PDO::PARAM_INT);
            $stmt_update_order->execute();

        } catch (\Stripe\Exception\ApiErrorException $e) {
            // Log Stripe specific error
            error_log("Stripe API Error during PaymentIntent creation: " . $e->getMessage() . " Code: " . $e->getStripeCode());
            throw new Exception("Stripe payment initiation failed: " . $e->getMessage());
        } catch (Exception $e) {
            error_log("General error during PaymentIntent creation: " . $e->getMessage());
            throw new Exception("Payment initiation failed: " . $e->getMessage());
        }
    }

    $pdo->commit(); // Commit the transaction if everything succeeded so far

    $response['success'] = true;
    $response['message'] = "Order created successfully.";
    $response['order_id'] = $order_id;
    if ($payment_method === 'Stripe') {
        $response['client_secret'] = $client_secret; // Send client secret back to Android
        // Optionally send publishable key from backend (recommended for dynamic keys)
        $response['stripe_publishable_key'] = 'pk_test_51RcpEUQtOPR6g7JfStFYgKmg4f4Wgq3ga1InRJ3ibmtkvCy27wcrgkV4O71aZ9ox5sSwhTSEB8UF7zLQSV8P8fLo00NIZDmbnL';
    }

} catch (PDOException $e) {
    $pdo->rollBack(); // Rollback on database error
    http_response_code(500);
    $response['success'] = false;
    $response['message'] = "Database error during order creation: " . $e->getMessage();
    error_log("create_order.php PDO Exception: " . $e->getMessage() . " SQLSTATE: " . $e->getCode());
} catch (Exception $e) {
    $pdo->rollBack(); // Rollback on general exception
    http_response_code(500);
    $response['success'] = false;
    $response['message'] = "An error occurred during order processing: " . $e->getMessage();
    error_log("create_order.php General Exception: " . $e->getMessage());
}

echo json_encode($response);
?>
