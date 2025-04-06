<template>
  <el-header v-if="showHeader">
    <div class="logo">
      <img src="@/assets/logo1.png" alt="Logo" />
      <span style="color: red">华师学子志愿活动平台</span>
    </div>
    <el-menu
      mode="horizontal"
      router
      :default-active="$route.path"
      class="menu"
    >
      <el-menu-item index="/org/activityManagement">
        <el-icon><HomeFilled /></el-icon>活动管理
      </el-menu-item>
      <el-menu-item index="/org/recruitBrief">
        <el-icon><List /></el-icon>招募情况
      </el-menu-item>
      <el-menu-item index="/org/orgInfo">
        <el-icon><UserFilled /></el-icon>组织信息
      </el-menu-item>
    </el-menu>
    <div class="user-actions">
      <template v-if="isLogged">
        <el-icon><User /></el-icon>
        <span style="margin-right: 10px">欢迎您</span>
        <el-button type="danger" plain round @click="handleLogout">
          退出登录
        </el-button>
      </template>
      <template v-else>
        <el-button type="default" @click="$router.push('/login')">
          登录
        </el-button>
        <el-button type="primary" @click="$router.push('/register')">
          注册
        </el-button>
      </template>
    </div>
  </el-header>
</template>
  
  <script setup>
import { useTokenStore } from "@/store/token";
import { useRoute, useRouter } from "vue-router";
import { ElMessageBox } from "element-plus";
import { UserFilled, User, HomeFilled, List } from "@element-plus/icons-vue";

defineProps({
  showHeader: {
    type: Boolean,
    default: true,
  },
});

const route = useRoute();
const router = useRouter();
const tokenStore = useTokenStore();

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm("确定要退出当前账号吗？", "退出确认", {
      confirmButtonText: "确定退出",
      cancelButtonText: "取消",
      type: "warning",
      closeOnClickModal: false,
    });
    logout();
  } catch (error) {
    console.log(error);
  }
};

const logout = () => {
  tokenStore.clearToken();
  // userInfoStore.removeInfo();
  router.push("/org/login");
};

const isLogged = tokenStore.token !== null;
</script>
  
  <style scoped>
.el-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 64px !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.menu {
  width: 500px;
}

.logo {
  display: flex;
  align-items: center;
  margin-left: 50px;
}

.logo img {
  height: 40px;
  margin-right: 10px;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.el-menu {
  border-bottom: none;
}
</style>