<?php
// Enable error reporting for debugging (REMOVE IN PRODUCTION)
error_reporting(E_ALL);
ini_set('display_errors', 1);

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

// Check if seller_id is provided
if (!isset($_POST['seller_id'])) {
    echo json_encode(['success' => false, 'message' => 'Seller ID not provided.']);
    exit();
}

$sellerId = filter_var($_POST['seller_id'], FILTER_SANITIZE_NUMBER_INT);
$requestedStatus = isset($_POST['approved_status']) ? filter_var($_POST['approved_status'], FILTER_SANITIZE_STRING) : 'All'; // Sanitize status

$products = [];

try {
    $sql = "SELECT * FROM Marketplace WHERE seller_id = :seller_id";
    $params = [':seller_id' => $sellerId];

    if ($requestedStatus !== 'All') {
        $sql .= " AND approved_status = :approved_status";
        $params[':approved_status'] = $requestedStatus;
    }

    $sql .= " ORDER BY posted_date DESC"; // Order by most recently posted

    $stmt = $pdo->prepare($sql);
    $stmt->execute($params);
    $productsResult = $stmt->fetchAll(PDO::FETCH_ASSOC);

    foreach ($productsResult as $productData) {
        $product_id = $productData['product_id'];
        $productData['images'] = [];
        $productData['variants'] = [];
        $productData['thumbnail_image_url'] = null; // Initialize thumbnail URL

        // Fetch Product Images (from ProductImages table)
        $stmtImages = $pdo->prepare("SELECT image_id, product_id, image_url, is_thumbnail, display_order FROM ProductImages WHERE product_id = :product_id ORDER BY display_order ASC");
        $stmtImages->bindParam(':product_id', $product_id, PDO::PARAM_INT);
        $stmtImages->execute();
        $imagesResult = $stmtImages->fetchAll(PDO::FETCH_ASSOC);
        $productData['images'] = $imagesResult;

        // Find the thumbnail image URL and set it in the main product data
        foreach ($imagesResult as $image) {
            if ($image['is_thumbnail'] == 1) { // Assuming 'is_thumbnail' is stored as 1 for true
                $productData['thumbnail_image_url'] = $image['image_url'];
                break; // Found thumbnail, no need to check others
            }
        }
        // Fallback: If no explicit thumbnail, take the first image
        if ($productData['thumbnail_image_url'] === null && !empty($imagesResult)) {
            $productData['thumbnail_image_url'] = $imagesResult[0]['image_url'];
        }

        // Fetch Product Variants (from Product_Variants table)
        $stmtVariants = $pdo->prepare("SELECT variant_id, product_id, variant_name, variant_price, variant_stock_quantity, variant_image_url FROM Product_Variants WHERE product_id = :product_id");
        $stmtVariants->bindParam(':product_id', $product_id, PDO::PARAM_INT);
        $stmtVariants->execute();
        $variantsResult = $stmtVariants->fetchAll(PDO::FETCH_ASSOC);
        $productData['variants'] = $variantsResult;
        
        $products[] = $productData;
    }


    echo json_encode(['success' => true, 'products' => $products]);

} catch (PDOException $e) {
    echo json_encode(['success' => false, 'message' => 'Query failed: ' . $e->getMessage()]);
} catch (Exception $e) {
    echo json_encode(['success' => false, 'message' => 'An unexpected error occurred: ' . $e->getMessage()]);
}
?>
