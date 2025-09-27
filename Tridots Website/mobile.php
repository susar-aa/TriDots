<?php
// --- CONFIGURATION ---
// Define all your project's information here for easy updates.

$pageTitle = "TRI DOTS - App Download";
$mainHeading = "Get the TRI DOTS Mobile App";
$subHeading = "The easiest way to rent machines, hire vehicles, and find service providers.";

// Project Links
$logoUrl = "https://lionsgoldencircle.com/Tridots/images/logo1.png";
$websiteUrl = "https://lionsgoldencircle.com/Tridots/";
$apkUrl = "https://drive.google.com/file/d/1smqJNZ4kObatRqhJ1mvHnFxvOA7n9Ehq/view?usp=sharing";

// Footer
// The copyright year will update automatically.
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
        
        /* -- Animated Gradient Background -- */
        .animated-gradient {
            background: linear-gradient(-45deg, #0d9488, #c3cfe2, #0d9488, #f8fafc);
            background-size: 400% 400%;
            animation: gradient-animation 15s ease infinite;
        }

        @keyframes gradient-animation {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
        }

        /* -- Element Entrance Animations -- */
        @keyframes fade-in-down {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        @keyframes fade-in-up {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        /* Apply animations with delays */
        .animate-fade-in-down {
            animation: fade-in-down 0.8s ease-out forwards;
        }
        .animate-fade-in-up {
            animation: fade-in-up 0.8s ease-out forwards;
        }

        /* Staggered delays */
        .delay-200 { animation-delay: 0.2s; }
        .delay-400 { animation-delay: 0.4s; }
        .delay-600 { animation-delay: 0.6s; }
        .delay-800 { animation-delay: 0.8s; }

        /* -- Card Hover Effect -- */
        .card-hover {
            transition: transform 0.3s ease, box-shadow 0.4s ease;
        }
        .card-hover:hover {
            transform: translateY(-10px);
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
        }

        /* -- Button Hover Effect -- */
        .btn-hover-effect {
            transition: transform 0.2s ease, background-color 0.3s ease;
        }
        .btn-hover-effect:hover {
            transform: scale(1.05);
        }

        /* Hide elements initially for animation */
        .initial-hidden {
            opacity: 0;
        }
    </style>
</head>
<body class="animated-gradient min-h-screen flex items-center justify-center p-4">

    <main class="w-full max-w-4xl mx-auto">
        <div class="text-center mb-12">
            <!-- Logo -->
            <img src="<?php echo htmlspecialchars($logoUrl); ?>" alt="TRI DOTS Logo" 
                 class="mx-auto h-20 w-auto initial-hidden animate-fade-in-down"
                 onerror="this.onerror=null; this.src='https://placehold.co/200x80/14B8A6/FFFFFF?text=TRI+DOTS';">
            
            <!-- Headings -->
            <h1 class="text-3xl md:text-4xl font-bold text-gray-700 mt-6 initial-hidden animate-fade-in-down delay-200">
                <?php echo htmlspecialchars($mainHeading); ?>
            </h1>
            <p class="text-md text-gray-500 mt-2 initial-hidden animate-fade-in-down delay-400">
                <?php echo htmlspecialchars($subHeading); ?>
            </p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
            <!-- Website Card -->
            <div class="bg-white/70 backdrop-blur-sm rounded-xl shadow-lg p-8 flex flex-col items-center text-center card-hover initial-hidden animate-fade-in-up delay-600">
                <div class="mb-6">
                    <!-- Globe Icon -->
                    <svg class="w-16 h-16 text-teal-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M12 21a9.004 9.004 0 008.716-6.747M12 21a9.004 9.004 0 01-8.716-6.747M12 21c2.485 0 4.5-4.03 4.5-9S14.485 3 12 3m0 18c-2.485 0-4.5-4.03-4.5-9S9.515 3 12 3m0 0a8.997 8.997 0 017.843 4.582M12 3a8.997 8.997 0 00-7.843 4.582m15.686 0A11.953 11.953 0 0112 10.5c-2.998 0-5.74-1.1-7.843-2.918m15.686 0A8.959 8.959 0 0121 12c0 .778-.099 1.533-.284 2.253m0 0A17.919 17.919 0 0112 16.5c-3.162 0-6.133-.815-8.716-2.247m0 0A9.015 9.015 0 013 12c0-1.605.42-3.113 1.157-4.418" />
                    </svg>
                </div>
                <h2 class="text-2xl font-bold text-gray-800 mb-3">Visit Our Website</h2>
                <p class="text-gray-600 mb-6 flex-grow">Discover the full features and details of our project on our official website.</p>
                <a href="<?php echo htmlspecialchars($websiteUrl); ?>" target="_blank" rel="noopener noreferrer" 
                   class="inline-block w-full px-8 py-3 text-lg font-semibold text-white bg-teal-600 rounded-lg shadow-sm hover:bg-teal-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-teal-500 btn-hover-effect">
                    Visit Website
                </a>
            </div>

            <!-- APK Download Card -->
            <div class="bg-white/70 backdrop-blur-sm rounded-xl shadow-lg p-8 flex flex-col items-center text-center card-hover initial-hidden animate-fade-in-up delay-800">
                <div class="mb-6">
                    <!-- Mobile Device Icon -->
                    <svg class="w-16 h-16 text-teal-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" d="M10.5 1.5H8.25A2.25 2.25 0 006 3.75v16.5a2.25 2.25 0 002.25 2.25h7.5A2.25 2.25 0 0018 20.25V3.75a2.25 2.25 0 00-2.25-2.25H13.5m-3 0V3h3V1.5m-3 0h3m-3 18.75h3" />
                    </svg>
                </div>
                <h2 class="text-2xl font-bold text-gray-800 mb-3">Get the APK</h2>
                <p class="text-gray-600 mb-6 flex-grow">Install the latest version of our application directly on your Android device.</p>
                <a href="<?php echo htmlspecialchars($apkUrl); ?>" target="_blank" rel="noopener noreferrer"
                   class="inline-block w-full px-8 py-3 text-lg font-semibold text-white bg-teal-600 rounded-lg shadow-sm hover:bg-teal-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-teal-500 btn-hover-effect">
                    View APK
                </a>
            </div>
        </div>

        <footer class="text-center mt-12 initial-hidden animate-fade-in-up" style="animation-delay: 1s;">
            <p class="text-gray-600"><?php echo $footerText; ?></p>
        </footer>
    </main>

</body>
</html>
