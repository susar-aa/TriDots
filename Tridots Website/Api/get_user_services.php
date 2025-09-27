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
                sp.seller_id,
                sp.user_id,
                sp.name,
                sc.service_category_name,
                sp.description,
                sp.contact_number,
                sp.email_address,
                sp.address,
                sp.experience_years,
                sp.qualifications,
                sp.location,
                sp.availability_status,
                sp.reviews_average,
                sp.review_count,
                sp.verification_status,
                sp.profile_picture,
                COALESCE(bp.business_name, u.username) AS lister_name
            FROM Service_Providers sp
            INNER JOIN ServicesCategory sc ON sp.service_category_id = sc.service_category_id
            INNER JOIN Users u ON sp.user_id = u.user_id
            LEFT JOIN BusinessProfiles bp ON sp.user_id = bp.user_id
            WHERE sp.user_id = :user_id AND sp.approval_status = 'Approved' AND sp.availability_status = 'Available'
        ");
        $stmt->bindParam(':user_id', $user_id, PDO::PARAM_INT);
        $stmt->execute();
        $services = $stmt->fetchAll(PDO::FETCH_ASSOC);

        header('Content-Type: application/json');
        echo json_encode($services);

    } else {
        http_response_code(400);
        echo json_encode(['error' => 'User ID is required.']);
    }

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>