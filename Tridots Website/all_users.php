<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

session_start();
if (empty($_SESSION['user_id']) || strtolower($_SESSION['user_type']) !== 'admin') {
    header("Location: login.php");
    exit();
}
require_once __DIR__ . '/db_config.php';

$dsn = isset($port)
    ? "mysql:host=$host;port=$port;dbname=$dbname"
    : "mysql:host=$host;dbname=$dbname";
try {
    $pdo = new PDO($dsn, $username_db, $password_db);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die("Database connection failed: " . $e->getMessage());
}

// Handle role/status/verification changes
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['user_id'])) {
    $userId = (int)$_POST['user_id'];
    if (isset($_POST['change_role']) && isset($_POST['new_role'])) {
        $newRole = $_POST['new_role'];
        $stmt = $pdo->prepare("UPDATE Users SET user_type = ? WHERE user_id = ?");
        $stmt->execute([$newRole, $userId]);
    }
    if (isset($_POST['toggle_active']) && isset($_POST['current_active'])) {
        $current = $_POST['current_active'];
        $newActive = ($current === 'Active') ? 'Deactive' : 'Active';
        $stmt = $pdo->prepare("UPDATE Users SET is_active = ? WHERE user_id = ?");
        $stmt->execute([$newActive, $userId]);
    }
    if (isset($_POST['toggle_verified']) && isset($_POST['current_verified'])) {
        $current = $_POST['current_verified'];
        $newVerified = ($current === 'Verified') ? 'Not Verified' : 'Verified';
        $stmt = $pdo->prepare("UPDATE Users SET verification_status = ? WHERE user_id = ?");
        $stmt->execute([$newVerified, $userId]);
    }
    // Prevent form resubmission
    header("Location: all_users.php");
    exit();
}

