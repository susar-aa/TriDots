<?php
header('Content-Type: application/json');

// --- Database Connection ---
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';
$charset = 'utf8mb4';

$dsn = "mysql:host=$host;dbname=$dbname;charset=$charset";
$options = [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES   => false,
];

try {
    $pdo = new PDO($dsn, $username, $password, $options);
} catch (\PDOException $e) {
    echo json_encode(['status' => 'error', 'message' => 'Database Connection Failed: ' . $e->getMessage()]);
    exit();
}

// --- Input Validation ---
$required_fields = ['user_id', 'main_category_id', 'sub_category_id', 'product_name', 'brand', 'product_description', 'keywords', 'price_per_day', 'product_location'];
foreach ($required_fields as $field) {
    if (empty($_POST[$field])) {
        echo json_encode(['status' => 'error', 'message' => "Required field '{$field}' is missing."]);
        exit();
    }
}

// Key 'product_images' must match the key in the Android Volley request.
if (!isset($_FILES['product_images']) || $_FILES['product_images']['error'] != UPLOAD_ERR_OK) {
    echo json_encode(['status' => 'error', 'message' => 'Product image is required and must be uploaded without errors.']);
    exit();
}


// --- File Upload Logic ---
$upload_dir = '../images/Ads/';
if (!is_dir($upload_dir)) {
    if (!mkdir($upload_dir, 0777, true)) {
        echo json_encode(['status' => 'error', 'message' => 'Failed to create image upload directory.']);
        exit();
    }
}

$image_file = $_FILES['product_images'];
$image_name = 'renting_' . uniqid() . '_' . basename($image_file['name']);
$target_path = $upload_dir . $image_name;
$image_url = 'https://lionsgoldencircle.com/Tridots/images/Ads/' . $image_name;

if (!move_uploaded_file($image_file['tmp_name'], $target_path)) {
    echo json_encode(['status' => 'error', 'message' => 'Failed to upload image.']);
    exit();
}


// --- Data Insertion ---
$sql = "INSERT INTO renting (user_id, main_category_id, sub_category_id, product_name, brand, model, product_images, product_description, keywords, price_per_hour, price_per_day, product_location, created_at) 
        VALUES (:user_id, :main_category_id, :sub_category_id, :product_name, :brand, :model, :product_images, :product_description, :keywords, :price_per_hour, :price_per_day, :product_location, NOW())";

try {
    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        ':user_id' => $_POST['user_id'],
        ':main_category_id' => $_POST['main_category_id'],
        ':sub_category_id' => $_POST['sub_category_id'],
        ':product_name' => $_POST['product_name'],
        ':brand' => $_POST['brand'],
        ':model' => !empty($_POST['model']) ? $_POST['model'] : null,
        ':product_images' => $image_url,
        ':product_description' => $_POST['product_description'],
        ':keywords' => $_POST['keywords'],
        ':price_per_hour' => !empty($_POST['price_per_hour']) ? $_POST['price_per_hour'] : null,
        ':price_per_day' => $_POST['price_per_day'],
        ':product_location' => $_POST['product_location'],
    ]);

    echo json_encode(['status' => 'success', 'message' => 'Renting ad created successfully.']);

} catch (\PDOException $e) {
    if (file_exists($target_path)) {
        unlink($target_path);
    }
    echo json_encode(['status' => 'error', 'message' => 'Failed to create ad: ' . $e->getMessage()]);
}
?>
