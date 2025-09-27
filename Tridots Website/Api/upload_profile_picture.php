<?php
// Database connection details
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

if ($_SERVER["REQUEST_METHOD"] == "POST" && isset($_POST["action"]) && $_POST["action"] == "upload_profile_picture") {
    $username = $_POST["username"];
    $encodedImage = $_POST["image"];

    if (empty($username) || empty($encodedImage)) {
        echo "Please provide username and image data.";
        exit();
    }

    // Decode the Base64 string
    $imageData = base64_decode($encodedImage);
    if ($imageData === false) {
        echo "Invalid base64 encoded image data.";
        exit();
    }

    // Define the upload directory - use absolute path
    $uploadDir = $_SERVER['DOCUMENT_ROOT'] . '/Tridots/images/ProfilePictures/';
    
    // Create directory if it doesn't exist
    if (!file_exists($uploadDir)) {
        mkdir($uploadDir, 0755, true);
    }

    // Generate a unique filename
    $filename = uniqid() . ".jpg";
    $filepath = $uploadDir . $filename;
    $relativePath = "images/ProfilePictures/" . $filename;

    // Save the image to the server
    $success = file_put_contents($filepath, $imageData);
    if (!$success) {
        error_log("Failed to write file to: " . $filepath);
        error_log("Upload directory: " . $uploadDir);
        error_log("Is writable: " . (is_writable($uploadDir) ? 'yes' : 'no'));
        echo "Failed to save the image. Server error.";
        exit();
    }

    // Update the database with the image path
    try {
        $stmt = $pdo->prepare("UPDATE Users SET profile_picture = :filepath WHERE username = :username");
        $stmt->bindParam(":filepath", $relativePath);
        $stmt->bindParam(":username", $username);

        if ($stmt->execute()) {
            echo "success";
        } else {
            echo "Failed to update database.";
        }
    } catch (PDOException $e) {
        echo "Database error: " . $e->getMessage();
    }
} else {
    echo "Invalid request.";
}

$pdo = null; // Close the database connection
?>