<?php
// =================================================================
// SHOPPING CART PAGE (TRIDOTS STYLE, WEB)
// Based on: CartActivity.java and CartManager.java
// =================================================================

// --- SESSION MANAGEMENT & SETUP ---
session_start();
$userSignedIn = isset($_SESSION['user_signed_in']) && $_SESSION['user_signed_in'] === true;
$username = $_SESSION['username'] ?? 'Guest';

// Initialize cart from session
$cart = $_SESSION['cart'] ?? [];

// --- DATA FETCHING & HELPERS ---
$api_base_url = "https://lionsgoldencircle.com/Tridots/Api/";

/**
 * Fetches details for a specific product from the API.
 * This is the same function from your product_detail page.
 * @param int $productId The ID of the product to fetch.
 * @return array|null The product data or null if not found.
 */
function fetchProductDetails($productId) {
    global $api_base_url;
    $url = $api_base_url . 'get_product_details.php?product_id=' . $productId;
    $json_data = @file_get_contents($url);
    if ($json_data === false) return null;
    $response = json_decode($json_data, true);
    return (isset($response['error'])) ? null : $response;
}

// --- CART PROCESSING ---
$detailed_cart_items = [];
$subtotal = 0.00;

if (!empty($cart)) {
    // NOTE: This loop makes one API call per product. For a high-traffic cart,
    // consider creating a batch API endpoint (e.g., get_products_by_ids.php)
    // to fetch all product details in a single request.
    foreach ($cart as $cartItemKey => $item) {
        $product = fetchProductDetails($item['product_id']);
        if ($product) {
            $variant_id = $item['variant_id'];
            $quantity = $item['quantity'];
            
            // Determine item details based on whether it's a variant or default
            $item_name = $product['product_name'];
            $item_price = (float)$product['price'];
            $item_image = $product['images'][0]['image_url'] ?? 'https://placehold.co/100x100/f1f5f9/64748b?text=Image';

            if ($variant_id && !empty($product['variants'])) {
                foreach ($product['variants'] as $variant) {
                    if ($variant['variant_id'] == $variant_id) {
                        $item_name .= ' (' . $variant['variant_name'] . ')';
                        $item_price = (float)$variant['variant_price'];
                        if (!empty($variant['variant_image_url'])) {
                            $item_image = $variant['variant_image_url'];
                        }
                        break;
                    }
                }
            }
            
            $line_total = $item_price * $quantity;
            $subtotal += $line_total;

            $detailed_cart_items[$cartItemKey] = [
                'product_id' => $item['product_id'],
                'variant_id' => $variant_id,
                'name' => $item_name,
                'image' => $item_image,
                'quantity' => $quantity,
                'price' => $item_price,
                'line_total' => $line_total
            ];
        }
    }
}

$delivery_fee = 500.00; // Example delivery fee
$grand_total = $subtotal + $delivery_fee;

