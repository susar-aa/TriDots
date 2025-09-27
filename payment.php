<?php
// =================================================================
// PAYMENT PAGE (TRIDOTS STYLE, WEB) - V3 (API Compatible)
// Based on: CheckoutActivity.java
// =================================================================

// --- SESSION MANAGEMENT & SETUP ---
session_start();
$userSignedIn = isset($_SESSION['user_signed_in']) && $_SESSION['user_signed_in'] === true;
$userId = $_SESSION['user_data']['user_id'] ?? null;

// Security checks
if (!$userSignedIn || !$userId) {
    header("Location: login.php");
    exit();
}
if (empty($_SESSION['cart'])) {
    header("Location: cart.php");
    exit();
}
$selected_address_id = filter_input(INPUT_POST, 'address_id', FILTER_VALIDATE_INT);
if (!$selected_address_id) {
    $selected_address_id = $_SESSION['selected_address_id'] ?? null;
    if(!$selected_address_id) {
        header("Location: address_selection.php");
        exit();
    }
}
$_SESSION['selected_address_id'] = $selected_address_id;

// --- DATA FETCHING & HELPERS ---
$api_base_url = "https://lionsgoldencircle.com/Tridots/Api/";

function fetchAPI($endpoint, $params = []) {
    $url = $GLOBALS['api_base_url'] . $endpoint;
    if (!empty($params)) {
        $url .= '?' . http_build_query($params);
    }
    $json_data = @file_get_contents($url);
    if ($json_data === false) return null;
    return json_decode($json_data, true);
}

// Fetch cart items and calculate totals
$cart = $_SESSION['cart'];
$detailed_cart_items = [];
$subtotal = 0.00;
$cart_items_for_json = [];

foreach ($cart as $cartItemKey => $item) {
    $product = fetchAPI('get_product_details.php', ['product_id' => $item['product_id']]);
    if ($product) {
        $variant_id = $item['variant_id'];
        $quantity = $item['quantity'];
        $item_name = $product['product_name'];
        $item_price = (float)$product['price'];

        if ($variant_id && !empty($product['variants'])) {
            foreach ($product['variants'] as $variant) {
                if ($variant['variant_id'] == $variant_id) {
                    $item_name .= ' (' . $variant['variant_name'] . ')';
                    $item_price = (float)$variant['variant_price'];
                    break;
                }
            }
        }
        $subtotal += $item_price * $quantity;
        $detailed_cart_items[] = ['name' => $item_name, 'quantity' => $quantity, 'price' => $item_price];
        $cart_items_for_json[] = [
            'product_id' => $item['product_id'],
            'variant_id' => $variant_id,
            'quantity' => $quantity,
            'price' => $item_price
        ];
    }
}

