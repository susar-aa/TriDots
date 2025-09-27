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
    echo json_encode(['success' => false, 'message' => 'Database connection failed: ' . $e->getMessage()]);
    exit();
}

// Check if product_id and variants_json are provided
if (!isset($_POST['product_id']) || !isset($_POST['variants_json'])) {
    echo json_encode(['success' => false, 'message' => 'Missing product ID or variants data.']);
    exit();
}

$productId = filter_var($_POST['product_id'], FILTER_SANITIZE_NUMBER_INT);
$variantsJson = $_POST['variants_json'];

// Decode the JSON string into a PHP array
$variants = json_decode($variantsJson, true);

if (json_last_error() !== JSON_ERROR_NONE) {
    echo json_encode(['success' => false, 'message' => 'Invalid JSON format for variants: ' . json_last_error_msg()]);
    exit();
}

if (empty($variants)) {
    echo json_encode(['success' => true, 'message' => 'No variants provided to add.']);
    exit();
}

try {
    $pdo->beginTransaction(); // Start transaction for multiple inserts

    $stmt = $pdo->prepare("INSERT INTO Product_Variants (
        product_id, variant_name, variant_price, variant_stock_quantity, variant_image_url
    ) VALUES (
        :product_id, :variant_name, :variant_price, :variant_stock_quantity, :variant_image_url
    )");

    foreach ($variants as $variant) {
        $variantName = filter_var($variant['variant_name'], FILTER_SANITIZE_STRING);
        $variantPrice = filter_var($variant['variant_price'], FILTER_SANITIZE_NUMBER_FLOAT, FILTER_FLAG_ALLOW_FRACTION);
        $variantStockQuantity = filter_var($variant['variant_stock_quantity'], FILTER_SANITIZE_NUMBER_INT);
        $variantImageUrl = isset($variant['variant_image_url']) ? filter_var($variant['variant_image_url'], FILTER_SANITIZE_URL) : null;

        $stmt->bindParam(':product_id', $productId, PDO::PARAM_INT);
        $stmt->bindParam(':variant_name', $variantName);
        $stmt->bindParam(':variant_price', $variantPrice);
        $stmt->bindParam(':variant_stock_quantity', $variantStockQuantity, PDO::PARAM_INT);
        $stmt->bindParam(':variant_image_url', $variantImageUrl);

        $stmt->execute();
    }

    $pdo->commit(); // Commit transaction
    echo json_encode(['success' => true, 'message' => 'Product variants added successfully!']);

} catch (PDOException $e) {
    $pdo->rollBack(); // Rollback on error
    echo json_encode(['success' => false, 'message' => 'Database error during variant addition: ' . $e->getMessage()]);
} catch (Exception $e) {
    echo json_encode(['success' => false, 'message' => 'An unexpected error occurred: ' . $e->getMessage()]);
}
?>
