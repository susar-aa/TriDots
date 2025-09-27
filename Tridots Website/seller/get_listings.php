<?php
require 'db.php';
session_start();
$user_id = $_SESSION['user_id'] ?? 0;
if (!$user_id) exit(json_encode(['error'=>'Not logged in']));

$out = ['services'=>[], 'vehicles'=>[], 'rentings'=>[]];

// Services
$stmt = $pdo->prepare("SELECT * FROM Service_Providers WHERE User_id=?");
$stmt->execute([$user_id]);
$out['services'] = $stmt->fetchAll(PDO::FETCH_ASSOC);

// Vehicles
$stmt = $pdo->prepare("SELECT * FROM Vehicles WHERE user_id=?");
$stmt->execute([$user_id]);
$out['vehicles'] = $stmt->fetchAll(PDO::FETCH_ASSOC);

// Renting
$stmt = $pdo->prepare("SELECT * FROM renting WHERE user_id=?");
$stmt->execute([$user_id]);
$out['rentings'] = $stmt->fetchAll(PDO::FETCH_ASSOC);

header('Content-Type: application/json');
echo json_encode($out);
