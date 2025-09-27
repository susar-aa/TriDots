<?php
$servername = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

    $conn = new mysqli($servername, $username, $password, $dbname);

    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    $sql = "SELECT image_path FROM banner_images";
    $result = $conn->query($sql);

    $bannerImages = array();

    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $bannerImages[] = $row["image_path"];
        }
    }

    header('Content-Type: application/json');
    echo json_encode($bannerImages);

    $conn->close();
?>