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
            style="width: 185px"
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
        <el-form-item>
          <el-button type="primary" @click="handleCreate"> 新增</el-button>
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
          prop="endDateTime"
          label="报名截止时间"
          min-width="160"
        ></el-table-column>
        <el-table-column
          prop="publishTime"
          label="发布时间"
          min-width="150"
        ></el-table-column>
        <el-table-column label="" width="130">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="handleViewDetail(scope.row)"
              text
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="state" label="状态" min-width="80">
          <template #default="scope">
            {{ getStatusLabel(scope.row.state) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <!-- 0：招募中 -->
            <el-button
              v-if="scope.row.state === 0"
              type="danger"
              size="small"
              @click="handleEndRecruit(scope.row)"
            >
              结束招募
            </el-button>
            <!-- 1：已结束报名 -->
            <el-button
              v-else-if="scope.row.state === 1"
              type="info"
              size="small"
              @click="handleEndActivity(scope.row)"
            >
              结束活动
            </el-button>
            <!-- 2：未发布 -->
            <el-button-group>
              <el-button
                v-if="scope.row.state === 2"
                type="primary"
                size="small"
                @click="handlePublish(scope.row)"
              >
                发布
              </el-button>
              <el-button
                v-if="scope.row.state === 2"
                @click="handleDelete(scope.row)"
                type="danger"
                size="small"
              >
                删除
              </el-button>
            </el-button-group>
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
    <!-- 活动详情表单 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="isEditMode ? '活动详情' : '新增活动'"
      width="800px"
      destroy-on-close
    >
      <el-form
        :model="currentActivity"
        label-width="120px"
        ref="activityForm"
        :rules="rules"
      >
        <!-- 基本信息 -->
        <el-form-item label="活动名称" prop="activityName">
          <el-input
            v-model="currentActivity.activityName"
            :disabled="!isEditable"
            placeholder="请输入活动名称"
          />
        </el-form-item>
        <el-form-item label="活动描述" prop="activityDesc">
          <el-input
            v-model="currentActivity.activityDesc"
            type="textarea"
            autosize
            :disabled="!isEditable"
          />
        </el-form-item>
        <el-form-item label="联系方式" prop="contactInfo">
          <el-input
            v-model="currentActivity.contactInfo"
            type="textarea"
            autosize
            :disabled="!isEditable"
          />
        </el-form-item>
        <el-form-item label="报名截止时间" prop="endDateTime">
          <el-date-picker
            v-model="currentActivity.endDateTime"
            type="datetime"
            placeholder="报名截止时间"
            :disabled="!isEditable"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="报名限制" prop="allowedColleges">
          <el-input
            v-model="currentActivity.allowedColleges"
            :disabled="!isEditable"
            placeholder="点击右侧按钮选择学院"
            clearable
            style="width: 60%"
          >
            <template #append v-if="isEditable">
              <div style="display: flex; gap: 20px">
                <el-button type="primary" @click="showCollegeDialog = true"
                  >添加</el-button
                >
                <el-button type="danger" @click="resetCols">重置</el-button>
              </div>
            </template>
          </el-input>

          <el-dialog
            title="选择学院"
            v-model="showCollegeDialog"
            width="30%"
            @closed="onCollegeDialogClosed"
          >
            <el-checkbox-group v-model="selectedColleges">
              <el-checkbox
                v-for="college in availableColleges"
                :key="college.collegeId"
                :label="college.collegeName"
                :value="college.collegeName"
              />
            </el-checkbox-group>
            <template #footer>
              <el-button @click="showCollegeDialog = false">取消</el-button>
              <el-button type="primary" @click="confirmSelection"
                >确定</el-button
              >
            </template>
          </el-dialog>
        </el-form-item>
        <el-form-item label="额外说明" prop="extraInfo">
          <el-input
            v-model="currentActivity.extraInfo"
            :disabled="!isEditable"
            type="textarea"
            autosize
          />
        </el-form-item>
        <!-- 动态活动时间段 -->
        <el-divider content-position="left">活动时间段</el-divider>
        <div class="time-slot-container">
          <div
            v-for="(detail, index) in currentActivity.activityDetails"
            :key="index"
            class="time-slot"
          >
            <!-- 时间段头部 -->
            <div class="slot-header">
              <h4>时间段 {{ index + 1 }}</h4>
              <el-button
                v-if="isEditable"
                type="danger"
                circle
                size="small"
                :disabled="currentActivity.activityDetails.length === 1"
                @click="removeTimeSlot(index)"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>

            <!-- 时间范围 -->
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item
                  label="开始时间"
                  :prop="`activityDetails.${index}.beginTime`"
                  :rules="[
                    {
                      required: true,
                      message: '请选择开始时间',
                      trigger: 'change',
                    },
                  ]"
                >
                  <el-date-picker
                    v-model="detail.beginTime"
                    type="datetime"
                    placeholder="选择开始时间"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    :disabled="!isEditable"
                    style="width: 100%"
                    @change="(val) => handleTimeChange(index, 'beginTime', val)"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item
                  label="结束时间"
                  :prop="`activityDetails.${index}.endTime`"
                  :rules="timeRules(index)"
                >
                  <el-date-picker
                    v-model="detail.endTime"
                    type="datetime"
                    placeholder="选择结束时间"
                    value-format="YYYY-MM-DD HH:mm:ss"
                    :disabled="!isEditable"
                    style="width: 100%"
                    @change="(val) => handleTimeChange(index, 'endTime', val)"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <!-- 活动地点 -->
            <el-form-item
              label="活动地点"
              :prop="`activityDetails.${index}.activityLocation`"
              :rules="locationRules"
            >
              <el-input
                v-model="detail.activityLocation"
                :disabled="!isEditable"
                placeholder="请输入详细地址"
              />
            </el-form-item>

            <!-- 人数及时长 -->
            <el-row :gutter="24">
              <el-col :span="8">
                <el-form-item
                  label="志愿者人数"
                  :prop="`activityDetails.${index}.volunteerNum`"
                  :rules="volunteerRules"
                >
                  <el-input-number
                    v-model="detail.volunteerNum"
                    :min="1"
                    :max="1000"
                    :disabled="!isEditable"
                    controls-position="right"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="!isEditable">
                <el-form-item
                  label="志愿者剩余名额"
                  :prop="`activityDetails.${index}.volLeft`"
                >
                  <el-input-number
                    v-model="detail.volLeft"
                    :disabled="true"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item
                  label="志愿时/h"
                  :prop="`activityDetails.${index}.volunteerHours`"
                  :rules="hoursRules"
                >
                  <el-input-number
                    v-model="detail.volunteerHours"
                    :min="0.0"
                    :step="0.5"
                    :disabled="!isEditable"
                    controls-position="right"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>

            <el-row :gutter="24">
              <el-col :span="8">
                <el-form-item
                  label="负责人人数"
                  :prop="`activityDetails.${index}.leaderNum`"
                  :rules="leaderRules"
                >
                  <el-input-number
                    v-model="detail.leaderNum"
                    :min="0"
                    :max="50"
                    :disabled="!isEditable"
                    controls-position="right"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8" v-if="!isEditable">
                <el-form-item
                  label="负责人剩余名额"
                  :prop="`activityDetails.${index}.leaderLeft`"
                >
                  <el-input-number
                    v-model="detail.leaderLeft"
                    :disabled="true"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item
                  label="负责人额外时长"
                  :prop="`activityDetails.${index}.extraHours`"
                  :rules="hoursRules"
                >
                  <el-input-number
                    v-model="detail.extraHours"
                    :min="0.0"
                    :step="0.5"
                    :disabled="!isEditable"
                    controls-position="right"
                    style="width: 100%"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </div>

        <!-- 添加时间段按钮 -->
        <div
          style="display: flex; justify-content: center; align-items: center"
        >
          <el-button
            v-if="isEditable"
            type="primary"
            plain
            @click="addTimeSlot"
            icon="Plus"
          >
            添加时间段
          </el-button>
        </div>
      </el-form>

      <template #footer>
        <el-button @click="detailDialogVisible = false">取消</el-button>
        <el-button
          v-if="isEditable"
          type="primary"
          @click="handleSubmit"
          :loading="submitting"
        >
          {{ isEditMode ? "保存修改" : "创建活动" }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>
    
  <script setup>
import { Search, ElIconDelete, Delete, Plus } from "@element-plus/icons-vue";
import { ref, onMounted, reactive, computed, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {
  orgSearchActivities,
  updateActivityState,
  deleteActivity,
  getActivityById,
  getActivityDetail,
  addActivity,
  updateActivity,
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
  dateRange: null,
  state: null,
});
const statusOptions = ref([
  { value: "", label: "全部" },
  { value: 0, label: "招募中" },
  { value: 1, label: "已结束报名" },
  { value: 2, label: "未发布" },
  { value: 3, label: "活动结束" },
]);
const total = ref(0);
const loading = ref(false);
const submitting = ref(false);
const originalAllowedColleges = ref([]);
const detailDialogVisible = ref(false);
const isEditMode = ref(false);
const activityForm = ref(null);
const emptyActivity = () => ({
  activityId: "",
  activityName: "",
  activityDesc: "",
  organizationId: "",
  contactInfo: "",
  endDateTime: "",
  state: 2, // 新增时默认未发布状态
  allowedColleges: [],
  extraInfo: "",
  activityDetails: [
    {
      activityDetailId: "",
      beginTime: "",
      endTime: "",
      activityLocation: "",
      volunteerNum: 0,
      volLeft: 0,
      leaderNum: 0,
      leaderLeft: 0,
      volunteerHours: 0,
      extraHours: 0,
    },
  ],
});
watch(detailDialogVisible, (newVal) => {
  if (newVal) {
    originalAllowedColleges.value = JSON.parse(
      JSON.stringify(currentActivity.allowedColleges)
    );
  }
});
const resetCols = () => {
  currentActivity.allowedColleges = [...originalAllowedColleges.value];
};
const originalActivity = ref(null);

const deepCopyWithoutFields = (data, excludedFields = []) => {
  const copy = Array.isArray(data) ? [] : {};

  for (const key in data) {
    if (excludedFields.includes(key)) continue;
    // 递归处理对象和数组
    if (data[key] && typeof data[key] === "object") {
      copy[key] = deepCopyWithoutFields(data[key], excludedFields);
    } else {
      copy[key] = data[key];
    }
  }
  return copy;
};

const getDiff = (original, current) => {
  const changes = {};
  // 遍历所有键值
  for (const key in original) {
    // 只比较双方都存在的字段
    if (current.hasOwnProperty(key)) {
      if (original[key] !== current[key]) {
        changes[key] = current[key];
      }
    }
  }
  return changes;
};

// 当前活动数据
const currentActivity = reactive(emptyActivity());
// 计算是否可编辑
const isEditable = computed(
  () => !isEditMode.value || currentActivity.state === 2
);
// 打开详情对话框
const handleViewDetail = async (row) => {
  try {
    const res = await getActivityById(row.activityId);
    const res2 = await getActivityDetail(row.activityId);
    Object.assign(currentActivity, JSON.parse(JSON.stringify(res.data)));
    currentActivity.activityDetails = res2.data;
    originalActivity.value = deepCopyWithoutFields(currentActivity, [
      "activityDetails",
      "allowedColleges",
    ]);
    isEditMode.value = true;
    detailDialogVisible.value = true;
  } catch (err) {
    console.log(err);
  }
};
// 新增活动
const handleCreate = () => {
  Object.assign(currentActivity, emptyActivity());
  isEditMode.value = false;
  detailDialogVisible.value = true;
};

// 验证规则
const rules = {
  activityName: [
    { required: true, message: "请输入活动名称", trigger: "blur" },
  ],
  activityDesc: [
    { required: true, message: "请输入活动描述", trigger: "blur" },
  ],
  contactInfo: [{ required: true, message: "请输入联系方式", trigger: "blur" }],
  endDateTime: [
    { required: true, message: "请输入截止日期", trigger: "change" },
  ],
  allowedColleges: [
    { required: true, message: "请选择报名限制", trigger: ["change", "blur"] },
  ],
};
const timeRules = (index) => [
  {
    required: true,
    message: "请选择结束时间",
    trigger: "change",
  },
  {
    validator: (rule, value, callback) => {
      const beginTime = currentActivity.activityDetails[index].beginTime;
      if (value && beginTime && new Date(beginTime) >= new Date(value)) {
        callback(new Error("结束时间必须晚于开始时间"));
      } else {
        callback();
      }
    },
    trigger: "change",
  },
];

// 时间变化处理
const handleTimeChange = (index, field, value) => {
  currentActivity.activityDetails[index][field] = value;
};

const locationRules = [
  { required: true, message: "请填写活动地点", trigger: "blur" },
  { max: 80, message: "长度不超过80个字符", trigger: "blur" },
];

const volunteerRules = [
  {
    type: "number",
    required: true,
    min: 1,
    max: 1000,
    message: "人数需大于1",
    trigger: ["blur", "change"],
  },
];

const leaderRules = [
  {
    type: "number",
    required: true,
    min: 0,
    message: "人数需大于0",
    trigger: ["blur", "change"],
  },
];

const hoursRules = [
  {
    type: "number",
    required: true,
    min: 0.0,
    message: "请输入正确时长",
    trigger: ["blur", "change"],
  },
];

// 添加时间段
const addTimeSlot = () => {
  currentActivity.activityDetails.push({
    activityDetailId: "",
    activityId: currentActivity.activityId,
    beginTime: "",
    endTime: "",
    activityLocation: "",
    volunteerNum: 0,
    volLeft: 0,
    leaderNum: 0,
    leaderLeft: 0,
    volunteerHours: 0,
    extraHours: 0,
  });
};
// 删除时间段
const removeTimeSlot = (index) => {
  currentActivity.activityDetails.splice(index, 1);
};

const handleSubmit = async () => {
  try {
    await activityForm.value.validate();
    currentActivity.allowedColleges = availableCollegeIds();
    submitting.value = true;

    if (isEditMode.value) {
      let payload = getDiff(originalActivity.value, currentActivity);
      payload.activityId = currentActivity.activityId;
      payload.activityDetails = currentActivity.activityDetails;
      payload.allowedColleges = currentActivity.allowedColleges;
      console.log(payload);
      const res = await updateActivity(payload);
      ElMessage.success(res.msg || "修改成功");
    } else {
      const res = await addActivity(currentActivity);
      ElMessage.success(res.msg || "创建成功");
    }

    detailDialogVisible.value = false;
    fetchActivities(); // 刷新
  } catch (error) {
    console.error("提交失败:", error);
  } finally {
    submitting.value = false;
  }
};

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
    state: searchForm.value.state,
  };

  const response = await orgSearchActivities(params);
  activities.value = response.data?.records || [];
  total.value = response.data?.total || 0;
  loading.value = false;
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
const handleEndActivity = (row) => {
  const res = handleUpdateState(row, 3, "结束活动", "");
  if (res) row.state = 3;
};
const handlePublish = (row) => {
  const res = handleUpdateState(row, 0, "发布活动", "发布活动后不可修改活动！");
  if (res) row.state = 0;
};
const handleEndRecruit = (row) => {
  const res = handleUpdateState(row, 1, "结束招募", "结束招募后不可重新招募！");
  if (res) row.state = 1;
};
const handleUpdateState = async (row, newState, op, extraInfo) => {
  try {
    await ElMessageBox.confirm("确定要" + op + "吗？" + extraInfo, "操作确认", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
    const payload = {
      activityId: row.activityId,
      newState: newState,
    };
    const res = await updateActivityState(payload);
    ElMessage.success(op + "成功");
    return true;
    // await fetchActivities();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("操作失败，请检查网络连接");
      console.log(error);
    }
    return false;
  }
};
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm("确定要删除该活动吗", "操作确认", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
    const res = await deleteActivity(row.activityId);
    ElMessage.success(res.msg || "删除成功");
    await fetchActivities();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("操作失败，请检查网络连接");
      console.log(error);
    }
  }
};
const collegeList = ref([
  { collegeId: 1, collegeName: "文学院" },
  { collegeId: 2, collegeName: "计算机学院" },
  { collegeId: 3, collegeName: "其他" },
]);

const showCollegeDialog = ref(false);
const selectedColleges = ref([]);
// 计算可选的学院列表
const availableColleges = computed(() => {
  return collegeList.value.filter(
    (college) => !currentActivity.allowedColleges.includes(college.collegeName)
  );
});
const availableCollegeIds = () => {
  const res = collegeList.value
    .filter((item) =>
      currentActivity.allowedColleges.includes(item.collegeName)
    )
    .map((item) => item.collegeId);
  // console.log(res);
  return res;
};
const onCollegeDialogClosed = () => {
  selectedColleges.value = [];
};
// 处理选择确认
const confirmSelection = () => {
  if (selectedColleges.value.includes("无限制")) {
    currentActivity.allowedColleges = ["无限制"];
  } else {
    currentActivity.allowedColleges = [
      ...currentActivity.allowedColleges,
      ...selectedColleges.value,
    ];
  }
  selectedColleges.value = [];
  showCollegeDialog.value = false;
};
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