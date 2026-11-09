import { createRouter, createWebHistory } from 'vue-router'
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { public: true }
    },
    {
      path: '/',
      component: DefaultLayout,
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/HomeView.vue'),
          meta: { title: '首页' }
        },
        {
          path: 'timeline',
          name: 'timeline',
          component: () => import('@/views/PlaceholderView.vue'),
          meta: { title: '时间轴', description: '按年月回看家庭的故事脉络，支持按人物与年份筛选。' }
        },
        {
          path: 'memoirs',
          name: 'memoirs',
          component: () => import('@/views/PlaceholderView.vue'),
          meta: { title: '回忆录', description: '记录一条回忆：文字、照片、时间、地点与涉及的人。' }
        },
        {
          path: 'media',
          name: 'media',
          component: () => import('@/views/PlaceholderView.vue'),
          meta: { title: '相册', description: '按年月整理照片与视频，支持上传到阿里云 OSS。' }
        },
        {
          path: 'persons',
          name: 'persons',
          component: () => import('@/views/PlaceholderView.vue'),
          meta: { title: '人物档案', description: '为家里的每个人建立档案，串联与他/她有关的回忆。' }
        },
        {
          path: 'settings',
          name: 'settings',
          component: () => import('@/views/PlaceholderView.vue'),
          meta: { title: '空间设置', description: '空间信息、成员与角色、邀请码管理。' }
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/'
    }
  ],
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  if (!to.meta.public && !userStore.accessToken) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }
  if (to.name === 'login' && userStore.accessToken) {
    return { name: 'home' }
  }
  return true
})

export default router
