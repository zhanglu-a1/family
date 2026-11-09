<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'

type Mode = 'login' | 'register'

const mode = ref<Mode>('login')
const loading = ref(false)
const formRef = ref<FormInstance>()
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const form = reactive({
  username: '',
  password: '',
  nickname: ''
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]{3,20}$/, message: '3-20 位字母、数字或下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 64, message: '密码长度 6-64 位', trigger: 'blur' }
  ]
}

function switchMode(next: Mode) {
  mode.value = next
  formRef.value?.clearValidate()
}

async function submit() {
  if (!formRef.value) {
    return
  }
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  loading.value = true
  try {
    if (mode.value === 'login') {
      await userStore.login({ username: form.username, password: form.password })
      ElMessage.success('欢迎回家')
    } else {
      await userStore.register({
        username: form.username,
        password: form.password,
        nickname: form.nickname || undefined
      })
      ElMessage.success('账号已创建，欢迎加入')
    }
    const redirect = route.query.redirect as string | undefined
    await router.push(redirect || '/')
  } catch {
    // 错误提示已由请求拦截器统一处理
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <section class="intro">
      <div class="intro-inner">
        <div class="seal fm-serif">家</div>
        <h1 class="fm-serif">把家里的故事<br />一件一件留下来</h1>
        <p class="intro-text">
          照片会褪色，记忆会模糊。<br />
          把长辈的口述、老照片和那些重要的小事，收进属于你们家的回忆录。
        </p>
        <div class="intro-quote fm-serif">「年年岁岁花相似，岁岁年年人不同。」</div>
      </div>
    </section>

    <section class="form-panel">
      <div class="form-card">
        <div class="tabs">
          <button :class="{ active: mode === 'login' }" type="button" @click="switchMode('login')">登录</button>
          <button :class="{ active: mode === 'register' }" type="button" @click="switchMode('register')">
            注册
          </button>
        </div>

        <h2 class="fm-serif form-title">{{ mode === 'login' ? '欢迎回来' : '创建账号' }}</h2>
        <p class="fm-muted form-sub">
          {{ mode === 'login' ? '继续记录你们家的故事' : '注册后即可开始记录，后续可创建或加入家庭空间' }}
        </p>

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="submit">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="字母、数字或下划线" size="large" />
          </el-form-item>
          <el-form-item v-if="mode === 'register'" label="昵称（可选）">
            <el-input v-model="form.nickname" placeholder="家人怎么称呼你" size="large" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              type="password"
              show-password
              placeholder="至少 6 位"
              size="large"
              @keyup.enter="submit"
            />
          </el-form-item>
          <el-button class="submit" type="primary" size="large" :loading="loading" @click="submit">
            {{ mode === 'login' ? '登录' : '注册并登录' }}
          </el-button>
        </el-form>

        <p class="privacy fm-muted">家庭数据仅空间成员可见，不会被搜索引擎收录。</p>
      </div>
    </section>
  </div>
</template>

<style scoped>
.login-page {
  display: grid;
  grid-template-columns: 1.1fr 1fr;
  min-height: 100vh;
}

.intro {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 64px;
  background:
    radial-gradient(circle at 20% 20%, rgba(244, 199, 147, 0.18), transparent 45%),
    linear-gradient(145deg, #2f251f 0%, #4a382c 60%, #5b4432 100%);
  color: #f4eadf;
}

.intro-inner {
  max-width: 460px;
}

.seal {
  width: 56px;
  height: 56px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(244, 199, 147, 0.6);
  border-radius: 14px;
  color: #f4c793;
  font-size: 28px;
  margin-bottom: 40px;
}

h1 {
  font-size: 38px;
  line-height: 1.45;
  letter-spacing: 2px;
  margin: 0 0 24px;
  font-weight: 600;
}

.intro-text {
  color: #d3c4b4;
  line-height: 1.9;
  margin: 0 0 48px;
}

.intro-quote {
  color: #a8947f;
  font-size: 15px;
  letter-spacing: 1px;
}

.form-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 40px;
  background: var(--fm-bg);
}

.form-card {
  width: 100%;
  max-width: 380px;
}

.tabs {
  display: inline-flex;
  padding: 4px;
  border-radius: 10px;
  background: #efe7db;
  margin-bottom: 36px;
}

.tabs button {
  border: none;
  background: transparent;
  padding: 8px 22px;
  border-radius: 8px;
  font-size: 14px;
  color: var(--fm-ink-soft);
  cursor: pointer;
}

.tabs button.active {
  background: var(--fm-panel);
  color: var(--fm-ink);
  box-shadow: 0 2px 6px rgba(63, 51, 43, 0.08);
}

.form-title {
  font-size: 26px;
  margin: 0 0 8px;
  letter-spacing: 1px;
}

.form-sub {
  font-size: 13px;
  margin: 0 0 28px;
  line-height: 1.7;
}

.submit {
  width: 100%;
  margin-top: 8px;
  background: var(--fm-accent);
  border-color: var(--fm-accent);
}

.privacy {
  margin-top: 24px;
  font-size: 12px;
  text-align: center;
}

@media (max-width: 900px) {
  .login-page {
    grid-template-columns: 1fr;
  }

  .intro {
    padding: 48px 32px;
  }

  h1 {
    font-size: 28px;
  }

  .form-panel {
    padding: 40px 24px;
  }
}
</style>
