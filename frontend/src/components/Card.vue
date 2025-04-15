<template>
  <div class="card-container">
    <div class="card" :class="[rarityClass]">
      <div class="card-badge" v-if="card.price">{{ card.price }} €</div>
      <div class="card-header">
        <img :src="card.imageUrl" :alt="card.name" class="card-image" />
        <div class="card-overlay">
          <span class="card-type">{{ card.type }}</span>
        </div>
      </div>
      <div class="card-details">
        <h3 class="card-name">{{ card.name }}</h3>
        <p class="card-description">{{ card.description }}</p>
        <div class="card-footer">
          <span class="card-rarity" :class="rarityClass">{{ card.rarity }}</span>
          <button class="card-button" v-if="!card.userId && card.price">Acheter</button>
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
      const rarityClasses = {
        'Common': 'common',
        'Uncommon': 'uncommon',
        'Rare': 'rare',
        'Epic': 'epic',
        'Legendary': 'legendary'
      };
      return rarityClasses[this.card.rarity] || 'common';
    }
  }
};
</script>