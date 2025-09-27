<?php
session_start();
if (empty($_SESSION['user_id']) || strtolower($_SESSION['user_type']) !== 'admin') {
    header("Location: login.php");
    exit();
}
require_once __DIR__ . '/db_config.php';

$tableMap = [
    'renting' => ['table' => 'renting', 'id' => 'ad_id'],
    'vehicles' => ['table' => 'Vehicles', 'id' => 'vehicle_id'],
    'services' => ['table' => 'Service_Providers', 'id' => 'provider_id'],
];

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['type'], $_POST['id'], $_POST['status'])) {
    $type = $_POST['type'];
    $id = $_POST['id'];
    $status = $_POST['status'];

    if (!isset($tableMap[$type])) exit('Invalid type');
    $table = $tableMap[$type]['table'];
    $idField = $tableMap[$type]['id'];

    require_once __DIR__ . '/db_config.php';
    $dsn = isset($port)
        ? "mysql:host=$host;port=$port;dbname=$dbname"
        : "mysql:host=$host;dbname=$dbname";
    $pdo = new PDO($dsn, $username_db, $password_db);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    $stmt = $pdo->prepare("UPDATE $table SET approval_status = ? WHERE $idField = ?");
    $stmt->execute([$status, $id]);
    header("Location: blocked_ads.php");
    exit();
}
?>