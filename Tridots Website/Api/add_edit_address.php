<?php
// =================================================================
// ADD/EDIT ADDRESS - FINAL API ENDPOINT (V11 - Schema Corrected)
// Handles both creating and updating user addresses.
// =================================================================

header('Content-Type: application/json');
session_start();
require_once 'db.php'; // Uses the PDO connection from db.php

if ($_SERVER['REQUEST_METHOD'] !== 'POST' || !isset($_SESSION['user_data']['user_id'])) {
    http_response_code(403);
    echo json_encode(['success' => false, 'message' => 'Unauthorized access.']);
    exit;
}

$userId = $_SESSION['user_data']['user_id'];
$addressId = filter_input(INPUT_POST, 'address_id', FILTER_VALIDATE_INT);

// All fields from the Android app for full compatibility
$params = [
    'user_id' => $userId,
    'address_title' => $_POST['address_title'] ?? '',
    'full_address' => $_POST['full_address'] ?? '',
    'city' => $_POST['city'] ?? '',
    'postal_code' => $_POST['postal_code'] ?? '',
    // 'contact_phone' is captured but not used in the final query as the column does not exist.
    'street_number' => $_POST['street_number'] ?? '',
    'street_name' => $_POST['street_name'] ?? '',
    'district' => $_POST['district'] ?? '',
    'province' => $_POST['province'] ?? '',
    'country' => $_POST['country'] ?? 'Sri Lanka',
    'is_default' => isset($_POST['is_default']) ? (int)$_POST['is_default'] : 0,
];

if (empty($params['address_title']) || empty($params['full_address']) || empty($params['city'])) {
    http_response_code(400);
    echo json_encode(['success' => false, 'message' => 'Address Title, Full Address, and City are required.']);
    exit;
}

try {
    if ($addressId) {
        // --- UPDATE existing address ---
        $params['address_id'] = $addressId;
        // ** CORRECTION **: `contact_phone` is removed from the UPDATE statement as it's not in the DB schema.
        $sql = "UPDATE UserAddresses SET address_title = :address_title, full_address = :full_address, city = :city, postal_code = :postal_code WHERE address_id = :address_id AND user_id = :user_id";
        $stmt = $pdo->prepare($sql);
        $stmt->execute([
            ':address_title' => $params['address_title'],
            ':full_address' => $params['full_address'],
            ':city' => $params['city'],
            ':postal_code' => $params['postal_code'],
            ':address_id' => $addressId,
            ':user_id' => $userId
        ]);
        $message = 'Address updated successfully!';
    } else {
        // --- INSERT new address ---
        // ** CORRECTION **: `contact_phone` is removed from the INSERT statement.
        $sql = "INSERT INTO UserAddresses (user_id, address_title, full_address, street_number, street_name, city, postal_code, district, province, country, is_default) VALUES (:user_id, :address_title, :full_address, :street_number, :street_name, :city, :postal_code, :district, :province, :country, :is_default)";
        $stmt = $pdo->prepare($sql);
        $stmt->execute([
            ':user_id' => $params['user_id'],
            ':address_title' => $params['address_title'],
            ':full_address' => $params['full_address'],
            ':street_number' => $params['street_number'],
            ':street_name' => $params['street_name'],
            ':city' => $params['city'],
            ':postal_code' => $params['postal_code'],
            ':district' => $params['district'],
            ':province' => $params['province'],
            ':country' => $params['country'],
            ':is_default' => $params['is_default'],
        ]);
        $message = 'Address added successfully!';
    }
    
    echo json_encode(['success' => true, 'message' => $message]);

} catch (PDOException $e) {
    error_log("Database Error in add_edit_address: " . $e->getMessage());
    http_response_code(500);
    echo json_encode(['success' => false, 'message' => 'A database error occurred. Please check the server logs for details.']);
}
?>
