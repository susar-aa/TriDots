<?php
// Set the content type to JSON for API response
header('Content-Type: application/json');

// Database connection parameters
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

// Establish a database connection using PDO
try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    echo json_encode(['success' => false, 'error' => 'Database connection failed: ' . $e->getMessage()]);
    exit();
}

try {
    // Prepare a SQL statement to fetch all categories from Marketplace_Categories
    $stmt = $pdo->prepare("SELECT category_id, category_name FROM Marketplace_Categories ORDER BY category_name ASC");
    $stmt->execute();
    $categories = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Return a success JSON response with the fetched categories
    echo json_encode(['success' => true, 'categories' => $categories]);

} catch (PDOException $e) {
    // If a query fails, return a JSON error response
    echo json_encode(['success' => false, 'error' => 'Failed to fetch categories: ' . $e->getMessage()]);
}
?>
