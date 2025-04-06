<template>
    <div class="layout-container">
      <Header :show-header="$route.meta.showHeader" />
      
      <main class="content-area">
        <router-view></router-view>
      </main>
  
      <Footer v-if="$route.meta.showFooter" />
    </div>
  </template>
  
  <script setup>
  import Header from '@/components/org/OrgHeader.vue'
  import Footer from '@/components/common/Footer.vue';
  import { useRoute } from 'vue-router'
  
  const route = useRoute()
  </script>
  
  <style scoped>
  .layout-container {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
  }
  
  .content-area {
    flex: 1;
    padding: 24px;
    background: #f8f9fa;
  }
  </style>