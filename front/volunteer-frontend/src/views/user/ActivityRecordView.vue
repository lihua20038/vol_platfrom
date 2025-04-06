<template>
  <div class="activity-record">
    <div class="page-header">
      <h2>个人活动记录</h2>
    </div>
    <!-- 筛选区域 -->
    <div class="filter-container">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="状态">
          <el-select
            v-model="filterForm.state"
            placeholder="请选择状态"
            @change="handleFilter"
            style="width: 130px"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <!-- <el-form-item>
          <el-button type="primary" @click="">筛选</el-button>
        </el-form-item> -->
      </el-form>
    </div>
    <!-- 报名记录区域 -->
    <div class="record-table-container">
      <el-table
        :data="records"
        style="width: 100%"
        v-loading="loading"
        element-loading-text="加载中..."
      >
        <el-table-column
          prop="activityName"
          label="活动名称"
          min-width="180"
        ></el-table-column>
        <el-table-column
          prop="activityLocation"
          label="活动地点"
          min-width="180"
        ></el-table-column>
        <el-table-column
          prop="activityTime"
          label="活动时间"
          min-width="300"
        ></el-table-column>
        <el-table-column
          prop="organizationName"
          label="发布组织"
          min-width="140"
        ></el-table-column>
        <el-table-column
          prop="position"
          label="岗位"
          width="80"
        ></el-table-column>

        <el-table-column label="状态" width="95">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.state)" effect="plain">
              {{ getStatusText(scope.row.state) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="" width="95">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              plain
              text
              @click="viewActivityDetail(scope.row)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="130">
          <template #default="scope">
            <el-button
              v-if="scope.row.state === 0"
              type="danger"
              size="small"
              @click="handleCancelRegistration(scope.row)"
            >
              取消报名
            </el-button>
            <el-button
              v-else-if="scope.row.state === 1"
              type="primary"
              size="small"
              @click="handleReRegister(scope.row)"
            >
              重新报名
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页条 -->
      <el-pagination
        :current-page="filterForm.pageNo"
        :page-size="filterForm.pageSize"
        :page-sizes="[10, 30, 50]"
        layout="jumper, total, sizes, prev, pager, next"
        background
        :total="total"
        @size-change="onSizeChange"
        @current-change="onCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </div>
    <!-- 查看详情框 -->
    <el-dialog title="报名详情" v-model="showEditDialog" width="50%">
      <el-descriptions
        :model="record"
        :column="1"
        label-width="100px"
        border="true"
        direction="horizontal"
      >
        <el-descriptions-item label="活动名称" prop="activityName">{{
          record.activityName
        }}</el-descriptions-item>
        <el-descriptions-item label="活动地点" prop="activityLocation">{{
          record.activityLocation
        }}</el-descriptions-item>
        <el-descriptions-item label="活动时间" prop="activityTime">{{
          record.activityTime
        }}</el-descriptions-item>

        <el-descriptions-item label="岗位" prop="position">{{
          record.position
        }}</el-descriptions-item>
        <el-descriptions-item label="状态" prop="state">{{
          record.state
        }}</el-descriptions-item>
        <el-descriptions-item label="报名时间" prop="participateTime">{{
          record.participateTime
        }}</el-descriptions-item>
        <el-descriptions-item label="志愿时/h" prop="volHours">{{
          record.volHours
        }}</el-descriptions-item>
        <el-descriptions-item label="发布组织" prop="organizationName">{{
          record.organizationName
        }}</el-descriptions-item>
        <el-descriptions-item label="联系方式" prop="contactInfo">{{
          record.contactInfo
        }}</el-descriptions-item>
        <el-descriptions-item label="活动描述" prop="activityDesc">{{
          record.activityDesc
        }}</el-descriptions-item>
        <el-descriptions-item label="额外说明" prop="extraInfo">{{
          record.extraInfo
        }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="showEditDialog = false"
            >返回</el-button
          >
        </div>
      </template>
    </el-dialog>
  </div>
</template>
  
<script setup>
import { ref, onMounted } from "vue";
import {
  getActivityRecords,
  reRegister,
  cancelRegistration,
} from "@/api/activity";
import { ElMessage, ElMessageBox } from "element-plus";

const showEditDialog = ref(false);
const records = ref([
  {
    activityRegistrationId: "",
    activityName: "",
    activityLocation: "",
    position: "",
    beginTime: "",
    endTime: "",
    activityTime: "", // beginTime - endTime
    state: "0",
    organizationName: "",
    activityDesc: "",
    contactInfo: "",
    extraInfo: "",
    volHours: "",
    participateTime: "",
  },
]);
const record = ref("");
const filterForm = ref({
  pageNo: 1,
  pageSize: 10,
  state: null,
});
const total = ref(0);
const loading = ref(false);
const statusOptions = ref([
  { value: null, label: "全部" },
  { value: 0, label: "报名成功" },
  { value: 1, label: "取消报名" },
  { value: 2, label: "报名未参与" },
  { value: 3, label: "报名且参与" },
]);

const fetchRecords = async () => {
  loading.value = true;

  await getActivityRecords(filterForm.value)
    .then((response) => {
      records.value = response.data.records || [];
      //
      records.value.forEach((item) => {
        item.activityTime = item.beginTime + " - " + item.endTime;
      });
      total.value = response.data.total || 0;
    })
    .catch((error) => {})
    .finally(() => {
      loading.value = false;
    });
};

const handleFilter = () => {
  filterForm.value.pageNo = 1;
  fetchRecords();
};

//当每页条数发生了变化，调用此函数
const onSizeChange = (size) => {
  filterForm.value.pageSize = size;
  fetchRecords();
};
//当前页码发生变化，调用此函数
const onCurrentChange = (newPageNo) => {
  filterForm.value.pageNo = newPageNo;
  fetchRecords();
};

const statusMap = {
  0: "报名成功",
  1: "取消报名",
  2: "报名未参与",
  3: "报名且参与",
};
const getStatusText = (state) => {
  return statusMap[state] || "未知状态";
};
const statusTypeMap = {
  0: "primary",
  1: "info",
  2: "warning",
  3: "success",
};
const getStatusType = (state) => {
  return statusTypeMap[state] || "info";
};

const viewActivityDetail = (data) => {
  record.value = { ...data };
  showEditDialog.value = true;
};

// 取消报名操作
const handleCancelRegistration = async (row) => {
  try {
    await ElMessageBox.confirm("确定要取消报名吗？", "操作确认", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    const res = cancelRegistration(row.activityRegistrationId);
    // console.log(res)
    ElMessage.success(res.msg || "取消报名成功");
    fetchRecords();
  } catch (error) {
    if (error !== "cancel") {
      // 排除用户主动取消的情况
      ElMessage.error("操作失败，请检查网络连接");
      // console.error("取消报名失败:", error);
    }
  }
};

const handleReRegister = (row) => {
  ElMessageBox.confirm("确定要重新报名该时段的活动吗？", "操作确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      // 调用重新报名API
      await reRegister(row.activityRegistrationId)
        .then((res) => {
          // console.log("重新报名成功");
          ElMessage.success(res.msg || "重新报名成功");
          fetchRecords();
        })
        .catch((err) => {});
    })
    .catch((err) => {});
};

onMounted(() => {
  fetchRecords();
});
</script>
  
<style scoped>
.activity-record {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.filter-container {
  display: flex;
  margin-bottom: 20px;
}

.filter-form {
  margin-left: auto;
}

.record-table-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>