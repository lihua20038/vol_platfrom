<template>
  <div class="register-container">
    <el-card class="register-card">
      <h1 style="color: red; text-align: center">华师学子志愿活动平台</h1>
      <h2 class="register-title">组织账号注册</h2>

      <el-form
        :model="form"
        :rules="rules"
        ref="registerForm"
        label-width="100px"
        status-icon
      >
        <!-- 组织名称 -->
        <el-form-item label="组织名称" prop="organizationName">
          <el-input
            v-model="form.organizationName"
            placeholder="请输入组织全称"
            clearable
          />
        </el-form-item>
        <!-- 注册码 -->
        <el-form-item label="注册码" prop="code">
          <el-input v-model="form.code" placeholder="请输入注册码" clearable />
        </el-form-item>
        <!-- 学院选择 -->
        <el-form-item label="所属学院" prop="collegeName">
          <el-select
            v-model="form.collegeName"
            placeholder="请选择学院"
            filterable
            @change="handleCollegeChange"
          >
            <el-option
              v-for="college in colleges"
              :key="college.collegeId"
              :label="college.collegeName"
              :value="college.collegeName"
            />
          </el-select>
        </el-form-item>

        <!-- 密码 -->
        <el-form-item label="登录密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="6-16位英文或数字"
            show-password
          />
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>

        <!-- 操作按钮 -->
        <el-form-item>
          <div class="button-group">
            <el-button @click="resetForm">重置</el-button>
            <el-button
              type="primary"
              @click="submitForm"
              :loading="isSubmitting"
            >
              立即注册
            </el-button>
          </div>
        </el-form-item>

        <!-- 返回链接 -->
        <el-link
          type="info"
          :underline="false"
          @click="$router.push('/org/login')"
          class="back-link"
        >
          ← 返回登录
        </el-link>
      </el-form>
    </el-card>
  </div>
</template>
  
<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { useRouter } from "vue-router";
import { useCollegeInfosStore } from "@/store/collegeInfos";
import { orgRegister } from "@/api/organization.js";

const collegeInfosStore = useCollegeInfosStore();
const colleges = ref([
  { collegeId: "001", collegeName: "计算机学院" },
  { collegeId: "002", collegeName: "文学院" },
  { collegeId: "003", collegeName: "数学学院" },
]);

const form = reactive({
  organizationName: "",
  code: "",
  collegeName: "",
  collegeId: "",
  password: "",
  confirmPassword: "",
});

const rules = reactive({
  organizationName: [
    { required: true, message: "请输入组织名称", trigger: "blur" },
    { min: 3, max: 21, message: "长度在3到21个字符", trigger: "blur" },
  ],
  code: [{ required: true, message: "请输入注册码", trigger: "blur" }],
  collegeName: [
    { required: true, message: "请选择所属学院", trigger: "change" },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    {
      pattern: /^[A-Za-z0-9]{6,16}$/,
      message: "密码必须为6-16位英文或数字组合",
      trigger: "blur",
    },
  ],
  confirmPassword: [
    { required: true, message: "请输入确认密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error("两次输入密码不一致!"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
});

// 找到所选学院的id
const handleCollegeChange = (selectedCollege) => {
  const college = colleges.value.find((c) => c.collegeName === selectedCollege);
  form.collegeId = college ? college.collegeId : "";
};

// 表单提交
const isSubmitting = ref(false);
const registerForm = ref(null);
const router = useRouter();

const submitForm = async () => {
  try {
    await registerForm.value.validate();
    isSubmitting.value = true;

    const res = await orgRegister(form);
    // console.log('注册成功:', res)

    ElMessage.success(res?.msg || "注册成功");
    router.push("/org/login");
  } catch (error) {
  } finally {
    isSubmitting.value = false;
  }
};

// 重置表单
const resetForm = () => {
  registerForm.value.resetFields();
  form.collegeId = "";
};

const getColleges = () => {
  if (collegeInfosStore.isEmpty) collegeInfosStore.fetchColleges();
  colleges.value = collegeInfosStore.colleges;
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
  min-height: 80vh;
  background-color: #f5f7fa;
}

.register-card {
  width: 550px;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.register-title {
  text-align: center;
  color: #409eff;
  margin-bottom: 20px;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.back-link {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  color: #909399;
  transition: color 0.3s;
}

.back-link:hover {
  color: #409eff;
}

/* 适配移动端 */
@media (max-width: 768px) {
  .register-card {
    width: 90%;
    padding: 20px;
  }

  :deep(.el-input) {
    width: 100%;
  }

  .button-group {
    flex-direction: column;
  }
}
</style>