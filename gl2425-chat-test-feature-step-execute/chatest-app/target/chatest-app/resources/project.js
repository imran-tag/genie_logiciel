// project.js - Highly unoptimized JavaScript file

// Global variables
let testData = [];
let imageData = [];
let analyticsData = [];

// Document ready handler
document.addEventListener('DOMContentLoaded', function() {
    console.log("Document loaded");
    
    // Initialize with heavy operations
    initializeApp();
    
    // Set up event listeners
    setupEventListeners();
    
    // Load unnecessary third-party scripts
    loadThirdPartyScripts();
    
    // Start intensive timers
    startTimers();
});

// Initialize app with heavy operations
function initializeApp() {
    console.log("Initializing app...");
    
    // Create large array in memory
    for (let i = 0; i < 10000; i++) {
        testData.push({
            id: i,
            name: 'Test ' + i,
            description: 'Description for test ' + i,
            data: Array(100).fill('data for test ' + i).join(' ')
        });
    }
    
    // Generate image data
    for (let i = 0; i < 50; i++) {
        imageData.push({
            id: i,
            url: 'https://via.placeholder.com/800x400?text=Image+' + i,
            title: 'Image ' + i,
            description: 'Description for image ' + i
        });
    }
    
    // Generate fake analytics data
    for (let i = 0; i < 1000; i++) {
        analyticsData.push({
            timestamp: Date.now() - (i * 60000),
            pageViews: Math.floor(Math.random() * 100),
            clicks: Math.floor(Math.random() * 50),
            conversions: Math.floor(Math.random() * 10)
        });
    }
    
    // Force layout reflow
    document.body.offsetHeight;
    
    // Add unnecessary elements to DOM
    addUnnecessaryElements();
}

// Add unnecessary elements to DOM
function addUnnecessaryElements() {
    const container = document.createElement('div');
    container.className = 'hidden-elements';
    container.style.display = 'none';
    
    // Add 1000 hidden elements
    for (let i = 0; i < 1000; i++) {
        const div = document.createElement('div');
        div.id = 'hidden-element-' + i;
        div.textContent = 'Hidden Element ' + i;
        div.dataset.index = i;
        container.appendChild(div);
    }
    
    document.body.appendChild(container);
}

// Set up excessive event listeners
function setupEventListeners() {
    // Add event listeners to every element
    const allElements = document.querySelectorAll('*');
    
    allElements.forEach(element => {
        // Mouse events
        element.addEventListener('mouseover', function() {
            console.log('Mouse over', element.tagName);
        });
        
        element.addEventListener('mouseout', function() {
            console.log('Mouse out', element.tagName);
        });
        
        // Click events
        element.addEventListener('click', function(e) {
            console.log('Clicked', element.tagName);
            
            // Expensive calculation on every click
            for (let i = 0; i < 10000; i++) {
                Math.sqrt(i) * Math.random();
            }
        });
    });
    
    // Form submission handlers
    const forms = document.querySelectorAll('form');
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            console.log('Form submitted', form.action);
            
            // Collect all form data
            const formData = new FormData(form);
            const formValues = {};
            
            for (const [key, value] of formData.entries()) {
                formValues[key] = value;
            }
            
            // Log form values
            console.log('Form values:', formValues);
            
            // Analytics tracking
            trackFormSubmission(formValues);
        });
    });
}

// Load unnecessary third-party scripts
function loadThirdPartyScripts() {
    // Analytics script
    const analyticsScript = document.createElement('script');
    analyticsScript.src = 'https://cdn.jsdelivr.net/npm/analytics-library@1.0.0/dist/analytics.min.js';
    document.body.appendChild(analyticsScript);
    
    // Chart library
    const chartScript = document.createElement('script');
    chartScript.src = 'https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js';
    document.body.appendChild(chartScript);
    
    // Animation library
    const animationScript = document.createElement('script');
    animationScript.src = 'https://cdn.jsdelivr.net/npm/gsap@3.11.5/dist/gsap.min.js';
    document.body.appendChild(animationScript);
    
    // 3D library
    const threeScript = document.createElement('script');
    threeScript.src = 'https://cdn.jsdelivr.net/npm/three@0.150.1/build/three.min.js';
    document.body.appendChild(threeScript);
}

