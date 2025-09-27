<?php
session_start();

// 1. Authentication Check: Ensure the user is logged in.
if (!isset($_SESSION['user_id'])) {
    header('Location: login.php');
    exit;
}

// 2. Database Connection
require_once 'db.php';

// 3. Fetch Inquiries for the Logged-in User
$inquiries = [];
$userId = $_SESSION['user_id'];

try {
    // Prepare a statement to select all inquiries made by the current user
    // Ordering by the most recent date first is user-friendly.
    $stmt = $pdo->prepare(
        "SELECT inquiry_id, ad_type, ad_id, inquiry_message, inquiry_date, status 
         FROM inquiries 
         WHERE user_id = ? 
         ORDER BY inquiry_date DESC"
    );
    $stmt->execute([$userId]);
    
    // Fetch all inquiries into an array
    $inquiries = $stmt->fetchAll(PDO::FETCH_ASSOC);

} catch (PDOException $e) {
    // Log the error for the developer and show a generic message to the user
    error_log("Database Error fetching inquiries: " . $e->getMessage());
    die("An error occurred. Please try again later.");
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Inquiries</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <style>
        /* Using the same color scheme and style from your dashboard for consistency */
        :root {
            --primary-color: #00715A;
            --accent-color: #00B07D;
            --background-color: #f7fafc;
            --card-background-color: #ffffff;
            --text-color: #2d3748;
            --text-color-light: #5a677b;
            --border-color: #e2e8f0;
            --shadow-md: 0 5px 15px -3px rgba(0,113,90,0.08), 0 3px 8px -3px rgba(0,113,90,0.05);
            --radius-lg: 1rem;
        }

        * { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'Inter', Arial, sans-serif;
            background-color: var(--background-color);
            color: var(--text-color);
            line-height: 1.6;
        }

        .container {
            max-width: 900px;
            margin: 0 auto;
            padding: 20px;
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            padding-bottom: 15px;
            border-bottom: 1px solid var(--border-color);
        }

        .header h1 {
            color: var(--primary-color);
            font-size: 2rem;
        }

        .header a.back-btn {
            color: var(--primary-color);
            background-color: #e8f6f1;
            padding: 8px 16px;
            border-radius: 0.5rem;
            text-decoration: none;
            font-weight: 600;
            transition: background-color 0.2s ease;
        }
        .header a.back-btn:hover {
             background-color: #d1e7e1;
        }
        
        .inquiry-card {
            background: var(--card-background-color);
            border-radius: var(--radius-lg);
            box-shadow: var(--shadow-md);
            margin-bottom: 20px;
            padding: 25px;
            border-left: 5px solid var(--primary-color);
        }

        .inquiry-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
            color: var(--text-color-light);
            font-size: 0.9rem;
        }
        
        .inquiry-header .inquiry-date {
            font-weight: 500;
        }
        
        .status-badge {
            padding: 4px 12px;
            border-radius: 1rem;
            font-weight: 600;
            font-size: 0.8rem;
            text-transform: uppercase;
        }

        .status-badge.pending {
            background-color: #fffbeb;
            color: #d97706;
        }

        .status-badge.answered {
            background-color: #ecfdf5;
            color: #059669;
        }
        
        .inquiry-message {
            margin-bottom: 15px;
        }

        .inquiry-ref {
            font-size: 0.9rem;
            color: var(--text-color-light);
            padding-top: 15px;
            border-top: 1px solid var(--border-color);
            margin-top: 15px;
        }
        
        .no-inquiries {
            text-align: center;
            padding: 50px;
            background-color: var(--card-background-color);
            border-radius: var(--radius-lg);
            box-shadow: var(--shadow-md);
        }
        .no-inquiries i {
            font-size: 3rem;
            color: var(--primary-color);
            margin-bottom: 15px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>My Inquiries</h1>
            <a href="dashboard.php" class="back-btn"><i class="fas fa-arrow-left"></i> Back to Dashboard</a>
        </div>

        <?php if (empty($inquiries)): ?>
            <div class="no-inquiries">
                <i class="fas fa-comments-dollar"></i>
                <h2>No Inquiries Found</h2>
                <p>You have not made any inquiries yet. Browse the marketplace to get started!</p>
            </div>
        <?php else: ?>
            <?php foreach ($inquiries as $inquiry): ?>
                <div class="inquiry-card">
                    <div class="inquiry-header">
                        <span class="inquiry-date">
                            <i class="fas fa-calendar-alt"></i> 
                            <?php echo htmlspecialchars(date('F j, Y, g:i a', strtotime($inquiry['inquiry_date']))); ?>
                        </span>
                        <span class="status-badge <?php echo htmlspecialchars(strtolower($inquiry['status'])); ?>">
                            <?php echo htmlspecialchars($inquiry['status']); ?>
                        </span>
                    </div>
                    <div class="inquiry-message">
                        <p><?php echo nl2br(htmlspecialchars($inquiry['inquiry_message'])); ?></p>
                    </div>
                    <div class="inquiry-ref">
                        Inquiry regarding: <strong><?php echo htmlspecialchars(ucfirst(str_replace('_', ' ', $inquiry['ad_type']))); ?></strong> (ID: #<?php echo htmlspecialchars($inquiry['ad_id']); ?>)
                    </div>
                </div>
            <?php endforeach; ?>
        <?php endif; ?>
    </div>
</body>
</html>
