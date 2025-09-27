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

// Use port if defined
$dsn = isset($port)
    ? "mysql:host=$host;port=$port;dbname=$dbname"
    : "mysql:host=$host;dbname=$dbname";

try {
    $pdo = new PDO($dsn, $username_db, $password_db);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die("Database connection failed: " . $e->getMessage());
}

function getCount($pdo, $table, $where = '1=1') {
    $allowedTables = ['Users', 'BusinessProfiles', 'renting', 'Service_Providers', 'Vehicles', 'inquiries'];
    if (!in_array($table, $allowedTables)) return 0;
    $query = "SELECT COUNT(*) FROM $table WHERE $where";
    try {
        return (int)$pdo->query($query)->fetchColumn();
    } catch (Exception $e) { return 0; }
}

$today = date('Y-m-d');
$weekAgo = date('Y-m-d', strtotime('-7 days'));

$totalUsers = getCount($pdo, 'Users');
$newUsersThisWeek = getCount($pdo, 'Users', "DATE(created_at) >= '$weekAgo'");

$totalBusinesses = getCount($pdo, 'BusinessProfiles');
$newBusinessesThisWeek = getCount($pdo, 'BusinessProfiles', "DATE(created_at) >= '$weekAgo'");

// Approved/Live Ads
$totalLiveAds = getCount($pdo, 'renting', "approval_status = 'Approved'");
$totalAds = getCount($pdo, 'renting');
$newAdsThisWeek = getCount($pdo, 'renting', "DATE(created_at) >= '$weekAgo'");

$totalServiceProviders = getCount($pdo, 'Service_Providers');

// Approved/Live Vehicles
$totalVehicles = getCount($pdo, 'Vehicles', "approval_status = 'Approved'");
$totalInquiries = getCount($pdo, 'inquiries');

// Blocked counts
$totalBlockedRenting = getCount($pdo, 'renting', "approval_status = 'Blocked'");
$totalBlockedVehicles = getCount($pdo, 'Vehicles', "approval_status = 'Blocked'");
$totalBlockedServices = getCount($pdo, 'Service_Providers', "approval_status = 'Blocked'");
$totalBlockedAds = $totalBlockedRenting + $totalBlockedVehicles + $totalBlockedServices;

// Rider count
$totalRiders = getCount($pdo, 'Users', "LOWER(user_type) = 'rider'");

