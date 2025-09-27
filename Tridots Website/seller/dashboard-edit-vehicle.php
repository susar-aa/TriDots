<?php
$vehicle = [];
if (!empty($_GET['id'])) {
  require_once __DIR__ . '/db.php';
  $stmt = $pdo->prepare("SELECT * FROM Vehicles WHERE vehicle_id=?");
  $stmt->execute([$_GET['id']]);
  $vehicle = $stmt->fetch(PDO::FETCH_ASSOC);
}
?>
<form id="vehicleForm" enctype="multipart/form-data" method="post" action="save_vehicle.php">
  <div class="mb-3">
    <label class="form-label">Select Image</label>
    <input type="file" class="form-control" name="vehicle_images[]" multiple accept="image/*">
    <?php if (!empty($vehicle['vehicle_images'])): foreach (explode(',',$vehicle['vehicle_images']) as $img): ?>
      <img src="<?=htmlspecialchars($img)?>" alt="Vehicle" class="mt-2 rounded" width="80">
    <?php endforeach; endif; ?>
  </div>
  <div class="mb-3">
    <label class="form-label">Vehicle Name</label>
    <input type="text" class="form-control" name="vehicle_name" value="<?=htmlspecialchars($vehicle['vehicle_name']??'')?>" required>
  </div>
  <div class="mb-3">
    <label class="form-label">Brand</label>
    <input type="text" class="form-control" name="brand" value="<?=htmlspecialchars($vehicle['brand']??'')?>" required>
  </div>
  <div class="mb-3">
    <label class="form-label">Model</label>
    <input type="text" class="form-control" name="model" value="<?=htmlspecialchars($vehicle['model']??'')?>" required>
  </div>
  <div class="mb-3">
    <label class="form-label">Description</label>
    <textarea class="form-control" name="description" required><?=htmlspecialchars($vehicle['description']??'')?></textarea>
  </div>
  <div class="mb-3">
    <label class="form-label">Capacity</label>
    <input type="text" class="form-control" name="capacity" value="<?=htmlspecialchars($vehicle['capacity']??'')?>" required>
  </div>
  <div class="mb-3">
    <label class="form-label">Fuel Type</label>
    <select class="form-select" name="fuel_type" required>
      <option value="">Select</option>
      <option value="Petrol" <?=($vehicle['fuel_type']??'')=='Petrol'?'selected':''?>>Petrol</option>
      <option value="Diesel" <?=($vehicle['fuel_type']??'')=='Diesel'?'selected':''?>>Diesel</option>
      <option value="Electric" <?=($vehicle['fuel_type']??'')=='Electric'?'selected':''?>>Electric</option>
      <option value="Hybrid" <?=($vehicle['fuel_type']??'')=='Hybrid'?'selected':''?>>Hybrid</option>
    </select>
  </div>
  <div class="mb-3">
    <label class="form-label">Transmission Type</label>
    <select class="form-select" name="transmission_type" required>
      <option value="">Select</option>
      <option value="Manual" <?=($vehicle['transmission_type']??'')=='Manual'?'selected':''?>>Manual</option>
      <option value="Automatic" <?=($vehicle['transmission_type']??'')=='Automatic'?'selected':''?>>Automatic</option>
    </select>
  </div>
  <div class="mb-3">
    <label class="form-label">Location</label>
    <input type="text" class="form-control" name="location" value="<?=htmlspecialchars($vehicle['location']??'')?>" required>
  </div>
  <div class="mb-3">
    <label class="form-label">Price Type</label>
    <select class="form-select" name="price_type" required>
      <option value="per_km" <?=($vehicle['price_type']??'')=='per_km'?'selected':''?>>Per KM</option>
      <option value="per_day" <?=($vehicle['price_type']??'')=='per_day'?'selected':''?>>Per Day</option>
      <option value="fixed" <?=($vehicle['price_type']??'')=='fixed'?'selected':''?>>Fixed</option>
    </select>
  </div>
  <div class="mb-3">
    <label class="form-label">Amount</label>
    <input type="number" class="form-control" name="amount" value="<?=htmlspecialchars($vehicle['amount']??'')?>" required>
  </div>
  <div class="mb-3">
    <label class="form-label">Keywords (Optional)</label>
    <input type="text" class="form-control" name="keywords" value="<?=htmlspecialchars($vehicle['keywords']??'')?>">
  </div>
  <div class="mb-3">
    <label class="form-label">Availability Status</label>
    <select class="form-select" name="availability_status" required>
      <option value="Available" <?=($vehicle['availability_status']??'')=='Available'?'selected':''?>>Available</option>
      <option value="Unavailable" <?=($vehicle['availability_status']??'')=='Unavailable'?'selected':''?>>Unavailable</option>
    </select>
  </div>
  <div class="d-flex justify-content-between">
    <?php if (!empty($vehicle['vehicle_id'])): ?>
      <button type="button" class="btn btn-danger" onclick="deleteListing('vehicle',<?=intval($vehicle['vehicle_id'])?>)">Delete Ad</button>
    <?php endif; ?>
    <button type="submit" class="btn btn-success ms-auto">Save Changes</button>
  </div>
  <?php if (!empty($vehicle['vehicle_id'])): ?>
    <input type="hidden" name="vehicle_id" value="<?=intval($vehicle['vehicle_id'])?>">
  <?php endif; ?>
</form>