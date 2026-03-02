<template>
  <div class="main-layout">
    <Header />
    <main class="main-content">
      <router-view />
    </main>
    <Footer />
  </div>
</template>

<script setup>
import Header from '@/components/common/Header.vue'
import Footer from '@/components/common/Footer.vue'
</script>

<style lang="scss" scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
.main-content {
  flex: 1;
  padding: 20px 0;
}
</style>
