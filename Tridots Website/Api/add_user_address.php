<?php
// =================================================================
// ADD USER ADDRESS - FINAL API ENDPOINT (V8 - Full Schema Compatibility)
// Securely inserts a new address, handling all possible columns to prevent NOT NULL errors.
// =================================================================

// Set the content type to JSON for all responses
header('Content-Type: application/json');

// Start the session to access session variables
session_start();

// Include the new PDO database connection file
require_once 'db.php'; // Assumes db.php is in the same Api/ folder

// --- VALIDATION & SETUP ---
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405); // Method Not Allowed
    echo json_encode(['success' => false, 'message' => 'Invalid request method. Only POST is accepted.']);
    exit;
}

// Get user_id from the SESSION
$user_id = $_SESSION['user_data']['user_id'] ?? null;

// Get POST data for other fields.
$recipient_name = $_POST['recipient_name'] ?? null; // This is the name from the web form
$contact_phone = $_POST['contact_phone'] ?? null;
$full_address = $_POST['full_address'] ?? null;
$city = $_POST['city'] ?? null;
$postal_code = $_POST['postal_code'] ?? null;

// ** NEW **: Provide default values for ALL columns that exist in the database but not in the web form.
// This prevents "Column cannot be null" errors.
$street_number = $_POST['street_number'] ?? ''; // Default to empty string
$street_name = $_POST['street_name'] ?? '';   // Default to empty string
$district = $_POST['district'] ?? '';         // Default to empty string
$province = $_POST['province'] ?? '';         // Default to empty string
$country = $_POST['country'] ?? 'Sri Lanka'; // Default to 'Sri Lanka'
$is_default = isset($_POST['is_default']) ? (int)$_POST['is_default'] : 0; // Default to 0 (not default)

// Server-side validation for the essential fields
if (empty($user_id) || empty($recipient_name) || empty($full_address) || empty($city)) {
    http_response_code(400); // Bad Request
    echo json_encode(['success' => false, 'message' => 'Missing required fields. user_id, recipient_name, full_address, and city are required.']);
    exit;
}

// --- DATABASE OPERATION ---
// ** CORRECTION **: The SQL now includes all columns to match the database schema perfectly.
$sql = "INSERT INTO user_addresses (user_id, address_title, contact_phone, full_address, street_number, street_name, city, postal_code, district, province, country, is_default) VALUES (:user_id, :address_title, :contact_phone, :full_address, :street_number, :street_name, :city, :postal_code, :district, :province, :country, :is_default)";

try {
    // Prepare the statement using the $pdo object from db.php
    $stmt = $pdo->prepare($sql);

    // Execute the statement by passing an associative array of parameters.
    $stmt->execute([
        ':user_id' => $user_id,
        ':address_title' => $recipient_name, // Map form field to 'address_title' DB column
        ':contact_phone' => $contact_phone,
        ':full_address' => $full_address,
        ':street_number' => $street_number,
        ':street_name' => $street_name,
        ':city' => $city,
        ':postal_code' => $postal_code,
        ':district' => $district,
        ':province' => $province,
        ':country' => $country,
        ':is_default' => $is_default
    ]);

    // If this new address was set as default, we need to unset the old default.
    if ($is_default == 1) {
        $last_id = $pdo->lastInsertId();
        $update_sql = "UPDATE user_addresses SET is_default = 0 WHERE user_id = :user_id AND address_id != :address_id";
        $update_stmt = $pdo->prepare($update_sql);
        $update_stmt->execute([':user_id' => $user_id, ':address_id' => $last_id]);
    }

    // If execution is successful, send a success response.
    echo json_encode(['success' => true, 'message' => 'Address added successfully!']);

} catch (PDOException $e) {
    // If an exception occurs during prepare or execute, it's a server error.
    // Log the detailed error for the developer.
    error_log("Database Error on address insert: " . $e->getMessage());
    
    // Send a generic, safe error message to the user.
    http_response_code(500); // Internal Server Error
    echo json_encode(['success' => false, 'message' => 'A database error occurred while saving the address. Please check the server logs.']);
}

// No need to manually close the connection with PDO when the script ends.
?>
