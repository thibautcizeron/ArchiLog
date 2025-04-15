<!-- src/App.vue -->
<template>
  <div id="app">
    <Navbar />
    <main>
      <router-view/>
    </main>
    <Notification />
  </div>
</template>

<script>
import Navbar from './components/NavBar.vue';
import Notification from './components/Notification.vue';
import AuthStore from './store/AuthStore';

export default {
  components: {
    Navbar,
    Notification
  },
  
  created() {
    this.checkAuthStatus();
  },
  
  methods: {
    async checkAuthStatus() {
      await AuthStore.checkAuthStatus();
    }
  }
}
</script>