<?php
session_start();
$userSignedIn = isset($_SESSION['user_id']);
$userData = $userSignedIn ? $_SESSION['user_data'] : null;
$api_base_url = "https://lionsgoldencircle.com/Tridots/Api/";
$image_base_url = "https://lionsgoldencircle.com/";

function fetchData($url) {
    $json_data = @file_get_contents($url);
    if ($json_data === false) return [];
    $data = json_decode($json_data, true);
    return is_array($data) ? $data : [];
}

function getFirstImage($imageString, $baseUrl) {
    if (empty($imageString)) return "https://placehold.co/400x260/eee/555?text=No+Image";
    $images = explode(',', $imageString);
    $firstImage = trim($images[0]);
    if (empty($firstImage)) return "https://placehold.co/400x260/eee/555?text=No+Image";
    // If full URL, return as is
    if (filter_var($firstImage, FILTER_VALIDATE_URL)) return $firstImage;
    return $baseUrl . ltrim($firstImage, '/');
}

function renderAdvertCard($details) {
    $link = $details['link'] ?? '#';
    $imageUrl = $details['imageUrl'] ?? 'https://placehold.co/400x260/eee/555?text=No+Image';
    $altText = htmlspecialchars($details['altText'] ?? 'Advert Image');
    $title = htmlspecialchars($details['title'] ?? 'N/A');
    $location = htmlspecialchars($details['location'] ?? 'N/A');
    $priceLine = $details['priceLine'] ?? '';
    $placeholderIcon = $details['placeholderIcon'] ?? 'person-circle-outline';
    $onErrorJs = "this.onerror=null; this.parentElement.innerHTML = `<div class='image-placeholder'><ion-icon name='{$placeholderIcon}'></ion-icon><span>Failed to Load</span></div>`;";
    echo "<a href='{$link}' class='ad-card' title='{$title}'>
                <div class='ad-img'><img src='{$imageUrl}' alt='{$altText}' onerror=\"{$onErrorJs}\"></div>
                <div class='ad-info'>
                    <h3 class='ad-title'>{$title}</h3>
                    <div class='ad-loc'><ion-icon name='person-outline'></ion-icon>{$location}</div>
                    {$priceLine}
                </div>
          </a>";
}

