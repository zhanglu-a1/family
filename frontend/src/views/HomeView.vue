<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Clock, Notebook, Picture } from '@element-plus/icons-vue'
import { fetchHealth, type HealthInfo } from '@/api/health'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const health = ref<HealthInfo | null>(null)
const healthError = ref(false)

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 11) return '早上好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const displayName = computed(() => userStore.user?.nickname || userStore.user?.username || '家人')

onMounted(async () => {
  try {
    health.value = await fetchHealth()
  } catch {
    healthError.value = true
  }
})
</script>

<template>
  <div class="home">
    <section class="hero fm-card">
      <div>
        <p class="hero-kicker">{{ greeting }}，{{ displayName }}</p>
        <h1 class="fm-serif">今天，想记下哪一段回忆？</h1>
        <p class="fm-muted hero-text">
          先写一条回忆，或者从时间轴回看家里的旧时光。等空间建好后，还可以邀请家人一起补充。
        </p>
      </div>
      <div class="health">
        <span class="health-label fm-muted">服务状态</span>
        <el-tag v-if="health && !healthError" type="success" effect="plain" round>后端已连通</el-tag>
        <el-tag v-else-if="healthError" type="danger" effect="plain" round>后端未连通</el-tag>
        <el-tag v-else type="info" effect="plain" round>检测中</el-tag>
        <span v-if="health" class="health-time fm-muted">{{ health.time }}</span>
      </div>
    </section>

    <section class="actions">
      <router-link to="/memoirs" class="action fm-card">
        <el-icon class="action-icon"><Notebook /></el-icon>
        <div>
          <h3>写一条回忆</h3>
          <p class="fm-muted">文字、照片、时间、地点与人物</p>
        </div>
      </router-link>
      <router-link to="/timeline" class="action fm-card">
        <el-icon class="action-icon"><Clock /></el-icon>
        <div>
          <h3>打开时间轴</h3>
          <p class="fm-muted">按年月回看家庭的脉络</p>
        </div>
      </router-link>
      <router-link to="/media" class="action fm-card">
        <el-icon class="action-icon"><Picture /></el-icon>
        <div>
          <h3>整理相册</h3>
          <p class="fm-muted">上传照片与视频到阿里云 OSS</p>
        </div>
      </router-link>
    </section>

    <section class="status-grid">
      <div class="fm-card">
        <div class="card-head">
          <h3 class="fm-serif">当前进度</h3>
          <el-tag size="small" effect="plain" type="warning">M0 已完成</el-tag>
        </div>
        <ul class="check-list">
          <li>前后端工程骨架与统一响应</li>
          <li>注册 / 登录 / 刷新令牌 / 当前用户</li>
          <li>阿里云 OSS 上传与签名地址</li>
          <li>MySQL 表结构与 Docker 本地环境</li>
        </ul>
      </div>

      <div class="fm-card">
        <div class="card-head">
          <h3 class="fm-serif">接下来</h3>
          <el-tag size="small" effect="plain">M1 - M5</el-tag>
        </div>
        <ul class="next-list">
          <li><span>M1</span>家庭空间、成员与角色权限</li>
          <li><span>M2</span>回忆录 CRUD 与富文本编辑</li>
          <li><span>M3</span>时间轴、标签、人物与检索</li>
          <li><span>M4</span>相册视图与 PDF 导出</li>
          <li><span>M5</span>审计、限流、适老化与性能</li>
        </ul>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
  padding: 32px;
  background:
    radial-gradient(circle at 90% 10%, rgba(168, 104, 63, 0.08), transparent 40%),
    var(--fm-panel);
}

.hero-kicker {
  margin: 0 0 10px;
  font-size: 13px;
  color: var(--fm-accent);
  letter-spacing: 1px;
}

.hero h1 {
  margin: 0 0 12px;
  font-size: 28px;
  letter-spacing: 2px;
}

.hero-text {
  margin: 0;
  max-width: 560px;
  line-height: 1.8;
  font-size: 14px;
}

.health {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
  flex-shrink: 0;
}

.health-label {
  font-size: 12px;
}

.health-time {
  font-size: 12px;
}

.actions {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.action {
  display: flex;
  align-items: center;
  gap: 14px;
  color: var(--fm-ink);
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.action:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(63, 51, 43, 0.08);
}

.action-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: var(--fm-accent-soft);
  color: var(--fm-accent);
  font-size: 22px;
  flex-shrink: 0;
}

.action h3 {
  margin: 0 0 4px;
  font-size: 15px;
}

.action p {
  margin: 0;
  font-size: 12px;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
}

.card-head h3 {
  margin: 0;
  font-size: 16px;
  letter-spacing: 1px;
}

.check-list,
.next-list {
  margin: 0;
  padding-left: 18px;
  line-height: 2;
  font-size: 13px;
  color: var(--fm-ink-soft);
}

.next-list {
  list-style: none;
  padding-left: 0;
}

.next-list span {
  display: inline-block;
  width: 34px;
  color: var(--fm-accent);
  font-weight: 600;
}

@media (max-width: 900px) {
  .actions,
  .status-grid {
    grid-template-columns: 1fr;
  }

  .hero {
    flex-direction: column;
  }

  .health {
    align-items: flex-start;
  }
}
</style>