// Fetch selected address details
$addresses = fetchAPI('get_user_addresses.php', ['user_id' => $userId]);
$selected_address = null;
if ($addresses) {
    foreach ($addresses as $addr) {
        if ($addr['address_id'] == $selected_address_id) {
            $selected_address = $addr;
            break;
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
    <title>Checkout – TRIDOTS</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <script src="https://js.stripe.com/v3/"></script>
    <style>
        :root { --primary-green: #00715A; --accent: #00B07D; --bg: #f7f8fa; --card: #fff; --text: #1d2d3d; --muted: #7b8a97; --border: #e5e7eb; --radius: 1rem; --shadow: 0 2px 16px 0 rgba(0,0,0,0.06); }
        body { margin:0; background:var(--bg); color:var(--text); font-family:'Inter',sans-serif; }
        .container { max-width: 800px; margin:0 auto; padding:24px;}
        .page-title { font-size: 2.5rem; font-weight: 800; margin: 30px 0; }
        .checkout-container { background: var(--card); border-radius: var(--radius); padding: 32px; box-shadow: var(--shadow); }
        .section-title { font-size: 1.5rem; font-weight: 700; margin-top: 0; margin-bottom: 20px; border-bottom: 1px solid var(--border); padding-bottom: 15px; }
        .summary-box, .address-box { margin-bottom: 30px; }
        .address-box p { margin: 4px 0; line-height: 1.6; }
        .address-box .name { font-weight: 600; }
        .order-item { display: flex; justify-content: space-between; padding: 8px 0; }
        .summary-totals { border-top: 2px solid var(--border); margin-top: 15px; padding-top: 15px; }
        .summary-row { display: flex; justify-content: space-between; padding: 8px 0; }
        .summary-row.grand-total { font-size: 1.3rem; font-weight: 800; color: var(--primary-green); }
        .payment-options .option { border: 2px solid var(--border); border-radius: 8px; padding: 20px; margin-bottom: 15px; cursor: pointer; }
        .payment-options .option.selected { border-color: var(--primary-green); }
        #card-element { border: 1px solid var(--border); padding: 15px; border-radius: 8px; margin-top: 10px; }
        #card-errors { color: #fa755a; margin-top: 10px; }
        .action-btn { display: block; width: 100%; border: none; padding: 16px; font-size: 1.1rem; font-weight: 700; border-radius: 8px; cursor: pointer; transition: background .2s; background: var(--primary-green); color: #fff; margin-top: 20px; }
        .action-btn:disabled { background: var(--muted); cursor: not-allowed; }
    </style>
</head>
<body>
    <main class="container">
        <h1 class="page-title">Final Checkout</h1>
        <div class="checkout-container">
            <div class="address-box">
                <h3 class="section-title">Shipping To</h3>
                <?php if ($selected_address): ?>
                    <p class="name"><?php echo htmlspecialchars($selected_address['address_title']); ?></p>
                    <p><?php echo htmlspecialchars($selected_address['full_address']); ?></p>
                    <p><?php echo htmlspecialchars($selected_address['city'] . ', ' . $selected_address['postal_code']); ?></p>
                <?php else: ?>
                    <p>Address not found. <a href="address_selection.php">Go back</a>.</p>
                <?php endif; ?>
            </div>

            <div class="summary-box">
                <h3 class="section-title">Order Summary</h3>
                <?php foreach ($detailed_cart_items as $item): ?>
                    <div class="order-item">
                        <span><?php echo htmlspecialchars($item['name']); ?> (x<?php echo $item['quantity']; ?>)</span>
                        <span>Rs. <?php echo number_format($item['price'] * $item['quantity'], 2); ?></span>
                    </div>
                <?php endforeach; ?>
                <div class="summary-totals">
                    <div class="summary-row"><span>Subtotal</span><span>Rs. <?php echo number_format($subtotal, 2); ?></span></div>
                    <div class="summary-row"><span>Delivery Fee</span><span>Rs. <?php echo number_format($delivery_fee, 2); ?></span></div>
                    <div class="summary-row grand-total"><span>Total</span><span>Rs. <?php echo number_format($grand_total, 2); ?></span></div>
                </div>
            </div>

            <form id="payment-form">
                <h3 class="section-title">Payment Method</h3>
                <div class="payment-options">
                    <div class="option selected" onclick="selectPaymentMethod('cod', this)">
                        <input type="radio" id="cod" name="payment_method" value="CashOnDelivery" checked>
                        <label for="cod">Cash on Delivery</label>
                    </div>
                    <div class="option" onclick="selectPaymentMethod('stripe', this)">
                        <input type="radio" id="stripe" name="payment_method" value="Stripe">
                        <label for="stripe">Pay with Card (Stripe)</label>
                        <div id="card-element-container" style="display:none;">
                            <div id="card-element"></div>
                            <div id="card-errors" role="alert"></div>
                        </div>
                    </div>
                </div>
                <button id="submit-button" class="action-btn">Place Order</button>
            </form>
        </div>
    </main>
    <script>
        const stripe = Stripe('pk_test_51RcpEUQtOPR6g7JfStFYgKmg4f4Wgq3ga1InRJ3ibmtkvCy27wcrgkV4O71aZ9ox5sSwhTSEB8UF7zLQSV8P8fLo00NIZDmbnL'); // Replace with your key
        const submitButton = document.getElementById('submit-button');
        let cardElement;

        function selectPaymentMethod(method, element) {
            document.querySelectorAll('.payment-options .option').forEach(el => el.classList.remove('selected'));
            element.classList.add('selected');
            document.getElementById(method).checked = true;
            const cardContainer = document.getElementById('card-element-container');
            if (method === 'stripe') {
                cardContainer.style.display = 'block';
                if (!cardElement) {
                    cardElement = stripe.elements().create('card');
                    cardElement.mount('#card-element');
                    cardElement.on('change', event => {
                        document.getElementById('card-errors').textContent = event.error ? event.error.message : '';
                    });
                }
            } else {
                cardContainer.style.display = 'none';
            }
        }

        document.getElementById('payment-form').addEventListener('submit', async (e) => {
            e.preventDefault();
            submitButton.disabled = true;
            submitButton.textContent = 'Processing...';
            
            const selectedPaymentMethod = document.querySelector('input[name="payment_method"]:checked').value;

            // --- ** FIX ** ---
            // Create a FormData object and append all required parameters, just like the Android app.
            const formData = new FormData();
            formData.append('payment_method', selectedPaymentMethod);
            formData.append('user_id', '<?php echo $userId; ?>');
            formData.append('address_id', '<?php echo $selected_address_id; ?>');
            formData.append('total_amount', '<?php echo $grand_total; ?>');
            formData.append('delivery_fee', '<?php echo $delivery_fee; ?>');
            formData.append('cart_items_json', JSON.stringify(<?php echo json_encode($cart_items_for_json); ?>));

            const createOrderResponse = await fetch('Api/create_order.php', {
                method: 'POST',
                body: formData
            });
            const orderData = await createOrderResponse.json();

            if (!orderData.success) {
                alert('Error creating order: ' + orderData.message);
                submitButton.disabled = false;
                submitButton.textContent = 'Place Order';
                return;
            }

            if (selectedPaymentMethod === 'CashOnDelivery') {
                window.location.href = `receipt.php?order_id=${orderData.order_id}`;
            } else if (selectedPaymentMethod === 'Stripe') {
                const { client_secret } = orderData;
                const { error } = await stripe.confirmCardPayment(client_secret, {
                    payment_method: { card: cardElement }
                });
                if (error) {
                    document.getElementById('card-errors').textContent = error.message;
                    submitButton.disabled = false;
                    submitButton.textContent = 'Place Order';
                } else {
                    window.location.href = `receipt.php?order_id=${orderData.order_id}`;
                }
            }
        });
    </script>
</body>
</html>
