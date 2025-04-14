<!-- src/views/BuyView.vue -->
<template>
  <div class="page-container">
    <div class="navbar">
      <button class="back-button" @click="$router.push('/')">
        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M19 12H5M12 19l-7-7 7-7"/>
        </svg>
        Retour
      </button>
      <h2>Page d'achat</h2>
    </div>

    <div class="content">
      <div class="filters-section">
        <h3>Filtrer les cartes</h3>
        
        <div class="form-group">
          <label for="rarity">Rareté</label>
          <select id="rarity" v-model="filters.rarity">
            <option value="">Toutes les raretés</option>
            <option value="Common">Commune</option>
            <option value="Uncommon">Peu commune</option>
            <option value="Rare">Rare</option>
            <option value="Epic">Épique</option>
            <option value="Legendary">Légendaire</option>
          </select>
        </div>
        
        <div class="form-group">
          <label for="type">Type</label>
          <select id="type" v-model="filters.type">
            <option value="">Tous les types</option>
            <option value="Dragon">Dragon</option>
            <option value="Spirit">Esprit</option>
            <option value="Mage">Mage</option>
            <option value="Beast">Bête</option>
            <option value="Knight">Chevalier</option>
            <option value="Assassin">Assassin</option>
            <option value="Golem">Golem</option>
            <option value="Fairy">Fée</option>
            <option value="Elf">Elfe</option>
            <option value="Giant">Géant</option>
          </select>
        </div>
        
        <div class="form-group">
          <label for="search">Recherche</label>
          <input type="text" id="search" v-model="filters.search" placeholder="Nom ou description...">
        </div>
        
        <div class="form-group">
          <label for="maxPrice">Budget max</label>
          <input type="range" id="maxPrice" v-model="filters.maxPrice" min="0" max="1000" step="50">
          <span>{{ filters.maxPrice }}€</span>
        </div>
        
        <button class="action-button" @click="applyFilters">Appliquer les filtres</button>
      </div>

      <div class="cards-section">
        <h3>Cartes disponibles ({{ filteredCards.length }})</h3>
        
        <div v-if="loading" class="loading-indicator">
          <p>Chargement des cartes...</p>
        </div>
        
        <div v-else-if="error" class="error-message">
          <p>{{ error }}</p>
        </div>
        
        <div v-else-if="filteredCards.length === 0" class="no-cards">
          <p>Aucune carte ne correspond à vos critères.</p>
        </div>
        
        <div v-else class="cards-grid">
          <Card 
            v-for="card in filteredCards" 
            :key="card.id"
            :card="card"
            @click="buyCard(card)"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Card from '@/components/Card.vue';

