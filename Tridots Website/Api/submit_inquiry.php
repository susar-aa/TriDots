<?php
require_once 'db.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $user_id = isset($_POST['user_id']) ? $_POST['user_id'] : null;
    $ad_owner_id = isset($_POST['ad_owner_id']) ? $_POST['ad_owner_id'] : null; // Retrieve ad_owner_id
    $ad_type = isset($_POST['ad_type']) ? $_POST['ad_type'] : null;
    $ad_id = isset($_POST['ad_id']) ? $_POST['ad_id'] : null;
    $inquirer_name = isset($_POST['inquirer_name']) ? $_POST['inquirer_name'] : null;
    $inquirer_email = isset($_POST['inquirer_email']) ? $_POST['inquirer_email'] : null;
    $inquirer_phone = isset($_POST['inquirer_phone']) ? $_POST['inquirer_phone'] : null;
    $inquiry_message = isset($_POST['inquiry_message']) ? $_POST['inquiry_message'] : null;

    if ($user_id !== null && $ad_owner_id !== null && $ad_type !== null && $ad_id !== null && $inquirer_name !== null && $inquirer_email !== null && $inquiry_message !== null) {
        try {
            $stmt = $pdo->prepare("INSERT INTO inquiries (user_id, ad_owner_id, ad_type, ad_id, inquirer_name, inquirer_email, inquirer_phone, inquiry_message) VALUES (:user_id, :ad_owner_id, :ad_type, :ad_id, :inquirer_name, :inquirer_email, :inquirer_phone, :inquiry_message)");
            $stmt->bindParam(':user_id', $user_id, PDO::PARAM_INT);
            $stmt->bindParam(':ad_owner_id', $ad_owner_id, PDO::PARAM_INT); // Bind ad_owner_id
            $stmt->bindParam(':ad_type', $ad_type);
            $stmt->bindParam(':ad_id', $ad_id, PDO::PARAM_INT);
            $stmt->bindParam(':inquirer_name', $inquirer_name);
            $stmt->bindParam(':inquirer_email', $inquirer_email);
            $stmt->bindParam(':inquirer_phone', $inquirer_phone);
            $stmt->bindParam(':inquiry_message', $inquiry_message);
            $stmt->execute();

            echo "Inquiry submitted successfully";

        } catch (PDOException $e) {
            echo "Error submitting inquiry: " . $e->getMessage();
        }
    } else {
        echo "Error: Missing required fields.";
    }
} else {
    echo "Invalid request method.";
}
?>