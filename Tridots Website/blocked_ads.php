<?php
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

session_start();
if (empty($_SESSION['user_id']) || strtolower($_SESSION['user_type']) !== 'admin') {
    header("Location: login.php");
    exit();
}
require_once __DIR__ . '/db_config.php';

try {
    $dsn = isset($port)
        ? "mysql:host=$host;port=$port;dbname=$dbname"
        : "mysql:host=$host;dbname=$dbname";
    $pdo = new PDO($dsn, $username_db, $password_db);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die("<pre>Database connection failed: " . htmlspecialchars($e->getMessage()) . "</pre>");
}

function getBlocked($pdo, $table, $fieldsArr, $idField, $label) {
    $fields = implode(', ', $fieldsArr);
    $query = "SELECT $fields FROM $table WHERE approval_status = 'Blocked'";
    try {
        $rows = $pdo->query($query)->fetchAll(PDO::FETCH_ASSOC);
    } catch (Exception $e) {
        $rows = [];
    }
    return ['rows' => $rows, 'label' => $label, 'idField' => $idField];
}

// Helper to get user type
function getUserType($pdo, $user_id) {
    try {
        $stmt = $pdo->prepare("SELECT user_type FROM Users WHERE user_id=? LIMIT 1");
        $stmt->execute([$user_id]);
        $res = $stmt->fetchColumn();
        return $res ?: '';
    } catch (Exception $e) {
        return '';
    }
}

$blockedRenting = getBlocked($pdo, 'renting', ['ad_id', 'title', 'owner_id', 'created_at'], 'ad_id', 'Renting Ads');
$blockedVehicles = getBlocked($pdo, 'Vehicles', ['vehicle_id', 'model', 'owner_id', 'created_at'], 'vehicle_id', 'Vehicles');
$blockedServices = getBlocked($pdo, 'Service_Providers', ['provider_id', 'name', 'owner_id', 'created_at'], 'provider_id', 'Service Providers');
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Blocked Ads | Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="p-4">
    <h2>Blocked Ads (All Types)</h2>
    <?php
    foreach ([$blockedRenting, $blockedVehicles, $blockedServices] as $group) {
        echo "<h4 class='mt-4'>{$group['label']}</h4>";
        if (count($group['rows'])) {
            echo "<table class='table table-bordered align-middle'><thead><tr>";
            foreach (array_keys($group['rows'][0]) as $col) echo "<th>$col</th>";
            echo "<th>Actions</th></tr></thead><tbody>";
            foreach ($group['rows'] as $row) {
                echo "<tr>";
                foreach ($row as $v) echo "<td>" . htmlspecialchars($v) . "</td>";
                echo "<td>";
                // Approve/Unblock button
                $type = ($group['label'] === 'Renting Ads') ? 'renting' : (($group['label'] === 'Vehicles') ? 'vehicles' : 'services');
                $idField = $group['idField'];
                $idVal = $row[$idField];
                echo "<form method='post' action='change_ad_status.php' style='display:inline;'>
                        <input type='hidden' name='type' value='$type'>
                        <input type='hidden' name='id' value='$idVal'>
                        <input type='hidden' name='status' value='Approved'>
                        <button type='submit' class='btn btn-success btn-sm'>Approve</button>
                    </form> ";
                // Show user type and button to make Rider if not already
                $user_type = getUserType($pdo, $row['owner_id']);
                echo "User: <b>" . htmlspecialchars($user_type) . "</b> ";
                if (strtolower($user_type) !== 'rider') {
                    echo "<form method='post' action='change_user_type.php' style='display:inline;'>
                            <input type='hidden' name='user_id' value='".htmlspecialchars($row['owner_id'])."'>
                            <input type='hidden' name='new_type' value='Rider'>
                            <button type='submit' class='btn btn-primary btn-sm'>Make Rider</button>
                        </form>";
                }
                echo "</td>";
                echo "</tr>";
            }
            echo "</tbody></table>";
        } else {
            echo "<div class='alert alert-warning'>No blocked {$group['label']} found.</div>";
        }
    }
    ?>
    <a href="admin_dashboard.php" class="btn btn-secondary mt-4">Back to Dashboard</a>
</body>
</html>