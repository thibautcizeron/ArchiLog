// frontend/src/store/auth.store.js
import { reactive } from 'vue';
import authService from '../services/auth.service';

const state = reactive({
  status: {
    loggedIn: false
  },
  user: null
});

// Initialize state from local storage
const user = authService.getCurrentUser();
if (user) {
  state.status.loggedIn = true;
  state.user = user;
}

const authStore = {
  state,
  
  async login(username, password) {
    try {
      const user = await authService.login(username, password);
      state.status.loggedIn = true;
      state.user = user;
      return user;
    } catch (error) {
      state.status.loggedIn = false;
      state.user = null;
      throw error;
    }
  },
  
  async register(username, email, password) {
    try {
      const response = await authService.register(username, email, password);
      return response;
    } catch (error) {
      throw error;
    }
  },
  
  logout() {
    authService.logout();
    state.status.loggedIn = false;
    state.user = null;
  },
  
  getCurrentUser() {
    return state.user;
  },
  
  isAuthenticated() {
    return state.status.loggedIn;
  }
};

export default authStore;