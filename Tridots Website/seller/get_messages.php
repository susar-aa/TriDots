<?php
session_start();
header('Content-Type: application/json');
if (empty($_SESSION['user_id']) || strtolower($_SESSION['user_type']) !== 'business') {
    echo json_encode(['error'=>'Not logged in']); exit();
}
require_once __DIR__ . '/db.php';

$user_id = $_SESSION['user_id'];
// Example: fetch the latest 20 messages for this user
$stmt = $pdo->prepare("SELECT * FROM messages WHERE recipient_id = ? OR sender_id = ? ORDER BY sent_at DESC LIMIT 20");
$stmt->execute([$user_id, $user_id]);
echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC));
?>