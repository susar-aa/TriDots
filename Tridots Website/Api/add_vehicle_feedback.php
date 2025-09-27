<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $conn = new PDO("mysql:host=$host;dbname=$dbname", $username, $password);
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    echo json_encode(['status' => 'error', 'message' => 'Database connection failed: ' . $e->getMessage()]);
    exit();
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $ad_id = isset($_POST['vehicle_id']) ? filter_var($_POST['vehicle_id'], FILTER_SANITIZE_NUMBER_INT) : null;
    $user_id = isset($_POST['user_id']) ? filter_var($_POST['user_id'], FILTER_SANITIZE_NUMBER_INT) : null;
    $rating = isset($_POST['rating']) ? filter_var($_POST['rating'], FILTER_SANITIZE_NUMBER_INT) : null;
    $feedback_text = isset($_POST['feedback_text']) ? filter_var($_POST['feedback_text'], FILTER_SANITIZE_FULL_SPECIAL_CHARS) : null;
     $ad_type = 'vehicles'; // Hardcoded for this file

    if ($ad_id !== null && $user_id !== null && $rating !== null && $feedback_text !== null) {
        // Start transaction
        $conn->beginTransaction();

        try {
            // 1. Insert the new vehicle feedback
            $stmt = $conn->prepare("INSERT INTO Feedbacks (ad_id, user_id, rating, feedback_text, ad_type) VALUES (:ad_id, :user_id, :rating, :feedback_text, :ad_type)");
            $stmt->bindParam(':ad_id', $ad_id, PDO::PARAM_INT);
            $stmt->bindParam(':user_id', $user_id, PDO::PARAM_INT);
            $stmt->bindParam(':rating', $rating, PDO::PARAM_INT);
            $stmt->bindParam(':feedback_text', $feedback_text, PDO::PARAM_STR);
            $stmt->bindParam(':ad_type', $ad_type, PDO::PARAM_STR);
            $stmt->execute();

            // 2. Update the Vehicles table (if you have rating fields there)
            // You might have 'average_rating' and 'review_count' in your Vehicles table similar to the 'renting' table.
            // If so, uncomment and adapt the following section:

            $stmt_select_vehicle = $conn->prepare("SELECT average_rating, review_count, user_id FROM Vehicles WHERE vehicle_id = :vehicle_id");
            $stmt_select_vehicle->bindParam(':vehicle_id', $ad_id, PDO::PARAM_INT);
            $stmt_select_vehicle->execute();
            $vehicle_data = $stmt_select_vehicle->fetch(PDO::FETCH_ASSOC);

            if ($vehicle_data) {
                $current_rating = $vehicle_data['average_rating'];
                $current_review_count = $vehicle_data['review_count'];
                $lister_user_id = $vehicle_data['user_id'];

                $new_review_count = $current_review_count + 1;
                $new_average_rating = (($current_rating * $current_review_count) + $rating) / $new_review_count;

                $stmt_update_vehicle = $conn->prepare("UPDATE Vehicles SET average_rating = :average_rating, review_count = :review_count WHERE vehicle_id = :vehicle_id");
                $stmt_update_vehicle->bindParam(':average_rating', $new_average_rating);
                $stmt_update_vehicle->bindParam(':review_count', $new_review_count);
                $stmt_update_vehicle->bindParam(':vehicle_id', $ad_id, PDO::PARAM_INT);
                $stmt_update_vehicle->execute();
            }


            // 3. Update the BusinessProfiles table
            // Fetch the user_id of the lister from the Vehicles table
            $stmt_select_lister = $conn->prepare("SELECT user_id FROM Vehicles WHERE vehicle_id = :vehicle_id");
            $stmt_select_lister->bindParam(':vehicle_id', $ad_id, PDO::PARAM_INT);
            $stmt_select_lister->execute();
            $vehicle_info = $stmt_select_lister->fetch(PDO::FETCH_ASSOC);

            if ($vehicle_info) {
                $lister_user_id = $vehicle_info['user_id'];

                $stmt_update_business_profile = $conn->prepare("UPDATE BusinessProfiles SET rating_count = rating_count + 1 WHERE user_id = :user_id");
                $stmt_update_business_profile->bindParam(':user_id', $lister_user_id, PDO::PARAM_INT);
                $stmt_update_business_profile->execute();
            }

            // Commit transaction
            $conn->commit();
            echo json_encode(['status' => 'success', 'message' => 'Feedback added successfully and tables updated']);

        } catch (PDOException $e) {
            // Rollback transaction on error
            $conn->rollBack();
            echo json_encode(['status' => 'error', 'message' => 'Error adding feedback or updating tables: ' . $e->getMessage()]);
        }

    } else {
        echo json_encode(['status' => 'error', 'message' => 'Missing or invalid parameters']);
    }
} else {
    echo json_encode(['status' => 'error', 'message' => 'Invalid request method']);
}

$conn = null;
?>