<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="login-header">
        <h1 style="color: red">华师学子志愿活动平台</h1>
        <h2>用户登录</h2>
      </div>
      <el-form :model="loginForm" :rules="rules" label-width="80px" status-icon>
        <el-form-item label="学号" prop="userId">
          <el-input
            v-model="loginForm.userId"
            placeholder="请输入学号"
          ></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item>
          <div style="display: flex; justify-content: space-between">
            <el-button link @click="$router.push('register')">注册账号</el-button>
            <el-button type="primary" @click="submitForm">登录</el-button>
          </div>
        </el-form-item>
      </el-form>
      <el-link
        type="info"
        :underline="false"
        @click="router.push('/login')"
        class="back-link"
      >
        ← 返回登录选择
      </el-link>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { ElMessage } from "element-plus";
import { useTokenStore } from "@/store/token.js";
import { useRouter } from "vue-router";
import { login } from "@/api/user";

const loginForm = reactive({
  userId: "",
  password: "",
});
const rules = reactive({
  userId: [
    { required: true, message: "请输入学号", trigger: "blur" },
    {
      pattern: /^[2]\d{10}$/,
      message: "学号必须是11位数字，且以2开头",
      trigger: "blur",
    },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    {
      min: 6,
      max: 16,
      message: "密码长度在6-16位之间",
      trigger: "blur",
    },
  ],
});

const tokenStore = useTokenStore();
const router = useRouter();

const submitForm = async () => {
  await login(loginForm)
    .then((res) => {
      ElMessage.success(res.msg ? res.msg : "登录成功");
      // 存储token
      tokenStore.setToken(res.data);
      tokenStore.setRole('user');
      router.push("/user/personal-center");
    })
    .catch((err) => {
      // ElMessage.error(err.msg ? err.msg : "服务异常");
    });
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 80vh;
  background-color: #f5f5f5;
}

.login-card {
  width: 400px;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 20px;
}

.login-header h2 {
  color: #303133;
}
.back-link {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  font-size: 14px;
  color: #606266;
  transition: all 0.3s ease;
}
</style>