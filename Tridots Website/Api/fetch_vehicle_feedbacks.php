<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $conn = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    echo json_encode(['status' => 'error', 'message' => 'Database connection failed: ' . $e->getMessage()]);
    exit();
}

if (isset($_GET['vehicle_id'])) {
    $vehicle_id = filter_var($_GET['vehicle_id'], FILTER_SANITIZE_NUMBER_INT);

    $stmt = $conn->prepare("SELECT user_id, rating, feedback_text, created_at FROM VehicleFeedbacks WHERE vehicle_id = :vehicle_id ORDER BY created_at DESC");
    $stmt->bindParam(':vehicle_id', $vehicle_id, PDO::PARAM_INT);
    $stmt->execute();
    $feedbacks = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode(['status' => 'success', 'feedbacks' => $feedbacks]);
} else {
    echo json_encode(['status' => 'error', 'message' => 'Missing vehicle_id parameter']);
}

$conn = null;
?>