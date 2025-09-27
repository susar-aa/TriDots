<?php
session_start();

// Check if user is signed in
$userSignedIn = isset($_SESSION['user_id']);
$userData = null;
if ($userSignedIn) {
    $userData = [
        'id'    => $_SESSION['user_data']['user_id'] ?? null,
        'name'  => $_SESSION['user_data']['full_name'] ?? 'Logged In User',
        'email' => $_SESSION['user_data']['email'] ?? null,
        'phone' => $_SESSION['user_data']['phone'] ?? null
    ];
}

// Base URLs
$api_base_url = "https://lionsgoldencircle.com/Tridots/Api/";
$image_base_url = "https://lionsgoldencircle.com/";

// Fetch data helper
function fetchData($url) {
    $json_data = @file_get_contents($url);
    if ($json_data === false) {
        return [];
    }
    $data = json_decode($json_data, true);
    return is_array($data) ? $data : [];
}

// Image resolver with clarified path
function getFirstImage($imageString, $baseUrl, $defaultFolder = 'Tridots/images/Ads/') {
    if (empty($imageString)) return null;
    $images = explode(',', $imageString);
    $firstImage = trim($images[0]);

    // If it's already a valid URL
    if (filter_var($firstImage, FILTER_VALIDATE_URL)) {
        return $firstImage;
    }

    // Prepend default folder if not an absolute path
    return $baseUrl . rtrim($defaultFolder, '/') . '/' . ltrim($firstImage, '/');
}

// Renders a card
function renderAdvertCard($details) {
    $link = $details['link'] ?? '#';
    $imageUrl = $details['imageUrl'] ?? 'https://placehold.co/280x160/eee/555?text=No+Image';
    $altText = htmlspecialchars($details['altText'] ?? 'Advert Image');
    $title = htmlspecialchars($details['title'] ?? 'N/A');
    $location = htmlspecialchars($details['location'] ?? 'N/A');
    $priceLine = $details['priceLine'] ?? '';
    $placeholderIcon = $details['placeholderIcon'] ?? 'image-outline';
    $onErrorJs = "this.onerror=null; this.parentElement.innerHTML = `<div class='image-placeholder'><ion-icon name='{$placeholderIcon}'></ion-icon><span>Failed to Load</span>`;";

    echo "<a href='{$link}' style='text-decoration: none; color: inherit;'>
            <div class='item-card'>
                <div class='item-image'>
                    <img src='{$imageUrl}' alt='{$altText}' onerror=\"{$onErrorJs}\">
                </div>
                <div class='item-details'>
                    <h3 class='item-title'>{$title}</h3>
                    <p class='item-location'>{$location}</p>
                    {$priceLine}
                </div>
            </div>
          </a>";
}

