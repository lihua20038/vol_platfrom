package cn.hms.volunteer_platform.controller;


import cn.hms.volunteer_platform.common.Result;
import cn.hms.volunteer_platform.entity.dto.ActivityDetailDto;
import cn.hms.volunteer_platform.service.IActivityDetailService;
import cn.hms.volunteer_platform.entity.vo.ActivityDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 活动详情表 前端控制器
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@RestController
@Slf4j
@RequestMapping("/activity-detail")
public class ActivityDetailController {

    @Autowired
    private IActivityDetailService service;

    @PostMapping
    public Result<String> add(@RequestBody ActivityDetailDto dto) {
        service.add(dto);
        return Result.success("新增活动详情成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody ActivityDetailDto dto) {
        service.update(dto);
        return Result.success("更新活动详情成功");
    }

    @DeleteMapping
    public Result<String> delete(@RequestBody ActivityDetailDto dto) {
        service.delete(dto);
        return Result.success("删除活动详情成功");
    }

    /**
     * 查找活动详情
     */
    @GetMapping("/{activityId}")
    public Result<List<ActivityDetailVO>> select(@PathVariable Long activityId) {
        List<ActivityDetailVO> list = service.select(activityId);

        return Result.success(list);
    }
}
