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
$required_fields = ['user_id', 'name', 'service_category_id', 'description', 'contact_number', 'email_address', 'address', 'location'];
foreach ($required_fields as $field) {
    if (empty($_POST[$field])) {
        echo json_encode(['status' => 'error', 'message' => "Required field '{$field}' is missing."]);
        exit();
    }
}

// The key 'profile_picture' here must match the key used in the Android Volley request
if (!isset($_FILES['profile_picture']) || $_FILES['profile_picture']['error'] != UPLOAD_ERR_OK) {
    echo json_encode(['status' => 'error', 'message' => 'Profile picture is required and must be uploaded without errors.']);
    exit();
}


// --- File Upload Logic ---
// The path from this API script to the images directory
$upload_dir = '../images/Ads/';
if (!is_dir($upload_dir)) {
    // Attempt to create the directory recursively
    if (!mkdir($upload_dir, 0777, true)) {
        echo json_encode(['status' => 'error', 'message' => 'Failed to create image upload directory. Check permissions.']);
        exit();
    }
}

$image_file = $_FILES['profile_picture'];
// Add a prefix to avoid name collisions and keep it unique
$image_name = 'service_' . uniqid() . '_' . basename($image_file['name']);
$target_path = $upload_dir . $image_name;
// This is the public URL that will be stored in the database
$image_url = 'https://lionsgoldencircle.com/Tridots/images/Ads/' . $image_name;

if (!move_uploaded_file($image_file['tmp_name'], $target_path)) {
    echo json_encode(['status' => 'error', 'message' => 'Failed to upload profile picture. Check server logs for more details.']);
    exit();
}

// --- Data Insertion ---
$sql = "INSERT INTO Service_Providers (User_id, name, service_category_id, description, contact_number, email_address, address, experience_years, qualifications, location, availability_status, profile_picture, created_at) 
        VALUES (:user_id, :name, :service_category_id, :description, :contact_number, :email_address, :address, :experience_years, :qualifications, :location, :availability_status, :profile_picture, NOW())";
        
try {
    $stmt = $pdo->prepare($sql);
    $stmt->execute([
        ':user_id' => $_POST['user_id'],
        ':name' => $_POST['name'],
        ':service_category_id' => $_POST['service_category_id'],
        ':description' => $_POST['description'],
        ':contact_number' => $_POST['contact_number'],
        ':email_address' => $_POST['email_address'],
        ':address' => $_POST['address'],
        ':experience_years' => !empty($_POST['experience_years']) ? $_POST['experience_years'] : null,
        ':qualifications' => !empty($_POST['qualifications']) ? $_POST['qualifications'] : null,
        ':location' => $_POST['location'],
        ':availability_status' => !empty($_POST['availability_status']) ? $_POST['availability_status'] : 'Available',
        ':profile_picture' => $image_url,
    ]);

    echo json_encode(['status' => 'success', 'message' => 'Service provider ad created successfully.']);

} catch (\PDOException $e) {
    // Clean up uploaded file if DB insert fails
    if (file_exists($target_path)) {
        unlink($target_path);
    }
    echo json_encode(['status' => 'error', 'message' => 'Failed to create service ad: ' . $e->getMessage()]);
}
?>
