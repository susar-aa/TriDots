<?php
require_once 'db.php';

$stmt = $pdo->query("SELECT vehicle_category_id, vehicle_category_name, description, category_icon FROM VehicleCategory ORDER BY vehicle_category_name ASC");
$vehicle_categories = $stmt->fetchAll();

header('Content-Type: application/json');
echo json_encode($vehicle_categories);
?>