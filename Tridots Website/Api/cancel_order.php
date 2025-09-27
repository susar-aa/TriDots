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

// Check if order_id is provided in the POST request
if (!isset($_POST['order_id'])) {
    echo json_encode(['success' => false, 'message' => 'Order ID not provided.']);
    exit();
}

$orderId = $_POST['order_id'];

try {
    // Start a transaction to ensure atomicity
    $pdo->beginTransaction();

    // First, check the current status of the order
    $stmtCheck = $pdo->prepare("SELECT order_status FROM Orders WHERE order_id = :order_id");
    $stmtCheck->bindParam(':order_id', $orderId, PDO::PARAM_INT);
    $stmtCheck->execute();
    $currentStatus = $stmtCheck->fetchColumn();

    // Only allow cancellation if the order is "Pending"
    if ($currentStatus === "Pending") {
        // Update the order status to 'Cancelled'
        $stmtUpdate = $pdo->prepare("UPDATE Orders SET order_status = 'Cancelled' WHERE order_id = :order_id");
        $stmtUpdate->bindParam(':order_id', $orderId, PDO::PARAM_INT);
        $stmtUpdate->execute();

        // Commit the transaction
        $pdo->commit();
        echo json_encode(['success' => true, 'message' => 'Order cancelled successfully.']);
    } elseif ($currentStatus === false) {
        // No order found with the given ID
        $pdo->rollBack(); // Rollback if transaction started
        echo json_encode(['success' => false, 'message' => 'Order not found.']);
    } else {
        // Order is not in a cancellable state
        $pdo->rollBack(); // Rollback if transaction started
        echo json_encode(['success' => false, 'message' => 'Order cannot be cancelled. Current status is ' . $currentStatus . '. Only Pending orders can be cancelled.']);
    }

} catch (PDOException $e) {
    // Rollback the transaction on error
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    echo json_encode(['success' => false, 'message' => 'Failed to cancel order: ' . $e->getMessage()]);
}
?>
