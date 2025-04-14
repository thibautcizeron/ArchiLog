<!-- src/views/LoginView.vue -->
<template>
    <div class="login-container">
      <div class="login-form">
        <h2>Connexion</h2>
        
        <div v-if="message" class="alert" :class="{'alert-error': isError, 'alert-success': !isError}">
          {{ message }}
        </div>
        
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="username">Nom d'utilisateur</label>
            <input
              type="text"
              id="username"
              v-model="username"
              required
              placeholder="Entrez votre nom d'utilisateur"
            />
          </div>
          
          <div class="form-group">
            <label for="password">Mot de passe</label>
            <input
              type="password"
              id="password"
              v-model="password"
              required
              placeholder="Entrez votre mot de passe"
            />
          </div>
          
          <div class="form-actions">
            <button type="submit" :disabled="loading" class="login-button">
              <span v-if="loading">Chargement...</span>
              <span v-else>Se connecter</span>
            </button>
          </div>
          
          <div class="form-footer">
            <p>Pas encore de compte ? <router-link to="/register">S'inscrire</router-link></p>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  import AuthService from '@/services/AuthService';
  
  export default {
    name: 'LoginView',
    data() {
      return {
        username: '',
        password: '',
        loading: false,
        message: '',
        isError: false
      };
    },
    created() {
      // Si déjà connecté, rediriger vers la page d'accueil
      if (AuthService.isLoggedIn()) {
        this.$router.push('/');
      }
    },
    methods: {
      async handleLogin() {
        this.loading = true;
        this.message = '';
        this.isError = false;
        
        try {
          const response = await AuthService.login(this.username, this.password);
          console.log('Connexion réussie:', response);
          
          // Rediriger vers la page précédente ou vers la page d'accueil
          const redirectPath = this.$route.query.redirect || '/';
          this.$router.push(redirectPath);
          
          // Rafraîchir le composant parent si besoin
          this.$root.$emit('login-success');
        } catch (error) {
          console.error('Erreur de connexion:', error);
          this.isError = true;
          
          if (error.response) {
            this.message = error.response.data.message || 'Identifiants invalides';
          } else {
            this.message = 'Erreur de connexion. Veuillez réessayer.';
          }
        } finally {
          this.loading = false;
        }
      }
    }
  }
  </script>
  
  <style scoped>
  .login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 80vh;
    padding: 2rem;
  }
  
  .login-form {
    width: 100%;
    max-width: 400px;
    padding: 2rem;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  }
  
  h2 {
    text-align: center;
    margin-bottom: 2rem;
    color: #7966f6;
  }
  
  .form-group {
    margin-bottom: 1.5rem;
  }
  
  label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 600;
    color: #333;
  }
  
  input {
    width: 100%;
    padding: 0.8rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
  }
  
  input:focus {
    border-color: #7966f6;
    outline: none;
  }
  
  .form-actions {
    margin-top: 2rem;
  }
  
  .login-button {
    width: 100%;
    padding: 0.8rem;
    background-color: #7966f6;
    color: white;
    border: none;
    border-radius: 4px;
    font-size: 1rem;
    font-weight: bold;
    cursor: pointer;
    transition: background-color 0.3s;
  }
  
  .login-button:hover {
    background-color: #6857d8;
  }
  
  .login-button:disabled {
    background-color: #b3b3b3;
    cursor: not-allowed;
  }
  
  .form-footer {
    margin-top: 1.5rem;
    text-align: center;
    font-size: 0.9rem;
  }
  
  .form-footer a {
    color: #7966f6;
    text-decoration: none;
    font-weight: bold;
  }
  
  .alert {
    padding: 0.8rem;
    margin-bottom: 1.5rem;
    border-radius: 4px;
    text-align: center;
  }
  
  .alert-error {
    background-color: #ffebee;
    color: #d32f2f;
    border: 1px solid #ffcdd2;
  }
  
  .alert-success {
    background-color: #e8f5e9;
    color: #388e3c;
    border: 1px solid #c8e6c9;
  }
  </style>