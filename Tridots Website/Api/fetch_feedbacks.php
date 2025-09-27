<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

$conn = new mysqli($host, $username, $password, $dbname);
if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Database connection failed: " . $conn->connect_error]);
    exit;
}

$feedback_type = isset($_GET['feedback_type']) ? $_GET['feedback_type'] : '';
$item_id = isset($_GET['item_id']) ? intval($_GET['item_id']) : 0;

if (empty($feedback_type) || $item_id === 0) {
    http_response_code(400);
    echo json_encode(["status" => "error", "message" => "Missing parameters"]);
    exit;
}

// Use Users.username as user_name, or Users.business_name as you wish
$stmt = $conn->prepare("SELECT feedbacks.*, Users.username AS user_name 
                        FROM feedbacks 
                        LEFT JOIN Users ON feedbacks.user_id = Users.user_id 
                        WHERE feedback_type = ? AND item_id = ? 
                        ORDER BY created_at DESC");
if (!$stmt) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Prepare failed: " . $conn->error]);
    exit;
}
$stmt->bind_param("si", $feedback_type, $item_id);
if (!$stmt->execute()) {
    http_response_code(500);
    echo json_encode(["status" => "error", "message" => "Execute failed: " . $stmt->error]);
    exit;
}
$result = $stmt->get_result();

$feedbacks = [];
while ($row = $result->fetch_assoc()) {
    $feedbacks[] = [
        "feedback_id" => $row['feedback_id'],
        "user_id" => $row['user_id'],
        "user_name" => isset($row['user_name']) ? $row['user_name'] : "",
        "rating" => $row['rating'],
        "comment" => $row['comment'],
        "created_at" => $row['created_at'],
    ];
}

header('Content-Type: application/json');
echo json_encode($feedbacks);

$stmt->close();
$conn->close();
?>