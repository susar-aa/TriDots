<?php
session_start();
header('Content-Type: application/json');
if (empty($_SESSION['user_id']) || strtolower($_SESSION['user_type']) !== 'business') {
    echo json_encode(['error'=>'Not logged in']); exit();
}
require_once __DIR__ . '/db.php';

$user_id = $_SESSION['user_id'];
$stmt = $pdo->prepare("SELECT * FROM inquiries WHERE user_id = ? ORDER BY inquiry_date DESC");
$stmt->execute([$user_id]);
echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC));
?>