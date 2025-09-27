<?php
// Save or update vehicle ad
require 'db.php';
session_start();
$user_id = $_SESSION['user_id'] ?? 0;
if (!$user_id) exit('Not logged in');

$vehicle_id = $_POST['vehicle_id'] ?? null; // for update
$vehicle_category_id = $_POST['vehicle_category_id'] ?? '';
$vehicle_name = $_POST['vehicle_name'] ?? '';
$brand = $_POST['brand'] ?? '';
$model = $_POST['model'] ?? '';
$description = $_POST['description'] ?? '';
$capacity = $_POST['capacity'] ?? '';
$fuel_type = $_POST['fuel_type'] ?? '';
$transmission_type = $_POST['transmission_type'] ?? '';
$location = $_POST['location'] ?? '';
$price_type = $_POST['price_type'] ?? '';
$amount = $_POST['amount'] ?? null;
$keywords = $_POST['keywords'] ?? '';
$availability_status = $_POST['availability_status'] ?? 'Available';
$vehicle_images = null;

// Handle image upload (multiple images as comma-separated list)
if (!empty($_FILES['vehicle_images']['tmp_name'][0])) {
    $images = [];
    foreach ($_FILES['vehicle_images']['tmp_name'] as $i => $tmp_name) {
        if ($_FILES['vehicle_images']['error'][$i] === 0) {
            $dest = 'uploads/' . uniqid() . '_' . basename($_FILES['vehicle_images']['name'][$i]);
            move_uploaded_file($tmp_name, $dest);
            $images[] = $dest;
        }
    }
    if ($images) $vehicle_images = implode(',', $images);
}

if ($vehicle_id) {
    // Update
    $sql = "UPDATE Vehicles SET vehicle_category_id=?, vehicle_name=?, brand=?, model=?, description=?, capacity=?, fuel_type=?, transmission_type=?, location=?, price_type=?, amount=?, keywords=?, availability_status=?";
    $params = [$vehicle_category_id, $vehicle_name, $brand, $model, $description, $capacity, $fuel_type, $transmission_type, $location, $price_type, $amount, $keywords, $availability_status];
    if ($vehicle_images) {
        $sql .= ", vehicle_images=?";
        $params[] = $vehicle_images;
    }
    $sql .= " WHERE vehicle_id=? AND user_id=?";
    $params[] = $vehicle_id; $params[] = $user_id;
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    echo "updated";
} else {
    // Insert
    $stmt = $pdo->prepare("INSERT INTO Vehicles (user_id, vehicle_category_id, vehicle_name, brand, model, description, capacity, fuel_type, transmission_type, location, price_type, amount, keywords, availability_status, vehicle_images) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    $stmt->execute([$user_id, $vehicle_category_id, $vehicle_name, $brand, $model, $description, $capacity, $fuel_type, $transmission_type, $location, $price_type, $amount, $keywords, $availability_status, $vehicle_images]);
    echo "created";
}
?>