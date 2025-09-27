<?php
session_start();

// =================================================================
// CONFIGURATION
// =================================================================
$api_base_url = "https://lionsgoldencircle.com/Tridots/Api/";
$image_base_url = "https://lionsgoldencircle.com/";


// =================================================================
// USER SESSION HANDLING
// =================================================================
$userSignedIn = isset($_SESSION['user_id'], $_SESSION['user_data']);
$userData = null;

if ($userSignedIn) {
    // Populate user data from the session
    $userData = [
        'id'    => $_SESSION['user_data']['user_id'] ?? null,
        'name'  => $_SESSION['user_data']['full_name'] ?? 'User',
        'email' => $_SESSION['user_data']['email'] ?? null,
        'phone' => $_SESSION['user_data']['phone'] ?? null
    ];
}


// =================================================================
// HELPER FUNCTIONS
// =================================================================

/**
 * Fetches data from a URL and decodes the JSON response.
 * Returns an empty array on failure.
 */
function fetchData($url) {
    // Note: The '@' suppresses errors. If the API is down, this will return false.
    $json_data = @file_get_contents($url);
    if ($json_data === false) {
        return []; // API failed or is offline
    }
    $data = json_decode($json_data, true);
    return is_array($data) ? $data : [];
}

/**
 * Resolves the full URL for the first image in a comma-separated list.
 * Handles both full URLs and relative paths.
 */
function getFirstImage($imageString, $baseUrl, $defaultFolder = 'Tridots/images/Ads/') {
    if (empty($imageString)) {
        return null;
    }
    $images = explode(',', $imageString);
    $firstImage = trim($images[0]);

    // Return immediately if it's already a valid, full URL
    if (filter_var($firstImage, FILTER_VALIDATE_URL)) {
        return $firstImage;
    }

    // Otherwise, construct the path from the base URL and default folder
    return $baseUrl . rtrim($defaultFolder, '/') . '/' . ltrim($firstImage, '/');
}

/**
 * Renders a single advertisement card.
 */
function renderAdvertCard($details) {
    $link = $details['link'] ?? '#';
    $imageUrl = $details['imageUrl'] ?? 'https://placehold.co/280x160/eee/555?text=No+Image';
    $altText = htmlspecialchars($details['altText'] ?? 'Advert Image');
    $title = htmlspecialchars($details['title'] ?? 'N/A');
    $location = htmlspecialchars($details['location'] ?? 'N/A');
    $priceLine = $details['priceLine'] ?? '';
    $placeholderIcon = $details['placeholderIcon'] ?? 'image-outline';
    
    // JS to run if the image fails to load
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


// =================================================================
// DATA FETCHING (CRITICAL POINT)
// =================================================================
// The following lines are the source of the problem.
// If the APIs are offline or empty, the variables will be empty arrays.
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
        .main-header { background: #00715A; color: white; padding: 12px 0; }
        .header-content { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; }
        
        /* FIX: Added rule to control logo size */
        .logo img {
            height: 40px;
            display: block;
        }

        .main-nav a { color: white; margin-left: 24px; text-decoration: none; font-weight: 500; }
        .main-nav a:hover { opacity: 0.8; }
        .header-actions .btn { background-color: white; color: #00715A; padding: 10px 20px; border-radius: 50px; text-decoration: none; font-weight: 600; }
        .header-actions .btn:hover { background-color: #f1f1f1; }
        .listing-section { background: white; border-radius: 12px; padding: 16px; margin: 24px 0; box-shadow: 0 2px 6px rgba(0,0,0,0.1); }
        .listing-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
        .listing-header h2 { margin: 0; }
        .item-carousel { display: flex; gap: 12px; overflow-x: auto; padding-bottom: 10px; }
        .item-card { min-width: 240px; border: 1px solid #ddd; border-radius: 8px; background: white; display: flex; flex-direction: column; cursor: pointer; transition: all 0.2s ease-in-out; }
        .item-card:hover { transform: translateY(-3px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
        .item-image { height: 140px; background: #eee; position: relative; border-top-left-radius: 8px; border-top-right-radius: 8px; overflow: hidden; }
        .item-image img { width: 100%; height: 100%; object-fit: cover; }
        .image-placeholder { display: flex; flex-direction: column; align-items: center; justify-content: center; height: 100%; color: #999; }
        .image-placeholder ion-icon { font-size: 2rem; }
        .item-details { padding: 12px; flex: 1; display: flex; flex-direction: column; }
        .item-title { font-weight: 600; font-size: 1rem; margin: 0 0 4px 0; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
        .item-location { font-size: 0.85rem; color: #666; margin-bottom: 8px; }
        .item-price { font-weight: bold; color: #00715A; margin-top: auto; }
        .main-footer { text-align: center; padding: 40px 20px; margin-top: 30px; background-color: #fff; border-top: 1px solid #ecf0f1; }
        .more-link { color: #00715A; text-decoration: none; font-weight: 600; }
    </style>
</head>
<body>
<header class="main-header">
    <div class="container header-content">
        <a href="index.php" class="logo"><img src="https://lionsgoldencircle.com/Tridots/images/logo1.png
			" alt="TRIDOTS Logo"></a>
        <nav class="main-nav">
            <a href="index.php">Home</a>
            <a href="about.php">About</a>
            <a href="solutions.php">Solutions</a>
            <a href="contact.php">Contact</a>
        </nav>
        <div class="header-actions"><a href="login.php" class="btn">Get Started</a></div>
    </div>
</header>
<main class="container">
    <section class="listing-section">
        <div class="listing-header">
            <h2>Rent A Machine</h2>
            <a href="machines.php" class="more-link">More</a>
        </div>
        <div class="item-carousel">
            <?php if (!empty($rentingAds)): foreach ($rentingAds as $ad):
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

    <section class="listing-section">
        <div class="listing-header">
            <h2>Hire A Vehicle</h2>
            <a href="vehicles.php" class="more-link">More</a>
        </div>
        <div class="item-carousel">
            <?php if (!empty($vehicles)): foreach ($vehicles as $vehicle):
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

    <section class="listing-section">
        <div class="listing-header">
            <h2>Verified Service Providers</h2>
            <a href="laborers.php" class="more-link">More</a>
        </div>
        <div class="item-carousel">
            <?php if (!empty($serviceProviders)): foreach ($serviceProviders as $provider):
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
    <p>&copy; <?php echo date("Y"); ?> TRIDOTS. All rights reserved. | <a href="#" class="more-link">Privacy Policy</a></p>
</footer>
</body>
</html>