<?php
// =================================================================
// PROFILE PAGE (TRIDOTS STYLE, WEB)
// Corrected on: Monday, 7th July 2025
// Features Added: Marketplace Orders, Categorized Inquiries, Logout Button
// Version: 3.1 (Complete Code)
// =================================================================

// --- AUTHENTICATION GATEKEEPER ---
session_start();

if (!isset($_SESSION['user_signed_in']) || $_SESSION['user_signed_in'] !== true) {
    header("Location: login.php");
    exit();
}

$userData = $_SESSION['user_data'];
$username = $_SESSION['username'];
$userId = $userData['user_id'] ?? null;

// --- API & HELPER FUNCTIONS ---
$api_base_url = "https://lionsgoldencircle.com/Tridots/Api/";
$image_base_url = "https://lionsgoldencircle.com/Tridots/";

function makeApiPostRequest($url, $params) {
    $options = ['http' => ['header'  => "Content-type: application/x-www-form-urlencoded\r\n", 'method'  => 'POST', 'content' => http_build_query($params), 'timeout' => 10]];
    $context  = stream_context_create($options);
    $result = @file_get_contents($url, false, $context);
    return $result;
}

function fetchProfile($username) {
    global $api_base_url;
    $result = makeApiPostRequest($api_base_url . 'get_profile.php', ['username' => $username, 'action' => 'get_profile']);
    if ($result === false) return null;
    $json = json_decode($result, true);
    return ($json && $json['status'] === 'success') ? $json['user_data'] : null;
}

function fetchUserOrders($userId) {
    global $api_base_url;
    if (!$userId) return [];
    $result = makeApiPostRequest($api_base_url . 'get_user_orders.php', ['user_id' => $userId]);
    if ($result === false) return [];
    $json = json_decode($result, true);
    return ($json && isset($json['orders'])) ? $json['orders'] : [];
}

function fetchCategorizedInquiries($userId) {
    global $api_base_url;
    if (!$userId) return [];
    $result = makeApiPostRequest($api_base_url . 'get_inquiries.php', ['user_id' => $userId]);
    if ($result === false) return [];
    $json = json_decode($result, true);
    return $json ?: [];
}

function profileImageUrl($img) {
    global $image_base_url;
    if (empty($img) || $img === "null" || $img === "images/ProfilePictures/") {
        return "https://placehold.co/120x120/eeeeee/888888?text=User";
    }
    if (strpos($img, "http") === 0) return $img;
    return $image_base_url . ltrim($img, '/');
}

// --- DATA FETCHING & PROCESSING ---
$profile = fetchProfile($username);
if (!$profile) $profile = $userData;

// Fetch and categorize marketplace orders
$allOrders = fetchUserOrders($userId);
$activeOrders = []; $completedOrders = []; $cancelledOrders = [];
$completedStatuses = ['Delivered', 'Completed'];
$cancelledStatuses = ['Cancelled', 'Refunded'];

foreach ($allOrders as $order) {
    $status = $order['order_status'] ?? 'Unknown';
    if (in_array($status, $completedStatuses)) {
        $completedOrders[] = $order;
    } else if (in_array($status, $cancelledStatuses)) {
        $cancelledOrders[] = $order;
    } else {
        $activeOrders[] = $order;
    }
}

// Fetch and process categorized inquiries
$categorizedInquiries = fetchCategorizedInquiries($userId);
$pendingInquiries = $categorizedInquiries['Pending'] ?? [];
$repliedInquiries = $categorizedInquiries['Replied'] ?? [];
$confirmedInquiries = $categorizedInquiries['Confirmed'] ?? [];
$closedInquiries = $categorizedInquiries['Closed'] ?? [];

