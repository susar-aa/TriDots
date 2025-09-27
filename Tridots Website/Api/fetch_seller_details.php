<?php
header('Content-Type: application/json');

$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    if (isset($_GET['user_id']) && is_numeric($_GET['user_id'])) {
        $userId = $_GET['user_id'];

        // Check if a business profile exists for this user_id
        $stmtBusiness = $pdo->prepare("SELECT * FROM BusinessProfiles WHERE user_id = :user_id");
        $stmtBusiness->bindParam(':user_id', $userId, PDO::PARAM_INT);
        $stmtBusiness->execute();
        $businessProfile = $stmtBusiness->fetch(PDO::FETCH_ASSOC);

        if ($businessProfile) {
            // Return business profile details
            echo json_encode(['type' => 'business', 'details' => $businessProfile]);
        } else {
            // Fetch and return user details
            $stmtUser = $pdo->prepare("SELECT user_id, username, email_address, contact_number, profile_picture, address, verification_status, is_active FROM Users WHERE user_id = :user_id");
            $stmtUser->bindParam(':user_id', $userId, PDO::PARAM_INT);
            $stmtUser->execute();
            $user = $stmtUser->fetch(PDO::FETCH_ASSOC);

            if ($user) {
                echo json_encode(['type' => 'personal', 'details' => $user]);
            } else {
                http_response_code(404);
                echo json_encode(['error' => 'User not found']);
            }
        }

    } else {
        http_response_code(400);
        echo json_encode(['error' => 'Invalid user ID']);
    }

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>