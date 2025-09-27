<?php
require_once 'db.php';

$stmt = $pdo->query("SELECT main_category_id, main_category, category_icon FROM main_category ORDER BY main_category ASC");
$categories = $stmt->fetchAll();

header('Content-Type: application/json');
echo json_encode($categories);
?>