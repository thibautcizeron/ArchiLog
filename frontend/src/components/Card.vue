<!-- src/components/Card.vue -->
<template>
    <div class="card-container">
      <div class="card">
        <img :src="card.imageUrl" :alt="card.name" class="card-image" />
        <div class="card-details">
          <h3 class="card-name">{{ card.name }}</h3>
          <p class="card-description">{{ card.description }}</p>
          <div class="card-info">
            <span class="card-rarity" :class="rarityClass">{{ card.rarity }}</span>
            <span class="card-type">{{ card.type }}</span>
          </div>
        </div>
      </div>
    </div>
  </template>
   
  <script>
  export default {
    name: 'Card',
    props: {
      card: {
        type: Object,
        required: true,
        validator(value) {
          // Validation moins stricte pour être plus flexible
          return value && 
                 typeof value === 'object' && 
                 'name' in value && 
                 'rarity' in value && 
                 'type' in value;
        }
      }
    },
    computed: {
      rarityClass() {
        // Cette méthode retourne une classe CSS en fonction de la rareté de la carte
        const rarityClasses = {
          'Common': 'common',
          'Uncommon': 'uncommon',
          'Rare': 'rare',
          'Epic': 'epic',
          'Legendary': 'legendary'
        };
        return rarityClasses[this.card.rarity] || 'common'; // Valeur par défaut si non trouvé
      }
    }
  };
  </script>
   
  <style scoped>
  .card-container {
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 10px;
  }
   
  .card {
    width: 100%;
    border: 1px solid #ddd;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    background-color: #fff;
    transition: transform 0.3s ease;
  }
   
  .card:hover {
    transform: scale(1.05);
  }
   
  .card-image {
    width: 100%;
    height: 200px;
    object-fit: cover;
    background-color: #f5f5f5;
  }
   
  .card-details {
    padding: 15px;
  }
   
  .card-name {
    font-size: 1.25em;
    font-weight: bold;
    margin: 0;
    color: #333;
  }
   
  .card-description {
    font-size: 1em;
    color: #555;
    margin: 10px 0;
    min-height: 60px;
  }
   
  .card-info {
    display: flex;
    justify-content: space-between;
    font-size: 0.9em;
  }
   
  .card-rarity {
    font-weight: bold;
    text-transform: uppercase;
  }
   
  .card-type {
    color: #777;
  }
   
  .common {
    color: #95a5a6;
  }
  
  .uncommon {
    color: #27ae60;
  }
   
  .rare {
    color: #3498db;
  }
  
  .epic {
    color: #9b59b6;
  }
   
  .legendary {
    color: #e74c3c;
  }
  </style>