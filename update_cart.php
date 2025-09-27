<?php
// =================================================================
// UPDATE CART ACTION SCRIPT (TRIDOTS API)
// Handles updating quantities and removing items from the session cart.
// =================================================================

session_start();
header('Content-Type: application/json');

// --- VALIDATION ---
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['success' => false, 'message' => 'Invalid request method.']);
    exit;
}

$action = $_POST['action'] ?? null;
$itemKey = $_POST['item_key'] ?? null;

if (!$action || !$itemKey || !isset($_SESSION['cart'][$itemKey])) {
    echo json_encode(['success' => false, 'message' => 'Invalid action or item.']);
    exit;
}

// --- CART LOGIC ---
$cart = &$_SESSION['cart']; // Use a reference to modify the session directly

switch ($action) {
    case 'update':
        $quantity = filter_input(INPUT_POST, 'quantity', FILTER_VALIDATE_INT);
        if ($quantity && $quantity > 0) {
            $cart[$itemKey]['quantity'] = $quantity;
        } else {
            // If quantity is invalid or zero, remove the item
            unset($cart[$itemKey]);
        }
        break;

    case 'remove':
        unset($cart[$itemKey]);
        break;

    default:
        echo json_encode(['success' => false, 'message' => 'Unknown action.']);
        exit;
}

// --- RECALCULATE TOTALS ---
// This part is crucial for sending updated totals back to the frontend
$subtotal = 0.00;
$api_base_url = "https://lionsgoldencircle.com/Tridots/Api/"; // Make sure this is accessible

// This function needs to be defined here or included from a shared file
function fetchProductDetails($productId) {
    global $api_base_url;
    $url = $api_base_url . 'get_product_details.php?product_id=' . $productId;
    $json_data = @file_get_contents($url);
    if ($json_data === false) return null;
    $response = json_decode($json_data, true);
    return (isset($response['error'])) ? null : $response;
}

if (!empty($cart)) {
    foreach ($cart as $key => $item) {
        $product = fetchProductDetails($item['product_id']);
        if ($product) {
            $item_price = (float)$product['price'];
            if ($item['variant_id']) {
                foreach ($product['variants'] as $variant) {
                    if ($variant['variant_id'] == $item['variant_id']) {
                        $item_price = (float)$variant['variant_price'];
                        break;
                    }
                }
            }
            $subtotal += $item_price * $item['quantity'];
        }
    }
}

$delivery_fee = 500.00; // Must be consistent with cart.php
$grand_total = $subtotal + $delivery_fee;

// --- RESPOND ---
echo json_encode([
    'success' => true,
    'new_subtotal' => (float)$subtotal,
    'new_grand_total' => (float)$grand_total,
    'item_count' => count($cart)
]);
?>
