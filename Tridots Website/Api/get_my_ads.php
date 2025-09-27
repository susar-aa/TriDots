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

if (isset($_POST['user_id'])) {
    $userId = $_POST['user_id'];

    $response = array("status" => "success", "renting_ads" => array(), "vehicle_ads" => array(), "service_ads" => array());

    // Fetch renting ads with approval_status
    $stmtRenting = $pdo->prepare("SELECT rent_id, product_name, product_images, product_description, approval_status FROM renting WHERE user_id = :user_id");
    $stmtRenting->bindParam(':user_id', $userId, PDO::PARAM_INT);
    $stmtRenting->execute();
    $response["renting_ads"] = $stmtRenting->fetchAll(PDO::FETCH_ASSOC);

    // Fetch vehicle ads with approval_status
    $stmtVehicles = $pdo->prepare("SELECT vehicle_id, vehicle_name, vehicle_images, description, approval_status FROM Vehicles WHERE user_id = :user_id");
    $stmtVehicles->bindParam(':user_id', $userId, PDO::PARAM_INT);
    $stmtVehicles->execute();
    $response["vehicle_ads"] = $stmtVehicles->fetchAll(PDO::FETCH_ASSOC);

    // Fetch service provider ads with approval_status
    $stmtServices = $pdo->prepare("SELECT seller_id, name, profile_picture, description, approval_status FROM Service_Providers WHERE User_id = :user_id");
    $stmtServices->bindParam(':user_id', $userId, PDO::PARAM_INT);
    $stmtServices->execute();
    $response["service_ads"] = $stmtServices->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($response);

} else {
    echo json_encode(array("status" => "error", "message" => "User ID not provided."));
}

$pdo = null; // Close connection
?>
