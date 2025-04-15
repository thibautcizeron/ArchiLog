// src/services/UserService.js
import axios from 'axios';

const api = axios.create({
  baseURL: '/user/api/users',
  withCredentials: true 
});

export default {
  getCurrentUser(userId) {
    return api.get(`/${userId}`);
  },
  
  updateUserSolde(userId, solde) {
    return api.put(`/${userId}/solde`, { solde });
  }
};