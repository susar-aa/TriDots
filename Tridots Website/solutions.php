<?php
// --- CONFIGURATION ---
$pageTitle = "Our Solutions - TRI DOTS";
$mainHeading = "Comprehensive Solutions for Your Project Needs";
$subHeading = "From heavy machinery to skilled labor, find everything you need on one platform.";

// Project Links
$logoUrl = "https://lionsgoldencircle.com/Tridots/images/logo1.png";
$machinesUrl = "machines.php";
$vehiclesUrl = "vehicles.php";
$laborersUrl = "laborers.php";

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
        .main-header { background: #00715A; color: white; padding: 12px 0; position: sticky; top: 0; z-index: 1000; }
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

        @keyframes fade-in-down {
            from { opacity: 0; transform: translateY(-20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        @keyframes fade-in-up {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }
        
        .animate-fade-in-down { animation: fade-in-down 0.8s ease-out forwards; }
        .animate-fade-in-up { animation: fade-in-up 0.8s ease-out forwards; }

        .delay-200 { animation-delay: 0.2s; }
        .delay-400 { animation-delay: 0.4s; }
        .delay-600 { animation-delay: 0.6s; }
        .delay-800 { animation-delay: 0.8s; }
        .delay-1000 { animation-delay: 1.0s; }

        .card-hover { transition: transform 0.3s ease, box-shadow 0.4s ease; }
        .card-hover:hover { transform: translateY(-10px); box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04); }

        .btn-hover-effect { transition: transform 0.2s ease, background-color 0.3s ease; }
        .btn-hover-effect:hover { transform: scale(1.05); }

        .initial-hidden { opacity: 0; }
    </style>
</head>
<body class="animated-gradient">

    <header class="main-header">
        <div class="container header-content">
            <a href="index.php" class="logo"><img src="<?php echo htmlspecialchars($logoUrl); ?>" alt="TRIDOTS Logo"></a>
            <nav class="main-nav">
                <a href="index.php">Home</a>
                <a href="about.php">About</a>
                <a href="solutions.php" class="active">Solutions</a>
                <a href="contact.php">Contact</a>
            </nav>
            <div class="header-actions"><a href="login.php" class="btn">Get Started</a></div>
        </div>
    </header>

    <main class="w-full max-w-6xl mx-auto px-4 py-16 sm:py-24">
        <div class="text-center mb-16">
            <h1 class="text-4xl md:text-5xl font-extrabold text-gray-800 mt-6 initial-hidden animate-fade-in-down">
                <?php echo htmlspecialchars($mainHeading); ?>
            </h1>
            <p class="text-lg text-gray-600 mt-3 max-w-3xl mx-auto initial-hidden animate-fade-in-down delay-200">
                <?php echo htmlspecialchars($subHeading); ?>
            </p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            
            <div class="bg-white/70 backdrop-blur-sm rounded-xl shadow-lg p-8 flex flex-col items-center text-center card-hover initial-hidden animate-fade-in-up delay-400">
                <div class="mb-6">
                    <svg class="w-16 h-16 text-teal-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M11.42 15.17L17.25 21A2.652 2.652 0 0021 17.25l-5.877-5.877M11.42 15.17l2.471-2.471a2.652 2.652 0 00-3.75-3.75L4.5 12.25l-2.471 2.471a2.652 2.652 0 003.75 3.75L8.42 15.17z" />
                        <path stroke-linecap="round" stroke-linejoin="round" d="M4.5 12.25L15.17 1.586a2.652 2.652 0 013.75 0L21 4.836a2.652 2.652 0 010 3.75L12.25 16.5" />
                    </svg>
                </div>
                <h2 class="text-2xl font-bold text-gray-800 mb-3">Rent Heavy Machinery</h2>
                <p class="text-gray-600 mb-6 flex-grow">Access a wide range of construction machinery. Find the right equipment for your job, from excavators to concrete mixers, available for daily or long-term rental.</p>
                <a href="<?php echo htmlspecialchars($machinesUrl); ?>" class="inline-block w-full px-8 py-3 text-lg font-semibold text-white bg-teal-600 rounded-lg shadow-sm hover:bg-teal-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-teal-500 btn-hover-effect">
                    Browse Machines
                </a>
            </div>

            <div class="bg-white/70 backdrop-blur-sm rounded-xl shadow-lg p-8 flex flex-col items-center text-center card-hover initial-hidden animate-fade-in-up delay-600">
                <div class="mb-6">
                    <svg class="w-16 h-16 text-teal-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                        <path d="M9 17a2 2 0 11-4 0 2 2 0 014 0zM19 17a2 2 0 11-4 0 2 2 0 014 0z" />
                        <path stroke-linecap="round" stroke-linejoin="round" d="M13 16V6a1 1 0 00-1-1H4a1 1 0 00-1 1v10l2-2h8a1 1 0 001-1z" />
                        <path stroke-linecap="round" stroke-linejoin="round" d="M22 9h-2a1 1 0 00-1 1v6a1 1 0 001 1h2v-2.121A8.003 8.003 0 0018 10c-1.32 0-2.5.32-3.535.879M9 10.121A8.003 8.003 0 0113 9c2.684 0 5.076 1.342 6.535 3.379" />
                    </svg>
                </div>
                <h2 class="text-2xl font-bold text-gray-800 mb-3">Hire Commercial Vehicles</h2>
                <p class="text-gray-600 mb-6 flex-grow">Need a pickup truck, lorry, or van? Our platform connects you with vehicle owners for easy and reliable hiring to transport materials and personnel.</p>
                <a href="<?php echo htmlspecialchars($vehiclesUrl); ?>" class="inline-block w-full px-8 py-3 text-lg font-semibold text-white bg-teal-600 rounded-lg shadow-sm hover:bg-teal-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-teal-500 btn-hover-effect">
                    Find Vehicles
                </a>
            </div>

            <div class="bg-white/70 backdrop-blur-sm rounded-xl shadow-lg p-8 flex flex-col items-center text-center card-hover initial-hidden animate-fade-in-up delay-800">
                <div class="mb-6">
                    <svg class="w-16 h-16 text-teal-600" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M18 18.72a9.094 9.094 0 003.741-.479 3 3 0 00-4.682-2.72m-7.5-2.962a3.75 3.75 0 015.25 0m-5.25 0a3.75 3.75 0 00-5.25 0m7.5-7.5c3.464 0 6.25-2.786 6.25-6.25S14.964 1.5 11.5 1.5 5.25 4.286 5.25 7.75s2.786 6.25 6.25 6.25z" />
                        <path stroke-linecap="round" stroke-linejoin="round" d="M21 18.72a9.094 9.094 0 01-3.741-.479 3 3 0 01-4.682-2.72m-7.5-2.962a3.75 3.75 0 00-5.25 0m5.25 0a3.75 3.75 0 015.25 0m-15 0a3.75 3.75 0 015.25 0m-5.25 0a3.75 3.75 0 005.25 0" />
                    </svg>
                </div>
                <h2 class="text-2xl font-bold text-gray-800 mb-3">Find Skilled Labor</h2>
                <p class="text-gray-600 mb-6 flex-grow">Connect with verified service providers and skilled laborers for your project. From electricians to general workers, find the right person for the job.</p>
                <a href="<?php echo htmlspecialchars($laborersUrl); ?>" class="inline-block w-full px-8 py-3 text-lg font-semibold text-white bg-teal-600 rounded-lg shadow-sm hover:bg-teal-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-teal-500 btn-hover-effect">
                    Hire Providers
                </a>
            </div>
        </div>

        <footer class="text-center mt-16 pb-8 initial-hidden animate-fade-in-up" style="animation-delay: 1.0s;">
            <p class="text-gray-600"><?php echo $footerText; ?></p>
        </footer>
    </main>

</body>
</html>