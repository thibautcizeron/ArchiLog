// src/services/AuthService.js
import axios from 'axios';

const API_URL = '/auth/api/auth/';  // URL relative pour le service d'authentification via le reverse proxy

class AuthService {
  // Méthode pour s'inscrire
  async register(username, email, password) {
    try {
      const response = await axios.post(API_URL + 'register', {
        username,
        email,
        password
      });
      
      if (response.data.token) {
        localStorage.setItem('user', JSON.stringify(response.data));
      }
      
      return response.data;
    } catch (error) {
      console.error('Erreur lors de l\'inscription:', error);
      throw error;
    }
  }

  // Méthode pour se connecter
  async login(username, password) {
    try {
      const response = await axios.post(API_URL + 'login', {
        username,
        password
      });
      
      if (response.data.token) {
        localStorage.setItem('user', JSON.stringify(response.data));
      }
      
      return response.data;
    } catch (error) {
      console.error('Erreur lors de la connexion:', error);
      throw error;
    }
  }

  // Méthode pour se déconnecter
  logout() {
    const user = this.getCurrentUser();
    
    if (user && user.token) {
      // Appel au backend pour invalider le token
      axios.post(API_URL + 'logout', {}, {
        headers: {
          'Authorization': 'Bearer ' + user.token
        }
      }).catch(error => {
        console.error('Erreur lors de la déconnexion:', error);
      });
    }
    
    localStorage.removeItem('user');
  }

  // Méthode pour récupérer l'utilisateur connecté
  getCurrentUser() {
    const userStr = localStorage.getItem('user');
    if (!userStr) return null;
    
    try {
      return JSON.parse(userStr);
    } catch (e) {
      localStorage.removeItem('user');
      return null;
    }
  }

  // Méthode pour vérifier si l'utilisateur est connecté
  isLoggedIn() {
    const user = this.getCurrentUser();
    return !!user && !!user.token;
  }

  // Méthode pour vérifier si le token est valide
  async validateToken() {
    const user = this.getCurrentUser();
    if (!user || !user.token) return false;
    
    try {
      const response = await axios.get(API_URL + 'validate', {
        headers: {
          'Authorization': 'Bearer ' + user.token
        }
      });
      
      return response.data.valid;
    } catch (error) {
      console.error('Erreur lors de la validation du token:', error);
      return false;
    }
  }
}

export default new AuthService();