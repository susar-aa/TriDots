<?php
// =================================================================
// RECEIPT PAGE (TRIDOTS STYLE, WEB)
// Created on: Saturday, 5th July 2025
// Based on: ReceiptActivity.java
// =================================================================

// --- AUTHENTICATION & SETUP ---
session_start();

// Ensure user is logged in
if (!isset($_SESSION['user_signed_in']) || $_SESSION['user_signed_in'] !== true) {
    header("Location: login.php");
    exit();
}

// Get Order ID from URL parameter
$orderId = filter_input(INPUT_GET, 'order_id', FILTER_VALIDATE_INT);
if (!$orderId) {
    die("Error: Invalid or missing Order ID.");
}

// --- API & HELPER FUNCTIONS ---
$api_base_url = "https://lionsgoldencircle.com/Tridots/Api/";

function makeApiPostRequest($url, $params) {
    $options = ['http' => ['header'  => "Content-type: application/x-www-form-urlencoded\r\n", 'method'  => 'POST', 'content' => http_build_query($params), 'timeout' => 10]];
    $context  = stream_context_create($options);
    $result = @file_get_contents($url, false, $context);
    return $result;
}

// Fetches detailed information for a single order
function fetchOrderDetails($orderId) {
    global $api_base_url;
    $result = makeApiPostRequest($api_base_url . 'get_order_details.php', ['order_id' => $orderId]);
    if ($result === false) return null;
    $json = json_decode($result, true);
    // As per Java code, we expect a 'success' flag and an 'order' object
    return ($json && isset($json['success']) && $json['success'] && isset($json['order'])) ? $json['order'] : null;
}

// --- DATA FETCHING ---
$order = fetchOrderDetails($orderId);

// If order fetching fails, display an error
if (!$order) {
    die("Error: Could not retrieve order details. The order may not exist or an API error occurred.");
}

// --- DATA PREPARATION ---
$customerName = htmlspecialchars($order['customer_name'] ?? 'N/A');
$address = $order['delivery_address'] ?? [];
$fullAddress = htmlspecialchars($address['full_address'] ?? 'N/A');
$city = htmlspecialchars($address['city'] ?? 'N/A');
$postalCode = htmlspecialchars($address['postal_code'] ?? 'N/A');
$orderItems = $order['items'] ?? [];

?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Receipt for Order #<?php echo htmlspecialchars($order['order_id'] ?? ''); ?></title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600;700&display=swap" rel="stylesheet">
    <script type="module" src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.esm.js"></script>
    <script nomodule src="https://unpkg.com/ionicons@7.1.0/dist/ionicons/ionicons.js"></script>
    <style>
        :root {
            --primary-green: #00715A;
            --background-color: #f0f2f5;
            --card-background: #fff;
            --text-dark: #2c3e50;
            --text-light: #7f8c8d;
            --border-color: #dee2e6;
        }
        body {
            background-color: var(--background-color);
            font-family: 'Inter', sans-serif;
            color: var(--text-dark);
            margin: 0;
            padding: 20px;
        }
        .receipt-container {
            max-width: 800px;
            margin: 20px auto;
            background: var(--card-background);
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        }
        .receipt-header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            border-bottom: 2px solid var(--border-color);
            padding-bottom: 20px;
            margin-bottom: 30px;
        }
        .receipt-header .company-details h1 {
            margin: 0;
            color: var(--primary-green);
            font-size: 2rem;
        }
        .receipt-header .company-details p {
            margin: 5px 0 0;
            color: var(--text-light);
        }
        .receipt-header .receipt-info h2 {
            margin: 0;
            text-align: right;
            font-size: 1.8rem;
        }
        .receipt-header .receipt-info p {
            margin: 5px 0 0;
            text-align: right;
            color: var(--text-light);
        }
        .customer-details {
            margin-bottom: 30px;
        }
        .customer-details h3 {
            margin-bottom: 10px;
            border-bottom: 1px solid #eee;
            padding-bottom: 8px;
        }
        .items-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 30px;
        }
        .items-table th, .items-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid var(--border-color);
        }
        .items-table th {
            background-color: #f8f9fa;
            font-weight: 600;
        }
        .items-table td.number { text-align: right; }
        .receipt-summary {
            display: flex;
            justify-content: flex-end;
        }
        .summary-table {
            width: 50%;
            max-width: 350px;
        }
        .summary-table td {
            padding: 8px 12px;
        }
        .summary-table .label {
            color: var(--text-light);
            font-weight: 600;
        }
        .summary-table .value {
            text-align: right;
            font-weight: 600;
        }
        .summary-table .total .value {
            font-size: 1.2rem;
            color: var(--primary-green);
        }
        .receipt-footer {
            text-align: center;
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #eee;
            color: var(--text-light);
        }
        .actions {
            text-align: center;
            margin: 40px 0 20px;
        }
        .actions a, .actions button {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            padding: 12px 24px;
            border-radius: 50px;
            border: none;
            background: var(--primary-green);
            color: #fff;
            text-decoration: none;
            font-weight: 600;
            cursor: pointer;
            transition: background .2s;
            margin: 0 10px;
        }
        .actions a.secondary, .actions button.secondary {
            background: #6c757d;
        }
        .actions a:hover, .actions button:hover {
            filter: brightness(1.1);
        }

        @media print {
            body {
                background-color: #fff;
                padding: 0;
            }
            .receipt-container {
                box-shadow: none;
                margin: 0;
                max-width: 100%;
                border-radius: 0;
            }
            .actions {
                display: none;
            }
        }
    </style>
