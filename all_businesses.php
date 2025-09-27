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
$stmt = $pdo->query("SELECT * FROM BusinessProfiles ORDER BY business_profile_id DESC");
$businesses = $stmt->fetchAll(PDO::FETCH_ASSOC);

$username = htmlspecialchars($_SESSION['username']);
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><title>All Businesses | Admin</title>
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
                    <a href="all_users.php" class="nav-link"><i class="bi bi-people-fill me-2"></i>All Users</a>
                </li>
                <li>
                    <a href="all_businesses.php" class="nav-link active"><i class="bi bi-building me-2"></i>All Businesses</a>
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
            <div class="dashboard-title mb-3"><i class="bi bi-building"></i> All Businesses</div>
            <div class="table-card">
            <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>ID</th>
                        <th>Business Name</th>
                        <th>Email</th>
                        <th>Contact No</th>
                        <th>Verification</th>
                        <th>Active</th>
                        <th>Bio</th>
                    </tr>
                </thead>
                <tbody>
                <?php foreach ($businesses as $business): ?>
                    <tr>
                        <td><?= htmlspecialchars($business['business_profile_id'] ?? '') ?></td>
                        <td><?= htmlspecialchars($business['business_name'] ?? '') ?></td>
                        <td><?= htmlspecialchars($business['email_address'] ?? '') ?></td>
                        <td><?= htmlspecialchars($business['contact_number'] ?? '') ?></td>
                        <td>
                            <span class="badge bg-<?= ($business['verification_status'] ?? '') === 'Verified' ? 'success' : 'secondary' ?>">
                                <?= htmlspecialchars($business['verification_status'] ?? '') ?>
                            </span>
                        </td>
                        <td>
                            <span class="badge bg-<?= ($business['is_active'] ?? '') === 'Active' ? 'success' : 'secondary' ?>">
                                <?= htmlspecialchars($business['is_active'] ?? '') ?>
                            </span>
                        </td>
                        <td><?= htmlspecialchars($business['bio_description'] ?? '-') ?></td>
                    </tr>
                <?php endforeach ?>
                <?php if (empty($businesses)): ?>
                    <tr><td colspan="7" class="text-center">No businesses found.</td></tr>
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