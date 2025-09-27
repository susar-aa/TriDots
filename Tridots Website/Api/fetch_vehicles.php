<?php
$servername = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$sql = "SELECT
            vehicle_id,
            vehicle_name,
            brand,
            model,
            price_type,
            amount,
            vehicle_images,
            location,
            description
        FROM
            Vehicles
        WHERE
            availability_status = 'Available' AND approval_status = 'Approved'
        ORDER BY
            vehicle_id DESC
        LIMIT 10";

$result = $conn->query($sql);

$vehicles = array();

if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $vehicles[] = $row;
    }
}

// Return data as JSON
echo json_encode($vehicles);

$conn->close();
?>