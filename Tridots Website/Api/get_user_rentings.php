<?php
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $user_id = isset($_GET['user_id']) ? $_GET['user_id'] : null;

    if ($user_id !== null) {
        $stmt = $pdo->prepare("
            SELECT
                r.rent_id,
                r.user_id,
                r.product_name,
                r.brand,
                r.model,
                r.product_images,
                r.availability_status,
                r.product_description,
                r.keywords,
                r.price_per_hour,
                r.price_per_day,
                r.product_location,
                r.average_rating,
                r.review_count,
                COALESCE(bp.business_name, u.username) AS lister_name
            FROM renting r
            INNER JOIN Users u ON r.user_id = u.user_id
            LEFT JOIN BusinessProfiles bp ON r.user_id = bp.user_id
            WHERE r.user_id = :user_id AND r.approval_status = 'Approved'
        ");
        $stmt->bindParam(':user_id', $user_id, PDO::PARAM_INT);
        $stmt->execute();
        $rentings = $stmt->fetchAll(PDO::FETCH_ASSOC);

        header('Content-Type: application/json');
        echo json_encode($rentings);

    } else {
        http_response_code(400);
        echo json_encode(['error' => 'User ID is required.']);
    }

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>