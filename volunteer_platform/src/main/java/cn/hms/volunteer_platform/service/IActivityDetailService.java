package cn.hms.volunteer_platform.service;

import cn.hms.volunteer_platform.entity.dto.ActivityDetailDto;
import cn.hms.volunteer_platform.entity.po.ActivityDetail;
import cn.hms.volunteer_platform.entity.vo.ActivityDetailVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 活动详情表 服务类
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
public interface IActivityDetailService extends IService<ActivityDetail> {

    void add(ActivityDetailDto dto);

    void update(ActivityDetailDto dto);

    void delete(ActivityDetailDto dto);

    List<ActivityDetailVO> select(Long activityId);
}
