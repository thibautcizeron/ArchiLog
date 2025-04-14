<!-- src/App.vue -->
<template>
  <div id="app">
    <header class="app-header">
      <div class="header-left">
        <a href="/" class="logo">Card Game</a>
        <nav class="main-nav">
          <router-link to="/buy" class="nav-item">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
            </svg>
            Acheter
          </router-link>
          <router-link to="/sell" class="nav-item">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"></circle>
              <path d="M16 8h-6a2 2 0 1 0 0 4h4a2 2 0 1 1 0 4H8"></path>
              <path d="M12 6v2m0 8v2"></path>
            </svg>
            Vendre
          </router-link>
          <router-link to="/play" class="nav-item">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path>
              <line x1="3" y1="6" x2="21" y2="6"></line>
              <path d="M16 10a4 4 0 0 1-8 0"></path>
            </svg>
            Jouer
          </router-link>
          <router-link v-if="currentUser" to="/inventory" class="nav-item">
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <rect x="2" y="3" width="20" height="18" rx="2"></rect>
              <line x1="2" y1="9" x2="22" y2="9"></line>
              <line x1="9" y1="21" x2="9" y2="9"></line>
            </svg>
            Inventaire
          </router-link>
        </nav>
      </div>
      <div class="header-right">
        <div v-if="currentUser" class="user-menu">
          <button class="user-button" @click="showUserMenu = !showUserMenu">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
            <span>{{ currentUser.username }}</span>
          </button>
          
          <div v-if="showUserMenu" class="user-dropdown">
            <router-link to="/profile" class="dropdown-item">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                <circle cx="12" cy="7" r="4"></circle>
              </svg>
              Mon profil
            </router-link>
            <a href="#" class="dropdown-item" @click.prevent="logout">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                <polyline points="16 17 21 12 16 7"></polyline>
                <line x1="21" y1="12" x2="9" y2="12"></line>
              </svg>
              Déconnexion
            </a>
          </div>
        </div>
        
        <div v-else class="auth-buttons">
          <router-link to="/login" class="login-link">Connexion</router-link>
          <router-link to="/register" class="register-button">S'inscrire</router-link>
        </div>
      </div>
    </header>

    <main>
      <router-view/>
    </main>
  </div>
</template>

<script>
import AuthService from '@/services/AuthService';
import setupAxiosInterceptors from '@/services/axios-interceptor';

export default {
  data() {
    return {
      currentUser: null,
      showUserMenu: false
    };
  },
  created() {
    // Configurer les intercepteurs Axios
    setupAxiosInterceptors();
    
    // Récupérer l'utilisateur actuel
    this.currentUser = AuthService.getCurrentUser();
    
    // Écouter les événements de connexion/déconnexion
    this.$root.$on('login-success', this.updateCurrentUser);
    this.$root.$on('logout', this.updateCurrentUser);
  },
  methods: {
    updateCurrentUser() {
      this.currentUser = AuthService.getCurrentUser();
    },
    logout() {
      AuthService.logout();
      this.showUserMenu = false;
      this.updateCurrentUser();
      this.$router.push('/login');
      this.$root.$emit('logout');
    }
  },
  beforeDestroy() {
    // Supprimer les écouteurs d'événements
    this.$root.$off('login-success', this.updateCurrentUser);
    this.$root.$off('logout', this.updateCurrentUser);
  }
}
</script>

<style>
/* Styles existants, plus les nouveaux styles pour l'authentification */

.user-menu {
  position: relative;
}

.user-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: white;
  border: 1px solid #7966f6;
  border-radius: 20px;
  color: #7966f6;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.user-button:hover {
  background-color: #f5f5ff;
}

.user-dropdown {
  position: absolute;
  top: 120%;
  right: 0;
  width: 200px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 100;
  overflow: hidden;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: