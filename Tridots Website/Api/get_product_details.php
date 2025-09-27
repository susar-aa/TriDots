    <?php
    // get_product_details.php
    // This script fetches product details and its variants from the 'tridots' database.

    // --- Database Connection Details ---
    $host = 'localhost:3306'; // Your MySQL host and port
    $dbname = 'tridots';
    $username = 'tridots';
    $password = 'tridots12369';

    // --- Error Reporting (IMPORTANT FOR DEBUGGING - COMMENT OUT IN PRODUCTION) ---
    error_reporting(E_ALL);
    ini_set('display_errors', 1); // Keep on for debugging, turn off for production

    // Set content type to JSON
    header('Content-Type: application/json');

    // Create connection
    $conn = new mysqli($host, $username, $password, $dbname);

    // Check connection
    if ($conn->connect_error) {
        error_log("Database Connection Error: " . $conn->connect_error);
        echo json_encode(['error' => 'Database connection failed. Please try again later.']);
        exit();
    }

    // Optional: Set character set for proper data handling
    $conn->set_charset("utf8mb4");

    // Get product_id from the GET request
    $product_id = isset($_GET['product_id']) ? intval($_GET['product_id']) : 0;

    // Validate product_id
    if ($product_id === 0) {
        echo json_encode(['error' => 'Product ID is required.']);
        $conn->close();
        exit();
    }

    // --- Fetch product details from the 'Marketplace' table ---
    // Removed filtering by approved_status and availability_status
    $product_sql = "SELECT p.*, GROUP_CONCAT(pi.image_url ORDER BY pi.display_order ASC) AS image_urls_concat
                    FROM Marketplace p
                    LEFT JOIN ProductImages pi ON p.product_id = pi.product_id
                    WHERE p.product_id = ?
                    GROUP BY p.product_id"; // Group by product_id to aggregate image_urls into one string

    $product_stmt = $conn->prepare($product_sql);

    if ($product_stmt === false) {
        error_log("Prepare failed for product_sql: " . $conn->error);
        echo json_encode(['error' => 'Failed to prepare product statement.']);
        $conn->close();
        exit();
    }

    $product_stmt->bind_param("i", $product_id);
    $product_stmt->execute();
    $product_result = $product_stmt->get_result();

    if ($product_result->num_rows > 0) {
        $product_data = $product_result->fetch_assoc();

        // --- Process Product Images ---
        $images_data = [];
        if (!empty($product_data['image_urls_concat'])) {
            $image_urls = explode(',', $product_data['image_urls_concat']);
            foreach ($image_urls as $index => $url) {
                $images_data[] = [
                    'image_id' => $index + 1, // Placeholder ID or fetch actual image_id if possible
                    'product_id' => $product_data['product_id'],
                    'image_url' => $url,
                    'is_thumbnail' => ($index == 0) ? 1 : 0, // Assume first is thumbnail
                    'display_order' => $index + 1
                ];
            }
        }
        unset($product_data['image_urls_concat']);
        $product_data['images'] = $images_data;

        // Add a "thumbnail_image_url" field to the main product data for convenience
        // This takes the first image from the 'images' array if available
        $product_data['thumbnail_image_url'] = !empty($images_data) ? $images_data[0]['image_url'] : null;


        // --- Fetch variants for this product from 'Product_Variants' table ---
        $variants_sql = "SELECT variant_id, product_id, variant_name, variant_price, variant_stock_quantity, variant_image_url
                        FROM Product_Variants
                        WHERE product_id = ?";
        $variants_stmt = $conn->prepare($variants_sql);

        if ($variants_stmt === false) {
            error_log("Prepare failed for variants_sql: " . $conn->error);
            echo json_encode(['error' => 'Failed to prepare variants statement.']);
            $conn->close();
            exit();
        }

        $variants_stmt->bind_param("i", $product_id);
        $variants_stmt->execute();
        $variants_result = $variants_stmt->get_result();

        $variants = [];
        while ($row = $variants_result->fetch_assoc()) {
            $variants[] = $row;
        }
        $variants_stmt->close();

        $product_data['variants'] = $variants;

        echo json_encode($product_data);

    } else {
        echo json_encode(['error' => 'Product not found with this ID.']); // Simplified message
    }

    $product_stmt->close();
    $conn->close();
    ?>
    