<?php
header('Content-Type: application/json');

$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    if (isset($_GET['sub_category_id'])) {
        $subCategoryId = $_GET['sub_category_id'];

        $stmt = $pdo->prepare("
            SELECT
                r.*,
                u.user_id,
                COALESCE(bp.business_name, u.username) AS lister_name
            FROM
                renting r
            INNER JOIN
                Users u ON r.user_id = u.user_id
            LEFT JOIN
                BusinessProfiles bp ON r.user_id = bp.user_id
            WHERE
                r.sub_category_id = :sub_category_id AND r.Availability_status = 'Available' AND r.approval_status = 'Approved'
        ");
        $stmt->bindParam(':sub_category_id', $subCategoryId, PDO::PARAM_INT);
        $stmt->execute();
        $rentingAds = $stmt->fetchAll(PDO::FETCH_ASSOC);

        echo json_encode($rentingAds);

    } else {
        http_response_code(400);
        echo json_encode(['error' => 'Missing sub_category_id parameter']);
    }

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>