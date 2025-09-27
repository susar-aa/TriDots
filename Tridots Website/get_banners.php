<?php
require_once 'db.php';

$stmt = $pdo->query("SELECT id, image_path FROM banner_images ORDER BY id DESC");
$banners = $stmt->fetchAll();

header('Content-Type: application/json');
echo json_encode($banners);
?>