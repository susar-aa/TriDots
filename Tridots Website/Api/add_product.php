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

// Check if all required fields are provided via POST
// Minimal required fields for adding a product
$required_fields = [
    'seller_id', 'category_id', 'product_name', 'product_description',
    'price', 'stock_quantity', 'product_condition', 'store_address_line1',
    'store_city', 'store_district', 'store_postal_code', 'store_country'
];

foreach ($required_fields as $field) {
    if (!isset($_POST[$field]) || empty(trim($_POST[$field]))) {
        echo json_encode(['success' => false, 'message' => 'Missing required field: ' . $field]);
        exit();
    }
}

// Sanitize and get POST data
$seller_id = filter_var($_POST['seller_id'], FILTER_SANITIZE_NUMBER_INT);
$category_id = filter_var($_POST['category_id'], FILTER_SANITIZE_NUMBER_INT);
$product_name = filter_var($_POST['product_name'], FILTER_SANITIZE_STRING);
$product_description = filter_var($_POST['product_description'], FILTER_SANITIZE_STRING);
$price = filter_var($_POST['price'], FILTER_SANITIZE_NUMBER_FLOAT, FILTER_FLAG_ALLOW_FRACTION);
$stock_quantity = filter_var($_POST['stock_quantity'], FILTER_SANITIZE_NUMBER_INT);
$product_condition = filter_var($_POST['product_condition'], FILTER_SANITIZE_STRING); // 'New' or 'Used'

// Optional fields with default values or null if not provided
$sku = isset($_POST['sku']) ? filter_var($_POST['sku'], FILTER_SANITIZE_STRING) : null;
$keywords = isset($_POST['keywords']) ? filter_var($_POST['keywords'], FILTER_SANITIZE_STRING) : null;
$brand = isset($_POST['brand']) ? filter_var($_POST['brand'], FILTER_SANITIZE_STRING) : null;
$model = isset($_POST['model']) ? filter_var($_POST['model'], FILTER_SANITIZE_STRING) : null;
$compatibility = isset($_POST['compatibility']) ? filter_var($_POST['compatibility'], FILTER_SANITIZE_STRING) : null;
$warranty_policy = isset($_POST['warranty_policy']) ? filter_var($_POST['warranty_policy'], FILTER_SANITIZE_STRING) : null;
$weight_g = isset($_POST['weight_g']) ? filter_var($_POST['weight_g'], FILTER_SANITIZE_NUMBER_INT) : null;
$dimensions_cm = isset($_POST['dimensions_cm']) ? filter_var($_POST['dimensions_cm'], FILTER_SANITIZE_STRING) : null;
$material = isset($_POST['material']) ? filter_var($_POST['material'], FILTER_SANITIZE_STRING) : null;
$color = isset($_POST['color']) ? filter_var($_POST['color'], FILTER_SANITIZE_STRING) : null;
$manufacturing_date = isset($_POST['manufacturing_date']) && !empty($_POST['manufacturing_date']) ? $_POST['manufacturing_date'] : null;
$expiration_date = isset($_POST['expiration_date']) && !empty($_POST['expiration_date']) ? $_POST['expiration_date'] : null;
$store_address_line1 = filter_var($_POST['store_address_line1'], FILTER_SANITIZE_STRING);
$store_address_line2 = isset($_POST['store_address_line2']) ? filter_var($_POST['store_address_line2'], FILTER_SANITIZE_STRING) : null;
$store_city = filter_var($_POST['store_city'], FILTER_SANITIZE_STRING);
$store_district = filter_var($_POST['store_district'], FILTER_SANITIZE_STRING);
$store_postal_code = filter_var($_POST['store_postal_code'], FILTER_SANITIZE_STRING);
$store_country = filter_var($_POST['store_country'], FILTER_SANITIZE_STRING);
$delivery_fee = isset($_POST['delivery_fee']) ? filter_var($_POST['delivery_fee'], FILTER_SANITIZE_NUMBER_FLOAT, FILTER_FLAG_ALLOW_FRACTION) : null;

// Default statuses for new products
$product_status = 'active';
$approved_status = 'Pending';
$availability_status = 'Available';

try {
    // Prepare SQL INSERT statement
    $stmt = $pdo->prepare("INSERT INTO Marketplace (
        seller_id, category_id, product_name, product_description, price, stock_quantity, sku,
        product_status, product_condition, posted_date, last_updated_date, approved_status,
        availability_status, keywords, brand, model, compatibility, warranty_policy,
        weight_g, dimensions_cm, material, color, manufacturing_date, expiration_date,
        store_address_line1, store_address_line2, store_city, store_district, store_postal_code, store_country, delivery_fee
    ) VALUES (
        :seller_id, :category_id, :product_name, :product_description, :price, :stock_quantity, :sku,
        :product_status, :product_condition, NOW(), NOW(), :approved_status,
        :availability_status, :keywords, :brand, :model, :compatibility, :warranty_policy,
        :weight_g, :dimensions_cm, :material, :color, :manufacturing_date, :expiration_date,
        :store_address_line1, :store_address_line2, :store_city, :store_district, :store_postal_code, :store_country, :delivery_fee
    )");

    // Bind parameters
    $stmt->bindParam(':seller_id', $seller_id, PDO::PARAM_INT);
    $stmt->bindParam(':category_id', $category_id, PDO::PARAM_INT);
    $stmt->bindParam(':product_name', $product_name);
    $stmt->bindParam(':product_description', $product_description);
    $stmt->bindParam(':price', $price);
    $stmt->bindParam(':stock_quantity', $stock_quantity, PDO::PARAM_INT);
    $stmt->bindParam(':sku', $sku);
    $stmt->bindParam(':product_status', $product_status);
    $stmt->bindParam(':product_condition', $product_condition);
    $stmt->bindParam(':approved_status', $approved_status);
    $stmt->bindParam(':availability_status', $availability_status);
    $stmt->bindParam(':keywords', $keywords);
    $stmt->bindParam(':brand', $brand);
    $stmt->bindParam(':model', $model);
    $stmt->bindParam(':compatibility', $compatibility);
    $stmt->bindParam(':warranty_policy', $warranty_policy);
    $stmt->bindParam(':weight_g', $weight_g, PDO::PARAM_INT);
    $stmt->bindParam(':dimensions_cm', $dimensions_cm);
    $stmt->bindParam(':material', $material);
    $stmt->bindParam(':color', $color);
    $stmt->bindParam(':manufacturing_date', $manufacturing_date);
    $stmt->bindParam(':expiration_date', $expiration_date);
    $stmt->bindParam(':store_address_line1', $store_address_line1);
    $stmt->bindParam(':store_address_line2', $store_address_line2);
    $stmt->bindParam(':store_city', $store_city);
    $stmt->bindParam(':store_district', $store_district);
    $stmt->bindParam(':store_postal_code', $store_postal_code);
    $stmt->bindParam(':store_country', $store_country);
    $stmt->bindParam(':delivery_fee', $delivery_fee);


    // Execute the statement
    if ($stmt->execute()) {
        $last_id = $pdo->lastInsertId(); // Get the ID of the newly inserted product
        echo json_encode(['success' => true, 'message' => 'Product added successfully!', 'product_id' => $last_id]);
    } else {
        echo json_encode(['success' => false, 'message' => 'Failed to add product.']);
    }

} catch (PDOException $e) {
    echo json_encode(['success' => false, 'message' => 'Database error: ' . $e->getMessage()]);
}
?>
