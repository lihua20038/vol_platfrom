<template>
  <div class="personal-center">
    <div class="user-profile">
      <div class="user-info">
        <div class="user-avatar">
          <!-- <el-avatar icon="el-icon-user-solid" size="large"></el-avatar> -->
          <el-avatar src="/images/user.png" size="large"></el-avatar>
        </div>
        <div class="user-details">
          <h3>{{ userInfo.name }}</h3>
          <p>学号: {{ userInfo.userId }}</p>
          <p>学院: {{ userInfo.collegeName }}</p>
          <p>绑定手机号: {{ userInfo.phoneNumber }}</p>
        </div>
      </div>
      <div class="user-stats">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="stat-item">
              <span>总参与次数: {{ userInfo.totalCount }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="stat-item">
              <span>总志愿时: {{ userInfo.totalVolHours }} h</span>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
    <div class="user-actions">
      <el-button type="primary" @click="showEditDialog = true">
        修改个人信息
      </el-button>
      <el-button
        type="warning"
        @click="showChangePasswordDialog = true"
        style="margin-left: auto"
      >
        修改密码
      </el-button>
    </div>
    <!-- 修改信息框 -->
    <el-dialog
      title="修改个人信息"
      v-model="showEditDialog"
      width="50%"
      @closed="onDialogClosed"
    >
      <el-form
        :model="editForm"
        ref="editFormRef"
        :rules="editRules"
        label-width="130px"
        status-icon
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="editForm.gender">
            <el-radio label="M">男</el-radio>
            <el-radio label="F">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="学院" prop="collegeName">
          <el-select
            v-model="editForm.collegeName"
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
        <el-form-item label="i志愿绑定手机号" prop="phoneNumber">
          <el-input v-model.number="editForm.phoneNumber"></el-input>
        </el-form-item>
        <el-form-item label="联系方式" prop="contactInfo">
          <el-input
            v-model="editForm.contactInfo"
            type="textarea"
            :rows="2"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showEditDialog = false">取 消</el-button>
          <el-button type="primary" @click="submitEditForm">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 修改密码对话框 -->
    <el-dialog
      title="修改密码"
      v-model="showChangePasswordDialog"
      width="30%"
      @closed="onPasswordDialogClosed"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordRules"
        label-width="120px"
        status-icon
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="passwordForm.oldPassword"
            type="password"
            show-password
            placeholder="请输入当前密码"
          ></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            show-password
            placeholder="6-20位字母数字组合"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showChangePasswordDialog = false">取消</el-button>
          <el-button type="primary" @click="submitPasswordChange"
            >提交修改</el-button
          >
        </div>
      </template>
    </el-dialog>
  </div>
</template>
  
<script setup>
import { ref, onMounted, reactive, watch, nextTick } from "vue";
import { useTokenStore } from "@/store/token.js";
import { useUserInfoStore } from "@/store/userInfo.js";
import { useCollegeInfosStore } from "@/store/collegeInfos.js";
import { updateUserInfo, fetchUserInfo, changePassword } from "@/api/user";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";

const router = useRouter();
const tokenStore = useTokenStore();
const userInfoStore = useUserInfoStore();
const collegeInfosStore = useCollegeInfosStore();
const showEditDialog = ref(false);
const editForm = ref({
  name: "",
  gender: "",
  collegeName: "",
  collegeId: "",
  phoneNumber: "",
  contactInfo: "",
});
// 修改密码相关
const showChangePasswordDialog = ref(false);
const passwordFormRef = ref(null);
const passwordForm = reactive({
  oldPassword: "",
  newPassword: "",
});
const passwordRules = {
  oldPassword: [{ required: true, message: "请输入原密码", trigger: "blur" }],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    {
      pattern: /^[A-Za-z0-9]{6,16}$/,
      message: "密码必须由6-16位数字或字母组成",
      trigger: "blur",
    },
  ],
};
const submitPasswordChange = async () => {
  try {
    await passwordFormRef.value.validate();
    await ElMessageBox.confirm("确定要修改登录密码吗？", "操作确认", {
      confirmButtonText: "确认修改",
      cancelButtonText: "再想想",
      type: "warning",
      lockScroll: false, // 允许滚动查看表单内容
      beforeClose: async (action, instance, done) => {
        if (action === "confirm") {
          instance.confirmButtonLoading = true;
          try {
            // 执行密码修改请求
            const res = await changePassword({
              oldPassword: passwordForm.oldPassword,
              newPassword: passwordForm.newPassword,
            });
            if (res.msg === "密码修改成功") {
              ElMessage.success(res.msg);
              // showChangePasswordDialog.value = false;
              setTimeout(() => {
                //  修改密码后退出登录
                done();
                logout();
              }, 1300);
            } else {
              ElMessage.error(res.msg);
              instance.confirmButtonLoading = false;
            }
            done();
          } catch (error) {
            done();
          }
        } else {
          done();
        }
      },
    });
  } catch (error) {
    // 处理表单验证错误
    if (error !== "cancel") {
      ElMessage.warning("请正确填写所有必填项");
    }
  }
};
const onPasswordDialogClosed = () => {
  passwordFormRef.value.resetFields();
};

