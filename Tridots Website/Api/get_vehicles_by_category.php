<?php
header('Content-Type: application/json');

$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    if (isset($_GET['vehicle_category_id'])) {
        $vehicleCategoryId = $_GET['vehicle_category_id'];
        $stmt = $pdo->prepare("SELECT * FROM Vehicles WHERE vehicle_category_id = :vehicle_category_id AND approval_status = 'Approved' AND availability_status = 'Available'");
        $stmt->bindParam(':vehicle_category_id', $vehicleCategoryId, PDO::PARAM_INT);
        $stmt->execute();
        $vehicles = $stmt->fetchAll(PDO::FETCH_ASSOC);

        echo json_encode($vehicles);
    } else {
        http_response_code(400);
        echo json_encode(['error' => 'Missing vehicle_category_id parameter']);
    }

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>