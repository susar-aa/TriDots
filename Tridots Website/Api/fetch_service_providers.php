<?php
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $stmt = $pdo->prepare("
        SELECT
            sp.seller_id,
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
            u.user_id,
            COALESCE(bp.business_name, u.username) AS lister_name
        FROM Service_Providers sp
        INNER JOIN ServicesCategory sc ON sp.service_category_id = sc.service_category_id
        INNER JOIN Users u ON sp.user_id = u.user_id
        LEFT JOIN BusinessProfiles bp ON sp.user_id = bp.user_id
        WHERE sp.availability_status = 'Available' AND sp.approval_status = 'Approved'
        ORDER BY sp.seller_id DESC
        LIMIT 10
    ");
    $stmt->execute();
    $service_providers = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($service_providers);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>