<?php
// Set response content type to JSON
header('Content-Type: application/json');

// --- Database Connection ---
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';
$charset = 'utf8mb4';

$dsn = "mysql:host=$host;dbname=$dbname;charset=$charset";
$options = [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES   => false,
];

try {
    $pdo = new PDO($dsn, $username, $password, $options);
} catch (\PDOException $e) {
    echo json_encode(['status' => 'error', 'message' => 'Database Connection Failed: ' . $e->getMessage()]);
    exit();
}


// Check if the required parameters are present
if (isset($_POST['ad_type'])) {
    $ad_type = $_POST['ad_type'];
    $idToDelete = null;
    $idColumn = '';
    $tableName = '';

    // Determine the table name and ID column based on the ad type
    if ($ad_type === 'Renting') {
        if (isset($_POST['rent_id'])) {
            $idToDelete = $_POST['rent_id'];
            $idColumn = 'rent_id';
            $tableName = 'renting'; // CORRECTED TABLE NAME
        } else {
            echo json_encode(['status' => 'error', 'message' => 'Missing rent_id parameter for Renting ad.']);
            exit();
        }
    } else if ($ad_type === 'Vehicle') {
        if (isset($_POST['vehicle_id'])) {
            $idToDelete = $_POST['vehicle_id'];
            $idColumn = 'vehicle_id';
            $tableName = 'Vehicles'; // This was already correct
        } else {
            echo json_encode(['status' => 'error', 'message' => 'Missing vehicle_id parameter for Vehicle ad.']);
            exit();
        }
    } else if ($ad_type === 'Service') {
        if (isset($_POST['seller_id'])) {
            $idToDelete = $_POST['seller_id'];
            $idColumn = 'seller_id';
            $tableName = 'Service_Providers'; // CORRECTED TABLE NAME
        } else {
            echo json_encode(['status' => 'error', 'message' => 'Missing seller_id parameter for Service ad.']);
            exit();
        }
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Invalid ad_type provided.']);
        exit();
    }

    // Prepare and execute the delete query using PDO
    $sql = "DELETE FROM `$tableName` WHERE `$idColumn` = :id";
    $stmt = $pdo->prepare($sql);

    if ($stmt) {
        $stmt->bindParam(':id', $idToDelete, PDO::PARAM_INT);

        if ($stmt->execute()) {
            if ($stmt->rowCount() > 0) {
                echo json_encode(['status' => 'success', 'message' => 'Ad deleted successfully.']);
            } else {
                echo json_encode(['status' => 'error', 'message' => 'No ad found with the given ID.']);
            }
        } else {
            echo json_encode(['status' => 'error', 'message' => 'Error executing delete query: ' . print_r($stmt->errorInfo(), true)]);
        }
    } else {
        echo json_encode(['status' => 'error', 'message' => 'Error preparing SQL statement: ' . print_r($pdo->errorInfo(), true)]);
    }

} else {
    // If required parameters are missing
    echo json_encode(['status' => 'error', 'message' => 'Missing ad_type parameter in the request.']);
}

$pdo = null; // Close connection
?>
