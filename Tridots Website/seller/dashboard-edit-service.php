<?php
// If editing, load $service by id from DB, else leave empty for "Add"
$service = [];
if (!empty($_GET['id'])) {
  require_once __DIR__ . '/db.php';
  $stmt = $pdo->prepare("SELECT * FROM Service_Providers WHERE seller_id=?");
  $stmt->execute([$_GET['id']]);
  $service = $stmt->fetch(PDO::FETCH_ASSOC);
}
?>
<form id="serviceForm" enctype="multipart/form-data" method="post" action="save_service.php">
  <div class="mb-3">
    <label class="form-label">Select Profile Picture</label>
    <input type="file" class="form-control" name="profile_picture" accept="image/*">
    <?php if (!empty($service['profile_picture'])): ?>
      <img src="<?=htmlspecialchars($service['profile_picture'])?>" alt="Profile" class="mt-2 rounded" width="80">
    <?php endif; ?>
  </div>
  <div class="mb-3">
    <label class="form-label">Name</label>
    <input type="text" class="form-control" name="name" value="<?=htmlspecialchars($service['name']??'')?>" required>
  </div>
  <div class="mb-3">
    <label class="form-label">Service Category</label>
    <select class="form-select" name="service_category_id" required>
      <option value="">Select category</option>
      <option value="Painting" <?=($service['service_category_id']??'')=='Painting'?'selected':''?>>Painting</option>
      <option value="Plumbing" <?=($service['service_category_id']??'')=='Plumbing'?'selected':''?>>Plumbing</option>
      <!-- Add more categories as needed -->
    </select>
  </div>
  <div class="mb-3">
    <label class="form-label">Description</label>
    <textarea class="form-control" name="description" required><?=htmlspecialchars($service['description']??'')?></textarea>
  </div>
  <div class="mb-3">
    <label class="form-label">Contact Number</label>
    <input type="text" class="form-control" name="contact_number" value="<?=htmlspecialchars($service['contact_number']??'')?>" required>
  </div>
  <div class="mb-3">
    <label class="form-label">Email Address</label>
    <input type="email" class="form-control" name="email_address" value="<?=htmlspecialchars($service['email_address']??'')?>" required>
  </div>
  <div class="mb-3">
    <label class="form-label">Address</label>
    <input type="text" class="form-control" name="address" value="<?=htmlspecialchars($service['address']??'')?>" required>
  </div>
  <div class="mb-3">
    <label class="form-label">Experience (Years)</label>
    <input type="number" class="form-control" name="experience" value="<?=htmlspecialchars($service['experience']??'')?>" required>
  </div>
  <div class="mb-3">
    <label class="form-label">Qualifications (Optional)</label>
    <input type="text" class="form-control" name="qualifications" value="<?=htmlspecialchars($service['qualifications']??'')?>">
  </div>
  <div class="mb-3">
    <label class="form-label">Location (Optional)</label>
    <input type="text" class="form-control" name="location" value="<?=htmlspecialchars($service['location']??'')?>">
  </div>
  <div class="mb-3">
    <label class="form-label">Availability Status</label>
    <select class="form-select" name="availability_status" required>
      <option value="Available" <?=($service['availability_status']??'')=='Available'?'selected':''?>>Available</option>
      <option value="Unavailable" <?=($service['availability_status']??'')=='Unavailable'?'selected':''?>>Unavailable</option>
    </select>
  </div>
  <div class="d-flex justify-content-between">
    <?php if (!empty($service['seller_id'])): ?>
      <button type="button" class="btn btn-danger" onclick="deleteListing('service',<?=intval($service['seller_id'])?>)">Delete Profile</button>
    <?php endif; ?>
    <button type="submit" class="btn btn-success ms-auto">Save Changes</button>
  </div>
  <?php if (!empty($service['seller_id'])): ?>
    <input type="hidden" name="seller_id" value="<?=intval($service['seller_id'])?>">
  <?php endif; ?>
</form>