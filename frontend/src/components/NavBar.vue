<!-- NavBar.vue -->
<template>
    <nav class="navbar">
      <div class="container">
        <h1 class="navbar-title">{{ title }}</h1>
        <div class="navbar-buttons">
          <button class="nav-button" @click="navigate('buy')">
            <span class="icon">🛒</span> 
            <span class="text">Buy</span>
          </button>
          <button class="nav-button" @click="navigate('sell')">
            <span class="icon">💰</span> 
            <span class="text">Sell</span>
          </button>
          <button class="nav-button" @click="navigate('play')">
            <span class="icon">▶️</span> 
            <span class="text">Play</span>
          </button>
        </div>
      </div>
    </nav>
  </template>
  
  <script>
  export default {
    name: 'NavBar',
    props: {
      title: {
        type: String,
        default: 'Mon Site Web'
      }
    },
    methods: {
      navigate(route) {
        // Cette méthode peut être utilisée pour la navigation
        // Si vous utilisez Vue Router:
        // this.$router.push(`/${route}`);
        console.log(`Navigation vers ${route}`);
      }
    }
  }
  </script>
  
  <style scoped>
  .navbar {
    width: 100%;
    background-color: white;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    padding: 1rem 0;
  }
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 1rem;
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .navbar-title {
    color: #7966f6;
    font-size: 2rem;
    margin-bottom: 1.5rem;
  }
  
  .navbar-buttons {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 1.5rem;
  }
  
  .nav-button {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    padding: 0.8rem 2rem;
    border: 2px solid #333;
    border-radius: 50px;
    background-color: white;
    font-size: 1.2rem;
    font-weight: bold;
    cursor: pointer;
    transition: all 0.3s ease;
    min-width: 150px;
  }
  
  .nav-button:hover {
    background-color: #f5f5ff;
    border-color: #7966f6;
  }
  
  .nav-button .icon {
    font-size: 1.2rem;
    color: #7966f6;
  }
  
  .nav-button .text {
    color: #333;
  }
  
  /* Responsive design */
  @media (min-width: 768px) {
    .container {
      flex-direction: row;
      justify-content: space-between;
    }
    
    .navbar-title {
      margin-bottom: 0;
    }
  }
  </style>