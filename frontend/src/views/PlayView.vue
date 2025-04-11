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
  
  // Mise à jour de PlayView.vue
<script>
import gameService from '../services/game.service';
import authStore from '../store/auth.store';

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
      ],
      rooms: [],
      loading: false,
      error: null
    }
  },
  created() {
    this.fetchRooms();
  },
  methods: {
    async fetchRooms() {
      this.loading = true;
      try {
        // Assuming the room service will have an endpoint to list rooms
        const response = await fetch('http://localhost:8086/api/rooms', {
          headers: this.getAuthHeaders()
        });
        if (response.ok) {
          this.rooms = await response.json();
        }
      } catch (error) {
        console.error('Error fetching rooms:', error);
      } finally {
        this.loading = false;
      }
    },
    getAuthHeaders() {
      const headers = {
        'Content-Type': 'application/json'
      };
      if (authStore.isAuthenticated()) {
        headers['Authorization'] = `Bearer ${authStore.state.user.token}`;
      }
      return headers;
    },
    selectCategory(categoryId) {
      console.log('Catégorie sélectionnée :', categoryId);
      // Filter games by category
    },
    async startGame(gameId) {
      if (!authStore.isAuthenticated()) {
        this.error = 'Please login to play games';
        return;
      }
      
      this.loading = true;
      try {
        // Create a new room for the game
        const roomData = {
          gameId: gameId,
          creatorId: authStore.state.user.userId,
          maxPlayers: 4,
          status: 'waiting'
        };
        const room = await gameService.createRoom(roomData);
        // Navigate to the room
        this.$router.push(`/room/${room.id}`);
      } catch (error) {
        this.error = 'Failed to start game. Please try again.';
        console.error('Error starting game:', error);
      } finally {
        this.loading = false;
      }
    },
    async joinRoom(roomId) {
      if (!authStore.isAuthenticated()) {
        this.error = 'Please login to join rooms';
        return;
      }
      
      this.loading = true;
      try {
        await gameService.joinRoom(roomId);
        // Navigate to the room
        this.$router.push(`/room/${roomId}`);
      } catch (error) {
        this.error = 'Failed to join room. Please try again.';
        console.error('Error joining room:', error);
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
  
  .game-container {
    background-color: white;
    padding: 2rem;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 900px;
  }
  
  .game-header {
    margin-bottom: 2rem;
    text-align: center;
  }
  
  h3 {
    margin-bottom: 0.5rem;
    color: #333;
    font-size: 1.8rem;
  }
  
  .subtitle {
    color: #777;
    font-size: 1.1rem;
  }
  
  .category-tiles {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(130px, 1fr));
    gap: 1.5rem;
    margin-bottom: 3rem;
  }
  
  .category-tile {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.5rem;
    cursor: pointer;
    transition: transform 0.2s ease;
  }
  
  .category-tile:hover {
    transform: translateY(-5px);
  }
  
  .category-icon {
    width: 70px;
    height: 70px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
  }
  
  .bg-purple {
    background-color: #7966f6;
  }
  
  .bg-blue {
    background-color: #4a90e2;
  }
  
  .bg-green {
    background-color: #2ecc71;
  }
  
  .bg-orange {
    background-color: #e67e22;
  }
  
  .category-name {
    font-weight: 600;
    color: #333;
  }
  
  h4 {
    margin-bottom: 1.5rem;
    color: #333;
    font-size: 1.5rem;
  }
  
  .featured-games {
    margin-bottom: 3rem;
  }
  
  .game-cards {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 1.5rem;
  }
  
  .game-card {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 1rem;
    border-radius: 10px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    border: 1px solid #eee;
    cursor: pointer;
    transition: box-shadow 0.3s ease;
  }
  
  .game-card:hover {
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .game-image {
    width: 60px;
    height: 60px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-weight: bold;
    font-size: 1.5rem;
  }
  
  .game-info {
    flex: 1;
  }
  
  h5 {
    margin-bottom: 0.3rem;
    color: #333;
    font-size: 1.1rem;
  }
  
  .game-meta {
    display: flex;
    gap: 1rem;
    font-size: 0.9rem;
    color: #777;
  }
  
  .game-rating {
    display: flex;
    align-items: center;
    gap: 0.3rem;
  }
  
  .play-now-button {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    border: none;
    background-color: #f5f5ff;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }
  
  .play-now-button:hover {
    background-color: #e9e7ff;
  }
  
  .community-section h4 {
    margin-bottom: 1.5rem;
  }
  
  .community-cards {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
    gap: 1.5rem;
  }
  
  .community-card {
    padding: 1.5rem;
    border-radius: 10px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    border: 1px solid #eee;
    background-color: #f9f9f9;
  }
  
  .community-card h5 {
    margin-bottom: 0.8rem;
  }
  
  .community-card p {
    margin-bottom: 1.5rem;
    color: #666;
    font-size: 0.95rem;
  }
  
  .community-button {
    padding: 0.6rem 1.2rem;
    background-color: #7966f6;
    color: white;
    border: none;
    border-radius: 5px;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.3s ease;
  }
  
  .community-button:hover {
    background-color: #6452d9;
  }
  
  /* Responsive design */
  @media (max-width: 768px) {
    .category-tiles {
      grid-template-columns: repeat(2, 1fr);
    }
    
    .game-cards, .community-cards {
      grid-template-columns: 1fr;
    }
  }
  </style>