<?php
// cancel_inquiry.php
// Error reporting (remove in production)
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Database connection details
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

// Create connection
$conn = new mysqli($host, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// Get inquiry ID from the request
if (isset($_POST["inquiry_id"])) {
    $inquiry_id = $_POST["inquiry_id"];

    // Prepare the SQL query to update the inquiry status to "Closed"
    $sql = "UPDATE inquiries SET status = 'Closed' WHERE inquiry_id = ?"; // Changed to Closed
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $inquiry_id);

    if ($stmt->execute()) {
        echo "success";
    } else {
        echo "Error updating record: " . $stmt->error;
    }
    $stmt->close();
} else {
    echo "Inquiry ID is required";
}
$conn->close();
?>
