<!-- src/components/Notification.vue -->
<template>
    <div v-if="notification.show" class="notification" :class="notification.type">
      <div class="notification-content">
        <span>{{ notification.message }}</span>
        <button class="notification-close" @click="hideNotification">×</button>
      </div>
    </div>
  </template>
  
  <script>
  import AuthStore from '../store/AuthStore';
  
  export default {
    computed: {
      notification() {
        return AuthStore.state.notification;
      }
    },
    methods: {
      hideNotification() {
        AuthStore.hideNotification();
      }
    }
  }
  </script>
  