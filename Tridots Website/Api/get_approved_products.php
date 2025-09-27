<?php
// get_approved_products.php
// Path: https://lionsgoldencircle.com/Tridots/Api/get_approved_products.php

require_once 'db.php'; // Include your database connection

header('Content-Type: application/json');

try {
    // Select all columns from Marketplace and the image_url from ProductImages
    // We LEFT JOIN because a product might not have a thumbnail yet, and we still want to list it.
    // We filter for `is_thumbnail = 1` to get the main image.
    $stmt = $pdo->prepare(
        "SELECT m.*, pi.image_url AS thumbnail_image_url
         FROM Marketplace m
         LEFT JOIN ProductImages pi ON m.product_id = pi.product_id AND pi.is_thumbnail = 1
         WHERE m.approved_status = 'Approved' AND m.availability_status = 'Available'"
    );
    $stmt->execute();
    $products = $stmt->fetchAll(PDO::FETCH_ASSOC);

    // Prepend the base image URL to the thumbnail_image_url
    // Make sure this matches where your images are actually stored.
    $imagesBaseUrl = "https://lionsgoldencircle.com/Tridots/images/Marketplace/"; // Your sample URL indicates this base path

    foreach ($products as &$product) { // Use & to modify the array by reference
        if (!empty($product['thumbnail_image_url'])) {
            // Only prepend if the image_url is relative. If it's already a full URL, skip.
            // A simple check: if it doesn't start with http, assume it's relative.
            if (strpos($product['thumbnail_image_url'], 'http') !== 0) {
                $product['thumbnail_image_url'] = $imagesBaseUrl . $product['thumbnail_image_url'];
            }
        } else {
            // Set a default/empty string if no thumbnail is found, so Android doesn't crash on null
            $product['thumbnail_image_url'] = '';
        }
    }
    unset($product); // Break the reference with the last element

    echo json_encode($products);

} catch (\PDOException $e) {
    error_log("Error fetching approved products: " . $e->getMessage());
    echo json_encode(['error' => 'Failed to retrieve products.']);
}
?>