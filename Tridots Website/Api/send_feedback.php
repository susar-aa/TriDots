<?php
// Database connection details
$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $conn = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password);
    // Set the PDO error mode to exception
    $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    $response = array('error' => true, 'message' => 'Database connection failed: ' . $e->getMessage());
    header('Content-Type: application/json');
    echo json_encode($response);
    die(); // Terminate the script if database connection fails
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $user_id = isset($_POST['user_id']) ? intval($_POST['user_id']) : 0;
    $feedback_type = isset($_POST['feedback_type']) ? $_POST['feedback_type'] : '';
    $item_id = isset($_POST['item_id']) ? intval($_POST['item_id']) : 0;
    $rating = isset($_POST['rating']) ? intval($_POST['rating']) : 0;
    $comment = isset($_POST['comment']) ? $_POST['comment'] : '';
    $inquiry_id = isset($_POST['inquiry_id']) ? intval($_POST['inquiry_id']) : 0;

    $response = array('error' => false, 'message' => '');

    try {
        // Start transaction to ensure data consistency
        $conn->beginTransaction();

        // 1. Insert feedback into the 'feedbacks' table
        $sql_insert_feedback = "INSERT INTO feedbacks (user_id, feedback_type, item_id, rating, comment, created_at)
                                VALUES (:user_id, :feedback_type, :item_id, :rating, :comment, NOW())";
        $stmt_insert_feedback = $conn->prepare($sql_insert_feedback);
        $stmt_insert_feedback->bindParam(':user_id', $user_id, PDO::PARAM_INT);
        $stmt_insert_feedback->bindParam(':feedback_type', $feedback_type, PDO::PARAM_STR);
        $stmt_insert_feedback->bindParam(':item_id', $item_id, PDO::PARAM_INT);
        $stmt_insert_feedback->bindParam(':rating', $rating, PDO::PARAM_INT);
        $stmt_insert_feedback->bindParam(':comment', $comment, PDO::PARAM_STR);
        $stmt_insert_feedback->execute();

        // 2. Update relevant table based on feedback_type
        if ($feedback_type == 'vehicles') {
            $sql_update_vehicle = "UPDATE Vehicles
                                   SET average_rating = (SELECT AVG(rating) FROM feedbacks WHERE item_id = :item_id AND feedback_type = 'vehicles')
                                   WHERE vehicle_id = :item_id";
            $stmt_update_vehicle = $conn->prepare($sql_update_vehicle);
            $stmt_update_vehicle->bindParam(':item_id', $item_id, PDO::PARAM_INT);
            $stmt_update_vehicle->execute();
        } else if ($feedback_type == 'renting') {
            $sql_update_renting = "UPDATE renting
                                   SET average_rating = (SELECT AVG(rating) FROM feedbacks WHERE item_id = :item_id AND feedback_type = 'renting'),
                                       review_count = review_count + 1
                                   WHERE rent_id = :item_id";
            $stmt_update_renting = $conn->prepare($sql_update_renting);
            $stmt_update_renting->bindParam(':item_id', $item_id, PDO::PARAM_INT);
            $stmt_update_renting->execute();
        } else if ($feedback_type == 'service_provider') {
            $sql_update_provider = "UPDATE Service_Providers
                                    SET reviews_average = (SELECT AVG(rating) FROM feedbacks WHERE item_id = :item_id AND feedback_type = 'service_provider'),
                                        review_count = review_count + 1
                                    WHERE seller_id = :item_id";
            $stmt_update_provider = $conn->prepare($sql_update_provider);
            $stmt_update_provider->bindParam(':item_id', $item_id, PDO::PARAM_INT);
            $stmt_update_provider->execute();
        }

        // 3. Update the inquiry status
        $sql_update_inquiry = "UPDATE inquiries SET status = 'To Review' WHERE inquiry_id = :inquiry_id";
        $stmt_update_inquiry = $conn->prepare($sql_update_inquiry);
        $stmt_update_inquiry->bindParam(':inquiry_id', $inquiry_id, PDO::PARAM_INT);
        $stmt_update_inquiry->execute();

        // Commit the transaction
        $conn->commit();

        $response['message'] = 'Feedback submitted successfully.';

    } catch (PDOException $e) {
        // Rollback the transaction on error
        $conn->rollBack();
        $response['error'] = true;
        $response['message'] = 'Error processing feedback: ' . $e->getMessage();
    }

    header('Content-Type: application/json');
    echo json_encode($response);

} else {
    // Handle non-POST requests
    header('HTTP/1.0 405 Method Not Allowed');
    echo 'Method Not Allowed';
}

// Close the database connection (optional, PDO manages connection efficiently)
$conn = null;
?>