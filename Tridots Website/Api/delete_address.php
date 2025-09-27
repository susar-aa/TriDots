<?php
// =================================================================
// DELETE ADDRESS SCRIPT (TRIDOTS API) - V2 (Advanced)
// Handles deleting an address and re-assigning the default if necessary.
// =================================================================

// --- IMPORTANT: Enable error reporting for debugging (REMOVE IN PRODUCTION) ---
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
// ----------------------------------------------------------------------------

header('Content-Type: application/json');
session_start(); // Start session to get user_id

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
    // If connection fails, immediately output JSON error and exit
    http_response_code(500);
    echo json_encode([
        "success" => false,
        "message" => "Database connection failed: " . $e->getMessage()
    ]);
    exit();
}
// --- End of database connection logic ---

$response = array();

// Check if PDO connection object ($pdo) is available
if ($pdo === null) {
    $response['success'] = false;
    $response['message'] = "Internal Server Error: Database connection not established.";
    http_response_code(500);
    echo json_encode($response);
    exit();
}

// Use user_id from session for security, not from POST
if (isset($_POST['address_id']) && isset($_SESSION['user_data']['user_id'])) {
    $address_id = $_POST['address_id'];
    $user_id = $_SESSION['user_data']['user_id'];

    // Start a transaction for atomicity
    $pdo->beginTransaction();

    try {
        // Check if the address exists and belongs to the user, and get its is_default status
        // Note: The table name is assumed to be 'user_addresses' (lowercase with underscore)
        // based on standard conventions and previous files. Change if your table name is different.
        $stmt_check = $pdo->prepare("SELECT is_default FROM user_addresses WHERE address_id = :address_id AND user_id = :user_id");
        $stmt_check->bindParam(':address_id', $address_id, PDO::PARAM_INT);
        $stmt_check->bindParam(':user_id', $user_id, PDO::PARAM_INT);
        $stmt_check->execute();
        $is_default = $stmt_check->fetchColumn(); // Fetch just the 'is_default' value

        if ($is_default === false) { // PDO fetchColumn returns false if no rows found
            throw new Exception("Address not found or does not belong to user.");
        }

        // Delete the address
        $stmt = $pdo->prepare("DELETE FROM user_addresses WHERE address_id = :address_id AND user_id = :user_id");
        $stmt->bindParam(':address_id', $address_id, PDO::PARAM_INT);
        $stmt->bindParam(':user_id', $user_id, PDO::PARAM_INT);

        if ($stmt->execute()) {
            // If the deleted address was the default, and there are other addresses, set a new default
            if ($is_default == 1) {
                $stmt_count_remaining = $pdo->prepare("SELECT COUNT(*) FROM user_addresses WHERE user_id = :user_id");
                $stmt_count_remaining->bindParam(':user_id', $user_id, PDO::PARAM_INT);
                $stmt_count_remaining->execute();
                $remaining_addresses_count = $stmt_count_remaining->fetchColumn();

                if ($remaining_addresses_count > 0) {
                    // Find the oldest (lowest ID) remaining address and make it default
                    $set_new_default_sql = "UPDATE user_addresses SET is_default = 1 WHERE user_id = :user_id ORDER BY address_id ASC LIMIT 1";
                    $stmt_set_default = $pdo->prepare($set_new_default_sql);
                    $stmt_set_default->bindParam(':user_id', $user_id, PDO::PARAM_INT);
                    $stmt_set_default->execute();
                }
            }
            $response['success'] = true;
            $response['message'] = "Address deleted successfully.";
            $pdo->commit(); // Commit the transaction
        } else {
            // If execution fails, throw an exception with error info
            throw new Exception("Error deleting address: " . implode(" ", $stmt->errorInfo()));
        }
    } catch (PDOException $e) {
        $pdo->rollBack(); // Rollback on PDO error
        $response['success'] = false;
        $response['message'] = "DB Error: " . $e->getMessage();
        http_response_code(500);
    } catch (Exception $e) {
        $pdo->rollBack(); // Rollback on general exception
        $response['success'] = false;
        $response['message'] = $e->getMessage();
        http_response_code(404); // Not Found or Forbidden
    }
} else {
    $response['success'] = false;
    $response['message'] = "Missing address ID or user is not logged in.";
    http_response_code(400);
}

echo json_encode($response);
// PDO connection closes automatically when script ends
?>
