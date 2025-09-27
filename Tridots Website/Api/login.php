<?php
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    die("Database connection failed: " . $e->getMessage());
}

if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST["action"]) && $_POST["action"] == "login") {
    $loginIdentifier = trim($_POST["login_identifier"]); // Trim input
    $password = trim($_POST["password"]);             // Trim input

    if (empty($loginIdentifier) || empty($password)) {
        echo "Please enter username/email and password.";
        exit();
    }

    // Query to check for username or email (case-insensitive)
    $stmt = $pdo->prepare("SELECT user_id, username, password_hash, profile_picture FROM Users
                            WHERE LOWER(username) = LOWER(:login) OR LOWER(email_address) = LOWER(:login)");
    $stmt->bindParam(":login", $loginIdentifier);
    $stmt->execute();
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($user && password_verify($password, $user["password_hash"])) {
        // Password is correct, login successful
        echo "success:" . $user["username"] . ":" . basename($user["profile_picture"]) . ":" . $user["user_id"]; // Include user_id
    } else {
        echo "Invalid username/email or password.";
    }
} else {
    echo "Invalid request.";
}

$pdo = null; // Close the database connection
?>