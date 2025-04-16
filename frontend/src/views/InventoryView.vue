<!-- src/views/InventoryView.vue -->
<template>
    <div class="page-container">
      <div class="content">
        <div class="inventory-container">
          <h1 class="page-title">Mon inventaire</h1>
  
          <div class="inventory-tabs">
            <button 
              class="tab-button" 
              :class="{ active: activeTab === 'all' }" 
              @click="activeTab = 'all'"
            >
              Tous ({{allItems.length}})
            </button>
            <button 
              class="tab-button" 
              :class="{ active: activeTab === 'available' }" 
              @click="activeTab = 'available'"
            >
              Disponibles ({{availableItems.length}})
            </button>
            <button 
              class="tab-button" 
              :class="{ active: activeTab === 'sold' }" 
              @click="activeTab = 'sold'"
            >
              Vendus ({{soldItems.length}})
            </button>
          </div>
  
          <div class="search-filter">
            <div class="search-box">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="11" cy="11" r="8"></circle>
                <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
              </svg>
              <input 
                type="text" 
                placeholder="Rechercher un article..." 
                v-model="searchQuery"
              >
            </div>
            <div class="filter-dropdown">
              <select v-model="categoryFilter">
                <option value="">Toutes les catégories</option>
                <option value="electronics">Électronique</option>
                <option value="clothing">Vêtements</option>
                <option value="home">Maison</option>
                <option value="sports">Sports & Loisirs</option>
              </select>
            </div>
          </div>
  
          <div class="inventory-grid" v-if="filteredItems.length">
            <div 
              class="inventory-item" 
              v-for="(item, index) in filteredItems" 
              :key="index"
            >
              <div class="item-image" :style="{ backgroundColor: item.color }">
                <span class="item-initials">{{ item.name.charAt(0) }}</span>
              </div>
              <div class="item-details">
                <h3 class="item-name">{{ item.name }}</h3>
                <p class="item-category">{{ getCategoryName(item.category) }}</p>
                <div class="item-price-status">
                  <span class="item-price">{{ item.price }}€</span>
                  <span :class="['item-status', 'status-' + item.status]">
                    {{ item.status === 'available' ? 'Disponible' : 'Vendu' }}
                  </span>
                </div>
              </div>
              <div class="item-actions">
                <button class="action-edit">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20 14.66V20a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h5.34"></path>
                    <polygon points="18 2 22 6 12 16 8 16 8 12 18 2"></polygon>
                  </svg>
                </button>
                <button class="action-delete">
                  <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="3 6 5 6 21 6"></polyline>
                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                  </svg>
                </button>
              </div>
            </div>
          </div>
  
          <div class="no-items" v-else>
            <svg xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="#ccc" stroke-width="1">
              <rect x="2" y="3" width="20" height="18" rx="2"></rect>
              <line x1="2" y1="9" x2="22" y2="9"></line>
              <line x1="9" y1="21" x2="9" y2="9"></line>
            </svg>
            <p>Aucun article trouvé</p>
            <router-link to="/sell" class="add-item-button">
              Ajouter un article
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </template>

<script>
import axios from 'axios';
import authStore from '../store/AuthStore';

export default {
  name: 'InventoryView',
  data() {
    return {
      activeTab: 'all',
      searchQuery: '',
      categoryFilter: '',
      allItems: []
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
        const userId = authStore.state.userId;
        // Récupérer les cartes de l'utilisateur
        const response = await axios.get(`/card/api/cards/user/${userId}`);
        this.allItems  = response.data;
        
        console.log('Cartes de l\'utilisateur:', this.userCards);
      } catch (error) {
        console.error('Erreur lors de la récupération des cartes:', error);
        this.error = "Impossible de charger vos cartes. Veuillez réessayer plus tard.";
      } finally {
        this.loading = false;
      }
    },
    getCategoryName(category) {
      const categories = {
        'electronics': 'Électronique',
        'clothing': 'Vêtements',
        'home': 'Maison',
        'sports': 'Sports & Loisirs'
      };
      return categories[category] || category;
    }
  },
  computed: {
    availableItems() {
      return this.allItems.filter(item => item.status === 'available');
    },
    soldItems() {
      return this.allItems.filter(item => item.status === 'sold');
    },
    filteredItems() {
      let items = this.allItems;

      if (this.activeTab === 'available') {
        items = this.availableItems;
      } else if (this.activeTab === 'sold') {
        items = this.soldItems;
      }

      if (this.categoryFilter) {
        items = items.filter(item => item.category === this.categoryFilter);
      }

      if (this.searchQuery.trim()) {
        const query = this.searchQuery.toLowerCase();
        items = items.filter(item =>
            item.name.toLowerCase().includes(query) ||
            this.getCategoryName(item.category).toLowerCase().includes(query)
        );
      }

      return items;
    }
  }
}
</script>


<style src="../assets/styles/Inventory.css" scoped></style>