<?php
// Enable error reporting for debugging (REMOVE IN PRODUCTION)
// error_reporting(E_ALL);
// ini_set('display_errors', 1);

// Set the content type to JSON for API response
header('Content-Type: application/json');

// Database connection parameters
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

// Establish a database connection using PDO
try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    echo json_encode(['success' => false, 'message' => 'Database connection failed: ' . $e->getMessage()]);
    exit();
}

// Define the directory where images will be saved
$upload_dir = __DIR__ . '/../images/Marketplace/'; // Corrected path
$base_image_url = 'https://lionsgoldencircle.com/Tridots/images/Marketplace/'; // Public URL path

// Check if the upload directory exists and is writable
if (!is_dir($upload_dir)) {
    echo json_encode(['success' => false, 'message' => 'Upload directory does not exist: ' . $upload_dir]);
    exit();
}
if (!is_writable($upload_dir)) {
    echo json_encode(['success' => false, 'message' => 'Upload directory is not writable: ' . $upload_dir]);
    exit();
}


// Check if image data is provided
if (!isset($_POST['image']) || empty($_POST['image'])) {
    echo json_encode(['success' => false, 'message' => 'No image data provided.']);
    exit();
}

$imageData = $_POST['image'];
$productId = isset($_POST['product_id']) ? filter_var($_POST['product_id'], FILTER_SANITIZE_NUMBER_INT) : 'unknown_product';
$variantName = isset($_POST['variant_name']) ? (string)$_POST['variant_name'] : 'main';
$isThumbnail = isset($_POST['is_thumbnail']) ? (int)$_POST['is_thumbnail'] : 0; // 1 for thumbnail, 0 for variant
$filenameHint = isset($_POST['filename_hint']) ? (string)$_POST['filename_hint'] : $variantName;

// Remove the "data:image/png;base64," or "data:image/jpeg;base64," prefix
$imageData = str_replace('data:image/jpeg;base64,', '', $imageData);
$imageData = str_replace('data:image/png;base64,', '', $imageData);
$imageData = str_replace(' ', '+', $imageData);

$decodedImage = base64_decode($imageData);

if ($decodedImage === false) {
    echo json_encode(['success' => false, 'message' => 'Base64 decode failed.']);
    exit();
}

// Determine file extension based on MIME type
$finfo = finfo_open(FILEINFO_MIME_TYPE);
$mimeType = finfo_buffer($finfo, $decodedImage);
finfo_close($finfo);

$extension = '';
if ($mimeType == 'image/jpeg') {
    $extension = '.jpg';
} elseif ($mimeType == 'image/png') {
    $extension = '.png';
} else {
    echo json_encode(['success' => false, 'message' => 'Unsupported image format: ' . $mimeType . '. Only JPEG and PNG are supported.']);
    exit();
}

// Generate a unique and descriptive file name
$sanitizedFilenameHint = strtolower(preg_replace('/[^a-zA-Z0-9-]/', '', str_replace(' ', '-', $filenameHint)));
$filename = $productId . '_' . $sanitizedFilenameHint . '_' . time() . $extension;
$filepath = $upload_dir . $filename;
$fullImageUrl = $base_image_url . $filename;

try {
    $pdo->beginTransaction(); // Start a transaction for file save and DB insert

    // Save the image file
    if (file_put_contents($filepath, $decodedImage)) {
        // Now, insert the image record into the ProductImages table
        $stmt = $pdo->prepare("INSERT INTO ProductImages (
            product_id, image_url, is_thumbnail, display_order
        ) VALUES (
            :product_id, :image_url, :is_thumbnail, :display_order
        )");

        // For simplicity, we can set display_order to 1 for now, or you can send it from the app.
        // If it's a variant image, its display_order might be handled differently, or default to 0.
        $displayOrder = ($isThumbnail == 1) ? 1 : 0; // Set display_order to 1 for thumbnail, 0 for variant for now

        $stmt->bindParam(':product_id', $productId, PDO::PARAM_INT);
        $stmt->bindParam(':image_url', $fullImageUrl);
        $stmt->bindParam(':is_thumbnail', $isThumbnail, PDO::PARAM_INT);
        $stmt->bindParam(':display_order', $displayOrder, PDO::PARAM_INT);

        if ($stmt->execute()) {
            $pdo->commit(); // Commit transaction if both file save and DB insert are successful
            echo json_encode(['success' => true, 'message' => 'Image uploaded and path saved to DB successfully!', 'image_url' => $fullImageUrl]);
        } else {
            // If DB insert fails, delete the uploaded file and rollback
            unlink($filepath); // Delete the file
            $pdo->rollBack();
            echo json_encode(['success' => false, 'message' => 'Image saved to file, but failed to save path to DB.']);
        }
    } else {
        // File saving failed
        $pdo->rollBack(); // Rollback if transaction started
        $error_message = 'Failed to save image file to ' . $filepath . '.';
        if (!is_writable(dirname($filepath))) {
            $error_message .= ' Directory ' . dirname($filepath) . ' is not writable.';
        }
        echo json_encode(['success' => false, 'message' => $error_message]);
    }

} catch (PDOException $e) {
    // Database error during transaction
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    // Attempt to delete the file if it was saved before the DB error
    if (file_exists($filepath)) {
        unlink($filepath);
    }
    echo json_encode(['success' => false, 'message' => 'Database error during image record insertion: ' . $e->getMessage()]);
} catch (Exception $e) {
    // General unexpected errors
    if ($pdo->inTransaction()) {
        $pdo->rollBack();
    }
    echo json_encode(['success' => false, 'message' => 'An unexpected error occurred: ' . $e->getMessage()]);
}
?>
