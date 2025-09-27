<?php
require_once 'db.php';

$main_category_id = isset($_GET['main_category_id']) ? (int)$_GET['main_category_id'] : 0;
if ($main_category_id > 0) {
    $stmt = $pdo->prepare("SELECT sub_category_id, sub_category, category_icon, main_category_id FROM sub_category WHERE main_category_id = ? ORDER BY sub_category ASC");
    $stmt->execute([$main_category_id]);
} else {
    $stmt = $pdo->query("SELECT sub_category_id, sub_category, category_icon, main_category_id FROM sub_category ORDER BY sub_category ASC");
}
$subcategories = $stmt->fetchAll();

header('Content-Type: application/json');
echo json_encode($subcategories);
?>