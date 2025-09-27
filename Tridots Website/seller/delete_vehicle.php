<?php
require 'db.php';
session_start();
$user_id = $_SESSION['user_id'] ?? 0;
if (!$user_id) exit('Not logged in');
$vehicle_id = $_POST['vehicle_id'] ?? 0;

$stmt = $pdo->prepare("DELETE FROM Vehicles WHERE vehicle_id=? AND user_id=?");
$stmt->execute([$vehicle_id, $user_id]);
echo "deleted";
?>