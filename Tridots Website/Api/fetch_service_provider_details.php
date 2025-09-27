<?php
// Set the content type of the response to JSON
header('Content-Type: application/json');
header("Access-Control-Allow-Origin: *");

// --- DATABASE CONNECTION ---
$db_host = 'localhost:3306';
$db_name = 'tridots';
$db_username = 'tridots';
$db_password = 'tridots12369';

$conn = new mysqli($db_host, $db_username, $db_password, $db_name);

if ($conn->connect_error) {
    http_response_code(500);
    echo json_encode(['status' => 'error', 'message' => 'Database connection failed: ' . $conn->connect_error]);
    exit();
}
$conn->set_charset("utf8mb4");

// --- INPUT VALIDATION ---
if (!isset($_GET['id']) || !is_numeric($_GET['id'])) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'A valid Service Provider ID is required.']);
    exit();
}
$serviceProviderId = intval($_GET['id']);

// --- FINAL CORRECTED SQL QUERY ---
/**
 * This query has been fully updated to match all your provided table structures.
 * - All table names and column names are now correct.
 */
$sql = "SELECT
            sp.seller_id AS id,
            sp.name,
            sp.description,
            sp.contact_number,
            sp.email_address,
            sp.address,
            sp.location,
            sp.experience_years,
            sp.qualifications,
            sp.availability_status,
            sp.verification_status,
            sp.review_count,
            sp.reviews_average,
            sp.User_id AS user_id,
            sp.profile_picture,
            u.username AS lister_name,
            sc.service_category_name AS service_category_name -- FIXED: Was sc.name
        FROM
            Service_Providers sp
        JOIN
            Users u ON sp.User_id = u.user_id
        JOIN
            ServicesCategory sc ON sp.service_category_id = sc.service_category_id -- FIXED: Was service_categories and sc.id
        WHERE
            sp.seller_id = ?";

// --- DATABASE EXECUTION ---
$stmt = $conn->prepare($sql);

if ($stmt === false) {
    http_response_code(500);
    echo json_encode([
        'status' => 'error',
        'message' => 'Failed to prepare the SQL statement. Check SQL syntax.',
        'sql_error' => $conn->error
    ]);
    $conn->close();
    exit();
}

$stmt->bind_param("i", $serviceProviderId);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    // Success: A record was found
    $providerDetails = $result->fetch_assoc();
    http_response_code(200);
    echo json_encode($providerDetails);
} else {
    // Not Found: No record with the given ID
    http_response_code(404);
    echo json_encode(['status' => 'error', 'message' => 'Service provider with ID ' . $serviceProviderId . ' not found.']);
}

// Clean up
$stmt->close();
$conn->close();
?>
