import axios from "axios"

const getBaseURL = () => {
    const path = window.location.pathname;
    if (path.includes("login")) {
        return "http://localhost:8082";
    }
    return "http://localhost:8080";
};

const axiosInstance = axios.create({
    baseURL: getBaseURL(),
    headers: {
        post: {
            "Content-Type": "application/json",
            "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Headers":
                "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With"
        }
    }
});

// Interceptor: ataseaza automat header-ul Authorization: Basic ...
// la FIECARE request pe care il face aplicatia React
axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('authToken');
        if (token) {
            config.headers['Authorization'] = `Basic ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Interceptor pe raspuns: gestioneaza erorile de autentificare/autorizare
axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        const requestUrl = error.config?.url || '';

        if (error.response && error.response.status === 401 && !requestUrl.includes('/api/auth')) {
            // 401 pe o ruta protejata -> sesiunea nu e valida, redirectam la login
            localStorage.removeItem('authToken');
            localStorage.removeItem('isAuthenticated');
            localStorage.removeItem('user');
            window.location.reload();
        }

        // 403 Forbidden -> user-ul nu are rolul necesar (ex: CLIENT pe ruta de ADMIN)
        // Nu facem redirect, lasam componenta sa afiseze eroarea
        return Promise.reject(error);
    }
);

export default axiosInstance;