?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title><?php echo htmlspecialchars($profile['username'] ?? 'Profile'); ?> – Profile | TRIDOTS</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
    <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
    <style>
        :root {
            --primary-green: #00715A; --background-color: #f7f8fa; --card-background: #fff;
            --text-dark: #2c3e50; --text-light: #7f8c8d; --shadow-color: rgba(0,0,0,0.07);
            --border-color: #ecf0f1; --accent-blue: #3498db; --accent-red: #e74c3c;
        }
        body { background: var(--background-color); color: var(--text-dark); font-family: 'Inter', sans-serif; margin: 0; }
        .container { max-width: 900px; margin: 36px auto; padding: 0 20px; }
        .profile-header-card { background: var(--card-background); padding: 32px 24px 16px 24px; border-radius: 18px; display: flex; align-items: flex-start; box-shadow: 0 4px 18px var(--shadow-color); gap: 28px; }
        .profile-image { width: 120px; height: 120px; border-radius: 50%; overflow: hidden; box-shadow: 0 2px 8px var(--shadow-color); flex-shrink: 0; }
        .profile-image img { width: 100%; height: 100%; object-fit: cover; }
        .profile-main-info { flex-grow: 1; }
        .profile-main-info h2 { font-size: 2rem; font-weight: 700; margin: 0 0 6px 0; }
        .profile-main-info .usertype { font-weight: 600; color: var(--primary-green); font-size: 1.1rem; }
        .profile-actions { display: flex; gap: 16px; margin-top: 12px; }
        .profile-actions a { padding: 8px 18px; border-radius: 50px; border: none; background: var(--primary-green); color: #fff; text-decoration: none; font-weight: 600; transition: background .2s; display: flex; align-items: center; gap: 7px; }
        .profile-actions a.edit { background: var(--accent-blue); }
        .profile-actions a.logout { background: var(--accent-red); }
        .profile-actions a:hover { filter: brightness(1.1); }
        .info-card { margin-top: 32px; background: var(--card-background); border-radius: 16px; box-shadow: 0 2px 8px var(--shadow-color); padding: 24px 20px; }
        .info-card h3 { margin-top: 0; }
        .profile-info-table { width: 100%; border-collapse: collapse; }
        .profile-info-table th, .profile-info-table td { text-align: left; padding: 8px 0; font-weight: 400; vertical-align: top; }
        .profile-info-table th { color: var(--text-light); width: 160px; font-weight: 600; }
        .profile-status-list { display: flex; gap: 32px; flex-wrap: wrap; margin-top: 10px;}
        .status-block { min-width: 150px; padding: 10px 0;}
        .status-block .status-label { color: var(--text-light);}
        .status-block .status-value { font-weight: 600;}
        
        /* Tab Styles */
        .tabs { display: flex; border-bottom: 2px solid var(--border-color); margin-bottom: 20px; flex-wrap: wrap; }
        .tab-button { padding: 10px 16px; font-weight: 600; cursor: pointer; border: none; background: none; color: var(--text-light); border-bottom: 3px solid transparent; position: relative; top: 2px; font-size: 0.95em; }
        .tab-button.active { color: var(--primary-green); border-bottom-color: var(--primary-green); }
        .tab-content { display: none; }
        .tab-content.active { display: block; }

        /* Order Item Styles */
        .order-item { display: grid; grid-template-columns: 1fr auto auto; align-items: center; gap: 20px; padding: 16px 8px; border-bottom: 1px solid var(--border-color); }
        .order-item:last-child { border: none; }
        .order-details .order-id { font-weight: 600; font-size: 1.1em; }
        .order-details .order-date { color: var(--text-light); font-size: 0.9em; margin-top: 4px; }
        .order-status { font-weight: 600; padding: 4px 10px; border-radius: 20px; font-size: 0.9em; text-align: center; }
        .order-actions { display: flex; gap: 10px; }
        .order-actions .btn { padding: 6px 14px; text-decoration: none; border-radius: 6px; font-weight: 600; border: 1px solid var(--border-color); color: var(--text-dark); background: #fff; cursor: pointer; }
        .order-actions .btn-cancel { color: var(--accent-red); border-color: var(--accent-red); }
        .order-actions .btn-cancel:hover { background: var(--accent-red); color: #fff; }
        .order-actions .btn-receipt { color: var(--accent-blue); border-color: var(--accent-blue); }
        .order-actions .btn-receipt:hover { background: var(--accent-blue); color: #fff; }

        /* Inquiry List Styles */
        .inquiry-item { background: #fdfdfd; border: 1px solid var(--border-color); padding: 16px; border-radius: 8px; margin-bottom: 12px; }
        .inquiry-header { display: flex; justify-content: space-between; align-items: center; gap: 15px; flex-wrap: wrap; }
        .inquiry-ad-name { font-weight: 600; font-size: 1.1em; }
        .inquiry-date { color: var(--text-light); font-size: 0.9em; }
        .inquiry-message { margin: 12px 0; color: var(--text-dark); }
        .inquiry-reply { background: #eaf5ff; border-left: 4px solid var(--accent-blue); padding: 12px; margin-top: 12px; border-radius: 0 4px 4px 0; }
        .inquiry-reply strong { color: var(--primary-green); }
        
        .mobile-promo { text-align: center; padding: 20px; background: #f8f9fa; border-radius: 8px; }
        .mobile-promo-btn { display: inline-block; margin-top: 12px; padding: 10px 25px; background: var(--primary-green); color: white; text-decoration: none; font-weight: 600; border-radius: 50px; }

        @media (max-width:600px) {
            .profile-header-card { flex-direction: column; align-items: center; }
            .profile-main-info { text-align: center; }
            .order-item { grid-template-columns: 1fr; text-align: center; gap: 12px; }
            .order-actions { justify-content: center; }
        }
    </style>
</head>
<body>
    <div class="container">
        <!-- Profile Header -->
        <div class="profile-header-card">
            <div class="profile-image">
                <img src="<?php echo htmlspecialchars(profileImageUrl($profile['profile_picture'] ?? '')); ?>"
                     alt="Profile Picture"
                     onerror="this.onerror=null;this.src='https://placehold.co/120x120/eeeeee/888888?text=User';">
            </div>
            <div class="profile-main-info">
                <h2><?php echo htmlspecialchars($profile['full_name'] ?? $profile['username'] ?? 'User'); ?></h2>
                <div class="usertype"><?php echo htmlspecialchars($profile['user_type'] ?? 'N/A'); ?></div>
                <div class="profile-actions">
                    <a href="edit_profile.php" class="edit"><ion-icon name="create-outline"></ion-icon> Edit Profile</a>
                    <a href="logout.php" class="logout"><ion-icon name="log-out-outline"></ion-icon> Logout</a>
                </div>
            </div>
        </div>

        <!-- Personal Information Card -->
        <div class="info-card">
            <h3>Personal Information</h3>
            <table class="profile-info-table">
                <tr><th>Username</th><td><?php echo htmlspecialchars($profile['username'] ?? '-'); ?></td></tr>
                <tr><th>Email</th><td><?php echo htmlspecialchars($profile['email_address'] ?? '-'); ?></td></tr>
                <tr><th>Contact</th><td><?php echo htmlspecialchars($profile['contact_number'] ?? '-'); ?></td></tr>
                <tr><th>NIC</th><td><?php echo htmlspecialchars($profile['nic_number'] ?? '-'); ?></td></tr>
                <tr><th>Address</th><td><?php echo htmlspecialchars($profile['address'] ?? '-'); ?></td></tr>
            </table>
        </div>

        <!-- Account Status Card -->
        <div class="info-card">
            <h3>Account Status</h3>
            <div class="profile-status-list">
                <div class="status-block">
                    <div class="status-label">Verification</div>
                    <div class="status-value"><?php echo htmlspecialchars($profile['verification_status'] ?? '-'); ?></div>
                </div>
                <div class="status-block">
                    <div class="status-label">Active</div>
                    <div class="status-value"><?php echo (isset($profile['is_active']) && $profile['is_active']) ? "Yes" : "No"; ?></div>
                </div>
                <div class="status-block">
                    <div class="status-label">Profile Updated</div>
                    <div class="status-value"><?php echo htmlspecialchars($profile['last_updated'] ?? 'N/A'); ?></div>
                </div>
            </div>
        </div>
        
        <!-- Marketplace Orders Card -->
        <div id="orders-container" class="info-card">
             <h3>My Marketplace Orders</h3>
            <?php if (!empty($allOrders)): ?>
                <div class="tabs">
                    <button class="tab-button active" onclick="showTab('orders-container', 'active')">Active (<?php echo count($activeOrders); ?>)</button>
                    <button class="tab-button" onclick="showTab('orders-container', 'completed')">Completed (<?php echo count($completedOrders); ?>)</button>
                    <button class="tab-button" onclick="showTab('orders-container', 'cancelled')">Cancelled (<?php echo count($cancelledOrders); ?>)</button>
                </div>
                
                <?php
                function render_orders($orders, $is_active = false) {
                    global $completedStatuses, $cancelledStatuses;
                    if (empty($orders)) { echo "<p style='padding: 10px 0;'>No orders in this category.</p>"; return; }
                    foreach ($orders as $order) {
                        $status = htmlspecialchars($order['order_status'] ?? 'Unknown');
                        $status_color = '#3498db'; $status_bg = '#eaf5ff'; // Default to active
                        if (in_array($status, $completedStatuses)) { $status_color = '#2ecc71'; $status_bg = '#e5f9ed'; } 
                        else if (in_array($status, $cancelledStatuses)) { $status_color = '#e74c3c'; $status_bg = '#fbeeeC'; }
                        
                        echo '<div class="order-item">';
                        echo '  <div class="order-details">';
                        echo '      <div class="order-id">Order #' . htmlspecialchars($order['order_id']) . '</div>';
                        echo '      <div class="order-date">' . htmlspecialchars($order['order_date']) . '</div>';
                        echo '  </div>';
                        echo '  <div class="order-status" style="background-color: ' . $status_bg . '; color: ' . $status_color . ';">' . $status . '</div>';
                        echo '  <div class="order-actions">';
                        echo '      <a href="receipt.php?order_id=' . $order['order_id'] . '" class="btn btn-receipt">View Receipt</a>';
                        if ($is_active) { echo '      <button class="btn btn-cancel" data-order-id="' . $order['order_id'] . '">Cancel</button>'; }
                        echo '  </div>';
                        echo '</div>';
                    }
                }
                ?>

                <div id="tab-active" class="tab-content active"><?php render_orders($activeOrders, true); ?></div>
                <div id="tab-completed" class="tab-content"><?php render_orders($completedOrders); ?></div>
                <div id="tab-cancelled" class="tab-content"><?php render_orders($cancelledOrders); ?></div>

            <?php else: ?>
                <div style="color:var(--text-light);margin-top:8px;">No marketplace orders found.</div>
            <?php endif; ?>
        </div>

        <!-- Inquiries Card -->
        <div id="inquiries-container" class="info-card">
            <h3>My Inquiries</h3>
            <div class="tabs">
                <button class="tab-button active" onclick="showTab('inquiries-container', 'pending')">Pending (<?php echo count($pendingInquiries); ?>)</button>
                <button class="tab-button" onclick="showTab('inquiries-container', 'replied')">Replied (<?php echo count($repliedInquiries); ?>)</button>
                <button class="tab-button" onclick="showTab('inquiries-container', 'confirmed')">Confirmed (<?php echo count($confirmedInquiries); ?>)</button>
                <button class="tab-button" onclick="showTab('inquiries-container', 'closed')">Closed (<?php echo count($closedInquiries); ?>)</button>
            </div>
            
            <?php
            function render_inquiries($inquiries) {
                if (empty($inquiries)) { echo "<p style='padding: 10px 0;'>No inquiries in this category.</p>"; return; }
                foreach ($inquiries as $inq) {
                    echo '<div class="inquiry-item">';
                    echo '<div class="inquiry-header">';
                    echo '<span class="inquiry-ad-name">' . htmlspecialchars($inq['ad_name'] ?? 'N/A') . '</span>';
                    echo '<span class="inquiry-date">' . htmlspecialchars($inq['inquiry_date'] ?? '') . '</span>';
                    echo '</div>';
                    echo '<p class="inquiry-message">' . nl2br(htmlspecialchars($inq['inquiry_message'] ?? '')) . '</p>';
                    if (!empty($inq['seller_reply'])) {
                        echo '<div class="inquiry-reply"><strong>Seller Reply:</strong> ' . nl2br(htmlspecialchars($inq['seller_reply'])) . '</div>';
                    }
                    echo '</div>';
                }
            }
            ?>
            <div id="tab-pending" class="tab-content active"><?php render_inquiries($pendingInquiries); ?></div>
            <div id="tab-replied" class="tab-content"><?php render_inquiries($repliedInquiries); ?></div>
            <div id="tab-confirmed" class="tab-content"><?php render_inquiries($confirmedInquiries); ?></div>
            <div id="tab-closed" class="tab-content"><?php render_inquiries($closedInquiries); ?></div>
        </div>

        <!-- Mobile App Promo Card -->
        <div class="info-card">
            <h3>Service Inquiries & Rentals</h3>
            <div class="mobile-promo">
                <p>For the best experience, all inquiries, rentals, and communications are managed through our mobile application.</p>
                <a href="mobile.html" class="mobile-promo-btn">Get the Mobile App</a>
            </div>
        </div>
    </div>

    <script>
        function showTab(containerId, tabName) {
            const container = document.getElementById(containerId);
            if (!container) return;
            container.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
            container.querySelectorAll('.tab-button').forEach(b => b.classList.remove('active'));
            container.querySelector('#tab-' + tabName).classList.add('active');
            container.querySelector(`.tab-button[onclick*="'${tabName}'"]`).classList.add('active');
        }

        document.addEventListener('DOMContentLoaded', () => {
            document.querySelectorAll('.btn-cancel').forEach(button => {
                button.addEventListener('click', function() {
                    const orderId = this.dataset.orderId;
                    if (confirm(`Are you sure you want to cancel order #${orderId}?`)) {
                        cancelOrder(orderId);
                    }
                });
            });
        });

        function cancelOrder(orderId) {
            const apiUrl = '<?php echo $api_base_url; ?>cancel_order.php';
            const formData = new FormData();
            formData.append('order_id', orderId);

            fetch(apiUrl, { method: 'POST', body: formData })
            .then(response => response.json())
            .then(data => {
                alert(data.message || (data.success ? 'Order cancelled.' : 'Failed to cancel order.'));
                if (data.success) { window.location.reload(); }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred while trying to cancel the order.');
            });
        }
    </script>
</body>
</html>
