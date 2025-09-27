<?php
header('Content-Type: application/json');

$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Support ?id=xxx for a single machine fetch
    if (isset($_GET['id'])) {
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
                r.approval_status = 'Approved'
                AND r.product_id = :id
            LIMIT 1
        ");
        $stmt->execute([':id' => $_GET['id']]);
        $result = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($result) {
            echo json_encode($result);
        } else {
            http_response_code(404);
            echo json_encode(['error' => 'Machine not found']);
        }
        exit;
    }

    // Default: return all approved renting ads
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
            r.approval_status = 'Approved'
    ");
    $stmt->execute();
    $rentingAds = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode($rentingAds);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['error' => 'Database error: ' . $e->getMessage()]);
}
?>