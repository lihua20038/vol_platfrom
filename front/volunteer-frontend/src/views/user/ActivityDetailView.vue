<template>
  <div class="activity-detail">
    <div class="header-container">
      <div class="page-header">
        <h2>{{ activityDetailed.activityName }}</h2>
      </div>
      <div class="returnBut">
        <el-button
          type="primary"
          plain
          round
          @click="$router.push('/user/activity-list')"
          >返回</el-button
        >
      </div>
    </div>
    <el-descriptions
      class="margin-top"
      title="活动基本信息"
      :column="1"
      size="default"
      border
    >
      <el-descriptions-item label="活动描述">
        {{ activityDetailed.activityDesc }}
      </el-descriptions-item>
      <el-descriptions-item label="联系方式">
        {{ activityDetailed.contactInfo }}
      </el-descriptions-item>
      <el-descriptions-item label="招募截止时间">
        {{ activityDetailed.endDateTime }}
      </el-descriptions-item>
      <el-descriptions-item label="报名限制">
        {{ activityDetailed.allowedColleges }}
      </el-descriptions-item>
      <el-descriptions-item label="额外说明">
        {{ activityDetailed.extraInfo || "无" }}
      </el-descriptions-item>
      <el-descriptions-item label="发布组织">
        {{ activityDetailed.organizationName }}
      </el-descriptions-item>
    </el-descriptions>
    <div class="activity-schedule">
      <h3>活动日程</h3>
      <el-table :data="activityDetailed.activityDetails" style="width: 100%">
        <el-table-column prop="beginTime" label="开始时间" min-width="160">
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" min-width="160">
        </el-table-column>
        <el-table-column
          prop="activityLocation"
          label="活动地点"
          min-width="180"
        >
        </el-table-column>
        <el-table-column label="志愿者招募情况" min-width="160">
          <template #default="scope">
            <span
              >剩{{ scope.row.volLeft }} / {{ scope.row.volunteerNum }}人</span
            >
          </template>
        </el-table-column>
        <el-table-column label="负责人招募情况" min-width="160">
          <template #default="scope">
            <span
              >剩{{ scope.row.leaderLeft }} / {{ scope.row.leaderNum }}人</span
            >
          </template>
        </el-table-column>
        <el-table-column prop="volunteerHours" label="志愿时/h" min-width="120">
        </el-table-column>
        <el-table-column
          prop="extraHours"
          label="负责人额外志愿时/h"
          min-width="140"
        >
        </el-table-column>
      </el-table>
    </div>
    <!-- 活动报名表 -->
    <div class="registration-section" v-if="canRegister">
      <h3>报名活动</h3>
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="registerRules"
        label-width="100px"
        status-icon
      >
        <el-form-item label="报名岗位" prop="position">
          <el-radio-group
            v-model="registerForm.position"
            @change="handlePositionChange"
          >
            <el-radio value="0" :disabled="!hasVolunteerQuota">志愿者</el-radio>
            <el-radio value="1" :disabled="!hasLeaderQuota">负责人</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="活动时段" prop="activityDetailId">
          <el-select
            v-model="registerForm.activityDetailId"
            placeholder="请选择活动时段"
            style="width: 500px"
          >
            <el-option
              v-for="item in activityPeriods"
              :key="item.activityDetailId"
              :label="item.period"
              :value="item.activityDetailId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            @click="submitRegistration"
            :disabled="!canRegister"
          >
            提交报名
          </el-button>
        </el-form-item>
      </el-form>
    </div>
    <div v-else-if="overtime" class="overtime">
      <p>报名已截止</p>
    </div>
    <div v-else class="no-quota">
      <p>当前活动已无剩余名额</p>
    </div>
  </div>
</template>
  
