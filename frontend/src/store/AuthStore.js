import { reactive } from 'vue';
import AuthService from '../services/AuthService';
const state = reactive({
  isLoggedIn: false,
  username: '',
  userId: null,
  notification: {
    show: false,
    message: '',
    type: 'success',
    timeout: null
  }
});

export default {
  state,
  async checkAuthStatus() {
    try {
      const response = await AuthService.validateToken();
      if (response.data.valid) {
        state.isLoggedIn = true;
        state.username = response.data.username || '';
        state.userId = response.data.userId || null;
        return true;
      } else {
        this.resetUser();
        return false;
      }
    } catch (error) {
      this.resetUser();
      return false;
    }
  },
  async login(credentials) {
    try {
      const response = await AuthService.login(credentials);
     
      state.isLoggedIn = true;
      state.username = response.data.username;
      state.userId = response.data.userId;
     
      this.showNotification('Connexion réussie!', 'success');
      return true;
    } catch (error) {
      console.error('Erreur de connexion:', error.response?.data || error.message);
      this.showNotification(error.response?.data?.message || 'Échec de la connexion', 'error');
      return false;
    }
  },
  async signup(userData) {
    try {
      await AuthService.signup(userData);
      this.showNotification('Inscription réussie! Vous pouvez maintenant vous connecter.', 'success');
      return true;
    } catch (error) {
      console.error('Erreur d\'inscription:', error.response?.data || error.message);
      this.showNotification(error.response?.data?.message || 'Échec de l\'inscription', 'error');
      return false;
    }
  },
  async logout() {
    try {
      await AuthService.logout();
      this.resetUser();
      this.showNotification('Vous avez été déconnecté avec succès', 'success');
      return true;
    } catch (error) {
      console.error('Erreur de déconnexion:', error.response?.data || error.message);
      this.showNotification('Erreur lors de la déconnexion', 'error');
      return false;
    }
  },
  resetUser() {
    state.isLoggedIn = false;
    state.username = '';
    state.userId = null;
  },
  showNotification(message, type = 'success') {
    if (state.notification.timeout) {
      clearTimeout(state.notification.timeout);
    }
    state.notification.show = true;
    state.notification.message = message;
    state.notification.type = type;
    state.notification.timeout = setTimeout(() => {
      this.hideNotification();
    }, 3000);
  },
  hideNotification() {
    state.notification.show = false;
    if (state.notification.timeout) {
      clearTimeout(state.notification.timeout);
    }
  },
  getUserInitial() {
    return state.username ? state.username.charAt(0).toUpperCase() : '?';
  }
};