<?php
header('Content-Type: application/json');

$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    if (isset($_POST['seller_id']) && is_numeric($_POST['seller_id'])) {
        $seller_id = $_POST['seller_id'];

        $stmt = $pdo->prepare("SELECT * FROM Service_Providers WHERE seller_id = :seller_id");
        $stmt->bindParam(':seller_id', $seller_id, PDO::PARAM_INT);
        $stmt->execute();

        if ($stmt->rowCount() > 0) {
            $service_provider = $stmt->fetch(PDO::FETCH_ASSOC);
            echo json_encode(['status' => 'success', 'data' => $service_provider]);
        } else {
            echo json_encode(['status' => 'error', 'message' => 'Service provider profile not found.']);
        }
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Invalid or missing seller ID.']);
    }

} catch (PDOException $e) {
    echo json_encode(['status' => 'error', 'message' => 'Database error: ' . $e->getMessage()]);
}
?>