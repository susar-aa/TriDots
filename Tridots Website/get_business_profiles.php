<?php
require_once 'db.php';

$stmt = $pdo->query("SELECT business_profile_id, user_id, email_address, contact_number, logo, address, business_name, bio_description, website_url, whatsapp_number, instagram_link, facebook_link, rating_count, verification_status, is_active FROM BusinessProfiles WHERE is_active = 'yes' ORDER BY business_name ASC");
$profiles = $stmt->fetchAll();

header('Content-Type: application/json');
echo json_encode($profiles);
?>