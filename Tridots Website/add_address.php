<?php
// =================================================================
// ADD ADDRESS SCRIPT (TRIDOTS API PROXY) - V5 (Hybrid Fallback)
// Uses cURL if available for better debugging, otherwise falls back
// to file_get_contents. Guarantees a JSON response.
// =================================================================

// Force clean output for JSON by suppressing any PHP warnings/notices.
error_reporting(0);
ini_set('display_errors', 0);

session_start();
header('Content-Type: application/json');

// --- VALIDATION & SETUP ---
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['success' => false, 'message' => 'Invalid request method.']);
    exit;
}

$userId = $_SESSION['user_data']['user_id'] ?? null;
if (!$userId) {
    echo json_encode(['success' => false, 'message' => 'User not authenticated. Please sign in again.']);
    exit;
}

// Collect and sanitize POST data
$params = [
    'user_id' => $userId,
    'recipient_name' => filter_input(INPUT_POST, 'recipient_name', FILTER_SANITIZE_STRING),
    'contact_phone' => filter_input(INPUT_POST, 'contact_phone', FILTER_SANITIZE_STRING),
    'full_address' => filter_input(INPUT_POST, 'full_address', FILTER_SANITIZE_STRING),
    'city' => filter_input(INPUT_POST, 'city', FILTER_SANITIZE_STRING),
    'postal_code' => filter_input(INPUT_POST, 'postal_code', FILTER_SANITIZE_STRING)
];

if (empty($params['recipient_name']) || empty($params['full_address']) || empty($params['city'])) {
    echo json_encode(['success' => false, 'message' => 'Missing required address fields.']);
    exit;
}

// --- API CALL ---
$api_url = "https://lionsgoldencircle.com/Tridots/Api/add_user_address.php";
$response = null;
$http_status = 0;

// Check if cURL extension is available
if (function_exists('curl_init')) {
    // Use cURL for more robust requests and better error info
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $api_url);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($params));
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 10);
    curl_setopt($ch, CURLOPT_TIMEOUT, 10);

    $response = curl_exec($ch);
    $http_status = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $curl_error = curl_error($ch);
    curl_close($ch);

    if ($response === false) {
        error_log("cURL Error calling address API: " . $curl_error);
        echo json_encode(['success' => false, 'message' => 'Failed to connect to the server. cURL error: ' . $curl_error]);
        exit;
    }
} else {
    // Fallback to file_get_contents if cURL is not available
    $options = ['http' => [
        'header'  => "Content-type: application/x-www-form-urlencoded\r\n",
        'method'  => 'POST',
        'content' => http_build_query($params),
        'timeout' => 10,
        'ignore_errors' => true // Get content even on 4xx/5xx errors
    ]];
    $context  = stream_context_create($options);
    $response = @file_get_contents($api_url, false, $context);
    
    // Manually check status from headers if possible
    if (isset($http_response_header)) {
        sscanf($http_response_header[0], 'HTTP/%*d.%*d %d', $http_status);
    }

    if ($response === false) {
        echo json_encode(['success' => false, 'message' => 'Could not connect to the address API using file_get_contents.']);
        exit;
    }
}

// --- UNIVERSAL RESPONSE HANDLING ---
json_decode($response);
if (json_last_error() !== JSON_ERROR_NONE) {
    // This is the critical check. If the response is not JSON, it's an error.
    error_log("API Error (HTTP Status: {$http_status}): The endpoint at {$api_url} returned a non-JSON response. Full response: " . $response);
    
    echo json_encode([
        'success' => false,
        'message' => "The server's final API endpoint failed. Please check the server error logs for details. (HTTP Status: {$http_status})",
        'debug_status' => $http_status
    ]);
    exit;
}

// If we reach here, the response was valid JSON.
echo $response;

?>
