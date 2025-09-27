<?php
session_start();
if (empty($_SESSION['user_id']) || strtolower($_SESSION['user_type']) !== 'admin') {
    header("Location: login.php");
    exit();
}
$pdo = new PDO("mysql:host=localhost:3306;dbname=tridots", "tridots", "tridots12369");
$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['seller_id'], $_POST['action'])) {
    $status = $_POST['action'] === 'approve' ? 'Approved' : 'Rejected';
    $stmt = $pdo->prepare("UPDATE Service_Providers SET approval_status = ? WHERE seller_id = ?");
    $stmt->execute([$status, (int)$_POST['seller_id']]);
    header("Location: pending_providers.php");
    exit();
}

$list = $pdo->query("SELECT seller_id, name, email_address, description, created_at FROM Service_Providers WHERE approval_status = 'Under Review'")->fetchAll(PDO::FETCH_ASSOC);
?>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Pending Providers | TR:Dots</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>body{font-family:'Inter',sans-serif;background:#f5f7fa}.navbar{background:#00715A}</style>
</head>
<body>
<nav class="navbar navbar-dark px-4">
    <a class="navbar-brand" href="admin_dashboard.php">← Back to Dashboard</a>
</nav>

<div class="container py-4">
    <h3>Pending Service Providers</h3>
    <div class="table-responsive mt-3">
        <table class="table table-bordered bg-white">
            <thead class="table-light">
                <tr><th>ID</th><th>Name</th><th>Email</th><th>Description</th><th>Created</th><th>Actions</th></tr>
            </thead>
            <tbody>
            <?php foreach ($list as $row): ?>
                <tr>
                    <td><?= $row['seller_id']; ?></td>
                    <td><?= htmlspecialchars($row['name']); ?></td>
                    <td><?= htmlspecialchars($row['email_address']); ?></td>
                    <td><?= htmlspecialchars($row['description']); ?></td>
                    <td><?= $row['created_at']; ?></td>
                    <td>
                        <form method="POST" class="d-inline">
                            <input type="hidden" name="seller_id" value="<?= $row['seller_id']; ?>">
                            <button name="action" value="approve" class="btn btn-success btn-sm">Approve</button>
                            <button name="action" value="reject" class="btn btn-danger btn-sm">Reject</button>
                        </form>
                    </td>
                </tr>
            <?php endforeach; if (count($list) === 0): ?>
                <tr><td colspan="6" class="text-muted text-center">No pending providers.</td></tr>
            <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