// Fetch data
$banners = fetchData($api_base_url . 'fetch_banners.php');
$rentingAds = fetchData($api_base_url . 'fetch_renting_ads.php');
$vehicles = fetchData($api_base_url . 'fetch_vehicles.php');
$serviceProviders = fetchData($api_base_url . 'fetch_service_providers.php');
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Marketplace Dashboard – TRIDOTS</title>
    <link rel="stylesheet" href="styles.css">
    <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
    <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
    <style>
        body { font-family: 'Inter', sans-serif; margin: 0; background: #f7f8fa; }
        .container { max-width: 1200px; margin: 0 auto; padding: 0 16px; }
        .main-header { background: #00715A; color: white; padding: 16px 0; }
        .header-content { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; }
        .main-nav a { color: white; margin-left: 16px; text-decoration: none; font-weight: 500; }
        .main-nav a:hover { opacity: 0.8; }
        .btn { background: white; color: #00715A; padding: 8px 16px; border-radius: 20px; text-decoration: none; font-weight: 600; }
        .btn:hover { background: #f1f1f1; }
        .category-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 16px; margin: 24px 0; }
        .category-card { display: flex; align-items: center; background: #00715A; color: white; padding: 16px; border-radius: 12px; text-decoration: none; }
        .category-card ion-icon { font-size: 1.8rem; margin-right: 12px; }
        .listing-section { background: white; border-radius: 12px; padding: 16px; margin: 24px 0; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        .listing-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
        .listing-header h2 { margin: 0; }
        .item-carousel { display: flex; gap: 12px; overflow-x: auto; }
        .item-card { min-width: 240px; border: 1px solid #ddd; border-radius: 8px; background: white; display: flex; flex-direction: column; cursor: pointer; }
        .item-image { height: 140px; background: #eee; position: relative; }
        .item-image img { width: 100%; height: 100%; object-fit: cover; }
        .image-placeholder { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; color: #999; }
        .image-placeholder ion-icon { font-size: 2rem; }
        .item-details { padding: 8px; flex: 1; }
        .item-title { font-weight: 600; font-size: 1rem; margin: 0; }
        .item-location { font-size: 0.85rem; color: #666; }
        .item-price { font-weight: bold; color: #00715A; }
        .main-footer { text-align: center; font-size: 0.85rem; color: #777; padding: 16px; }
    </style>
</head>
<body>
<header class="main-header">
    <div class="container header-content">
        <a href="marketplace_dashboard.php" class="logo"><img src="https://lionsgoldencircle.com/Tridots/images/logo1.png" alt="TRIDOTS" style="height:35px;"></a>
        <nav class="main-nav">
            <a href="marketplace_dashboard.php">Dashboard</a>
            <a href="machines.php">Machines</a>
            <a href="vehicles.php">Vehicles</a>
            <a href="laborers.php">Laborers</a>
            <a href="marketplace.php">Marketplace</a>
        </nav>
        <div>
        <?php if ($userSignedIn): ?>
            <span>Hi, <?php echo htmlspecialchars($userData['name']); ?></span>
            <a href="profile.php" class="btn">Profile</a>
        <?php else: ?>
            <a href="login.php" class="btn">Sign In</a>
        <?php endif; ?>
        </div>
    </div>
</header>
<main class="container">
    <!-- Rent A Machine -->
    <section class="listing-section">
        <div class="listing-header">
            <h2>Rent A Machine</h2>
            <a href="machines.php" class="btn">More</a>
        </div>
        <div class="item-carousel">
            <?php if ($rentingAds): foreach ($rentingAds as $ad):
                renderAdvertCard([
                    'link' => $userSignedIn ? "machine_details.php?id=" . urlencode($ad['product_id']) : "login.php",
                    'imageUrl' => getFirstImage($ad['product_images'], $image_base_url, 'Tridots/images/Ads/'),
                    'altText' => $ad['product_name'],
                    'title' => $ad['product_name'],
                    'location' => $ad['product_location'],
                    'priceLine' => '<p class="item-price">Rs. ' . number_format((float)$ad['price_per_day'], 2) . ' / Day</p>'
                ]);
            endforeach; else: ?>
            <p>No machines available right now.</p>
            <?php endif; ?>
        </div>
    </section>

    <!-- Hire A Vehicle -->
    <section class="listing-section">
        <div class="listing-header">
            <h2>Hire A Vehicle</h2>
            <a href="vehicles.php" class="btn">More</a>
        </div>
        <div class="item-carousel">
            <?php if ($vehicles): foreach ($vehicles as $vehicle):
                renderAdvertCard([
                    'link' => $userSignedIn ? "vehicle_details.php?id=" . urlencode($vehicle['vehicle_id']) : "login.php",
                    'imageUrl' => getFirstImage($vehicle['vehicle_images'], $image_base_url, 'Tridots/images/Ads/'),
                    'altText' => $vehicle['vehicle_name'],
                    'title' => $vehicle['vehicle_name'],
                    'location' => $vehicle['location'],
                    'priceLine' => '<p class="item-price">Rs. ' . number_format((float)$vehicle['amount'], 2) . ' / ' . htmlspecialchars($vehicle['price_type']) . '</p>'
                ]);
            endforeach; else: ?>
            <p>No vehicles available.</p>
            <?php endif; ?>
        </div>
    </section>

    <!-- Verified Service Providers -->
    <section class="listing-section">
        <div class="listing-header">
            <h2>Verified Service Providers</h2>
            <a href="laborers.php" class="btn">More</a>
        </div>
        <div class="item-carousel">
            <?php if ($serviceProviders): foreach ($serviceProviders as $provider):
                renderAdvertCard([
                    'link' => $userSignedIn ? "provider_details.php?id=" . urlencode($provider['user_id']) : "login.php",
                    'imageUrl' => getFirstImage($provider['profile_picture'], $image_base_url, 'Tridots/images/Ads/'),
                    'altText' => $provider['name'],
                    'title' => $provider['service_category_name'],
                    'location' => $provider['name'],
                    'placeholderIcon' => 'person-circle-outline'
                ]);
            endforeach; else: ?>
            <p>No service providers available.</p>
            <?php endif; ?>
        </div>
    </section>
</main>
<footer class="main-footer">
    &copy; <?php echo date("Y"); ?> TRIDOTS. All rights reserved.
</footer>
</body>
</html>