// Start intensive timers
function startTimers() {
    // Update page title every second
    setInterval(function() {
        document.title = "ChatTest - " + new Date().toLocaleTimeString();
    }, 1000);
    
    // Perform heavy calculations
    setInterval(function() {
        for (let i = 0; i < 50000; i++) {
            Math.sqrt(i) * Math.sin(i) * Math.cos(i);
        }
    }, 5000);
    
    // Update DOM unnecessarily
    setInterval(function() {
        // Force layout recalculation
        const buttons = document.querySelectorAll('button');
        
        buttons.forEach(button => {
            // Toggle classes
            button.classList.toggle('animate-pulse');
            
            // Directly modify style
            button.style.transform = button.style.transform === 'scale(1.05)' ? 'scale(1)' : 'scale(1.05)';
            
            // Force reflow
            button.offsetHeight;
        });
    }, 2000);
    
    // Memory leak - keep adding to arrays
    setInterval(function() {
        // Add new items to global arrays
        testData.push({
            id: testData.length,
            name: 'Test ' + testData.length,
            timestamp: new Date().toISOString(),
            data: Array(100).fill('new data').join(' ')
        });
        
        // Create and keep unnecessary DOM references
        const newDiv = document.createElement('div');
        newDiv.className = 'memory-leak';
        newDiv.textContent = 'Leak ' + testData.length;
        newDiv.style.display = 'none';
        document.body.appendChild(newDiv);
    }, 3000);
}

// Track form submission (analytics)
function trackFormSubmission(formData) {
    // Create large payload
    const payload = {
        event: 'form_submission',
        timestamp: new Date().toISOString(),
        formData: formData,
        browser: {
            userAgent: navigator.userAgent,
            language: navigator.language,
            platform: navigator.platform,
            vendor: navigator.vendor
        },
        screen: {
            width: window.screen.width,
            height: window.screen.height,
            colorDepth: window.screen.colorDepth,
            pixelDepth: window.screen.pixelDepth,
            availWidth: window.screen.availWidth,
            availHeight: window.screen.availHeight
        },
        window: {
            innerWidth: window.innerWidth,
            innerHeight: window.innerHeight,
            outerWidth: window.outerWidth,
            outerHeight: window.outerHeight,
            scrollX: window.scrollX,
            scrollY: window.scrollY
        },
        document: {
            title: document.title,
            readyState: document.readyState,
            URL: document.URL,
            referrer: document.referrer
        },
        history: {
            length: window.history.length
        }
    };
    
    // Store analytics data
    analyticsData.push(payload);
    
    // Log payload (unnecessary)
    console.log('Analytics payload:', payload);
    
    // Simulate sending to server (inefficient XHR request)
    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/analytics', true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            console.log('Analytics sent, status:', xhr.status);
        }
    };
    xhr.send(JSON.stringify(payload));
}

// Function for form submission (from other HTML file)
function formSubmit(e) {
    // Prevent default
    e.preventDefault();
    
    console.log("Form submitted");
    
    // Inefficient form data gathering
    const form = e.target;
    const formData = {};
    
    // Manually gather all inputs
    const inputs = form.querySelectorAll('input, textarea, select');
    inputs.forEach(input => {
        if (input.name) {
            formData[input.name] = input.value;
        }
    });
    
    // Log all form data
    console.log("Form data:", formData);
    
    // Track submission
    trackFormSubmission(formData);
    
    // Heavy processing
    for (let i = 0; i < 50000; i++) {
        Math.sqrt(i) * Math.sin(i);
    }
    
    // Submit form the old way
    form.submit();
}