<script setup>
import { ref, onMounted } from "vue";
import {
  getActivityDetail,
  getActivityById,
  participateActivity,
} from "@/api/activity";
import { useUserInfoStore } from "@/store/userInfo";
import { useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";

const userInfoStore = useUserInfoStore();
const route = useRoute();
const activityId = ref(route.params.activityId);
const activityDetailed = ref({
  activityName: "",
  activityDesc: "",
  contactInfo: "",
  endDateTime: "",
  allowedColleges: "",
  extraInfo: "",
  organizationName: "",
  activityDetails: [],
});
const registerForm = ref({
  activityId: activityId.value,
  activityDetailId: "",
  position: "0",
});
const activityPeriods = ref([
  { activityDetailId: 1, period: "2025-03-24 14:00:00至2025-03-24 15:00:00" },
]);

const registerFormRef = ref("");
const canRegister = ref(true); // 是否可报名
const overtime = ref(false); // 是否过了截止日期
const hasVolunteerQuota = ref(false);
const hasLeaderQuota = ref(false);
const registerRules = ref({
  position: [{ required: true, message: "请选择报名岗位", trigger: "change" }],
  activityDetailId: [
    { required: true, message: "请选择活动时间段", trigger: "change" },
  ],
});

const fetchActivityDetail = async () => {
  // console.log('当前活动ID:', route.params.activityId)
  await getActivityById(activityId.value)
    .then((res) => {
      activityDetailed.value = res.data;
    })
    .catch((err) => {});

  await getActivityDetail(activityId.value)
    .then((res) => {
      activityDetailed.value.activityDetails = res.data;
      checkRegistrationStatus();
    })
    .catch((err) => {});
  handlePositionChange(registerForm.value.position);
};

const checkRegistrationStatus = () => {
  const now = new Date();
  const endTime = new Date(activityDetailed.value.endDateTime);
  if (now > endTime) {
    canRegister.value = false;
    overtime.value = true;
    return;
  }

  hasVolunteerQuota.value = false;
  hasLeaderQuota.value = false;

  activityDetailed.value.activityDetails.forEach((detail) => {
    if (detail.volLeft > 0) {
      hasVolunteerQuota.value = true;
    }
    if (detail.leaderLeft > 0) {
      hasLeaderQuota.value = true;
    }
  });

  if (!hasVolunteerQuota.value && !hasLeaderQuota.value) {
    canRegister.value = false;
  }
  // console.log("hasVolLeft: " + hasVolunteerQuota.value)
  // console.log("hasLeaderLeft: " + hasLeaderQuota.value)
};

const submitRegistration = () => {
  registerFormRef.value.validate((valid) => {
    if (!valid) return;

    ElMessageBox.confirm("确认提交本次报名信息？", "报名确认", {
      confirmButtonText: "确认提交",
      cancelButtonText: "取消",
      type: "warning",
      closeOnClickModal: false,
    })
      .then(() => {
        // 用户确认后的处理
        participateActivity(registerForm.value)
          .then((res) => {
            if (res.code === 1) {
              ElMessage.success(res.msg || "报名成功");
              fetchActivityDetail();
            }
          })
          .catch((err) => {});
      })
      .catch(() => { ElMessage.info("已取消报名");});
  });
};

const handlePositionChange = (pos) => {
  // console.log("handlePositionChange" + "pos: " + pos);
  registerForm.value.position = pos;
  activityPeriods.value = [];
  activityDetailed.value.activityDetails.forEach((detail) => {
    // console.log(detail);
    if (pos === "0") {
      // 志愿者
      if (detail.volLeft > 0) {
        activityPeriods.value.push({
          activityDetailId: detail.activityDetailId,
          period:
            detail.beginTime +
            " 至 " +
            detail.endTime +
            " " +
            detail.activityLocation,
        });
      }
    } else {
      //负责人
      if (detail.leaderLeft > 0) {
        activityPeriods.value.push({
          activityDetailId: detail.activityDetailId,
          period:
            detail.beginTime +
            " 至 " +
            detail.endTime +
            " " +
            detail.activityLocation,
        });
      }
    }
  });
};
onMounted(() => {
  fetchActivityDetail();
});
</script>
  
<style scoped>
.activity-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
.header-container {
  display: flex;
  align-items: center; /* 垂直居中 */
  gap: 20px; /* 按钮与标题间距 */
  margin-bottom: 20px; /* 下方留白 */
}

.page-header {
  margin: 0; /* 清除 h2 默认上下边距 */
  line-height: 1; /* 根据实际情况调整行高 */
}

.returnBut {
  flex-shrink: 0;
  margin-left: auto;
}

.activity-schedule {
  margin-top: 20px;
}

.registration-section {
  margin-top: 30px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin: 0 auto;
}
/* margin: 0 auto;  */
.registration-closed {
  margin-top: 30px;
  text-align: center;
  color: #888;
}

.overtime {
  margin-top: 30px;
  text-align: center;
  color: #ff4d4f;
}

.no-quota {
  margin-top: 30px;
  text-align: center;
  color: #f03737;
}
</style>