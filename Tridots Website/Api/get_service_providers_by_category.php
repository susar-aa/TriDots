<?php
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Get the service category ID from the request
    $service_category_id = isset($_GET['service_category_id']) ? $_GET['service_category_id'] : null;

    if ($service_category_id !== null) {
        // SQL query to fetch approved and available service providers for the given category
        $sql = "SELECT
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
                WHERE sp.service_category_id = :service_category_id
                  AND sp.approval_status = 'Approved'
                  AND sp.availability_status = 'Available'";

        $stmt = $pdo->prepare($sql);
        $stmt->bindParam(':service_category_id', $service_category_id, PDO::PARAM_INT);
        $stmt->execute();
        $providers = $stmt->fetchAll(PDO::FETCH_ASSOC);

        // Set response content type to JSON
        header('Content-Type: application/json');
        // Encode the array to JSON
        echo json_encode($providers);

    } else {
        // Handle the case where service_category_id is not provided
        http_response_code(400); // Bad Request
        echo json_encode(array("error" => "Service category ID is required."));
    }

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>