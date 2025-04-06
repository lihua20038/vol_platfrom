<template>
  <div class="register-container">
    <el-card class="register-card">
      <div class="register-header">
        <h1 style="color: red; text-align: center">华师学子志愿活动平台</h1>
        <h2>用户注册</h2>
      </div>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="rules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="学号" prop="userId">
          <el-input
            v-model.number="registerForm.userId"
            placeholder="请输入学号"
          ></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input
            v-model="registerForm.name"
            placeholder="请输入姓名"
          ></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="registerForm.gender">
            <el-radio value="M">男</el-radio>
            <el-radio value="F">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="学院" prop="collegeName">
          <el-select
            v-model="registerForm.collegeName"
            placeholder="请选择学院"
            @change="handleCollegeChange"
          >
            <el-option
              v-for="college in collegeList"
              :key="college.collegeId"
              :label="college.collegeName"
              :value="college.collegeName"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phoneNumber">
          <el-input
            v-model="registerForm.phoneNumber"
            placeholder="请输入手机号"
          ></el-input>
        </el-form-item>
        <el-form-item label="联系方式" prop="contactInfo">
          <el-input
            v-model="registerForm.contactInfo"
            placeholder="请输入联系方式（可选）"
            type="textarea"
            :rows="2"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <div style="display: flex; justify-content: space-between; gap: 20px">
            <el-button type="text" @click="$router.push('/user/login')"
              >返回登录</el-button
            >
            <el-button @click="resetForm">重置</el-button>
            <el-button type="primary" @click="submitForm">注册</el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from "vue";
import { ElMessage } from "element-plus";
import { register } from "@/api/user";
import { useCollegeInfosStore } from "@/store/collegeInfos";
import { useRouter } from "vue-router";

const router = useRouter();
const collegeInfosStore = useCollegeInfosStore();

const registerForm = reactive({
  userId: "",
  password: "",
  confirmPassword: "",
  name: "",
  gender: "",
  collegeName: "",
  collegeId: "",
  phoneNumber: "",
  contactInfo: "",
});

const collegeList = ref([
  { collegeId: 1, collegeName: "文学院" },
  { collegeId: 2, collegeName: "计算机学院" },
  { collegeId: 3, collegeName: "其他" },
]);

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    return callback(new Error("请确认密码"));
  } else if (value !== registerForm.password) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

const rules = {
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
      pattern: /^[A-Za-z0-9]{6,16}$/,
      message: "密码必须由6-16位数字或字母组成",
      trigger: "blur",
    },
  ],
  confirmPassword: [
    { required: true, message: "请确认密码", trigger: "blur" },
    { validator: validateConfirmPassword, trigger: "blur" },
  ],
  name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
  gender: [{ required: true, message: "请选择性别", trigger: "change" }],
  collegeName: [{ required: true, message: "请选择学院", trigger: "change" }],
  phoneNumber: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
    // { validator: validatePhone, trigger: "blur" },
  ],
};

const registerFormRef = ref(null);

const handleCollegeChange = (value) => {
  const selectedCollege = collegeList.value.find(
    (college) => college.collegeName === value
  );
  if (selectedCollege) {
    registerForm.collegeId = selectedCollege.collegeId;
  }
};

const submitForm = () => {
  registerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await register(registerForm);
        ElMessage.success(res.msg ? res.msg : "注册成功");
        router.push("login");
      } catch {}
    }
  });
};

const getColleges = () => {
  if (collegeInfosStore.isEmpty()) collegeInfosStore.fetchColleges();
  collegeList.value = collegeInfosStore.colleges;
};

const resetForm = () => {
  registerFormRef.value.resetFields();
};

onMounted(() => {
  getColleges();
});
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 85vh;
  background-color: #f5f5f5;
}

.register-card {
  width: 600px;
  height: 600px;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.register-header {
  text-align: center;
  margin-top: 0px;
}

.register-header h2 {
  color: hsl(200, 95%, 55%);
}
</style>