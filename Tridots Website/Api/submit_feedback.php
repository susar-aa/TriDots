<?php
header('Content-Type: application/json');

$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION
    ]);
} catch (PDOException $e) {
    echo json_encode(['error' => 'Database connection failed']);
    exit;
}

$user_id = $_POST['user_id'] ?? null;
$feedback_type = $_POST['feedback_type'] ?? null;
$item_id = $_POST['item_id'] ?? null;
$rating = $_POST['rating'] ?? null;
$comment = $_POST['comment'] ?? null;

if (!$user_id || !$feedback_type || !$item_id || !$rating) {
    echo json_encode(['error' => 'Missing required parameters']);
    exit;
}

// Insert into feedbacks table
$stmt = $pdo->prepare("INSERT INTO feedbacks (user_id, feedback_type, item_id, rating, comment) VALUES (?, ?, ?, ?, ?)");
if (!$stmt->execute([$user_id, $feedback_type, $item_id, $rating, $comment])) {
    echo json_encode(['error' => 'Failed to insert feedback']);
    exit;
}

// Update relevant ad table's review_count and average_rating
switch ($feedback_type) {
    case 'renting':
        $table = 'renting';
        $id_field = 'rent_id';
        $rating_field = 'average_rating';
        $count_field = 'review_count';
        break;
    case 'vehicles':
        $table = 'Vehicles';
        $id_field = 'vehicle_id';
        $rating_field = null;
        $count_field = null;
        break;
    case 'service_provider':
        $table = 'Service_Providers';
        $id_field = 'seller_id';
        $rating_field = 'reviews_average';
        $count_field = 'review_count';
        break;
    default:
        echo json_encode(['error' => 'Invalid feedback_type']);
        exit;
}

// Calculate new average and count
try {
    $stmt = $pdo->prepare("SELECT AVG(rating) as avg_rating, COUNT(*) as count FROM feedbacks WHERE feedback_type=? AND item_id=?");
    $stmt->execute([$feedback_type, $item_id]);
    $row = $stmt->fetch(PDO::FETCH_ASSOC);
    $average = $row ? floatval($row['avg_rating']) : 0;
    $count = $row ? intval($row['count']) : 0;

    // Only update ad table if average/count fields exist
    if ($rating_field && $count_field) {
        $update = $pdo->prepare("UPDATE $table SET $rating_field=?, $count_field=? WHERE $id_field=?");
        $update->execute([$average, $count, $item_id]);
    }

    // Map feedback_type to inquiry ad_type for update
    $inquiry_ad_type = $feedback_type;
    if ($feedback_type === 'service_provider') {
        $inquiry_ad_type = 'service_providers';
    }

    // UPDATE: Set status='Closed' for the inquiry.
    $update_inquiry = $pdo->prepare("UPDATE inquiries SET status='Closed' WHERE user_id=? AND ad_type=? AND ad_id=?");
    $update_inquiry->execute([$user_id, $inquiry_ad_type, $item_id]);

    echo json_encode(['success' => true, 'average_rating' => $average, 'review_count' => $count]);
} catch (Exception $e) {
    echo json_encode(['error' => 'Failed to update ad statistics', 'details' => $e->getMessage()]);
}
?>