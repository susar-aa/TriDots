<?php
// =================================================================
// ADDRESS SELECTION PAGE (TRIDOTS STYLE, WEB) - V3 (API Fix)
// =================================================================

session_start();
$userSignedIn = isset($_SESSION['user_signed_in']) && $_SESSION['user_signed_in'] === true;
$username = $_SESSION['username'] ?? 'Guest';
$userId = $_SESSION['user_data']['user_id'] ?? null;

if (!$userSignedIn || !$userId) {
    $_SESSION['redirect_url'] = 'address_selection.php';
    header("Location: login.php");
    exit();
}

if (empty($_SESSION['cart'])) {
    header("Location: cart.php");
    exit();
}

$api_base_url = "https://lionsgoldencircle.com/Tridots/Api/";

/**
 * Fetches all saved addresses for a given user ID.
 * @param int $userId The user's ID.
 * @return array A list of addresses.
 */
function fetchUserAddresses($userId) {
    global $api_base_url;
    $url = $api_base_url . 'get_user_addresses.php?user_id=' . $userId;
    $json_data = @file_get_contents($url);
    if ($json_data === false) return [];
    $response = json_decode($json_data, true);

    // --- ** FIX ** ---
    // The Android app code shows the API returns a direct JSON array `[...]`,
    // not an object `{"success":true, "addresses":[]}`.
    // This line is updated to correctly handle the raw array response.
    return is_array($response) ? $response : [];
}

