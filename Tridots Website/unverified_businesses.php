<?php
session_start();
if (empty($_SESSION['user_id']) || strtolower($_SESSION['user_type']) !== 'admin') {
    header("Location: login.php");
    exit();
}
$pdo = new PDO("mysql:host=localhost:3306;dbname=tridots", "tridots", "tridots12369");
$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['action'], $_POST['profile_id'])) {
    $id = (int)$_POST['profile_id'];
    $status = $_POST['action'] === 'approve' ? 'Verified' : 'Rejected';
    $stmt = $pdo->prepare("UPDATE BusinessProfiles SET verification_status = ? WHERE business_profile_id = ?");
    $stmt->execute([$status, $id]);
    header("Location: unverified_businesses.php");
    exit();
}

$profiles = $pdo->query("SELECT business_profile_id, business_name, email_address, contact_number, address FROM BusinessProfiles WHERE verification_status = 'Not Verified'")->fetchAll(PDO::FETCH_ASSOC);
?>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Unverified Businesses | TR:Dots</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>body{font-family:'Inter',sans-serif;background:#f5f7fa}.navbar{background:#00715A}</style>
</head>
<body>
<nav class="navbar navbar-dark px-4">
    <a class="navbar-brand" href="admin_dashboard.php">← Back to Dashboard</a>
</nav>

<div class="container py-4">
    <h3>Unverified Business Profiles</h3>
    <div class="table-responsive mt-3">
        <table class="table table-bordered table-hover bg-white">
            <thead class="table-light">
                <tr><th>ID</th><th>Business</th><th>Email</th><th>Contact</th><th>Address</th><th>Actions</th></tr>
            </thead>
            <tbody>
            <?php foreach ($profiles as $bp): ?>
                <tr>
                    <td><?= $bp['business_profile_id']; ?></td>
                    <td><?= htmlspecialchars($bp['business_name']); ?></td>
                    <td><?= htmlspecialchars($bp['email_address']); ?></td>
                    <td><?= htmlspecialchars($bp['contact_number']); ?></td>
                    <td><?= htmlspecialchars($bp['address']); ?></td>
                    <td>
                        <form method="POST" class="d-inline">
                            <input type="hidden" name="profile_id" value="<?= $bp['business_profile_id']; ?>">
                            <button name="action" value="approve" class="btn btn-success btn-sm">Approve</button>
                            <button name="action" value="reject" class="btn btn-danger btn-sm">Reject</button>
                        </form>
                    </td>
                </tr>
            <?php endforeach; if (count($profiles) === 0): ?>
                <tr><td colspan="6" class="text-muted text-center">No unverified business profiles.</td></tr>
            <?php endif; ?>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
