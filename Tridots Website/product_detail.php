<?php
// =================================================================
// PRODUCT DETAIL PAGE (TRIDOTS STYLE, WEB) - V4.2 (Cart Only)
// Displays detailed information, with Add to Cart functionality
// and a dynamic "Go to Cart" button.
// =================================================================

// --- SESSION MANAGEMENT ---
session_start();
$userSignedIn = isset($_SESSION['user_signed_in']) && $_SESSION['user_signed_in'] === true;
$username = $_SESSION['username'] ?? 'Guest';

// --- DATA FETCHING & HELPERS ---
$api_base_url = "https://lionsgoldencircle.com/Tridots/Api/";
$image_base_url = "https://lionsgoldencircle.com/";

// Get Product ID from URL, redirect if not present
$product_id = filter_input(INPUT_GET, 'product_id', FILTER_VALIDATE_INT);
if (!$product_id) {
    header("Location: marketplace.php");
    exit();
}

/**
 * Fetches details for a specific product from the API.
 * @param int $productId The ID of the product to fetch.
 * @return array|null The product data or null if not found.
 */
function fetchProductDetails($productId) {
    global $api_base_url;
    $url = $api_base_url . 'get_product_details.php?product_id=' . $productId;
    $json_data = @file_get_contents($url);

    if ($json_data === false) {
        error_log("Failed to fetch data from API for product ID: {$productId}");
        return null;
    }
    $response = json_decode($json_data, true);
    
    if (isset($response['error'])) {
        error_log("API Error for product {$productId}: " . $response['error']);
        return null;
    }
    
    return is_array($response) ? $response : null;
}

// =================================================================
// --- PAGE EXECUTION ---
// =================================================================
$product = fetchProductDetails($product_id);
$pageTitle = $product ? ($product['product_name'] ?? 'Product Details') : 'Product Not Found';
?>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0"/>
<title><?php echo htmlspecialchars($pageTitle); ?> – TRIDOTS</title>
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
    --radius: 1rem;
    --shadow: 0 2px 16px 0 rgba(0,0,0,0.06);
}
body { margin:0; background:var(--bg); color:var(--text); font-family:'Inter',sans-serif; }
.container { max-width: 1100px; margin:0 auto; padding:0 24px;}
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
.main-footer { text-align:center; padding:38px 12px; margin-top:30px; background:var(--card); border-top:1px solid var(--border);}

/* Product Detail Styles */
.product-detail-container { display: grid; grid-template-columns: 1fr; gap: 32px; margin-top: 30px; }
@media (min-width: 768px) { .product-detail-container { grid-template-columns: 1.2fr 1fr; } }
.product-gallery { display: flex; flex-direction: column; gap: 10px; }
.main-image-wrapper { background: var(--card); border: 1px solid var(--border); border-radius: var(--radius); overflow: hidden; box-shadow: var(--shadow); }
.main-image-wrapper img { display: block; width: 100%; height: auto; aspect-ratio: 4/3; object-fit: cover; }
.thumbnail-strip { display: flex; gap: 10px; overflow-x: auto; padding-bottom: 5px;}
.thumbnail-wrapper { flex: 0 0 80px; height: 60px; border: 2px solid var(--border); border-radius: 8px; overflow: hidden; cursor: pointer; opacity: 0.7; transition: opacity 0.2s, border-color 0.2s; }
.thumbnail-wrapper.active { border-color: var(--primary-green); opacity: 1; }
.thumbnail-wrapper img { width: 100%; height: 100%; object-fit: cover; }

.product-info { padding-top: 10px; }
.product-title { font-size: 2.2rem; font-weight: 800; margin: 0 0 8px 0; line-height: 1.2; }
.product-location { font-size: 1rem; color: var(--muted); display: flex; align-items: center; gap: 6px; margin-bottom: 20px; }
.product-price { font-size: 2.5rem; font-weight: 800; color: var(--accent); margin-bottom: 24px; transition: color 0.3s; }

