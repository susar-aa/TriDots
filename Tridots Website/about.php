<?php
// about.php - About Us page for TR:Dots
?>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>About TR:Dots - Revolutionizing Rentals & Services</title>

  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;700;800&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
  <style>
    :root {
      --primary-dark: #00715A;
      --primary-light: #00B07D;
      --secondary-accent: #00d093;
      --text-dark: #2d3748;
      --text-light: #4a5568;
      --bg-light: #f7fafc;
      --bg-white: #ffffff;
      --border-color: #e2e8f0;
    }

    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }

    html {
        scroll-behavior: smooth;
    }

    body {
      font-family: 'Inter', sans-serif;
      background-color: var(--bg-light);
      color: var(--text-dark);
      line-height: 1.7;
      overflow-x: hidden;
      opacity: 0;
      transition: opacity 0.5s ease-in;
    }

    /* --- Loading Screen --- */
    .loading-screen {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: var(--primary-dark);
      display: flex;
      justify-content: center;
      align-items: center;
      z-index: 9999;
      transition: opacity 0.8s ease-out 1.5s, visibility 0.8s ease-out 1.5s;
    }
    .loading-logo {
      font-size: 2.5rem;
      font-weight: 800;
      color: var(--bg-white);
      animation: pulse 1.5s infinite ease-in-out;
    }
    .loading-logo img {
        height: 50px;
    }

    @keyframes pulse {
      0% { transform: scale(1); }
      50% { transform: scale(1.05); }
      100% { transform: scale(1); }
    }

    /* --- General Styles --- */
    .container {
      width: 90%;
      max-width: 1200px;
      margin: 0 auto;
      padding: 80px 20px;
    }
    
    h1, h2, h3 {
        font-weight: 700;
        color: var(--primary-dark);
        line-height: 1.3;
    }

    h1 { font-size: 3rem; }
    h2 { font-size: 2.5rem; text-align: center; margin-bottom: 50px; }
    h3 { font-size: 1.5rem; }
    p { margin-bottom: 1rem; color: var(--text-light); }

    .section-subtitle {
        text-align: center;
        color: var(--primary-light);
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 1px;
        margin-bottom: 10px;
    }

    /* --- Header --- */
    header {
      background: linear-gradient(90deg, var(--primary-dark) 0%, var(--primary-light) 100%);
      padding: 15px 5%;
      display: flex;
      justify-content: space-between;
      align-items: center;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      position: sticky;
      top: 0;
      z-index: 1000;
      width: 100%;
    }
    .logo img {
        height: 40px;
        display: block;
    }
    .nav-links { display: flex; gap: 2.5rem; }
    .nav-link {
      color: rgba(255, 255, 255, 0.9);
      text-decoration: none;
      font-weight: 500;
      transition: color 0.3s ease;
      position: relative;
      padding-bottom: 5px;
    }
    .nav-link::after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      width: 0;
      height: 2px;
      background-color: var(--bg-white);
      transition: width 0.3s ease;
    }
    .nav-link:hover,
    .nav-link.active {
      color: var(--bg-white);
    }
    .nav-link.active {
      font-weight: 700;
    }
    .nav-link:hover::after, .nav-link.active::after {
      width: 100%;
    }

    /* --- Hero Section --- */
    .hero {
      background: linear-gradient(rgba(0, 113, 90, 0.85), rgba(0, 0, 0, 0.7)), url('images/hero-background.jpg') no-repeat center center/cover;
      color: var(--bg-white);
      text-align: center;
      padding: 120px 20px;
    }
    .hero h1 {
      color: var(--bg-white);
      font-size: 3.5rem;
      font-weight: 800;
      margin-bottom: 20px;
      animation: fadeInDown 1s ease-out;
    }
    .hero p {
      font-size: 1.25rem;
      max-width: 700px;
      margin: 0 auto 30px;
      color: rgba(255,255,255,0.9);
      animation: fadeInUp 1s ease-out 0.3s;
    }

    /* --- Story Section --- */
    .story-section {
        display: flex;
        gap: 50px;
        align-items: center;
    }
    .story-content { flex: 1; }
    .story-image { 
        flex: 1;
        max-width: 500px;
    }
    .story-image img {
        width: 100%;
        border-radius: 12px;
        box-shadow: 0 10px 25px rgba(0,0,0,0.1);
    }
    
    /* --- How It Works Section --- */
    .how-it-works { background-color: var(--bg-white); }
    .steps-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 40px;
        text-align: center;
    }
    .step-card {
        padding: 20px;
    }
    .step-icon {
        font-size: 3rem;
        color: var(--primary-light);
        margin-bottom: 20px;
        line-height: 1;
    }
    .step-card h3 { margin-bottom: 10px; }

    /* --- Values Section --- */
    .values-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
        gap: 30px;
    }
    .value-card {
        background: var(--bg-white);
        padding: 30px;
        border-radius: 10px;
        border-left: 5px solid var(--primary-light);
        box-shadow: 0 4px 15px rgba(0,0,0,0.05);
        transition: transform 0.3s ease, box-shadow 0.3s ease;
    }
    .value-card:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 25px rgba(0,0,0,0.08);
    }
    .value-card .fa-solid {
        font-size: 2rem;
        color: var(--primary-light);
        margin-right: 15px;
        float: left;
    }

    /* --- CTA Section --- */
    .cta-section {
        background: var(--primary-dark);
        color: var(--bg-white);
        text-align: center;
        border-radius: 12px;
        padding: 60px 30px;
    }
    .cta-section h2 {
        color: var(--bg-white);
        margin-bottom: 20px;
    }
    .cta-buttons {
        display: flex;
        justify-content: center;
        gap: 20px;
        margin-top: 30px;
    }
    .btn {
        padding: 15px 30px;
        border-radius: 8px;
        text-decoration: none;
        font-weight: 700;
        transition: all 0.3s ease;
    }
    .btn-primary {
        background-color: var(--primary-light);
        color: var(--bg-white);
    }
    .btn-primary:hover {
        background-color: var(--secondary-accent);
        transform: translateY(-3px);
    }
    .btn-secondary {
        background-color: transparent;
        color: var(--bg-white);
        border: 2px solid var(--bg-white);
    }
    .btn-secondary:hover {
        background-color: var(--bg-white);
        color: var(--primary-dark);
        transform: translateY(-3px);
    }

    /* --- Footer --- */
    footer {
      background-color: var(--text-dark);
      color: var(--bg-light);
      padding: 50px 0;
    }
    .footer-container {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 40px;
        width: 90%;
        max-width: 1200px;
        margin: 0 auto;
    }
    .footer-about, .footer-links, .footer-contact { padding: 0 15px; }
    .footer-logo img {
        height: 35px;
        margin-bottom: 15px;
    }
    .footer h4 {
        font-size: 1.2rem;
        color: var(--bg-white);
        margin-bottom: 20px;
        font-weight: 700;
    }
    .footer p, .footer a {
        color: #a0aec0;
        text-decoration: none;
        transition: color 0.3s ease;
    }
    .footer a:hover { color: var(--primary-light); }
    .footer-links ul { list-style: none; }
    .footer-links li { margin-bottom: 10px; }
    .footer-socials { margin-top: 20px; }
    .footer-socials a {
        font-size: 1.5rem;
        margin-right: 15px;
    }
    .footer-bottom {
        text-align: center;
        padding: 20px 0;
        margin-top: 40px;
        border-top: 1px solid var(--text-light);
        font-size: 0.9rem;
    }
    
    /* --- Animations & Responsive --- */
    .fade-in-section {
        opacity: 0;
        transform: translateY(40px);
        transition: opacity 0.8s ease-out, transform 0.8s ease-out;
    }
    .fade-in-section.is-visible {
        opacity: 1;
        transform: translateY(0);
    }
    @keyframes fadeInDown {
      from { opacity: 0; transform: translateY(-30px); }
      to { opacity: 1; transform: translateY(0); }
    }
    @keyframes fadeInUp {
      from { opacity: 0; transform: translateY(30px); }
      to { opacity: 1; transform: translateY(0); }
    }

    @media (max-width: 768px) {
        h1 { font-size: 2.5rem; }
        h2 { font-size: 2rem; }
        .hero h1 { font-size: 2.8rem; }
        header { flex-direction: column; gap: 15px; }
        .story-section { flex-direction: column; }
    }
  </style>
