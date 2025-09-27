<?php
// db.php - Database connection for TR:Dots

$host = 'localhost:3306';
$dbname = 'tridots';
$username_db = 'tridots';
$password_db = 'tridots12369';

$dsn = "mysql:host=$host;dbname=$dbname;charset=utf8mb4";
$options = [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES   => false,
];

try {
    $pdo = new PDO($dsn, $username_db, $password_db, $options);
} catch (PDOException $e) {
    exit('Database connection failed: ' . $e->getMessage());
}
?>