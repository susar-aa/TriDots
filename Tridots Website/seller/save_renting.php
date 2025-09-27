<?php
// Save or update renting ad
require 'db.php';
session_start();
$user_id = $_SESSION['user_id'] ?? 0;
if (!$user_id) exit('Not logged in');

$rent_id = $_POST['rent_id'] ?? null; // for update
$main_category_id = $_POST['main_category_id'] ?? '';
$sub_category_id = $_POST['sub_category_id'] ?? '';
$product_name = $_POST['product_name'] ?? '';
$brand = $_POST['brand'] ?? '';
$model = $_POST['model'] ?? '';
$product_description = $_POST['product_description'] ?? '';
$keywords = $_POST['keywords'] ?? '';
$price_per_hour = $_POST['price_per_hour'] ?? null;
$price_per_day = $_POST['price_per_day'] ?? null;
$product_location = $_POST['product_location'] ?? '';
$availability_status = $_POST['availability_status'] ?? 'Available';
$product_images = null;

// Handle image upload (multiple images as comma-separated list)
if (!empty($_FILES['product_images']['tmp_name'][0])) {
    $images = [];
    foreach ($_FILES['product_images']['tmp_name'] as $i => $tmp_name) {
        if ($_FILES['product_images']['error'][$i] === 0) {
            $dest = 'uploads/' . uniqid() . '_' . basename($_FILES['product_images']['name'][$i]);
            move_uploaded_file($tmp_name, $dest);
            $images[] = $dest;
        }
    }
    if ($images) $product_images = implode(',', $images);
}

if ($rent_id) {
    // Update
    $sql = "UPDATE renting SET main_category_id=?, sub_category_id=?, product_name=?, brand=?, model=?, product_description=?, keywords=?, price_per_hour=?, price_per_day=?, product_location=?, availability_status=?";
    $params = [$main_category_id, $sub_category_id, $product_name, $brand, $model, $product_description, $keywords, $price_per_hour, $price_per_day, $product_location, $availability_status];
    if ($product_images) {
        $sql .= ", product_images=?";
        $params[] = $product_images;
    }
    $sql .= " WHERE rent_id=? AND user_id=?";
    $params[] = $rent_id; $params[] = $user_id;
    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    echo "updated";
} else {
    // Insert
    $stmt = $pdo->prepare("INSERT INTO renting (user_id, main_category_id, sub_category_id, product_name, brand, model, product_description, keywords, price_per_hour, price_per_day, product_location, availability_status, product_images) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
    $stmt->execute([$user_id, $main_category_id, $sub_category_id, $product_name, $brand, $model, $product_description, $keywords, $price_per_hour, $price_per_day, $product_location, $availability_status, $product_images]);
    echo "created";
}
?>