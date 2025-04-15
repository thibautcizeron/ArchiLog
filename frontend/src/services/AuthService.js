// src/services/AuthService.js
import axios from 'axios';

const api = axios.create({
  baseURL: '/auth/api/auth',
  withCredentials: true 
});

export default {
  signup(userData) {
    const signupData = {
      username: userData.username,
      email: userData.email,
      password: userData.password
    };
    return api.post('/register', signupData);
  },

  login(credentials) {
    const loginData = {
      username: credentials.email,
      password: credentials.password
    };
    return api.post('/login', loginData);
  },

  logout() {
    return api.post('/logout');
  },

  validateToken() {
    return api.get('/validate'); 
  }
};
