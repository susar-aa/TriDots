<?php
require_once 'db.php';

$business_profile_id = isset($_GET['business_profile_id']) ? (int)$_GET['business_profile_id'] : 0;
if ($business_profile_id > 0) {
    $stmt = $pdo->prepare("SELECT f.*, u.username FROM Feedbacks f JOIN Users u ON f.user_id = u.user_id WHERE f.business_profile_id = ? ORDER BY created_at DESC");
    $stmt->execute([$business_profile_id]);
    $feedbacks = $stmt->fetchAll();
} else {
    $feedbacks = [];
}

header('Content-Type: application/json');
echo json_encode($feedbacks);
?>