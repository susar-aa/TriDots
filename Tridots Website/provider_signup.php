<?php
// Handle form submission
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = $_POST['username'];
    $email = $_POST['email'];
    $password = password_hash($_POST['password'], PASSWORD_BCRYPT); // Hash the password
    $providerName = $_POST['provider_name'];
    $serviceCategory = $_POST['service_category'];
    $experienceYears = $_POST['experience_years'] ?? 0;

    // Database connection
    $db_host = 'localhost:3306';
    $db_name = 'tridots';
    $db_user = 'tridots';
    $db_pass = 'tridots12369';

    try {
        $pdo = new PDO("mysql:host=$db_host;dbname=$db_name", $db_user, $db_pass);
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        // Insert provider data
        $stmt = $pdo->prepare("INSERT INTO Providers (username, email_address, password_hash, name, service_category_id, experience_years) VALUES (?, ?, ?, ?, ?, ?)");
        $stmt->execute([$username, $email, $password, $providerName, $serviceCategory, $experienceYears]);

        echo "Provider registration successful!";
    } catch (PDOException $e) {
        echo "Error: " . $e->getMessage();
    }
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Provider Signup</title>
</head>
<body>
  <h1>Provider Signup</h1>
  <form method="POST" action="">
    <label for="username">Username:</label>
    <input type="text" id="username" name="username" required><br>

    <label for="email">Email:</label>
    <input type="email" id="email" name="email" required><br>

    <label for="password">Password:</label>
    <input type="password" id="password" name="password" required><br>

    <label for="provider_name">Provider Name:</label>
    <input type="text" id="provider_name" name="provider_name" required><br>

    <label for="service_category">Service Category:</label>
    <input type="text" id="service_category" name="service_category" required><br>

    <label for="experience_years">Years of Experience:</label>
    <input type="number" id="experience_years" name="experience_years"><br>

    <button type="submit">Register</button>
  </form>
</body>
</html>