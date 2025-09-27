<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');

// Database credentials
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

$pdo = null;

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die(json_encode(['error' => 'Database connection failed: ' . $e->getMessage()]));
}

if (isset($_GET['query'])) {
    $query = '%' . $_GET['query'] . '%';
    $results = [];

    $tables = [
        'Vehicles' => [
            'columns' => [
                'vehicle_id AS id',
                'vehicle_name AS title',
                'description',
                'location',
                'amount',
                'price_type AS priceType',
                'brand',
                'model',
                'vehicle_images AS imageUrl',
                "'vehicle' AS type"
            ],
            'where' => "(vehicle_name LIKE :query OR brand LIKE :query OR model LIKE :query OR description LIKE :query OR keywords LIKE :query) AND approval_status = 'Approved'"
        ],
        'renting' => [
            'columns' => [
                'rent_id AS id',
                'product_name AS title',
                'product_description AS description',
                'product_location AS location',
                'price_per_hour AS amount',
                "'per_hour' AS priceType", // Assuming price_per_hour is hourly
                'brand',
                'model',
                'product_images AS imageUrl',
                "'renting' AS type"
            ],
            'where' => "(product_name LIKE :query OR brand LIKE :query OR model LIKE :query OR product_description LIKE :query OR keywords LIKE :query) AND approval_status = 'Approved'"
        ],
        'Service_Providers' => [
            'columns' => [
                'seller_id AS id',
                'name AS title',
                'description',
                'location',
                'NULL AS amount',
                'NULL AS priceType',
                'NULL AS brand',
                'NULL AS model',
                'profile_picture AS imageUrl',
                'service_category_id AS service_category_name',
                'contact_number',
                "'service_provider' AS type"
            ],
            'where' => "(name LIKE :query OR service_category_name LIKE :query OR description LIKE :query OR qualifications LIKE :query OR location LIKE :query) AND approval_status = 'Approved'"
        ],
        'BusinessProfiles' => [
            'columns' => [
                'business_profile_id AS id',
                'business_name AS title',
                'bio_description AS description',
                'address AS location',
                'NULL AS amount',
                'NULL AS priceType',
                'NULL AS brand',
                'NULL AS model',
                'logo AS imageUrl',
                'contact_number',
                "'business_profile' AS type"
            ],
            'where' => "(business_name LIKE :query OR bio_description LIKE :query) AND is_active = 'Active'"
        ],
        'ServicesCategory' => [
            'columns' => [
                'service_category_id AS id',
                'service_category_name AS title',
                'description',
                "'' AS location", // Add empty fields as they don't exist in this table
                'NULL AS amount',
                "'' AS priceType",
                "'' AS brand",
                "'' AS model",
                'category_icon AS imageUrl',
                "'service_category' AS type"
            ],
            'where' => "service_category_name LIKE :query"
        ],
        'VehicleCategory' => [
            'columns' => [
                'vehicle_category_id AS id',
                'vehicle_category_name AS title',
                'description',
                "'' AS location",
                'NULL AS amount',
                "'' AS priceType",
                "'' AS brand",
                "'' AS model",
                'category_icon AS imageUrl',
                "'vehicle_category' AS type"
            ],
            'where' => "vehicle_category_name LIKE :query"
        ],
        'main_category' => [
            'columns' => [
                'main_category_id AS id',
                'main_category AS title',
                'category_description AS description',
                "'' AS location",
                'NULL AS amount',
                "'' AS priceType",
                "'' AS brand",
                "'' AS model",
                'category_icon AS imageUrl',
                "'main_category' AS type"
            ],
            'where' => "main_category LIKE :query"
        ]
    ];

    foreach ($tables as $tableName => $tableConfig) {
        try {
            $sql = "SELECT " . implode(', ', $tableConfig['columns']) . " FROM $tableName WHERE " . $tableConfig['where'];
            $stmt = $pdo->prepare($sql);
            $stmt->bindParam(':query', $query, PDO::PARAM_STR);
            $stmt->execute();
            while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
                $results[] = $row;
            }
        } catch (PDOException $e) {
            error_log("MySQL Error ($tableName): " . $e->getMessage());
        }
    }

    echo json_encode($results);

} else {
    echo json_encode([]);
}

$pdo = null;
?>