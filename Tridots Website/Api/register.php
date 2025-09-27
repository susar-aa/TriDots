<?php
// Enable error reporting for debugging (REMOVE IN PRODUCTION)
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

$host = 'localhost'; // Separate host and port for PDO is cleaner
$db_port = '3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;port=$db_port;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    // Log the actual database connection error for debugging
    error_log("Database connection failed: " . $e->getMessage());
    die("Database connection failed."); // Generic message to the client
}

if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST["action"]) && $_POST["action"] == "register") {
    $username = $_POST["username"];
    $email_address = $_POST["email_address"];
    $plain_password = $_POST["password_hash"]; // Get the plain password from the POST request
    $contact_number = $_POST["contact_number"];
    $nic_number = $_POST["nic_number"];
    $address = $_POST["address"];
    $user_type = $_POST["user_type"];
    $verification_status = $_POST["verification_status"];
    $is_active = $_POST["is_active"];

    // Basic input validation (you should add more robust validation)
    if (empty($username) || empty($email_address) || empty($plain_password) || empty($contact_number) || empty($nic_number) || empty($address)) {
        echo "Please fill in all required fields.";
        exit();
    }

    // Hash the password securely
    $password_hash = password_hash($plain_password, PASSWORD_DEFAULT);

    // Check if username or email already exists
    $stmt_check = $pdo->prepare("SELECT COUNT(*) FROM Users WHERE username = :username OR email_address = :email");
    $stmt_check->bindParam(":username", $username);
    $stmt_check->bindParam(":email", $email_address);
    $stmt_check->execute();
    $count = $stmt_check->fetchColumn();

    if ($count > 0) {
        echo "Username or email address already exists.";
        exit();
    }

    $stmt = $pdo->prepare("INSERT INTO Users (username, email_address, password_hash, contact_number, nic_number, user_type, address, verification_status, is_active)
                            VALUES (:username, :email, :password, :contact, :nic, :user_type, :address, :verification_status, :is_active)");

    $stmt->bindParam(":username", $username);
    $stmt->bindParam(":email", $email_address);
    $stmt->bindParam(":password", $password_hash); // Now we are binding the hashed password
    $stmt->bindParam(":contact", $contact_number);
    $stmt->bindParam(":nic", $nic_number);
    $stmt->bindParam(":user_type", $user_type);
    $stmt->bindParam(":address", $address);
    $stmt->bindParam(":verification_status", $verification_status);
    $stmt->bindParam(":is_active", $is_active);

    try {
        if ($stmt->execute()) {
            $last_id = $pdo->lastInsertId(); // Get the ID of the newly inserted user
            echo "success:" . $username . ":" . $last_id; // Respond with success, username, and user_id
        } else {
            // Log the actual SQL error for debugging
            error_log("Error during registration: " . implode(" - ", $stmt->errorInfo()));
            echo "Error during registration."; // Generic message to the client
        }
    } catch (PDOException $e) {
        // Catch any exceptions during execution and log them
        error_log("PDOException during registration: " . $e->getMessage());
        echo "An unexpected database error occurred during registration.";
    }
} else {
    echo "Invalid request.";
}

$pdo = null; // Close the database connection
?>
