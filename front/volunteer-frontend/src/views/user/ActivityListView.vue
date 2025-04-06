<template>
  <div class="activity-list">
    <div class="page-header">
      <h2>校园志愿活动</h2>
    </div>
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
        <el-form-item label="活动时间段">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY/MM/DD"
            value-format="YYYY-MM-DD"
            clearable
          ></el-date-picker>
        </el-form-item>
        <el-form-item>
          仅看可参与：
          <el-switch
            v-model="filter"
            class="mb-2"
            inline-prompt
            active-text="Y"
            inactive-text="N"
            style="
              --el-switch-on-color: #13ce66;
              --el-switch-off-color: #ff4949;
            "
          />
        </el-form-item>
        <el-form-item>
          <el-button
            :icon="Search"
            circle
            type="primary"
            @click="handleSearch"
          />
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
          prop="organizationName"
          label="发布组织"
          min-width="150"
        ></el-table-column>
        <el-table-column
          prop="endDateTime"
          label="报名截止时间"
          min-width="160"
        ></el-table-column>
        <el-table-column label="" width="100">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="viewActivityDetail(scope.row.activityId)"
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
        :page-sizes="[10, 20, 30, 40, 50]"
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
import { Search } from "@element-plus/icons-vue";
import { ref, onMounted } from "vue";
import { searchActivities } from "@/api/activity";
import { useUserInfoStore } from "@/store/userInfo.js";
import { useRouter } from "vue-router";

const router = useRouter();
const userInfoStore = useUserInfoStore();
const activities = ref([]);

const filter = ref(false);
const searchForm = ref({
  pageNo: 1,
  pageSize: 10,
  activityName: "",
  dateRange: null,
  collegeId: userInfoStore.info.collegeId,
});
const total = ref(0);
const loading = ref(false);

const fetchActivities = async () => {
  loading.value = true;
  const params = {
    pageNo: searchForm.value.pageNo,
    pageSize: searchForm.value.pageSize,
    activityName: searchForm.value.activityName,
    leftTime: searchForm.value.dateRange
      ? `${searchForm.value.dateRange[0]} 00:00:00`
      : "",
    rightTime: searchForm.value.dateRange
      ? `${searchForm.value.dateRange[1]} 23:59:59`
      : "",
    collegeId: !filter.value ? null : searchForm.value.collegeId,
  };

  const response = await searchActivities(params);
  activities.value = response.data.records || [];
  total.value = response.data.total || 0;
  loading.value = false;
};

const handleSearch = () => {
  searchForm.value.pageNo = 1;
  fetchActivities();
};

//当每页条数发生了变化，调用此函数
const onSizeChange = (size) => {
  searchForm.value.pageSize = size;
  fetchActivities();
};
//当前页码发生变化，调用此函数
const onCurrentChange = (newPageNo) => {
  searchForm.value.pageNo = newPageNo;
  fetchActivities();
};

const viewActivityDetail = (id) => {
  router.push({ name: "ActivityDetail", params: { activityId: id } });
};

onMounted(() => {
  fetchActivities();
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
  width: 90%;
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