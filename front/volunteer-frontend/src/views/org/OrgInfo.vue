<template>
  <div class="personal-center">
    <div class="user-profile">
      <div class="user-info">
        <div class="user-avatar">
          <el-avatar src="/images/user.png" size="large"></el-avatar>
        </div>
        <div class="user-details">
          <p>组织名称: {{ orgInfo.organizationName }}</p>
          <p>所属学院: {{ orgInfo.collegeName }}</p>
        </div>
      </div>
    </div>
    <div class="user-actions">
      <el-button type="primary" @click="showEditDialog = true">
        修改组织信息
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
      title="修改组织信息"
      v-model="showEditDialog"
      width="40%"
      @closed="onDialogClosed"
    >
      <el-form
        :model="editForm"
        ref="editFormRef"
        :rules="editRules"
        label-width="130px"
        status-icon
      >
        <el-form-item label="组织名称" prop="organizationName">
          <el-input v-model="editForm.organizationName"></el-input>
        </el-form-item>
        <el-form-item label="所属学院" prop="collegeName">
          <el-input v-model="editForm.collegeName" :disabled="true"></el-input>
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
import { useOrgInfoStore } from "@/store/orgInfo.js";
import { getOrgInfo, orgChangePassword, updateOrgInfo} from "@/api/organization.js";

import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";

const router = useRouter();
const tokenStore = useTokenStore();
const orgInfoStore = useOrgInfoStore();
const showEditDialog = ref(false);
const editForm = ref({
  organizationName: "",
  collegeName: "",
  collegeId: "",
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
      message: "密码由6-16位数字或字母组成",
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
      lockScroll: false,
      beforeClose: async (action, instance, done) => {
        if (action === "confirm") {
          instance.confirmButtonLoading = true;
          try {
            // 执行密码修改请求
            const res = await orgChangePassword({
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

const editRules = ref({
  organizationName: [
    { required: true, message: "请输入组织名称", trigger: "blur" },
    { min: 3, max: 21, message: "长度在3到21个字符", trigger: "blur" },
  ],
});

const orgInfo = ref({
  organizationName: "",
  collegeName: "",
  collegeId: "",
});

const editFormRef = ref("");

let rawData = ""; // 存储原始组织名

// 监听弹窗打开事件
watch(showEditDialog, async (newVal) => {
  if (newVal) {
    await nextTick(); // 等待 DOM 更新完成
    rawData = editForm.value.organizationName;
  }
});

const submitEditForm = () => {
  editFormRef.value.validate(async (valid) => {
    if (valid) {
      if (editForm.value.organizationName === rawData) {
        ElMessage.warning("未修改任何信息");
        return;
      }
      const payload = {
        organizationId: editForm.value.organizationId,
        organizationName: editForm.value.organizationName
      };
      await updateOrgInfo(payload)
        .then((res) => {
          ElMessage.success(res.msg || "修改信息成功");
          showEditDialog.value = false;
          fetchOrgInfo();
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
  editForm.value = { ...orgInfo.value };
  // console.log(editForm.value);
};

const logout = async () => {
  tokenStore.clearToken();
  orgInfoStore.removeInfo();
  router.push("/org/login");
};

const fetchOrgInfo = async () => {
  try {
    let res = await getOrgInfo();
    // 存储用户信息
    orgInfoStore.removeInfo();
    orgInfoStore.setInfo(res.data);
    orgInfo.value = { ...res.data };
    editForm.value = { ...res.data };
  } catch (err) {
    console.log(err);
  }
};

onMounted(() => {
  fetchOrgInfo();
});
</script>
  
<style scoped>
.personal-center {
  max-width: 450px;
  margin: 0 auto;
}

.user-profile {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-top: 20px;
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