$addresses = fetchUserAddresses($userId);
?>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<title>Select Delivery Address – TRIDOTS</title>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
<script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
<script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
<style>
    :root { --primary-green: #00715A; --accent: #00B07D; --bg: #f7f8fa; --card: #fff; --text: #1d2d3d; --muted: #7b8a97; --border: #e5e7eb; --radius: 1rem; --shadow: 0 2px 16px 0 rgba(0,0,0,0.06); }
    body { margin:0; background:var(--bg); color:var(--text); font-family:'Inter',sans-serif; }
    .container { max-width: 800px; margin:0 auto; padding:24px;}
    .page-title { font-size: 2.5rem; font-weight: 800; margin: 30px 0; }
    .address-selection-form { background: var(--card); border-radius: var(--radius); padding: 32px; box-shadow: var(--shadow); }
    .address-list .address-item { border: 2px solid var(--border); border-radius: 8px; padding: 20px; margin-bottom: 16px; cursor: pointer; transition: border-color .2s, box-shadow .2s; }
    .address-list .address-item:hover { border-color: var(--accent); }
    .address-list .address-item.selected { border-color: var(--primary-green); box-shadow: 0 0 0 2px var(--primary-green); }
    .address-item label { display: flex; align-items: flex-start; gap: 15px; cursor: pointer; }
    .address-item input[type="radio"] { margin-top: 5px; }
    .address-details p { margin: 0; line-height: 1.6; }
    .address-details .name { font-weight: 700; }
    .add-new-address-section { margin-top: 30px; }
    #addNewAddressForm { display: none; border-top: 1px solid var(--border); padding-top: 30px; margin-top: 20px; }
    .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
    .form-group { display: flex; flex-direction: column; }
    .form-group.full-width { grid-column: 1 / -1; }
    .form-group label { font-weight: 600; margin-bottom: 8px; font-size: 0.9rem; }
    .form-group input, .form-group textarea { padding: 12px; border: 1px solid var(--border); border-radius: 8px; font-size: 1rem; font-family: 'Inter', sans-serif; }
    .form-group textarea { resize: vertical; min-height: 80px; }
    .form-actions { margin-top: 30px; display: flex; justify-content: space-between; align-items: center; }
    .action-btn { display: inline-flex; justify-content: center; align-items: center; gap: 8px; border: none; padding: 14px 28px; font-size: 1rem; font-weight: 700; border-radius: 30px; cursor: pointer; transition: all 0.2s; text-decoration:none; }
    .primary-btn { background: var(--primary-green); color: #fff; }
    .secondary-btn { background: var(--border); color: var(--text); }
    #form-message { margin-top: 15px; font-weight: 600; }
    .msg-success { color: var(--primary-green); }
    .msg-error { color: #e74c3c; }
</style>
</head>
<body>
    <main class="container">
        <h1 class="page-title">Select Delivery Address</h1>
        <div class="address-selection-form">
            <form id="checkoutForm" action="payment.php" method="POST">
                <h3>Your Saved Addresses</h3>
                <div class="address-list">
                    <?php if (!empty($addresses)): ?>
                        <?php foreach ($addresses as $index => $addr): ?>
                        <div class="address-item <?php echo $index === 0 ? 'selected' : ''; ?>" onclick="selectAddress(this)">
                            <label>
                                <input type="radio" name="address_id" value="<?php echo $addr['address_id']; ?>" <?php echo $index === 0 ? 'checked' : ''; ?>>
                                <div class="address-details">
                                    <!-- ** FIX **: Changed 'recipient_name' to 'address_title' to match the database schema -->
                                    <p class="name"><?php echo htmlspecialchars($addr['address_title']); ?></p>
                                    <p><?php echo htmlspecialchars($addr['full_address']); ?></p>
                                    <p><?php echo htmlspecialchars($addr['city'] . ', ' . $addr['postal_code']); ?></p>
                                </div>
                            </label>
                        </div>
                        <?php endforeach; ?>
                    <?php else: ?>
                        <p>You have no saved addresses. Please add one below.</p>
                    <?php endif; ?>
                </div>

                <div class="add-new-address-section">
                    <button type="button" class="action-btn secondary-btn" onclick="toggleAddAddressForm()">Add a New Address</button>
                    <div id="addNewAddressForm" style="display:none;">
                        <h4>New Address Details</h4>
                        <div class="form-grid">
                            <div class="form-group"><label for="recipient_name">Address Title (e.g., Home, Work)</label><input type="text" id="recipient_name" name="recipient_name"></div>
                            <div class="form-group full-width"><label for="full_address">Full Address (Street, House No)</label><textarea id="full_address" name="full_address"></textarea></div>
                            <div class="form-group"><label for="city">City</label><input type="text" id="city" name="city"></div>
                            <div class="form-group"><label for="postal_code">Postal Code</label><input type="text" id="postal_code" name="postal_code"></div>
                        </div>
                        <button type="button" class="action-btn primary-btn" style="margin-top: 20px;" onclick="saveNewAddress()">Save Address</button>
                        <p id="form-message"></p>
                    </div>
                </div>

                <div class="form-actions">
                    <a href="cart.php" class="action-btn secondary-btn">Back to Cart</a>
                    <button type="submit" class="action-btn primary-btn" <?php echo empty($addresses) ? 'disabled' : ''; ?>>Continue to Payment</button>
                </div>
            </form>
        </div>
    </main>
    <script>
        function selectAddress(element) {
            document.querySelectorAll('.address-item').forEach(item => item.classList.remove('selected'));
            element.classList.add('selected');
            element.querySelector('input[type="radio"]').checked = true;
        }

        function toggleAddAddressForm() {
            const form = document.getElementById('addNewAddressForm');
            form.style.display = form.style.display === 'none' || form.style.display === '' ? 'block' : 'none';
        }

        function saveNewAddress() {
            const recipientName = document.getElementById('recipient_name').value;
            const fullAddress = document.getElementById('full_address').value;
            const city = document.getElementById('city').value;
            const postalCode = document.getElementById('postal_code').value;
            const messageEl = document.getElementById('form-message');

            if (!recipientName || !fullAddress || !city || !postalCode) {
                messageEl.textContent = 'Please fill in all fields for the new address.';
                messageEl.className = 'msg-error';
                return;
            }

            const formData = new FormData();
            formData.append('recipient_name', recipientName);
            formData.append('full_address', fullAddress);
            formData.append('city', city);
            formData.append('postal_code', postalCode);

            fetch('Api/add_user_address.php', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    messageEl.textContent = 'Address saved successfully! Page will now reload.';
                    messageEl.className = 'msg-success';
                    setTimeout(() => window.location.reload(), 2000);
                } else {
                    messageEl.textContent = data.message || 'Failed to save address.';
                    messageEl.className = 'msg-error';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                messageEl.textContent = 'An error occurred. Check the browser console for details.';
                messageEl.className = 'msg-error';
            });
        }
    </script>
</body>
</html>
