<?php
session_start();
if (empty($_SESSION['user_id']) || strtolower($_SESSION['user_type']) !== 'admin') {
    header("Location: login.php");
    exit();
}
$pdo = new PDO("mysql:host=localhost:3306;dbname=tridots", "tridots", "tridots12369");
$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

$inquiries = $pdo->query("SELECT inquiry_id, inquirer_name, inquirer_email, ad_type, inquiry_date, status FROM inquiries WHERE status = 'Pending'")->fetchAll(PDO::FETCH_ASSOC);
?>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Pending Inquiries | TR:Dots</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>body{font-family:'Inter',sans-serif;background:#f5f7fa}.navbar{background:#00715A}</style>
</head>
<body>
<nav class="navbar navbar-dark px-4">
    <a class="navbar-brand" href="admin_dashboard.php">← Back to Dashboard</a>
</nav>

<div class="container py-4">
    <h3>Pending Inquiries</h3>
    <div class="table-responsive mt-3">
        <table class="table table-bordered bg-white">
            <thead class="table-light">
                <tr><th>ID</th><th>Name</th><th>Email</th><th>Ad Type</th><th>Date</th><th>Status</th></tr>
            </thead>
            <tbody>
            <?php foreach ($inquiries as $inq): ?>
                <tr>
                    <td><?= $inq['inquiry_id']; ?></td>
                    <td><?= htmlspecialchars($inq['inquirer_name']); ?></td>
                    <td><?= htmlspecialchars($inq['inquirer_email']); ?></td>
                    <td><?= $inq['ad_type']; ?></td>
                    <td><?= $inq['inquiry_date']; ?></td>
                    <td><span class="badge bg-warning"><?= $inq['status']; ?></span></td>
                </tr>
            <?php endforeach; if (count($inquiries) === 0): ?>
                <tr><td colspan="6" class="text-muted text-center">No pending inquiries.</td></tr>
            <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
