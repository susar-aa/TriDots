<?php
session_start();
require_once 'db.php';

// Redirect if not logged in
if (!isset($_SESSION['user_id'])) {
    header('Location: login.php');
    exit;
}

// Fetch current user data for the form
$userData = [];
try {
    $stmt = $pdo->prepare("SELECT username, email_address, contact_number, profile_picture FROM Users WHERE user_id = ?");
    $stmt->execute([$_SESSION['user_id']]);
    $userData = $stmt->fetch(PDO::FETCH_ASSOC);
    if (!$userData) {
        session_destroy();
        header('Location: login.php?error=user_not_found');
        exit;
    }
} catch (PDOException $e) {
    error_log("DB Error: " . $e->getMessage());
    exit("DB Error");
}

// Handle form submission (pseudo, add your own backend validation/saving logic)
// if ($_SERVER['REQUEST_METHOD'] === 'POST') { /* ... */ }

?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Profile</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        body {
            background: #f7fafc;
            font-family: 'Inter', Arial, sans-serif;
        }
        .edit-profile-container {
            max-width: 430px;
            margin: 40px auto;
            background: #fff;
            border-radius: 1rem;
            box-shadow: 0 5px 20px rgba(0,113,90,0.10);
            padding: 32px 36px 28px 36px;
        }
        h2 {
            text-align: center;
            color: #00715A;
            margin-bottom: 22px;
            font-weight: 700;
        }
        .edit-profile-form label {
            font-weight: 500;
            color: #2d3748;
            margin-bottom: 7px;
            display: block;
        }
        .edit-profile-form input[type="text"],
        .edit-profile-form input[type="email"] {
            width: 100%;
            padding: 11px 12px;
            border: 1.5px solid #e2e8f0;
            border-radius: 0.7rem;
            margin-bottom: 18px;
            font-size: 1rem;
            background: #f9fafd;
        }
        .edit-profile-form input[type="file"] {
            margin-bottom: 18px;
        }
        .profile-picture-preview {
            display: flex;
            align-items: center;
            margin-bottom: 18px;
            gap: 18px;
        }
        .profile-picture-preview img {
            width: 64px; height: 64px;
            border-radius: 50%;
            object-fit: cover;
            border: 2px solid #00B07D;
        }
        .profile-picture-preview .fa-user-circle {
            font-size: 64px; color: #00715A; background: #e2e8f0; border-radius: 50%;
        }
        .edit-profile-form button {
            width: 100%;
            padding: 12px 0;
            background: linear-gradient(90deg,#00715A,#00B07D);
            color: #fff;
            font-weight: bold;
            border: none;
            border-radius: 0.7rem;
            font-size: 1.08rem;
            cursor: pointer;
            transition: background 0.18s, box-shadow 0.16s;
            box-shadow: 0 2px 8px rgba(0,113,90,0.08);
        }
        .edit-profile-form button:hover {
            background: linear-gradient(90deg,#00B07D,#00715A);
        }
        .back-link {
            display: block;
            margin: 18px auto 0 auto;
            text-align: center;
            color: #00715A;
            text-decoration: none;
            font-weight: 500;
        }
        .back-link:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="edit-profile-container">
        <h2><i class="fas fa-user-edit"></i> Edit Profile</h2>
        <form class="edit-profile-form" action="edit_profile_process.php" method="POST" enctype="multipart/form-data" autocomplete="off">
            <div class="profile-picture-preview">
                <?php if (!empty($userData['profile_picture']) && file_exists($_SERVER['DOCUMENT_ROOT'] . $userData['profile_picture'])): ?>
                    <img src="<?php echo htmlspecialchars($userData['profile_picture']); ?>" alt="Profile Picture">
                <?php else: ?>
                    <i class="fas fa-user-circle"></i>
                <?php endif; ?>
            </div>
            <label for="profile_picture">Change Profile Picture</label>
            <input type="file" id="profile_picture" name="profile_picture" accept="image/*">

            <label for="username">Username</label>
            <input type="text" name="username" id="username" maxlength="30" required value="<?php echo htmlspecialchars($userData['username']); ?>">

            <label for="email_address">Email Address</label>
            <input type="email" name="email_address" id="email_address" maxlength="60" required value="<?php echo htmlspecialchars($userData['email_address']); ?>">

            <label for="contact_number">Contact Number</label>
            <input type="text" name="contact_number" id="contact_number" maxlength="20" value="<?php echo htmlspecialchars($userData['contact_number']); ?>">

            <button type="submit"><i class="fas fa-save"></i> Save Changes</button>
        </form>
        <a href="dashboard.php" class="back-link"><i class="fas fa-chevron-left"></i> Back to Dashboard</a>
    </div>
</body>
</html>