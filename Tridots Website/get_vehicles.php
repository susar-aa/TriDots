<?php
require_once 'db.php';

$sql = "SELECT v.*, vc.vehicle_category_name, u.username
        FROM Vehicles v
        JOIN VehicleCategory vc ON v.vehicle_category_id = vc.vehicle_category_id
        JOIN Users u ON v.user_id = u.user_id
        WHERE v.approval_status = 'approved'
        ORDER BY v.created_at DESC
        LIMIT 30";
$stmt = $pdo->query($sql);
$vehicles = $stmt->fetchAll();

foreach ($vehicles as &$v) {
    $v['vehicle_images'] = $v['vehicle_images'] ? explode(',', $v['vehicle_images']) : [];
}
unset($v);

header('Content-Type: application/json');
echo json_encode($vehicles);
?>