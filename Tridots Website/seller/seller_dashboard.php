<?php
session_start();
// Redirect if user is not logged in or not a business user
if (empty($_SESSION['user_id']) || strtolower($_SESSION['user_type']) !== 'business') {
    header("Location: https://lionsgoldencircle.com/Tridots/login.php");
    exit();
}
require_once __DIR__ . '/db.php';

$user_id = $_SESSION['user_id'];
$user = null;
$business = null;

// --- USER & BUSINESS PROFILE ---
try {
    $stmt = $pdo->prepare("SELECT * FROM Users WHERE user_id = ?");
    $stmt->execute([$user_id]);
    $user = $stmt->fetch(PDO::FETCH_ASSOC);

    $stmt = $pdo->prepare("SELECT * FROM BusinessProfiles WHERE user_id = ?");
    $stmt->execute([$user_id]);
    $business = $stmt->fetch(PDO::FETCH_ASSOC);
} catch (PDOException $e) {
    error_log("Error fetching user/business profile: " . $e->getMessage());
    // Handle error gracefully, maybe show a generic error page
    die("A database error occurred. Please try again later.");
}


// --- STATS QUERIES (with error handling) ---
$services_count = 0;
try {
    $stmt = $pdo->prepare("SELECT COUNT(*) FROM Service_Providers WHERE User_id=?");
    $stmt->execute([$user_id]);
    $services_count = $stmt->fetchColumn();
} catch (PDOException $e) {
    error_log("Error counting services: " . $e->getMessage());
}

$vehicles_count = 0;
try {
    $stmt = $pdo->prepare("SELECT COUNT(*) FROM Vehicles WHERE user_id=?");
    $stmt->execute([$user_id]);
    $vehicles_count = $stmt->fetchColumn();
} catch (PDOException $e) {
    error_log("Error counting vehicles: " . $e->getMessage());
}

$rentings_count = 0;
try {
    $stmt = $pdo->prepare("SELECT COUNT(*) FROM renting WHERE user_id=?");
    $stmt->execute([$user_id]);
    $rentings_count = $stmt->fetchColumn();
} catch (PDOException $e) {
    error_log("Error counting rentings: " . $e->getMessage());
}

$products_count = 0;
try {
    // NEW: Count for marketplace products
    $stmt = $pdo->prepare("SELECT COUNT(*) FROM Products WHERE seller_id=?");
    $stmt->execute([$user_id]);
    $products_count = $stmt->fetchColumn();
} catch (PDOException $e) {
    error_log("Error counting products: " . $e->getMessage()); // Log if 'Products' table is missing
}

$orders_count = 0;
try {
    $stmt = $pdo->prepare("SELECT COUNT(*) FROM inquiries WHERE user_id=?");
    $stmt->execute([$user_id]);
    $orders_count = $stmt->fetchColumn();
} catch (PDOException $e) {
    error_log("Error counting inquiries: " . $e->getMessage());
}

// NEW: Fetch categories for the product form
$product_categories = [];
try {
    $cat_stmt = $pdo->query("SELECT category_id, category_name FROM Categories ORDER BY category_name ASC");
    $product_categories = $cat_stmt->fetchAll(PDO::FETCH_ASSOC);
} catch (PDOException $e) {
    error_log("Error fetching categories: " . $e->getMessage()); // Log if 'Categories' table is missing
}

// --- OTHER VARIABLES ---
$balance = 1250.00; // Example value
$unread_msgs = 2; // Example value

