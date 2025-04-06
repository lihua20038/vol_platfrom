package cn.hms.volunteer_platform.controller;


import cn.hms.volunteer_platform.common.Result;
import cn.hms.volunteer_platform.entity.dto.ActivityDto;
import cn.hms.volunteer_platform.entity.dto.PageParameter;
import cn.hms.volunteer_platform.entity.dto.RecruitInfoSearchParam;
import cn.hms.volunteer_platform.entity.vo.RecruitDetailInfo;
import cn.hms.volunteer_platform.entity.vo.RecruitInfo;
import cn.hms.volunteer_platform.service.IActivityService;
import cn.hms.volunteer_platform.entity.vo.ActivityVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 活动表 前端控制器
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@RestController
@Slf4j
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private IActivityService activityService;

    @PostMapping("/add")
    public Result<String> add(@RequestBody ActivityDto dto) {
        activityService.add(dto);
        return Result.successWithNoData("新增活动成功");
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody ActivityDto dto) {
        activityService.update(dto);
        return Result.successWithNoData("修改成功");
    }

    @PutMapping("/updateState")
    public Result<String> updateState(@RequestBody Map<String, String> map) {
        activityService.updateState(Long.valueOf(map.get("activityId")), Integer.valueOf(map.get("newState")));
        return Result.successWithNoData("更新活动状态成功");
    }

    // TODO 删除
    @DeleteMapping("/delete/{activityId}")
    public Result<String> delete(@PathVariable Long activityId) {
        activityService.delete(activityId);
        return Result.successWithNoData("删除成功");
    }

    // 查找指定组织发布的活动
    @GetMapping("/list")
    public Result<List<ActivityVO>> list() {
        return Result.success(activityService.listByOrgId());
    }

    /**
     * 分页多条件查询活动（可筛选时间段，活动名称，可参加活动）
     * ‌路径‌：/activity/search
     * ‌方法‌：GET
     * 请求体：
     * {
     * "page": "当前页码（默认1，若超出实际范围则自动修正为最大有效值）‌",
     * "size": "每页数据条数（默认10，最大值100）‌",
     * "activity_name": "活动名称模糊查询（支持部分匹配）‌",
     * "earliest_begin_time": "活动开始时间范围查询",
     * "latest_begin_time": "活动结束时间范围查询",
     * "college_id": "学院id，用于筛选学生可报名的活动",
     * "state": "学生用户查询时，该参数为0：招募中",
     * }
     *
     * @return 符合条件的活动列表
     */
    @PostMapping("/search")
    public Result<Page<ActivityVO>> search(@RequestBody PageParameter pageParameter) {
        Page<ActivityVO> data = activityService.pageSearch(pageParameter);
        return Result.success(data);
    }

    @PostMapping("/org/list")
    public Result<Page<ActivityVO>> list(@RequestBody PageParameter pageParameter) {
        Page<ActivityVO> data = activityService.orgList(pageParameter);
        return Result.success(data);
    }

    @GetMapping
    public String get() {
        return "ok";
    }

    @GetMapping("/{activityId}")
    public Result<ActivityVO> getActivityById(@PathVariable Long activityId) {
        ActivityVO vo = activityService.getByActivityId(activityId);
        return Result.success(vo);
    }

    @PostMapping("/org/recruitInfo")
    public Result<Page<RecruitInfo>> getRecruitInfo(@RequestBody RecruitInfoSearchParam param) {
        Page<RecruitInfo> info  = activityService.getRecruitInfo(param);
        return Result.success(info);
    }

    @GetMapping("/recruitDetail/{activityId}")
    public Result<RecruitDetailInfo> getRecruitDetailInfoById(@PathVariable Long activityId) {
        RecruitDetailInfo vo = activityService.getRecruitDetailInfoById(activityId);
        return Result.success(vo);
    }
}
