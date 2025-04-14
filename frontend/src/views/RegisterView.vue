<!-- src/views/RegisterView.vue -->
<template>
    <div class="register-container">
      <div class="register-form">
        <h2>Inscription</h2>
        
        <div v-if="message" class="alert" :class="{'alert-error': isError, 'alert-success': !isError}">
          {{ message }}
        </div>
        
        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label for="username">Nom d'utilisateur</label>
            <input
              type="text"
              id="username"
              v-model="username"
              required
              placeholder="Choisissez un nom d'utilisateur"
            />
          </div>
          
          <div class="form-group">
            <label for="email">Email</label>
            <input
              type="email"
              id="email"
              v-model="email"
              required
              placeholder="Entrez votre email"
            />
          </div>
          
          <div class="form-group">
            <label for="password">Mot de passe</label>
            <input
              type="password"
              id="password"
              v-model="password"
              required
              placeholder="Choisissez un mot de passe"
              minlength="6"
            />
          </div>
          
          <div class="form-group">
            <label for="confirmPassword">Confirmer le mot de passe</label>
            <input
              type="password"
              id="confirmPassword"
              v-model="confirmPassword"
              required
              placeholder="Confirmez votre mot de passe"
            />
            <p v-if="passwordMismatch" class="error-message">Les mots de passe ne correspondent pas</p>
          </div>
          
          <div class="form-actions">
            <button type="submit" :disabled="loading || passwordMismatch" class="register-button">
              <span v-if="loading">Chargement...</span>
              <span v-else>S'inscrire</span>
            </button>
          </div>
          
          <div class="form-footer">
            <p>Déjà inscrit ? <router-link to="/login">Se connecter</router-link></p>
          </div>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  import AuthService from '@/services/AuthService';
  
  export default {
    name: 'RegisterView',
    data() {
      return {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        loading: false,
        message: '',
        isError: false
      };
    },
    computed: {
      passwordMismatch() {
        return this.password && this.confirmPassword && this.password !== this.confirmPassword;
      }
    },
    created() {
      // Si déjà connecté, rediriger vers la page d'accueil
      if (AuthService.isLoggedIn()) {
        this.$router.push('/');
      }
    },
    methods: {
      async handleRegister() {
        // Vérification des mots de passe
        if (this.passwordMismatch) {
          return;
        }
        
        this.loading = true;
        this.message = '';
        this.isError = false;
        
        try {
          const response = await AuthService.register(this.username, this.email, this.password);
          console.log('Inscription réussie:', response);
          
          this.isError = false;
          this.message = 'Inscription réussie! Vous allez être redirigé...';
          
          // Rediriger vers la page d'accueil après un court délai
          setTimeout(() => {
            this.$router.push('/');
            // Rafraîchir le composant parent si besoin
            this.$root.$emit('login-success');
          }, 1500);
        } catch (error) {
          console.error('Erreur d\'inscription:', error);
          this.isError = true;
          
          if (error.response) {
            this.message = error.response.data.message || 'Erreur lors de l\'inscription';
          } else {
            this.message = 'Erreur lors de l\'inscription. Veuillez réessayer.';
          }
        } finally {
          this.loading = false;
        }
      }
    }
  }
  </script>
  
  <style scoped>
  .register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 80vh;
    padding: 2rem;
  }
  
  .register-form {
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
  
  .error-message {
    color: #d32f2f;
    font-size: 0.85rem;
    margin-top: 0.5rem;
  }
  
  .form-actions {
    margin-top: 2rem;
  }
  
  .register-button {
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
  
  .register-button:hover {
    background-color: #6857d8;
  }
  
  .register-button:disabled {
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