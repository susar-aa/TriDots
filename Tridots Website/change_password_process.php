<?php
session_start();
require_once 'db.php';

if (!isset($_SESSION['user_id'])) {
    header('Location: login.php');
    exit;
}

$user_id = $_SESSION['user_id'];
$errors = [];

// Get POST data
$current_password = $_POST['current_password'] ?? '';
$new_password = $_POST['new_password'] ?? '';
$confirm_password = $_POST['confirm_password'] ?? '';

// Validate
if ($new_password === '' || strlen($new_password) < 6) {
    $errors[] = "New password must be at least 6 characters.";
}
if ($new_password !== $confirm_password) {
    $errors[] = "Passwords do not match.";
}

// Fetch user's current password hash
$stmt = $pdo->prepare("SELECT password_hash FROM Users WHERE user_id=?");
$stmt->execute([$user_id]);
$hash = $stmt->fetchColumn();

if (!$hash || !password_verify($current_password, $hash)) {
    $errors[] = "Current password is incorrect.";
}

if (!empty($errors)) {
    echo "<h3>Change Password Error</h3><ul><li>" . implode("</li><li>", $errors) . "</li></ul>";
    echo '<a href="change_password.php">Go back</a>';
    exit;
}

// Hash new password
$new_hash = password_hash($new_password, PASSWORD_DEFAULT);

// Update
$stmt = $pdo->prepare("UPDATE Users SET password_hash=? WHERE user_id=?");
$stmt->execute([$new_hash, $user_id]);

header("Location: dashboard.php?success=password_changed");
exit;
?>