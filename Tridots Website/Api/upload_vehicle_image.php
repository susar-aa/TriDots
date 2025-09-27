<?php
    if (isset($_POST['image']) && isset($_POST['name'])) {
        $base64Image = $_POST['image'];
        $imageName = $_POST['name'];
        $uploadPath = "../images/Ads/"; // Local server path relative to this script

        if (!is_dir($uploadPath)) {
            mkdir($uploadPath, 0755, true); // Create directory if it doesn't exist
        }

        $decodedImage = base64_decode($base64Image);

        if ($decodedImage === false) {
            $response = array("status" => "error", "message" => "Failed to decode base64 image data.");
            echo json_encode($response);
            exit();
        }

        $filePath = $uploadPath . $imageName;
        $uploadSuccess = file_put_contents($filePath, $decodedImage);

        if ($uploadSuccess !== false) {
            // Public URL to access the uploaded image
            $imageUrl = "https://lionsgoldencircle.com/Tridots/images/Ads/" . $imageName;
            $response = array("status" => "success", "message" => "Image uploaded successfully.", "image_url" => $imageUrl);
        } else {
            $error = error_get_last();
            $errorMessage = "Failed to save image.";
            if ($error !== null) {
                $errorMessage .= " PHP Error: " . $error['message'];
            }
            $response = array("status" => "error", "message" => $errorMessage);
        }
        echo json_encode($response);
    } else {
        $response = array("status" => "error", "message" => "Missing image data or name.");
        echo json_encode($response);
    }
?>
