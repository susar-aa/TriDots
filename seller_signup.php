<?php
// Handle form submission
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = $_POST['username'];
    $email = $_POST['email'];
    $password = password_hash($_POST['password'], PASSWORD_BCRYPT); // Hash the password
    $sellerName = $_POST['seller_name'];
    $nicNumber = $_POST['nic_number'] ?? '';
    $bankAccount = $_POST['bank_account_number'] ?? '';

    // Database connection
    $db_host = 'localhost:3306';
    $db_name = 'tridots';
    $db_user = 'tridots';
    $db_pass = 'tridots12369';

    try {
        $pdo = new PDO("mysql:host=$db_host;dbname=$db_name", $db_user, $db_pass);
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        // Insert seller data
        $stmt = $pdo->prepare("INSERT INTO Sellers (username, email_address, password_hash, seller_name, nic_number, bank_account_number) VALUES (?, ?, ?, ?, ?, ?)");
        $stmt->execute([$username, $email, $password, $sellerName, $nicNumber, $bankAccount]);

        echo "Seller registration successful!";
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
  <title>Seller Signup</title>
</head>
<body>
  <h1>Seller Signup</h1>
  <form method="POST" action="">
    <label for="username">Username:</label>
    <input type="text" id="username" name="username" required><br>

    <label for="email">Email:</label>
    <input type="email" id="email" name="email" required><br>

    <label for="password">Password:</label>
    <input type="password" id="password" name="password" required><br>

    <label for="seller_name">Seller Name:</label>
    <input type="text" id="seller_name" name="seller_name" required><br>

    <label for="nic_number">NIC Number:</label>
    <input type="text" id="nic_number" name="nic_number"><br>

    <label for="bank_account_number">Bank Account Number:</label>
    <input type="text" id="bank_account_number" name="bank_account_number"><br>

    <button type="submit">Register</button>
  </form>
</body>
</html>