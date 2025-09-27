<?php
// Function to convert UUID to binary
function uuidToBin($uuid) {
    return pack('H*', str_replace('-', '', $uuid));
}

// Handle form submission
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $username = $_POST['username'];
    $nicNumber = $_POST['nic_number'];
    $email = $_POST['email'];
    $password = password_hash($_POST['password_hash'], PASSWORD_BCRYPT); // Hash the password
    $contact = $_POST['contact_number'];

    // Database connection
    $db_host = 'localhost:3306';
    $db_name = 'tridots';
    $db_user = 'tridots';
    $db_pass = 'tridots12369';

    try {
        // Establish database connection
        $pdo = new PDO("mysql:host=$db_host;dbname=$db_name", $db_user, $db_pass);
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        // Generate UUID in PHP
        $uuid = uuidToBin(bin2hex(random_bytes(16))); // Using random_bytes and pack for binary UUID

        // Insert into Users table
        $stmt = $pdo->prepare("INSERT INTO Users (user_id, username, email_address, password_hash, user_type, contact_number, nic_number, is_active)
                               VALUES (?, ?, ?, ?, 'Buyer', ?, ?, 1)");
        $stmt->execute([$uuid, $username, $email, $password, $contact, $nicNumber]);

        // Insert into Buyers table
        $stmt = $pdo->prepare("INSERT INTO Buyers (user_id) VALUES (?)");
        $stmt->execute([$uuid]);

        echo "Buyer registration successful!";
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
  <title>Buyer Signup</title>
  <style>
    body {
      font-family: 'Arial', sans-serif;
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100vh;
      margin: 0;
      background: linear-gradient(135deg, #00715A, #00B07D);
      color: #fff;
    }

    .signup-container {
      width: 100%;
      max-width: 400px;
      background: #fff;
      color: #333;
      padding: 20px;
      border-radius: 10px;
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
    }

    .signup-container h1 {
      font-size: 1.8rem;
      margin-bottom: 20px;
      text-align: center;
      color: #00715A;
    }

    .signup-container .form-group {
      margin-bottom: 15px;
    }

    .signup-container .form-group label {
      display: block;
      font-weight: bold;
      margin-bottom: 5px;
    }

    .signup-container .form-group input {
      width: 100%;
      padding: 10px;
      border: 1px solid #ddd;
      border-radius: 5px;
      font-size: 1rem;
    }

    .signup-container button {
      width: 100%;
      padding: 12px;
      background-color: #00715A;
      color: #fff;
      font-size: 1rem;
      font-weight: bold;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      transition: background-color 0.3s;
    }

    .signup-container button:hover {
      background-color: #005c49;
    }

    .signup-container .footer {
      text-align: center;
      margin-top: 15px;
      font-size: 0.9rem;
      color: #666;
    }

    .signup-container .footer a {
      color: #00715A;
      text-decoration: none;
    }

    .signup-container .footer a:hover {
      text-decoration: underline;
    }
  </style>
</head>
<body>
  <div class="signup-container">
    <h1>Buyer Signup</h1>
    <form method="POST" action="">
      <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" placeholder="Enter your username" required>
      </div>
      <div class="form-group">
        <label for="nic_number">NIC Number:</label>
        <input type="text" id="nic_number" name="nic_number" placeholder="Enter your NIC number" required>
      </div>
      <div class="form-group">
        <label for="email">Email Address:</label>
        <input type="email" id="email" name="email" placeholder="Enter your email address" required>
      </div>
      <div class="form-group">
        <label for="password">Password:</label>
        <input type="password" id="password" name="password" placeholder="Enter your password" required>
      </div>
      <div class="form-group">
        <label for="contact_number">Contact Number:</label>
        <input type="text" id="contact_number" name="contact_number" placeholder="Enter your contact number" required>
      </div>
      <button type="submit">Register</button>
    </form>
    <div class="footer">
      Already have an account? <a href="login.php">Login here</a>
    </div>
  </div>
</body>
</html>