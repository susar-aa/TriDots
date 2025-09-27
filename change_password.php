<?php
session_start();
require_once 'db.php';
if (!isset($_SESSION['user_id'])) {
    header('Location: login.php');
    exit;
}

// Handle form POST in separate file (change_password_process.php)
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Change Password</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        body {
            background: #f7fafc;
            font-family: 'Inter', Arial, sans-serif;
        }
        .cpw-container {
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
        .cpw-form label {
            font-weight: 500;
            color: #2d3748;
            margin-bottom: 7px;
            display: block;
        }
        .cpw-form input[type="password"] {
            width: 100%;
            padding: 11px 12px;
            border: 1.5px solid #e2e8f0;
            border-radius: 0.7rem;
            margin-bottom: 18px;
            font-size: 1rem;
            background: #f9fafd;
        }
        .cpw-form button {
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
        .cpw-form button:hover {
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
    <div class="cpw-container">
        <h2><i class="fas fa-key"></i> Change Password</h2>
        <form class="cpw-form" action="change_password_process.php" method="POST" autocomplete="off">
            <label for="current_password">Current Password</label>
            <input type="password" name="current_password" id="current_password" required autocomplete="current-password">

            <label for="new_password">New Password</label>
            <input type="password" name="new_password" id="new_password" required autocomplete="new-password">

            <label for="confirm_password">Confirm New Password</label>
            <input type="password" name="confirm_password" id="confirm_password" required autocomplete="new-password">

            <button type="submit"><i class="fas fa-key"></i> Change Password</button>
        </form>
        <a href="dashboard.php" class="back-link"><i class="fas fa-chevron-left"></i> Back to Dashboard</a>
    </div>
</body>
</html>