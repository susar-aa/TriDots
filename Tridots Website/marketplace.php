<?php
// =================================================================
// MARKETPLACE PAGE (TRIDOTS STYLE, WEB)
// Updated to match the design of vehicles.php for consistency.
// =================================================================

// --- SESSION MANAGEMENT ---
session_start();
$userSignedIn = isset($_SESSION['user_signed_in']) && $_SESSION['user_signed_in'] === true;
$username = $_SESSION['username'] ?? 'Guest';

// --- DATA FETCHING & HELPERS ---
$api_base_url = "https://lionsgoldencircle.com/Tridots/Api/";
$image_base_url = "https://lionsgoldencircle.com/";

function fetchApprovedProducts() {
    global $api_base_url;
    $url = $api_base_url . 'get_approved_products.php';
    $json_data = @file_get_contents($url);
    if ($json_data === false) {
        return [];
    }
    $products = json_decode($json_data, true);
    return is_array($products) ? $products : [];
}

function getProductImage($thumbnailUrl, $baseUrl) {
    if (empty($thumbnailUrl) || filter_var($thumbnailUrl, FILTER_VALIDATE_URL)) {
        return $thumbnailUrl ?: 'https://placehold.co/400x260/f1f5f9/64748b?text=No+Image';
    }
    return rtrim($baseUrl, '/') . '/' . ltrim($thumbnailUrl, '/');
}

function render_page_header($pageTitle, $activeLink, $userSignedIn, $username) {
    $navLinks = [
        'Dashboard' => 'marketplace_dashboard.php',
        'Machines' => 'machines.php',
        'Vehicles' => 'vehicles.php',
        'Laborers' => 'laborers.php',
        'Marketplace' => 'marketplace.php'
    ];

    $navItemsHTML = '';
    foreach ($navLinks as $label => $href) {
        $class = ($label === $activeLink) ? 'active' : '';
        $navItemsHTML .= "<a href='{$href}' class='{$class}'>{$label}</a>";
    }

    $userAuthHTML = '';
    if ($userSignedIn) {
        $userAuthHTML = "
            <span class='user-welcome'>Hi, " . htmlspecialchars($username) . "</span>
            <a href='logout.php' class='btn'>Sign Out</a>
        ";
    } else {
        $userAuthHTML = "<a href='login.php' class='btn'>Sign In</a>";
    }

    echo <<<HTML
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<title>{$pageTitle} – TRIDOTS</title>
<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
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
}
body { margin:0; background:var(--bg); color:var(--text); font-family:'Inter',sans-serif; }
.container { max-width: 1200px; margin:0 auto; padding:0 24px;}

