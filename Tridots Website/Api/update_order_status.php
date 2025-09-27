<?php
// update_order_status.php
header('Content-Type: application/json');

// --- IMPORTANT: Enable error reporting for debugging (REMOVE IN PRODUCTION) ---
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
// ----------------------------------------------------------------------------

// --- Database connection details and logic (self-contained) ---
$db_host = 'localhost';
$db_name = 'tridots';
$db_user = 'tridots';
$db_pass = 'tridots12369';

$pdo = null; // Initialize PDO object
try {
    $pdo = new PDO("mysql:host=$db_host;dbname=$db_name;charset=utf8mb4", $db_user, $db_pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);
} catch (PDOException $e) {
    error_log("DB Connection Error in update_order_status.php: " . $e->getMessage());
    echo json_encode(array(
        "success" => false,
        "message" => "Database connection failed: " . $e->getMessage()
    ));
    exit();
}
// --- End of database connection logic ---

$response = array();

// Check if the request method is POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405); // Method Not Allowed
    $response['success'] = false;
    $response['message'] = "Invalid request method. Only POST is allowed.";
    echo json_encode($response);
    exit();
}

// Validate required POST parameters
$required_params = ['order_id', 'order_status', 'payment_status'];
foreach ($required_params as $param) {
    if (!isset($_POST[$param])) {
        http_response_code(400); // Bad Request
        $response['success'] = false;
        $response['message'] = "Missing required parameter: " . $param;
        echo json_encode($response);
        exit();
    }
}

// Retrieve and sanitize POST parameters
$order_id = (int)$_POST['order_id'];
$order_status = $_POST['order_status'];
$payment_status = $_POST['payment_status'];
$stripe_payment_intent_id = isset($_POST['stripe_payment_intent_id']) ? $_POST['stripe_payment_intent_id'] : null;

error_log("update_order_status.php: Received order_id=" . $order_id . ", order_status=" . $order_status . ", payment_status=" . $payment_status . ", PI_ID=" . ($stripe_payment_intent_id ?: 'N/A'));

try {
    // Construct the SQL query for updating the order
    $sql = "UPDATE Orders SET order_status = :order_status, payment_status = :payment_status";
    if ($stripe_payment_intent_id !== null) {
        $sql .= ", stripe_payment_intent_id = :stripe_payment_intent_id";
    }
    $sql .= " WHERE order_id = :order_id";

    $stmt = $pdo->prepare($sql);

    // Bind parameters to the prepared statement
    $stmt->bindParam(':order_status', $order_status);
    $stmt->bindParam(':payment_status', $payment_status);
    if ($stripe_payment_intent_id !== null) {
        $stmt->bindParam(':stripe_payment_intent_id', $stripe_payment_intent_id);
    }
    $stmt->bindParam(':order_id', $order_id, PDO::PARAM_INT);

    // Execute the update query
    if ($stmt->execute()) {
        if ($stmt->rowCount() > 0) {
            // If at least one row was updated, it's a success
            $response['success'] = true;
            $response['message'] = "Order status updated successfully.";
            error_log("update_order_status.php: Order ID " . $order_id . " status updated to Order: " . $order_status . ", Payment: " . $payment_status);
        } else {
            // No rows updated, typically means the order_id was not found
            http_response_code(404); // Not Found
            $response['success'] = false;
            $response['message'] = "Order not found or no changes made for order ID: " . $order_id;
            error_log("update_order_status.php: Order ID " . $order_id . " not found for update.");
        }
    } else {
        // If query execution fails, throw an exception with PDO error info
        throw new Exception("Failed to execute update: " . implode(" ", $stmt->errorInfo()));
    }

} catch (PDOException $e) {
    http_response_code(500); // Internal Server Error
    $response['success'] = false;
    $response['message'] = "Database error during status update: " . $e->getMessage();
    error_log("update_order_status.php PDO Exception: " . $e->getMessage() . " SQLSTATE: " . $e->getCode());
} catch (Exception $e) {
    http_response_code(500); // Internal Server Error
    $response['success'] = false;
    $response['message'] = "An application error occurred during status update: " . $e->getMessage();
    error_log("update_order_status.php General Exception: " . $e->getMessage());
}

echo json_encode($response);
// PDO connection closes automatically when script ends
?>
