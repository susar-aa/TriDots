<?php
require 'db.php';
session_start();
$user_id = $_SESSION['user_id'] ?? 0;
if (!$user_id) exit('Not logged in');
$rent_id = $_POST['rent_id'] ?? 0;

$stmt = $pdo->prepare("DELETE FROM renting WHERE rent_id=? AND user_id=?");
$stmt->execute([$rent_id, $user_id]);
echo "deleted";
?>