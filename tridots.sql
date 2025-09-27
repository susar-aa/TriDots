-- phpMyAdmin SQL Dump
-- version 5.2.2
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Sep 27, 2025 at 05:19 PM
-- Server version: 5.5.68-MariaDB
-- PHP Version: 8.3.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `tridots`
--

-- --------------------------------------------------------

--
-- Table structure for table `auth_tokens`
--

CREATE TABLE `auth_tokens` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `selector` varchar(255) NOT NULL,
  `validator_hash` varchar(255) NOT NULL,
  `expires_at` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `auth_tokens`
--

INSERT INTO `auth_tokens` (`id`, `user_id`, `selector`, `validator_hash`, `expires_at`) VALUES
(1, 1, '9bafcde630ebc20fbc65cd543c8cb23e', '$2y$10$9GhAvM/IRoLJP.uXUQyTs.8NoXujrKf52op68.SMyBjw8OlQ2qNFu', '2025-07-31 20:10:44');

-- --------------------------------------------------------

--
-- Table structure for table `banner_images`
--

CREATE TABLE `banner_images` (
  `id` int(11) NOT NULL,
  `image_path` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `banner_images`
--

INSERT INTO `banner_images` (`id`, `image_path`) VALUES
(1, '/Tridots/images/Ads/car_rent.png'),
(2, '/Tridots/images/Ads/bmw.png'),
(3, '/Tridots/images/Ads/bmwm5.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `BusinessProfiles`
--

CREATE TABLE `BusinessProfiles` (
  `business_profile_id` int(11) UNSIGNED NOT NULL,
  `user_id` int(11) NOT NULL,
  `email_address` varchar(255) NOT NULL,
  `contact_number` varchar(50) DEFAULT NULL,
  `logo` text,
  `address` text,
  `verification_status` enum('Verified','Not Verified') DEFAULT 'Not Verified',
  `is_active` enum('Active','Deactive') DEFAULT 'Active',
  `business_name` varchar(255) DEFAULT NULL,
  `bio_description` text,
  `website_url` varchar(255) DEFAULT NULL,
  `whatsapp_number` varchar(50) DEFAULT NULL,
  `operating_hours_days` text,
  `instagram_link` varchar(255) DEFAULT NULL,
  `facebook_link` varchar(255) DEFAULT NULL,
  `rating_count` int(11) UNSIGNED DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `BusinessProfiles`
--

INSERT INTO `BusinessProfiles` (`business_profile_id`, `user_id`, `email_address`, `contact_number`, `logo`, `address`, `verification_status`, `is_active`, `business_name`, `bio_description`, `website_url`, `whatsapp_number`, `operating_hours_days`, `instagram_link`, `facebook_link`, `rating_count`) VALUES
(1, 1, 'contact@rentmachinery.lk', '+94771234567', 'https://lionsgoldencircle.com/Tridots/images/Ads/renting_image_1747503098846.jpg', '123, Galle Road, Colombo 03', 'Verified', 'Active', 'Rent Machinery Lanka', 'Specialized in heavy-duty equipment rentals across Sri Lanka.', 'https://rentmachinery.lk', '+94771234567', 'Mon-Fri: 9am-6pm', 'https://instagram.com/rentmachinery', 'https://facebook.com/rentmachinery', 35),
(3, 3, 'support@digitechtools.lk', NULL, NULL, NULL, 'Verified', 'Deactive', 'DigiTech Tools', 'Your digital partner for modern construction equipment.', 'https://digitechtools.lk', NULL, NULL, NULL, NULL, 5);

-- --------------------------------------------------------

--
-- Table structure for table `feedbacks`
--

CREATE TABLE `feedbacks` (
  `feedback_id` int(11) UNSIGNED NOT NULL,
  `user_id` int(11) NOT NULL,
  `feedback_type` enum('renting','vehicles','service_provider') NOT NULL,
  `item_id` int(11) NOT NULL,
  `rating` tinyint(3) UNSIGNED NOT NULL,
  `comment` text,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `feedbacks`
--

INSERT INTO `feedbacks` (`feedback_id`, `user_id`, `feedback_type`, `item_id`, `rating`, `comment`, `created_at`) VALUES
(64, 1, 'service_provider', 206, 3, 'gg', '2025-05-19 09:51:45'),
(65, 1, 'service_provider', 206, 3, 'f', '2025-05-19 09:52:47'),
(66, 1, 'service_provider', 207, 5, 'supaqqq', '2025-05-19 09:52:57'),
(67, 1, 'renting', 1, 3, 'gg', '2025-05-19 14:43:41'),
(68, 1, 'vehicles', 102, 5, 'el bosaaa', '2025-05-19 15:38:19');

-- --------------------------------------------------------

--
-- Table structure for table `inquiries`
--

CREATE TABLE `inquiries` (
  `inquiry_id` int(11) UNSIGNED NOT NULL,
  `user_id` int(11) NOT NULL,
  `ad_type` enum('renting','vehicles','service_providers') NOT NULL,
  `ad_id` int(11) NOT NULL,
  `ad_owner_id` int(11) NOT NULL,
  `inquirer_name` varchar(255) NOT NULL,
  `inquirer_email` varchar(255) NOT NULL,
  `inquirer_phone` varchar(20) DEFAULT NULL,
  `inquiry_message` text NOT NULL,
  `inquiry_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` enum('Pending','Replied','Confirmed','Closed','To Review') DEFAULT 'Pending',
  `estimated_cost` decimal(10,2) DEFAULT NULL,
  `seller_reply` text,
  `seller_reply_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `inquiries`
--

INSERT INTO `inquiries` (`inquiry_id`, `user_id`, `ad_type`, `ad_id`, `ad_owner_id`, `inquirer_name`, `inquirer_email`, `inquirer_phone`, `inquiry_message`, `inquiry_date`, `status`, `estimated_cost`, `seller_reply`, `seller_reply_date`) VALUES
(16, 32, 'renting', 1, 1, 'Ishanga Mallawarachchi', 'santalk@gmail.com', '0761197766', 'I need to buy this', '2025-05-19 18:30:21', 'Pending', NULL, NULL, '0000-00-00 00:00:00'),
(17, 32, 'vehicles', 102, 1, 'Zimba', 'zimba@gmail.com', '', 'I need to test this broom', '2025-05-19 18:31:21', 'Pending', NULL, NULL, '0000-00-00 00:00:00'),
(18, 32, 'service_providers', 206, 1, 'Millie', 'millie@gmail.com', '', 'I need to Hire you. how much is for hour?', '2025-05-19 18:31:55', 'Pending', NULL, NULL, '0000-00-00 00:00:00'),
(19, 1, 'renting', 4, 2, 'Susss', 'suz.x2006@gmail.com', '', 'for me', '2025-05-19 19:10:36', 'Pending', NULL, NULL, '0000-00-00 00:00:00'),
(20, 1, 'renting', 2, 2, 'sajabumthqaaa', 'srvtbtbt', '8585', 'svebtbtnyn', '2025-06-08 17:07:12', 'Pending', NULL, NULL, '0000-00-00 00:00:00'),
(22, 1, 'renting', 3, 3, 'Susara Senarathne', 'suz.x2006@gmail.com', '0761407875', 'Is this Available?', '2025-07-10 17:06:51', 'To Review', NULL, NULL, '0000-00-00 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `main_category`
--

CREATE TABLE `main_category` (
  `main_category_id` int(11) NOT NULL,
  `main_category` varchar(255) NOT NULL,
  `type` enum('renting','service','vehicle') NOT NULL,
  `category_description` text,
  `category_icon` varchar(500) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `main_category`
--

INSERT INTO `main_category` (`main_category_id`, `main_category`, `type`, `category_description`, `category_icon`) VALUES
(1, 'Heavy Machine', 'renting', 'Large machinery available for rent such as excavators and cranes.', 'https://example.com/icons/heavy_machine.png'),
(2, 'Office Machine', 'renting', 'Office equipment like printers and copiers with maintenance service.', 'https://example.com/icons/office_machine.png'),
(3, 'Vehicles', 'renting', 'Vehicles available for hire or lease.', 'https://example.com/icons/vehicles.png');

-- --------------------------------------------------------

--
-- Table structure for table `Marketplace`
--

CREATE TABLE `Marketplace` (
  `product_id` int(11) NOT NULL,
  `seller_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `product_description` text NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `stock_quantity` int(11) NOT NULL,
  `sku` varchar(50) DEFAULT NULL,
  `product_status` varchar(50) NOT NULL DEFAULT 'active',
  `product_condition` enum('New','Used') NOT NULL,
  `posted_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_updated_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `approved_status` enum('Approved','Pending','Rejected') NOT NULL DEFAULT 'Pending',
  `availability_status` enum('Available','Not available') NOT NULL DEFAULT 'Available',
  `keywords` text,
  `brand` varchar(100) DEFAULT NULL,
  `model` varchar(100) DEFAULT NULL,
  `compatibility` text,
  `warranty_policy` text,
  `weight_g` int(11) DEFAULT NULL,
  `dimensions_cm` varchar(50) DEFAULT NULL,
  `material` varchar(100) DEFAULT NULL,
  `color` varchar(50) DEFAULT NULL,
  `manufacturing_date` date DEFAULT NULL,
  `expiration_date` date DEFAULT NULL,
  `store_address_line1` varchar(255) NOT NULL,
  `store_address_line2` varchar(255) DEFAULT NULL,
  `store_city` varchar(100) NOT NULL,
  `store_district` varchar(100) NOT NULL,
  `store_postal_code` varchar(20) NOT NULL,
  `store_country` varchar(50) NOT NULL DEFAULT 'Sri Lanka',
  `delivery_fee` decimal(10,2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Marketplace`
--

INSERT INTO `Marketplace` (`product_id`, `seller_id`, `category_id`, `product_name`, `product_description`, `price`, `stock_quantity`, `sku`, `product_status`, `product_condition`, `posted_date`, `last_updated_date`, `approved_status`, `availability_status`, `keywords`, `brand`, `model`, `compatibility`, `warranty_policy`, `weight_g`, `dimensions_cm`, `material`, `color`, `manufacturing_date`, `expiration_date`, `store_address_line1`, `store_address_line2`, `store_city`, `store_district`, `store_postal_code`, `store_country`, `delivery_fee`) VALUES
(23, 75, 1, 'ProSound Wireless Headphones', 'High-fidelity wireless headphones with noise-cancellation and 20-hour battery life.', 18500.00, 15, 'PS-HP-2024-BLK', 'active', 'New', '2025-07-07 18:02:55', '0000-00-00 00:00:00', 'Approved', 'Available', 'headphones, wireless, audio, bluetooth', 'ProSound', 'X5-Series', NULL, '1 Year Manufacturer Warranty', 250, '20x18x8', 'Plastic, Faux Leather', 'Matte Black', '2024-05-01', NULL, 'No. 42, Main Street', NULL, 'Kurunegala', 'Kurunegala', '60000', 'Sri Lanka', 350.00),
(24, 75, 3, 'Galaxy A55 5G', 'Used Samsung Galaxy A55 5G, 128GB storage, 8GB RAM. Minor scratches on the back.', 75000.00, 1, 'SAM-A55-USE-128', 'active', 'Used', '2025-07-07 18:02:55', '0000-00-00 00:00:00', 'Pending', 'Available', 'samsung, galaxy, mobile, phone, used', 'Samsung', 'Galaxy A55', NULL, 'No Warranty', 180, '15.8x7.7x0.8', 'Glass, Aluminum', 'Awesome Iceblue', '2023-03-15', NULL, 'No. 42, Main Street', NULL, 'Kurunegala', 'Kurunegala', '60000', 'Sri Lanka', 300.00),
(25, 75, 2, 'Men\'s Linen Casual Shirt', 'Comfortable and breathable long-sleeve linen shirt, perfect for tropical weather.', 4500.00, 30, 'LS-SHIRT-MEN-L-WHT', 'active', 'New', '2025-07-07 18:02:55', '0000-00-00 00:00:00', 'Approved', 'Available', 'shirt, linen, mens fashion, casual wear', 'LinenLux', 'Casual Comfort', NULL, NULL, 200, 'L (42)', 'Linen', 'White', '2024-06-10', NULL, 'No. 42, Main Street', NULL, 'Kurunegala', 'Kurunegala', '60000', 'Sri Lanka', 300.00),
(26, 75, 4, 'The Pearl', 'A classic novel by John Steinbeck. Paperback, good condition.', 1200.00, 10, 'BOOK-STEIN-PEARL', 'active', 'Used', '2025-07-07 18:02:55', '0000-00-00 00:00:00', 'Approved', 'Available', 'book, novel, fiction, classic, steinbeck', 'Penguin Books', NULL, NULL, NULL, 150, '13x1x20', 'Paper', NULL, NULL, NULL, 'No. 42, Main Street', NULL, 'Kurunegala', 'Kurunegala', '60000', 'Sri Lanka', 250.00),
(27, 75, 5, 'Professional Cricket Bat', 'Full-size English Willow cricket bat. Grade 2. Excellent for club matches.', 22000.00, 5, 'CKT-BAT-ENGW-G2', 'active', 'New', '2025-07-07 18:02:55', '0000-00-00 00:00:00', 'Approved', 'Available', 'cricket, bat, sports, english willow', 'SG', 'Max-Cover', NULL, NULL, 1150, '85x11x6', 'English Willow', 'Natural Wood', '2024-04-20', NULL, 'No. 42, Main Street', NULL, 'Kurunegala', 'Kurunegala', '60000', 'Sri Lanka', 500.00),
(28, 75, 1, 'Smart Power Bank 20000mAh', 'Fast charging 20000mAh power bank with dual USB-C and USB-A ports.', 8900.00, 25, 'PB-20K-FST-CHG', 'active', 'New', '2025-07-07 18:02:55', '0000-00-00 00:00:00', 'Approved', 'Available', 'power bank, charger, mobile accessory', 'Anker', 'PowerCore 20K', NULL, '6 Month Warranty', 340, '15x7x2.5', 'Polycarbonate', 'Black', '2024-01-30', NULL, 'No. 42, Main Street', NULL, 'Kurunegala', 'Kurunegala', '60000', 'Sri Lanka', 350.00),
(29, 75, 2, 'Ladies\' Leather Handbag', 'Genuine leather handbag with multiple compartments. Elegant and durable.', 9500.00, 8, 'HB-LTHR-LD-BRN', 'active', 'New', '2025-07-07 18:02:55', '0000-00-00 00:00:00', 'Approved', 'Available', 'handbag, leather, fashion, ladies, accessory', 'Ceylon Leather', 'Duchess', NULL, '3 Month Warranty on stitches', 700, '30x25x12', 'Genuine Leather', 'Brown', '2024-02-18', NULL, 'No. 42, Main Street', NULL, 'Kurunegala', 'Kurunegala', '60000', 'Sri Lanka', 400.00),
(30, 75, 3, 'iPhone 14 Pro Case', 'Silicone protective case for iPhone 14 Pro. Shock absorbent and stylish.', 2500.00, 50, 'CASE-IP14P-SIL-NVY', 'active', 'New', '2025-07-07 18:02:55', '0000-00-00 00:00:00', 'Approved', 'Available', 'iphone case, cover, accessory, mobile', 'Spigen', 'Ultra Hybrid', NULL, NULL, 50, '15x7.5x1', 'Silicone', 'Navy Blue', NULL, NULL, 'No. 42, Main Street', NULL, 'Kurunegala', 'Kurunegala', '60000', 'Sri Lanka', 200.00),
(31, 75, 5, 'Yoga Mat - 6mm', 'Non-slip TPE yoga mat. Eco-friendly and comfortable for all types of yoga.', 3800.00, 40, 'YOGA-MAT-6MM-PUR', 'active', 'New', '2025-07-07 18:02:55', '0000-00-00 00:00:00', 'Approved', 'Available', 'yoga, fitness, exercise, mat, sports', 'FitLife', 'EcoMat', NULL, NULL, 1000, '183x61x0.6', 'TPE', 'Purple', NULL, NULL, 'No. 42, Main Street', NULL, 'Kurunegala', 'Kurunegala', '60000', 'Sri Lanka', 450.00),
(32, 75, 4, 'A Brief History of Time', 'By Stephen Hawking. A landmark volume in science writing.', 2100.00, 7, 'BOOK-HAWK-ABHT', 'active', 'New', '2025-07-07 18:02:55', '0000-00-00 00:00:00', 'Approved', 'Available', 'science, book, non-fiction, hawking', 'Bantam Books', NULL, NULL, NULL, 250, '15x2x23', 'Paper', NULL, NULL, NULL, 'No. 42, Main Street', NULL, 'Kurunegala', 'Kurunegala', '60000', 'Sri Lanka', 250.00);

-- --------------------------------------------------------

--
-- Table structure for table `Marketplace_Categories`
--

CREATE TABLE `Marketplace_Categories` (
  `category_id` int(11) NOT NULL,
  `category_name` varchar(100) NOT NULL,
  `description` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Marketplace_Categories`
--

INSERT INTO `Marketplace_Categories` (`category_id`, `category_name`, `description`) VALUES
(1, 'Electronics', 'Gadgets, devices, and electronic accessories.'),
(2, 'Fashion', 'Apparel, footwear, and accessories for all ages.'),
(3, 'Mobile Phones', 'Samsung, iPhone, Nokia, Asus, Redmi'),
(4, 'Books', 'Fiction, non-fiction, educational, and children\'s books.'),
(5, 'Sports & Outdoors', 'Equipment, apparel, and gear for sports and outdoor activities.');

-- --------------------------------------------------------

--
-- Table structure for table `Orders`
--

CREATE TABLE `Orders` (
  `order_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `address_id` int(11) NOT NULL,
  `order_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `total_amount` decimal(10,2) NOT NULL,
  `delivery_fee` decimal(10,2) NOT NULL DEFAULT '0.00',
  `order_status` enum('Pending','Processing','Shipped','Delivered','Cancelled','Refunded') NOT NULL DEFAULT 'Pending',
  `payment_method` varchar(50) NOT NULL,
  `payment_status` enum('Unpaid','Paid','Refunded','Failed') NOT NULL DEFAULT 'Unpaid',
  `stripe_payment_intent_id` varchar(255) DEFAULT NULL,
  `transaction_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Orders`
--

INSERT INTO `Orders` (`order_id`, `user_id`, `address_id`, `order_date`, `total_amount`, `delivery_fee`, `order_status`, `payment_method`, `payment_status`, `stripe_payment_intent_id`, `transaction_id`) VALUES
(32, 1, 2, '2025-06-25 06:28:25', 885.96, 400.00, 'Cancelled', 'Stripe', 'Paid', 'pi_3RdmqAQtOPR6g7Jf0AAq1hse', NULL),
(33, 1, 2, '2025-06-25 06:29:23', 419.99, 400.00, 'Processing', 'Cash On Delivery', 'Unpaid', NULL, NULL),
(34, 1, 2, '2025-06-25 06:39:32', 440.00, 400.00, 'Shipped', 'Stripe', 'Paid', 'pi_3Rdn0vQtOPR6g7Jf17FWoTng', NULL),
(35, 1, 2, '2025-06-25 07:24:49', 480.00, 400.00, 'Delivered', 'Cash On Delivery', 'Unpaid', NULL, NULL),
(36, 1, 2, '2025-06-25 07:24:59', 404.99, 400.00, 'Cancelled', 'Cash On Delivery', 'Unpaid', NULL, NULL),
(37, 1, 2, '2025-06-25 07:25:23', 404.99, 400.00, 'Refunded', 'Cash On Delivery', 'Unpaid', NULL, NULL),
(38, 1, 2, '2025-06-25 07:39:39', 440.00, 400.00, 'Cancelled', 'Cash On Delivery', 'Unpaid', NULL, NULL),
(39, 1, 2, '2025-06-25 07:46:29', 440.00, 400.00, 'Cancelled', 'Cash On Delivery', 'Unpaid', NULL, NULL),
(40, 1, 2, '2025-06-25 07:46:46', 440.00, 400.00, 'Cancelled', 'Stripe', 'Paid', 'pi_3Rdo3zQtOPR6g7Jf0bpvCdrj', NULL),
(41, 1, 2, '2025-06-25 13:05:55', 625.97, 400.00, 'Cancelled', 'Stripe', '', 'pi_3Rdt2rQtOPR6g7Jf0wNgQq9I', NULL),
(42, 1, 2, '2025-06-25 13:06:21', 625.97, 400.00, 'Cancelled', 'Stripe', 'Paid', 'pi_3Rdt3GQtOPR6g7Jf0iQDDM3I', NULL),
(43, 1, 2, '2025-06-27 06:17:40', 699.99, 400.00, 'Cancelled', 'Stripe', 'Unpaid', 'pi_3ReVcsQtOPR6g7Jf16TozGKy', NULL),
(44, 1, 2, '2025-06-27 06:17:40', 699.99, 400.00, 'Cancelled', 'Stripe', 'Unpaid', 'pi_3ReVcsQtOPR6g7Jf09o3jGya', NULL),
(45, 2, 4, '2025-07-02 19:14:25', 789.99, 400.00, 'Pending', 'Cash On Delivery', 'Unpaid', NULL, NULL),
(46, 1, 2, '2025-07-02 19:50:00', 520.00, 400.00, 'Cancelled', 'Cash On Delivery', 'Unpaid', NULL, NULL),
(47, 72, 5, '2025-07-06 15:19:00', 1299.99, 500.00, 'Pending', 'CashOnDelivery', '', NULL, NULL),
(48, 57, 8, '2025-07-06 15:23:27', 1299.99, 500.00, 'Pending', 'CashOnDelivery', '', NULL, NULL),
(49, 1, 2, '2025-07-06 18:37:22', 699.99, 400.00, 'Cancelled', 'Cash On Delivery', 'Unpaid', NULL, NULL),
(50, 1, 2, '2025-07-06 19:03:08', 699.99, 400.00, 'Cancelled', 'Cash On Delivery', 'Unpaid', NULL, NULL),
(51, 1, 2, '2025-07-06 19:03:53', 1049.98, 400.00, 'Cancelled', 'Cash On Delivery', 'Unpaid', NULL, NULL),
(52, 1, 2, '2025-07-07 18:06:32', 49800.00, 400.00, 'Cancelled', 'CashOnDelivery', '', NULL, NULL),
(53, 1, 2, '2025-07-07 18:19:55', 18900.00, 400.00, 'Cancelled', 'Stripe', 'Unpaid', 'pi_3RiJfMQtOPR6g7Jf1fQPlKYJ', NULL),
(54, 1, 2, '2025-07-07 18:19:56', 18900.00, 400.00, 'Cancelled', 'Stripe', 'Paid', 'pi_3RiJfLQtOPR6g7Jf0f57D6a2', NULL),
(55, 1, 2, '2025-07-08 04:54:51', 32400.00, 500.00, 'Cancelled', 'Stripe', 'Unpaid', 'pi_3RiTZnQtOPR6g7Jf1ywJd8Kh', NULL),
(56, 1, 2, '2025-07-08 04:55:22', 32400.00, 500.00, 'Cancelled', 'Stripe', 'Unpaid', 'pi_3RiTaIQtOPR6g7Jf0YzObMrk', NULL),
(57, 1, 2, '2025-07-08 06:10:18', 18900.00, 400.00, 'Cancelled', 'Stripe', 'Unpaid', 'pi_3RiUkoQtOPR6g7Jf1dMq9Z1g', NULL),
(58, 1, 2, '2025-07-08 06:10:19', 18900.00, 400.00, 'Cancelled', 'Stripe', 'Paid', 'pi_3RiUkpQtOPR6g7Jf18ATuyBt', NULL),
(59, 1, 2, '2025-07-08 06:36:00', 4900.00, 400.00, 'Cancelled', 'Stripe', 'Unpaid', 'pi_3RiV9fQtOPR6g7Jf00mplGzP', NULL),
(60, 1, 2, '2025-07-08 06:36:01', 4900.00, 400.00, 'Cancelled', 'Stripe', '', 'pi_3RiV9gQtOPR6g7Jf0wUbg5Gs', NULL),
(61, 1, 2, '2025-07-08 10:20:29', 18900.00, 400.00, 'Cancelled', 'Stripe', 'Unpaid', 'pi_3RiYevQtOPR6g7Jf0ixS38Ae', NULL),
(62, 1, 2, '2025-07-08 10:20:30', 18900.00, 400.00, 'Cancelled', 'Stripe', '', 'pi_3RiYevQtOPR6g7Jf0sHaoglh', NULL),
(63, 1, 2, '2025-07-08 10:30:03', 9900.00, 400.00, 'Cancelled', 'Stripe', '', 'pi_3RiYoBQtOPR6g7Jf1YfeJBK8', NULL),
(64, 1, 2, '2025-07-08 10:30:34', 1700.00, 500.00, 'Cancelled', 'Stripe', 'Unpaid', 'pi_3RiYofQtOPR6g7Jf0KWfabXb', NULL),
(65, 1, 2, '2025-07-08 10:30:44', 1700.00, 500.00, 'Cancelled', 'Stripe', 'Unpaid', 'pi_3RiYopQtOPR6g7Jf13f6BmGa', NULL),
(66, 1, 2, '2025-07-09 05:15:19', 4900.00, 400.00, 'Cancelled', 'Stripe', 'Unpaid', 'pi_3RiqNDQtOPR6g7Jf0CPO9CEH', NULL),
(67, 1, 2, '2025-07-09 05:15:19', 4300.00, 500.00, 'Cancelled', 'CashOnDelivery', '', NULL, NULL),
(68, 1, 2, '2025-07-09 05:15:19', 4900.00, 400.00, 'Cancelled', 'Stripe', 'Unpaid', 'pi_3RiqNDQtOPR6g7Jf15Q54xK3', NULL),
(69, 1, 2, '2025-07-09 06:59:28', 18900.00, 400.00, 'Cancelled', 'Stripe', 'Unpaid', 'pi_3RirzwQtOPR6g7Jf0yS2HcFV', NULL),
(70, 1, 2, '2025-07-09 06:59:29', 18900.00, 400.00, 'Cancelled', 'Stripe', '', 'pi_3RirzwQtOPR6g7Jf1lohjlXN', NULL),
(71, 1, 2, '2025-07-10 17:31:52', 55700.00, 400.00, 'Cancelled', 'Stripe', 'Unpaid', 'pi_3RjOLUQtOPR6g7Jf0tUfde7g', NULL),
(72, 1, 2, '2025-07-10 17:31:53', 55700.00, 400.00, 'Cancelled', 'Stripe', 'Paid', 'pi_3RjOLVQtOPR6g7Jf0Xg51QFX', NULL),
(73, 1, 2, '2025-07-10 20:07:12', 23500.00, 500.00, 'Pending', 'CashOnDelivery', '', NULL, NULL),
(74, 1, 2, '2025-07-16 05:48:09', 9300.00, 400.00, 'Pending', 'Stripe', 'Unpaid', 'pi_3RlODlQtOPR6g7Jf0LfT1Q6P', NULL),
(75, 1, 2, '2025-07-16 05:48:10', 9300.00, 400.00, 'Pending', 'Stripe', 'Paid', 'pi_3RlODmQtOPR6g7Jf0TCX6IEo', NULL),
(76, 1, 2, '2025-07-16 05:55:41', 27000.00, 500.00, 'Pending', 'CashOnDelivery', '', NULL, NULL),
(77, 1, 2, '2025-07-16 08:23:23', 18900.00, 400.00, 'Pending', 'Stripe', 'Unpaid', 'pi_3RlQe0QtOPR6g7Jf1ZmscoVT', NULL),
(78, 1, 2, '2025-07-16 08:23:24', 18900.00, 400.00, 'Pending', 'Stripe', '', 'pi_3RlQe0QtOPR6g7Jf14VFKfH9', NULL),
(79, 1, 2, '2025-07-16 08:23:36', 18900.00, 400.00, 'Pending', 'CashOnDelivery', '', NULL, NULL),
(80, 1, 2, '2025-07-16 08:27:53', 23500.00, 500.00, 'Pending', 'CashOnDelivery', '', NULL, NULL),
(81, 1, 2, '2025-08-04 08:58:59', 37400.00, 400.00, 'Pending', 'Stripe', 'Unpaid', 'pi_3RsKFvQtOPR6g7Jf0ApSVE1N', NULL),
(82, 1, 2, '2025-08-04 08:59:00', 37400.00, 400.00, 'Pending', 'Stripe', 'Paid', 'pi_3RsKFvQtOPR6g7Jf19L7rmIr', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `Order_Items`
--

CREATE TABLE `Order_Items` (
  `order_item_id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `variant_id` int(11) DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `delivery_status` enum('Pending','Ready to Pick Up','Cancelled','Out of Stock','Completed','Return') NOT NULL DEFAULT 'Pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Order_Items`
--

INSERT INTO `Order_Items` (`order_item_id`, `order_id`, `product_id`, `variant_id`, `quantity`, `unit_price`, `delivery_status`) VALUES
(50, 32, 4, 8, 1, 299.99, 'Pending'),
(51, 32, 5, 10, 2, 19.99, 'Pending'),
(52, 32, 7, NULL, 1, 25.99, 'Pending'),
(53, 32, 8, NULL, 1, 120.00, 'Pending'),
(54, 33, 5, 10, 1, 19.99, 'Cancelled'),
(55, 34, 9, NULL, 1, 40.00, 'Pending'),
(56, 35, 9, NULL, 2, 40.00, 'Pending'),
(57, 36, 6, 12, 1, 4.99, 'Pending'),
(58, 37, 6, 12, 1, 4.99, 'Pending'),
(59, 38, 9, NULL, 1, 40.00, 'Pending'),
(60, 39, 9, NULL, 1, 40.00, 'Pending'),
(61, 40, 9, NULL, 1, 40.00, 'Pending'),
(62, 41, 9, NULL, 1, 40.00, 'Pending'),
(63, 41, 8, NULL, 1, 120.00, 'Out of Stock'),
(64, 41, 7, NULL, 1, 25.99, 'Pending'),
(65, 41, 5, 10, 2, 19.99, 'Cancelled'),
(66, 42, 9, NULL, 1, 40.00, 'Pending'),
(67, 42, 8, NULL, 1, 120.00, 'Ready to Pick Up'),
(68, 42, 7, NULL, 1, 25.99, 'Pending'),
(69, 42, 5, 10, 2, 19.99, 'Out of Stock'),
(70, 43, 4, 8, 1, 299.99, 'Pending'),
(71, 44, 4, 8, 1, 299.99, 'Pending'),
(72, 45, 4, 9, 1, 349.99, 'Pending'),
(73, 45, 9, NULL, 1, 40.00, 'Pending'),
(74, 46, 8, NULL, 1, 120.00, 'Ready to Pick Up'),
(75, 47, 4, NULL, 1, 799.99, 'Pending'),
(76, 48, 4, NULL, 1, 799.99, 'Pending'),
(77, 49, 4, 8, 1, 299.99, 'Pending'),
(78, 50, 4, 8, 1, 299.99, 'Pending'),
(79, 51, 4, 8, 1, 299.99, 'Ready to Pick Up'),
(80, 51, 4, 9, 1, 349.99, 'Ready to Pick Up'),
(81, 52, 23, NULL, 1, 18500.00, 'Pending'),
(82, 52, 27, NULL, 1, 22000.00, 'Pending'),
(83, 52, 28, NULL, 1, 8900.00, 'Pending'),
(84, 53, 23, NULL, 1, 18500.00, 'Pending'),
(85, 54, 23, NULL, 1, 18500.00, 'Pending'),
(86, 55, 28, NULL, 1, 8900.00, 'Pending'),
(87, 55, 23, NULL, 1, 18500.00, 'Pending'),
(88, 55, 25, NULL, 1, 4500.00, 'Pending'),
(89, 56, 28, NULL, 1, 8900.00, 'Pending'),
(90, 56, 23, NULL, 1, 18500.00, 'Pending'),
(91, 56, 25, NULL, 1, 4500.00, 'Pending'),
(92, 57, 23, NULL, 1, 18500.00, 'Pending'),
(93, 58, 23, NULL, 1, 18500.00, 'Pending'),
(94, 59, 25, NULL, 1, 4500.00, 'Pending'),
(95, 60, 25, NULL, 1, 4500.00, 'Pending'),
(96, 61, 23, NULL, 1, 18500.00, 'Pending'),
(97, 62, 23, NULL, 1, 18500.00, 'Pending'),
(98, 63, 29, NULL, 1, 9500.00, 'Pending'),
(99, 64, 26, NULL, 1, 1200.00, 'Pending'),
(100, 65, 26, NULL, 1, 1200.00, 'Pending'),
(101, 68, 25, NULL, 1, 4500.00, 'Pending'),
(102, 66, 25, NULL, 1, 4500.00, 'Pending'),
(103, 67, 31, NULL, 1, 3800.00, 'Pending'),
(104, 69, 23, NULL, 1, 18500.00, 'Pending'),
(105, 70, 23, NULL, 1, 18500.00, 'Pending'),
(106, 71, 23, NULL, 1, 18500.00, 'Pending'),
(107, 71, 27, NULL, 1, 22000.00, 'Pending'),
(108, 71, 28, NULL, 1, 8900.00, 'Pending'),
(109, 71, 31, NULL, 1, 3800.00, 'Pending'),
(110, 71, 32, NULL, 1, 2100.00, 'Pending'),
(111, 72, 23, NULL, 1, 18500.00, 'Pending'),
(112, 72, 27, NULL, 1, 22000.00, 'Pending'),
(113, 72, 28, NULL, 1, 8900.00, 'Pending'),
(114, 72, 31, NULL, 1, 3800.00, 'Pending'),
(115, 72, 32, NULL, 1, 2100.00, 'Pending'),
(116, 73, 23, NULL, 1, 18500.00, 'Pending'),
(117, 73, 25, NULL, 1, 4500.00, 'Pending'),
(118, 74, 28, NULL, 1, 8900.00, 'Pending'),
(119, 75, 28, NULL, 1, 8900.00, 'Pending'),
(120, 76, 25, NULL, 1, 4500.00, 'Pending'),
(121, 76, 27, NULL, 1, 22000.00, 'Pending'),
(122, 77, 23, NULL, 1, 18500.00, 'Pending'),
(123, 78, 23, NULL, 1, 18500.00, 'Pending'),
(124, 79, 23, NULL, 1, 18500.00, 'Pending'),
(125, 80, 23, NULL, 1, 18500.00, 'Pending'),
(126, 80, 25, NULL, 1, 4500.00, 'Pending'),
(127, 81, 23, NULL, 2, 18500.00, 'Pending'),
(128, 82, 23, NULL, 2, 18500.00, 'Pending');

-- --------------------------------------------------------

--
-- Table structure for table `ProductImages`
--

CREATE TABLE `ProductImages` (
  `image_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `image_url` varchar(255) NOT NULL,
  `is_thumbnail` tinyint(1) DEFAULT '0',
  `display_order` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `ProductImages`
--

INSERT INTO `ProductImages` (`image_id`, `product_id`, `image_url`, `is_thumbnail`, `display_order`) VALUES
(49, 23, 'https://uniquebuds.co.uk/cdn/shop/files/TWSAirpods.png?v=1729170239', 1, 1),
(50, 24, 'https://lionsgoldencircle.com/Tridots/images/Marketplace/1_sideview_1751214002.jpghttps://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTWDR2qjeqXjUdJ3it_4ZyPMpwx67s7tpf73g&s', 0, 2),
(51, 25, 'https://litb-cgis.rightinthebox.com/desc_image/202304/bps/desc/inc/iwurjs1680502229530.jpg', 0, 3),
(52, 26, 'https://bookstopuae.com/wp-content/uploads/2025/01/Black-Green-Simple-Minimalist-Aesthetic-World-Book-Day-Mockup-Instagram-Post-67.png', 1, 1),
(53, 27, 'https://lionsgoldencircle.com/Tridots/images/Marketplace/2_back_1751214102.jpg', 0, 2),
(54, 25, 'https://m.media-amazon.com/images/I/616Q1bVCl2L._AC_SL1500_.jpg', 1, 1),
(55, 27, 'https://www.anglarsports.com/wp-content/uploads/2022/04/English-Willow-Reserve-Edition-Final-2-mini.jpg', 1, 1),
(56, 28, 'https://www.airox.pk/cdn/shop/files/Airox-PB08-20000mAh-Smart-Powerbank-with-Built-In-USB_-iPhone_-and-Type-C-Cables-22.5W-Fast-Charging-Support_-LED-Screen-Display-Airox.pk-69018047.jpg?v=1705869900&width=1200', 1, 1),
(57, 29, 'https://m.media-amazon.com/images/I/6117K81PFxL._AC_SY535_.jpg', 1, 1),
(58, 30, 'https://www.bare-cases.com/cdn/shop/products/BareArmourCaseforiPhone14Pro-MinimalistSlimShockProofMagSafeCaseforiPhone14Pro.jpg', 0, 1),
(59, 6, 'https://lionsgoldencircle.com/Tridots/images/Marketplace/6_black_1751214501.jpg', 1, 1),
(60, 7, 'https://lionsgoldencircle.com/Tridots/images/Marketplace/7_brown_1751214601.jpg', 1, 1),
(61, 7, 'https://lionsgoldencircle.com/Tridots/images/Marketplace/7_open_1751214602.jpg', 0, 2),
(62, 8, 'https://lionsgoldencircle.com/Tridots/images/Marketplace/8_navy_1751214701.jpg', 1, 1),
(63, 30, 'https://www.bare-cases.com/cdn/shop/products/BareArmourCaseforiPhone14Pro-MinimalistSlimShockProofMagSafeCaseforiPhone14Pro.jpg', 1, 1),
(64, 31, 'https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcSYQThNE59VHrOj48k9f0Nph_31WZ0Bqx3f9g&s', 1, 1),
(65, 32, 'https://m.media-amazon.com/images/I/81pQPZAFWbL.jpg', 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `Product_Variants`
--

CREATE TABLE `Product_Variants` (
  `variant_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `variant_name` varchar(255) NOT NULL,
  `variant_price` decimal(10,2) NOT NULL,
  `variant_stock_quantity` int(11) NOT NULL DEFAULT '0',
  `variant_image_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Product_Variants`
--

INSERT INTO `Product_Variants` (`variant_id`, `product_id`, `variant_name`, `variant_price`, `variant_stock_quantity`, `variant_image_url`) VALUES
(8, 4, 'Storage: 64GB', 299.99, 20, 'https://example.com/images/phone_64.jpg'),
(9, 4, 'Storage: 128GB', 349.99, 15, 'https://example.com/images/phone_128.jpg'),
(10, 5, 'Color: Red, Size: M', 19.99, 50, 'https://example.com/images/shirt_red_m.jpg'),
(11, 5, 'Color: Blue, Size: L', 19.99, 40, 'https://example.com/images/shirt_blue_l.jpg'),
(12, 6, 'Wattage: 9W', 4.99, 100, 'https://example.com/images/bulb_9w.jpg'),
(13, 6, 'Wattage: 12W', 5.99, 80, 'https://example.com/images/bulb_12w.jpg'),
(20, 22, 'Purple', 245000.00, 6, 'https://lionsgoldencircle.com/Tridots/images/Marketplace/22_purple_1751213778.jpg'),
(21, 22, 'Silver', 245000.00, 6, 'https://lionsgoldencircle.com/Tridots/images/Marketplace/22_silver_1751213778.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `renting`
--

CREATE TABLE `renting` (
  `rent_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `main_category_id` int(11) NOT NULL,
  `sub_category_id` int(11) NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `brand` varchar(100) NOT NULL,
  `model` varchar(100) DEFAULT NULL,
  `product_images` text,
  `availability_status` enum('Available','Not Available') DEFAULT 'Available',
  `product_description` text,
  `keywords` varchar(500) DEFAULT NULL,
  `price_per_hour` decimal(10,2) DEFAULT NULL,
  `price_per_day` decimal(10,2) DEFAULT NULL,
  `product_location` varchar(255) DEFAULT NULL,
  `average_rating` float DEFAULT '0',
  `review_count` int(11) DEFAULT '0',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_at` datetime DEFAULT NULL,
  `approval_status` enum('Approved','Not Approved','Under Review','Blocked') DEFAULT 'Under Review'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `renting`
--

INSERT INTO `renting` (`rent_id`, `user_id`, `main_category_id`, `sub_category_id`, `product_name`, `brand`, `model`, `product_images`, `availability_status`, `product_description`, `keywords`, `price_per_hour`, `price_per_day`, `product_location`, `average_rating`, `review_count`, `updated_at`, `created_at`, `approval_status`) VALUES
(1, 1, 1, 1, 'Excavator', 'Caterpillar', 'CAT 320', 'https://lionsgoldencircle.com/Tridots/images/Ads/renting_image_1747503098846.jpg', 'Available', 'Heavy-duty excavator for construction.', 'excavator, construction, digging', 50.00, 400.00, 'Kandy', 3, 1, '2025-07-08 04:03:22', '2025-05-03 20:58:15', 'Approved'),
(2, 2, 1, 2, 'Concrete Mixture', 'Komatsu', 'KM450', 'https://surplus.lk/wp-content/uploads/2022/07/IMG_20210312_112111-scaled.jpg', 'Not Available', 'Concrete mixing machine for large scale projects.', 'concrete, mixing, construction', 30.00, 250.00, 'Los Angeles', 4.2, 15, '2025-07-08 04:07:11', '2025-05-03 20:58:15', 'Approved'),
(3, 3, 2, 3, 'Laser Printer', 'HP', 'LaserJet Pro MFP M428', 'https://www.officesupplies.lk/wp-content/uploads/2023/12/20231106104601HP-LASERJET-1008A-PRINTER-1200x900.jpg', 'Not Available', 'Multi-function laser printer for office use.', 'printer, office, HP', 5.00, 30.00, 'San Francisco', 4.8, 25, '2025-07-08 04:08:14', '2025-05-03 20:58:15', 'Approved'),
(4, 2, 3, 4, 'Pickup Truck', 'Ford', 'F-150', 'https://hips.hearstapps.com/hmg-prod/images/2024-ford-f-150-raptor-r-296-67092b83c578d.jpg?crop=0.623xw:0.528xh;0.147xw,0.381xh&resize=1200:*', 'Available', 'Ford pickup truck for rent, ideal for transporting goods.', 'pickup, truck, rent, Ford', 20.00, 150.00, 'Chicago', 3, 2, '2025-07-08 04:05:33', '2025-05-03 20:58:15', 'Approved');

-- --------------------------------------------------------

--
-- Table structure for table `ServicesCategory`
--

CREATE TABLE `ServicesCategory` (
  `service_category_id` int(11) NOT NULL,
  `service_category_name` varchar(255) NOT NULL,
  `description` text,
  `category_icon` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `ServicesCategory`
--

INSERT INTO `ServicesCategory` (`service_category_id`, `service_category_name`, `description`, `category_icon`, `created_at`) VALUES
(1, 'Plumbing', 'Services related to water and drainage systems.', 'plumbing_icon.png', '2025-05-04 16:09:59'),
(2, 'Electrical', 'Services for electrical wiring, fixtures, and repairs.', 'electrical_icon.png', '2025-05-04 16:09:59'),
(3, 'Carpentry', 'Woodworking services including furniture and repairs.', 'carpentry_icon.png', '2025-05-04 16:09:59'),
(4, 'Painting', 'Interior and exterior painting services.', 'painting_icon.png', '2025-05-04 16:09:59'),
(5, 'Cleaning', 'Home and commercial cleaning services.', 'cleaning_icon.png', '2025-05-04 16:09:59');

-- --------------------------------------------------------

--
-- Table structure for table `Service_Providers`
--

CREATE TABLE `Service_Providers` (
  `User_id` int(11) DEFAULT NULL,
  `seller_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `service_category_id` int(11) DEFAULT NULL,
  `description` text,
  `contact_number` varchar(20) DEFAULT NULL,
  `email_address` varchar(255) DEFAULT NULL,
  `address` text,
  `experience_years` int(11) DEFAULT NULL,
  `qualifications` text,
  `location` varchar(255) DEFAULT NULL,
  `availability_status` enum('Available','Not Available') NOT NULL DEFAULT 'Available',
  `reviews_average` float DEFAULT NULL,
  `review_count` int(11) DEFAULT '0',
  `verification_status` enum('Verified','Not Verified') DEFAULT 'Not Verified',
  `profile_picture` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `approval_status` enum('Approved','Not Approved','Under Review','Blocked') DEFAULT 'Under Review'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Service_Providers`
--

INSERT INTO `Service_Providers` (`User_id`, `seller_id`, `name`, `service_category_id`, `description`, `contact_number`, `email_address`, `address`, `experience_years`, `qualifications`, `location`, `availability_status`, `reviews_average`, `review_count`, `verification_status`, `profile_picture`, `created_at`, `updated_at`, `approval_status`) VALUES
(1, 201, 'Kamal Perera', 1, 'Experienced plumber for all your needs.', '0771234567', 'kamal@plumbing.lk', '15 Main Street, Colombo', 10, 'NVQ Level 4 Plumbing', 'Colombo', 'Available', 4.8, 55, 'Verified', 'https://lionsgoldencircle.com/Tridots/Api/uploads/vehicle_image_1747427475398.jpg', '2025-05-04 16:09:59', '2025-07-01 21:00:15', 'Not Approved'),
(2, 202, 'Priya Silva', 2, 'Certified electrician for residential and commercial work.', '0719876543', 'priya@electrical.lk', '22 Flower Road, Kandy', 5, 'BSc Electrical Engineering', 'Kandy', 'Available', 4.5, 32, 'Verified', 'priya_profile.png', '2025-05-04 16:09:59', '2025-06-02 15:36:31', 'Approved'),
(3, 203, 'Sunil Fernando', 3, 'Skilled carpenter specializing in custom furniture.', '0765432109', 'sunil@carpentry.lk', '8 Lake View Avenue, Negombo', 15, 'Master Carpenter Certification', 'Negombo', 'Available', 4.9, 78, 'Verified', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSkcJK5JQS4LZcYAIa8FknRPmUhYT6lORq6QA&s', '2025-05-04 16:09:59', '2025-07-08 04:23:41', 'Approved'),
(1, 204, 'Ishanga Mallawarachchi', 4, 'Professional painter with an eye for detail.', '0701122334', 'ishangamallawaarachchi@gmail.com', '3 Hill Street, Galle', 7, 'Diploma in Fine Arts', 'Kurunegala', 'Available', 4.7, 41, 'Verified', 'https://www.iupat.org/wp-content/uploads/2023/09/Commercial-Painter-New-2-1440x1080.jpg', '2025-05-04 16:09:59', '2025-07-08 04:22:44', 'Approved'),
(1, 205, 'Rohitha Mendis', 1, 'Reliable plumbing services for your home.', '0756789012', 'rohitha@plumbing.lk', '10 Park Road, Kurunegala', 3, 'NVQ Level 3 Plumbing', 'Kurunegala', 'Available', 4.2, 20, 'Not Verified', 'https://myplumber.lk/wp-content/uploads/2023/11/OUTDOOR-PLUMBING-SYSTEM-REPAIR.jpg', '2025-05-04 16:09:59', '2025-07-08 04:21:57', 'Approved'),
(1, 206, 'Travis Senarathne', 2, 'Quick and efficient electrical repairs.', '0761407875', 'suz.x2006@gmail.com', '79, Dambakanda Estate, Boyagane, Kurunegala', 8, 'Electrical Technician License', 'Kurunegala', 'Available', 3, 2, 'Verified', 'https://cdn.prod.website-files.com/6390e14cc734a931f8327343/65c086a8515ee2a80d7c0111_65815fbe8bced97cfeafbf4b_image5%20(3).webp', '2025-05-04 16:09:59', '2025-07-08 04:19:37', 'Approved'),
(2, 207, 'Sanduni Weerasekara', 2, 'Thorough cleaning services for homes and offices.', '0789012345', 'sanduni@cleaning.lk', '12 School Lane, Matara', 2, 'Cleaning Service Certification', 'Matara', 'Available', 5, 1, 'Not Verified', 'https://www.pioneers.lk/images/house-cleaning-services.jpg', '2025-05-04 16:09:59', '2025-07-08 04:21:07', 'Approved');

-- --------------------------------------------------------

--
-- Table structure for table `sub_category`
--

CREATE TABLE `sub_category` (
  `sub_category_id` int(11) NOT NULL,
  `sub_category` varchar(255) NOT NULL,
  `category_description` text,
  `category_icon` varchar(500) DEFAULT NULL,
  `main_category_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `sub_category`
--

INSERT INTO `sub_category` (`sub_category_id`, `sub_category`, `category_description`, `category_icon`, `main_category_id`) VALUES
(1, 'Excavator', 'Used for digging and construction-related tasks.', 'https://example.com/icons/excavator.png', 1),
(2, 'Printer', 'All-in-one office printers.', 'https://example.com/icons/printer.png', 2),
(3, 'Scanners', 'Scan Everything', 'https://lionsgoldencircle.com/Tridots/images/Ads/car_rent.png', 3),
(4, 'Mixture', 'Concrete mixtures for construction.', 'https://example.com/icons/mixture.png', 1);

-- --------------------------------------------------------

--
-- Table structure for table `UserAddresses`
--

CREATE TABLE `UserAddresses` (
  `address_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `address_title` varchar(255) NOT NULL,
  `full_address` text NOT NULL,
  `street_number` varchar(255) DEFAULT NULL,
  `street_name` varchar(255) DEFAULT NULL,
  `city` varchar(255) NOT NULL,
  `district` varchar(255) DEFAULT NULL,
  `province` varchar(255) DEFAULT NULL,
  `postal_code` varchar(20) DEFAULT NULL,
  `country` varchar(255) DEFAULT 'Sri Lanka',
  `is_default` tinyint(1) DEFAULT '0',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `UserAddresses`
--

INSERT INTO `UserAddresses` (`address_id`, `user_id`, `address_title`, `full_address`, `street_number`, `street_name`, `city`, `district`, `province`, `postal_code`, `country`, `is_default`, `updated_at`) VALUES
(2, 1, 'Home', '79, Dambakanda Estate, Boyagane, Kurunegala', '79', 'Dambakanda Estate', 'Kurunagala', 'Kurunagala', 'North Western', '60000', 'Sri Lanka', 1, '2025-06-24 08:03:47'),
(4, 2, 'Home', 'eu', '3e', 'Re', 'Ee', 'Ee', 'Ee', '44', 'Sri Lanka', 1, '2025-07-02 19:14:14'),
(5, 72, 'lasantha bake house', 'work work', '', '', 'maho', '', '', '60600', 'Sri Lanka', 1, '2025-07-06 15:07:04'),
(6, 57, 'home', 'street', '', '', 'city', '', '', '1000', 'Sri Lanka', 0, '2025-07-06 15:04:22'),
(7, 57, 'home', 'street', '', '', 'city', '', '', '1000', 'Sri Lanka', 0, '2025-07-06 15:05:32'),
(8, 57, 'home', 'street', '', '', 'city', '', '', '1000', 'Sri Lanka', 0, '2025-07-06 15:06:49');

-- --------------------------------------------------------

--
-- Table structure for table `Users`
--

CREATE TABLE `Users` (
  `user_id` int(11) NOT NULL,
  `username` varchar(255) NOT NULL,
  `email_address` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `contact_number` varchar(50) DEFAULT NULL,
  `nic_number` varchar(50) DEFAULT NULL,
  `user_type` enum('Personal','Business','Admin','Rider') NOT NULL,
  `profile_picture` text,
  `address` text,
  `verification_status` enum('Verified','Not Verified') DEFAULT 'Not Verified',
  `is_active` enum('Active','Deactive') DEFAULT 'Active',
  `business_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Users`
--

INSERT INTO `Users` (`user_id`, `username`, `email_address`, `password_hash`, `contact_number`, `nic_number`, `user_type`, `profile_picture`, `address`, `verification_status`, `is_active`, `business_name`) VALUES
(1, 'Susara Senarathne', 'suz.x2006@gmail.com', '$2y$10$S3G0bxo1BomcinCOTd6z0uLoTUlRl2wYAq9pb7iMumFQ6InEZUreG', '0761407875', '200611003614', 'Business', 'https://lionsgoldencircle.com/Tridots/images/ProfilePictures/Susara_Senarathne_6840b3d208f72.jpg', '79, Dambakanda Estate, Boyagane, Kurunegala.', 'Verified', 'Active', ''),
(2, 'DIGI TECH', 'ishi4u@gmail.com', '$2y$10$f01vVyNs18nCme0XB4fnn./xu1jiRsFAVa73yr3Dc3glK8HNlNsMG', '0711011324', '200688337717', 'Admin', 'https://lionsgoldencircle.com/Tridots/images/Ads/vehicle_image_1747503063049.jpg', 'Kurunegala, Sri Lanka', 'Verified', 'Active', ''),
(3, 'admin', 'admin@example.com', '$2y$10$GvUaiCt4epRPoFjN3F5SS.8Ji6VR4wH7a3BcenUn.l/1D/twS6jPy', '0711011322', '200411307822', '', NULL, NULL, 'Verified', 'Active', NULL),
(32, 'Susara', 'dssusara5@gmail.com', '$2y$10$53Q2TZ/T42eBVi4ZlzLie.4mupk3cGyj1GvJD9nbLTiQYUOkOlNP.', '0761407875', NULL, 'Business', NULL, '79, Dambakanda Estate\r\nBoyagane', 'Verified', 'Active', NULL),
(42, 'piumal', 'piumalishanga@gmail.com', '$2y$10$3NHP4isdBiu4rZ8/Fuef1uAM1rzT8MLqrA6CyqcsvVs7hevgsNSVG', '0741221301', '20061130167', 'Business', NULL, 'Kurunegala 1', 'Verified', 'Active', NULL),
(48, 'admin_user', 'admin@tridots.lk', 'b9fb641f26f1403c6e9678f080fe628973cb656accca74586408b6d585acf0cf', '0701122334', '900112233V', 'Admin', NULL, NULL, 'Verified', 'Active', NULL),
(57, 'eash_mini', 'eashminirajapakse@gmail.com', '$2y$10$OWX0V1CjMC3z/psjggPa8OweeC5Me4G0qF/RUXVSgvg9FGeKHjYIS', '0772666133', '200669702521', 'Personal', NULL, '74/30 D, Rajamal Uyana,Colombo RD, Kurunegala', 'Verified', 'Active', NULL),
(72, 'harry', 'roshanhayrish@gmail.com', '$2y$10$Vz3e6EaFSIJMEGKEOY2x8e0/5jthG8582qTSnVJ8A1i/U1uTr9B4u', '0742673300', '200623400458', 'Personal', NULL, '391 puttalam road kurunegala', 'Verified', 'Active', NULL),
(74, 'lucyy', '1977lilyy@gmail.com', '$2y$10$VsEEpNotoLRkGoyN5ip2SuM7S2LGdCTo.F81PRi7AyWssFxPUAUAW', '071 231 6788', NULL, 'Business', NULL, '69/I, Kurunegala', 'Verified', 'Active', NULL),
(75, 'luciiii', 'abcs@gmail.com', '$2y$10$OxCqbCjF4yHIY/xGm5nLreI23cDxg4MZEVDioAhAK1boIVMl2HynG', '071 231 6786', NULL, 'Business', NULL, '69/i,kurunegala', 'Verified', 'Active', NULL),
(76, 'Munshak Mohamed', 'munshakibnumarikkar@gmail.com', '$2y$10$Qz9EiFPmzLv97WP/w.4HfO8vzIQ2qo0G/1hhfr1RThzc388Bkwd/O', '0743729022', '200512304168', 'Personal', NULL, 'Agara Waththa, Horombawa', 'Verified', 'Active', NULL),
(77, 'sakuna', 'sakunasy5@gmail.com', '$2y$10$.OPzen3F/09wKWCjMkuiXePAlXGVou0OTSpbLYxJLWdERR3mECo9S', '0716531817', '200135404064', 'Personal', 'images/ProfilePictures/68906b47a43ce.jpg', 'kurunegala', 'Not Verified', 'Active', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `VehicleCategory`
--

CREATE TABLE `VehicleCategory` (
  `vehicle_category_id` int(11) NOT NULL,
  `vehicle_category_name` varchar(255) DEFAULT NULL,
  `description` text,
  `category_icon` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `VehicleCategory`
--

INSERT INTO `VehicleCategory` (`vehicle_category_id`, `vehicle_category_name`, `description`, `category_icon`, `created_at`) VALUES
(1, 'Trucks', 'Heavy-duty vehicles for transporting goods.', 'truck_icon.png', '2025-05-04 13:26:54'),
(2, 'Vans', 'Medium-sized vehicles for passengers or cargo.', 'van_icon.png', '2025-05-04 13:26:54'),
(3, 'SUVs', 'Sport Utility Vehicles with off-road capabilities.', 'suv_icon.png', '2025-05-04 13:26:54'),
(4, 'Sedans', 'Standard passenger cars.', 'sedan_icon.png', '2025-05-04 13:26:54'),
(5, 'Buses', 'Large vehicles for transporting many passengers.', 'bus_icon.png', '2025-05-04 13:26:54'),
(6, 'Concrete Mixing Trucks', 'Specialized trucks for transporting and mixing concrete.', 'mixer_truck_icon.png', '2025-05-04 13:26:54');

-- --------------------------------------------------------

--
-- Table structure for table `Vehicles`
--

CREATE TABLE `Vehicles` (
  `vehicle_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `vehicle_category_id` int(11) DEFAULT NULL,
  `vehicle_name` varchar(255) DEFAULT NULL,
  `brand` varchar(255) DEFAULT NULL,
  `model` varchar(255) DEFAULT NULL,
  `description` text,
  `capacity` varchar(255) DEFAULT NULL,
  `fuel_type` varchar(50) DEFAULT NULL,
  `transmission_type` varchar(50) DEFAULT NULL,
  `availability_status` enum('Available','Not Available') NOT NULL,
  `location` varchar(255) DEFAULT NULL,
  `price_type` enum('per_km','per_hour','per_day') DEFAULT NULL,
  `amount` decimal(10,2) DEFAULT NULL,
  `vehicle_images` text,
  `keywords` varchar(255) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  `approval_status` enum('Approved','Not Approved','Under Review','Blocked') DEFAULT 'Under Review'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `Vehicles`
--

INSERT INTO `Vehicles` (`vehicle_id`, `user_id`, `vehicle_category_id`, `vehicle_name`, `brand`, `model`, `description`, `capacity`, `fuel_type`, `transmission_type`, `availability_status`, `location`, `price_type`, `amount`, `vehicle_images`, `keywords`, `created_at`, `updated_at`, `approval_status`) VALUES
(101, 1, 1, 'Volvo FH16 Truck', 'Volvo', 'FH16-750', 'Powerful truck for long-haul transport.', '40 tons', 'Diesel', 'Automatic', 'Available', 'Colombo', 'per_km', 1.50, 'https://assets.volvo.com/is/image/VolvoInformationTechnologyAB/volvo-trucks-59A1644-PR?wid=1024', 'truck, heavy duty, volvo', '2025-05-04 13:28:43', '2025-07-08 04:11:45', 'Approved'),
(102, 1, 4, 'BMW 520d', 'BMW', '520d', 'Reliable Sport Sedan.', '4 passengers maximum 5', 'Diesel', 'Automatic', 'Available', 'Kurunegala', 'per_day', 28000.00, 'https://autostore.nyc3.cdn.digitaloceanspaces.com/12282_0.jpg', 'car,sedan,bmw,520d', '2025-05-04 13:28:43', '2025-07-08 04:10:25', 'Approved'),
(105, 2, 5, 'Ashok Leyland Viking Bus', 'Ashok Leyland', 'Viking', 'City bus for public transport.', '50 passengers', 'Diesel', 'Manual', 'Available', 'Colombo', 'per_hour', 15.00, 'https://thumbs.dreamstime.com/b/lanka-ashok-leyland-viking-colombo-sri-september-cyan-city-bus-street-339688783.jpg', 'bus, city, ashok leyland', '2025-05-04 13:28:43', '2025-07-08 04:13:35', 'Approved'),
(106, 1, 6, 'Sinotruk Howo Mixer', 'Sinotruk', 'Howo', 'Concrete mixer truck for construction sites.', '10 cubic meters', 'Diesel', 'Manual', 'Available', 'Gampaha', 'per_hour', 25.00, 'https://sinotrukhowo.cn/wp-content/uploads/2023/07/Sinotruk-Howo-6x4-Concrete-Mixer-Truck-212.webp', 'concrete mixer, sinotruk, construction', '2025-05-04 13:28:43', '2025-07-08 04:14:38', 'Approved'),
(107, 2, 4, 'Nissan NV350 Caravan', 'Nissan', 'E26', 'Versatile van for cargo transport.', '3 tons', 'Diesel', 'Manual', 'Available', 'Jaffna', 'per_day', 65.00, 'https://i.ytimg.com/vi/jZ19FKe3Y60/sddefault.jpg', 'van, cargo, nissan', '2025-05-04 13:28:43', '2025-07-08 04:16:31', 'Approved'),
(108, 1, 4, 'BMW M5 Competition', 'BMW', 'M5', 'Fuel-efficient sedan for daily commute.', '500 passengers', 'Petrol', 'Automatic', 'Available', 'Matara', 'per_km', 40.00, 'https://rushlane.com/wp-content/uploads/2019/10/2019-bmw-m5-competition-india-launch-price-2.jpg', 'sedan, toyota, fuel efficient', '2025-05-04 13:28:43', '2025-07-08 04:17:39', 'Approved');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `auth_tokens`
--
ALTER TABLE `auth_tokens`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_user_id` (`user_id`),
  ADD KEY `idx_selector` (`selector`);

--
-- Indexes for table `banner_images`
--
ALTER TABLE `banner_images`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `BusinessProfiles`
--
ALTER TABLE `BusinessProfiles`
  ADD PRIMARY KEY (`business_profile_id`),
  ADD UNIQUE KEY `email_address` (`email_address`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `feedbacks`
--
ALTER TABLE `feedbacks`
  ADD PRIMARY KEY (`feedback_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `feedback_type` (`feedback_type`,`item_id`);

--
-- Indexes for table `inquiries`
--
ALTER TABLE `inquiries`
  ADD PRIMARY KEY (`inquiry_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `main_category`
--
ALTER TABLE `main_category`
  ADD PRIMARY KEY (`main_category_id`);

--
-- Indexes for table `Marketplace`
--
ALTER TABLE `Marketplace`
  ADD PRIMARY KEY (`product_id`),
  ADD UNIQUE KEY `sku` (`sku`),
  ADD KEY `seller_id` (`seller_id`),
  ADD KEY `category_id` (`category_id`);

--
-- Indexes for table `Marketplace_Categories`
--
ALTER TABLE `Marketplace_Categories`
  ADD PRIMARY KEY (`category_id`),
  ADD UNIQUE KEY `category_name` (`category_name`);

--
-- Indexes for table `Orders`
--
ALTER TABLE `Orders`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `address_id` (`address_id`);

--
-- Indexes for table `Order_Items`
--
ALTER TABLE `Order_Items`
  ADD PRIMARY KEY (`order_item_id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `ProductImages`
--
ALTER TABLE `ProductImages`
  ADD PRIMARY KEY (`image_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `Product_Variants`
--
ALTER TABLE `Product_Variants`
  ADD PRIMARY KEY (`variant_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `renting`
--
ALTER TABLE `renting`
  ADD PRIMARY KEY (`rent_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `main_category_id` (`main_category_id`),
  ADD KEY `sub_category_id` (`sub_category_id`);

--
-- Indexes for table `ServicesCategory`
--
ALTER TABLE `ServicesCategory`
  ADD PRIMARY KEY (`service_category_id`);

--
-- Indexes for table `Service_Providers`
--
ALTER TABLE `Service_Providers`
  ADD PRIMARY KEY (`seller_id`),
  ADD KEY `User_id` (`User_id`),
  ADD KEY `service_category_id` (`service_category_id`);

--
-- Indexes for table `sub_category`
--
ALTER TABLE `sub_category`
  ADD PRIMARY KEY (`sub_category_id`),
  ADD KEY `main_category_id` (`main_category_id`);

--
-- Indexes for table `UserAddresses`
--
ALTER TABLE `UserAddresses`
  ADD PRIMARY KEY (`address_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `Users`
--
ALTER TABLE `Users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email_address` (`email_address`),
  ADD UNIQUE KEY `nic_number` (`nic_number`);

--
-- Indexes for table `VehicleCategory`
--
ALTER TABLE `VehicleCategory`
  ADD PRIMARY KEY (`vehicle_category_id`);

--
-- Indexes for table `Vehicles`
--
ALTER TABLE `Vehicles`
  ADD PRIMARY KEY (`vehicle_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `vehicle_category_id` (`vehicle_category_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `auth_tokens`
--
ALTER TABLE `auth_tokens`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `banner_images`
--
ALTER TABLE `banner_images`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `BusinessProfiles`
--
ALTER TABLE `BusinessProfiles`
  MODIFY `business_profile_id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `feedbacks`
--
ALTER TABLE `feedbacks`
  MODIFY `feedback_id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=69;

--
-- AUTO_INCREMENT for table `inquiries`
--
ALTER TABLE `inquiries`
  MODIFY `inquiry_id` int(11) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;

--
-- AUTO_INCREMENT for table `main_category`
--
ALTER TABLE `main_category`
  MODIFY `main_category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `Marketplace`
--
ALTER TABLE `Marketplace`
  MODIFY `product_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT for table `Marketplace_Categories`
--
ALTER TABLE `Marketplace_Categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `Orders`
--
ALTER TABLE `Orders`
  MODIFY `order_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=83;

--
-- AUTO_INCREMENT for table `Order_Items`
--
ALTER TABLE `Order_Items`
  MODIFY `order_item_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=129;

--
-- AUTO_INCREMENT for table `ProductImages`
--
ALTER TABLE `ProductImages`
  MODIFY `image_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=66;

--
-- AUTO_INCREMENT for table `Product_Variants`
--
ALTER TABLE `Product_Variants`
  MODIFY `variant_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT for table `renting`
--
ALTER TABLE `renting`
  MODIFY `rent_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `sub_category`
--
ALTER TABLE `sub_category`
  MODIFY `sub_category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `UserAddresses`
--
ALTER TABLE `UserAddresses`
  MODIFY `address_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `Users`
--
ALTER TABLE `Users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=78;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `auth_tokens`
--
ALTER TABLE `auth_tokens`
  ADD CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `BusinessProfiles`
--
ALTER TABLE `BusinessProfiles`
  ADD CONSTRAINT `BusinessProfiles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `feedbacks`
--
ALTER TABLE `feedbacks`
  ADD CONSTRAINT `feedbacks_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `BusinessProfiles` (`user_id`);

--
-- Constraints for table `inquiries`
--
ALTER TABLE `inquiries`
  ADD CONSTRAINT `inquiries_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`);

--
-- Constraints for table `Marketplace`
--
ALTER TABLE `Marketplace`
  ADD CONSTRAINT `Marketplace_ibfk_1` FOREIGN KEY (`seller_id`) REFERENCES `Users` (`user_id`),
  ADD CONSTRAINT `Marketplace_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `Marketplace_Categories` (`category_id`);

--
-- Constraints for table `Orders`
--
ALTER TABLE `Orders`
  ADD CONSTRAINT `Orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`),
  ADD CONSTRAINT `Orders_ibfk_2` FOREIGN KEY (`address_id`) REFERENCES `UserAddresses` (`address_id`);

--
-- Constraints for table `Order_Items`
--
ALTER TABLE `Order_Items`
  ADD CONSTRAINT `Order_Items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `Orders` (`order_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `Order_Items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `Marketplace` (`product_id`);

--
-- Constraints for table `ProductImages`
--
ALTER TABLE `ProductImages`
  ADD CONSTRAINT `ProductImages_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `Marketplace` (`product_id`);

--
-- Constraints for table `Product_Variants`
--
ALTER TABLE `Product_Variants`
  ADD CONSTRAINT `Product_Variants_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `Marketplace` (`product_id`) ON DELETE CASCADE;

--
-- Constraints for table `renting`
--
ALTER TABLE `renting`
  ADD CONSTRAINT `renting_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`),
  ADD CONSTRAINT `renting_ibfk_2` FOREIGN KEY (`main_category_id`) REFERENCES `main_category` (`main_category_id`),
  ADD CONSTRAINT `renting_ibfk_3` FOREIGN KEY (`sub_category_id`) REFERENCES `sub_category` (`sub_category_id`);

--
-- Constraints for table `Service_Providers`
--
ALTER TABLE `Service_Providers`
  ADD CONSTRAINT `Service_Providers_ibfk_1` FOREIGN KEY (`User_id`) REFERENCES `Users` (`user_id`),
  ADD CONSTRAINT `Service_Providers_ibfk_2` FOREIGN KEY (`service_category_id`) REFERENCES `ServicesCategory` (`service_category_id`);

--
-- Constraints for table `sub_category`
--
ALTER TABLE `sub_category`
  ADD CONSTRAINT `sub_category_ibfk_1` FOREIGN KEY (`main_category_id`) REFERENCES `main_category` (`main_category_id`);

--
-- Constraints for table `UserAddresses`
--
ALTER TABLE `UserAddresses`
  ADD CONSTRAINT `UserAddresses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`) ON DELETE CASCADE;

--
-- Constraints for table `Vehicles`
--
ALTER TABLE `Vehicles`
  ADD CONSTRAINT `Vehicles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `Users` (`user_id`),
  ADD CONSTRAINT `Vehicles_ibfk_2` FOREIGN KEY (`vehicle_category_id`) REFERENCES `VehicleCategory` (`vehicle_category_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
