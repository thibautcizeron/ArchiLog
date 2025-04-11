<!-- src/views/HomeView.vue -->
<template>
  <div class="home-container">
    <div class="hero-section">
      <h1 class="title">Bienvenue sur notre plateforme</h1>
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

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: calc(100vh - 60px);
  padding: 3rem 2rem;
  background-color: #f9f9f9;
}

.hero-section {
  max-width: 1200px;
  width: 100%;
  text-align: center;
}

.title {
  color: #7966f6;
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.subtitle {
  color: #666;
  font-size: 1.2rem;
  margin-bottom: 3rem;
}

.play-button-container {
  display: flex;
  justify-content: center;
  margin-bottom: 4rem;
}

.play-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  background-color: #7966f6;
  color: white;
  border: none;
  border-radius: 50px;
  padding: 1.2rem 3rem;
  font-size: 1.5rem;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(121, 102, 246, 0.4);
}

.play-button:hover {
  background-color: #6452d9;
  transform: translateY(-5px);
  box-shadow: 0 6px 20px rgba(121, 102, 246, 0.5);
}

.button-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}

.features-section {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 2rem;
  margin-top: 2rem;
}

.feature {
  flex: 1;
  min-width: 250px;
  max-width: 350px;
  background-color: white;
  border-radius: 10px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-align: left;
}

.feature-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 60px;
  height: 60px;
  background-color: #f5f5ff;
  border-radius: 50%;
  margin-bottom: 1.5rem;
}

.feature h3 {
  color: #333;
  font-size: 1.3rem;
  margin-bottom: 1rem;
}

.feature p {
  color: #666;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .title {
    font-size: 2rem;
  }
  
  .subtitle {
    font-size: 1rem;
  }
  
  .play-button {
    padding: 1rem 2.5rem;
    font-size: 1.2rem;
  }
  
  .features-section {
    flex-direction: column;
    align-items: center;
  }
  
  .feature {
    width: 100%;
    max-width: 100%;
  }
}
</style>