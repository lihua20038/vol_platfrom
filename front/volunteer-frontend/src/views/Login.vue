<template>
  <div class="unified-login">
    <div class="system-title">
      <h1>欢迎您使用，华师学子志愿活动系统</h1>
    </div>

    <div class="login-buttons">
      <el-button
        type="primary"
        size="large"
        @click="navigateToOrgLogin"
        class="org-login-btn"
      >
        组织登录
      </el-button>

      <el-button
        type="success"
        size="large"
        @click="navigateToStudentLogin"
        class="student-login-btn"
      >
        学生用户登录
      </el-button>
    </div>
  </div>
</template>
  
  <script setup>
import { useRouter } from "vue-router";
import { useCollegeInfosStore } from "@/store/collegeInfos.js";
import { onMounted } from "vue";

const collegeInfosStore = useCollegeInfosStore();
const router = useRouter();

const navigateToOrgLogin = () => {
  router.push("/org/login");
};

const navigateToStudentLogin = () => {
  router.push("/user/login");
};

onMounted(() => {
  collegeInfosStore.fetchColleges();
});
</script>
  
<style scoped>
.unified-login {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background: #f0f2f5;
}

.system-title {
  text-align: center;
  margin-bottom: 50px;
}

.system-title h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  letter-spacing: 2px;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.login-buttons {
  display: flex;
  gap: 30px;
}

.el-button {
  width: 200px;
  height: 60px;
  font-size: 1.2rem;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.org-login-btn {
  background: linear-gradient(135deg, #409eff, #337ecc);
  border: none;
}

.student-login-btn {
  background: linear-gradient(135deg, #67c23a, #529b2e);
  border: none;
}
</style>