$profileStatus = ($business && $business['verification_status'] == "Verified") ? "Verified" : "Not Verified";
$username = $user['username'] ?? "Seller";
?>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <title>Seller Dashboard - TridotsMarket</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"/>
  <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet"/>
  <style>
    body { background: #f6f9fa; font-family: 'Inter',Arial,sans-serif;}
    .container-fluid { min-height: 100vh; }
    .sidebar {
      background: #fff;
      border-right: 1.5px solid #e5eaef;
      box-shadow: 0 4px 24px #00715a0b;
      min-height: 100vh;
      height: 100vh;
      position: sticky;
      top: 0;
      padding: 0;
      display: flex;
      flex-direction: column;
      justify-content: flex-start;
    }
    .sidebar .nav {
      flex-direction: column;
      gap: 0.5rem;
      margin-bottom: 2rem;
    }
    .sidebar .nav-link {
      color: #00715A;
      font-weight: 700;
      border-radius: 9px;
      padding: 0.75em 1em;
      transition: background .13s;
      font-size: 1em;
      margin-bottom: 0.1em;
    }
    .sidebar .nav-link.active, .sidebar .nav-link:hover {
      background: linear-gradient(90deg,#00b07d12 60%,#00715a08 100%);
      color: #00b07d;
    }
    .sidebar .mt-auto {
      margin-top: auto !important;
      padding-bottom: 2rem;
    }
    .sidebar .marketplace-link {
      margin-bottom: 1.5rem;
      padding: 0 1rem;
      text-align: center;
    }
    .sidebar .marketplace-link a {
      display: block;
      background: linear-gradient(90deg,#00b07d14 60%,#00715a08 100%);
      color: #00715A;
      font-weight: 700;
      border-radius: 8px;
      padding: 0.65em 1em;
      font-size: 1em;
      text-decoration: none;
      transition: background .13s, color .13s;
    }
    .sidebar .marketplace-link a:hover {
      background: #00b07d;
      color: #fff;
      text-decoration: none;
    }
    .dashboard-main {
      background: #f6f9fa;
      min-height: 100vh;
      padding: 0;
      display: flex;
      flex-direction: column;
    }
    .dashboard-header {
      border-bottom: 1.2px solid #e7eae9; background: #fff;
      padding: 1.3em 1.1em 1em 1.1em; margin-bottom: 1.3em;
      border-radius: 0 0 1.2rem 1.2rem; box-shadow: 0 2px 12px #00715a0a;
      display: flex; justify-content: space-between; align-items: center;
      flex-wrap: wrap;
    }
    .dashboard-header .user-info { display: flex; align-items: center; gap: 1.2em;}
    .dashboard-header .user-info img {border-radius:50%;width:46px;height:46px;object-fit:cover;}
    .dashboard-header .user-info .user-meta {font-weight:700; color:#00715A;}
    .dashboard-content { padding: 1.2em 1.7em;}
    .stat-card {
      background: linear-gradient(100deg,#fff 60%,#eafff7 100%);
      border-radius: .8em; box-shadow: 0 2px 12px #00b07d0a;
      padding: 1em 1.1em .7em 1.1em; display: flex; flex-direction: column; align-items: flex-start; gap: .3em;
      border: 1.1px solid #e7eae9; min-width: 120px;
    }
    .stat-card .stat-icon { font-size: 1.5em; margin-bottom:.2em; color:#00b07d;}
    .stat-card .stat-value { font-size: 1.1em; font-weight:800;color:#00715A;}
    .stat-card .stat-label { font-size: .87em; color:#5f6e7a;}
    .ad-card, .service-card, .product-card {
      background: #fff; border-radius: .7em; box-shadow: 0 2px 8px #00b07d0a;
      padding: .9em 1em .8em 1em; margin-bottom: .8em;
      border: 1px solid #e7eae9; display: flex; gap: 1em; align-items: center;
    }
    .ad-card img, .service-card img, .product-card img {width: 62px; height: 52px; object-fit:cover; border-radius: .5em; border:1px solid #e5eae5;}
    .ad-actions .btn { margin-right: .35em; font-size: .92em;}
    .dashboard-form label { font-weight: 700; color: #00715A; font-size: 0.9em; margin-bottom: 0.3em; }
    .dashboard-form input, .dashboard-form select, .dashboard-form textarea {
      border-radius: 0.5em; border:1.2px solid #e7eae9; background: #f7fafc;
      font-weight: 500; margin-bottom: .6em;
      font-size: .98em;
    }
    .dashboard-form .form-row {margin-bottom:.95em;}
    .dashboard-form .preview-img {width:42px;height:42px;border-radius:50%;object-fit:cover;display:block;margin-top:.25em;}
    .dashboard-form .buttons {display:flex;gap:.7em;}
    .save-btn {background:linear-gradient(100deg,#00b07d 70%,#00715a 100%);color:#fff;font-weight:800;}
    .delete-btn {background:#ffeaea;color:#b9002e;font-weight:600;}
    .modal .modal-body { max-height: 75vh; overflow-y: auto;}
    .section-title { font-weight: 700; color: #00715A; margin-top: 1.2rem; margin-bottom: 0.8rem; border-bottom: 2px solid #e0e7e5; padding-bottom: 0.3rem;}
    .variant-row { background-color: #f8f9fa; }
    @media (max-width:1000px) {
      .dashboard-content {padding:.6em;}
      .dashboard-header {padding:.7em .6em .7em .6em;}
    }
    @media (max-width:767.98px) {
      .sidebar, .dashboard-main {min-height: unset; height: auto;}
      .sidebar {position: static; border-right: none; box-shadow: none;}
      .dashboard-content {padding: .5em;}
      .dashboard-header {flex-direction: column; align-items: flex-start;}
      .sidebar .marketplace-link {margin-bottom: 1rem;}
    }
  </style>
</head>
<body>
<div class="container-fluid px-0">
  <div class="row gx-0">
    <!-- Sidebar -->
    <nav class="col-12 col-md-3 col-lg-2 sidebar overflow-auto">
      <a class="navbar-brand mb-4 mt-3 mx-3" href="#"><span style="color:#00b07d;font-weight:900;">TR:Dots</span> Dashboard</a>
      <ul class="nav flex-column mb-auto px-2 py-3 gap-2">
        <li class="nav-item"><a class="nav-link active" data-bs-toggle="tab" href="#tab-overview"><i class="fa-solid fa-gauge"></i> Overview</a></li>
        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#tab-listings"><i class="fa-solid fa-list"></i> My Listings</a></li>
        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#tab-orders"><i class="fa-solid fa-cart-arrow-down"></i> Orders/Inquiries</a></li>
        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#tab-messages"><i class="fa-solid fa-comments"></i> Messages</a></li>
        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#tab-reviews"><i class="fa-solid fa-star"></i> Reviews</a></li>
        <li class="nav-item"><a class="nav-link" data-bs-toggle="tab" href="#tab-profile"><i class="fa-solid fa-user"></i> Profile</a></li>
      </ul>
      <div class="mt-auto text-center">
        <span class="badge bg-success">Seller: <?=htmlspecialchars($username)?></span>
      </div>
      <div class="marketplace-link">
        <a href="/Tridots/marketplace_dashboard.php" title="Go to Marketplace Dashboard">
          <i class="fa-solid fa-store"></i> Marketplace
        </a>
      </div>
    </nav>
    <!-- Main Content Area -->
    <div class="col dashboard-main">
      <!-- Dashboard Header -->
      <div class="dashboard-header">
        <div class="user-info">
          <img src="<?=htmlspecialchars($business['logo'] ?? $user['profile_picture'] ?? 'https://placehold.co/64x64/00715A/fff?text=U')?>" alt="User">
          <div>
            <div class="user-meta"><?=htmlspecialchars($business['business_name'] ?? $username)?> <span class="badge bg-info ms-2"><?=$profileStatus?></span></div>
            <div style="font-size:.98em;color:#5f6e7a;">Welcome back to your business hub!</div>
          </div>
        </div>
        <div>
          <button class="btn btn-success px-3 fw-bold" data-bs-toggle="modal" data-bs-target="#addListingModal"><i class="fa fa-plus"></i> New Listing</button>
        </div>
      </div>
      <div class="dashboard-content">
        <div class="tab-content">
          <!-- Overview -->
          <div class="tab-pane fade show active" id="tab-overview">
            <div class="row g-3 mb-3">
              <div class="col-6 col-lg-2 col-md-4">
                <div class="stat-card">
                  <div class="stat-icon"><i class="fa-solid fa-briefcase"></i></div>
                  <div class="stat-value"><?=$services_count?></div>
                  <div class="stat-label">Services</div>
                </div>
              </div>
              <div class="col-6 col-lg-2 col-md-4">
                <div class="stat-card">
                  <div class="stat-icon"><i class="fa-solid fa-truck"></i></div>
                  <div class="stat-value"><?=$vehicles_count?></div>
                  <div class="stat-label">Vehicles</div>
                </div>
              </div>
              <div class="col-6 col-lg-2 col-md-4">
                <div class="stat-card">
                  <div class="stat-icon"><i class="fa-solid fa-box-open"></i></div>
                  <div class="stat-value"><?=$rentings_count?></div>
                  <div class="stat-label">Renting Ads</div>
                </div>
              </div>
              <div class="col-6 col-lg-2 col-md-4">
                <div class="stat-card">
                  <div class="stat-icon"><i class="fa-solid fa-box"></i></div>
                  <div class="stat-value"><?=$products_count?></div>
                  <div class="stat-label">Products</div>
                </div>
              </div>
              <div class="col-6 col-lg-2 col-md-4">
                <div class="stat-card">
                  <div class="stat-icon"><i class="fa-solid fa-cart-shopping"></i></div>
                  <div class="stat-value"><?=$orders_count?></div>
                  <div class="stat-label">Orders/Inquiries</div>
                </div>
              </div>
              <div class="col-6 col-lg-2 col-md-4">
                <div class="stat-card">
                  <div class="stat-icon"><i class="fa-solid fa-envelope"></i></div>
                  <div class="stat-value"><?=$unread_msgs?></div>
                  <div class="stat-label">Unread Msgs</div>
                </div>
              </div>
            </div>
            <div class="card p-2 mb-2">
              <h6 class="mb-1">Your Listings At A Glance</h6>
              <div id="dashboardListingsPreview" class="row g-2"></div>
            </div>
          </div>
          <div class="tab-pane fade" id="tab-listings">
            <div class="d-flex justify-content-between align-items-center mb-2">
              <h5>My Listings</h5>
              <button class="btn btn-outline-success btn-sm" data-bs-toggle="modal" data-bs-target="#addListingModal"><i class="fa fa-plus"></i> New Listing</button>
            </div>
            <ul class="nav nav-pills mb-3" id="listingsTab" role="tablist">
              <li class="nav-item" role="presentation"><button class="nav-link active" data-bs-toggle="pill" data-bs-target="#services-list" type="button">Services</button></li>
              <li class="nav-item" role="presentation"><button class="nav-link" data-bs-toggle="pill" data-bs-target="#vehicles-list" type="button">Vehicles</button></li>
              <li class="nav-item" role="presentation"><button class="nav-link" data-bs-toggle="pill" data-bs-target="#rentings-list" type="button">Renting Ads</button></li>
              <li class="nav-item" role="presentation"><button class="nav-link" data-bs-toggle="pill" data-bs-target="#products-list" type="button">Products</button></li>
            </ul>
            <div class="tab-content" id="listingsTabContent">
              <div class="tab-pane fade show active" id="services-list"></div>
              <div class="tab-pane fade" id="vehicles-list"></div>
              <div class="tab-pane fade" id="rentings-list"></div>
              <div class="tab-pane fade" id="products-list"></div>
            </div>
          </div>
          <div class="tab-pane fade" id="tab-orders">
            <h5>Orders / Inquiries</h5>
            <div id="orders-list"></div>
          </div>
          <div class="tab-pane fade" id="tab-messages">
            <h5>Messages</h5>
            <div id="messages-list"></div>
          </div>
          <div class="tab-pane fade" id="tab-reviews">
            <h5>Reviews</h5>
            <div id="reviews-list"></div>
          </div>
          <div class="tab-pane fade" id="tab-profile">
            <h5>Business Profile</h5>
            <form class="dashboard-form mt-3" enctype="multipart/form-data" method="post" action="save_business_profile.php">
              <div class="form-row">
                <label>Logo</label>
                <input type="file" name="logo" class="form-control" accept="image/*"/>
                <img src="<?=htmlspecialchars($business['logo'] ?? 'https://placehold.co/64x64/00715A/fff?text=B')?>" class="preview-img" alt="Logo"/>
              </div>
              <div class="form-row"><label>Business Name</label>
                <input class="form-control" type="text" name="business_name" value="<?=htmlspecialchars($business['business_name'] ?? '')?>">
              </div>
              <div class="form-row"><label>Email Address</label>
                <input class="form-control" type="email" name="email_address" value="<?=htmlspecialchars($business['email_address'] ?? '')?>">
              </div>
              <div class="form-row"><label>Contact Number</label>
                <input class="form-control" type="text" name="contact_number" value="<?=htmlspecialchars($business['contact_number'] ?? '')?>">
              </div>
              <div class="form-row"><label>Address</label>
                <input class="form-control" type="text" name="address" value="<?=htmlspecialchars($business['address'] ?? '')?>">
              </div>
              <div class="form-row"><label>Bio/Description</label>
                <textarea class="form-control" name="bio_description"><?=htmlspecialchars($business['bio_description'] ?? '')?></textarea>
              </div>
              <div class="form-row"><label>Website URL</label>
                <input class="form-control" type="text" name="website_url" value="<?=htmlspecialchars($business['website_url'] ?? '')?>">
              </div>
              <div class="form-row"><label>Operating Hours/Days</label>
                <input class="form-control" type="text" name="operating_hours_days" value="<?=htmlspecialchars($business['operating_hours_days'] ?? '')?>">
              </div>
              <div class="form-row buttons">
                <button class="btn save-btn" type="submit">Save Changes</button>
              </div>
            </form>
          </div>
        </div>
      </div>
      <!-- Add/Edit Listing Modal -->
      <div class="modal fade" id="addListingModal" tabindex="-1">
        <div class="modal-dialog modal-xl modal-dialog-scrollable">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title"><i class="fa fa-plus"></i> Add New Listing</h5>
              <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
              <ul class="nav nav-tabs" id="addListingTabs" role="tablist">
                <li class="nav-item"><button class="nav-link active" data-bs-toggle="tab" data-bs-target="#add-service-form" type="button">Service</button></li>
                <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#add-vehicle-form" type="button">Vehicle</button></li>
                <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#add-renting-form" type="button">Renting Ad</button></li>
                <li class="nav-item"><button class="nav-link" data-bs-toggle="tab" data-bs-target="#add-product-form" type="button">Marketplace Product</button></li>
              </ul>
              <div class="tab-content pt-3">
                <div class="tab-pane fade show active" id="add-service-form">
                  <?php @include "dashboard-edit-service.php"; ?>
                </div>
                <div class="tab-pane fade" id="add-vehicle-form">
                  <?php @include "dashboard-edit-vehicle.php"; ?>
                </div>
                <div class="tab-pane fade" id="add-renting-form">
                  <?php @include "dashboard-edit-renting.php"; ?>
                </div>
                <div class="tab-pane fade" id="add-product-form">
                  <form class="dashboard-form" action="save_product.php" method="post" enctype="multipart/form-data">
                      <h5 class="section-title">Basic Information</h5>
                      <div class="row">
                          <div class="col-md-8 mb-3">
                              <label for="product_name" class="form-label">Product Name *</label>
                              <input type="text" class="form-control" id="product_name" name="product_name" required>
                          </div>
                          <div class="col-md-4 mb-3">
                              <label for="category_id" class="form-label">Category *</label>
                              <select class="form-select" id="category_id" name="category_id" required>
                                  <option value="">Select Category...</option>
                                  <?php if (!empty($product_categories)): ?>
                                      <?php foreach ($product_categories as $cat): ?>
                                          <option value="<?= htmlspecialchars($cat['category_id']) ?>"><?= htmlspecialchars($cat['category_name']) ?></option>
                                      <?php endforeach; ?>
                                  <?php else: ?>
                                      <option value="" disabled>No categories found</option>
                                  <?php endif; ?>
                              </select>
                          </div>
                      </div>
                      <div class="mb-3">
                          <label for="product_description" class="form-label">Product Description *</label>
                          <textarea class="form-control" id="product_description" name="product_description" rows="4" required></textarea>
                      </div>
                      <div class="row">
                          <div class="col-md-4 mb-3">
                              <label for="price" class="form-label">Price (LKR) *</label>
                              <input type="number" step="0.01" class="form-control" id="price" name="price" required>
                          </div>
                          <div class="col-md-4 mb-3">
                              <label for="stock_quantity" class="form-label">Stock Quantity *</label>
                              <input type="number" class="form-control" id="stock_quantity" name="stock_quantity" required>
                          </div>
                          <div class="col-md-4 mb-3">
                              <label class="form-label">Condition *</label>
                              <div class="pt-2">
                                  <div class="form-check form-check-inline">
                                      <input class="form-check-input" type="radio" name="product_condition" id="condition_new" value="New" checked>
                                      <label class="form-check-label" for="condition_new">New</label>
                                  </div>
                                  <div class="form-check form-check-inline">
                                      <input class="form-check-input" type="radio" name="product_condition" id="condition_used" value="Used">
                                      <label class="form-check-label" for="condition_used">Used</label>
                                  </div>
                              </div>
                          </div>
                      </div>

                      <h5 class="section-title">Product Images</h5>
                       <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="main_product_image" class="form-label">Main Image (Thumbnail) *</label>
                                <input class="form-control" type="file" id="main_product_image" name="main_product_image" accept="image/*" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="gallery_images" class="form-label">Additional Images (Gallery)</label>
                                <input class="form-control" type="file" id="gallery_images" name="gallery_images[]" accept="image/*" multiple>
                            </div>
                        </div>

                      <h5 class="section-title">Product Attributes</h5>
                      <div class="row">
                          <div class="col-md-3 mb-3"><label class="form-label">SKU</label><input type="text" class="form-control" name="sku"></div>
                          <div class="col-md-3 mb-3"><label class="form-label">Brand</label><input type="text" class="form-control" name="brand"></div>
                          <div class="col-md-3 mb-3"><label class="form-label">Model</label><input type="text" class="form-control" name="model"></div>
                          <div class="col-md-3 mb-3"><label class="form-label">Color</label><input type="text" class="form-control" name="color"></div>
                          <div class="col-md-6 mb-3"><label class="form-label">Keywords (comma-separated)</label><input type="text" class="form-control" name="keywords"></div>
                          <div class="col-md-6 mb-3"><label class="form-label">Compatibility</label><input type="text" class="form-control" name="compatibility"></div>
                          <div class="col-md-4 mb-3"><label class="form-label">Weight (g)</label><input type="number" class="form-control" name="weight_g"></div>
                          <div class="col-md-4 mb-3"><label class="form-label">Dimensions (cm) (LxWxH)</label><input type="text" class="form-control" name="dimensions_cm"></div>
                          <div class="col-md-4 mb-3"><label class="form-label">Material</label><input type="text" class="form-control" name="material"></div>
                           <div class="col-md-12 mb-3"><label class="form-label">Warranty Policy</label><textarea class="form-control" name="warranty_policy" rows="2"></textarea></div>
                           <div class="col-md-6 mb-3"><label class="form-label">Manufacturing Date</label><input type="date" class="form-control" name="manufacturing_date"></div>
                           <div class="col-md-6 mb-3"><label class="form-label">Expiration Date</label><input type="date" class="form-control" name="expiration_date"></div>
                      </div>

                      <h5 class="section-title">Store & Delivery</h5>
                      <div class="row">
                        <div class="col-md-6 mb-3"><label class="form-label">Store Address Line 1 *</label><input type="text" class="form-control" name="store_address_line1" required></div>
                        <div class="col-md-6 mb-3"><label class="form-label">Store Address Line 2</label><input type="text" class="form-control" name="store_address_line2"></div>
                        <div class="col-md-3 mb-3"><label class="form-label">City *</label><input type="text" class="form-control" name="store_city" required></div>
                        <div class="col-md-3 mb-3"><label class="form-label">District *</label><input type="text" class="form-control" name="store_district" required></div>
                        <div class="col-md-3 mb-3"><label class="form-label">Postal Code *</label><input type="text" class="form-control" name="store_postal_code" required></div>
                        <div class="col-md-3 mb-3"><label class="form-label">Country *</label><input type="text" class="form-control" name="store_country" value="Sri Lanka" required></div>
                        <div class="col-md-4 mb-3"><label class="form-label">Delivery Fee (LKR)</label><input type="number" step="0.01" class="form-control" name="delivery_fee"></div>
                      </div>

                      <h5 class="section-title">Product Variants</h5>
                      <div id="variants-container">
                        <!-- Variant rows will be dynamically added here by JavaScript -->
                      </div>
                      <button type="button" class="btn btn-outline-primary mt-2" onclick="addVariantRow()">
                          <i class="fa fa-plus"></i> Add Variant
                      </button>
                      
                      <hr class="my-4">
                      <div class="d-flex justify-content-end">
                        <button type="button" class="btn btn-secondary me-2" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn save-btn"><i class="fa fa-check"></i> Add Product</button>
                      </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- Listing Edit Modal (dynamically loaded) -->
      <div class="modal fade" id="editListingModal" tabindex="-1">
        <div class="modal-dialog modal-xl modal-dialog-scrollable">
          <div class="modal-content" id="editListingModalContent"></div>
        </div>
      </div>
    </div>
  </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
document.addEventListener("DOMContentLoaded", function() {
  // Logic to show tab based on URL hash
  if(window.location.hash) {
    var triggerEl = document.querySelector('a[data-bs-toggle="tab"][href="' + window.location.hash + '"]');
    if (triggerEl) {
      var tab = new bootstrap.Tab(triggerEl);
      tab.show();
    }
  }
  // Logic to update URL hash when a tab is shown
  document.querySelectorAll('a[data-bs-toggle="tab"]').forEach(function(tabEl){
    tabEl.addEventListener('shown.bs.tab', function(e){
      history.replaceState(null, null, e.target.getAttribute('href'));
    });
  });

  // Initial data loads
  loadListings();
  loadOrders();
  loadReviews();
  loadMessages();
});

function renderCard(type, data) {
  let html = '';
  // Sanitize data to prevent rendering issues
  const safeData = (obj) => new Proxy(obj || {}, { get: (target, prop) => target[prop] || '' });
  const d = safeData(data);

  if (type === 'service') {
    html = `<div class="service-card">
      <img src="${d.profile_picture || 'https://placehold.co/84x70/EEE/31343C?text=Service'}" alt="Service">
      <div>
        <div style="font-weight:700;font-size:1em;">${d.name}</div>
        <div style="color:#5f6e7a;">Category: ${d.service_category_id}</div>
        <div><span class="badge bg-success">${d.approval_status}</span> <span class="badge bg-info">${d.availability_status}</span></div>
      </div>
      <div class="ms-auto ad-actions">
        <button class="btn btn-sm btn-outline-primary" onclick="openEditListing('service',${d.seller_id})"><i class="fa fa-edit"></i> Edit</button>
        <button class="btn btn-sm btn-outline-danger" onclick="deleteListing('service',${d.seller_id})"><i class="fa fa-trash"></i></button>
      </div>
    </div>`;
  } else if (type === 'vehicle') {
    html = `<div class="ad-card">
      <img src="${(d.vehicle_images.split(',')[0]) || 'https://placehold.co/84x70/EEE/31343C?text=Vehicle'}" alt="Vehicle">
      <div>
        <div style="font-weight:700;font-size:1em;">${d.vehicle_name}</div>
        <div style="color:#5f6e7a;">Brand: ${d.brand} | Model: ${d.model}</div>
        <div><span class="badge bg-info">${d.availability_status}</span> <span class="badge bg-success">${d.approval_status}</span></div>
      </div>
      <div class="ms-auto ad-actions">
        <button class="btn btn-sm btn-outline-primary" onclick="openEditListing('vehicle',${d.vehicle_id})"><i class="fa fa-edit"></i> Edit</button>
        <button class="btn btn-sm btn-outline-danger" onclick="deleteListing('vehicle',${d.vehicle_id})"><i class="fa fa-trash"></i></button>
      </div>
    </div>`;
  } else if (type === 'renting') {
    html = `<div class="ad-card">
      <img src="${(d.product_images.split(',')[0]) || 'https://placehold.co/84x70/EEE/31343C?text=Rent'}" alt="Renting">
      <div>
        <div style="font-weight:700;font-size:1em;">${d.product_name}</div>
        <div style="color:#5f6e7a;">Brand: ${d.brand} | Model: ${d.model}</div>
        <div><span class="badge bg-success">${d.availability_status}</span> <span class="badge bg-warning">${d.approval_status}</span></div>
      </div>
      <div class="ms-auto ad-actions">
        <button class="btn btn-sm btn-outline-primary" onclick="openEditListing('renting',${d.rent_id})"><i class="fa fa-edit"></i> Edit</button>
        <button class="btn btn-sm btn-outline-danger" onclick="deleteListing('renting',${d.rent_id})"><i class="fa fa-trash"></i></button>
      </div>
    </div>`;
  } else if (type === 'product') {
     html = `<div class="product-card">
      <img src="${d.thumbnail_url || 'https://placehold.co/84x70/EEE/31343C?text=Product'}" alt="Product">
      <div>
        <div style="font-weight:700;font-size:1em;">${d.product_name}</div>
        <div style="color:#5f6e7a;">Price: LKR ${d.price} | Stock: ${d.stock_quantity}</div>
        <div><span class="badge bg-primary">${d.brand}</span> <span class="badge bg-secondary">${d.product_condition}</span></div>
      </div>
      <div class="ms-auto ad-actions">
        <button class="btn btn-sm btn-outline-primary" onclick="openEditListing('product',${d.product_id})"><i class="fa fa-edit"></i> Edit</button>
        <button class="btn btn-sm btn-outline-danger" onclick="deleteListing('product',${d.product_id})"><i class="fa fa-trash"></i></button>
      </div>
    </div>`;
  }
  return html;
}

function loadListings() {
  fetch('get_listings.php') // This script must be updated to return products as well
    .then(r=>r.json())
    .then(data=>{
      let previewHTML = '';
      (data.services||[]).slice(0,1).forEach(s=>{previewHTML += `<div class="col-md-6">${renderCard('service',s)}</div>`;});
      (data.vehicles||[]).slice(0,1).forEach(v=>{previewHTML += `<div class="col-md-6">${renderCard('vehicle',v)}</div>`;});
      (data.rentings||[]).slice(0,1).forEach(r=>{previewHTML += `<div class="col-md-6">${renderCard('renting',r)}</div>`;});
      (data.products||[]).slice(0,1).forEach(p=>{previewHTML += `<div class="col-md-6">${renderCard('product',p)}</div>`;});
      document.getElementById('dashboardListingsPreview').innerHTML = previewHTML;

      let services = '', vehicles = '', rentings = '', products = '';
      (data.services||[]).forEach(s=>{services += renderCard('service',s);});
      (data.vehicles||[]).forEach(v=>{vehicles += renderCard('vehicle',v);});
      (data.rentings||[]).forEach(r=>{rentings += renderCard('renting',r);});
      (data.products||[]).forEach(p=>{products += renderCard('product',p);});
      
      document.getElementById('services-list').innerHTML = services||'<div class="alert alert-info">No services yet.</div>';
      document.getElementById('vehicles-list').innerHTML = vehicles||'<div class="alert alert-info">No vehicles yet.</div>';
      document.getElementById('rentings-list').innerHTML = rentings||'<div class="alert alert-info">No renting ads yet.</div>';
      document.getElementById('products-list').innerHTML = products||'<div class="alert alert-info">No marketplace products yet.</div>';
    })
    .catch(error => {
        console.error("Error loading listings:", error);
        document.getElementById('dashboardListingsPreview').innerHTML = '<div class="alert alert-danger">Could not load listing previews.</div>';
    });
}

function loadOrders() { /* ... existing code ... */ }
function loadReviews() { /* ... existing code ... */ }
function loadMessages() { /* ... existing code ... */ }

function deleteListing(type, id) {
  if (!confirm('Are you sure you want to delete this ' + type + '?')) return;
  let url = '';
  let param = '';
  if (type === 'service') { url = 'delete_service.php'; param = 'seller_id='+id; }
  else if (type === 'vehicle') { url = 'delete_vehicle.php'; param = 'vehicle_id='+id; }
  else if (type === 'renting') { url = 'delete_renting.php'; param = 'rent_id='+id; }
  else if (type === 'product') { url = 'delete_product.php'; param = 'product_id='+id; }
  else { return; }
  
  fetch(url, {method:'POST', headers:{'Content-Type':'application/x-www-form-urlencoded'}, body: param})
    .then(r=>r.text()).then(msg=>{
      alert(msg);
      loadListings();
    });
}

function openEditListing(type, id) {
  let url = '';
  if (type === 'service') url = 'dashboard-edit-service.php?id=' + id;
  else if (type === 'vehicle') url = 'dashboard-edit-vehicle.php?id=' + id;
  else if (type === 'renting') url = 'dashboard-edit-renting.php?id=' + id;
  else if (type === 'product') url = 'dashboard-edit-product.php?id=' + id;
  else { return; }

  fetch(url).then(r=>r.text()).then(html=>{
    document.getElementById('editListingModalContent').innerHTML = html;
    var modal = new bootstrap.Modal(document.getElementById('editListingModal'));
    modal.show();
  });
}

function addVariantRow() {
    const container = document.getElementById('variants-container');
    const newRow = document.createElement('div');
    newRow.classList.add('variant-row', 'row', 'g-2', 'mb-2', 'p-2', 'border', 'rounded', 'align-items-end');
    const variantIndex = container.children.length; // for unique names
    newRow.innerHTML = `
        <div class="col-md-3">
            <label class="form-label small">Variant Name</label>
            <input type="text" class="form-control form-control-sm" name="variants[${variantIndex}][name]" placeholder="e.g., Red, Large" required>
        </div>
        <div class="col-md-2">
            <label class="form-label small">Price</label>
            <input type="number" step="0.01" class="form-control form-control-sm" name="variants[${variantIndex}][price]" required>
        </div>
        <div class="col-md-2">
            <label class="form-label small">Stock</label>
            <input type="number" class="form-control form-control-sm" name="variants[${variantIndex}][stock]" required>
        </div>
        <div class="col-md-4">
            <label class="form-label small">Variant Image</label>
            <input type="file" class="form-control form-control-sm" name="variants[${variantIndex}][image]" accept="image/*">
        </div>
        <div class="col-md-1">
            <button type="button" class="btn btn-sm btn-outline-danger w-100" onclick="this.closest('.variant-row').remove()">
                <i class="fa fa-trash"></i>
            </button>
        </div>
    `;
    container.appendChild(newRow);
}

</script>
</body>
</html>