$serviceProviders = fetchData($api_base_url . 'fetch_service_providers.php');
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Laborers / Service Providers – TRIDOTS</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@500;700&display=swap" rel="stylesheet">
    <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
    <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
    <style>
        :root {
            --primary-green: #00715A;
            --accent: #00B07D;
            --bg: #f7f8fa;
            --card: #fff;
            --text: #1d2d3d;
            --muted: #7b8a97;
            --border: #e5e7eb;
            --radius: 1.3rem;
            --shadow: 0 2px 16px 0 rgba(0,0,0,0.06);
            --gradient: linear-gradient(90deg, #2ecc71 0%, #27ae60 100%);
        }
        body { margin:0; background:var(--bg); color:var(--text); font-family:'Inter',sans-serif; }
        .main-header { background:var(--primary-green); color:#fff; padding:20px 0; box-shadow:0 2px 10px rgba(0,0,0,0.07);}
        .container { max-width: 1200px; margin:0 auto; padding:0 24px;}
        .header-content { display:flex; justify-content:space-between; align-items:center;}
        .logo img { height:38px;}
        .main-nav { display:flex; gap:22px;}
        .main-nav a { color:#fff; text-decoration:none; font-weight:500; opacity:.87; padding:2px 4px; border-radius:7px; transition:.18s;}
        .main-nav a:hover, .main-nav a[style*="bold"] { background:rgba(255,255,255,0.11); font-weight:700;}
        .header-actions { display:flex; gap:10px; align-items:center;}
        .user-welcome { color:#fff; font-weight:500;}
        .btn { background:#fff; color:var(--primary-green); border:none; border-radius:30px; padding:9px 22px; font-weight:700; text-decoration:none; font-size:1rem; transition:.18s;}
        .btn:hover { background:var(--accent); color:#fff;}
        .page-title { font-size:2rem; font-weight:800; margin:30px 0 18px;}
        .ad-grid { display:grid; grid-template-columns:repeat(auto-fit,minmax(280px,1fr)); gap:32px; margin-bottom:45px;}
        .ad-card { display:flex; flex-direction:column; background:var(--card); border-radius:var(--radius); box-shadow:var(--shadow); overflow:hidden; text-decoration:none; color:inherit; border:1.5px solid var(--border); transition:.22s; position:relative;}
        .ad-card:hover { box-shadow:0 6px 24px 0 rgba(0,113,90,0.08),0 2px 10px 0 rgba(0,0,0,0.07); border-color:var(--primary-green);}
        .ad-img { width:100%; height:190px; background:#f2f5f7; display:flex; align-items:center; justify-content:center; }
        .ad-img img { width:100%; height:100%; object-fit:cover;}
        .image-placeholder { display:flex; flex-direction:column; align-items:center; justify-content:center; width:100%; height:100%; color:#bbb;}
        .image-placeholder ion-icon { font-size:2.7rem;}
        .ad-info { padding:17px 15px 20px 15px; display:flex; flex-direction:column; gap:.7em;}
        .ad-title { font-size:1.13rem; font-weight:700; margin:0 0 2px 0;}
        .ad-loc { color:var(--muted); font-size:.98rem; display:flex; align-items:center; gap:6px;}
        .ad-info .item-price { color:var(--accent); font-weight:800; font-size:1.13rem;}
        .ad-info .item-price span { font-size:.99em; font-weight:500; color:var(--muted);}
        .empty-message { background:#fff; border:1.2px dashed var(--border); color:var(--muted); font-size:1.13rem; border-radius:var(--radius); padding:2.2em; text-align:center; margin:20px 0;}
        .main-footer { text-align:center; padding:38px 12px; margin-top:30px; background:var(--card); border-top:1px solid var(--border);}
        @media (max-width:800px) {.container{padding:0 10px;} .page-title{font-size:1.5rem;} .ad-img{height:150px;}}
        @media (max-width:550px) {.ad-grid{gap:18px;} .main-header{padding:12px 0;} .page-title{margin:18px 0 10px;}}
    </style>
</head>
<body>
    <header class="main-header">
        <div class="container header-content">
            <a href="marketplace_dashboard.php" class="logo"><img src="https://lionsgoldencircle.com/Tridots/images/logo1.png" alt="TRIDOTS Logo"></a>
            <nav class="main-nav">
                <a href="marketplace_dashboard.php">Dashboard</a>
                <a href="machines.php">Machines</a>
                <a href="vehicles.php">Vehicles</a>
                <a href="laborers.php" style="font-weight:bold;">Laborers</a>
                <a href="marketplace.php">Marketplace</a>
            </nav>
            <div class="header-actions">
            <?php if ($userSignedIn && $userData): ?>
                <span class="user-welcome">Hi, <?php echo htmlspecialchars($userData['full_name'] ?? 'User'); ?></span>
                <a href="?logout=true" class="btn">Sign Out</a>
            <?php else: ?>
                <a href="login.php" class="btn">Sign In</a>
            <?php endif; ?>
            </div>
        </div>
    </header>
    <main class="container">
        <h1 class="page-title">Laborers / Service Providers</h1>
        <div class="ad-grid">
            <?php if (!empty($serviceProviders)): ?>
                <?php foreach ($serviceProviders as $provider): ?>
                    <?php renderAdvertCard([
                        'link' => $userSignedIn ? "provider_details.php?id=" . urlencode($provider['user_id']) : "login.php",
                        'imageUrl' => getFirstImage($provider['profile_picture'], $image_base_url),
                        'altText' => $provider['name'],
                        'title' => $provider['service_category_name'],
                        'location' => $provider['name'],
                        'placeholderIcon' => 'person-circle-outline'
                    ]); ?>
                <?php endforeach; ?>
            <?php else: ?>
                <div class="empty-message"><ion-icon name="person-circle-outline"></ion-icon><br>No service providers available at the moment.</div>
            <?php endif; ?>
        </div>
    </main>
    <footer class="main-footer">
        <p>&copy; <?php echo date("Y"); ?> TRIDOTS. All rights reserved. | <a href="#" class="more-link">Privacy Policy</a></p>
    </footer>
</body>
</html>