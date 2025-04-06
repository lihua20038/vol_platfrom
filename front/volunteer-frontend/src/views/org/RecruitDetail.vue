<template>
  <div class="activity-container">
    <!-- 头部信息区 -->
    <div class="header-info">
      <h2>{{ RecruitDetailInfo.activityName }}</h2>
      <!--       <el-button type="info" round @click="$router.push('/org/recruitBrief')"
        >返回</el-button
      > -->
    </div>

    <div class="header2">
      <div style="color: red">
        报名截止时间：{{ RecruitDetailInfo.endDateTime }}
      </div>
      <div style="display: flex; gap: 30px">
        <el-button
          type="success"
          size="small"
          @click="handleUpdateStateBatch(3)"
        >
          批量考勤通过
        </el-button>
        <el-button
          type="danger"
          size="small"
          @click="handleUpdateStateBatch(2)"
        >
          批量考勤未通过
        </el-button>
      </div>
    </div>
    <!-- 筛选区 -->
    <div class="filters">
      <el-select
        v-model="selectedPeriod"
        placeholder="选择时间段"
        style="width: 500px"
      >
        <el-option
          v-for="detail in RecruitDetailInfo.details"
          :key="detail.period"
          :label="detail.period"
          :value="detail.period"
        />
      </el-select>
      <el-select
        v-model="selectedState"
        placeholder="选择状态"
        style="width: 130px"
      >
        <el-option
          v-for="state in stateOptions"
          :key="state.value"
          :label="state.label"
          :value="state.value"
        />
      </el-select>
      <div v-if="currentDetail" style="display: flex; gap: 30px">
        <div>
          志愿者：剩 {{ currentDetail.volLeft }}/
          {{ currentDetail.volunteerNum }}
        </div>
        <div>
          负责人：剩 {{ currentDetail.leaderLeft }}/
          {{ currentDetail.leaderNum }}
        </div>
        <div>共{{ currentDetail.regList?.length || 0 }}条记录</div>
      </div>
    </div>
    <!-- 数据表格 -->
    <el-table :data="filteredRegList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" :selectable="selectable" />
      <el-table-column prop="name" label="姓名" width="150px" />
      <el-table-column prop="collegeName" label="学院" width="150px" />
      <el-table-column prop="position" label="岗位" width="100px" />
      <el-table-column prop="contactInfo" label="联系方式" width="200px" />
      <el-table-column label="状态" width="150px">
        <template #default="{ row }">{{ stateMap[row.state] }}</template>
      </el-table-column>
      <el-table-column label="操作" width="300px">
        <template #default="{ row }">
          <div v-if="row.state === 0" style="display: flex">
            <el-button
              type="success"
              size="small"
              @click="handleUpdateStateSingle(row, 3)"
              >考勤通过</el-button
            >
            <el-button
              type="danger"
              size="small"
              @click="handleUpdateStateSingle(row, 2)"
              >考勤未通过</el-button
            >
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>


<script setup>
import { ref, computed, onMounted } from "vue";
import { fetchRecruitDetail, updateRegState } from "@/api/activity";
import { useRoute } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";

const route = useRoute();

const RecruitDetailInfo = ref({
  activityName: "活动名称",
  endDateTime: "2025/4/3",
  details: [
    {
      period: "体育馆 09:00-12:00",
      volunteerNum: 10,
      volLeft: 5,
      leaderNum: 2,
      leaderLeft: 1,
      regList: [
        {
          name: "张三",
          collegeName: "计算机学院",
          contactInfo: "13800138000",
          state: 0,
          position: "负责人",
        },
      ],
    },
    {
      period: "操场 14:00-17:00",
      volunteerNum: 8,
      volLeft: 3,
      leaderNum: 1,
      leaderLeft: 0,
      regList: [
        {
          name: "李四",
          collegeName: "文学院",
          contactInfo: "13900139000",
          state: 3,
          position: "志愿者",
        },
      ],
    },
  ],
});

const stateMap = { 4: "全部", 0: "报名成功", 2: "报名未参与", 3: "报名且参与" };
const stateOptions = Object.entries(stateMap).map(([value, label]) => ({
  value: +value,
  label,
}));
const selectable = (row) => {
  return row.state === 0; // 只有当 state 为 0(报名成功) 时可选中该行
};

const selectedPeriod = ref("");
const selectedState = ref(4);
const selectedIds = ref([]);
const selectedReg = ref([]);

// 获取当前选中的detail
const currentDetail = computed(() => {
  return RecruitDetailInfo.value?.details.find(
    (d) => d.period === selectedPeriod.value
  );
});

const handleSelectionChange = (val) => {
  selectedIds.value = val.map((v) => v.activityRegistrationId);
  selectedReg.value = val;
};

const handleUpdateStateBatch = async (newState) => {
  if (!selectedIds.value.length) return;
  const res = await handleUpdate(selectedIds.value, newState);
  if (res) selectedReg.value.forEach((r) => (r.state = newState));
};

const handleUpdateStateSingle = async (row, newState) => {
  const res = await handleUpdate([row.activityRegistrationId], newState);
  if (res) row.state = newState;
};

const handleUpdate = async (ids, newState) => {
  try {
    const payload = {
      newState: newState,
      ids: ids,
    };
    await updateRegState(payload);
    ElMessage.success("操作成功");
    return true;
  } catch (err) {
    console.log(err);
    return false;
  }
};

// 处理筛选后的数据
const filteredRegList = computed(() => {
  if (!currentDetail.value) return [];
  return currentDetail.value.regList?.filter((item) => {
    return selectedState.value === 4 || item.state === selectedState.value;
  });
});

const fetchData = async () => {
  try {
    const activityId = route.params.activityId;
    const res = await fetchRecruitDetail(activityId);
    RecruitDetailInfo.value = { ...res?.data };
    RecruitDetailInfo.value?.details.forEach((d) => {
      d.period = d.activityLocation + " " + d.beginTime + " 至 " + d.endTime;
    });
    // console.log(res);
  } catch (err) {
    console.log(err);
  }
};

onMounted(() => {
  fetchData();
});
</script>


<style scoped>
.activity-container {
  padding: 20px;
  max-width: 1100px;
  margin: 0 auto;
}
.header {
  margin-bottom: 10px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.filters {
  display: flex;
  gap: 30px;
  align-items: center;
  margin-bottom: 20px;
}
.el-select {
  width: 200px;
}
.el-table {
  margin-top: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header2 {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
  margin-left: auto;
  width: 100%;
}
</style>
