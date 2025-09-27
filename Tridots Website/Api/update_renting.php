<?php
header('Content-Type: application/json');

$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    if (isset($_POST['rent_id']) && is_numeric($_POST['rent_id'])) {
        $rent_id = $_POST['rent_id'];
        $product_name = $_POST['product_name'] ?? '';
        $brand = $_POST['brand'] ?? '';
        $model = $_POST['model'] ?? null;
        $product_description = $_POST['product_description'] ?? null;
        $keywords = $_POST['keywords'] ?? null;
        $price_per_hour = $_POST['price_per_hour'] ?? null;
        $price_per_day = $_POST['price_per_day'] ?? null;
        $product_location = $_POST['product_location'] ?? null;
        $availability_status = $_POST['availability_status'] ?? 'Available';
        $product_images = $_POST['product_images'] ?? null;

        $stmt = $pdo->prepare("UPDATE renting SET
            product_name = :product_name,
            brand = :brand,
            model = :model,
            product_description = :product_description,
            keywords = :keywords,
            price_per_hour = :price_per_hour,
            price_per_day = :price_per_day,
            product_location = :product_location,
            availability_status = :availability_status,
            product_images = :product_images,
            updated_at = NOW()
            WHERE rent_id = :rent_id");

        $stmt->bindParam(':rent_id', $rent_id, PDO::PARAM_INT);
        $stmt->bindParam(':product_name', $product_name, PDO::PARAM_STR);
        $stmt->bindParam(':brand', $brand, PDO::PARAM_STR);
        $stmt->bindParam(':model', $model, PDO::PARAM_STR);
        $stmt->bindParam(':product_description', $product_description, PDO::PARAM_STR);
        $stmt->bindParam(':keywords', $keywords, PDO::PARAM_STR);
        $stmt->bindParam(':price_per_hour', $price_per_hour, PDO::PARAM_STR);
        $stmt->bindParam(':price_per_day', $price_per_day, PDO::PARAM_STR);
        $stmt->bindParam(':product_location', $product_location, PDO::PARAM_STR);
        $stmt->bindParam(':availability_status', $availability_status, PDO::PARAM_STR);
        $stmt->bindParam(':product_images', $product_images, PDO::PARAM_STR);
        $stmt->execute();

        if ($stmt->rowCount() > 0) {
            echo json_encode(['status' => 'success', 'message' => 'Renting ad updated successfully.']);
        } else {
            echo json_encode(['status' => 'info', 'message' => 'No changes were made to the renting ad.']);
        }

    } else {
        echo json_encode(['status' => 'error', 'message' => 'Invalid or missing renting ad ID.']);
    }

} catch (PDOException $e) {
    echo json_encode(['status' => 'error', 'message' => 'Database error: ' . $e->getMessage()]);
}
?>