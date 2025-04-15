import { createRouter, createWebHistory } from 'vue-router'
import AuthStore from '../store/AuthStore'

const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('../views/HomeView.vue')
  },
  {
    path: '/buy',
    name: 'buy',
    component: () => import('../views/BuyView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/sell',
    name: 'sell',
    component: () => import('../views/SellView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/play',
    name: 'play',
    component: () => import('../views/PlayView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/inventory',
    name: 'inventory',
    component: () => import('../views/InventoryView.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/terms',
    name: 'terms',
    component: () => import('../views/TermsView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  if (to.meta.requiresAuth) {
    const isAuthenticated = await AuthStore.checkAuthStatus();
    if (!isAuthenticated) {
      AuthStore.showNotification('Veuillez vous connecter', 'error');
      next({ name: 'home' });
    } else {
      next();
    }
  } else {
    next();
  }
});


export default router