<?php
require_once 'db.php';

$sql = "SELECT r.*, c.main_category, c.category_icon, sc.sub_category
        FROM renting r
        JOIN main_category c ON r.main_category_id = c.main_category_id
        LEFT JOIN sub_category sc ON r.sub_category_id = sc.sub_category_id
        WHERE r.approval_status = 'approved'
        ORDER BY r.created_at DESC
        LIMIT 30";
$stmt = $pdo->query($sql);
$rentals = $stmt->fetchAll();

foreach ($rentals as &$r) {
    $r['images'] = $r['product_images'] ? explode(',', $r['product_images']) : [];
}
unset($r);

header('Content-Type: application/json');
echo json_encode($rentals);
?>