?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Your Cart – TRIDOTS</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
    <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
    <style>
        :root { --primary-green: #00715A; --accent: #00B07D; --bg: #f7f8fa; --card: #fff; --text: #1d2d3d; --muted: #7b8a97; --border: #e5e7eb; --radius: 1rem; --shadow: 0 2px 16px 0 rgba(0,0,0,0.06); }
        body { margin:0; background:var(--bg); color:var(--text); font-family:'Inter',sans-serif; }
        .container { max-width: 1100px; margin:0 auto; padding:0 24px;}
        .main-header { background:var(--primary-green); color:#fff; padding:12px 0; box-shadow:0 2px 10px rgba(0,0,0,0.07);}
        .header-content { display:flex; justify-content:space-between; align-items:center;}
        .logo img { height:38px;}
        .main-nav a { color:#fff; text-decoration:none; font-weight:500; opacity:.87; padding:2px 4px; border-radius:7px; transition:.18s;}
        .header-actions { display:flex; gap:16px; align-items:center;}
        .btn { background:#fff; color:var(--primary-green); border:none; border-radius:30px; padding:9px 22px; font-weight:700; text-decoration:none; font-size:1rem; transition:.18s;}
        
        .page-title { font-size: 2.5rem; font-weight: 800; margin: 30px 0; }
        .cart-layout { display: grid; grid-template-columns: 1fr; gap: 32px; }
        @media (min-width: 992px) { .cart-layout { grid-template-columns: 2fr 1fr; } }

        .cart-items-list { background: var(--card); border-radius: var(--radius); padding: 24px; box-shadow: var(--shadow); }
        .cart-item { display: grid; grid-template-columns: 100px 1fr auto; gap: 20px; align-items: center; padding: 20px 0; border-bottom: 1px solid var(--border); }
        .cart-item:last-child { border-bottom: none; }
        .cart-item-image img { width: 100px; height: 80px; object-fit: cover; border-radius: 8px; }
        .cart-item-details .name { font-weight: 600; font-size: 1.1rem; margin: 0 0 5px; }
        .cart-item-details .price { color: var(--muted); }
        .cart-item-actions { text-align: right; }
        .quantity-input { width: 60px; text-align: center; padding: 8px; border: 1px solid var(--border); border-radius: 8px; font-weight: 600; }
        .remove-btn { background: none; border: none; color: var(--muted); cursor: pointer; font-size: 1.2rem; padding: 5px; }
        .remove-btn:hover { color: #e74c3c; }

        .order-summary { background: var(--card); border-radius: var(--radius); padding: 24px; box-shadow: var(--shadow); position: sticky; top: 20px; }
        .order-summary h3 { margin-top: 0; font-size: 1.5rem; }
        .summary-row { display: flex; justify-content: space-between; padding: 12px 0; }
        .summary-row.total { font-size: 1.2rem; font-weight: 700; border-top: 2px solid var(--border); margin-top: 10px; padding-top: 15px; }
        .checkout-btn { display: block; width: 100%; background: var(--primary-green); color: #fff; border: none; padding: 16px; font-size: 1.1rem; font-weight: 700; border-radius: 8px; cursor: pointer; transition: background .2s; }
        .checkout-btn:hover { background: #005a48; }
        .empty-cart { text-align: center; padding: 50px; background: var(--card); border-radius: var(--radius); }
    </style>
</head>
<body>
    <header class="main-header">
        <div class="container header-content">
            <a href="marketplace_dashboard.php" class="logo"><img src="https://lionsgoldencircle.com/Tridots/images/logo1.png" alt="TRIDOTS Logo"></a>
            <nav class="main-nav">
                <a href="marketplace.php">Marketplace</a>
                <a href="profile.php">My Profile</a>
            </nav>
            <div class="header-actions">
                <?php if($userSignedIn): ?>
                    <a href='logout.php' class='btn'>Sign Out</a>
                <?php else: ?>
                    <a href='login.php' class='btn'>Sign In</a>
                <?php endif; ?>
            </div>
        </div>
    </header>
    <main class="container">
        <h1 class="page-title">Your Shopping Cart</h1>

        <?php if (!empty($detailed_cart_items)): ?>
        <div class="cart-layout">
            <div class="cart-items-list">
                <?php foreach ($detailed_cart_items as $key => $item): ?>
                <div class="cart-item" data-item-key="<?php echo $key; ?>">
                    <div class="cart-item-image">
                        <img src="<?php echo htmlspecialchars($item['image']); ?>" alt="<?php echo htmlspecialchars($item['name']); ?>">
                    </div>
                    <div class="cart-item-details">
                        <p class="name"><?php echo htmlspecialchars($item['name']); ?></p>
                        <p class="price">Rs. <?php echo number_format($item['price'], 2); ?></p>
                    </div>
                    <div class="cart-item-actions">
                        <input type="number" class="quantity-input" value="<?php echo $item['quantity']; ?>" min="1" 
                               onchange="updateQuantity('<?php echo $key; ?>', this.value)">
                        <button class="remove-btn" onclick="removeItem('<?php echo $key; ?>')">
                            <ion-icon name="trash-outline"></ion-icon>
                        </button>
                    </div>
                </div>
                <?php endforeach; ?>
            </div>

            <div class="order-summary">
                <h3>Order Summary</h3>
                <div class="summary-row">
                    <span>Subtotal</span>
                    <span id="summary-subtotal">Rs. <?php echo number_format($subtotal, 2); ?></span>
                </div>
                <div class="summary-row">
                    <span>Delivery Fee</span>
                    <span id="summary-delivery">Rs. <?php echo number_format($delivery_fee, 2); ?></span>
                </div>
                <div class="summary-row total">
                    <span>Total</span>
                    <span id="summary-total">Rs. <?php echo number_format($grand_total, 2); ?></span>
                </div>
                <a href="address_selection.php" class="checkout-btn" style="text-align:center; margin-top:20px; text-decoration:none;">Proceed to Checkout</a>
            </div>
        </div>
        <?php else: ?>
            <div class="empty-cart">
                <h2>Your cart is empty.</h2>
                <p>Looks like you haven't added anything to your cart yet.</p>
                <a href="marketplace.php" class="btn" style="background:var(--primary-green); color:#fff; margin-top:10px;">Start Shopping</a>
            </div>
        <?php endif; ?>
    </main>

    <script>
        function handleCartUpdate(action, itemKey, quantity) {
            const formData = new FormData();
            formData.append('action', action);
            formData.append('item_key', itemKey);
            if (quantity) {
                formData.append('quantity', quantity);
            }

            fetch('update_cart.php', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    document.getElementById('summary-subtotal').innerText = 'Rs. ' + parseFloat(data.new_subtotal).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
                    document.getElementById('summary-total').innerText = 'Rs. ' + parseFloat(data.new_grand_total).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

                    if (action === 'remove') {
                        const itemElement = document.querySelector(`.cart-item[data-item-key="${itemKey}"]`);
                        if (itemElement) itemElement.remove();
                    }
                    
                    if (data.item_count === 0) {
                        window.location.reload();
                    }
                } else {
                    alert(data.message || 'Failed to update cart.');
                    // If an item fails to update, reload the page to show the correct server state
                    window.location.reload();
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred while updating the cart.');
            });
        }

        function updateQuantity(itemKey, newQuantity) {
            handleCartUpdate('update', itemKey, newQuantity);
        }

        function removeItem(itemKey) {
            if (confirm('Are you sure you want to remove this item from your cart?')) {
                handleCartUpdate('remove', itemKey);
            }
        }
    </script>
</body>
</html>
