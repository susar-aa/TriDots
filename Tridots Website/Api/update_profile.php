<?php
header('Content-Type: application/json');
error_reporting(E_ALL);
ini_set('display_errors', 0); // Disable error display to prevent output before JSON

// Database configuration
$host = 'localhost:3306';
$dbname = 'tridots';
$db_username = 'tridots';
$db_password = 'tridots12369';

// Image upload configuration - using relative paths within allowed directory
$upload_dir_filesystem = '../../Tridots/images/ProfilePictures/';
$public_base_url = 'https://lionsgoldencircle.com/Tridots/images/ProfilePictures/';

// Create directory if it doesn't exist
if (!file_exists($upload_dir_filesystem)) {
    if (!mkdir($upload_dir_filesystem, 0755, true)) {
        echo json_encode(['status' => 'error', 'message' => 'Failed to create upload directory']);
        exit();
    }
}

// Check if directory is writable
if (!is_writable($upload_dir_filesystem)) {
    echo json_encode(['status' => 'error', 'message' => 'Upload directory not writable']);
    exit();
}

// Establish database connection
$conn = new mysqli($host, $db_username, $db_password, $dbname);

if ($conn->connect_error) {
    echo json_encode(['status' => 'error', 'message' => 'Database connection failed']);
    exit();
}

// Set charset to utf8
$conn->set_charset("utf8");

if ($_SERVER['REQUEST_METHOD'] == 'POST' && isset($_POST['action']) && $_POST['action'] == 'update_profile') {
    // Validate required fields
    if (!isset($_POST['current_username'])) {
        echo json_encode(['status' => 'error', 'message' => 'Current username is required']);
        exit();
    }

    // Sanitize and validate input
    $current_username = trim($conn->real_escape_string($_POST['current_username']));
    $new_username = isset($_POST['new_username']) ? trim($conn->real_escape_string($_POST['new_username'])) : $current_username;
    $email_address = isset($_POST['email_address']) ? trim($conn->real_escape_string($_POST['email_address'])) : null;
    $contact_number = isset($_POST['contact_number']) ? trim($conn->real_escape_string($_POST['contact_number'])) : null;
    $nic_number = isset($_POST['nic_number']) ? trim($conn->real_escape_string($_POST['nic_number'])) : null;
    $address = isset($_POST['address']) ? trim($conn->real_escape_string($_POST['address'])) : null;
    $business_name = isset($_POST['business_name']) ? trim($conn->real_escape_string($_POST['business_name'])) : null;
    
    // Initialize profile picture path
    $profile_picture_path = null;
    
    // Handle profile picture upload if provided
    if (isset($_POST['profile_picture_base64']) && !empty($_POST['profile_picture_base64'])) {
        $base64_image = $_POST['profile_picture_base64'];
        
        // Remove data URL prefix if present
        if (strpos($base64_image, 'data:image') === 0) {
            $base64_image = preg_replace('#^data:image/\w+;base64,#i', '', $base64_image);
        }
        
        // Decode the base64 image
        $image_data = base64_decode($base64_image);
        
        if ($image_data === false) {
            echo json_encode(['status' => 'error', 'message' => 'Invalid image data']);
            exit();
        }
        
        // Validate image size (max 2MB)
        if (strlen($image_data) > 2 * 1024 * 1024) {
            echo json_encode(['status' => 'error', 'message' => 'Image size exceeds 2MB limit']);
            exit();
        }
        
        // Generate unique filename with sanitized username
        $safe_username = preg_replace('/[^a-zA-Z0-9]/', '_', $new_username);
        $filename = $safe_username . '_' . uniqid() . '.jpg';
        $file_path = $upload_dir_filesystem . $filename;
        
        // Save the file
        if (file_put_contents($file_path, $image_data)) {
            $profile_picture_path = $public_base_url . $filename;
        } else {
            echo json_encode(['status' => 'error', 'message' => 'Failed to save image file']);
            exit();
        }
    }
    
    try {
        // Prepare SQL update
        $sql = "UPDATE Users SET 
                username = ?,
                email_address = ?,
                contact_number = ?,
                nic_number = ?,
                address = ?,
                business_name = ?";
        
        // Add profile picture to update if it was uploaded
        if ($profile_picture_path !== null) {
            $sql .= ", profile_picture = ?";
        }
        
        $sql .= " WHERE username = ?";
        
        $stmt = $conn->prepare($sql);
        
        if ($stmt === false) {
            throw new Exception("Prepare failed: " . $conn->error);
        }
        
        // Bind parameters
        if ($profile_picture_path !== null) {
            $stmt->bind_param("ssssssss", 
                $new_username,
                $email_address,
                $contact_number,
                $nic_number,
                $address,
                $business_name,
                $profile_picture_path,
                $current_username);
        } else {
            $stmt->bind_param("sssssss", 
                $new_username,
                $email_address,
                $contact_number,
                $nic_number,
                $address,
                $business_name,
                $current_username);
        }
        
        if (!$stmt->execute()) {
            throw new Exception("Execute failed: " . $stmt->error);
        }
        
        $affected_rows = $stmt->affected_rows;
        $stmt->close();
        
        if ($affected_rows > 0) {
            $response = [
                'status' => 'success', 
                'message' => 'Profile updated successfully'
            ];
            
            if ($profile_picture_path !== null) {
                $response['profile_picture'] = $profile_picture_path;
            }
            
            echo json_encode($response);
        } else {
            echo json_encode(['status' => 'success', 'message' => 'No changes made to profile']);
        }
        
    } catch (Exception $e) {
        echo json_encode(['status' => 'error', 'message' => 'Database operation failed']);
    }
    
} else {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method or missing action']);
}

$conn->close();
?>