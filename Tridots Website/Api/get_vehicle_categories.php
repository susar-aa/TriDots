<?php
header('Content-Type: application/json');

$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $stmt = $pdo->query("SELECT vehicle_category_id, vehicle_category_name AS main_category, description AS category_description, category_icon FROM VehicleCategory");
    $vehicleCategories = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($vehicleCategories);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>