<!-- src/views/SellView.vue -->
<template>
  <div class="page-container">
    <div class="navbar">
      <button class="back-button" @click="$router.push('/')">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
        Retour
      </button>
      <h2>Vendre une carte</h2>
    </div>

    <div class="content">
      <div class="form-container">
        <h3>Sélectionnez une carte à vendre</h3>
        
        <!-- Liste des cartes de l'utilisateur -->
        <div v-if="loading" class="loading-indicator">
          <p>Chargement de vos cartes...</p>
        </div>
        
        <div v-else-if="error" class="error-message">
          <p>{{ error }}</p>
        </div>
        
        <div v-else-if="userCards.length === 0" class="no-cards">
          <p>Vous n'avez aucune carte à vendre.</p>
          <router-link to="/buy" class="action-button">Acheter des cartes</router-link>
        </div>
        
        <div v-else>
          <div class="cards-selection">
            <div 
              v-for="card in userCards" 
              :key="card.id"
              :class="['card-option', { 'selected': selectedCard && selectedCard.id === card.id }]"
              @click="selectCard(card)"
            >
              <div class="card-preview" :class="getCardRarityClass(card.rarity)">
                <div class="card-name">{{ card.name }}</div>
                <div class="card-type">{{ card.type }}</div>
                <div class="card-rarity">{{ card.rarity }}</div>
              </div>
            </div>
          </div>
          
          <div v-if="selectedCard" class="sell-form">
            <h4>Détails de la vente</h4>
            
            <div class="selected-card-details">
              <p><strong>Carte sélectionnée:</strong> {{ selectedCard.name }}</p>
              <p><strong>Rareté:</strong> {{ selectedCard.rarity }}</p>
              <p><strong>Type:</strong> {{ selectedCard.type }}</p>
            </div>
            
            <div class="form-group">
              <label for="price">Prix de vente (€)</label>
              <input 
                type="number" 
                id="price" 
                v-model="price" 
                min="1" 
                step="1"
                placeholder="Entrez le prix de vente"
              >
              <div v-if="priceError" class="form-error">{{ priceError }}</div>
            </div>
            
            <div class="form-group">
              <label for="description">Description complémentaire (optionnel)</label>
              <textarea 
                id="description" 
                v-model="description" 
                rows="3" 
                placeholder="Ajoutez des informations supplémentaires pour les acheteurs"
              ></textarea>
            </div>
            
            <div class="button-group">
              <button class="action-button" @click="sellCard" :disabled="isSubmitting">
                {{ isSubmitting ? 'Publication en cours...' : 'Mettre en vente' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import AuthStore from '../store/AuthStore';

export default {
  name: 'SellView',
  data() {
    return {
      userCards: [],
      loading: true,
      error: null,
      selectedCard: null,
      price: null,
      description: '',
      priceError: '',
      isSubmitting: false
    }
  },
  mounted() {
    this.fetchUserCards();
  },
  methods: {
    async fetchUserCards() {
      this.loading = true;
      this.error = null;
      
      try {
        const userId = AuthStore.state.userId;
        if (!userId) {
          this.error = "Vous devez être connecté pour accéder à cette page.";
          this.loading = false;
          return;
        }
        
        // Récupérer les cartes de l'utilisateur
        const response = await axios.get(`/card/api/cards/user/${userId}`);
        this.userCards = response.data;
        
        console.log('Cartes de l\'utilisateur:', this.userCards);
      } catch (error) {
        console.error('Erreur lors de la récupération des cartes:', error);
        this.error = "Impossible de charger vos cartes. Veuillez réessayer plus tard.";
      } finally {
        this.loading = false;
      }
    },
    
    selectCard(card) {
      this.selectedCard = card;
      this.price = card.price || null;
      this.priceError = '';
    },
    
    getCardRarityClass(rarity) {
      const rarityClasses = {
        'Common': 'common',
        'Uncommon': 'uncommon',
        'Rare': 'rare',
        'Epic': 'epic',
        'Legendary': 'legendary'
      };
      return rarityClasses[rarity] || 'common';
    },
    
    validateForm() {
      this.priceError = '';
      
      if (!this.selectedCard) {
        this.error = "Veuillez sélectionner une carte à vendre.";
        return false;
      }
      
      if (!this.price || this.price <= 0) {
        this.priceError = "Veuillez entrer un prix valide (supérieur à 0).";
        return false;
      }
      
      return true;
    },
    
    async sellCard() {
    if (!this.validateForm()) return;
    
    this.isSubmitting = true;
    
    try {
      const cardId = this.selectedCard.id;
      
      // Utilisation du paramètre de chemin comme défini dans votre back-end
      const response = await axios.post(`/market/api/market/sell/${cardId}`);
      
      if (response.status === 200) {
        AuthStore.showNotification('Votre carte a été mise en vente avec succès!', 'success');
        // Rediriger vers l'inventaire
        this.$router.push('/inventory');
      } else {
        throw new Error('La mise en vente a échoué.');
      }
    } catch (error) {
      console.error('Erreur lors de la mise en vente:', error);
      AuthStore.showNotification(
        'Erreur lors de la mise en vente. ' + 
        (error.response?.data || 'Veuillez réessayer.'), 
        'error'
      );
    } finally {
      this.isSubmitting = false;
    }
  }
}
}

</script>

<style src="../assets/styles/SellView.css" scoped></style>