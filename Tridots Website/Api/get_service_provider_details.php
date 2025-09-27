<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

$conn = new mysqli($host, $username, $password, $dbname);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if (isset($_POST['seller_id'])) {
    $seller_id = $_POST['seller_id'];
    error_log("Fetching service provider with seller_id: " . $seller_id); // Log the ID

    $sql = "SELECT name, profile_picture FROM Service_Providers WHERE seller_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $seller_id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        $row = $result->fetch_assoc();
        error_log("Service provider found: " . json_encode($row)); // Log the fetched data
        echo json_encode($row);
    } else {
        error_log("Service provider NOT found for seller_id: " . $seller_id); // Log if not found
        echo json_encode(array("error" => "Service provider not found."));
    }
    $stmt->close();
} else {
    error_log("Seller ID not provided in the request."); // Log missing ID
    echo json_encode(array("error" => "Seller ID is required."));
}
$conn->close();
?>