<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');

// Database credentials
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die(json_encode(['error' => 'Database connection failed: ' . $e->getMessage()]));
}

if (isset($_GET['query'])) {
    $query = '%' . $_GET['query'] . '%';
    $limit = 5; // Limit the number of suggestions
    $suggestions = [];

    try {
        // Search in Vehicles
        $stmt = $pdo->prepare("SELECT vehicle_name FROM Vehicles WHERE vehicle_name LIKE :query LIMIT :limit");
        $stmt->bindParam(':query', $query, PDO::PARAM_STR);
        $stmt->bindParam(':limit', $limit, PDO::PARAM_INT);
        $stmt->execute();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $suggestions[] = $row['vehicle_name'];
        }
    } catch (PDOException $e) {
        error_log("MySQL Error (Vehicles): " . $e->getMessage());
    }

    try {
        // Search in renting (product_name and brand)
        $stmt = $pdo->prepare("SELECT product_name FROM renting WHERE product_name LIKE :query LIMIT :limit");
        $stmt->bindParam(':query', $query, PDO::PARAM_STR);
        $stmt->bindParam(':limit', $limit, PDO::PARAM_INT);
        $stmt->execute();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $suggestions[] = $row['product_name'];
        }
    } catch (PDOException $e) {
        error_log("MySQL Error (renting - product_name): " . $e->getMessage());
    }

    try {
        $stmt = $pdo->prepare("SELECT brand FROM renting WHERE brand LIKE :query LIMIT :limit");
        $stmt->bindParam(':query', $query, PDO::PARAM_STR);
        $stmt->bindParam(':limit', $limit, PDO::PARAM_INT);
        $stmt->execute();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $suggestions[] = $row['brand'];
        }
    } catch (PDOException $e) {
        error_log("MySQL Error (renting - brand): " . $e->getMessage());
    }

    try {
        // Search in Service_Providers (name and service_category_name)
        $stmt = $pdo->prepare("SELECT name FROM Service_Providers WHERE name LIKE :query LIMIT :limit");
        $stmt->bindParam(':query', $query, PDO::PARAM_STR);
        $stmt->bindParam(':limit', $limit, PDO::PARAM_INT);
        $stmt->execute();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $suggestions[] = $row['name'];
        }
    } catch (PDOException $e) {
        error_log("MySQL Error (Service_Providers - name): " . $e->getMessage());
    }

    try {
        $stmt = $pdo->prepare("SELECT service_category_name FROM Service_Providers WHERE service_category_name LIKE :query LIMIT :limit");
        $stmt->bindParam(':query', $query, PDO::PARAM_STR);
        $stmt->bindParam(':limit', $limit, PDO::PARAM_INT);
        $stmt->execute();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $suggestions[] = $row['service_category_name'];
        }
    } catch (PDOException $e) {
        error_log("MySQL Error (Service_Providers - service_category_name): " . $e->getMessage());
    }

    try {
        // Search in BusinessProfiles (business_name)
        $stmt = $pdo->prepare("SELECT business_name FROM BusinessProfiles WHERE business_name LIKE :query AND is_active = 'Active' LIMIT :limit");
        $stmt->bindParam(':query', $query, PDO::PARAM_STR);
        $stmt->bindParam(':limit', $limit, PDO::PARAM_INT);
        $stmt->execute();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $suggestions[] = $row['business_name'];
        }
    } catch (PDOException $e) {
        error_log("MySQL Error (BusinessProfiles): " . $e->getMessage());
    }

    // New suggestions from ServicesCategory
    try {
        $stmt = $pdo->prepare("SELECT service_category_name FROM ServicesCategory WHERE service_category_name LIKE :query LIMIT :limit");
        $stmt->bindParam(':query', $query, PDO::PARAM_STR);
        $stmt->bindParam(':limit', $limit, PDO::PARAM_INT);
        $stmt->execute();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $suggestions[] = $row['service_category_name'];
        }
    } catch (PDOException $e) {
        error_log("MySQL Error (ServicesCategory): " . $e->getMessage());
    }

    // New suggestions from VehicleCategory
    try {
        $stmt = $pdo->prepare("SELECT vehicle_category_name FROM VehicleCategory WHERE vehicle_category_name LIKE :query LIMIT :limit");
        $stmt->bindParam(':query', $query, PDO::PARAM_STR);
        $stmt->bindParam(':limit', $limit, PDO::PARAM_INT);
        $stmt->execute();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $suggestions[] = $row['vehicle_category_name'];
        }
    } catch (PDOException $e) {
        error_log("MySQL Error (VehicleCategory): " . $e->getMessage());
    }

    // New suggestions from main_category
    try {
        $stmt = $pdo->prepare("SELECT main_category FROM main_category WHERE main_category LIKE :query LIMIT :limit");
        $stmt->bindParam(':query', $query, PDO::PARAM_STR);
        $stmt->bindParam(':limit', $limit, PDO::PARAM_INT);
        $stmt->execute();
        while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
            $suggestions[] = $row['main_category'];
        }
    } catch (PDOException $e) {
        error_log("MySQL Error (main_category): " . $e->getMessage());
    }

    echo json_encode(array_unique($suggestions));

} else {
    echo json_encode([]);
}

$pdo = null;
?>