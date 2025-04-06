package cn.hms.volunteer_platform.controller;


import cn.hms.volunteer_platform.common.Result;
import cn.hms.volunteer_platform.entity.vo.CollegeVO;
import cn.hms.volunteer_platform.service.ICollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 学院表 前端控制器
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@RestController
@RequestMapping("/college")
public class CollegeController {
    @Autowired
    private ICollegeService service;

    @GetMapping
    public Result<List<CollegeVO>> getColleges() {
        List<CollegeVO> collegeVOS = service.getColleges();

        return Result.success(collegeVOS);
    }
}