/* Header Styles from vehicles.php */
.main-header { background:var(--primary-green); color:#fff; padding:12px 0; box-shadow:0 2px 10px rgba(0,0,0,0.07);}
.header-content { display:flex; justify-content:space-between; align-items:center;}
.logo img { height:38px;}
.main-nav { display:flex; gap:22px;}
.main-nav a { color:#fff; text-decoration:none; font-weight:500; opacity:.87; padding:2px 4px; border-radius:7px; transition:.18s;}
.main-nav a:hover, .main-nav a.active { background:rgba(255,255,255,0.11); font-weight:700; opacity: 1;}
.header-actions { display:flex; gap:16px; align-items:center;}
.user-welcome { color:#fff; font-weight:500;}
.btn { background:#fff; color:var(--primary-green); border:none; border-radius:30px; padding:9px 22px; font-weight:700; text-decoration:none; font-size:1rem; transition:.18s;}
.btn:hover { background:var(--accent); color:#fff;}

/* Main Content Styles */
.page-title { font-size:2rem; font-weight:800; margin:30px 0 18px;}
.search-container { margin-bottom: 24px; }
.search-container form { display: flex; max-width: 500px; }
.search-container input {
    flex: 1;
    padding: 10px 14px;
    font-size: 1rem;
    border: 1.5px solid var(--border);
    border-radius: 8px 0 0 8px;
    outline: none;
    transition: border-color 0.2s;
}
.search-container input:focus { border-color: var(--primary-green); }
.search-container button {
    background-color: var(--primary-green);
    color: white;
    border: none;
    padding: 0 20px;
    border-radius: 0 8px 8px 0;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.2s;
}
.search-container button:hover { background-color: #005a48; }

/* Grid & Card Styles from vehicles.php */
.ad-grid { display:grid; grid-template-columns:repeat(auto-fill,minmax(280px,1fr)); gap:32px; margin-bottom:45px;}
.ad-card { display:flex; flex-direction:column; background:var(--card); border-radius:var(--radius); box-shadow:var(--shadow); overflow:hidden; text-decoration:none; color:inherit; border:1.5px solid var(--border); transition:.22s; position:relative;}
.ad-card:hover { box-shadow:0 6px 24px 0 rgba(0,113,90,0.08),0 2px 10px 0 rgba(0,0,0,0.07); border-color:var(--primary-green);}
.ad-img { width:100%; height:190px; background:#f2f5f7; display:flex; align-items:center; justify-content:center; }
.ad-img img { width:100%; height:100%; object-fit:cover;}
.ad-info { padding:17px 15px 20px 15px; display:flex; flex-direction:column; gap:.7em;}
.ad-title { font-size:1.13rem; font-weight:700; margin:0 0 2px 0;}
.ad-loc { color:var(--muted); font-size:.98rem; display:flex; align-items:center; gap:6px;}
.item-price { color:var(--accent); font-weight:800; font-size:1.13rem;}
.empty-message { background:#fff; border:1.2px dashed var(--border); color:var(--muted); font-size:1.13rem; border-radius:var(--radius); padding:2.2em; text-align:center; margin:20px 0; grid-column: 1 / -1;}

/* Footer */
.main-footer { text-align:center; padding:38px 12px; margin-top:30px; background:var(--card); border-top:1px solid var(--border);}
.more-link { color: var(--primary-green); text-decoration: none; font-weight: 500;}
.more-link:hover { text-decoration: underline; }

@media (max-width:800px) {.container{padding:0 10px;} .page-title{font-size:1.5rem;} .ad-img{height:150px;}}
@media (max-width:550px) {.ad-grid{gap:18px;} .main-header{padding:12px 0;} .page-title{margin:18px 0 10px;}}
</style>
</head>
<body>
<header class="main-header">
    <div class="container header-content">
        <a href="marketplace_dashboard.php" class="logo"><img src="https://lionsgoldencircle.com/Tridots/images/logo1.png" alt="TRIDOTS Logo"></a>
        <nav class="main-nav">{$navItemsHTML}</nav>
        <div class="header-actions">{$userAuthHTML}</div>
    </div>
</header>
<main class="container">
HTML;
}

function render_page_footer() {
    echo <<<HTML
    </main>
    <footer class="main-footer">
        <p>&copy; <?php echo date("Y"); ?> TRIDOTS. All rights reserved. | <a href="#" class="more-link">Privacy Policy</a></p>
    </footer>
</body>
</html>
HTML;
}

function renderProductCard($product) {
    global $image_base_url;
    $productId = htmlspecialchars($product['product_id'] ?? '0');
    $productName = htmlspecialchars($product['product_name'] ?? 'Untitled Product');
    $price = 'Rs. ' . number_format((float)($product['price'] ?? 0), 2);
    $location = htmlspecialchars(($product['store_city'] ?? '') . ', ' . ($product['store_district'] ?? ''));
    $imageUrl = getProductImage($product['thumbnail_image_url'] ?? '', $image_base_url);
    $detailLink = "product_detail.php?product_id={$productId}";

    echo "
    <a href='{$detailLink}' class='ad-card' title='{$productName}'>
        <div class='ad-img'>
            <img src='{$imageUrl}' alt='{$productName}' onerror=\"this.onerror=null; this.src='https://placehold.co/400x260/f1f5f9/64748b?text=No+Image';\">
        </div>
        <div class='ad-info'>
            <h3 class='ad-title'>{$productName}</h3>
            <div class='ad-loc'><ion-icon name='location-outline'></ion-icon>{$location}</div>
            <p class='item-price'>{$price}</p>
        </div>
    </a>
    ";
}

// =================================================================
// --- PAGE EXECUTION ---
// =================================================================
$searchTerm = trim($_GET['search'] ?? '');
$allProducts = fetchApprovedProducts();
$filteredProducts = !empty($searchTerm)
    ? array_filter($allProducts, fn($p) => stripos($p['product_name'], $searchTerm) !== false)
    : $allProducts;

render_page_header('Marketplace', 'Marketplace', $userSignedIn, $username);
?>
<h1 class="page-title">Marketplace / Products</h1>
<div class="search-container">
    <form action="marketplace.php" method="get">
        <input type="text" name="search" placeholder="Search products..." value="<?php echo htmlspecialchars($searchTerm); ?>">
        <button type="submit">Search</button>
    </form>
</div>
<div class="ad-grid">
<?php
if (!empty($filteredProducts)) {
    foreach ($filteredProducts as $product) {
        renderProductCard($product);
    }
} else {
    echo "<div class='empty-message'><ion-icon name='storefront-outline'></ion-icon><br>No products found. Try a different search term or check back later.</div>";
}
?>
</div>
<?php
render_page_footer();
?>
