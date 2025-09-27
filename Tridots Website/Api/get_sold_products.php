<?php
// Enable error reporting for debugging
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// Database connection parameters
$host = 'localhost:3306'; // Ensure this is correct for your server setup
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

header('Content-Type: application/json');

// Create connection
$conn = new mysqli($host, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    echo json_encode(["error" => "Connection failed: " . $conn->connect_error]);
    exit();
}

// Check if seller_id is provided
if (!isset($_POST['seller_id'])) {
    echo json_encode(["error" => "Seller ID not provided."]);
    $conn->close();
    exit();
}

$seller_id = $conn->real_escape_string($_POST['seller_id']);

$sql = "
    SELECT
        oi.order_item_id,
        o.order_id,
        o.total_amount,
        o.delivery_fee,
        m.product_id,
        m.product_name,
        oi.quantity,
        oi.unit_price,
        o.user_id AS buyer_user_id,
        u.username AS buyer_username,
        o.order_date,
        o.order_status,
        o.payment_status,
        pi.image_url AS product_image_url,
        pv.variant_id,
        pv.variant_name,
        pv.variant_price,
        pv.variant_image_url,
        oi.delivery_status -- Added new delivery_status column
    FROM
        Order_Items oi
    JOIN
        Marketplace m ON oi.product_id = m.product_id
    JOIN
        Orders o ON oi.order_id = o.order_id
    JOIN
        Users u ON o.user_id = u.user_id
    LEFT JOIN
        ProductImages pi ON m.product_id = pi.product_id AND pi.is_thumbnail = 1
    LEFT JOIN
        Product_Variants pv ON oi.variant_id = pv.variant_id
    WHERE
        m.seller_id = ?
    ORDER BY
        o.order_date DESC, o.order_id DESC;
";

$stmt = $conn->prepare($sql);

if (!$stmt) {
    echo json_encode(["error" => "SQL Prepare failed: " . $conn->error . " | SQL: " . $sql]);
    $conn->close();
    exit();
}

$stmt->bind_param("i", $seller_id);
$stmt->execute();
$result = $stmt->get_result();

$sold_products = [];
if ($result->num_rows > 0) {
    while ($row = $result->fetch_assoc()) {
        if ($row['product_image_url']) {
            $row['product_image_url'] = "https://lionsgoldencircle.com/Tridots/uploads/product_images/" . $row['product_image_url'];
        } else {
            $row['product_image_url'] = "";
        }

        if ($row['variant_image_url']) {
            $row['variant_image_url'] = "https://lionsgoldencircle.com/Tridots/uploads/variant_images/" . $row['variant_image_url'];
        } else {
            $row['variant_image_url'] = "";
        }
        $sold_products[] = $row;
    }
    echo json_encode(["sold_products" => $sold_products]);
} else {
    echo json_encode(["sold_products" => []]);
}

$stmt->close();
$conn->close();
?>