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
$required_fields = ['user_id', 'vehicle_category_id', 'vehicle_name', 'brand', 'model', 'description', 'capacity', 'fuel_type', 'transmission_type', 'availability_status', 'location', 'price_type', 'amount', 'keywords'];
foreach ($required_fields as $field) {
    if (empty($_POST[$field])) {
        echo json_encode(['status' => 'error', 'message' => "Required field '{$field}' is missing."]);
        exit();
    }
}

// Key 'vehicle_images' must match the key in the Android Volley request.
if (!isset($_FILES['vehicle_images']) || $_FILES['vehicle_images']['error'] != UPLOAD_ERR_OK) {
    echo json_encode(['status' => 'error', 'message' => 'Vehicle image is required and must be uploaded without errors.']);
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

$image_file = $_FILES['vehicle_images'];
$image_name = 'vehicle_' . uniqid() . '_' . basename($image_file['name']);
$target_path = $upload_dir . $image_name;
$image_url = 'https://lionsgoldencircle.com/Tridots/images/Ads/' . $image_name;

if (!move_uploaded_file($image_file['tmp_name'], $target_path)) {
    echo json_encode(['status' => 'error', 'message' => 'Failed to upload vehicle image.']);
    exit();
}

// --- Data Insertion ---
$sql = "INSERT INTO Vehicles (user_id, vehicle_category_id, vehicle_name, brand, model, description, capacity, fuel_type, transmission_type, availability_status, location, price_type, amount, vehicle_images, keywords, created_at) 
        VALUES (:user_id, :vehicle_category_id, :vehicle_name, :brand, :model, :description, :capacity, :fuel_type, :transmission_type, :availability_status, :location, :price_type, :amount, :vehicle_images, :keywords, NOW())";

try {
    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        ':user_id' => $_POST['user_id'],
        ':vehicle_category_id' => $_POST['vehicle_category_id'],
        ':vehicle_name' => $_POST['vehicle_name'],
        ':brand' => $_POST['brand'],
        ':model' => $_POST['model'],
        ':description' => $_POST['description'],
        ':capacity' => $_POST['capacity'],
        ':fuel_type' => $_POST['fuel_type'],
        ':transmission_type' => $_POST['transmission_type'],
        ':availability_status' => $_POST['availability_status'],
        ':location' => $_POST['location'],
        ':price_type' => $_POST['price_type'],
        ':amount' => $_POST['amount'],
        ':vehicle_images' => $image_url,
        ':keywords' => $_POST['keywords'],
    ]);

    echo json_encode(['status' => 'success', 'message' => 'Vehicle ad created successfully.']);

} catch (\PDOException $e) {
    if (file_exists($target_path)) {
        unlink($target_path);
    }
    echo json_encode(['status' => 'error', 'message' => 'Failed to create vehicle ad: ' . $e->getMessage()]);
}
?>
