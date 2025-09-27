<?php
// =================================================================
// ADD TO CART SCRIPT (TRIDOTS API)
// Handles adding items to the user's session cart.
// =================================================================

session_start();

// Set header to return JSON
header('Content-Type: application/json');

// --- Basic Validation ---
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['success' => false, 'message' => 'Invalid request method.']);
    exit;
}

if (!isset($_SESSION['user_signed_in']) || $_SESSION['user_signed_in'] !== true) {
    echo json_encode(['success' => false, 'message' => 'You must be signed in to add items to your cart.']);
    exit;
}

$productId = filter_input(INPUT_POST, 'product_id', FILTER_VALIDATE_INT);
$variantId = filter_input(INPUT_POST, 'variant_id', FILTER_VALIDATE_INT);
$quantity = filter_input(INPUT_POST, 'quantity', FILTER_VALIDATE_INT);

if (!$productId || !$quantity || $quantity < 1) {
    echo json_encode(['success' => false, 'message' => 'Invalid product data provided.']);
    exit;
}

// If variantId is not set or empty, make it null.
// This handles products without variants.
$variantId = $variantId ? $variantId : null;

// --- Cart Logic ---

// Initialize cart in session if it doesn't exist
if (!isset($_SESSION['cart'])) {
    $_SESSION['cart'] = [];
}

// Create a unique key for the item (product + variant)
$cartItemKey = $productId . '-' . ($variantId ?? '0');

// Check if the item already exists in the cart
if (isset($_SESSION['cart'][$cartItemKey])) {
    // If it exists, just update the quantity
    $_SESSION['cart'][$cartItemKey]['quantity'] += $quantity;
} else {
    // If it's a new item, add it to the cart
    $_SESSION['cart'][$cartItemKey] = [
        'product_id' => $productId,
        'variant_id' => $variantId,
        'quantity'   => $quantity
    ];
}

// --- Respond with Success ---
echo json_encode([
    'success' => true,
    'message' => 'Item added to cart successfully!',
    'cart_item_count' => count($_SESSION['cart']), // Optional: return new cart count
    'show_go_to_cart' => true // New flag for the frontend to show a "Go to Cart" button
]);

?>
