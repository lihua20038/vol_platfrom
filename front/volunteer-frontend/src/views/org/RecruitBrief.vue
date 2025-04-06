<template>
  <div class="activity-list">
    <!-- 搜索框 -->
    <div class="search-container">
      <el-form
        :inline="true"
        :model="searchForm"
        class="search-form"
        @keyup.enter.native="handleSearch"
      >
        <el-form-item label="活动名称">
          <el-input
            v-model="searchForm.activityName"
            placeholder="请输入活动名称"
            maxlength="12"
            clearable
          ></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.state"
            placeholder="请选择状态"
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
        <el-form-item>
          <el-button :icon="Search" circle @click="handleSearch" />
        </el-form-item>
      </el-form>
    </div>
    <!-- 活动列表 -->
    <div class="activity-table-container">
      <el-table
        :data="activities"
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
          prop="publishTime"
          label="发布时间"
          min-width="150"
        ></el-table-column>
        <el-table-column
          prop="recruitment"
          label="招募情况(已招/总需)"
          min-width="160"
        ></el-table-column>
        <el-table-column prop="state" label="状态" min-width="80">
          <template #default="scope">
            {{ getStatusLabel(scope.row.state) }}
          </template>
        </el-table-column>
        <el-table-column label="" width="130">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleViewDetail(scope.row.activityId)"
              text
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页条 -->
      <el-pagination
        :current-page="searchForm.pageNo"
        :page-size="searchForm.pageSize"
        :page-sizes="[10, 30, 50]"
        layout="jumper, total, sizes, prev, pager, next"
        background
        :total="total"
        @size-change="onSizeChange"
        @current-change="onCurrentChange"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </div>
  </div>
</template>
      
    <script setup>
import { Search, Plus } from "@element-plus/icons-vue";
import { ref, onMounted, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  fetchRecruitInfo,
} from "@/api/activity";
import { useRouter } from "vue-router";
import { useCollegeInfosStore } from "@/store/collegeInfos";

const router = useRouter();
const collegeInfosStore = useCollegeInfosStore();
const activities = ref([]);
const searchForm = ref({
  pageNo: 1,
  pageSize: 10,
  activityName: "",
  state: null,
});
const statusOptions = ref([
  { value: 4, label: "全部" },
  { value: 0, label: "招募中" },
  { value: 1, label: "已结束报名" },
  { value: 3, label: "活动结束" },
]);
const total = ref(0);
const loading = ref(false);
const originalAllowedColleges = ref([]);
const detailDialogVisible = ref(false);

watch(detailDialogVisible, (newVal) => {
  if (newVal) {
    originalAllowedColleges.value = JSON.parse(
      JSON.stringify(currentActivity.allowedColleges)
    );
  }
});

// 跳转到详情页面
const handleViewDetail = (id) => {
  // router.push({ name: "RecruitDetail", params: { activityId: id } });
  const route = router.resolve({
    name: "RecruitDetail",
    params: { activityId: id },
  });
  window.open(route.href, "_blank");
};

const fetchActivities = async () => {
  loading.value = true;
  const params = {
    pageNo: searchForm.value.pageNo,
    pageSize: searchForm.value.pageSize,
    activityName: searchForm.value.activityName,
    state: searchForm.value.state === 4 ? null : searchForm.value.state,
  };
  // 调用查询招募情况接口
  const response = await fetchRecruitInfo(params);
  activities.value = response.data?.records || [];
  activities.value.forEach((a) => {
    a.recruitment =
      "志愿者：" +
      a.volNum +
      "/" +
      a.volTotal +
      " 负责人：" +
      a.leaderNum +
      "/" +
      a.leaderTotal;
  });
  total.value = response.data?.total || 0;
  loading.value = false;
  // console.log(activities.value);
};

const getStatusLabel = (value) => {
  const option = statusOptions.value.find((opt) => opt.value === value);
  return option ? option.label : "未知状态";
};
const handleSearch = () => {
  searchForm.value.pageNo = 1;
  fetchActivities();
};

const onSizeChange = (size) => {
  searchForm.value.pageSize = size;
  fetchActivities();
};
const onCurrentChange = (newPageNo) => {
  searchForm.value.pageNo = newPageNo;
  fetchActivities();
};

const collegeList = ref([
  { collegeId: 1, collegeName: "文学院" },
  { collegeId: 2, collegeName: "计算机学院" },
  { collegeId: 3, collegeName: "其他" },
]);

const getColleges = () => {
  if (collegeInfosStore.isEmpty()) collegeInfosStore.fetchColleges();
  collegeList.value = [
    { collegeId: 0, collegeName: "无限制" },
    ...collegeInfosStore.colleges,
  ];
};

onMounted(() => {
  fetchActivities();
  getColleges();
});
</script>
      
    <style scoped>
.activity-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.search-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
  width: 100%;
}

.search-form {
  width: 95%;
}

.activity-table-container {
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