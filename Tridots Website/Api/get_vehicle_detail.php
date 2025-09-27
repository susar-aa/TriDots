<?php
header('Content-Type: application/json');

$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    echo json_encode(array("status" => "error", "message" => "Database connection failed: " . $e->getMessage()));
    exit();
}

if (isset($_POST['id'])) {
    $vehicleId = $_POST['id'];
    $stmt = $pdo->prepare("SELECT * FROM Vehicles WHERE vehicle_id = :vehicle_id");
    $stmt->bindParam(':vehicle_id', $vehicleId, PDO::PARAM_INT);
    $stmt->execute();
    $vehicleAd = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($vehicleAd) {
        echo json_encode(array("status" => "success", "data" => $vehicleAd));
    } else {
        echo json_encode(array("status" => "error", "message" => "Vehicle ad not found."));
    }
} else {
    echo json_encode(array("status" => "error", "message" => "Vehicle ID not provided."));
}

$pdo = null;
?>