/* Cart Action Styles */
.cart-actions { margin-top: 24px; }
.quantity-control { display: flex; align-items: center; gap: 10px; margin-bottom: 20px; }
.quantity-control label { font-weight: 600; color: var(--muted); }
.quantity-input { width: 70px; padding: 10px; border: 2px solid var(--border); border-radius: 8px; text-align: center; font-size: 1.1rem; font-weight: 600; }
.action-buttons { display: grid; grid-template-columns: 1fr; gap: 12px; } /* MODIFIED */
.action-btn { display: inline-flex; justify-content: center; align-items: center; gap: 8px; border: none; padding: 16px; font-size: 1rem; font-weight: 700; border-radius: 30px; cursor: pointer; transition: all 0.2s; text-decoration:none; }
.add-to-cart-btn { background: var(--primary-green); color: #fff; }
.add-to-cart-btn:hover { background: #005a48; }
.go-to-cart-btn { background: var(--accent); color: #fff; display: none; }
.go-to-cart-btn:hover { background: #009a6b; }

/* Variants Section */
.variants-section { margin-top: 24px; }
.variants-section h3 { font-size: 1.1rem; font-weight: 600; color: var(--muted); margin-bottom: 12px; }
.variant-options { display: flex; flex-wrap: wrap; gap: 12px; }
.variant-option { padding: 10px 16px; border: 2px solid var(--border); border-radius: 30px; cursor: pointer; font-weight: 500; transition: all 0.2s; }
.variant-option.active { border-color: var(--primary-green); background-color: var(--primary-green); color: #fff; font-weight: 600; }

.product-details-section { margin-top: 40px; background: var(--card); padding: 24px; border-radius: var(--radius); border: 1px solid var(--border); }
.product-details-section h3 { font-size: 1.3rem; margin: 0 0 16px 0; }
.spec-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 12px; }
.spec-item { display: flex; flex-direction: column; }
.spec-label { color: var(--muted); font-size: 0.9rem; }
.spec-value { font-weight: 600; }
.product-description p { line-height: 1.7; margin-top: 24px; }
.not-found { text-align: center; padding: 60px 20px; }
.not-found h1 { font-size: 2rem; }
.not-found p { font-size: 1.1rem; color: var(--muted); }

/* Toast Notification */
.toast-notification { position: fixed; bottom: 20px; left: 50%; transform: translateX(-50%); background-color: var(--text); color: #fff; padding: 12px 24px; border-radius: 30px; z-index: 1000; opacity: 0; transition: opacity 0.3s, bottom 0.3s; font-weight: 500; }
.toast-notification.show { opacity: 1; bottom: 30px; }
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
            <a href="marketplace.php" class="active">Marketplace</a>
        </nav>
        <div class="header-actions">
            <?php if($userSignedIn): ?>
                <a href="profile.php" class="user-welcome">Hi, <?php echo htmlspecialchars($username); ?></a>
                <a href='logout.php' class='btn'>Sign Out</a>
            <?php else: ?>
                <a href='login.php' class='btn'>Sign In</a>
            <?php endif; ?>
        </div>
    </div>
</header>
<main class="container">
<?php if ($product):
    $images = $product['images'] ?? [];
    if(empty($images)) { $images[] = ['image_url' => 'https://placehold.co/600x400/f1f5f9/64748b?text=No+Image']; }
    $variants = $product['variants'] ?? [];
?>
    <div class="product-detail-container">
        <div class="product-gallery">
            <div class="main-image-wrapper">
                <img id="mainProductImage" src="<?php echo htmlspecialchars($images[0]['image_url']); ?>" alt="<?php echo htmlspecialchars($product['product_name']); ?>">
            </div>
            <?php if (count($images) > 1): ?>
            <div class="thumbnail-strip">
                <?php foreach ($images as $index => $img): ?>
                <div class="thumbnail-wrapper <?php echo $index === 0 ? 'active' : ''; ?>" onclick="changeImage('<?php echo htmlspecialchars($img['image_url']); ?>', this)">
                    <img src="<?php echo htmlspecialchars($img['image_url']); ?>" alt="Thumbnail <?php echo $index + 1; ?>">
                </div>
                <?php endforeach; ?>
            </div>
            <?php endif; ?>
        </div>

        <div class="product-info">
            <h1 class="product-title"><?php echo htmlspecialchars($product['product_name']); ?></h1>
            <p class="product-location">
                <ion-icon name="location-outline"></ion-icon>
                <?php echo htmlspecialchars(($product['store_city'] ?? '') . ', ' . ($product['store_district'] ?? 'Location not specified')); ?>
            </p>
            <p class="product-price" id="displayPrice">Rs. <?php echo number_format((float)($product['price'] ?? 0), 2); ?></p>
            
            <form id="purchaseForm" onsubmit="return false;"> <input type="hidden" name="product_id" value="<?php echo $product_id; ?>">
                <input type="hidden" id="selectedVariantId" name="variant_id" value="">
            
                <?php if (!empty($variants)): ?>
                <div class="variants-section">
                    <h3>Select Variant</h3>
                    <div class="variant-options">
                        <div class="variant-option active" onclick="selectVariant(this, '<?php echo (float)($product['price'] ?? 0); ?>', '', null)">Default</div>
                        <?php foreach ($variants as $variant): ?>
                        <div class="variant-option" onclick="selectVariant(this, '<?php echo (float)($variant['variant_price'] ?? 0); ?>', '<?php echo htmlspecialchars($variant['variant_image_url'] ?? ''); ?>', '<?php echo (int)($variant['variant_id'] ?? 0); ?>')">
                            <?php echo htmlspecialchars($variant['variant_name']); ?>
                        </div>
                        <?php endforeach; ?>
                    </div>
                </div>
                <?php endif; ?>
                
                <div class="cart-actions">
                    <div class="quantity-control">
                        <label for="quantity">Quantity:</label>
                        <input type="number" id="quantity" name="quantity" class="quantity-input" value="1" min="1">
                    </div>
                    <div class="action-buttons">
                        <button type="button" id="addToCartBtn" class="action-btn add-to-cart-btn" onclick="addToCart()">
                            <ion-icon name="cart-outline"></ion-icon> Add to Cart
                        </button>
                        <a href="cart.php" id="goToCartBtn" class="action-btn go-to-cart-btn">
                            <ion-icon name="arrow-forward-outline"></ion-icon> Go to Cart
                        </a>
                        </div>
                </div>
            </form>
        </div>
    </div>

    <div class="product-details-section">
        <h3>Details</h3>
        <div class="spec-grid">
            <div class="spec-item"><span class="spec-label">Condition</span><span class="spec-value"><?php echo htmlspecialchars($product['product_condition'] ?? '-'); ?></span></div>
            <div class="spec-item"><span class="spec-label">Brand</span><span class="spec-value"><?php echo htmlspecialchars($product['brand'] ?? '-'); ?></span></div>
            <div class="spec-item"><span class="spec-label">Model</span><span class="spec-value"><?php echo htmlspecialchars($product['model'] ?? '-'); ?></span></div>
            <div class="spec-item"><span class="spec-label">Color</span><span class="spec-value"><?php echo htmlspecialchars($product['color'] ?? '-'); ?></span></div>
        </div>
        
        <?php if(!empty($product['product_description'])): ?>
        <div class="product-description">
             <p><?php echo nl2br(htmlspecialchars($product['product_description'])); ?></p>
        </div>
        <?php endif; ?>
    </div>
    
    <div id="toast" class="toast-notification"></div>

    <script>
        const priceElement = document.getElementById('displayPrice');
        const mainImage = document.getElementById('mainProductImage');
        const defaultImage = '<?php echo htmlspecialchars($images[0]['image_url']); ?>';
        const variantIdInput = document.getElementById('selectedVariantId');

        function changeImage(imageUrl, clickedElement) {
            mainImage.src = imageUrl;
            document.querySelectorAll('.thumbnail-wrapper').forEach(el => el.classList.remove('active'));
            if (clickedElement) {
                clickedElement.classList.add('active');
            }
        }

        function selectVariant(clickedElement, price, variantImageUrl, variantId) {
            priceElement.innerText = 'Rs. ' + parseFloat(price).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
            variantIdInput.value = variantId || '';

            document.querySelectorAll('.variant-option').forEach(el => el.classList.remove('active'));
            clickedElement.classList.add('active');

            if (variantImageUrl) {
                mainImage.src = variantImageUrl;
                document.querySelectorAll('.thumbnail-wrapper').forEach(el => el.classList.remove('active'));
            } else {
                mainImage.src = defaultImage;
                const firstThumbnail = document.querySelector('.thumbnail-wrapper');
                if(firstThumbnail) firstThumbnail.classList.add('active');
            }
        }
        
        function showToast(message) {
            const toast = document.getElementById('toast');
            toast.textContent = message;
            toast.classList.add('show');
            setTimeout(() => { toast.classList.remove('show'); }, 3000);
        }

        function addToCart() {
            const form = document.getElementById('purchaseForm');
            const formData = new FormData(form);

            fetch('add_to_cart.php', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if(data.success) {
                    showToast(data.message || 'Item added to cart!');
                    
                    if (data.show_go_to_cart) {
                        const addToCartButton = document.getElementById('addToCartBtn');
                        const goToCartButton = document.getElementById('goToCartBtn');
                        if(addToCartButton && goToCartButton) {
                            addToCartButton.style.display = 'none';
                            goToCartButton.style.display = 'inline-flex';
                        }
                    }
                } else {
                    showToast(data.message || 'Could not add item to cart.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showToast('An error occurred.');
            });
        }
    </script>

<?php else: ?>
    <div class="not-found">
        <h1>Product Not Found</h1>
        <p>Sorry, the product you are looking for does not exist or has been removed.</p>
        <a href="marketplace.php" class="btn">Back to Marketplace</a>
    </div>
<?php endif; ?>

</main>
<footer class="main-footer">
    <p>&copy; <?php echo date("Y"); ?> TRIDOTS. All rights reserved. | <a href="profile.php" class="more-link">My Profile</a></p>
</footer>
</body>
</html>