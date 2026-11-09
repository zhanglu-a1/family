<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const title = computed(() => (route.meta.title as string | undefined) ?? '功能')
const description = computed(() => (route.meta.description as string | undefined) ?? '')
</script>

<template>
  <div class="fm-card placeholder">
    <div class="head">
      <h2 class="fm-serif">{{ title }}</h2>
      <el-tag type="warning" effect="plain" round>规划中</el-tag>
    </div>
    <p class="fm-muted desc">{{ description }}</p>
    <el-empty description="该模块将在后续里程碑开放" :image-size="96" />
  </div>
</template>

<style scoped>
.placeholder {
  min-height: calc(100vh - 112px);
}

.head {
  display: flex;
  align-items: center;
  gap: 12px;
}

.head h2 {
  margin: 0;
  font-size: 22px;
  letter-spacing: 2px;
}

.desc {
  margin: 10px 0 0;
  font-size: 13px;
  line-height: 1.8;
}
</style>
