<!-- src/App.vue -->
<template>
  <div id="app">
    <header class="app-header">
      <div class="header-left">
        <a href="/" class="logo">MonSite</a>
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
        <button class="login-button" @click="showAuthForm = !showAuthForm">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
            <circle cx="12" cy="7" r="4"></circle>
          </svg>
          <span>Connexion</span>
        </button>

        <!-- Auth Form Popup -->
        <div class="auth-popup" v-if="showAuthForm">
          <!-- Login Form -->
          <div class="auth-form" v-if="!isSignupMode">
            <div class="form-header">
              <h3>Connexion</h3>
              <button class="close-button" @click="showAuthForm = false">×</button>
            </div>
            <div class="form-group">
              <label for="login-email">Email</label>
              <input type="email" id="login-email" v-model="loginEmail" placeholder="Votre email">
            </div>
            <div class="form-group">
              <label for="login-password">Mot de passe</label>
              <input type="password" id="login-password" v-model="loginPassword" placeholder="Votre mot de passe">
            </div>
            <div class="form-actions">
              <button class="auth-submit" @click="login">Se connecter</button>
              <a href="#" class="forgot-password">Mot de passe oublié?</a>
            </div>
            <div class="form-footer">
              <p>Pas encore de compte? <a href="#" @click.prevent="isSignupMode = true">S'inscrire</a></p>
            </div>
          </div>

          <!-- Signup Form -->
          <div class="auth-form" v-else>
            <div class="form-header">
              <h3>Inscription</h3>
              <button class="close-button" @click="showAuthForm = false">×</button>
            </div>
            <div class="form-group">
              <label for="signup-name">Nom complet</label>
              <input type="text" id="signup-name" v-model="signupName" placeholder="Votre nom">
            </div>
            <div class="form-group">
              <label for="signup-email">Email</label>
              <input type="email" id="signup-email" v-model="signupEmail" placeholder="Votre email">
            </div>
            <div class="form-group">
              <label for="signup-password">Mot de passe</label>
              <input type="password" id="signup-password" v-model="signupPassword" placeholder="Votre mot de passe">
            </div>
            <div class="form-group">
              <label for="signup-confirm">Confirmer le mot de passe</label>
              <input type="password" id="signup-confirm" v-model="signupConfirmPassword" placeholder="Confirmez votre mot de passe">
              <p class="password-mismatch" v-if="passwordMismatch">Les mots de passe ne correspondent pas</p>
            </div>
            <div class="form-group">
              <input type="checkbox" id="terms" v-model="acceptTerms">
              <label for="terms" class="inline-label">J'accepte les <a href="#">conditions d'utilisation</a></label>
            </div>
            <div class="form-actions">
              <button class="auth-submit" @click="signup" :disabled="!canSignup">S'inscrire</button>
            </div>
            <div class="form-footer">
              <p>Déjà inscrit? <a href="#" @click.prevent="isSignupMode = false">Se connecter</a></p>
            </div>
          </div>
        </div>
      </div>
    </header>

    <main>
      <router-view/>
    </main>
  </div>
</template>

<script>
export default {
  data() {
    return {
      showAuthForm: false,
      isSignupMode: false,
      
      // Login form data
      loginEmail: '',
      loginPassword: '',
      
      // Signup form data
      signupName: '',
      signupEmail: '',
      signupPassword: '',
      signupConfirmPassword: '',
      acceptTerms: false
    }
  },
  computed: {
    passwordMismatch() {
      return this.signupPassword && 
             this.signupConfirmPassword && 
             this.signupPassword !== this.signupConfirmPassword;
    },
    canSignup() {
      return this.signupName && 
             this.signupEmail && 
             this.signupPassword &&
             this.signupConfirmPassword &&
             !this.passwordMismatch &&
             this.acceptTerms;
    }
  },
  methods: {
    login() {
      // Ici vous pouvez implémenter la logique de connexion
      console.log('Tentative de connexion avec:', this.loginEmail, this.loginPassword);
      alert('Fonctionnalité de connexion à implémenter');
      this.resetForms();
    },
    signup() {
      if (!this.canSignup) return;
      
      // Ici vous pouvez implémenter la logique d'inscription
      console.log('Tentative d\'inscription avec:', {
        name: this.signupName,
        email: this.signupEmail,
        password: this.signupPassword
      });
      alert('Inscription réussie! Vous pouvez maintenant vous connecter.');
      
      // Rediriger vers le formulaire de connexion après l'inscription
      this.isSignupMode = false;
      this.resetForms();
    },
    resetForms() {
      // Réinitialiser les formulaires
      this.showAuthForm = false;
      
      // Clear login form
      this.loginEmail = '';
      this.loginPassword = '';
      
      // Clear signup form
      this.signupName = '';
      this.signupEmail = '';
      this.signupPassword = '';
      this.signupConfirmPassword = '';
      this.acceptTerms = false;
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Arial', sans-serif;
}

body {
  background-color: #f9f9f9;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

main {
  flex: 1;
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.8rem 2rem;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
  color: #7966f6;
  text-decoration: none;
}

.main-nav {
  display: flex;
  gap: 1.5rem;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  color: #555;
  text-decoration: none;
  font-weight: 500;
  padding: 0.4rem 0.5rem;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.nav-item:hover {
  color: #7966f6;
  background-color: #f5f5ff;
}

.nav-item.router-link-active {
  color: #7966f6;
  font-weight: 600;
}

.header-right {
  position: relative;
}

.login-button {
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
  transition: all 0.3s ease;
}

.login-button:hover {
  background-color: #f5f5ff;
}

.auth-popup {
  position: absolute;
  top: 120%;
  right: 0;
  width: 320px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 100;
}

.auth-form {
  padding: 1.5rem;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.2rem;
}

.form-header h3 {
  color: #333;
  font-size: 1.2rem;
}

.close-button {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #777;
  cursor: pointer;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.4rem;
  font-size: 0.85rem;
  color: #555;
}

.form-group .inline-label {
  display: inline;
  margin-left: 0.5rem;
  vertical-align: middle;
}

.form-group input[type="text"],
.form-group input[type="email"],
.form-group input[type="password"] {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
}

.form-group input[type="checkbox"] {
  vertical-align: middle;
}

.password-mismatch {
  color: #e74c3c;
  font-size: 0.8rem;
  margin-top: 0.3rem;
}

.form-actions {
  margin-top: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.auth-submit {
  width: 100%;
  padding: 0.8rem;
  background-color: #7966f6;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.auth-submit:hover {
  background-color: #6452d9;
}

.auth-submit:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.forgot-password {
  text-align: center;
  font-size: 0.8rem;
  color: #7966f6;
  text-decoration: none;
}

.form-footer {
  margin-top: 1.5rem;
  text-align: center;
  font-size: 0.8rem;
  color: #555;
}

.form-footer a {
  color: #7966f6;
  text-decoration: none;
  font-weight: bold;
}

/* Media queries for responsive design */
@media (max-width: 768px) {
  .app-header {
    flex-direction: column;
    padding: 1rem;
    gap: 1rem;
  }
  
  .header-left {
    width: 100%;
    flex-direction: column;
    gap: 1rem;
  }
  
  .main-nav {
    width: 100%;
    justify-content: space-between;
  }
  
  .header-right {
    width: 100%;
    display: flex;
    justify-content: center;
  }
  
  .login-button {
    width: 100%;
    justify-content: center;
  }
  
  .auth-popup {
    right: 50%;
    transform: translateX(50%);
  }
}
</style>