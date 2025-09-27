<?php
require 'db.php';
session_start();
$user_id = $_SESSION['user_id'] ?? 0;
if (!$user_id) exit('Not logged in');
// Collect fields as per BusinessProfiles table
$business_name = $_POST['business_name'] ?? '';
$email_address = $_POST['email_address'] ?? '';
$logo = null;
if (!empty($_FILES['logo']['tmp_name'])) {
  $dest = 'uploads/' . uniqid() . '_' . basename($_FILES['logo']['name']);
  move_uploaded_file($_FILES['logo']['tmp_name'], $dest);
  $logo = $dest;
}
$contact_number = $_POST['contact_number'] ?? '';
$address = $_POST['address'] ?? '';
$bio_description = $_POST['bio_description'] ?? '';
$website_url = $_POST['website_url'] ?? '';
$operating_hours_days = $_POST['operating_hours_days'] ?? '';
// ...add the rest

// Upsert logic
$stmt = $pdo->prepare("SELECT business_profile_id FROM BusinessProfiles WHERE user_id=?");
$stmt->execute([$user_id]);
$row = $stmt->fetch();
if ($row) {
  // Update
  $sql = "UPDATE BusinessProfiles SET business_name=?, email_address=?, contact_number=?, address=?, bio_description=?, website_url=?, operating_hours_days=?";
  $params = [$business_name, $email_address, $contact_number, $address, $bio_description, $website_url, $operating_hours_days];
  if ($logo) { $sql .= ", logo=?"; $params[] = $logo; }
  $sql .= " WHERE user_id=?";
  $params[] = $user_id;
  $stmt = $pdo->prepare($sql);
  $stmt->execute($params);
  echo "updated";
} else {
  // Insert
  $stmt = $pdo->prepare("INSERT INTO BusinessProfiles (user_id, business_name, email_address, contact_number, address, bio_description, website_url, operating_hours_days, logo) VALUES (?,?,?,?,?,?,?,?,?)");
  $stmt->execute([$user_id, $business_name, $email_address, $contact_number, $address, $bio_description, $website_url, $operating_hours_days, $logo]);
  echo "created";
}