$stmt = $pdo->query("SELECT * FROM Users ORDER BY user_id DESC");
$users = $stmt->fetchAll(PDO::FETCH_ASSOC);
$username = htmlspecialchars($_SESSION['username']);
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><title>All Users | Admin</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    <style>
        body { font-family: 'Inter', sans-serif; background: #f5f7fa; }
        .sidebar {
            min-height: 100vh;
            background: #00715A;
            color: #fff;
            padding: 2rem 1rem 2rem 1rem;
            position: sticky;
            top: 0;
        }
        .sidebar .nav-link {
            color: #fff;
            font-weight: 500;
            border-radius: 8px;
            margin-bottom: 4px;
            padding: 0.8rem 1rem;
        }
        .sidebar .nav-link.active, .sidebar .nav-link:hover {
            background: rgba(255,255,255,0.13);
            color: #fff;
        }
        .main-content { padding: 2rem 1.2rem 1.2rem 1.2rem; }
        .dashboard-title { font-size: 2rem; font-weight: 700; color: #22223b; margin-bottom: 1.2rem; }
        .profile-info { font-size: 1rem; font-weight: 500; margin-top: 2rem; }
        .sidebar .logout-btn { margin-top: 1.5rem; }
        .table-card {
            background: #fff;
            border-radius: 18px;
            box-shadow: 0 2px 12px rgba(34,34,59,0.08);
            padding: 2rem;
            margin-bottom: 2rem;
        }
        .form-select, .btn { min-width: 95px; }
        @media (max-width: 991.98px) {
            .sidebar { min-height: auto; position: static; padding-top: 1rem; }
            .main-content { padding: 1.5rem 0.7rem; }
        }
        @media (max-width: 767.98px) {
            .table-responsive { font-size: 0.93rem; }
            .dashboard-title { font-size: 1.35rem; }
            .table-card { padding: 0.7rem; }
        }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="row flex-nowrap">
        <!-- Sidebar -->
        <nav class="col-auto col-md-3 col-lg-2 px-3 sidebar d-flex flex-column">
            <span class="fs-4 fw-bold mb-4">TR:Dots Admin</span>
            <ul class="nav nav-pills flex-column mb-auto">
                <li class="nav-item">
                    <a href="admin_dashboard.php" class="nav-link"><i class="bi bi-speedometer2 me-2"></i>Dashboard</a>
                </li>
                <li>
                    <a href="all_users.php" class="nav-link active"><i class="bi bi-people-fill me-2"></i>All Users</a>
                </li>
                <li>
                    <a href="all_businesses.php" class="nav-link"><i class="bi bi-building me-2"></i>All Businesses</a>
                </li>
                <li>
                    <a href="live_ads.php" class="nav-link"><i class="bi bi-bullseye me-2"></i>Live Ads</a>
                </li>
                <li>
                    <a href="live_vehicles.php" class="nav-link"><i class="bi bi-truck me-2"></i>Live Vehicles</a>
                </li>
                <li>
                    <a href="all_service_providers.php" class="nav-link"><i class="bi bi-briefcase me-2"></i>Service Providers</a>
                </li>
                <li>
                    <a href="all_inquiries.php" class="nav-link"><i class="bi bi-envelope me-2"></i>Inquiries</a>
                </li>
            </ul>
            <div class="profile-info mt-auto d-flex align-items-center">
                <i class="bi bi-person-circle fs-4 me-2"></i>
                <span><?= $username ?></span>
            </div>
            <a href="logout.php" class="btn btn-sm btn-outline-light mt-3 logout-btn"><i class="bi bi-box-arrow-right me-1"></i>Logout</a>
        </nav>
        <!-- Main Content -->
        <main class="col main-content">
            <div class="dashboard-title mb-3"><i class="bi bi-people-fill"></i> All Users</div>
            <div class="table-card">
            <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>User Type</th>
                        <th>Business Name</th>
                        <th>Verification</th>
                        <th>Active</th>
                    </tr>
                </thead>
                <tbody>
                <?php foreach ($users as $user): ?>
                    <tr>
                        <td><?= htmlspecialchars($user['user_id'] ?? '') ?></td>
                        <td><?= htmlspecialchars($user['username'] ?? '') ?></td>
                        <td><?= htmlspecialchars($user['email_address'] ?? '') ?></td>
                        <td>
                            <form method="post" class="d-inline">
                                <input type="hidden" name="user_id" value="<?= $user['user_id'] ?>">
                                <select name="new_role" onchange="this.form.submit()" class="form-select form-select-sm">
                                    <?php foreach (['Personal', 'Business', 'Admin', 'Rider'] as $role): ?>
                                        <option value="<?= $role ?>" <?= $user['user_type'] === $role ? 'selected' : '' ?>><?= $role ?></option>
                                    <?php endforeach; ?>
                                </select>
                                <input type="hidden" name="change_role" value="1">
                            </form>
                        </td>
                        <td><?= htmlspecialchars($user['business_name'] ?? '-') ?></td>
                        <td>
                            <form method="post" class="d-inline">
                                <input type="hidden" name="user_id" value="<?= $user['user_id'] ?>">
                                <input type="hidden" name="current_verified" value="<?= $user['verification_status'] ?>">
                                <button type="submit" name="toggle_verified" value="1" class="btn btn-sm <?= $user['verification_status'] === 'Verified' ? 'btn-primary' : 'btn-outline-primary' ?>">
                                    <?= $user['verification_status'] === 'Verified' ? 'Verified' : 'Not Verified' ?>
                                </button>
                            </form>
                        </td>
                        <td>
                            <form method="post" class="d-inline">
                                <input type="hidden" name="user_id" value="<?= $user['user_id'] ?>">
                                <input type="hidden" name="current_active" value="<?= $user['is_active'] ?>">
                                <button type="submit" name="toggle_active" value="1" class="btn btn-sm <?= $user['is_active'] === 'Active' ? 'btn-success' : 'btn-secondary' ?>">
                                    <?= $user['is_active'] === 'Active' ? 'Active' : 'Deactive' ?>
                                </button>
                            </form>
                        </td>
                    </tr>
                <?php endforeach ?>
                <?php if (empty($users)): ?>
                    <tr><td colspan="7" class="text-center">No users found.</td></tr>
                <?php endif; ?>
                </tbody>
            </table>
            </div>
            </div>
            <a href="admin_dashboard.php" class="btn btn-outline-secondary mt-2"><i class="bi bi-arrow-left"></i> Back to Dashboard</a>
        </main>
    </div>
</div>
</body>
</html>