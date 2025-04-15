<!-- src/views/PlayView.vue -->
<template>
    <div class="page-container">
      <div class="navbar">
        <button class="back-button" @click="$router.push('/')">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          Retour
        </button>
        <h2>Page de jeu</h2>
      </div>
  
      <div class="content">
        <div class="game-container">
          <div class="game-header">
            <h3>Bienvenue dans notre espace de jeu</h3>
            <p class="subtitle">Sélectionnez une catégorie de jeu ou découvrez nos recommandations</p>
          </div>
          
          <div class="category-tiles">
            <div class="category-tile" v-for="(category, index) in categories" :key="index" @click="selectCategory(category.id)">
              <div class="category-icon" :class="category.colorClass">
                <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path :d="category.icon"></path>
                </svg>
              </div>
              <span class="category-name">{{ category.name }}</span>
            </div>
          </div>
  
          <div class="featured-games">
            <h4>Jeux à la une</h4>
            
            <div class="game-cards">
              <div class="game-card" v-for="(game, index) in featuredGames" :key="index" @click="startGame(game.id)">
                <div class="game-image" :style="{ backgroundColor: game.color }">
                  <span class="game-initials">{{ game.initials }}</span>
                </div>
                <div class="game-info">
                  <h5>{{ game.name }}</h5>
                  <div class="game-meta">
                    <span class="game-players">{{ game.players }}</span>
                    <span class="game-rating">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="#FFD700" stroke="#FFD700" stroke-width="1">
                        <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
                      </svg>
                      {{ game.rating }}
                    </span>
                  </div>
                </div>
                <button class="play-now-button">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="#7966f6" stroke="none">
                    <polygon points="5 3 19 12 5 21 5 3"></polygon>
                  </svg>
                </button>
              </div>
            </div>
          </div>
          
          <div class="community-section">
            <h4>Communauté</h4>
            <div class="community-cards">
              <div class="community-card">
                <h5>Tournois</h5>
                <p>Participez à nos tournois hebdomadaires et gagnez des prix!</p>
                <button class="community-button">Explorer</button>
              </div>
              <div class="community-card">
                <h5>Classements</h5>
                <p>Consultez les meilleurs joueurs et relevez le défi!</p>
                <button class="community-button">Voir le classement</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'PlayView',
    data() {
      return {
        categories: [
          { 
            id: 1, 
            name: 'Arcade', 
            icon: 'M17 3H7a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V5a2 2 0 0 0-2-2zM9 9h6M9 12h6M9 15h6', 
            colorClass: 'bg-purple' 
          },
          { 
            id: 2, 
            name: 'Stratégie', 
            icon: 'M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5', 
            colorClass: 'bg-blue' 
          },
          { 
            id: 3, 
            name: 'Sports', 
            icon: 'M12 22c-4.97 0-9-4.03-9-9s4.03-9 9-9 9 4.03 9 9-4.03 9-9 9zM15 9l-6 6M9 9l6 6', 
            colorClass: 'bg-green' 
          },
          { 
            id: 4, 
            name: 'Quiz', 
            icon: 'M8.5 14l-5-5L5 7.5 8.5 11 19 0.5l2 1.5-12.5 12zM21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h7', 
            colorClass: 'bg-orange' 
          }
        ],
        featuredGames: [
          { id: 101, name: 'Space Adventure', players: '1-4 joueurs', rating: 4.8, initials: 'SA', color: '#7966f6' },
          { id: 102, name: 'Chess Master', players: '2 joueurs', rating: 4.9, initials: 'CM', color: '#4a90e2' },
          { id: 103, name: 'Quiz Challenge', players: '1-8 joueurs', rating: 4.5, initials: 'QC', color: '#e74c3c' }
        ]
      }
    },
    methods: {
      selectCategory(categoryId) {
        console.log('Catégorie sélectionnée :', categoryId);
      },
      startGame(gameId) {
        console.log('Lancement du jeu :', gameId);
      }
    }
  }
  </script>

<style src="../assets/styles/PlayView.css" scoped></style>