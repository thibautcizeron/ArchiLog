<!-- frontend/src/components/Login.vue -->
<template>
  <div class="auth-form">
    <div class="form-header">
      <h3>{{ isSignupMode ? 'Inscription' : 'Connexion' }}</h3>
      <button class="close-button" @click="$emit('close')">×</button>
    </div>
    
    <!-- Login Form -->
    <div v-if="!isSignupMode">
      <div class="form-group">
        <label for="login-username">Nom d'utilisateur</label>
        <input 
          type="text" 
          id="login-username" 
          v-model="loginForm.username" 
          placeholder="Votre nom d'utilisateur"
          :class="{'error-input': formErrors.login && formErrors.login.username}"
        >
        <p class="error-text" v-if="formErrors.login && formErrors.login.username">
          {{ formErrors.login.username }}
        </p>
      </div>
      
      <div class="form-group">
        <label for="login-password">Mot de passe</label>
        <input 
          type="password" 
          id="login-password" 
          v-model="loginForm.password" 
          placeholder="Votre mot de passe"
          :class="{'error-input': formErrors.login && formErrors.login.password}"
        >
        <p class="error-text" v-if="formErrors.login && formErrors.login.password">
          {{ formErrors.login.password }}
        </p>
      </div>
      
      <div class="form-actions">
        <button 
          class="auth-submit" 
          @click="handleLogin" 
          :disabled="isLoading"
        >
          <span v-if="isLoading">Connexion en cours...</span>
          <span v-else>Se connecter</span>
        </button>
        <a href="#" class="forgot-password">Mot de passe oublié?</a>
      </div>
      
      <div class="form-footer">
        <p>Pas encore de compte? <a href="#" @click.prevent="isSignupMode = true">S'inscrire</a></p>
      </div>
      
      <div v-if="loginError" class="error-message">
        {{ loginError }}
      </div>
    </div>

    <!-- Signup Form -->
    <div v-else>
      <div class="form-group">
        <label for="signup-username">Nom d'utilisateur</label>
        <input 
          type="text" 
          id="signup-username" 
          v-model="signupForm.username" 
          placeholder="Choisissez un nom d'utilisateur"
          :class="{'error-input': formErrors.signup && formErrors.signup.username}"
        >
        <p class="error-text" v-if="formErrors.signup && formErrors.signup.username">
          {{ formErrors.signup.username }}
        </p>
      </div>
      
      <div class="form-group">
        <label for="signup-email">Email</label>
        <input 
          type="email" 
          id="signup-email" 
          v-model="signupForm.email" 
          placeholder="Votre email"
          :class="{'error-input': formErrors.signup && formErrors.signup.email}"
        >
        <p class="error-text" v-if="formErrors.signup && formErrors.signup.email">
          {{ formErrors.signup.email }}
        </p>
      </div>
      
      <div class="form-group">
        <label for="signup-password">Mot de passe</label>
        <input 
          type="password" 
          id="signup-password" 
          v-model="signupForm.password" 
          placeholder="Choisissez un mot de passe"
          :class="{'error-input': formErrors.signup && formErrors.signup.password}"
        >
        <p class="error-text" v-if="formErrors.signup && formErrors.signup.password">
          {{ formErrors.signup.password }}
        </p>
      </div>
      
      <div class="form-group">
        <label for="signup-confirm">Confirmer le mot de passe</label>
        <input 
          type="password" 
          id="signup-confirm" 
          v-model="signupForm.confirmPassword" 
          placeholder="Confirmez votre mot de passe"
          :class="{'error-input': passwordMismatch}"
        >
        <p class="error-text" v-if="passwordMismatch">
          Les mots de passe ne correspondent pas
        </p>
      </div>
      
      <div class="form-group">
        <input type="checkbox" id="terms" v-model="signupForm.acceptTerms">
        <label for="terms" class="inline-label">J'accepte les <a href="#">conditions d'utilisation</a></label>
        <p class="error-text" v-if="formErrors.signup && formErrors.signup.acceptTerms">
          {{ formErrors.signup.acceptTerms }}
        </p>
      </div>
      
      <div class="form-actions">
        <button 
          class="auth-submit" 
          @click="handleSignup" 
          :disabled="!canSignup || isLoading"
        >
          <span v-if="isLoading">Inscription en cours...</span>
          <span v-else>S'inscrire</span>
        </button>
      </div>
      
      <div class="form-footer">
        <p>Déjà inscrit? <a href="#" @click.prevent="isSignupMode = false">Se connecter</a></p>
      </div>
      
      <div v-if="signupError" class="error-message">
        {{ signupError }}
      </div>
    </div>
  </div>
