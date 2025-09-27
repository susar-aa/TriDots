<?php
// booking.php - Start a booking for a listing
session_start();
if (!isset($_SESSION['user_id'])) {
    header('Location: login.php');
    exit;
}
$user_id = $_SESSION['user_id'];
$ad_id = isset($_GET['ad']) ? intval($_GET['ad']) : 0;

// Fetch ad info if needed (optional, for display)
// require_once 'db.php';
// $stmt = $pdo->prepare("SELECT product_name FROM Marketplace WHERE product_id = ?");
// $stmt->execute([$ad_id]);
// $ad = $stmt->fetch(PDO::FETCH_ASSOC);

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    // Save booking (implement your own logic/tables as needed)
    // $stmt = $pdo->prepare("INSERT ...");
    // $stmt->execute([...]);
    echo "<p>Booking request sent! (Implement booking logic as needed.)</p>";
    exit;
}
?>
<!DOCTYPE html>
<html>
<head>
    <title>Book Now</title>
</head>
<body>
<h2>Book Listing</h2>
<form method="post">
    <label>Booking Details (dates, etc.):<br>
        <input type="text" name="details" required>
    </label><br>
    <button type="submit">Book Now</button>
</form>
</body>
</html>