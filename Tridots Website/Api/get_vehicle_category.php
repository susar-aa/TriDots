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
    $stmt = $pdo->prepare("SELECT vehicle_category_id, vehicle_category_name FROM VehicleCategory");
    $stmt->execute();
    $categories = $stmt->fetchAll();

    echo json_encode(['status' => 'success', 'categories' => $categories]);

} catch (\PDOException $e) {
    echo json_encode(['status' => 'error', 'message' => 'Failed to fetch vehicle categories: ' . $e->getMessage()]);
}
?>
