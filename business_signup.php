<?php
// Handle form submission
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $businessName = $_POST['business_name'];
    $username = $_POST['username'];
    $email = $_POST['email'];
    $password = password_hash($_POST['password'], PASSWORD_BCRYPT); // Hash the password
    $contact = $_POST['contact_number'];
    $address = $_POST['address'];

    // Database connection
    $db_host = 'localhost:3306';
    $db_name = 'tridots';
    $db_user = 'tridots';
    $db_pass = 'tridots12369';

    try {
        // Establish database connection
        $pdo = new PDO("mysql:host=$db_host;dbname=$db_name", $db_user, $db_pass);
        $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

        // Check if the email already exists
        $stmt = $pdo->prepare("SELECT COUNT(*) FROM Users WHERE email_address = ?");
        $stmt->execute([$email]);
        $emailExists = $stmt->fetchColumn();

        if ($emailExists) {
            echo "<script>
                alert('The email address is already registered. Please use another email or sign in.');
                window.location.href = 'login.php';
            </script>";
            exit;
        }

        // Insert into Users table
        $stmt = $pdo->prepare("INSERT INTO Users (username, email_address, password_hash, contact_number, user_type, address, verification_status, is_active)
                               VALUES (?, ?, ?, ?, 'Business', ?, 'Not Verified', 'Deactive')");
        $stmt->execute([$username, $email, $password, $contact, $address]);

        // Insert into Businesses table
        $businessId = $pdo->lastInsertId(); // Get the last inserted user_id
        $stmt = $pdo->prepare("INSERT INTO Businesses (user_id, business_name) VALUES (?, ?)");
        $stmt->execute([$businessId, $businessName]);

        echo "<script>
            alert('Congratulations! Your business account registration was successful. Your account will be activated shortly.');
            window.location.href = 'login.php';
        </script>";
        exit;
    } catch (PDOException $e) {
        // Log error for debugging (remove in production)
        error_log('Database Error: ' . $e->getMessage());

        // Display a user-friendly error message
        echo "<script>
            alert('An error occurred during registration. Please try again later.');
        </script>";
    }
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Business Signup</title>
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

    .signup-container .form-group input,
    .signup-container .form-group textarea {
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
    <h1>Business Signup</h1>
    <form method="POST" action="">
      <div class="form-group">
        <label for="business_name">Business Name:</label>
        <input type="text" id="business_name" name="business_name" placeholder="Enter your business name" required>
      </div>
      <div class="form-group">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" placeholder="Enter your username" required>
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
      <div class="form-group">
        <label for="address">Address:</label>
        <textarea id="address" name="address" placeholder="Enter your business address"></textarea>
      </div>
      <button type="submit">Register</button>
    </form>
    <div class="footer">
      Already have an account? <a href="login.php">Login here</a>
    </div>
  </div>
</body>
</html>