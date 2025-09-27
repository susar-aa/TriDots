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

if (isset($_POST['id'])) {
    $sellerId = $_POST['id'];
    $stmt = $pdo->prepare("SELECT * FROM Service_Providers WHERE seller_id = :seller_id");
    $stmt->bindParam(':seller_id', $sellerId, PDO::PARAM_INT);
    $stmt->execute();
    $serviceAd = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($serviceAd) {
        echo json_encode(array("status" => "success", "data" => $serviceAd));
    } else {
        echo json_encode(array("status" => "error", "message" => "Service provider not found."));
    }
} else {
    echo json_encode(array("status" => "error", "message" => "Seller ID not provided."));
}

$pdo = null;
?>