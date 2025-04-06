package cn.hms.volunteer_platform.service.impl;

import cn.hms.volunteer_platform.entity.dto.ActivityDetailDto;
import cn.hms.volunteer_platform.entity.po.Activity;
import cn.hms.volunteer_platform.entity.po.ActivityDetail;
import cn.hms.volunteer_platform.mapper.ActivityDetailMapper;
import cn.hms.volunteer_platform.service.IActivityDetailService;
import cn.hms.volunteer_platform.entity.vo.ActivityDetailVO;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动详情表 服务实现类
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@Service
public class ActivityDetailServiceImpl extends ServiceImpl<ActivityDetailMapper, ActivityDetail> implements IActivityDetailService {

    @Override
    @Transactional
    public void add(ActivityDetailDto dto) {
        ActivityDetail activityDetail = dto2po(dto);

        this.save(activityDetail);
        updateActivityUPTime(dto.getActivityId());
    }

    private static ActivityDetail dto2po(ActivityDetailDto dto) {
        ActivityDetail activityDetail = new ActivityDetail();
        BeanUtil.copyProperties(dto, activityDetail);
        activityDetail.setVolLeft(activityDetail.getVolunteerNum());
        activityDetail.setLeaderLeft(activityDetail.getLeaderNum());
        return activityDetail;
    }

    @Override
    @Transactional
    public void update(ActivityDetailDto dto) {
        ActivityDetail activityDetail = dto2po(dto);
        this.updateById(activityDetail);

        updateActivityUPTime(dto.getActivityId());
    }

    /**
     * 更新活动的更新时间
     */
    private void updateActivityUPTime(Long activityId) {
        Activity activity = new Activity();
        activity.setActivityId(activityId);
        Db.updateById(activity);
    }

    @Override
    @Transactional
    public void delete(ActivityDetailDto dto) {
        this.removeById(dto.getActivityDetailId());

        updateActivityUPTime(dto.getActivityId());
    }

    @Override
    public List<ActivityDetailVO> select(Long activityId) {
        List<ActivityDetail> list = this.lambdaQuery().eq(ActivityDetail::getActivityId, activityId)
                .orderByAsc(ActivityDetail::getBeginTime)
                .list();

        return list.stream().map(po -> {
            ActivityDetailVO vo = new ActivityDetailVO();
            BeanUtil.copyProperties(po, vo);
            return vo;
        }).collect(Collectors.toList());
    }

}