export default {
  name: 'BuyView',
  components: {
    Card
  },
  data() {
    return {
      cards: [],
      loading: true,
      error: null,
      filters: {
        rarity: '',
        type: '',
        search: '',
        maxPrice: 500
      }
    };
  },
  computed: {
    filteredCards() {
      if (!this.cards.length) return [];
      
      return this.cards.filter(card => {
        // Filtre par rareté
        if (this.filters.rarity && card.rarity !== this.filters.rarity) {
          return false;
        }
        
        // Filtre par type
        if (this.filters.type && card.type !== this.filters.type) {
          return false;
        }
        
        // Filtre par recherche (nom ou description)
        if (this.filters.search) {
          const searchTerm = this.filters.search.toLowerCase();
          const nameMatch = card.name && card.name.toLowerCase().includes(searchTerm);
          const descMatch = card.description && card.description.toLowerCase().includes(searchTerm);
          if (!nameMatch && !descMatch) {
            return false;
          }
        }
        
        // Filtre par prix (si disponible dans l'API)
        if (this.filters.maxPrice && card.price && card.price > this.filters.maxPrice) {
          return false;
        }
        
        return true;
      });
    }
  },
  mounted() {
    this.loadAvailableCards();
  },
  methods: {
    async loadAvailableCards() {
      this.loading = true;
      this.error = null;
      
      try {
        // Utilisation du chemin corrigé pour le reverse proxy
        const response = await fetch('/card/api/cards/available');
        
        if (!response.ok) {
          throw new Error(`Erreur lors de la récupération des cartes: ${response.status}`);
        }
        
        this.cards = await response.json();
        
        // Ajouter des URL d'images par défaut si elles n'existent pas
        this.cards = this.cards.map(card => ({
          ...card,
          imageUrl: card.imageUrl || this.getDefaultCardImage(card.rarity)
        }));
        
        console.log('Cartes chargées:', this.cards);
      } catch (error) {
        console.error('Erreur lors du chargement des cartes:', error);
        this.error = 'Impossible de charger les cartes. Veuillez réessayer plus tard.';
      } finally {
        this.loading = false;
      }
    },
    
    applyFilters() {
      console.log('Filtres appliqués:', this.filters);
      // Vous pourriez vouloir recharger les cartes depuis l'API avec les filtres
      // si le filtrage se fait côté serveur
    },
    
    getCardColor(rarity) {
      const colors = {
        'Common': '#7d7d7d',
        'Uncommon': '#1a9b45',
        'Rare': '#2d6cd1',
        'Epic': '#9b2bda',
        'Legendary': '#e6a331'
      };
      
      return colors[rarity] || '#7966f6';
    },
    
    getDefaultCardImage(rarity) {
      const defaultImages = {
        'Common': '/assets/images/card-common.jpg',
        'Uncommon': '/assets/images/card-uncommon.jpg',
        'Rare': '/assets/images/card-rare.jpg',
        'Epic': '/assets/images/card-epic.jpg',
        'Legendary': '/assets/images/card-legendary.jpg'
      };
      
      return defaultImages[rarity] || '/assets/images/card-default.jpg';
    },
    
    async buyCard(card) {
      console.log('Tentative d\'achat de la carte:', card);
      
      try {
        // Utilisation du chemin correct pour le service de transaction avec le reverse proxy
        const response = await fetch('/transaction/api/purchase', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            cardId: card.id,
            userId: this.$store && this.$store.state.user ? this.$store.state.user.id : null
          })
        });
        
        if (response.ok) {
          const result = await response.json();
          alert(`Achat de "${card.name}" réussi !`);
          // Actualiser les cartes disponibles
          this.loadAvailableCards();
        } else {
          const errorData = await response.json().catch(() => ({}));
          alert(`Échec de l'achat de "${card.name}". ${errorData.message || 'Veuillez réessayer.'}`);
        }
      } catch (error) {
        console.error('Erreur lors de l\'achat:', error);
        alert('Une erreur est survenue lors de l\'achat. Veuillez réessayer plus tard.');
      }
    },
    
    // Méthode pour gérer des erreurs de validation côté client
    validateCard(card) {
      if (!card.id) {
        console.warn('Carte sans ID détectée:', card);
        return false;
      }
      return true;
    }
  }
};
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background-color: #f9f9f9;
  display: flex;
  flex-direction: column;
}

.navbar {
  background-color: white;
  padding: 1rem 2rem;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.back-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: none;
  border: none;
  cursor: pointer;
  color: #7966f6;
  font-weight: bold;
}

.navbar h2 {
  margin-left: 2rem;
  color: #333;
}

.content {
  flex: 1;
  padding: 2rem;
  display: flex;
  flex-wrap: wrap;
  gap: 2rem;
}

.filters-section {
  flex: 1;
  min-width: 250px;
  max-width: 300px;
  background-color: white;
  padding: 1.5rem;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: fit-content;
}

.cards-section {
  flex: 3;
  min-width: 300px;
  background-color: white;
  padding: 1.5rem;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

h3 {
  margin-bottom: 1.5rem;
  color: #333;
  font-size: 1.2rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
  color: #555;
  font-size: 0.9rem;
}

input, select {
  width: 100%;
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 0.9rem;
}

input[type="range"] {
  margin-bottom: 0.5rem;
}

.action-button {
  background-color: #7966f6;
  color: white;
  border: none;
  padding: 0.8rem 1.5rem;
  font-size: 0.9rem;
  font-weight: bold;
  border-radius: 5px;
  cursor: pointer;
  width: 100%;
  transition: background-color 0.3s ease;
}

.action-button:hover {
  background-color: #6452d9;
}

.loading-indicator, .error-message, .no-cards {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  color: #666;
}

.error-message {
  color: #e74c3c;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

@media (max-width: 768px) {
  .content {
    flex-direction: column;
  }
  
  .filters-section {
    max-width: 100%;
  }
}
</style>