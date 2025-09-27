<?php
$renting = [];
if (!empty($_GET['id'])) {
  require_once __DIR__ . '/db.php';
  $stmt = $pdo->prepare("SELECT * FROM renting WHERE rent_id=?");
  $stmt->execute([$_GET['id']]);
  $renting = $stmt->fetch(PDO::FETCH_ASSOC);
}
?>
<form id="rentingForm" enctype="multipart/form-data" method="post" action="save_renting.php">
  <div class="mb-3">
    <label class="form-label">Select Image</label>
    <input type="file" class="form-control" name="product_images[]" multiple accept="image/*">
    <?php if (!empty($renting['product_images'])): foreach (explode(',',$renting['product_images']) as $img): ?>
      <img src="<?=htmlspecialchars($img)?>" alt="Product" class="mt-2 rounded" width="80">
    <?php endforeach; endif; ?>
  </div>
  <div class="mb-3">
    <label class="form-label">Product Name</label>
    <input type="text" class="form-control" name="product_name" value="<?=htmlspecialchars($renting['product_name']??'')?>" required>
  </div>
  <div class="mb-3">
    <label class="form-label">Brand</label>
    <input type="text" class="form-control" name="brand" value="<?=htmlspecialchars($renting['brand']??'')?>" required>
  </div>
  <div class="mb-3">
    <label class="form-label">Model (Optional)</label>
    <input type="text" class="form-control" name="model" value="<?=htmlspecialchars($renting['model']??'')?>">
  </div>
  <div class="mb-3">
    <label class="form-label">Description</label>
    <textarea class="form-control" name="description" required><?=htmlspecialchars($renting['description']??'')?></textarea>
  </div>
  <div class="mb-3">
    <label class="form-label">Keywords (Optional)</label>
    <input type="text" class="form-control" name="keywords" value="<?=htmlspecialchars($renting['keywords']??'')?>">
  </div>
  <div class="mb-3">
    <label class="form-label">Price Per Hour (Optional)</label>
    <input type="number" class="form-control" name="price_per_hour" value="<?=htmlspecialchars($renting['price_per_hour']??'')?>">
  </div>
  <div class="mb-3">
    <label class="form-label">Price Per Day (Optional)</label>
    <input type="number" class="form-control" name="price" value="<?=htmlspecialchars($renting['price']??'')?>">
  </div>
  <div class="mb-3">
    <label class="form-label">Location (Optional)</label>
    <input type="text" class="form-control" name="location" value="<?=htmlspecialchars($renting['location']??'')?>">
  </div>
  <div class="mb-3">
    <label class="form-label">Availability Status</label>
    <select class="form-select" name="availability_status" required>
      <option value="Available" <?=($renting['availability_status']??'')=='Available'?'selected':''?>>Available</option>
      <option value="Unavailable" <?=($renting['availability_status']??'')=='Unavailable'?'selected':''?>>Unavailable</option>
    </select>
  </div>
  <div class="d-flex justify-content-between">
    <?php if (!empty($renting['rent_id'])): ?>
      <button type="button" class="btn btn-danger" onclick="deleteListing('renting',<?=intval($renting['rent_id'])?>)">Delete Ad</button>
    <?php endif; ?>
    <button type="submit" class="btn btn-success ms-auto">Save Changes</button>
  </div>
  <?php if (!empty($renting['rent_id'])): ?>
    <input type="hidden" name="rent_id" value="<?=intval($renting['rent_id'])?>">
  <?php endif; ?>
</form>