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

if (isset($_POST['vehicle_id']) && isset($_POST['vehicle_name']) && isset($_POST['brand']) && isset($_POST['description']) && isset($_POST['capacity']) && isset($_POST['fuel_type']) && isset($_POST['transmission_type']) && isset($_POST['availability_status']) && isset($_POST['location']) && isset($_POST['price_type']) && isset($_POST['amount']) && isset($_POST['keywords']) && isset($_POST['vehicle_images'])) {
    $vehicleId = $_POST['vehicle_id'];
    $vehicleName = $_POST['vehicle_name'];
    $brand = $_POST['brand'];
    $model = isset($_POST['model']) ? $_POST['model'] : null;
    $description = $_POST['description'];
    $capacity = $_POST['capacity'];
    $fuelType = $_POST['fuel_type'];
    $transmissionType = $_POST['transmission_type'];
    $availabilityStatus = $_POST['availability_status'];
    $location = $_POST['location'];
    $priceType = $_POST['price_type'];
    $amount = $_POST['amount'];
    $keywords = $_POST['keywords'];
    $vehicleImages = $_POST['vehicle_images']; // Get the image URL

    $stmt = $pdo->prepare("UPDATE Vehicles SET vehicle_name = :vehicle_name, brand = :brand, model = :model, description = :description, capacity = :capacity, fuel_type = :fuel_type, transmission_type = :transmission_type, availability_status = :availability_status, location = :location, price_type = :price_type, amount = :amount, keywords = :keywords, vehicle_images = :vehicle_images, updated_at = CURRENT_TIMESTAMP WHERE vehicle_id = :vehicle_id");
    $stmt->bindParam(':vehicle_id', $vehicleId, PDO::PARAM_INT);
    $stmt->bindParam(':vehicle_name', $vehicleName);
    $stmt->bindParam(':brand', $brand);
    $stmt->bindParam(':model', $model);
    $stmt->bindParam(':description', $description);
    $stmt->bindParam(':capacity', $capacity);
    $stmt->bindParam(':fuel_type', $fuelType);
    $stmt->bindParam(':transmission_type', $transmissionType);
    $stmt->bindParam(':availability_status', $availabilityStatus);
    $stmt->bindParam(':location', $location);
    $stmt->bindParam(':price_type', $priceType);
    $stmt->bindParam(':amount', $amount);
    $stmt->bindParam(':keywords', $keywords);
    $stmt->bindParam(':vehicle_images', $vehicleImages);

    if ($stmt->execute()) {
        echo json_encode(array("status" => "success", "message" => "Vehicle ad updated successfully."));
    } else {
        echo json_encode(array("status" => "error", "message" => "Failed to update vehicle ad."));
    }
} else {
    echo json_encode(array("status" => "error", "message" => "Missing required parameters."));
}

$pdo = null;
?>