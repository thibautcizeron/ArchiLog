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
        <!-- Votre contenu de page ici -->
        <div class="form-container">
          <h3>Que souhaitez-vous acheter ?</h3>
          
          <div class="form-group">
            <label for="category">Catégorie</label>
            <select id="category" v-model="category">
              <option value="">Sélectionnez une catégorie</option>
              <option value="electronics">Électronique</option>
              <option value="clothing">Vêtements</option>
              <option value="home">Maison</option>
            </select>
          </div>
          
          <div class="form-group">
            <label for="price">Budget</label>
            <input type="range" id="price" v-model="price" min="0" max="1000" step="50">
            <span>{{ price }}€</span>
          </div>
          
          <div class="form-group">
            <label for="search">Recherche</label>
            <input type="text" id="search" v-model="search" placeholder="Que recherchez-vous ?">
          </div>
          
          <button class="action-button">Rechercher</button>
        </div>
      </div>
    </div>
  </template>
  
  // Mise à jour de BuyView.vue
  <script>
  import marketService from '../services/market.service';
  import cardService from '../services/card.service';
  import authStore from '../store/auth.store';
  
  export default {
    name: 'BuyView',
    data() {
      return {
        category: '',
        price: 500,
        search: '',
        listings: [],
        loading: false,
        error: null,
        success: null
      }
    },
    created() {
      this.fetchListings();
    },
    computed: {
      filteredListings() {
        let results = this.listings;
        
        if (this.category) {
          results = results.filter(listing => listing.type === this.category);
        }
        
        if (this.price) {
          results = results.filter(listing => listing.price <= this.price);
        }
        
        if (this.search) {
          const query = this.search.toLowerCase();
          results = results.filter(listing => 
            listing.name.toLowerCase().includes(query) || 
            listing.description.toLowerCase().includes(query)
          );
        }
        
        return results;
      }
    },
    methods: {
      async fetchListings() {
        this.loading = true;
        try {
          const listings = await marketService.getAllListings();
          // For each listing, fetch the card details
          this.listings = [];
          for (const listing of listings) {
            try {
              const card = await cardService.getCardById(listing.cardId);
              this.listings.push({
                ...card,
                price: listing.price,
                sellerId: listing.sellerId
              });
            } catch (error) {
              console.error(`Error fetching card ${listing.cardId}:`, error);
            }
          }
        } catch (error) {
          this.error = 'Failed to fetch listings. Please try again.';
          console.error('Error fetching listings:', error);
        } finally {
          this.loading = false;
        }
      },
      async buyCard(cardId) {
        if (!authStore.isAuthenticated()) {
          this.error = 'Please login to buy cards';
          return;
        }
        
        this.loading = true;
        try {
          await marketService.buyCard(cardId);
          this.success = 'Card purchased successfully!';
          this.fetchListings(); // Refresh listings
        } catch (error) {
          this.error = 'Failed to buy card. Please try again.';
          console.error('Error buying card:', error);
        } finally {
          this.loading = false;
        }
      }
    }
  }
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
    justify-content: center;
  }
  
  .form-container {
    background-color: white;
    padding: 2rem;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 600px;
  }
  
  h3 {
    margin-bottom: 1.5rem;
    color: #333;
  }
  
  .form-group {
    margin-bottom: 1.5rem;
  }
  
  label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: bold;
    color: #555;
  }
  
  input, select {
    width: 100%;
    padding: 0.8rem;
    border: 1px solid #ddd;
    border-radius: 5px;
    font-size: 1rem;
  }
  
  input[type="range"] {
    margin-bottom: 0.5rem;
  }
  
  .action-button {
    background-color: #7966f6;
    color: white;
    border: none;
    padding: 1rem 2rem;
    font-size: 1rem;
    font-weight: bold;
    border-radius: 5px;
    cursor: pointer;
    width: 100%;
    transition: background-color 0.3s ease;
  }
  
  .action-button:hover {
    background-color: #6452d9;
  }
  </style>