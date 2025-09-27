<?php
session_start();

// Check if the user is logged in
if (!isset($_SESSION['user_id'])) {
    header('Location: login.php');
    exit;
}

// Database connection
require_once 'db.php';

// Fetch user details
$userData = [];
try {
    $stmt = $pdo->prepare("SELECT user_id, username, email_address, contact_number, user_type, profile_picture FROM Users WHERE user_id = ?");
    $stmt->execute([$_SESSION['user_id']]);
    $userData = $stmt->fetch(PDO::FETCH_ASSOC);

    if (!$userData) {
        error_log("User not found in DB for session user_id: " . $_SESSION['user_id']);
        session_destroy();
        header('Location: login.php?error=user_not_found');
        exit;
    }
} catch (PDOException $e) {
    error_log("Database Error: " . $e->getMessage());
    echo "An error occurred while fetching user data. Please try again later.";
    exit;
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Dashboard - <?php echo htmlspecialchars($userData['username']); ?></title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        :root {
            --primary-color: #00715A;
            --accent-color: #00B07D;
            --background-color: #f7fafc;
            --card-background-color: #ffffff;
            --text-color: #2d3748;
            --text-color-light: #5a677b;
            --border-color: #e2e8f0;
            --shadow-sm: 0 2px 6px 0 rgba(0,113,90,0.06);
            --shadow-md: 0 5px 15px -3px rgba(0,113,90,0.08), 0 3px 8px -3px rgba(0,113,90,0.05);
            --radius-md: 0.75rem;
            --radius-lg: 1rem;
        }

        * { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Inter', Arial, sans-serif;
            background-color: var(--background-color);
            color: var(--text-color);
            line-height: 1.6;
        }

        .dashboard-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }

        .dashboard-header {
            background: linear-gradient(102deg, var(--accent-color) 36%, var(--primary-color) 100%);
            color: #fff;
            padding: 30px 20px;
            border-radius: var(--radius-lg);
            margin-bottom: 30px;
            box-shadow: var(--shadow-md);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .dashboard-header h1 {
            font-size: 2.2rem;
            font-weight: 700;
        }
        .dashboard-header a.logout-btn {
            color: #fff;
            background-color: rgba(255,255,255,0.2);
            padding: 10px 18px;
            border-radius: var(--radius-md);
            text-decoration: none;
            font-weight: 600;
            transition: background-color 0.2s ease;
        }
        .dashboard-header a.logout-btn:hover { background-color: rgba(255,255,255,0.3); }

        .profile-section {
            display: flex;
            gap: 30px;
            margin-bottom: 30px;
            align-items: flex-start;
        }

        .profile-card {
            background: var(--card-background-color);
            padding: 25px;
            border-radius: var(--radius-lg);
            box-shadow: var(--shadow-md);
            flex: 1;
        }
        
        .profile-card .profile-header {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 20px;
            border-bottom: 1px solid var(--border-color);
        }

        .profile-card .profile-avatar img {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            object-fit: cover;
            margin-right: 20px;
            border: 3px solid var(--primary-color);
        }
        .profile-card .profile-avatar .placeholder-avatar {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            background-color: var(--primary-color);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 2.5rem;
            font-weight: 600;
            margin-right: 20px;
        }

        .profile-card .profile-info h2 {
            font-size: 1.6rem;
            color: var(--primary-color);
            margin:0;
            font-weight: 700;
        }
        .profile-card .profile-info p.user-type {
            font-size: 0.95rem;
            color: var(--text-color-light);
            font-weight: 500;
            text-transform: capitalize;
        }

        .profile-details p {
            margin-bottom: 12px;
            font-size: 1rem;
            color: var(--text-color-light);
        }

        .profile-details p strong {
            color: var(--text-color);
            min-width: 100px;
            display: inline-block;
            font-weight: 600;
        }
        .profile-details p i {
            margin-right: 10px;
            color: var(--primary-color);
            width: 16px;
        }

        .dashboard-actions {
            background: var(--card-background-color);
            padding: 25px 25px 15px 25px;
            border-radius: var(--radius-lg);
            box-shadow: var(--shadow-md);
            width: 320px;
            display: flex;
            flex-direction: column;
            align-items: stretch;
        }
        .dashboard-actions h3 {
            font-size: 1.3rem;
            color: var(--primary-color);
            margin-bottom: 18px;
            font-weight: 600;
            text-align: left;
        }
        .dashboard-action-btn {
            display: flex;
            align-items: center;
            gap: 10px;
            width: 100%;
            background: linear-gradient(90deg, var(--primary-color) 60%, var(--accent-color) 100%);
            color: #fff;
            padding: 13px 18px;
            border: none;
            border-radius: var(--radius-md);
            font-size: 1rem;
            font-weight: 600;
            text-align: left;
            text-decoration: none;
            margin-bottom: 12px;
            transition: transform 0.08s, box-shadow 0.18s;
            box-shadow: var(--shadow-sm);
            cursor: pointer;
        }
        .dashboard-action-btn:hover {
            transform: translateY(-2px) scale(1.02);
            box-shadow: var(--shadow-md);
        }
        .dashboard-action-btn.secondary {
            background: linear-gradient(90deg, var(--accent-color) 60%, var(--primary-color) 100%);
            color: #fff;
            border: 2px solid var(--primary-color);
        }
        .dashboard-action-btn.secondary:hover {
            background: var(--primary-color);
            color: #fff;
        }
        .dashboard-action-btn i { font-size: 1.15em; }

        .dashboard-footer {
            text-align: center;
            padding: 20px;
            background: var(--primary-color);
            color: #fff;
            margin-top: 30px;
            border-radius: var(--radius-lg) var(--radius-lg) 0 0;
            font-size: 0.9rem;
        }

        @media (max-width: 992px) {
            .profile-section { flex-direction: column; }
            .dashboard-actions {
                width: 100%;
                margin-top: 20px;
            }
        }
        @media (max-width: 768px) {
            .dashboard-header {
                flex-direction: column;
                gap: 15px;
                text-align:center;
            }
            .dashboard-header h1 { font-size: 1.8rem; }
            .profile-card .profile-header {
                flex-direction: column;
                text-align: center;
            }
            .profile-card .profile-avatar img,
            .profile-card .profile-avatar .placeholder-avatar {
                margin-right: 0;
                margin-bottom: 15px;
            }
        }
    </style>
</head>
<body>
    <div class="dashboard-container">
        <div class="dashboard-header">
            <h1>Welcome, <?php echo htmlspecialchars($userData['username']); ?>!</h1>
            <a href="logout.php" class="logout-btn"><i class="fas fa-sign-out-alt"></i> Logout</a>
        </div>

        <div class="profile-section">
            <div class="profile-card">
                <div class="profile-header">
                    <div class="profile-avatar">
                        <?php 
                        $profilePic = $userData['profile_picture'] ?? null;
                        if ($profilePic && file_exists($_SERVER['DOCUMENT_ROOT'] . $profilePic)):
                        ?>
                            <img src="<?php echo htmlspecialchars($profilePic); ?>" alt="Profile Picture">
                        <?php else: 
                            $initials = strtoupper(substr($userData['username'], 0, 1));
                        ?>
                            <div class="placeholder-avatar"><?php echo htmlspecialchars($initials); ?></div>
                        <?php endif; ?>
                    </div>
                    <div class="profile-info">
                        <h2><?php echo htmlspecialchars($userData['username']); ?></h2>
                        <p class="user-type"><?php echo htmlspecialchars($userData['user_type']); ?> Account</p>
                    </div>
                </div>
                <div class="profile-details">
                    <p><i class="fas fa-envelope"></i><strong>Email:</strong> <?php echo htmlspecialchars($userData['email_address']); ?></p>
                    <p><i class="fas fa-phone"></i><strong>Contact:</strong> <?php echo htmlspecialchars($userData['contact_number'] ?? 'N/A'); ?></p>
                </div>
            </div>

            <div class="dashboard-actions">
                <h3>Quick Actions</h3>
                <a href="edit_profile.php" class="dashboard-action-btn"><i class="fas fa-user-edit"></i> Edit Profile</a>
                <a href="change_password.php" class="dashboard-action-btn"><i class="fas fa-key"></i> Change Password</a>
                <a href="my_bookings.php" class="dashboard-action-btn"><i class="fas fa-calendar-check"></i> My Bookings/Rentals</a>
                <?php if ($userData['user_type'] === 'business'): ?>
                    <a href="/seller/seller_dashboard.php" class="dashboard-action-btn secondary"><i class="fas fa-store"></i> Go to Business Dashboard</a>
                <?php elseif ($userData['user_type'] === 'admin'): ?>
                    <a href="admin_dashboard.php" class="dashboard-action-btn secondary"><i class="fas fa-user-shield"></i> Go to Admin Dashboard</a>
                <?php endif; ?>
            </div>
        </div>
    </div>

    <div class="dashboard-footer">
        &copy; <?php echo date("Y"); ?> Service & Rental Platform. All rights reserved.
    </div>
</body>
</html>