const collegeList = ref([
  { collegeId: 1, collegeName: "文学院" },
  { collegeId: 2, collegeName: "理学院" },
]);

const editRules = ref({
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
  ],
});

const userInfo = ref({
  name: "",
  userId: "",
  collegeName: "",
  contactInfo: "",
  totalCount: 0,
  totalVolHours: 0,
});

const editFormRef = ref("");

const handleCollegeChange = (value) => {
  const selectedCollege = collegeList.value.find(
    (college) => college.collegeName === value
  );
  if (selectedCollege) {
    editForm.value.collegeId = selectedCollege.collegeId;
  }
};

const rawData = ref({}); // 存储原始数据

// 监听弹窗打开事件
watch(showEditDialog, async (newVal) => {
  // console.log("打开修改框");
  // console.log(editForm.value);
  if (newVal) {
    await nextTick(); // 等待 DOM 更新完成
    rawData.value = JSON.parse(JSON.stringify(editForm.value));
  }
});
// 找出用户修改了的字段
const getChangedFields = (currentData) => {
  const changes = {};
  Object.keys(currentData).forEach((key) => {
    if (
      JSON.stringify(currentData[key]) !== JSON.stringify(rawData.value[key])
    ) {
      changes[key] = currentData[key];
    }
  });
  return changes;
};

const submitEditForm = () => {
  editFormRef.value.validate(async (valid) => {
    if (valid) {
      const changedFields = getChangedFields(editForm.value);
      if (Object.keys(changedFields).length === 0) {
        ElMessage.warning("未修改任何信息");
        return;
      }
      const payload = { ...changedFields };
      await updateUserInfo(payload)
        .then((res) => {
          ElMessage.success(res.msg || "修改信息成功");
          showEditDialog.value = false;
          getUserInfo();
        })
        .catch((error) => {
          console.error("Update user info failed:", error);
        });
    } else {
      console.log("Form validation failed");
      return false;
    }
  });
};

const onDialogClosed = () => {
  // editFormRef.value.resetFields();
  editForm.value = { ...userInfo.value };
  console.log(editForm.value);
};

const logout = async () => {
  tokenStore.clearToken();
  userInfoStore.removeInfo();
  router.push("/user/login");
};

const getUserInfo = async () => {
  try {
    let res = await fetchUserInfo();
    // 存储用户信息
    userInfoStore.removeInfo();
    userInfoStore.setInfo(res.data);
    userInfo.value = { ...res.data };
    editForm.value = { ...res.data };
  } catch (err) {}
  // editFormRef.value = { ...res.data };
  // console.log(editForm.value);
  // console.log(editFormRef.value);
};

const getColleges = () => {
  if (collegeInfosStore.isEmpty()) {
    collegeInfosStore.fetchColleges();
  }
  collegeList.value = collegeInfosStore.colleges;
};

onMounted(() => {
  getUserInfo();
  getColleges();
});
</script>
  
<style scoped>
.personal-center {
  max-width: 1000px;
  margin: 0 auto;
}

.user-profile {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.user-avatar {
  margin-right: 20px;
}

.user-details h3 {
  margin: 0 0 10px 0;
}

.user-details p {
  margin: 5px 0;
  color: #666;
}

.user-stats {
  margin-top: 20px;
}

.stat-item {
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  text-align: center;
}

.user-actions {
  display: flex;
  gap: 10px; /* 添加按钮间距 */
  margin-top: 20px;
}
.dialog-footer {
  padding-top: 15px;
  border-top: 1px solid #eee;
}
/* 加载状态时的光标样式 */
.el-message-box__content {
  cursor: wait;
}

/* 确认按钮加载状态样式 */
.confirm-loading .el-loading-spinner {
  top: -5px;
}
</style>