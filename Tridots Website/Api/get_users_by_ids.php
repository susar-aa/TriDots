<?php
header('Content-Type: application/json');

// Database connection details
$host = 'localhost'; // Often 'localhost' for PHP on the same server
$port = '3306';      // MySQL default port
$dbname = 'tridots';
$username = 'tridots';
$password = 'tridots12369';

// Construct DSN for PDO
$dsn = "mysql:host=$host;port=$port;dbname=$dbname;charset=utf8mb4";
$options = [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
    PDO::ATTR_EMULATE_PREPARES   => false,
];

$conn = null; // Initialize connection variable

try {
    $conn = new PDO($dsn, $username, $password, $options);
} catch (PDOException $e) {
    echo json_encode(['status' => 'error', 'message' => 'Database connection failed: ' . $e->getMessage()]);
    exit();
}

// Check if user_ids are provided in the POST request body
$input = file_get_contents('php://input');
$data = json_decode($input, true);

if (!isset($data['user_ids']) || !is_array($data['user_ids']) || empty($data['user_ids'])) {
    echo json_encode(['status' => 'error', 'message' => 'No user IDs provided or invalid format.']);
    exit();
}

$userIds = $data['user_ids'];

// Sanitize user IDs to ensure they are integers
$sanitizedUserIds = [];
foreach ($userIds as $id) {
    if (filter_var($id, FILTER_VALIDATE_INT) !== false) {
        $sanitizedUserIds[] = (int)$id;
    }
}

if (empty($sanitizedUserIds)) {
    echo json_encode(['status' => 'error', 'message' => 'No valid integer user IDs provided.']);
    exit();
}

// Create placeholders for the IN clause
$placeholders = implode(',', array_fill(0, count($sanitizedUserIds), '?'));

// SQL query to fetch user details
// Ensure the column names 'user_id', 'username', 'profile_picture' match your table exactly
$sql = "SELECT user_id, username, profile_picture FROM Users WHERE user_id IN ($placeholders)";

try {
    $stmt = $conn->prepare($sql);
    $stmt->execute($sanitizedUserIds);
    $users = $stmt->fetchAll();

    // Format the profile_picture URL if it's a relative path
    $baseUrl = 'https://lionsgoldencircle.com/Tridots/'; // Adjust if your base image URL is different
    $formattedUsers = [];
    foreach ($users as $user) {
        // If profile_picture is stored as a relative path, prepend the base URL
        if (!empty($user['profile_picture']) && !filter_var($user['profile_picture'], FILTER_VALIDATE_URL)) {
            $user['profile_picture'] = $baseUrl . $user['profile_picture'];
        }
        $formattedUsers[] = $user;
    }

    echo json_encode(['status' => 'success', 'users' => $formattedUsers]);

} catch (PDOException $e) {
    echo json_encode(['status' => 'error', 'message' => 'Query failed: ' . $e->getMessage()]);
} finally {
    if ($conn) {
        $conn = null; // Close the database connection
    }
}
?>