</head>

<body>
  <!-- Loading Animation -->
  <div class="loading-screen">
    <div class="loading-logo">
        <img src="images/logo1.png" alt="TR:Dots Loading Logo" onerror="this.style.display='none'">
    </div>
  </div>

  <div class="main-content">
    <header>
      <div class="logo">
        <a href="index.php">
            <img src="images/logo1.png" alt="TR:Dots Logo" onerror="this.style.display='none'">
        </a>
      </div>
      <nav class="nav-links">
        <a href="index.php" class="nav-link">Home</a>
        <a href="about.php" class="nav-link active">About Us</a>
        <a href="solutions.php" class="nav-link">Solutions</a>
        <a href="sectors.php" class="nav-link">Sectors</a>
        <a href="contact.php" class="nav-link">Contact</a>
      </nav>
    </header>

    <main>
      <section class="hero">
        <h1>Connecting Ambition with Opportunity</h1>
        <p>We are the digital bridge in the industrial and service sectors, making it simpler than ever to find the right equipment, services, and skilled professionals right when you need them.</p>
      </section>

      <section class="container fade-in-section">
        <div class="story-section">
            <div class="story-content">
                <p class="section-subtitle">Our Journey</p>
                <h2>From a Complex Problem to a Simple Solution</h2>
                <p>The rental and service industry has long been fragmented. Finding reliable equipment, verifying skilled operators, and managing bookings was a chaotic process of endless phone calls, uncertain availability, and paperwork nightmares. We saw an opportunity to change that.</p>
                <p><strong>TR:Dots was born from a simple idea:</strong> what if we could create a single, trusted platform to connect everyone? A place where businesses could easily list their assets and services, and customers could find and book them with confidence and transparency. We're here to eliminate the friction, build trust, and empower growth for everyone involved.</p>
            </div>
            <div class="story-image">
                <img src="images/team-collaboration.jpg" alt="TR:Dots team collaborating" onerror="this.onerror=null;this.src='https://placehold.co/600x400/2c3e50/ffffff?text=Empowering+Growth';">
            </div>
        </div>
      </section>

      <section class="how-it-works fade-in-section">
        <div class="container">
            <p class="section-subtitle">How It Works</p>
            <h2>Your Project, Simplified</h2>
            <div class="steps-grid">
                <div class="step-card">
                    <div class="step-icon"><i class="fa-solid fa-magnifying-glass"></i></div>
                    <h3>1. Search & Discover</h3>
                    <p>Easily find equipment, services, or skilled operators using our powerful search and filtering tools.</p>
                </div>
                <div class="step-card">
                    <div class="step-icon"><i class="fa-solid fa-calendar-check"></i></div>
                    <h3>2. Book with Confidence</h3>
                    <p>Check real-time availability, view ratings, and book securely through our transparent platform.</p>
                </div>
                <div class="step-card">
                    <div class="step-icon"><i class="fa-solid fa-screwdriver-wrench"></i></div>
                    <h3>3. Manage & Execute</h3>
                    <p>Communicate directly, track your bookings, and manage all project details in one centralized dashboard.</p>
                </div>
                <div class="step-card">
                    <div class="step-icon"><i class="fa-solid fa-star"></i></div>
                    <h3>4. Rate & Grow</h3>
                    <p>Provide feedback to help build a trusted community and find reliable partners for future projects.</p>
                </div>
            </div>
        </div>
      </section>

      <section class="container fade-in-section">
        <p class="section-subtitle">Our Core Values</p>
        <h2>The Principles That Guide Us</h2>
        <div class="values-grid">
            <div class="value-card">
                <div>
                    <i class="fa-solid fa-handshake-angle"></i>
                    <h3>Trust & Transparency</h3>
                    <p>We believe in open communication and honest dealings. Our rating systems and verified profiles are designed to build a community you can rely on.</p>
                </div>
            </div>
            <div class="value-card">
                <div>
                    <i class="fa-solid fa-lightbulb"></i>
                    <h3>Innovation for Impact</h3>
                    <p>We are constantly evolving our technology to solve real-world problems, making our platform more efficient, intuitive, and powerful for our users.</p>
                </div>
            </div>
            <div class="value-card">
                <div>
                    <i class="fa-solid fa-users"></i>
                    <h3>Community Empowerment</h3>
                    <p>Our success is tied to the success of our users. We provide the tools and visibility for businesses to grow and for customers to execute their projects flawlessly.</p>
                </div>
            </div>
        </div>
      </section>

      <section class="container fade-in-section">
        <div class="cta-section">
            <h2>Ready to Build, Create, or Grow?</h2>
            <p>Explore our marketplace to find what you need, or join our network of trusted providers.</p>
            <div class="cta-buttons">
                <a href="marketplace.php" class="btn btn-primary">Explore Marketplace</a>
                <a href="register.php" class="btn btn-secondary">Become a Partner</a>
            </div>
        </div>
      </section>
    </main>

    <footer>
        <div class="footer-container">
            <div class="footer-about">
                <a href="index.php" class="footer-logo">
                    <img src="images/logo1.png" alt="TR:Dots Footer Logo" onerror="this.style.display='none'">
                </a>
                <p>The all-in-one platform for equipment rentals, service bookings, and skilled workforce management. We connect you to the right resources, right now.</p>
                <div class="footer-socials">
                    <a href="#" aria-label="Facebook"><i class="fa-brands fa-facebook-f"></i></a>
                    <a href="#" aria-label="LinkedIn"><i class="fa-brands fa-linkedin-in"></i></a>
                    <a href="#" aria-label="Twitter"><i class="fa-brands fa-twitter"></i></a>
                </div>
            </div>
            <div class="footer-links">
                <h4>Quick Links</h4>
                <ul>
                    <li><a href="about.php">About Us</a></li>
                    <li><a href="contact.php">Contact</a></li>
                    <li><a href="faq.php">FAQ</a></li>
                    <li><a href="login.php">Login</a></li>
                    <li><a href="register.php">Register</a></li>
                </ul>
            </div>
            <div class="footer-contact">
                <h4>Contact Us</h4>
                <p><i class="fa-solid fa-location-dot"></i> Kurunegala, Sri Lanka</p>
                <p><i class="fa-solid fa-envelope"></i> <a href="mailto:support@trdots.com">support@trdots.com</a></p>
                <p><i class="fa-solid fa-phone"></i> <a href="tel:+94123456789">+94 123 456 789</a></p>
            </div>
        </div>
        <div class="footer-bottom">
            &copy; <?php echo date("Y"); ?> TR:Dots. All Rights Reserved. | <a href="privacy.php">Privacy Policy</a>
        </div>
    </footer>
  </div>

  <script>
    window.addEventListener("load", () => {
      const loadingScreen = document.querySelector(".loading-screen");
      loadingScreen.style.opacity = "0";
      loadingScreen.style.visibility = "hidden";
      document.body.style.opacity = "1";

      const observer = new IntersectionObserver((entries) => {
          entries.forEach(entry => {
              if (entry.isIntersecting) {
                  entry.target.classList.add('is-visible');
              }
          });
      }, {
          threshold: 0.1
      });

      document.querySelectorAll('.fade-in-section').forEach(section => {
          observer.observe(section);
      });
    });
  </script>
</body>
</html>
