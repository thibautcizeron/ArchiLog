// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/buy',
    name: 'buy',
    component: () => import(/* webpackChunkName: "buy" */ '../views/BuyView.vue')
  },
  {
    path: '/sell',
    name: 'sell',
    component: () => import(/* webpackChunkName: "sell" */ '../views/SellView.vue')
  },
  {
    path: '/play',
    name: 'play',
    component: () => import(/* webpackChunkName: "play" */ '../views/PlayView.vue')
  },
  {
    path: '/inventory',
    name: 'inventory',
    component: () => import(/* webpackChunkName: "inventory" */ '../views/InventoryView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router