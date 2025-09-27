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
    $rentId = $_POST['id'];
    $stmt = $pdo->prepare("SELECT * FROM renting WHERE rent_id = :rent_id");
    $stmt->bindParam(':rent_id', $rentId, PDO::PARAM_INT);
    $stmt->execute();
    $rentingAd = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($rentingAd) {
        echo json_encode(array("status" => "success", "data" => $rentingAd));
    } else {
        echo json_encode(array("status" => "error", "message" => "Renting ad not found."));
    }
} else {
    echo json_encode(array("status" => "error", "message" => "Renting ID not provided."));
}

$pdo = null;
?>