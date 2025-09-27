<?php
// Enable error reporting for debugging
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// Database connection parameters
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

header('Content-Type: application/json');

// Create connection
$conn = new mysqli($host, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    echo json_encode(["error" => "Connection failed: " . $conn->connect_error]);
    exit();
}

// Check if required parameters are provided
if (!isset($_POST['order_item_id']) || !isset($_POST['new_delivery_status']) || !isset($_POST['seller_id'])) {
    echo json_encode(["error" => "Required parameters (order_item_id, new_delivery_status, seller_id) not provided."]);
    $conn->close();
    exit();
}

$order_item_id = $conn->real_escape_string($_POST['order_item_id']);
$new_delivery_status = $conn->real_escape_string($_POST['new_delivery_status']);
$seller_id = $conn->real_escape_string($_POST['seller_id']);

// --- Security Check: Ensure the seller owns the product associated with this order item ---
// First, get the product_id from Order_Items
// Then, get the seller_id from Marketplace for that product_id
// And verify it matches the provided seller_id

$check_owner_sql = "
    SELECT
        m.seller_id
    FROM
        Order_Items oi
    JOIN
        Marketplace m ON oi.product_id = m.product_id
    WHERE
        oi.order_item_id = ?;
";

$stmt_check = $conn->prepare($check_owner_sql);
if (!$stmt_check) {
    echo json_encode(["error" => "Owner check prepare failed: " . $conn->error]);
    $conn->close();
    exit();
}
$stmt_check->bind_param("i", $order_item_id);
$stmt_check->execute();
$check_result = $stmt_check->get_result();

if ($check_result->num_rows == 0) {
    echo json_encode(["error" => "Order item not found or no associated product."]);
    $stmt_check->close();
    $conn->close();
    exit();
}

$row = $check_result->fetch_assoc();
$actual_seller_id = $row['seller_id'];
$stmt_check->close();

if ($actual_seller_id != $seller_id) {
    echo json_encode(["error" => "Unauthorized: You do not own this product's order item."]);
    $conn->close();
    exit();
}
// --- End Security Check ---


// Update the delivery_status
$sql = "
    UPDATE Order_Items
    SET delivery_status = ?
    WHERE order_item_id = ?;
";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode(["error" => "SQL Prepare failed: " . $conn->error]);
    $conn->close();
    exit();
}

$stmt->bind_param("si", $new_delivery_status, $order_item_id); // 's' for string, 'i' for integer

if ($stmt->execute()) {
    if ($stmt->affected_rows > 0) {
        echo json_encode(["success" => "Delivery status updated successfully."]);
    } else {
        echo json_encode(["error" => "No rows updated. Order item ID might not exist or status is already the same."]);
    }
} else {
    echo json_encode(["error" => "Execute failed: " . $stmt->error]);
}

$stmt->close();
$conn->close();
?>