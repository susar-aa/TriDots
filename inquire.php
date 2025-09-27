<?php
// inquire.php - Send an inquiry from buyer to seller for a specific ad
session_start();
require_once 'db.php';
if (!isset($_SESSION['user_id'])) {
    header('Location: login.php');
    exit;
}
$user_id = $_SESSION['user_id'];
$seller_id = isset($_GET['seller']) ? intval($_GET['seller']) : 0;
$ad_id = isset($_GET['ad']) ? intval($_GET['ad']) : 0;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $message = trim($_POST['inquiry_message'] ?? '');
    if ($message && $seller_id && $ad_id) {
        $stmt = $pdo->prepare("INSERT INTO inquiries (user_id, ad_owner_id, ad_id, inquiry_message, inquiry_date, status) VALUES (?, ?, ?, ?, NOW(), 'Pending')");
        $stmt->execute([$user_id, $seller_id, $ad_id, $message]);
        // Optionally, send notification to seller
        header('Location: chat.php?seller=' . $seller_id . '&ad=' . $ad_id);
        exit;
    }
    $error = "Please enter your message.";
}
?>
<!DOCTYPE html>
<html>
<head>
    <title>Send Inquiry</title>
</head>
<body>
<h2>Send Inquiry</h2>
<?php if (!empty($error)) echo "<p style='color:red;'>".htmlspecialchars($error)."</p>"; ?>
<form method="post">
    <textarea name="inquiry_message" rows="6" cols="50" placeholder="Your inquiry message..." required></textarea><br>
    <button type="submit">Send Inquiry</button>
</form>
</body>
</html>