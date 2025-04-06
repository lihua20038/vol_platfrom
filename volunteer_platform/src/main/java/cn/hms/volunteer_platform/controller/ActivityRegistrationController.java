package cn.hms.volunteer_platform.controller;


import cn.hms.volunteer_platform.common.Result;
import cn.hms.volunteer_platform.entity.dto.ActivityRegistrationDto;
import cn.hms.volunteer_platform.entity.dto.RecordSearchParameter;
import cn.hms.volunteer_platform.entity.dto.RecordUpdateStateDto;
import cn.hms.volunteer_platform.entity.vo.ActivityRecordVO;
import cn.hms.volunteer_platform.service.IActivityRegistrationService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 活动报名表 前端控制器
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@RestController
@RequestMapping("/activity-registration")
public class ActivityRegistrationController {

    @Autowired
    private IActivityRegistrationService service;

    @PostMapping("/participate")
    public Result<String> add(@RequestBody ActivityRegistrationDto dto) {
        service.add(dto);
        return Result.successWithNoData("报名活动成功");
    }

    @PutMapping("/cancel/{activityRegistrationId}")
    public Result<String> cancel(@PathVariable Long activityRegistrationId) {
        service.cancel(activityRegistrationId);
        return Result.successWithNoData("取消报名成功");
    }

    @PutMapping("/reregister/{activityRegistrationId}")
    public Result<String> reRegister(@PathVariable Long activityRegistrationId) {
        service.reRegister(activityRegistrationId);
        return Result.successWithNoData("重新报名成功");
    }

    @PostMapping("/list")
    public Result<Page<ActivityRecordVO>> pageSearch(@RequestBody RecordSearchParameter parameter) {
        return Result.success(service.myList(parameter));
    }

    @PutMapping("/updateState")
    public Result<String> updateState(@RequestBody RecordUpdateStateDto dto) {
        service.updateState(dto);
        return Result.successWithNoData("操作成功");
    }
}
