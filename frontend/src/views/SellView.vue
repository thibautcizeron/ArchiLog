<!-- src/views/SellView.vue -->
<template>
    <div class="page-container">
      <div class="navbar">
        <button class="back-button" @click="$router.push('/')">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
          Retour
        </button>
        <h2>Page de vente</h2>
      </div>
  
      <div class="content">
        <div class="form-container">
          <h3>Que souhaitez-vous vendre ?</h3>
          
          <div class="form-group">
            <label for="productName">Nom du produit</label>
            <input type="text" id="productName" v-model="productName" placeholder="Entrez le nom de votre produit">
          </div>
          
          <div class="form-group">
            <label for="category">Catégorie</label>
            <select id="category" v-model="category">
              <option value="">Sélectionnez une catégorie</option>
              <option value="electronics">Électronique</option>
              <option value="clothing">Vêtements</option>
              <option value="home">Maison</option>
              <option value="sports">Sports & Loisirs</option>
              <option value="other">Autre</option>
            </select>
          </div>
          
          <div class="form-group">
            <label for="description">Description</label>
            <textarea id="description" v-model="description" rows="4" placeholder="Décrivez votre produit (état, caractéristiques, etc.)"></textarea>
          </div>
          
          <div class="form-group">
            <label for="price">Prix (€)</label>
            <input type="number" id="price" v-model="price" min="0" step="0.01">
          </div>
          
          <div class="form-group">
            <label for="photos">Photos</label>
            <div class="photo-upload">
              <button class="upload-button">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h7"></path>
                  <path d="M16 5h6v6"></path>
                  <path d="M8 12l3 3 9-9"></path>
                </svg>
                Ajouter des photos
              </button>
              <span class="upload-help">Jusqu'à 5 photos (JPEG ou PNG)</span>
            </div>
          </div>
          
          <div class="button-group">
            <button class="action-button draft-button">Enregistrer comme brouillon</button>
            <button class="action-button">Publier l'annonce</button>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'SellView',
    data() {
      return {
        productName: '',
        category: '',
        description: '',
        price: null,
        photos: []
      }
    }
  }
  </script>

<style src="../assets/styles/SellView.css" scoped></style>