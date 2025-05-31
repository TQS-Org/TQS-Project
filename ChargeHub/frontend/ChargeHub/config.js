const isDevelopment = window.location.hostname === 'localhost';

const CONFIG = {
    API_URL: isDevelopment 
        ? 'http://localhost:8080/api/'
        : 'http://deti-tqs-23.ua.pt:3000/api/',
};

export default CONFIG;