<?php
// get_listings.php - Returns JSON listings for marketplace_dashboard.php
session_start();
header('Content-Type: application/json');
require_once 'db.php'; // Make sure this file connects $pdo as PDO instance

// --- Helper function for safe values ---
function getParam($key, $default = '') {
    return isset($_GET[$key]) ? trim($_GET[$key]) : $default;
}

$tab = getParam('tab', 'all');
$cat = getParam('cat', '');
$subcat = getParam('subcat', '');
$min = is_numeric(getParam('min')) ? (float)getParam('min') : 0;
$max = is_numeric(getParam('max')) ? (float)getParam('max') : 1000000;
$rating = is_numeric(getParam('rating')) ? (int)getParam('rating') : 0;
$q = getParam('q', '');

$listings = [];
$params = [];
$where = [];
$order = "ORDER BY m.posted_date DESC";
$limit = "LIMIT 40";

// --- Query Construction ---
switch ($tab) {
    case 'vehicles':
        $sql = "SELECT v.vehicle_id AS id, v.vehicle_name, v.brand, v.model, v.description, v.amount, v.availability_status, v.user_id as seller_id,
                    (SELECT image_url FROM VehicleImages WHERE vehicle_id = v.vehicle_id ORDER BY image_id ASC LIMIT 1) AS image_url,
                    u.username AS seller_name, u.profile_picture AS seller_avatar
                FROM Vehicles v
                JOIN Users u ON v.user_id = u.user_id
                WHERE v.approval_status = 'Approved' AND v.availability_status = 'Available'";
        if ($cat) { $sql .= " AND v.vehicle_category_id = ?"; $params[] = $cat; }
        break;
    case 'forsale':
        $sql = "SELECT m.product_id AS id, m.product_name, m.brand, m.model, m.product_description, m.seller_id,
                    (SELECT image_url FROM ProductImages WHERE product_id = m.product_id AND is_thumbnail = 1 LIMIT 1) AS image_url,
                    u.username AS seller_name, u.profile_picture AS seller_avatar
                FROM Marketplace m
                JOIN Users u ON m.seller_id = u.user_id
                WHERE m.product_status = 'For Sale' AND m.approved_status = 'Approved' AND m.availability_status = 'Available'";
        if ($cat) { $sql .= " AND m.category_id = ?"; $params[] = $cat; }
        break;
    case 'rentals':
        $sql = "SELECT r.rent_id AS id, r.product_name, r.brand, r.model, r.product_description, r.user_id as seller_id,
                    (SELECT product_images FROM renting WHERE rent_id = r.rent_id LIMIT 1) AS image_url,
                    u.username AS seller_name, u.profile_picture AS seller_avatar
                FROM renting r
                JOIN Users u ON r.user_id = u.user_id
                WHERE r.approval_status = 'Approved' AND r.availability_status = 'Available'";
        if ($cat) { $sql .= " AND r.main_category_id = ?"; $params[] = $cat; }
        break;
    case 'services':
        $sql = "SELECT s.seller_id AS id, s.name AS product_name, s.description AS product_description, s.User_id as seller_id,
                    s.profile_picture AS image_url,
                    u.username AS seller_name, u.profile_picture AS seller_avatar
                FROM Service_Providers s
                JOIN Users u ON s.User_id = u.user_id
                WHERE s.approval_status = 'Approved' AND s.availability_status = 'Available'";
        if ($cat) { $sql .= " AND s.service_category_id = ?"; $params[] = $cat; }
        break;
    default: // All
        $sql = "(SELECT m.product_id AS id, m.product_name, m.brand, m.model, m.product_description, m.seller_id,
                    (SELECT image_url FROM ProductImages WHERE product_id = m.product_id AND is_thumbnail = 1 LIMIT 1) AS image_url,
                    u.username AS seller_name, u.profile_picture AS seller_avatar
                FROM Marketplace m
                JOIN Users u ON m.seller_id = u.user_id
                WHERE m.approved_status = 'Approved' AND m.availability_status = 'Available')
                UNION ALL
                (SELECT v.vehicle_id AS id, v.vehicle_name, v.brand, v.model, v.description AS product_description, v.user_id as seller_id,
                    (SELECT image_url FROM VehicleImages WHERE vehicle_id = v.vehicle_id ORDER BY image_id ASC LIMIT 1) AS image_url,
                    u.username AS seller_name, u.profile_picture AS seller_avatar
                FROM Vehicles v
                JOIN Users u ON v.user_id = u.user_id
                WHERE v.approval_status = 'Approved' AND v.availability_status = 'Available')
                UNION ALL
                (SELECT r.rent_id AS id, r.product_name, r.brand, r.model, r.product_description, r.user_id as seller_id,
                    (SELECT product_images FROM renting WHERE rent_id = r.rent_id LIMIT 1) AS image_url,
                    u.username AS seller_name, u.profile_picture AS seller_avatar
                FROM renting r
                JOIN Users u ON r.user_id = u.user_id
                WHERE r.approval_status = 'Approved' AND r.availability_status = 'Available')
                $order $limit";
        break;
}

// --- Search and Ordering ---
if ($q && strpos($sql, 'WHERE') !== false) {
    $sql .= " AND (m.product_name LIKE ? OR m.product_description LIKE ? OR v.vehicle_name LIKE ? OR r.product_name LIKE ?)";
    $params = array_merge($params, array_fill(0, 4, "%$q%"));
}
$sql .= " $order $limit";

// --- Execute query ---
try {
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    $listings = $stmt->fetchAll(PDO::FETCH_ASSOC);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Failed to load listings']);
    exit;
}
echo json_encode($listings);