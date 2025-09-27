<?php
// set_default_address.php
header('Content-Type: application/json');
include 'db.php';

$response = array();

if (isset($_POST['address_id'], $_POST['user_id'], $_POST['is_default'])) {
    $address_id = $conn->real_escape_string($_POST['address_id']);
    $user_id = $conn->real_escape_string($_POST['user_id']);
    $is_default = (int)$_POST['is_default'];

    // Start a transaction for atomicity
    $conn->begin_transaction();

    try {
        if ($is_default == 1) {
            // Unset current default for this user
            $update_others_sql = "UPDATE UserAddresses SET is_default = 0 WHERE user_id = ? AND address_id != ?";
            $stmt_others = $conn->prepare($update_others_sql);
            $stmt_others->bind_param("ii", $user_id, $address_id);
            if (!$stmt_others->execute()) {
                throw new Exception("Error unsetting other defaults: " . $stmt_others->error);
            }
            $stmt_others->close();

            // Set the specified address as default
            $set_this_sql = "UPDATE UserAddresses SET is_default = 1 WHERE address_id = ? AND user_id = ?";
            $stmt_this = $conn->prepare($set_this_sql);
            $stmt_this->bind_param("ii", $address_id, $user_id);
            if (!$stmt_this->execute()) {
                throw new Exception("Error setting new default: " . $stmt_this->error);
            }
            $stmt_this->close();

            $response['success'] = true;
            $response['message'] = "Default address updated successfully.";

        } else {
            // If trying to unset the default, handle carefully.
            // Option 1: Prevent unsetting if it's the only address or if another isn't explicitly set.
            // Option 2: Allow unsetting, and perhaps the system will auto-assign a new default (as in delete logic).
            // For now, let's allow unsetting but warn if no other default exists.
            
            // Check how many default addresses would be left
            $check_defaults_sql = "SELECT COUNT(*) FROM UserAddresses WHERE user_id = ? AND is_default = 1 AND address_id != ?";
            $stmt_check = $conn->prepare($check_defaults_sql);
            $stmt_check->bind_param("ii", $user_id, $address_id);
            $stmt_check->execute();
            $stmt_check->bind_result($default_count);
            $stmt_check->fetch();
            $stmt_check->close();

            if ($default_count == 0) { // If this is the only default
                $count_total_addresses = 0;
                $stmt_total = $conn->prepare("SELECT COUNT(*) FROM UserAddresses WHERE user_id = ?");
                $stmt_total->bind_param("i", $user_id);
                $stmt_total->execute();
                $stmt_total->bind_result($count_total_addresses);
                $stmt_total->fetch();
                $stmt_total->close();

                if ($count_total_addresses > 1) { // If there are other addresses, but no other default
                     // Find another address and make it default
                    $set_new_default_sql = "UPDATE UserAddresses SET is_default = 1 WHERE user_id = ? AND address_id != ? ORDER BY address_id ASC LIMIT 1";
                    $stmt_auto_default = $conn->prepare($set_new_default_sql);
                    $stmt_auto_default->bind_param("ii", $user_id, $address_id);
                    $stmt_auto_default->execute();
                    $stmt_auto_default->close();

                    $update_this_sql = "UPDATE UserAddresses SET is_default = 0 WHERE address_id = ? AND user_id = ?";
                    $stmt_this = $conn->prepare($update_this_sql);
                    $stmt_this->bind_param("ii", $address_id, $user_id);
                    if (!$stmt_this->execute()) {
                        throw new Exception("Error unsetting address: " . $stmt_this->error);
                    }
                    $stmt_this->close();
                    $response['success'] = true;
                    $response['message'] = "Default address changed. Another address was set as default.";
                } else { // Only one address, and trying to unset it as default
                    $response['success'] = false;
                    $response['message'] = "Cannot unset the only address as default. Please add another address first.";
                    $conn->rollback();
                    echo json_encode($response);
                    $conn->close();
                    exit();
                }
            } else { // There are other default addresses or this one isn't default
                $update_this_sql = "UPDATE UserAddresses SET is_default = 0 WHERE address_id = ? AND user_id = ?";
                $stmt_this = $conn->prepare($update_this_sql);
                $stmt_this->bind_param("ii", $address_id, $user_id);
                if (!$stmt_this->execute()) {
                    throw new Exception("Error unsetting address: " . $stmt_this->error);
                }
                $stmt_this->close();
                $response['success'] = true;
                $response['message'] = "Address updated. Default status removed.";
            }
        }
        $conn->commit();
    } catch (Exception $e) {
        $conn->rollback(); // Rollback on error
        $response['success'] = false;
        $response['message'] = $e->getMessage();
        http_response_code(500); // Internal Server Error
    }

} else {
    $response['success'] = false;
    $response['message'] = "Missing required parameters.";
    http_response_code(400); // Bad Request
}

echo json_encode($response);
$conn->close();
?>