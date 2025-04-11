// Modification du composant NavBar.vue
<template>
    <nav class="navbar">
      <div class="container">
        <h1 class="navbar-title">{{ title }}</h1>
        <div class="navbar-buttons">
          <button class="nav-button" @click="navigate('buy')">
            <span class="icon">🛒</span> 
            <span class="text">Buy</span>
          </button>
          <button class="nav-button" @click="navigate('sell')">
            <span class="icon">💰</span> 
            <span class="text">Sell</span>
          </button>
          <button class="nav-button" @click="navigate('play')">
            <span class="icon">▶️</span> 
            <span class="text">Play</span>
          </button>
          <button v-if="isAuthenticated" class="nav-button" @click="navigate('inventory')">
            <span class="icon">📦</span> 
            <span class="text">Inventory</span>
          </button>
          <button v-if="isAuthenticated" class="nav-button logout-button" @click="logout">
            <span class="icon">🚪</span> 
            <span class="text">Logout</span>
          </button>
        </div>
      </div>
    </nav>
  </template>
  
  <script>
  import authStore from '../store/auth.store';

  export default {
    name: 'NavBar',
    props: {
      title: {
        type: String,
        default: 'Mon Site Web'
      }
    },
    data() {
      return {
        isAuthenticated: false
      }
    },
    created() {
      this.isAuthenticated = authStore.isAuthenticated();
      // Listen for authentication changes
      this.$root.$on('auth-changed', this.checkAuth);
    },
    methods: {
      navigate(route) {
        // Cette méthode peut être utilisée pour la navigation
        this.$router.push(`/${route}`);
      },
      logout() {
        authStore.logout();
        this.isAuthenticated = false;
        this.$root.$emit('auth-changed');
        this.$router.push('/');
      },
      checkAuth() {
        this.isAuthenticated = authStore.isAuthenticated();
      }
    }
  }
  </script>