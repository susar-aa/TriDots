<?php
require 'db.php';
session_start();
$user_id = $_SESSION['user_id'] ?? 0;
if (!$user_id) exit('Not logged in');

$seller_id = $_POST['seller_id'] ?? null; // for update
$name = $_POST['name'] ?? '';
$service_category_id = $_POST['service_category_id'] ?? '';
$description = $_POST['description'] ?? '';
$contact_number = $_POST['contact_number'] ?? '';
$email_address = $_POST['email_address'] ?? '';
$address = $_POST['address'] ?? '';
$experience_years = $_POST['experience_years'] ?? null;
$qualifications = $_POST['qualifications'] ?? '';
$location = $_POST['location'] ?? '';
$availability_status = $_POST['availability_status'] ?? 'Available';
$profile_picture = null;

// Handle file upload
if (!empty($_FILES['profile_picture']['tmp_name'])) {
  $dest = 'uploads/' . uniqid() . '_' . basename($_FILES['profile_picture']['name']);
  move_uploaded_file($_FILES['profile_picture']['tmp_name'], $dest);
  $profile_picture = $dest;
}

if ($seller_id) {
  // Update
  $sql = "UPDATE Service_Providers SET name=?, service_category_id=?, description=?, contact_number=?, email_address=?, address=?, experience_years=?, qualifications=?, location=?, availability_status=?";
  $params = [$name, $service_category_id, $description, $contact_number, $email_address, $address, $experience_years, $qualifications, $location, $availability_status];
  if ($profile_picture) {
    $sql .= ", profile_picture=?";
    $params[] = $profile_picture;
  }
  $sql .= " WHERE seller_id=? AND User_id=?";
  $params[] = $seller_id; $params[] = $user_id;
  $stmt = $pdo->prepare($sql);
  $stmt->execute($params);
  echo "updated";
} else {
  // Insert
  $stmt = $pdo->prepare("INSERT INTO Service_Providers (User_id, name, service_category_id, description, contact_number, email_address, address, experience_years, qualifications, location, availability_status, profile_picture) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
  $stmt->execute([$user_id, $name, $service_category_id, $description, $contact_number, $email_address, $address, $experience_years, $qualifications, $location, $availability_status, $profile_picture]);
  echo "created";
}
