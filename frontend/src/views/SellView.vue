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
  
  .form-container {
    background-color: white;
    padding: 2rem;
    border-radius: 10px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 600px;
  }
  
  h3 {
    margin-bottom: 1.5rem;
    color: #333;
  }
  
  .form-group {
    margin-bottom: 1.5rem;
  }
  
  label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: bold;
    color: #555;
  }
  
  input, select, textarea {
    width: 100%;
    padding: 0.8rem;
    border: 1px solid #ddd;
    border-radius: 5px;
    font-size: 1rem;
  }
  
  textarea {
    resize: vertical;
    min-height: 100px;
  }
  
  .photo-upload {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .upload-button {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    padding: 0.8rem;
    border: 1px dashed #7966f6;
    border-radius: 5px;
    background-color: #f5f5ff;
    color: #7966f6;
    cursor: pointer;
    width: 100%;
  }
  
  .upload-help {
    font-size: 0.85rem;
    color: #777;
  }
  
  .button-group {
    display: flex;
    gap: 1rem;
    flex-wrap: wrap;
  }
  
  .action-button {
    background-color: #7966f6;
    color: white;
    border: none;
    padding: 1rem 2rem;
    font-size: 1rem;
    font-weight: bold;
    border-radius: 5px;
    cursor: pointer;
    flex: 1;
    min-width: 180px;
    transition: background-color 0.3s ease;
  }
  
  .action-button:hover {
    background-color: #6452d9;
  }
  
  .draft-button {
    background-color: white;
    color: #7966f6;
    border: 1px solid #7966f6;
  }
  
  .draft-button:hover {
    background-color: #f5f5ff;
  }
  
  @media (max-width: 500px) {
    .button-group {
      flex-direction: column;
    }
  }
  </style>