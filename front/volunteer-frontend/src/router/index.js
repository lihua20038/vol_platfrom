import {
  createRouter,
  createWebHistory
} from 'vue-router'
import {
  useTokenStore
} from '@/store/token'

import UserLayout from '@/components/user/UserLayout.vue'
import LoginView from '../views/Login.vue'
import UserLoginView from '../views/user/UserLoginView.vue'
import UserRegisterView from '../views/user/UserRegisterView.vue'
import PersonalCenterView from '../views/user/PersonalCenterView.vue'
import ActivityListView from '../views/user/ActivityListView.vue'
import ActivityDetailView from '../views/user/ActivityDetailView.vue'
import ActivityRecordView from '../views/user/ActivityRecordView.vue'

import OrgLayout from '@/components/org/OrgLayout.vue'
import OrgLogin from '@/views/org/OrgLogin.vue'
import OrgRegister from '@/views/org/OrgRegister.vue'
import OrgInfo from '@/views/org/OrgInfo.vue'
import ActivityManagement from '@/views/org/ActivityManagement.vue'
import RecruitBrief from '@/views/org/RecruitBrief.vue';
import RecruitDetail from '@/views/org/RecruitDetail.vue';

const routes = [{
    path: '/',
    redirect: '/login'
  },
  // 统一登录入口
  {
    path: '/login',
    name: 'Login',
    component: LoginView,
    meta: {
      showHeader: false,
      showFooter: true
    }
  },
  // 学生用户子系统
  {
    path: '/user',
    component: UserLayout,
    meta: {
      requiresAuth: true,
      showFooter: true,
      showHeader: true,
      role: 'user'
    },
    children: [{
        path: 'login',
        name: 'UserLogin',
        component: UserLoginView,
        meta: {
          requiresAuth: false,
          showHeader: false,
        }
      },
      {
        path: 'register',
        name: 'UserRegister',
        component: UserRegisterView,
        meta: {
          requiresAuth: false,
          showHeader: false,
        }
      },
      {
        path: 'personal-center',
        name: 'PersonalCenter',
        component: PersonalCenterView,
      },
      {
        path: 'activity-list',
        name: 'ActivityList',
        component: ActivityListView,
      },
      {
        path: 'activity-detail/:activityId',
        name: 'ActivityDetail',
        component: ActivityDetailView,
        props: true,
      },
      {
        path: 'activity-record',
        name: 'ActivityRecord',
        component: ActivityRecordView,
      }
    ]
  },
  // 组织子系统
  {
    path: '/org',
    component: OrgLayout,
    meta: {
      requiresAuth: true,
      showFooter: true,
      showHeader: true,
      role: 'org'
    },
    children: [{
        path: 'login',
        name: 'OrgLogin',
        component: OrgLogin,
        meta: {
          requiresAuth: false,
          showHeader: false
        }
      },
      {
        path: 'register',
        name: 'OrgRegister',
        component: OrgRegister,
        meta: {
          requiresAuth: false,
          showHeader: false,
        }
      },
      {
        path: 'orgInfo',
        name: 'OrgInfo',
        component: OrgInfo,
      },
      {
        path: 'activityManagement',
        name: 'ActivityManagement',
        component: ActivityManagement,
      },
      {
        path: 'recruitBrief',
        name: 'RecruitBrief',
        component: RecruitBrief,
      },
      {
        path: 'recruitDetail/:activityId',
        name: 'RecruitDetail',
        props: true,
        component: RecruitDetail,
        meta: {
          showHeader: false,
        }
      },
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: routes
})

// 路由守卫，检查登录状态
router.beforeEach((to, from, next) => {
  const publicPages = [
    '/login',
    '/user/login',
    '/user/register',
    '/org/login',
    '/org/register'
  ]
  const tokenStore = useTokenStore()
  const isAuthenticated = !!tokenStore.token
  const requiresAuth = !publicPages.includes(to.path)
  const loginPages = ['/login', '/user/login', '/org/login', ]

  // 已登录时访问登录相关页面
  if (isAuthenticated && loginPages.includes(to.path)) {
    // 根据角色重定向
    const redirectPath = tokenStore.role === 'org' ?
      '/org/orgInfo' :
      '/user/personal-center'
    return next(redirectPath)
  }

  // 需要认证且未登录
  if (requiresAuth && !isAuthenticated) {
    return next('/login')
  }

  // 权限验证通过
  next()
})

export default router