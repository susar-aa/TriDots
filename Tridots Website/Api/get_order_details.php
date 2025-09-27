<?php
header('Content-Type: application/json');

// --- Database Configuration (Embedded) ---
$host = 'localhost';
$port = 3306;     // Port as an integer
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';
// --- End Database Configuration ---

// --- Error Reporting (for debugging - remove in production) ---
// This is crucial for seeing detailed errors on your server.
// REMOVE OR COMMENT OUT THESE LINES IN A PRODUCTION ENVIRONMENT FOR SECURITY.
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
// --- End Error Reporting ---


$response = array();

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    // Get order_id from POST request. Ensure it's an integer and not empty.
    $order_id = isset($_POST['order_id']) ? intval($_POST['order_id']) : 0;

    if ($order_id <= 0) {
        $response['success'] = false;
        $response['message'] = "Invalid Order ID provided.";
        echo json_encode($response);
        exit();
    }

    // Create database connection using MySQLi.
    $conn = new mysqli($host, $username, $password, $dbname, $port);

    // Check if the database connection was successful.
    if ($conn->connect_error) {
        $response['success'] = false;
        $response['message'] = "Database connection failed: " . $conn->connect_error;
        // Log the full connection error for server-side debugging.
        error_log("Database connection failed: " . $conn->connect_error);
        echo json_encode($response);
        exit();
    }

    // Prepare the SQL statement to fetch main order details.
    // Table names are now capitalized based on your provided schema (Orders, UserAddresses, Users).
    // 'sub_total_amount' is commented out as it's not in your 'Orders' table schema.
    $stmt = $conn->prepare("SELECT o.order_id, o.user_id, o.order_date, o.total_amount, o.delivery_fee,
                                   o.payment_method, o.payment_status, o.order_status,
                                   -- o.sub_total_amount, -- Uncomment this line IF you add this column to your 'Orders' table
                                   ua.address_id, ua.address_title, ua.full_address, ua.city, ua.postal_code,
                                   u.username as customer_name -- Fetching username from the 'Users' table
                            FROM Orders o -- Corrected table casing
                            JOIN UserAddresses ua ON o.address_id = ua.address_id -- Corrected table casing
                            JOIN Users u ON o.user_id = u.user_id -- Corrected table casing
                            WHERE o.order_id = ?");
    
    // Check if the statement preparation was successful.
    if (!$stmt) {
        $response['success'] = false;
        $response['message'] = "Failed to prepare order details statement: " . $conn->error;
        error_log("Failed to prepare statement (main order): " . $conn->error);
        echo json_encode($response);
        $conn->close();
        exit();
    }

    // Bind the order_id parameter to the prepared statement and execute it.
    $stmt->bind_param("i", $order_id);
    $stmt->execute();
    $result = $stmt->get_result();

    // Process the results if an order is found.
    if ($result->num_rows > 0) {
        $order = $result->fetch_assoc();

        // Calculate 'sub_total_amount' if it's not directly available from the database.
        // This is a fallback if the 'Orders' table doesn't store sub_total_amount.
        // Convert to float for calculation, then format back to 2 decimal places as a string.
        if (!isset($order['sub_total_amount'])) {
            $order['sub_total_amount'] = (float)$order['total_amount'] - (float)$order['delivery_fee'];
            $order['sub_total_amount'] = sprintf("%.2f", $order['sub_total_amount']);
        }

        // Format the 'order_date' for consistent display on the Android side.
        if (isset($order['order_date'])) {
            $order['order_date'] = date('Y-m-d H:i:s', strtotime($order['order_date']));
        }

        // Structure the delivery address details into a nested object as expected by Android.
        $delivery_address = array(
            'address_id' => $order['address_id'],
            'address_title' => $order['address_title'],
            'full_address' => $order['full_address'],
            'city' => $order['city'],
            'postal_code' => $order['postal_code'],
            // Use the delivery fee from the 'Orders' table, convert to string for BigDecimal.
            'delivery_fee' => (string)$order['delivery_fee'] 
        );
        // Remove individual address fields from the main 'order' array to prevent duplication in JSON.
        unset($order['address_id'], $order['address_title'], $order['full_address'], $order['city'], $order['postal_code']);
        $order['delivery_address'] = $delivery_address;

        // Prepare the SQL statement to fetch order items.
        // Table names are now capitalized based on your provided schema (Order_Items, Marketplace, Product_Variants).
        $items_stmt = $conn->prepare("SELECT oi.product_id, m.product_name, oi.quantity, oi.unit_price,
                                             pv.variant_name -- LEFT JOIN for optional variant details
                                      FROM Order_Items oi -- Corrected table casing
                                      JOIN Marketplace m ON oi.product_id = m.product_id -- Corrected table casing
                                      LEFT JOIN Product_Variants pv ON oi.variant_id = pv.variant_id -- <<< CORRECTED TABLE NAME for variants
                                      WHERE oi.order_id = ?");

        // Check if the statement preparation for order items was successful.
        if (!$items_stmt) {
            $response['success'] = false;
            $response['message'] = "Failed to prepare order items statement: " . $conn->error;
            error_log("Failed to prepare statement (order items): " . $conn->error);
            echo json_encode($response);
            $conn->close();
            exit();
        }

        // Bind the order_id parameter and execute the statement.
        $items_stmt->bind_param("i", $order_id);
        $items_stmt->execute();
        $items_result = $items_stmt->get_result();

        // Fetch and process each order item.
        $items = array();
        while ($item = $items_result->fetch_assoc()) {
            // Explicitly cast quantity to int and unit_price to string for Android's BigDecimal.
            $item['quantity'] = (int)$item['quantity']; 
            $item['unit_price'] = (string)$item['unit_price'];
            
            // Handle 'variant_name': if it's not set (e.g., product has no variant), set it to null.
            // If the LEFT JOIN for variants results in NULL for variant_name, ensure it's handled.
            if (!isset($item['variant_name'])) {
                $item['variant_name'] = null; 
            }
            $items[] = $item;
        }
        $order['items'] = $items; // Add the fetched items array to the main order data.

        $response['success'] = true;
        $response['order'] = $order; // Include the complete order data in the response.

    } else {
        // If no order is found for the given ID.
        $response['success'] = false;
        $response['message'] = "Order not found for ID: " . $order_id . ".";
    }

    // Close prepared statements and database connection.
    $stmt->close();
    if (isset($items_stmt)) $items_stmt->close(); // Close only if it was successfully prepared.
    $conn->close(); 

} else {
    // Handle cases where the request method is not POST.
    $response['success'] = false;
    $response['message'] = "Invalid request method. Only POST requests are allowed for this endpoint.";
}

// Encode the final response array to JSON and output it.
echo json_encode($response);
?>
