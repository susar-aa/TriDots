<?php
session_start();
if (empty($_SESSION['user_id']) || strtolower($_SESSION['user_type']) !== 'admin') {
    header("Location: login.php");
    exit();
}

$pdo = new PDO("mysql:host=localhost:3306;dbname=tridots", "tridots", "tridots12369");
$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

// Handle Approve/Reject
if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['action'], $_POST['user_id'])) {
    $userId = (int)$_POST['user_id'];
    $newStatus = $_POST['action'] === 'approve' ? 'Verified' : 'Rejected';
    $stmt = $pdo->prepare("UPDATE Users SET verification_status = ? WHERE user_id = ?");
    $stmt->execute([$newStatus, $userId]);
    header("Location: unverified_users.php");
    exit();
}

// Get unverified users
$users = $pdo->query("SELECT user_id, username, email_address, contact_number, nic_number FROM Users WHERE verification_status = 'Not Verified'")->fetchAll(PDO::FETCH_ASSOC);
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Unverified Users | TR:Dots</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f5f7fa;
        }
        .navbar {
            background-color: #00715A;
        }
        .badge-status {
            font-size: 0.9rem;
            padding: 0.4em 0.7em;
        }
    </style>
</head>
<body>

<nav class="navbar navbar-expand-lg navbar-dark px-4">
    <a class="navbar-brand" href="admin_dashboard.php">← Back to Dashboard</a>
</nav>

<div class="container py-4">
    <h3 class="mb-4">Unverified Users</h3>

    <div class="table-responsive">
        <table class="table table-bordered table-hover align-middle bg-white shadow-sm">
            <thead class="table-light">
                <tr>
                    <th>User ID</th>
                    <th>Username</th>
                    <th>Email</th>
                    <th>Contact</th>
                    <th>NIC</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
            <?php if (count($users) > 0): ?>
                <?php foreach ($users as $user): ?>
                    <tr>
                        <td><?= $user['user_id']; ?></td>
                        <td><?= htmlspecialchars($user['username']); ?></td>
                        <td><?= htmlspecialchars($user['email_address']); ?></td>
                        <td><?= htmlspecialchars($user['contact_number']); ?></td>
                        <td><?= htmlspecialchars($user['nic_number']); ?></td>
                        <td>
                            <form method="POST" class="d-inline">
                                <input type="hidden" name="user_id" value="<?= $user['user_id']; ?>">
                                <button name="action" value="approve" class="btn btn-sm btn-success">Approve</button>
                                <button name="action" value="reject" class="btn btn-sm btn-danger">Reject</button>
                            </form>
                        </td>
                    </tr>
                <?php endforeach; ?>
            <?php else: ?>
                <tr>
                    <td colspan="6" class="text-muted text-center">No unverified users found.</td>
                </tr>
            <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>
