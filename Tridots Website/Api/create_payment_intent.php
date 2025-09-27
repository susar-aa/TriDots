<?php
// Set content type to JSON
header('Content-Type: application/json');
// Allow cross-origin requests for development. In production, restrict this to your app's domain(s).
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization'); // Include Authorization if you plan to use API keys

// Handle OPTIONS request for CORS preflight
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Composer's autoload file handles loading Stripe library classes
require_once __DIR__ . '/vendor/autoload.php'; // Adjust path if vendor folder is not in the same directory

// !!! IMPORTANT: Replace with your actual Stripe Secret Key !!!
// For production, load this from environment variables (e.g., getenv('STRIPE_SECRET_KEY'))
\Stripe\Stripe::setApiKey('sk_test_51RcpEUQtOPR6g7JfG3vtB8nAUEu21rLSQyaoWP1dr7zNJnwHWGqUHsQwOwujoFpDcZyvEaf7WDSSPUub7OIFxB0C00ZD1vLvhI');

// Read the raw POST data (JSON sent from Android)
$input = file_get_contents('php://input');
$data = json_decode($input, true);

// Validate input data
if (!isset($data['amount']) || !isset($data['currency']) || !isset($data['description'])) {
    http_response_code(400); // Bad Request
    echo json_encode(['status' => 'error', 'message' => 'Missing required payment intent parameters (amount, currency, description).']);
    exit;
}

$amount = $data['amount']; // Amount in smallest currency unit (e.g., 1000 for LKR 10.00, or 100 for USD 1.00)
$currency = strtolower($data['currency']); // Stripe expects lowercase currency code (e.g., 'lkr', 'usd')
$description = $data['description'];
$shipping = isset($data['shipping']) ? $data['shipping'] : null;

// Basic validation for amount and currency
if (!is_int($amount) || $amount <= 0) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => 'Invalid amount. Must be a positive integer in the smallest currency unit.']);
    exit;
}

// Add/remove currencies as per your business needs. Stripe supports many.
// Ensure your Stripe account is configured for LKR if that's your primary currency.
if (!in_array($currency, ['lkr', 'usd', 'eur', 'gbp', 'aud'])) {
    http_response_code(400);
    echo json_encode(['status' => 'error', 'message' => "Unsupported currency: {$currency}."]);
    exit;
}

try {
    $params = [
        'amount' => $amount,
        'currency' => $currency,
        'description' => $description,
        'automatic_payment_methods' => ['enabled' => true], // Recommended for modern Stripe integration
        // Add more parameters as needed, e.g., 'setup_future_usage' => 'off_session' for recurring payments
    ];

    // Add shipping details if provided and valid
    if ($shipping && is_array($shipping) &&
        isset($shipping['name']) && isset($shipping['address']) && is_array($shipping['address']) &&
        isset($shipping['address']['line1']) && isset($shipping['address']['city']) &&
        isset($shipping['address']['postal_code'])) {

        // Ensure country is present, default to LK if not
        if (!isset($shipping['address']['country'])) {
             $shipping['address']['country'] = 'LK'; // Default to Sri Lanka if not provided by app
        }
        $params['shipping'] = $shipping;
    } else {
        // Log or handle case where shipping data is incomplete if it's mandatory
        // For now, simply exclude it if incomplete
        error_log("Warning: Incomplete shipping data provided for PaymentIntent.");
    }


    $paymentIntent = \Stripe\PaymentIntent::create($params);

    http_response_code(200); // OK
    echo json_encode([
        'status' => 'success',
        'client_secret' => $paymentIntent->client_secret, // This is what your Android app needs
        'payment_intent_id' => $paymentIntent->id,
        'amount_charged' => $paymentIntent->amount,
        'currency' => $paymentIntent->currency
    ]);

} catch (\Stripe\Exception\ApiErrorException $e) {
    // Display error to the user
    http_response_code(400); // Usually 400 for client-side errors like invalid card, 500 for Stripe server issues
    echo json_encode([
        'status' => 'error',
        'message' => $e->getMessage(),
        'stripe_code' => $e->getStripeCode(),
        'http_status' => $e->getHttpStatus()
    ]);
    error_log("Stripe API Error: " . $e->getMessage());
} catch (\Exception $e) {
    // Catch any other unexpected errors
    http_response_code(500); // Internal Server Error
    echo json_encode(['status' => 'error', 'message' => 'An unexpected server error occurred: ' . $e->getMessage()]);
    error_log("General Error: " . $e->getMessage());
}
?>