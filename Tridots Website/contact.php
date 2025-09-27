<?php
// --- CONFIGURATION ---
$pageTitle = "Contact Us - TRI DOTS";
$mainHeading = "Get in Touch";
$subHeading = "We'd love to hear from you. Whether you have a question about our services or any feedback, our team is ready to answer all your inquiries.";

// Project Links
$logoUrl = "https://lionsgoldencircle.com/Tridots/images/logo1.png";

// Contact Details
$address = "123 Galle Road, Colombo 03, Sri Lanka";
$phone = "+94 11 234 5678";
$email = "support@tridots.com";

// Footer
$footerText = "&copy; " . date("Y") . " TRIDOTS. All rights reserved.";

?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?php echo htmlspecialchars($pageTitle); ?></title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Inter', sans-serif;
        }
        
        /* --- Navigation Header Styles --- */
        .container { max-width: 1200px; margin: 0 auto; padding: 0 16px; }
        .main-header { background: #00715A; color: white; padding: 12px 0; position: sticky; top: 0; z-index: 1000; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .header-content { display: flex; justify-content: space-between; align-items: center; }
        .logo img { height: 40px; display: block; }
        .main-nav a { color: white; margin-left: 24px; text-decoration: none; font-weight: 500; padding-bottom: 4px; border-bottom: 2px solid transparent; transition: border-color 0.3s; }
        .main-nav a:hover { border-color: rgba(255,255,255,0.7); }
        .main-nav a.active { font-weight: 700; border-color: white; } /* Active link style */
        .header-actions .btn { background-color: white; color: #00715A; padding: 10px 20px; border-radius: 50px; text-decoration: none; font-weight: 600; }
        .header-actions .btn:hover { background-color: #f1f1f1; }

        /* --- Page-Specific Styles --- */
        .animated-gradient {
            background: linear-gradient(-45deg, #f5f7fa, #c3cfe2, #e2e8f0, #f8fafc);
            background-size: 400% 400%;
            animation: gradient-animation 15s ease infinite;
        }

        @keyframes gradient-animation {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
        }

        @keyframes fade-in-up {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        .animate-fade-in-up { animation: fade-in-up 0.8s ease-out forwards; }
        .delay-200 { animation-delay: 0.2s; }
        .delay-400 { animation-delay: 0.4s; }
        .delay-600 { animation-delay: 0.6s; }

        /* Form input focus styles */
        .form-input:focus {
            border-color: #00715A;
            box-shadow: 0 0 0 2px rgba(0, 113, 90, 0.2);
        }
        
        /* Hide elements initially for animation */
        .initial-hidden { opacity: 0; }
    </style>
</head>
<body class="bg-gray-50">

    <header class="main-header">
        <div class="container header-content">
            <a href="index.php" class="logo"><img src="<?php echo htmlspecialchars($logoUrl); ?>" alt="TRIDOTS Logo"></a>
            <nav class="main-nav">
                <a href="index.php">Home</a>
                <a href="about.php">About</a>
                <a href="solutions.php">Solutions</a>
                <a href="contact.php" class="active">Contact</a>
            </nav>
            <div class="header-actions"><a href="login.php" class="btn">Get Started</a></div>
        </div>
    </header>

    <main class="animated-gradient">
        <div class="container py-16 sm:py-24">
            <!-- Page Header -->
            <div class="text-center mb-16 initial-hidden animate-fade-in-up">
                <h1 class="text-4xl md:text-5xl font-extrabold text-gray-800">
                    <?php echo htmlspecialchars($mainHeading); ?>
                </h1>
                <p class="text-lg text-gray-600 mt-3 max-w-3xl mx-auto">
                    <?php echo htmlspecialchars($subHeading); ?>
                </p>
            </div>

            <!-- Contact Section -->
            <div class="bg-white/70 backdrop-blur-sm rounded-xl shadow-xl overflow-hidden initial-hidden animate-fade-in-up delay-200">
                <div class="grid grid-cols-1 lg:grid-cols-2">
                    
                    <!-- Contact Information -->
                    <div class="p-8 lg:p-12 bg-teal-700/5">
                        <h2 class="text-3xl font-bold text-gray-800 mb-6">Contact Information</h2>
                        <p class="text-gray-600 mb-8">Find us at our office, drop us a call, or send an email. We're here to help.</p>
                        
                        <div class="space-y-6">
                            <div class="flex items-start">
                                <div class="flex-shrink-0 w-10 h-10 bg-teal-600 text-white rounded-lg flex items-center justify-center">
                                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"></path></svg>
                                </div>
                                <div class="ml-4">
                                    <h3 class="text-lg font-semibold text-gray-700">Address</h3>
                                    <p class="text-gray-600"><?php echo htmlspecialchars($address); ?></p>
                                </div>
                            </div>
                             <div class="flex items-start">
                                <div class="flex-shrink-0 w-10 h-10 bg-teal-600 text-white rounded-lg flex items-center justify-center">
                                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z"></path></svg>
                                </div>
                                <div class="ml-4">
                                    <h3 class="text-lg font-semibold text-gray-700">Phone</h3>
                                    <p class="text-gray-600"><?php echo htmlspecialchars($phone); ?></p>
                                </div>
                            </div>
                             <div class="flex items-start">
                                <div class="flex-shrink-0 w-10 h-10 bg-teal-600 text-white rounded-lg flex items-center justify-center">
                                    <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z"></path></svg>
                                </div>
                                <div class="ml-4">
                                    <h3 class="text-lg font-semibold text-gray-700">Email</h3>
                                    <p class="text-gray-600"><?php echo htmlspecialchars($email); ?></p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Contact Form -->
                    <div class="p-8 lg:p-12">
                        <h2 class="text-3xl font-bold text-gray-800 mb-6">Send us a Message</h2>
                        <form action="#" method="POST">
                            <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
                                <div>
                                    <label for="name" class="block text-sm font-medium text-gray-700">Full Name</label>
                                    <input type="text" name="name" id="name" required class="form-input mt-1 block w-full rounded-md border-gray-300 shadow-sm transition duration-150" placeholder="John Doe">
                                </div>
                                <div>
                                    <label for="email" class="block text-sm font-medium text-gray-700">Email Address</label>
                                    <input type="email" name="email" id="email" required class="form-input mt-1 block w-full rounded-md border-gray-300 shadow-sm transition duration-150" placeholder="you@example.com">
                                </div>
                            </div>
                            <div class="mt-6">
                                <label for="subject" class="block text-sm font-medium text-gray-700">Subject</label>
                                <input type="text" name="subject" id="subject" required class="form-input mt-1 block w-full rounded-md border-gray-300 shadow-sm transition duration-150" placeholder="Question about renting">
                            </div>
                            <div class="mt-6">
                                <label for="message" class="block text-sm font-medium text-gray-700">Message</label>
                                <textarea name="message" id="message" rows="5" required class="form-input mt-1 block w-full rounded-md border-gray-300 shadow-sm transition duration-150" placeholder="Your message here..."></textarea>
                            </div>
                            <div class="mt-8 text-right">
                                <button type="submit" class="inline-flex items-center justify-center w-full sm:w-auto px-8 py-3 border border-transparent text-base font-medium rounded-md text-white bg-teal-600 hover:bg-teal-700 transition duration-150">
                                    Send Message
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

             <!-- Google Map Section -->
            <div class="mt-16 initial-hidden animate-fade-in-up delay-600">
                 <h2 class="text-3xl font-bold text-gray-800 text-center mb-8">Our Location</h2>
                 <div class="rounded-xl shadow-xl overflow-hidden h-96">
                    <iframe 
                        src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d126743.5858603949!2d79.82136199999999!3d6.921838399999999!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3ae253d10f7a7003%3A0x320b2e4d32d3838d!2sColombo!5e0!3m2!1sen!2slk!4v1678886134511!5m2!1sen!2slk" 
                        width="100%" 
                        height="100%" 
                        style="border:0;" 
                        allowfullscreen="" 
                        loading="lazy" 
                        referrerpolicy="no-referrer-when-downgrade">
                    </iframe>
                 </div>
            </div>

        </div>
    </main>
    
    <footer class="text-center py-8 bg-gray-100">
        <p class="text-gray-600"><?php echo $footerText; ?></p>
    </footer>

</body>
</html>