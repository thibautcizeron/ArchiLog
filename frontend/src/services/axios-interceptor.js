// src/services/axios-interceptor.js
import axios from 'axios';

const setupAxiosInterceptors = () => {
  axios.interceptors.request.use(
    (config) => {
      const user = JSON.parse(localStorage.getItem('user'));
      
      if (user && user.token) {
        config.headers.Authorization = `Bearer ${user.token}`;
      }
      
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  axios.interceptors.response.use(
    (response) => {
      return response;
    },
    (error) => {
      if (error.response && error.response.status === 401) {
        // Token expiré ou invalide - déconnexion
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
      
      return Promise.reject(error);
    }
  );
};

export default setupAxiosInterceptors;