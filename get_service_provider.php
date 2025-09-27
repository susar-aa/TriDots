<?php
require_once 'db.php';

$seller_id = isset($_GET['seller_id']) ? (int)$_GET['seller_id'] : 0;
if ($seller_id > 0) {
    $stmt = $pdo->prepare("SELECT * FROM Service_Providers WHERE seller_id = ?");
    $stmt->execute([$seller_id]);
    $provider = $stmt->fetch();
} else {
    $provider = null;
}

header('Content-Type: application/json');
echo json_encode($provider);
?>