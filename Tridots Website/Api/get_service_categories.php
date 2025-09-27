<?php
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';


// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// SQL query to fetch service categories
$sql = "SELECT service_category_id, service_category_name, description, category_icon FROM ServicesCategory";
$result = $conn->query($sql);

$categories = array();
if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        $categories[] = $row;
    }
}

// Set response content type to JSON
header('Content-Type: application/json');

// Encode the array to JSON
echo json_encode($categories);

$conn->close();
?>