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
                v.vehicle_id,
                v.user_id,
                v.vehicle_name,
                v.brand,
                v.model,
                v.description,
                v.capacity,
                v.fuel_type,
                v.transmission_type,
                v.availability_status,
                v.location,
                v.price_type,
                v.amount,
                v.vehicle_images,
                v.keywords,
                COALESCE(bp.business_name, u.username) AS lister_name
            FROM Vehicles v
            INNER JOIN Users u ON v.user_id = u.user_id
            LEFT JOIN BusinessProfiles bp ON v.user_id = bp.user_id
            WHERE v.user_id = :user_id AND v.approval_status = 'Approved'
        ");
        $stmt->bindParam(':user_id', $user_id, PDO::PARAM_INT);
        $stmt->execute();
        $vehicles = $stmt->fetchAll(PDO::FETCH_ASSOC);

        header('Content-Type: application/json');
        echo json_encode($vehicles);

    } else {
        http_response_code(400);
        echo json_encode(['error' => 'User ID is required.']);
    }

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>