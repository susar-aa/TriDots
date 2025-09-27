<?php
// Enable full error reporting for debugging
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// Set the content type to application/json
header('Content-Type: application/json');
// Allow cross-origin requests (for development - adjust as needed for production)
header('Access-Control-Allow-Origin: *');

// Database credentials
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

// Log file path (within your website's directory)
$logFilePath = '/var/www/vhosts/lionsgoldencircle.com/httpdocs/Tridots/Api/logs/php_error.log';

// Get source and ID from the request
$source = $_GET['source'] ?? null;
$id = $_GET['id'] ?? null;

$response = [];

try {
    // Create the logs directory if it doesn't exist
    $logDir = dirname($logFilePath);
    if (!is_dir($logDir)) {
        if (!mkdir($logDir, 0755, true)) {
            $response = ["error" => "Could not create log directory. Check server permissions."];
            echo json_encode($response);
            exit();
        }
    }

    // Create a database connection
    $conn = new mysqli($host, $username, $password, $dbname);

    // Check for connection errors
    if ($conn->connect_error) {
        $error_message = "Database connection failed: " . $conn->connect_error;
        error_log("[".date("Y-m-d H:i:s")."] PHP Error - " . $error_message . " - Source: " . $source . ", ID: " . $id . "\n", 3, $logFilePath);
        $response = ["error" => $error_message];
        echo json_encode($response);
        exit(); // Stop further execution
    }

    $stmt = null; // Initialize statement

    // Prepare SQL based on the source
    switch ($source) {
        case 'renting':
            $sql = "SELECT * FROM renting_ads WHERE product_id = ?";
            $stmt = $conn->prepare($sql);
            break;
        case 'vehicle':
            $sql = "SELECT * FROM Vehicles WHERE vehicle_id = ?";
            $stmt = $conn->prepare($sql);
            break;
        case 'service_provider':
            $sql = "SELECT * FROM Service_Providers WHERE seller_id = ?";
            $stmt = $conn->prepare($sql);
            break;
        case 'service_category':
            $sql = "SELECT * FROM ServicesCategory WHERE service_category_id = ?";
            $stmt = $conn->prepare($sql);
            break;
        case 'main_category':
            $sql = "SELECT * FROM main_category WHERE main_category_id = ?";
            $stmt = $conn->prepare($sql);
            break;
        case 'sub_category':
            $sql = "SELECT * FROM sub_category WHERE sub_category_id = ?";
            $stmt = $conn->prepare($sql);
            break;
        case 'vehicle_category':
            $sql = "SELECT * FROM VehicleCategory WHERE vehicle_category_id = ?";
            $stmt = $conn->prepare($sql);
            break;
        default:
            $error_message = "Invalid source: " . $source;
            error_log("[".date("Y-m-d H:i:s")."] PHP Error - " . $error_message . " - Source: " . $source . ", ID: " . $id . "\n", 3, $logFilePath);
            $response = ["error" => $error_message];
            echo json_encode($response);
            $conn->close();
            exit();
    }

    // Check if the statement was prepared successfully
    if ($stmt === false) {
        $error_message = "Error preparing SQL statement for " . $source . ": " . $conn->error;
        error_log("[".date("Y-m-d H:i:s")."] PHP Error - " . $error_message . " - Source: " . $source . ", ID: " . $id . "\n", 3, $logFilePath);
        $response = ["error" => $error_message];
    } else {
        // Bind the ID parameter
        $stmt->bind_param("i", $id);

        // Execute the query
        if ($stmt->execute()) {
            $result = $stmt->get_result();

            // Check if any rows were found
            if ($result->num_rows > 0) {
                $response = $result->fetch_assoc();
            } else {
                $response = ["error" => "No data found for source: " . $source . " and ID: " . $id];
            }

            $stmt->close();
        } else {
            $error_message = "Error executing SQL statement for " . $source . ": " . $stmt->error;
            error_log("[".date("Y-m-d H:i:s")."] PHP Error - " . $error_message . " - Source: " . $source . ", ID: " . $id . "\n", 3, $logFilePath);
            $response = ["error" => $error_message];
        }
    }

} catch (Exception $e) {
    $error_message = "An unexpected error occurred: " . $e->getMessage();
    error_log("[".date("Y-m-d H:i:s")."] PHP Exception - " . $error_message . " - Source: " . $source . ", ID: " . $id . "\n", 3, $logFilePath);
    $response = ["error" => "An unexpected server error occurred."];
    // Optionally, you could echo a more detailed error for development purposes only:
    // $response = ["error" => $error_message];
} finally {
    // Ensure the database connection is closed if it was opened
    if (isset($conn) && $conn) {
        $conn->close();
    }

    // Output the JSON response
    echo json_encode($response);
}
?>