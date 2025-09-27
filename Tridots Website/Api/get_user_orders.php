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
    // Create a new PDO instance
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    // Set PDO error mode to exception for better error handling
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    // If connection fails, return a JSON error response and exit
    echo json_encode(['error' => 'Database connection failed: ' . $e->getMessage()]);
    exit();
}

// Check if user_id is provided in the POST request
if (!isset($_POST['user_id'])) {
    echo json_encode(['error' => 'User ID not provided.']);
    exit();
}

// Get the user ID from the POST request
$userId = $_POST['user_id'];
$orders = []; // Initialize an empty array to store fetched orders

try {
    // Prepare a SQL statement to fetch orders for the given user ID
    // Orders are ordered by date in descending order (newest first)
    $stmt = $pdo->prepare("SELECT * FROM Orders WHERE user_id = :user_id ORDER BY order_date DESC");
    // Bind the user_id parameter to prevent SQL injection
    $stmt->bindParam(':user_id', $userId, PDO::PARAM_INT);
    // Execute the prepared statement
    $stmt->execute();
    // Fetch all results as an associative array
    $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Iterate through each fetched order
    foreach ($result as $order) {
        $orderId = $order['order_id'];
        // Initialize an empty array for items within each order
        $order['items'] = [];

        // Prepare a SQL statement to fetch order items for the current order ID
        // Now joining with 'Marketplace' for product_name and 'Product_Variants' for variant_name
        $itemStmt = $pdo->prepare("
            SELECT
                oi.order_item_id,
                oi.order_id,
                oi.product_id,
                oi.variant_id,
                oi.quantity,
                oi.unit_price,
                m.product_name,        -- product_name from Marketplace table
                pv.variant_name        -- variant_name from Product_Variants table
            FROM
                Order_Items oi
            JOIN
                Marketplace m ON oi.product_id = m.product_id -- Corrected table name
            LEFT JOIN
                Product_Variants pv ON oi.variant_id = pv.variant_id -- Corrected table name
            WHERE
                oi.order_id = :order_id
        ");
        // Bind the order_id parameter
        $itemStmt->bindParam(':order_id', $orderId, PDO::PARAM_INT);
        // Execute the statement
        $itemStmt->execute();
        // Fetch all items for the current order
        $itemsResult = $itemStmt->fetchAll(PDO::FETCH_ASSOC);

        // Add the fetched items to the current order object
        $order['items'] = $itemsResult;

        // Fetch delivery address details if address_id exists
        // Now querying from 'UserAddresses' table
        if (isset($order['address_id']) && $order['address_id'] > 0) {
            $addressStmt = $pdo->prepare("SELECT * FROM UserAddresses WHERE address_id = :address_id"); // Corrected table name
            $addressStmt->bindParam(':address_id', $order['address_id'], PDO::PARAM_INT);
            $addressStmt->execute();
            $addressDetails = $addressStmt->fetch(PDO::FETCH_ASSOC);
            if ($addressDetails) {
                $order['delivery_address'] = $addressDetails;
            } else {
                $order['delivery_address'] = null; // Or an empty object if no address found
            }
        } else {
            $order['delivery_address'] = null; // No address_id or invalid
        }

        // Add the complete order (including its items) to the main orders array
        $orders[] = $order;
    }

    // Return a success JSON response with the fetched orders
    echo json_encode(['success' => true, 'orders' => $orders]);

} catch (PDOException $e) {
    // If a query fails, return a JSON error response
    echo json_encode(['error' => 'Query failed: ' . $e->getMessage()]);
}
?>
