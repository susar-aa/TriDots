<?php
header('Content-Type: application/json');

$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    if (isset($_GET['main_category_id'])) {
        $mainCategoryId = $_GET['main_category_id'];
        $stmt = $pdo->prepare("SELECT sub_category_id, sub_category, category_description, category_icon, main_category_id FROM sub_category WHERE main_category_id = :main_category_id");
        $stmt->bindParam(':main_category_id', $mainCategoryId, PDO::PARAM_INT);
        $stmt->execute();
        $subCategories = $stmt->fetchAll(PDO::FETCH_ASSOC);

        echo json_encode($subCategories);
    } else {
        http_response_code(400);
        echo json_encode(['error' => 'Missing main_category_id parameter']);
    }

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>