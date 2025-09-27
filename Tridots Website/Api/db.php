<?php
// Database configuration
$db_host = 'localhost:3306';
$db_name = 'tridots';
$db_user = 'tridots';
$db_pass = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$db_host;dbname=$db_name", $db_user, $db_pass);
    // Set error mode to exception
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die("Database connection failed: " . $e->getMessage());
}
?>