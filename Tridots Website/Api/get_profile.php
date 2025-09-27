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
    die(json_encode(array("status" => "error", "message" => "Database connection failed: " . $e->getMessage())));
}

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['action']) && $_POST['action'] === 'get_profile' && isset($_POST['username'])) {
    $username = $_POST['username'];
    $baseImageUrl = "https://lionsgoldencircle.com/Tridots/";

    $stmt = $pdo->prepare("SELECT user_id, username, email_address, contact_number, nic_number, user_type, profile_picture, address, verification_status, is_active FROM Users WHERE username = :username");
    $stmt->bindParam(':username', $username);
    $stmt->execute();

    if ($stmt->rowCount() > 0) {
        $userData = $stmt->fetch(PDO::FETCH_ASSOC);
        // Ensure the profile_picture path stored in the database is RELATIVE
        echo json_encode(array("status" => "success", "user_data" => $userData));
    } else {
        echo json_encode(array("status" => "error", "message" => "User not found."));
    }

} else {
    echo json_encode(array("status" => "error", "message" => "Invalid request."));
}

$pdo = null;
?>