<?php
require_once 'db.php';

$sql = "SELECT sp.*, sc.service_category_name
        FROM Service_Providers sp
        LEFT JOIN ServicesCategory sc ON sp.service_category_id = sc.service_category_id
        WHERE sp.approval_status = 'approved'
        ORDER BY sp.created_at DESC
        LIMIT 30";
$stmt = $pdo->query($sql);
$services = $stmt->fetchAll();

header('Content-Type: application/json');
echo json_encode($services);
?>