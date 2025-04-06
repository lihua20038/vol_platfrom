package cn.hms.volunteer_platform.service;

import cn.hms.volunteer_platform.entity.po.College;
import cn.hms.volunteer_platform.entity.vo.CollegeVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 学院表 服务类
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
public interface ICollegeService extends IService<College> {

    List<CollegeVO> getColleges();
}
