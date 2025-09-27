<?php
session_start();
require_once 'db.php';

if (!isset($_SESSION['user_id'])) {
    header('Location: login.php');
    exit;
}

$user_id = $_SESSION['user_id'];
$errors = [];

// Helper function for safe file upload
function upload_profile_picture($file, $current_path = null) {
    $target_dir = "/uploads/profiles/";
    $abs_dir = $_SERVER['DOCUMENT_ROOT'] . $target_dir;
    if (!file_exists($abs_dir)) mkdir($abs_dir, 0755, true);

    $filename = basename($file['name']);
    $ext = strtolower(pathinfo($filename, PATHINFO_EXTENSION));
    $allowed_types = ['jpg', 'jpeg', 'png'];
    $max_size = 2 * 1024 * 1024;

    if (!in_array($ext, $allowed_types)) return [null, "Only JPG and PNG files allowed."];
    if ($file['size'] > $max_size) return [null, "Max file size is 2MB."];
    if ($file['error'] !== UPLOAD_ERR_OK) return [null, "File upload error."];

    $new_name = uniqid("profile_") . "." . $ext;
    $target_file = $abs_dir . $new_name;
    $rel_file = $target_dir . $new_name;

    if (move_uploaded_file($file['tmp_name'], $target_file)) {
        // Optionally delete old profile pic
        if ($current_path && file_exists($_SERVER['DOCUMENT_ROOT'] . $current_path)) {
            @unlink($_SERVER['DOCUMENT_ROOT'] . $current_path);
        }
        return [$rel_file, null];
    } else {
        return [null, "Failed to save uploaded file."];
    }
}

// Receive form data
$username = trim($_POST['username'] ?? '');
$email = trim($_POST['email_address'] ?? '');
$contact = trim($_POST['contact_number'] ?? '');

// Validate
if ($username === '') $errors[] = "Username is required.";
if ($email === '' || !filter_var($email, FILTER_VALIDATE_EMAIL)) $errors[] = "Valid email required.";

// Check uniqueness for username/email (exclude self)
$stmt = $pdo->prepare("SELECT COUNT(*) FROM Users WHERE (username=? OR email_address=?) AND user_id<>?");
$stmt->execute([$username, $email, $user_id]);
if ($stmt->fetchColumn() > 0) $errors[] = "Username or email is already taken.";

// Get current profile picture path
$stmt = $pdo->prepare("SELECT profile_picture FROM Users WHERE user_id=?");
$stmt->execute([$user_id]);
$current_pic = $stmt->fetchColumn();

// Handle profile picture upload
if (isset($_FILES['profile_picture']) && $_FILES['profile_picture']['name']) {
    list($profile_pic_path, $pic_err) = upload_profile_picture($_FILES['profile_picture'], $current_pic);
    if ($pic_err) $errors[] = $pic_err;
} else {
    $profile_pic_path = $current_pic;
}

// If errors, show and exit
if (!empty($errors)) {
    echo "<h3>Edit Profile Error</h3><ul><li>" . implode("</li><li>", $errors) . "</li></ul>";
    echo '<a href="edit_profile.php">Go back</a>';
    exit;
}

// Update in DB
$stmt = $pdo->prepare("UPDATE Users SET username=?, email_address=?, contact_number=?, profile_picture=? WHERE user_id=?");
$stmt->execute([$username, $email, $contact, $profile_pic_path, $user_id]);

// (Optional) update session username
$_SESSION['username'] = $username;

header("Location: dashboard.php?success=profile_updated");
exit;
?>