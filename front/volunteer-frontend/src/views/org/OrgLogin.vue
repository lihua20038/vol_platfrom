<template>
  <div class="login-container">
    <el-card class="login-card">
      <h1 style="color: red; text-align: center">华师学子志愿活动平台</h1>
      <h2 class="login-title">组织账号登录</h2>

      <el-form :model="form" :rules="rules" label-width="80px" status-icon>
        <el-form-item label="组织名称" prop="organizationName">
          <el-input
            v-model="form.organizationName"
            placeholder="请输入组织名称"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <div style="display: flex; justify-content: flex-end">
          <el-button link @click="$router.push('register')">注册账号</el-button>
          <el-button
            type="primary"
            @click="handleLogin"
            style="position: absolute; left: 50%; transform: translateX(-50%)"
            >登录</el-button
          >
        </div>
        <el-link
          type="info"
          :underline="false"
          @click="router.push('/login')"
          class="back-link"
        >
          ← 返回登录选择
        </el-link>
      </el-form>
    </el-card>
  </div>
</template>
  
<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { orgLogin, getOrgInfo } from "@/api/organization";
import { useOrgInfoStore } from "@/store/orgInfo";
import { useTokenStore } from "@/store/token.js";

const tokenStore = useTokenStore();
const orgInfoStore = useOrgInfoStore();
const router = useRouter();
const form = ref({
  organizationName: "",
  password: "",
});

const rules = reactive({
  organizationName: [
    { required: true, message: "请输入组织名称", trigger: "blur" },
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

const handleLogin = async () => {
  // 实现组织登录逻辑 todo
  // console.log("组织登录:", form.value);
    try {
    const res = await orgLogin(form.value);
    ElMessage.success(res.msg || "登录成功");
    tokenStore.setToken(res.data);

    const infoRes = await getOrgInfo();
    orgInfoStore.setInfo(infoRes.data);
    tokenStore.setRole('org');
    router.push("/org/orgInfo");
  } catch (err) {console.log(err)}
};
</script>
  
<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 85vh;
  background: #f0f2f5;
}

.login-card {
  width: 400px;
  padding: 30px;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.login-title {
  text-align: center;
  color: #409eff;
  margin-bottom: 30px;
}

.login-btn {
  /* width: 100%; */
  text-align: center;
  /* height: 45px; */
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