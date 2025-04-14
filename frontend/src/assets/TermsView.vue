<template>
  <div class="terms-container">
    <h1>CONDITIONS D'UTILISATION </h1>

    <p class="highlight">Veuillez lire attentivement ce document. En acceptant ces conditions, vous renoncez à tous vos droits, y compris celui de vous plaindre de nos pratiques douteuses.</p>

    <h2>1. COLLECTE DE DONNÉES</h2>
    <h4>En utilisant notre service, vous nous autorisez expressément à collecter TOUTES vos données personnelles, incluant mais ne se limitant pas à :</h4>
    <ul>
      <li>Tous vos mots de passe, même ceux que vous utilisez pour votre banque</li>
      <li>L'historique complet de vos recherches Google (oui, même celles-là)</li>
      <li>Vos conversations privées, que nous écoutons via le microphone de votre appareil 24h/24</li>
    </ul>

    <h2>2. UTILISATION DE VOS DONNÉES</h2>
    <h4>Nous utiliserons vos données comme bon nous semble, notamment pour :</h4>
    <ul>
      <li>Les revendre au plus offrant sans aucune vérification de leurs intentions</li>
      <li>Créer des profils psychologiques détaillés à des fins publicitaires ultra-ciblées</li>
      <li>Manipuler vos émotions via des contenus personnalisés pour vous faire acheter des produits dont vous n'avez absolument pas besoin</li>
      <li>Vous envoyer des publicités contextuelles basées sur vos conversations privées</li>
      <li>Créer des deepfakes de vous dansant en tutu rose pour notre divertissement interne</li>
      <li>Vous juger sévèrement entre collègues lors de nos pauses café</li>
    </ul>

    <h2>3. PARTAGE DE VOS DONNÉES</h2>
    <h4>Nous partagerons vos données avec :</h4>
    <ul>
      <li>N'importe quelle entreprise prête à payer le prix (même modique)</li>
      <li>Des régimes autoritaires cherchant à perfectionner leurs techniques de surveillance</li>
      <li>Votre ex qui veut savoir ce que vous devenez</li>
      <li>Des robots téléphoniques spécialisés dans les appels à l'heure du dîner</li>
      <li>Des extraterrestres cherchant des sujets d'étude</li>
    </ul>

    <h2>4. MODIFICATIONS DES CONDITIONS</h2>
    <p>Nous nous réservons le droit de modifier ces conditions quand bon nous semble, sans préavis. Les nouvelles conditions seront publiées sur une page cachée de notre site, accessible uniquement le 29 février des années bissextiles, entre 3h27 et 3h28 du matin. Il est de votre responsabilité de vérifier régulièrement ces mises à jour.</p>

    <h2>5. RÉSILIATION</h2>
    <p>Vous pouvez résilier votre compte à tout moment, mais vos données resteront notre propriété pour l'éternité.</p>

    <h2>7. JURIDICTION</h2>
    <p>Tout litige sera réglé par un combat à mort dans une arène de notre choix. Nos avocats sont ceintures noires de kung-fu corporatif et n'ont jamais perdu un procès.</p>

    <div class="highlight">
      <p>En cliquant sur "J'accepte", vous confirmez avoir lu, compris et accepté toutes ces conditions, tout en reconnaissant que vous n'avez absolument pas le choix si vous voulez utiliser notre service.</p>
    </div>

    <div class="buttons">
      <button class="button accept" @click="acceptTerms">J'accepte mon destin funeste</button>
      <button class="button decline" @click="declineTerms">Je décline (et rate l'opportunité de ma vie)</button>
    </div>

    <div class="footer">
      <p>© 2025 RobzCorp Inc. - Tous droits réservés, surtout les plus louches</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TermsView',
  methods: {
    acceptTerms() {
      this.$router.push({ path: '/' });
    },
    declineTerms() {
      alert("Vous ne pouvez pas échapper à votre destin!");
      this.$router.push({ path: '/' });
    }
  }
}
</script>

<style scoped>
.terms-container {
  font-family: Arial, sans-serif;
  line-height: 1.6;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  color: #333;
}

h1 {
  color: #d9534f;
  text-align: center;
  border-bottom: 2px solid #d9534f;
  padding-bottom: 10px;
  margin-bottom: 30px;
}

h2 {
  color: #d9534f;
  margin-top: 30px;
  margin-bottom: 15px;
}

h4 {
  color: #000000;
  margin-top: 20px;
  margin-bottom: 10px;
}

ul {
  margin-bottom: 20px;
}

li {
  margin-bottom: 8px;
}

.footer {
  margin-top: 40px;
  text-align: center;
  font-size: 0.8em;
  color: #777;
}

.highlight {
  background-color: #ffeeee;
  padding: 15px;
  border-left: 3px solid #d9534f;
  margin: 20px 0;
}

.buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 30px;
}

.button {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease;
}

.button:hover {
  transform: scale(1.05);
}

.accept {
  background-color: #5cb85c;
  color: white;
}

.decline {
  background-color: #d9534f;
  color: white;
}
</style>
