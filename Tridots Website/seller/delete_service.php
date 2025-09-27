<?php
require 'db.php';
session_start();
$user_id = $_SESSION['user_id'] ?? 0;
if (!$user_id) exit('Not logged in');
$seller_id = $_POST['seller_id'] ?? 0;

$stmt = $pdo->prepare("DELETE FROM Service_Providers WHERE seller_id=? AND User_id=?");
$stmt->execute([$seller_id, $user_id]);
echo "deleted";