</head>
<body>

    <div class="actions">
        <a href="profile.php" class="secondary"><ion-icon name="arrow-back-outline"></ion-icon> Back to Profile</a>
        <button onclick="window.print();"><ion-icon name="print-outline"></ion-icon> Print / Save as PDF</button>
    </div>

    <div class="receipt-container">
        <header class="receipt-header">
            <div class="company-details">
                <h1>TRIDOTS</h1>
                <p>Your Trusted Marketplace</p>
            </div>
            <div class="receipt-info">
                <h2>RECEIPT</h2>
                <p><strong>Order ID:</strong> #<?php echo htmlspecialchars($order['order_id']); ?></p>
                <p><strong>Date:</strong> <?php echo htmlspecialchars($order['order_date']); ?></p>
            </div>
        </header>

        <section class="customer-details">
            <h3>Billed To:</h3>
            <p>
                <strong><?php echo $customerName; ?></strong><br>
                <?php echo $fullAddress; ?><br>
                <?php echo $city; ?>, <?php echo $postalCode; ?>
            </p>
        </section>

        <section>
            <table class="items-table">
                <thead>
                    <tr>
                        <th>Item Description</th>
                        <th class="number">Quantity</th>
                        <th class="number">Unit Price</th>
                        <th class="number">Total</th>
                    </tr>
                </thead>
                <tbody>
                    <?php foreach ($orderItems as $item): 
                        $itemName = htmlspecialchars($item['product_name'] ?? 'N/A');
                        // Append variant name if it exists
                        if (!empty($item['variant_name']) && strtolower($item['variant_name']) !== 'null') {
                            $itemName .= ' (' . htmlspecialchars($item['variant_name']) . ')';
                        }
                        $quantity = (int)($item['quantity'] ?? 0);
                        $unitPrice = (float)($item['unit_price'] ?? 0.00);
                        $itemTotal = $quantity * $unitPrice;
                    ?>
                    <tr>
                        <td><?php echo $itemName; ?></td>
                        <td class="number"><?php echo $quantity; ?></td>
                        <td class="number">LKR <?php echo number_format($unitPrice, 2); ?></td>
                        <td class="number">LKR <?php echo number_format($itemTotal, 2); ?></td>
                    </tr>
                    <?php endforeach; ?>
                </tbody>
            </table>
        </section>

        <section class="receipt-summary">
            <table class="summary-table">
                <tr>
                    <td class="label">Subtotal</td>
                    <td class="value">LKR <?php echo number_format($order['sub_total_amount'] ?? 0, 2); ?></td>
                </tr>
                <tr>
                    <td class="label">Delivery Fee</td>
                    <td class="value">LKR <?php echo number_format($order['delivery_fee'] ?? 0, 2); ?></td>
                </tr>
                <tr class="total">
                    <td class="label">Total Amount</td>
                    <td class="value">LKR <?php echo number_format($order['total_amount'] ?? 0, 2); ?></td>
                </tr>
            </table>
        </section>
        
        <footer class="receipt-footer">
            <p>Thank you for your business!</p>
            <p>Payment Method: <?php echo htmlspecialchars($order['payment_method'] ?? 'N/A'); ?> | Payment Status: <?php echo htmlspecialchars($order['payment_status'] ?? 'N/A'); ?></p>
        </footer>
    </div>

</body>
</html>