</template>

<script>
import authStore from '../store/auth.store';

export default {
  name: 'LoginComponent',
  emits: ['close', 'login-success'],
  
  data() {
    return {
      isSignupMode: false,
      isLoading: false,
      loginForm: {
        username: '',
        password: ''
      },
      signupForm: {
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
        acceptTerms: false
      },
      formErrors: {
        login: {},
        signup: {}
      },
      loginError: '',
      signupError: ''
    };
  },
  
  computed: {
    passwordMismatch() {
      return this.signupForm.password &&
             this.signupForm.confirmPassword &&
             this.signupForm.password !== this.signupForm.confirmPassword;
    },
    
    canSignup() {
      return this.signupForm.username &&
             this.signupForm.email &&
             this.signupForm.password &&
             this.signupForm.confirmPassword &&
             !this.passwordMismatch &&
             this.signupForm.acceptTerms;
    }
  },
  
  methods: {
    validateLoginForm() {
      this.formErrors.login = {};
      let isValid = true;
      
      if (!this.loginForm.username) {
        this.formErrors.login.username = "Le nom d'utilisateur est requis";
        isValid = false;
      }
      
      if (!this.loginForm.password) {
        this.formErrors.login.password = "Le mot de passe est requis";
        isValid = false;
      }
      
      return isValid;
    },
    
    validateSignupForm() {
      this.formErrors.signup = {};
      let isValid = true;
      
      if (!this.signupForm.username) {
        this.formErrors.signup.username = "Le nom d'utilisateur est requis";
        isValid = false;
      }
      
      if (!this.signupForm.email) {
        this.formErrors.signup.email = "L'email est requis";
        isValid = false;
      } else if (!/\S+@\S+\.\S+/.test(this.signupForm.email)) {
        this.formErrors.signup.email = "Format d'email invalide";
        isValid = false;
      }
      
      if (!this.signupForm.password) {
        this.formErrors.signup.password = "Le mot de passe est requis";
        isValid = false;
      } else if (this.signupForm.password.length < 6) {
        this.formErrors.signup.password = "Le mot de passe doit contenir au moins 6 caractères";
        isValid = false;
      }
      
      if (this.passwordMismatch) {
        isValid = false;
      }
      
      if (!this.signupForm.acceptTerms) {
        this.formErrors.signup.acceptTerms = "Vous devez accepter les conditions d'utilisation";
        isValid = false;
      }
      
      return isValid;
    },
    
    async handleLogin() {
      if (!this.validateLoginForm()) {
        return;
      }
      
      this.isLoading = true;
      this.loginError = '';
      
      try {
        await authStore.login(this.loginForm.username, this.loginForm.password);
        this.$emit('login-success');
        this.$emit('close');
      } catch (error) {
        this.loginError = "Échec de la connexion. Vérifiez vos identifiants.";
        console.error("Login error:", error);
      } finally {
        this.isLoading = false;
      }
    },
    
    async handleSignup() {
      if (!this.validateSignupForm()) {
        return;
      }
      
      this.isLoading = true;
      this.signupError = '';
      
      try {
        await authStore.register(
          this.signupForm.username,
          this.signupForm.email,
          this.signupForm.password
        );
        
        // Automatic login after successful registration
        await authStore.login(this.signupForm.username, this.signupForm.password);
        
        this.$emit('login-success');
        this.$emit('close');
      } catch (error) {
        this.signupError = "Échec de l'inscription. Veuillez réessayer.";
        console.error("Signup error:", error);
      } finally {
        this.isLoading = false;
      }
    }
  }
};
</script>

<style scoped>
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

.error-message {
  margin-top: 1rem;
  padding: 0.8rem;
  background-color: #ffebee;
  color: #d32f2f;
  border-radius: 4px;
  font-size: 0.9rem;
}

.error-text {
  color: #d32f2f;
  font-size: 0.8rem;
  margin-top: 0.3rem;
}

.error-input {
  border-color: #d32f2f !important;
}
</style>