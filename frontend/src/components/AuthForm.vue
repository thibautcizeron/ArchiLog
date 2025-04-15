<template>
    <div class="auth-popup" v-if="show">
      <!-- Login Form -->
      <div class="auth-form" v-if="!isSignupMode">
        <div class="form-header">
          <h3>Connexion</h3>
          <button class="close-button" @click="close">×</button>
        </div>
        <div class="form-group">
          <label for="login-email">Utilisateur</label>
          <input type="email" id="login-email" v-model="loginEmail" placeholder="Votre nom d'utilisateur">
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
          <button class="close-button" @click="close">×</button>
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
          <label for="terms" class="inline-label">J'accepte les <a href="/terms" target="_blank" rel="noopener noreferrer">conditions d'utilisation</a></label>
        </div>
        <div class="form-actions">
          <button class="auth-submit" @click="signup" :disabled="!canSignup">S'inscrire</button>
        </div>
        <div class="form-footer">
          <p>Déjà inscrit? <a href="#" @click.prevent="isSignupMode = false">Se connecter</a></p>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import AuthStore from '../store/AuthStore';
  
  export default {
    props: {
      show: {
        type: Boolean,
        default: false
      }
    },
    
    data() {
      return {
        isSignupMode: false,
        
        loginEmail: '',
        loginPassword: '',
        
        signupName: '',
        signupEmail: '',
        signupPassword: '',
        signupConfirmPassword: '',
        acceptTerms: false,
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
      close() {
        this.$emit('close');
        this.resetForms();
      },
      
      async login() {
        const loginData = {
          email: this.loginEmail,
          password: this.loginPassword
        };
        
        const success = await AuthStore.login(loginData);
        if (success) {
          this.resetForms();
          this.$emit('close');
        }
      },
      
      async signup() {
        if (!this.canSignup) return;
        
        const userData = {
          username: this.signupName,
          email: this.signupEmail,
          password: this.signupPassword
        };
  
        const success = await AuthStore.signup(userData);
        if (success) {
          this.isSignupMode = false;
          this.resetForms();
        }
      },
      
      resetForms() {
        this.loginEmail = '';
        this.loginPassword = '';
        this.signupName = '';
        this.signupEmail = '';
        this.signupPassword = '';
        this.signupConfirmPassword = '';
        this.acceptTerms = false;
      }
    }
  }
  </script>