<?php
header('Content-Type: application/json');

$host = 'localhost:3306';
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    if (isset($_POST['seller_id']) && is_numeric($_POST['seller_id'])) {
        $seller_id = $_POST['seller_id'];
        $name = $_POST['name'] ?? '';
        $description = $_POST['description'] ?? null;
        $contact_number = $_POST['contact_number'] ?? null;
        $email_address = $_POST['email_address'] ?? null;
        $address = $_POST['address'] ?? null;
        $experience_years = $_POST['experience_years'] ?? null;
        $qualifications = $_POST['qualifications'] ?? null;
        $location = $_POST['location'] ?? null;
        $availability_status = $_POST['availability_status'] ?? 'Available';
        $profile_picture = $_POST['profile_picture'] ?? null;
        $service_category = $_POST['service_category'] ?? null; // Assuming you send category name

        // Modified query to use the correct table name 'ServicesCategory'
        // and the column name 'service_category_name'
        $stmt = $pdo->prepare("UPDATE Service_Providers SET
            name = :name,
            description = :description,
            contact_number = :contact_number,
            email_address = :email_address,
            address = :address,
            experience_years = :experience_years,
            qualifications = :qualifications,
            location = :location,
            availability_status = :availability_status,
            profile_picture = :profile_picture,
            service_category_id = (SELECT service_category_id FROM ServicesCategory WHERE service_category_name = :service_category),
            updated_at = NOW()
            WHERE seller_id = :seller_id");

        $stmt->bindParam(':seller_id', $seller_id, PDO::PARAM_INT);
        $stmt->bindParam(':name', $name, PDO::PARAM_STR);
        $stmt->bindParam(':description', $description, PDO::PARAM_STR);
        $stmt->bindParam(':contact_number', $contact_number, PDO::PARAM_STR);
        $stmt->bindParam(':email_address', $email_address, PDO::PARAM_STR);
        $stmt->bindParam(':address', $address, PDO::PARAM_STR);
        $stmt->bindParam(':experience_years', $experience_years, PDO::PARAM_INT);
        $stmt->bindParam(':qualifications', $qualifications, PDO::PARAM_STR);
        $stmt->bindParam(':location', $location, PDO::PARAM_STR);
        $stmt->bindParam(':availability_status', $availability_status, PDO::PARAM_STR);
        $stmt->bindParam(':profile_picture', $profile_picture, PDO::PARAM_STR);
        $stmt->bindParam(':service_category', $service_category, PDO::PARAM_STR);
        $stmt->execute();

        if ($stmt->rowCount() > 0) {
            echo json_encode(['status' => 'success', 'message' => 'Service provider profile updated successfully.']);
        } else {
            echo json_encode(['status' => 'info', 'message' => 'No changes were made to the service provider profile.']);
        }

    } else {
        echo json_encode(['status' => 'error', 'message' => 'Invalid or missing seller ID.']);
    }

} catch (PDOException $e) {
    echo json_encode(['status' => 'error', 'message' => 'Database error: ' . $e->getMessage()]);
}
?>