$username = htmlspecialchars($_SESSION['username']);
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Analytics Dashboard | TR:Dots</title>
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
            padding-top: 2rem;
            padding-bottom: 2rem;
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
        .profile-info {
            font-size: 1rem;
            font-weight: 500;
            margin-top: 2rem;
        }
        .sidebar .logout-btn {
            margin-top: 1.5rem;
        }
        .main-content {
            padding: 2rem 1.2rem 1.2rem 1.2rem;
        }
        .dashboard-title { font-size: 2rem; font-weight: 700; color: #22223b; margin-bottom: 1.2rem; }
        .dashboard-tiles { margin-top: 1.5rem; }
        .dashboard-card {
            border: none;
            border-radius: 16px;
            box-shadow: 0 2px 12px rgba(34,34,59,0.08);
            padding: 1.1rem 0.9rem;
            margin-bottom: 1rem;
            text-align: left;
            min-width: 150px; max-width: 100%;
            transition: transform 0.08s;
            background: #fff;
            display: flex;
            align-items: flex-start;
            gap: 0.7rem;
        }
        .dashboard-card:hover { transform: scale(1.025); }
        .dashboard-card .main-count { font-size: 1.38rem; font-weight: 700; }
        .dashboard-card .card-label { font-size: 0.97rem; font-weight: 500; color: #444; }
        .dashboard-card .mini { font-size: 0.81rem; color: #666; }
        .dashboard-card i {
            font-size: 1.45rem;
            margin-right: 0.3rem;
            color: #00715A;
            margin-top: 0.15rem;
        }
        @media (max-width: 991.98px) {
            .sidebar {
                min-height: auto;
                position: static;
                padding-top: 1rem;
            }
            .main-content {
                padding: 1.5rem 0.7rem;
            }
        }
        @media (max-width: 767.98px) {
            .dashboard-tiles .col { width: 100%; }
        }
        @media (min-width: 768px) and (max-width: 991.98px) {
            .dashboard-tiles .col { width: 50%; }
        }
        @media (min-width: 992px) {
            .dashboard-tiles .col { width: 25%; }
        }
        .dashboard-card-link {
            text-decoration: none;
            color: inherit;
        }
        .dashboard-card-link:focus, .dashboard-card-link:hover {
            color: inherit;
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
                    <a href="admin_dashboard.php" class="nav-link active" aria-current="page"><i class="bi bi-speedometer2 me-2"></i>Dashboard</a>
                </li>
                <li>
                    <a href="unverified_users.php" class="nav-link"><i class="bi bi-person-x-fill me-2"></i>Unverified Users</a>
                </li>
                <li>
                    <a href="unverified_businesses.php" class="nav-link"><i class="bi bi-building-x me-2"></i>Unverified Businesses</a>
                </li>
                <li>
                    <a href="pending_rentals.php" class="nav-link"><i class="bi bi-house-door me-2"></i>Pending Renting Ads</a>
                </li>
                <li>
                    <a href="pending_providers.php" class="nav-link"><i class="bi bi-briefcase me-2"></i>Pending Service Providers</a>
                </li>
                <li>
                    <a href="pending_vehicles.php" class="nav-link"><i class="bi bi-truck me-2"></i>Pending Vehicles</a>
                </li>
                <li>
                    <a href="pending_inquiries.php" class="nav-link"><i class="bi bi-question-circle me-2"></i>Pending Inquiries</a>
                </li>
                <li>
                    <a href="blocked_ads.php" class="nav-link"><i class="bi bi-slash-circle me-2"></i>Blocked Ads</a>
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
            <div class="dashboard-title">Analytics Overview</div>
            <div class="row dashboard-tiles g-3">
                <div class="col">
                    <a href="all_users.php" class="dashboard-card-link">
                        <div class="dashboard-card">
                            <i class="bi bi-people-fill"></i>
                            <div>
                                <div class="main-count"><?= $totalUsers ?></div>
                                <div class="card-label">Total Users</div>
                                <div class="mini">+<?= $newUsersThisWeek ?> this week</div>
                            </div>
                        </div>
                    </a>
                </div>
                <div class="col">
                    <a href="all_businesses.php" class="dashboard-card-link">
                        <div class="dashboard-card">
                            <i class="bi bi-building"></i>
                            <div>
                                <div class="main-count"><?= $totalBusinesses ?></div>
                                <div class="card-label">Registered Businesses</div>
                                <div class="mini">+<?= $newBusinessesThisWeek ?> this week</div>
                            </div>
                        </div>
                    </a>
                </div>
                <div class="col">
                    <a href="live_ads.php" class="dashboard-card-link">
                        <div class="dashboard-card">
                            <i class="bi bi-bullseye"></i>
                            <div>
                                <div class="main-count"><?= $totalLiveAds ?></div>
                                <div class="card-label">Live Ads</div>
                                <div class="mini">Total: <?= $totalAds ?> | +<?= $newAdsThisWeek ?> this week</div>
                            </div>
                        </div>
                    </a>
                </div>
                <div class="col">
                    <a href="all_service_providers.php" class="dashboard-card-link">
                        <div class="dashboard-card">
                            <i class="bi bi-briefcase"></i>
                            <div>
                                <div class="main-count"><?= $totalServiceProviders ?></div>
                                <div class="card-label">Service Providers</div>
                            </div>
                        </div>
                    </a>
                </div>
                <div class="col">
                    <a href="live_vehicles.php" class="dashboard-card-link">
                        <div class="dashboard-card">
                            <i class="bi bi-truck"></i>
                            <div>
                                <div class="main-count"><?= $totalVehicles ?></div>
                                <div class="card-label">Live Vehicles</div>
                            </div>
                        </div>
                    </a>
                </div>
                <div class="col">
                    <a href="all_inquiries.php" class="dashboard-card-link">
                        <div class="dashboard-card">
                            <i class="bi bi-envelope"></i>
                            <div>
                                <div class="main-count"><?= $totalInquiries ?></div>
                                <div class="card-label">Total Inquiries</div>
                            </div>
                        </div>
                    </a>
                </div>
                <div class="col">
                    <a href="blocked_ads.php" class="dashboard-card-link">
                        <div class="dashboard-card">
                            <i class="bi bi-slash-circle"></i>
                            <div>
                                <div class="main-count"><?= $totalBlockedAds ?></div>
                                <div class="card-label">Blocked Ads</div>
                                <div class="mini">Renting: <?= $totalBlockedRenting ?> | Vehicles: <?= $totalBlockedVehicles ?> | Services: <?= $totalBlockedServices ?></div>
                            </div>
                        </div>
                    </a>
                </div>
                <div class="col">
                    <a href="all_riders.php" class="dashboard-card-link">
                        <div class="dashboard-card">
                            <i class="bi bi-person-badge"></i>
                            <div>
                                <div class="main-count"><?= $totalRiders ?></div>
                                <div class="card-label">Riders</div>
                            </div>
                        </div>
                    </a>
                </div>
            </div>
        </main>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>