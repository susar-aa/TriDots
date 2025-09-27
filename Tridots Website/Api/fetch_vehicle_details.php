<?php
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Check if the vehicle_id parameter is set
    if (isset($_GET['vehicle_id']) && is_numeric($_GET['vehicle_id'])) {
        $vehicleId = $_GET['vehicle_id'];

        $stmt = $pdo->prepare("
            SELECT
                v.vehicle_name,
                v.brand,
                v.model,
                v.description,
                v.capacity,
                v.fuel_type,
                v.transmission_type,
                v.location,
                v.price_type,
                v.amount,
                v.vehicle_images,
                u.user_id,
                COALESCE(bp.business_name, u.username) AS lister_name
            FROM
                Vehicles v
            INNER JOIN
                Users u ON v.user_id = u.user_id
            LEFT JOIN
                BusinessProfiles bp ON v.user_id = bp.user_id
            WHERE
                v.vehicle_id = :vehicle_id
        ");
        $stmt->bindParam(':vehicle_id', $vehicleId, PDO::PARAM_INT);
        $stmt->execute();

        if ($stmt->rowCount() > 0) {
            $vehicle = $stmt->fetch(PDO::FETCH_ASSOC);
            header('Content-Type: application/json');
            echo json_encode($vehicle);
        } else {
            http_response_code(404); // Not Found
            echo json_encode(array("error" => "Vehicle not found"));
        }

    } else {
        http_response_code(400); // Bad Request
        echo json_encode(array("error" => "Invalid vehicle_id"));
    }

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(array("error" => "Database connection failed: " . $e->getMessage()));
}
?>