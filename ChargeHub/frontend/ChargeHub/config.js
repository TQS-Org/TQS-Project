const isDevelopment = window.location.hostname === 'localhost';

const CONFIG = {
    API_URL: isDevelopment 
        ? 'http://localhost:8080/api/'
        : 'http://deti-tqs-23.ua.pt:8080/api/',
};

export default CONFIG;