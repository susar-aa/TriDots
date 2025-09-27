<?php
// get_inquiries.php

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

// Get user ID from the request
if (isset($_POST["user_id"])) {
    $user_id = $_POST["user_id"];

    // Prepare the SQL query to fetch inquiries, including seller_reply and estimated_cost
    $sql = "SELECT inquiry_id, user_id, ad_type, ad_id, ad_owner_id, inquirer_name, inquirer_email, inquirer_phone, inquiry_message, inquiry_date, status, seller_reply, estimated_cost
            FROM inquiries
            WHERE user_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();

    // Initialize an array to store inquiries, categorized by status
    $inquiries = array(
        "Pending" => array(),
        "Replied" => array(),
        "Confirmed" => array(),
        "Closed" => array(),
        "To Review" => array()
    );

    // Helper function to fetch ad name and image
    function getAdDetails($conn, $ad_type, $ad_id) {
        $ad_name = "";
        $ad_image_url = "";
        if ($ad_type === 'renting') {
            $sql = "SELECT product_name, product_images FROM renting WHERE rent_id = ?";
            $stmt = $conn->prepare($sql);
            $stmt->bind_param("i", $ad_id);
            $stmt->execute();
            $res = $stmt->get_result();
            if ($row = $res->fetch_assoc()) {
                $ad_name = $row["product_name"];
                $ad_image_url = "";
                if (!empty($row["product_images"])) {
                    $imgs = explode(",", $row["product_images"]);
                    $ad_image_url = trim($imgs[0]);
                }
            }
            $stmt->close();
        } elseif ($ad_type === 'vehicles') {
            $sql = "SELECT vehicle_name, vehicle_images FROM Vehicles WHERE vehicle_id = ?";
            $stmt = $conn->prepare($sql);
            $stmt->bind_param("i", $ad_id);
            $stmt->execute();
            $res = $stmt->get_result();
            if ($row = $res->fetch_assoc()) {
                $ad_name = $row["vehicle_name"];
                $ad_image_url = "";
                if (!empty($row["vehicle_images"])) {
                    $imgs = explode(",", $row["vehicle_images"]);
                    $ad_image_url = trim($imgs[0]);
                }
            }
            $stmt->close();
        } elseif ($ad_type === 'service_providers') {
            $sql = "SELECT name, profile_picture FROM Service_Providers WHERE seller_id = ?";
            $stmt = $conn->prepare($sql);
            $stmt->bind_param("i", $ad_id);
            $stmt->execute();
            $res = $stmt->get_result();
            if ($row = $res->fetch_assoc()) {
                $ad_name = $row["name"];
                $ad_image_url = $row["profile_picture"];
            }
            $stmt->close();
        }
        return array("ad_name" => $ad_name, "ad_image_url" => $ad_image_url);
    }

    if ($result->num_rows > 0) {
        // Fetch each row and add it to the appropriate status category
        while ($row = $result->fetch_assoc()) {
            $status = $row["status"];

            // Format the inquiry_date for display (d M Y, eg: 19 May 2025)
            if (!empty($row["inquiry_date"]) && $row["inquiry_date"] != "0000-00-00 00:00:00") {
                $row["inquiry_date"] = date('d M Y', strtotime($row["inquiry_date"]));
            } else {
                $row["inquiry_date"] = "";
            }

            // Fetch ad details for this inquiry
            $adDetails = getAdDetails($conn, $row["ad_type"], $row["ad_id"]);
            $row["ad_name"] = $adDetails["ad_name"];
            $row["ad_image_url"] = $adDetails["ad_image_url"];

            // Ensure the status array exists before adding to it
            if (array_key_exists($status, $inquiries)) {
                $inquiries[$status][] = $row;
            }
        }

        // Convert the categorized inquiries array to JSON
        echo json_encode($inquiries);
    } else {
        echo json_encode(array("error" => "No inquiries found for this user."));
    }
    $stmt->close();
} else {
    echo json_encode(array("error" => "User ID is required."));
}
$conn->close();
?>