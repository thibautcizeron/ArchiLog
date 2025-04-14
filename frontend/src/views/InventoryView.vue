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

export default {
  name: 'InventoryView',
  data() {
    return {
      activeTab: 'all',
      searchQuery: '',
      categoryFilter: '',
      allItems: [] // Vide au départ, on la remplit dynamiquement
    }
  },
  mounted() {
    this.fetchCards();
  },
  methods: {
    async fetchCards() {
      try {
        const response = await axios.get('/card/api/cards');
        console.log('Cartes récupérées depuis le backend :', response.data);
        this.allItems = response.data.map(card => ({
          name: card.name,
          category: card.type, // ou card.category selon ta structure
          price: card.price,
          status: card.status,
          color: card.color || '#ccc' // par défaut si pas de couleur
        }));
      } catch (error) {
        console.error('Erreur lors de la récupération des cartes :', error);
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


<style scoped>
  .page-container {
    min-height: 100vh;
    background-color: #f9f9f9;
    display: flex;
    flex-direction: column;
  }
  
  .content {
    flex: 1;
    padding: 2rem;
    display: flex;
    justify-content: center;
  }
  
  .inventory-container {
    width: 100%;
    max-width: 1000px;
    background-color: white;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    padding: 2rem;
  }
  
  .page-title {
    font-size: 1.8rem;
    color: #333;
    margin-bottom: 1.5rem;
  }
  
  .inventory-tabs {
    display: flex;
    border-bottom: 1px solid #eee;
    margin-bottom: 1.5rem;
  }
  
  .tab-button {
    padding: 0.8rem 1.5rem;
    background: none;
    border: none;
    font-size: 1rem;
    color: #666;
    cursor: pointer;
    position: relative;
  }
  
  .tab-button.active {
    color: #7966f6;
    font-weight: 600;
  }
  
  .tab-button.active::after {
    content: '';
    position: absolute;
    bottom: -1px;
    left: 0;
    width: 100%;
    height: 2px;
    background-color: #7966f6;
  }
  
  .search-filter {
    display: flex;
    gap: 1rem;
    margin-bottom: 2rem;
  }
  
  .search-box {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.5rem 1rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    color: #777;
  }
  
  .search-box input {
    flex: 1;
    border: none;
    outline: none;
    font-size: 0.9rem;
  }
  
  .filter-dropdown select {
    padding: 0.5rem 1rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 0.9rem;
    color: #555;
    background-color: white;
  }
  
  .inventory-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 1.5rem;
  }
  
  .inventory-item {
    display: flex;
    align-items: center;
    padding: 1rem;
    border: 1px solid #eee;
    border-radius: 8px;
    transition: all 0.2s ease;
    position: relative;
  }
  
  .inventory-item:hover {
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
  
  .item-image {
    width: 60px;
    height: 60px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 1.5rem;
    font-weight: bold;
  }
  
  .item-details {
    flex: 1;
    margin-left: 1rem;
  }
  
  .item-name {
    font-size: 1.1rem;
    color: #333;
    margin-bottom: 0.2rem;
  }
  
  .item-category {
    font-size: 0.85rem;
    color: #777;
    margin-bottom: 0.5rem;
  }
  
  .item-price-status {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .item-price {
    font-weight: bold;
    color: #333;
  }
  
  .item-status {
    font-size: 0.8rem;
    padding: 0.2rem 0.5rem;
    border-radius: 20px;
  }
  
  .status-available {
    background-color: #e6f7ee;
    color: #2ecc71;
  }
  
  .status-sold {
    background-color: #f8eae7;
    color: #e74c3c;
  }
  
  .item-actions {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    margin-left: 1rem;
  }
  
  .action-edit, .action-delete {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s;
  }
  
  .action-edit {
    background-color: #f5f5ff;
    color: #7966f6;
  }
  
  .action-edit:hover {
    background-color: #e9e7ff;
  }
  
  .action-delete {
    background-color: #fff5f5;
    color: #e74c3c;
  }
  
  .action-delete:hover {
    background-color: #ffe7e7;
  }
  
  .no-items {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 3rem 0;
    color: #999;
  }
  
  .no-items p {
    margin: 1rem 0 1.5rem;
    font-size: 1.1rem;
  }
  
  .add-item-button {
    display: inline-block;
    padding: 0.8rem 1.5rem;
    background-color: #7966f6;
    color: white;
    text-decoration: none;
    border-radius: 5px;
    font-weight: bold;
    transition: background-color 0.3s ease;
  }
  
  .add-item-button:hover {
    background-color: #6452d9;
  }
  
  @media (max-width: 768px) {
    .inventory-container {
      padding: 1.5rem;
    }
    
    .search-filter {
      flex-direction: column;
    }
    
    .inventory-tabs {
      overflow-x: auto;
      white-space: nowrap;
    }
    
    .tab-button {
      padding: 0.8rem 1rem;
    }
  }
  </style>