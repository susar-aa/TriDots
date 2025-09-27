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

if (isset($_POST['rent_id'])) {
    $rent_id = $_POST['rent_id'];
    $sql = "SELECT product_name, product_images FROM renting WHERE rent_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $rent_id);
    $stmt->execute();
    $result = $stmt->get_result();
    if ($result->num_rows > 0) {
        echo json_encode($result->fetch_assoc());
    } else {
        echo json_encode(array("error" => "Renting ad not found."));
    }
    $stmt->close();
} else {
    echo json_encode(array("error" => "Rent ID is required."));
}
$conn->close();
?>