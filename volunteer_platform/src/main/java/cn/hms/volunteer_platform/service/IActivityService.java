package cn.hms.volunteer_platform.service;

import cn.hms.volunteer_platform.entity.dto.ActivityDto;
import cn.hms.volunteer_platform.entity.dto.PageParameter;
import cn.hms.volunteer_platform.entity.dto.RecruitInfoSearchParam;
import cn.hms.volunteer_platform.entity.po.Activity;
import cn.hms.volunteer_platform.entity.vo.ActivityVO;
import cn.hms.volunteer_platform.entity.vo.RecruitDetailInfo;
import cn.hms.volunteer_platform.entity.vo.RecruitInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 活动表 服务类
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
public interface IActivityService extends IService<Activity> {
    void add(ActivityDto dto);

    void update(ActivityDto dto);

    List<ActivityVO> listByOrgId();

    Page<ActivityVO> pageSearch(PageParameter pageParameter);

    ActivityVO getByActivityId(Long activityId);

    Page<ActivityVO> orgList(PageParameter pageParameter);

    void updateState(Long ActivityId, Integer newState);

    void delete(Long activityId);

    Page<RecruitInfo> getRecruitInfo(RecruitInfoSearchParam param);

    RecruitDetailInfo getRecruitDetailInfoById(Long activityId);
}
