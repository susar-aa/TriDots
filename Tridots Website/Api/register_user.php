<?php
ini_set('display_errors', 1);
error_reporting(E_ALL);

include 'db.php';

$upload_dir = "../images/ProfilePictures/";

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $username = $_POST['username'] ?? '';
    $email = $_POST['email_address'] ?? '';
    $password = password_hash($_POST['password'] ?? '', PASSWORD_DEFAULT);
    $contact = $_POST['contact_number'] ?? '';
    $nic = $_POST['nic_number'] ?? '';
    $address = $_POST['address'] ?? '';
    $user_type = 'Personal';
    $verification_status = 'Not Verified';
    $is_active = 'Active';

    $image_path = null;
    if (isset($_FILES['profile_picture']) && $_FILES['profile_picture']['error'] == 0) {
        $file_name = uniqid() . '_' . basename($_FILES["profile_picture"]["name"]);
        $target_file = $upload_dir . $file_name;

        if (move_uploaded_file($_FILES["profile_picture"]["tmp_name"], $target_file)) {
            $image_path = "https://lionsgoldencircle.com/Tridots/images/ProfilePictures/" . $file_name;
        } else {
            echo json_encode(["success" => false, "message" => "Image upload failed"]);
            exit;
        }
    }

    if ($username && $email && $password && $nic) {
        $stmt = $conn->prepare("INSERT INTO Users (username, email_address, password_hash, contact_number, nic_number, user_type, address, profile_picture, verification_status, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("ssssssssss", $username, $email, $password, $contact, $nic, $user_type, $address, $image_path, $verification_status, $is_active);

        if ($stmt->execute()) {
            echo json_encode(["success" => true, "message" => "User registered successfully"]);
        } else {
            echo json_encode(["success" => false, "message" => "DB Error: " . $stmt->error]);
        }

        $stmt->close();
    } else {
        echo json_encode(["success" => false, "message" => "Missing required fields"]);
    }

    $conn->close();
} else {
    echo json_encode(["success" => false, "message" => "Invalid request method"]);
}
?>
