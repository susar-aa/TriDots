<?php
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
$today = date('Y-m-d');
$stmt = $pdo->prepare("SELECT * FROM Users WHERE DATE(created_at) = :today ORDER BY created_at DESC");
$stmt->execute(['today' => $today]);
$users = $stmt->fetchAll(PDO::FETCH_ASSOC);
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><title>New Users Today</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <h2>New Users Today</h2>
    <table class="table table-bordered table-hover">
        <thead>
            <tr>
                <th>ID</th><th>Username</th><th>Email</th><th>User Type</th><th>Created At</th>
            </tr>
        </thead>
        <tbody>
        <?php foreach ($users as $user): ?>
            <tr>
                <td><?= htmlspecialchars($user['id']) ?></td>
                <td><?= htmlspecialchars($user['username']) ?></td>
                <td><?= htmlspecialchars($user['email']) ?></td>
                <td><?= htmlspecialchars($user['user_type']) ?></td>
                <td><?= htmlspecialchars($user['created_at']) ?></td>
            </tr>
        <?php endforeach ?>
        </tbody>
    </table>
    <a href="admin_dashboard.php" class="btn btn-secondary">Back to Dashboard</a>
</div>
</body>
</html>