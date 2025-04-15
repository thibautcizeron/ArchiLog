<!-- src/views/HomeView.vue -->
<template>
  <div class="home-container">
    <div class="hero-section">
      <h1 class="title">Play with Riboudou</h1>
      <p class="subtitle">Achetez, vendez et découvrez de nouvelles expériences</p>
      
      <div class="play-button-container">
        <button class="play-button" @click="navigate('play')">
          <div class="button-icon">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="white" stroke-width="2">
              <circle cx="12" cy="12" r="10"></circle>
              <polygon points="10 8 16 12 10 16 10 8" fill="white"></polygon>
            </svg>
          </div>
          <span>PLAY</span>
        </button>
      </div>
      
      <div class="features-section">
        <div class="feature">
          <div class="feature-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#7966f6" stroke-width="2">
              <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path>
              <line x1="3" y1="6" x2="21" y2="6"></line>
              <path d="M16 10a4 4 0 0 1-8 0"></path>
            </svg>
          </div>
          <h3>Achetez en toute confiance</h3>
          <p>Découvrez des produits vérifiés avec garantie de satisfaction</p>
        </div>
        
        <div class="feature">
          <div class="feature-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#7966f6" stroke-width="2">
              <line x1="12" y1="1" x2="12" y2="23"></line>
              <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
            </svg>
          </div>
          <h3>Vendez facilement</h3>
          <p>Publiez vos annonces en quelques clics et touchez un large public</p>
        </div>
        
        <div class="feature">
          <div class="feature-icon">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#7966f6" stroke-width="2">
              <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"></polygon>
            </svg>
          </div>
          <h3>Expérience interactive</h3>
          <p>Participez à des jeux et événements pour gagner des avantages exclusifs</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'HomeView',
  methods: {
    navigate(route) {
      this.$router.push(`/${route}`);
    }
  }
}
</script>

<style src="../assets/styles/HomeView.css" scoped></style>