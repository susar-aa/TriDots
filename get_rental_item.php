<?php
require_once 'db.php';

$rent_id = isset($_GET['rent_id']) ? (int)$_GET['rent_id'] : 0;
if ($rent_id > 0) {
    $stmt = $pdo->prepare("SELECT * FROM renting WHERE rent_id = ?");
    $stmt->execute([$rent_id]);
    $item = $stmt->fetch();
    if ($item) {
        $item['images'] = $item['product_images'] ? explode(',', $item['product_images']) : [];
    }
} else {
    $item = null;
}

header('Content-Type: application/json');
echo json_encode($item);
?>