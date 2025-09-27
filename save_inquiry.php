<?php
// save_inquiry.php (Corrected to use PDO)

// You can now remove the error reporting lines, but it's fine to leave them during testing.
error_reporting(E_ALL);
ini_set('display_errors', 1);

session_start();
header('Content-Type: application/json');

// Make sure this path is correct and that it creates the $pdo object
require_once __DIR__ . '/db.php'; 

// Check if user is logged in
if (!isset($_SESSION['user_signed_in']) || $_SESSION['user_signed_in'] !== true) {
    echo json_encode(['success' => false, 'message' => 'You must be logged in to send an inquiry.']);
    exit();
}

// Get raw POST data from the JavaScript fetch call
$input = file_get_contents('php://input');
$data = json_decode($input, true);

// Validate incoming data
if (
    !isset($data['user_id'], $data['ad_type'], $data['ad_id'], $data['ad_owner_id'], $data['inquirer_name'], $data['inquirer_email'], $data['inquiry_message']) ||
    empty($data['user_id']) || empty($data['ad_type']) || empty($data['ad_id']) || empty($data['ad_owner_id']) || empty($data['inquirer_name']) || empty($data['inquirer_email']) || empty($data['inquiry_message'])
) {
    echo json_encode(['success' => false, 'message' => 'Missing required inquiry data.']);
    exit();
}

// Use a try-catch block for robust PDO error handling
try {
    // Assign data to variables (no need for real_escape_string with PDO prepared statements)
    $user_id = (int)$data['user_id'];
    $ad_type = $data['ad_type'];
    $ad_id = (int)$data['ad_id'];
    $ad_owner_id = (int)$data['ad_owner_id'];
    $inquirer_name = $data['inquirer_name'];
    $inquirer_email = $data['inquirer_email'];
    $inquirer_phone = isset($data['inquirer_phone']) && !empty($data['inquirer_phone']) ? $data['inquirer_phone'] : NULL;
    $inquiry_message = $data['inquiry_message'];

    // Prepare SQL statement using PDO style
    $sql = "INSERT INTO inquiries (user_id, ad_type, ad_id, ad_owner_id, inquirer_name, inquirer_email, inquirer_phone, inquiry_message) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    $stmt = $pdo->prepare($sql);

    // Execute the statement by passing the parameters as an array
    // This is the PDO way to bind parameters and is very secure
    $stmt->execute([
        $user_id,
        $ad_type,
        $ad_id,
        $ad_owner_id,
        $inquirer_name,
        $inquirer_email,
        $inquirer_phone,
        $inquiry_message
    ]);

    // If we get here, the query was successful
    echo json_encode(['success' => true, 'message' => 'Inquiry sent successfully!']);

} catch (PDOException $e) {
    // If any database error occurs, the catch block will run
    // This will report the actual database error for debugging
    echo json_encode(['success' => false, 'message' => 'Database error: ' . $e->getMessage()]);
}
?>