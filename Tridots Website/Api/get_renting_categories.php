<?php
header('Content-Type: application/json');

// --- Database Connection ---
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';
$charset = 'utf8mb4';

$dsn = "mysql:host=$host;dbname=$dbname;charset=$charset";
$options = [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES   => false,
];

try {
    $pdo = new PDO($dsn, $username, $password, $options);
} catch (\PDOException $e) {
    echo json_encode(['status' => 'error', 'message' => 'Database Connection Failed: ' . $e->getMessage()]);
    exit();
}

try {
    // Fetch main categories for renting
    $stmtMain = $pdo->prepare("SELECT main_category_id, main_category FROM main_category WHERE type = 'renting'");
    $stmtMain->execute();
    $mainCategories = $stmtMain->fetchAll();

    // Fetch all sub-categories
    $stmtSub = $pdo->prepare("SELECT sub_category_id, sub_category, main_category_id FROM sub_category");
    $stmtSub->execute();
    $subCategories = $stmtSub->fetchAll();

    echo json_encode([
        'status' => 'success',
        'main_categories' => $mainCategories,
        'sub_categories' => $subCategories
    ]);

} catch (\PDOException $e) {
    echo json_encode(['status' => 'error', 'message' => 'Failed to fetch categories: ' . $e->getMessage()]);
}
?>
