<template>
  <header class="app-header">
    <div class="header-left">
      <a href="/" class="logo">Riboudou Cards Games</a>
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
        <router-link to="/inventory" class="nav-item">
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
      <button v-if="!isLoggedIn" class="login-button" @click="showAuthForm = true">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
          <circle cx="12" cy="7" r="4"></circle>
        </svg>
        <span>Connexion</span>
      </button>

      <div v-else class="user-avatar-container">
        <!-- Affichage du solde -->
        <div class="user-solde" v-if="userSolde !== null">
          <span>{{ userSolde }} €</span>
        </div>
        
        <div class="user-avatar" @click="showUserMenu = !showUserMenu">
          {{ getUserInitial }}
        </div>
        
        <div v-if="showUserMenu" class="user-menu">
          <div class="user-menu-header">
            <span class="username">{{ username }}</span>
            <!-- Affichage du solde dans le menu -->
            <div class="user-solde-menu" v-if="userSolde !== null">
              Solde: {{ userSolde }} €
            </div>
          </div>
          <div class="user-menu-items">
            <button class="menu-item" @click="logout">
              <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                <polyline points="16 17 21 12 16 7"></polyline>
                <line x1="21" y1="12" x2="9" y2="12"></line>
              </svg>
              Déconnexion
            </button>
          </div>
        </div>
      </div>
      
      <AuthForm
        v-if="showAuthForm" 
        :show="showAuthForm" 
        @close="showAuthForm = false"
      />
    </div>
  </header>
</template>

<script>
import AuthForm from './AuthForm.vue';
import AuthStore from '../store/AuthStore';
import axios from 'axios';

export default {
  components: {
    AuthForm
  },
  
  data() {
    return {
      showAuthForm: false,
      showUserMenu: false,
      userSolde: null
    }
  },
  
  computed: {
    isLoggedIn() {
      return AuthStore.state.isLoggedIn;
    },
    username() {
      return AuthStore.state.username;
    },
    getUserInitial() {
      return AuthStore.getUserInitial();
    }
  },
  
  watch: {
    // Surveille l'état de connexion pour charger le solde
    'isLoggedIn': function(newVal) {
      if (newVal) {
        this.fetchUserSolde();
      } else {
        this.userSolde = null;
      }
    }
  },
  
  mounted() {
    document.addEventListener('click', this.handleOutsideClick);
    // Charger le solde à l'initialisation si connecté
    if (this.isLoggedIn) {
      this.fetchUserSolde();
    }
  },
  
  beforeUnmount() {
    document.removeEventListener('click', this.handleOutsideClick);
  },
  
  methods: {
    handleOutsideClick(event) {
      const avatarContainer = this.$el.querySelector('.user-avatar-container');
      if (this.showUserMenu && avatarContainer && !avatarContainer.contains(event.target)) {
        this.showUserMenu = false;
      }
    },
    
    async logout() {
      const success = await AuthStore.logout();
      if (success) {
        this.showUserMenu = false;
        this.userSolde = null;
      }
    },
    
    async fetchUserSolde() {
      try {
        if (AuthStore.state.userId) {
          // Utilise un endpoint spécifique qui renvoie uniquement le solde
          const response = await axios.get(`/user/api/users/${AuthStore.state.userId}/solde`);
          if (response.data && typeof response.data.solde !== 'undefined') {
            this.userSolde = response.data.solde;
          }
        }
      } catch (error) {
        console.error('Erreur lors de la récupération du solde:', error);
      }
    }
  }
}
</script>

