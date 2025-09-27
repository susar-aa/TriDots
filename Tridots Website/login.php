<?php
// --- KEEP USERS LOGGED IN UNTIL EXPLICIT LOGOUT: EXTENDED SESSION LIFETIME ---

// Set session cookie to last 30 days (even if browser closes)
$lifetime = 60 * 60 * 24 * 30; // 30 days in seconds
session_set_cookie_params([
    'lifetime' => $lifetime,
    'path' => '/',
    'domain' => '', // Add your domain if needed
    'secure' => isset($_SERVER['HTTPS']), // true if using HTTPS
    'httponly' => true,
    'samesite' => 'Lax'
]);
session_start();
require_once __DIR__ . '/db.php';

// Handle form submission
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    if (isset($_POST['action']) && $_POST['action'] == 'Sign In') {
        $email = $_POST['email'];
        $password = $_POST['password'];

        $stmt = $pdo->prepare("SELECT * FROM Users WHERE email_address = ?");
        $stmt->execute([$email]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($user) {
            if ($user['verification_status'] != 'Verified') {
                $error = "Your account has not been verified. Please verify your email.";
            } elseif ($user['is_active'] != 'Active') {
                $error = "Your account is inactive. Please contact support.";
            } elseif (password_verify($password, $user['password_hash'])) {
                // Regenerate session ID for security
                session_regenerate_id(true);

                // Set session variables for all dashboards
                $_SESSION['user_signed_in'] = true;
                $_SESSION['user_id'] = $user['user_id'];
                $_SESSION['user_type'] = $user['user_type'];
                $_SESSION['username'] = $user['full_name'] ?? $user['username'];
                $_SESSION['user_data'] = [
                    'user_id'   => $user['user_id'],
                    'full_name' => $user['full_name'] ?? $user['username'],
                    'email'     => $user['email_address'],
                    'phone'     => $user['phone_number'] ?? null
                ];

                // Redirect user based on their role
                $user_type = strtolower($user['user_type']);
                if ($user_type === 'admin') {
                    header("Location: admin_dashboard.php");
                } elseif ($user_type === 'business') {
                    header("Location: seller/seller_dashboard.php");
                } else {
                    header("Location: marketplace_dashboard.php");
                }
                exit();
            } else {
                $error = "Invalid password!";
            }
        } else {
            $error = "Email address not found!";
        }
    } elseif (isset($_POST['action']) && $_POST['action'] == 'Sign Up') {
        header("Location: role_selection.php");
        exit();
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In / Sign Up</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <style>
        * { font-family: 'Inter', sans-serif; }
        body { background-color: #f4f7f6; }
        .card { border-radius: 12px; box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1); overflow: hidden; }
        .form-section { padding: 40px 30px; }
        .form-section h2 { color: #027361; }
        .form-control:focus { border-color: #027361; box-shadow: 0 0 8px rgba(2, 115, 97, 0.3); }
        .btn-primary { background-color: #027361; border: none; }
        .btn-primary:hover { background-color: #015446; }
        .btn-secondary-custom { background: transparent; border: 2px solid #fff; color: #fff; font-weight: 600; padding: 10px 20px; }
        .btn-secondary-custom:hover { background-color: rgba(255, 255, 255, 0.2); }
        .overlay { background: linear-gradient(135deg, #015446, #003329); color: #fff; }
        .error { color: #e74c3c; background-color: #fdd; border: 1px solid #f99; border-radius: 5px; padding: 10px; margin-bottom: 15px; text-align: center; }
        @media (max-width: 768px) { .form-section, .overlay { padding: 20px; } }
    </style>
</head>
<body>
    <div class="container-fluid d-flex align-items-center justify-content-center min-vh-100">
        <div class="row w-100 mx-2 mx-md-auto" style="max-width: 900px;">
            <div class="card p-0 d-flex flex-column flex-md-row w-100">
                <div class="col-12 col-md-6 form-section bg-white">
                    <h2 class="mb-4">Sign In</h2>
                    <?php if (isset($error)) echo "<div class='error'>$error</div>"; ?>
                    <form method="POST">
                        <div class="mb-3">
                            <input type="email" name="email" class="form-control" placeholder="Email" required>
                        </div>
                        <div class="mb-3">
                            <input type="password" name="password" class="form-control" placeholder="Password" required>
                        </div>
                        <button type="submit" name="action" value="Sign In" class="btn btn-primary w-100">Sign In</button>
                    </form>
                </div>
                <div class="col-12 col-md-6 overlay text-center d-flex flex-column justify-content-center p-4">
                    <h2>Create Account</h2>
                    <p>Join our community and unlock a world of possibilities!</p>
                    <form method="POST">
                        <button type="submit" name="action" value="Sign Up" class="btn btn-secondary-custom">Sign Up</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>