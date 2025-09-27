<?php
// get_user_addresses.php
header('Content-Type: application/json');

// --- IMPORTANT: Enable error reporting for debugging (REMOVE IN PRODUCTION) ---
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
// ----------------------------------------------------------------------------

// --- Database connection details and logic (self-contained) ---
$db_host = 'localhost';
$db_name = 'tridots';
$db_user = 'tridots';
$db_pass = 'tridots12369';

$pdo = null; // Initialize PDO object
try {
    $pdo = new PDO("mysql:host=$db_host;dbname=$db_name;charset=utf8mb4", $db_user, $db_pass);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $pdo->setAttribute(PDO::ATTR_EMULATE_PREPARES, false);
    error_log("get_user_addresses.php: Database connection established successfully.");
} catch (PDOException $e) {
    error_log("get_user_addresses.php: DB Connection Error: " . $e->getMessage());
    echo json_encode(array(
        "success" => false,
        "message" => "Database connection failed: " . $e->getMessage()
    ));
    exit();
}
// --- End of database connection logic ---

// --- Include the Delivery Fee Calculator functions ---
// Ensure the path is correct relative to get_user_addresses.php
// This file contains geocodeAddress, getDistanceBetweenPoints, and calculateDeliveryFee functions
// and also defines $googleApiKey and $store_address.
error_log("get_user_addresses.php: Attempting to include DeliveryFeeCalculator.php");
include 'DeliveryFeeCalculator.php';
error_log("get_user_addresses.php: DeliveryFeeCalculator.php included.");

// Verify that variables/functions from DeliveryFeeCalculator.php are available
if (!function_exists('geocodeAddress') || !isset($googleApiKey) || !isset($store_address)) {
    error_log("get_user_addresses.php: ERROR: Google Maps functions/variables are NOT accessible after include! Check DeliveryFeeCalculator.php content or path.");
    // If not accessible, return an error to the app to indicate server setup issue.
    echo json_encode(array(
        "success" => false,
        "message" => "Server configuration error: Delivery fee calculator not fully loaded."
    ));
    exit();
} else {
    error_log("get_user_addresses.php: Google Maps functions/variables are accessible.");
    error_log("get_user_addresses.php: Google API Key (first 5 chars): " . substr($googleApiKey, 0, 5) . "...");
    error_log("get_user_addresses.php: Store Address: " . $store_address);
}

$response = array(); // Initialize response array for error cases

if (isset($_GET['user_id'])) {
    $user_id = $_GET['user_id'];
    error_log("get_user_addresses.php: Received user_id = " . $user_id);

    try {
        $stmt = $pdo->prepare("SELECT * FROM UserAddresses WHERE user_id = :user_id ORDER BY is_default DESC, address_id DESC");
        $stmt->bindParam(':user_id', $user_id, PDO::PARAM_INT);
        $stmt->execute();

        $addresses = $stmt->fetchAll(PDO::FETCH_ASSOC); // Fetch all rows as associative array
        error_log("get_user_addresses.php: Fetched " . count($addresses) . " addresses from DB before fee calculation.");

        // --- Calculate Delivery Fee for each address ---
        $processedAddresses = [];
        
        // Geocode store address once for efficiency if the store location is fixed
        // This will call Google Geocoding API.
        $storeCoords = null;
        if (!empty($store_address) && !empty($googleApiKey)) {
            $storeCoords = geocodeAddress($store_address, $googleApiKey); // from DeliveryFeeCalculator.php
            if (!$storeCoords) {
                error_log("get_user_addresses.php: WARNING: Could not geocode store address: " . $store_address . ". Delivery fees for all addresses will be 0.");
                // Option: You might throw an error or set a default fee/message to the user.
            } else {
                error_log("get_user_addresses.php: Store geocoded successfully.");
            }
        } else {
             error_log("get_user_addresses.php: WARNING: Google API Key or Store Address not set/valid in DeliveryFeeCalculator.php. Delivery fees will be 0.");
        }


        foreach ($addresses as $address) {
            $full_delivery_address = $address['full_address'] . ", " . $address['city'] . ", " . $address['postal_code'] . ", " . $address['country'];
            $deliveryFee = 0.00; // Default to 0 if calculation fails

            error_log("get_user_addresses.php: Processing address_id " . $address['address_id'] . ", Full Address: " . $full_delivery_address);

            if ($storeCoords) { // Only attempt distance calculation if store coordinates are valid
                $userAddressCoords = geocodeAddress($full_delivery_address, $googleApiKey);
                if ($userAddressCoords) {
                    $distance = getDistanceBetweenPoints($storeCoords, $userAddressCoords, $googleApiKey);
                    if ($distance !== null) {
                        $deliveryFee = calculateDeliveryFee($distance);
                        error_log("get_user_addresses.php: Calculated fee for address_id " . $address['address_id'] . " (Distance: " . number_format($distance, 2) . "km): LKR " . number_format($deliveryFee, 2));
                    } else {
                        error_log("get_user_addresses.php: Failed to get distance for address_id " . $address['address_id'] . ". Distance Matrix API issue or route not found.");
                    }
                } else {
                    error_log("get_user_addresses.php: Failed to geocode user address for address_id " . $address['address_id'] . ": '" . $full_delivery_address . "'. Geocoding API issue or bad address format.");
                }
            } else {
                error_log("get_user_addresses.php: Skipping distance calculation for address_id " . $address['address_id'] . " because store coordinates are missing/invalid.");
            }

            // Add the calculated delivery_fee to the address array
            $address['delivery_fee'] = (string)number_format($deliveryFee, 2, '.', ''); // Ensure 2 decimal places and string for BigDecimal
            error_log("get_user_addresses.php: Final delivery_fee for address_id " . $address['address_id'] . ": " . $address['delivery_fee']);
            $processedAddresses[] = $address;
        }

        error_log("get_user_addresses.php: Final list of processed addresses count: " . count($processedAddresses));
        error_log("get_user_addresses.php: Sending JSON response: " . json_encode($processedAddresses));

        echo json_encode($processedAddresses);

    } catch (PDOException $e) {
        http_response_code(500); // Internal Server Error
        $response['success'] = false;
        $response['message'] = "Error fetching addresses: " . $e->getMessage();
        error_log("get_user_addresses.php: PDO Error in main block: " . $e->getMessage());
        echo json_encode($response);
    } catch (Exception $e) { // Catch any general exceptions from geocoding/distance functions
        http_response_code(500);
        $response['success'] = false;
        $response['message'] = "Application Error: " . $e->getMessage();
        error_log("get_user_addresses.php: General Application Error: " . $e->getMessage());
        echo json_encode($response);
    }
} else {
    http_response_code(400); // Bad Request
    $response['success'] = false;
    $response['message'] = "User ID not provided.";
    error_log("get_user_addresses.php: User ID not provided in GET request.");
    echo json_encode($response);
}
// PDO connection closes automatically when script ends
?>
