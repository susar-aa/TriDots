<?php
// DeliveryFeeCalculator.php
// This script calculates delivery fee based on distance using Google Maps APIs.

// --- IMPORTANT: Enable error reporting for debugging (REMOVE IN PRODUCTION) ---
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);
// ----------------------------------------------------------------------------

// --- Google Maps API Key ---
// !!! IMPORTANT: Replace with your actual Google Maps API Key !!!
// In production, store this securely (e.g., environment variable, not directly in file)
global $googleApiKey; // Declare as global
$googleApiKey = 'AIzaSyDyy0MvD1ipA3Opo_dZSpELLgjUwW3780M';

// --- Store Location (Origin for delivery) ---
// This should ideally come from your database (e.g., from a 'Stores' table)
// For this example, we'll hardcode it to a central point in Sri Lanka or a hypothetical store.
global $store_address; // Declare as global
$store_address = "Colombo, Sri Lanka"; // Example store location. Use actual address or LatLng.
// Or if you have coordinates for your store:
// global $store_lat, $store_lng;
// $store_lat = 6.9271;
// $store_lng = 79.8612;

/**
 * Function to geocode an address into latitude and longitude using Google Geocoding API.
 * @param string $address The address string to geocode.
 * @param string $apiKey Your Google Maps API Key.
 * @return array|null An associative array with 'lat' and 'lng' on success, null on failure.
 */
function geocodeAddress($address, $apiKey) {
    error_log("DeliveryFeeCalculator: Geocoding address: " . $address);
    $url = "https://maps.googleapis.com/maps/api/geocode/json?address=" . urlencode($address) . "&key=" . $apiKey;
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_TIMEOUT, 10); // 10 seconds timeout
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true); // Ensure SSL verification is on
    $response = curl_exec($ch);
    $http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $curl_errno = curl_errno($ch);
    $curl_error = curl_error($ch);
    curl_close($ch);

    if ($response === false) {
        error_log("DeliveryFeeCalculator: cURL error geocoding: " . $curl_error . " (Error No: " . $curl_errno . ")");
        return null;
    }

    $data = json_decode($response, true);
    error_log("DeliveryFeeCalculator: Geocoding response for '" . $address . "' (HTTP " . $http_code . "): " . $response);


    if ($data && $data['status'] == 'OK' && !empty($data['results'])) {
        $location = $data['results'][0]['geometry']['location'];
        error_log("DeliveryFeeCalculator: Geocoded to Lat: " . $location['lat'] . ", Lng: " . $location['lng']);
        return ['lat' => $location['lat'], 'lng' => $location['lng']];
    }
    error_log("DeliveryFeeCalculator: Geocoding failed for address: " . $address . " Status: " . ($data['status'] ?? 'UNKNOWN') . " Error Message: " . ($data['error_message'] ?? 'None') . " Raw Response: " . $response);
    return null;
}

/**
 * Function to calculate distance between two points using Google Distance Matrix API.
 * @param array $originCoords Associative array with 'lat' and 'lng' for the origin.
 * @param array $destinationCoords Associative array with 'lat' and 'lng' for the destination.
 * @param string $apiKey Your Google Maps API Key.
 * @return float|null Distance in kilometers on success, null on failure.
 */
function getDistanceBetweenPoints($originCoords, $destinationCoords, $apiKey) {
    $origins = $originCoords['lat'] . "," . $originCoords['lng'];
    $destinations = $destinationCoords['lat'] . "," . $destinationCoords['lng'];
    error_log("DeliveryFeeCalculator: Calculating distance from " . $origins . " to " . $destinations);

    $url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" . $origins . "&destinations=" . $destinations . "&units=metric&key=" . $apiKey;

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_TIMEOUT, 10); // 10 seconds timeout
    curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true); // Ensure SSL verification is on
    $response = curl_exec($ch);
    $http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $curl_errno = curl_errno($ch);
    $curl_error = curl_error($ch);
    curl_close($ch);

    if ($response === false) {
        error_log("DeliveryFeeCalculator: cURL error distance matrix: " . $curl_error . " (Error No: " . $curl_errno . ")");
        return null;
    }

    $data = json_decode($response, true);
    error_log("DeliveryFeeCalculator: Distance Matrix response for " . $origins . " to " . $destinations . " (HTTP " . $http_code . "): " . $response);

    if ($data && $data['status'] == 'OK' && !empty($data['rows'][0]['elements'][0]['distance'])) {
        $element = $data['rows'][0]['elements'][0];
        if ($element['status'] == 'OK') {
            $distanceMeters = $element['distance']['value']; // distance in meters
            error_log("DeliveryFeeCalculator: Distance found: " . $distanceMeters . " meters");
            return (float)($distanceMeters / 1000); // convert to kilometers
        } else {
            error_log("DeliveryFeeCalculator: Distance Matrix Element Status not OK: " . ($element['status'] ?? 'UNKNOWN') . " Message: " . ($element['fare']['text'] ?? 'None') );
        }
    }
    error_log("DeliveryFeeCalculator: Distance Matrix failed. Status: " . ($data['status'] ?? 'UNKNOWN') . " Error Message: " . ($data['error_message'] ?? 'None') . " Raw Response: " . $response);
    return null;
}

/**
 * Calculates the delivery fee based on distance.
 * @param float $distanceKm Distance in kilometers.
 * @return float The calculated delivery fee.
 */
function calculateDeliveryFee($distanceKm) {
    // --- Your Delivery Fee Business Logic ---
    error_log("DeliveryFeeCalculator: Calculating fee for distance: " . $distanceKm . " km");
    if ($distanceKm <= 5) {
        return 100.00; // Flat fee for short distances
    } elseif ($distanceKm <= 10) {
        return 150.00;
    } elseif ($distanceKm <= 20) {
        return 250.00;
    } else {
        return 400.00; // Max fee for longer distances
    }
}
?>
