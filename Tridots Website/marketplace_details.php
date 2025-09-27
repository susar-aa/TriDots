<?php
session_start();
$userSignedIn = isset($_SESSION['user_id']);
$userData = $userSignedIn ? $_SESSION['user_data'] : null;
$api_base_url = "https://lionsgoldencircle.com/Tridots/Api/";
$image_base_url = "https://lionsgoldencircle.com/";

function fetchData($url) {
    $json_data = @file_get_contents($url);
    return $json_data ? json_decode($json_data, true) : [];
}
function getFirstImage($imageString, $baseUrl) {
    if (empty($imageString)) return "https://placehold.co/500x300/eee/555?text=No+Image";
    $images = explode(',', $imageString);
    $firstImage = trim($images[0]);
    if (empty($firstImage)) return "https://placehold.co/500x300/eee/555?text=No+Image";
    return $baseUrl . ltrim($firstImage, '/');
}

// You'll need to use the real API for marketplace ads!
$adId = isset($_GET['id']) ? $_GET['id'] : '';
$ad = null;
if ($adId) {
    $ads = fetchData($api_base_url . 'fetch_marketplace_ads.php');
    foreach ($ads as $a) {
        if ($a['ad_id'] == $adId) {
            $ad = $a;
            break;
        }
    }
}
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title><?php echo $ad ? htmlspecialchars($ad['ad_title']) : "Ad Not Found"; ?> – TRIDOTS</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@500;700&display=swap" rel="stylesheet">
    <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
    <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
    <style>
        :root { --primary-green:#00715A; --accent:#00B07D; --bg:#f7f8fa; --card:#fff; --text:#1d2d3d; --muted:#7b8a97; --border:#e5e7eb; --radius:1.3rem; --shadow:0 2px 16px 0 rgba(0,0,0,0.06);}
        body { margin:0; background:var(--bg); color:var(--text); font-family:'Inter',sans-serif;}
        .main-header { background:var(--primary-green); color:#fff; padding:20px 0; box-shadow:0 2px 10px rgba(0,0,0,0.07);}
        .container { max-width:700px; margin:0 auto; padding:0 20px;}
        .header-content { display:flex; justify-content:space-between; align-items:center;}
        .logo img { height:38px;}
        .main-nav { display:flex; gap:18px;}
        .main-nav a { color:#fff; text-decoration:none; font-weight:500; opacity:.87; padding:2px 4px; border-radius:7px; transition:.18s;}
        .main-nav a:hover { background:rgba(255,255,255,0.11); font-weight:700;}
        .header-actions { display:flex; gap:10px; align-items:center;}
        .user-welcome { color:#fff; font-weight:500;}
        .btn { background:#fff; color:var(--primary-green); border:none; border-radius:30px; padding:9px 22px; font-weight:700; text-decoration:none; font-size:1rem; transition:.18s;}
        .btn:hover { background:var(--accent); color:#fff;}
        .details-card { margin:40px auto 32px; background:var(--card); border-radius:var(--radius); box-shadow:var(--shadow); display:flex; flex-direction:column; align-items:center; padding:36px 18px 32px 18px; border:1.5px solid var(--border);}
        .details-img { width:100%; max-width:390px; height:230px; border-radius:1.1rem; background:#ececec; margin-bottom:18px; overflow:hidden; display:flex;align-items:center;justify-content:center;}
        .details-img img { width:100%; height:100%; object-fit:cover;}
        .details-title { font-size:1.45rem; font-weight:800; margin:0 0 8px;}
        .details-category { color:var(--accent); font-weight:700; margin-bottom:13px;}
        .details-info { color:var(--muted); font-size:.99rem; margin-bottom:13px;}
        .details-block { margin:0 0 9px 0;}
        .profile-label { color:var(--muted); font-weight:600; margin-right:7px;}
        .details-desc { background:#f8fafb; border-radius:.7rem; padding:16px; font-size:1.06rem; color:var(--text);}
        .notfound { padding:3em 2em; text-align:center; color:var(--muted);}
        .item-price { color:var(--accent); font-weight:800; font-size:1.13rem; margin-bottom:8px;}
        @media (max-width:600px) { .details-card{padding:18px 5px;} .details-img{height:160px;} }
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
                <a href="laborers.php">Laborers</a>
                <a href="marketplace.php" style="font-weight:bold;">Marketplace</a>
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
        <?php if ($ad): ?>
        <div class="details-card">
            <div class="details-img">
                <img src="<?php echo getFirstImage($ad['ad_images'], $image_base_url); ?>" alt="Ad Image" onerror="this.onerror=null;this.src='https://placehold.co/390x230/eee/555?text=No+Image';">
            </div>
            <h1 class="details-title"><?php echo htmlspecialchars($ad['ad_title']); ?></h1>
            <div class="details-category"><?php echo htmlspecialchars($ad['category'] ?? 'Marketplace Ad'); ?></div>
            <div class="item-price">Rs. <?php echo htmlspecialchars(number_format((float)$ad['ad_price'], 2)); ?></div>
            <div class="details-info">
                <span class="profile-label"><ion-icon name="location-outline"></ion-icon> Location:</span>
                <?php echo htmlspecialchars($ad['ad_location'] ?? 'N/A'); ?>
            </div>
            <div class="details-block details-desc">
                <?php echo nl2br(htmlspecialchars($ad['ad_description'] ?? 'No description available.')); ?>
            </div>
        </div>
        <?php else: ?>
            <div class="notfound"><ion-icon name="storefront-outline" style="font-size:3em;"></ion-icon><br>Marketplace ad not found.</div>
        <?php endif; ?>
    </main>
    <footer class="main-footer" style="text-align:center; padding:40px 12px; margin-top:30px; background:var(--card); border-top:1px solid var(--border);">
        <p>&copy; <?php echo date("Y"); ?> TRIDOTS. All rights reserved. | <a href="#" class="more-link">Privacy Policy</a></p>
    </footer>
</body>
</html>