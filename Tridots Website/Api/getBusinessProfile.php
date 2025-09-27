<?php
 header('Content-Type: application/json');

 $host = 'localhost:3306';
 $dbname = 'tridots';
 $username = 'tridots';
 $password = 'tridots12369';

 try {
     $pdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $username, $password);
     $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
 } catch (PDOException $e) {
     die(json_encode(array('status' => 'error', 'message' => 'Database connection failed: ' . $e->getMessage())));
 }

 if (isset($_POST['user_id'])) {
     $user_id = $_POST['user_id'];

     $stmt = $pdo->prepare("SELECT * FROM BusinessProfiles WHERE user_id = :user_id");
     $stmt->bindParam(':user_id', $user_id, PDO::PARAM_INT);
     $stmt->execute();

     if ($stmt->rowCount() > 0) {
         $businessProfile = $stmt->fetch(PDO::FETCH_ASSOC);
         echo json_encode(array('status' => 'success', 'business_profile' => $businessProfile));
     } else {
         echo json_encode(array('status' => 'success', 'business_profile' => array())); // Return empty array if no business profile
     }
 } else {
     echo json_encode(array('status' => 'error', 'message' => 'User ID not provided.'));
 }

 $pdo = null;
 ?>