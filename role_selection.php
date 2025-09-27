<?php
// Handle form submission if POST request is received
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $selectedRole = $_POST['selected_role'] ?? '';

    // Redirect to the appropriate signup form based on the selected role
    if ($selectedRole === 'personal') {
        header('Location: personal_signup.php');
        exit;
    } elseif ($selectedRole === 'business') {
        header('Location: business_signup.php');
        exit;
    } else {
        echo "Invalid selection. Please try again.";
        exit;
    }
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Select Account Type</title>
  <style>
    body {
      font-family: 'Arial', sans-serif;
      margin: 0;
      padding: 0;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100vh;
      background: linear-gradient(135deg, #00715A, #00B07D);
      color: white;
    }

    h1 {
      font-size: 1.8rem;
      margin-bottom: 20px;
    }

    .role-container {
      display: flex;
      gap: 20px;
      justify-content: center;
      flex-wrap: wrap;
    }

    .role-box {
      width: 180px;
      height: 180px;
      background: white;
      border-radius: 15px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      cursor: pointer;
      transition: transform 0.3s, box-shadow 0.3s;
      text-align: center;
      color: #00715A;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
    }

    .role-box:hover {
      transform: translateY(-10px);
      box-shadow: 0 6px 15px rgba(0, 0, 0, 0.3);
    }

    .role-box.selected {
      border: 3px solid #00B07D;
      color: #00B07D;
    }

    .role-box img {
      width: 70px;
      height: 70px;
      margin-bottom: 10px;
    }

    .role-box label {
      font-size: 1.1rem;
      font-weight: bold;
    }

    .button-container {
      margin-top: 30px;
      text-align: center;
    }

    button {
      padding: 12px 30px;
      background-color: #00B07D;
      color: white;
      font-size: 1rem;
      font-weight: bold;
      border: none;
      border-radius: 8px;
      cursor: pointer;
      transition: background-color 0.3s;
    }

    button:hover {
      background-color: #00715A;
    }
  </style>
</head>
<body>
  <h1>Please Select Your Account Type</h1>
  <form method="POST" action="">
    <div class="role-container">
      <div class="role-box" data-role="personal" onclick="selectRole('personal')">
        <img src="personal-icon.png" alt="Personal Icon">
        <label>Personal</label>
      </div>
      <div class="role-box" data-role="business" onclick="selectRole('business')">
        <img src="business-icon.png" alt="Business Icon">
        <label>Business</label>
      </div>
    </div>
    <input type="hidden" name="selected_role" id="selected-role">
    <div class="button-container">
      <button type="submit">Continue</button>
    </div>
  </form>

  <script>
    function selectRole(role) {
      const personalBox = document.querySelector(`.role-box[data-role="personal"]`);
      const businessBox = document.querySelector(`.role-box[data-role="business"]`);

      // Reset all selections
      personalBox.classList.remove('selected');
      businessBox.classList.remove('selected');

      // Apply selection rules
      if (role === "personal") {
        personalBox.classList.add('selected');
        document.getElementById('selected-role').value = 'personal';
      } else if (role === "business") {
        businessBox.classList.add('selected');
        document.getElementById('selected-role').value = 'business';
      }
    }
  </script>
</body>
</html>