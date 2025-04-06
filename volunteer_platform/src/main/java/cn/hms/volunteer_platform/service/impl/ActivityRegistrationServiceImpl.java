package cn.hms.volunteer_platform.service.impl;

import cn.hms.volunteer_platform.common.Constants;
import cn.hms.volunteer_platform.common.Context;
import cn.hms.volunteer_platform.entity.dto.ActivityRegistrationDto;
import cn.hms.volunteer_platform.entity.dto.RecordSearchParameter;
import cn.hms.volunteer_platform.entity.dto.RecordUpdateStateDto;
import cn.hms.volunteer_platform.entity.po.*;
import cn.hms.volunteer_platform.entity.vo.ActivityRecordVO;
import cn.hms.volunteer_platform.exception.CustomException;
import cn.hms.volunteer_platform.mapper.ActivityRegistrationMapper;
import cn.hms.volunteer_platform.service.IActivityRegistrationService;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * 活动报名表 服务实现类
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@Service
public class ActivityRegistrationServiceImpl extends ServiceImpl<ActivityRegistrationMapper, ActivityRegistration> implements IActivityRegistrationService {

    @Override
    @Transactional
    public void add(ActivityRegistrationDto dto) {
        Long userId = Context.getCurrentId();
        Integer position = dto.getPosition();
        Long detailId = dto.getActivityDetailId();

        isExist(userId, detailId);
        ActivityRegistration registration = new ActivityRegistration();
        BeanUtil.copyProperties(dto, registration);

        registration.setUserId(userId);
        registration.setState(Constants.PartakeState.REGISTERED);

        // 扣减活动详情表的剩余人数
        boolean update = updateNum(detailId, position, -1);

        if (!update) {
            throw new CustomException("报名失败，请稍后重试");
        }
        Db.save(registration);
    }

    /**
     * 判断用户是否重复报名
     *
     * @param userId   -
     * @param detailId -
     */
    private static void isExist(Long userId, Long detailId) {
        ActivityRegistration record = Db.lambdaQuery(ActivityRegistration.class).eq(ActivityRegistration::getState, Constants.PartakeState.REGISTERED).eq(ActivityRegistration::getUserId, userId).eq(ActivityRegistration::getActivityDetailId, detailId).one();
        if (record != null) {
            throw new CustomException("您已报名该活动，请勿重复报名");
        }
    }

    /**
     * 加乐观锁修改人数
     */
    private static boolean updateNum(Long detailId, Integer position, int num) {
        ActivityDetail detail = Db.lambdaQuery(ActivityDetail.class).select(ActivityDetail::getLeaderLeft, ActivityDetail::getVolLeft).eq(ActivityDetail::getActivityDetailId, detailId).one();
        boolean update = false;
        // 使用当前人数作为乐观锁条件
        if (position.equals(Constants.VOL)) {
            update = Db.lambdaUpdate(ActivityDetail.class).eq(ActivityDetail::getActivityDetailId, detailId).eq(ActivityDetail::getVolLeft, detail.getVolLeft()).gt(num == -1, ActivityDetail::getVolLeft, 0).set(ActivityDetail::getVolLeft, detail.getVolLeft() + num).update();
        } else if (position.equals(Constants.LEADER)) {
            update = Db.lambdaUpdate(ActivityDetail.class).eq(ActivityDetail::getActivityDetailId, detailId).eq(ActivityDetail::getLeaderLeft, detail.getLeaderLeft()).gt(num == -1, ActivityDetail::getLeaderLeft, 0).set(ActivityDetail::getLeaderLeft, detail.getLeaderLeft() + num).update();
        }
        return update;
    }

    @Override
    @Transactional
    public void cancel(Long activityRegistrationId) {
        ActivityRegistration registration = this.lambdaQuery().eq(ActivityRegistration::getActivityRegistrationId, activityRegistrationId).select(ActivityRegistration::getActivityDetailId, ActivityRegistration::getPosition).one();
        // 回滚人数
        Long detailId = registration.getActivityDetailId();
        Integer position = registration.getPosition();
        boolean update = updateNum(detailId, position, 1);
        if (!update) {
            throw new RuntimeException("回滚人数失败");
        }
//        this.removeById(activityRegistrationId);
        this.lambdaUpdate().eq(ActivityRegistration::getActivityRegistrationId, activityRegistrationId).set(ActivityRegistration::getState, Constants.PartakeState.CANCEL).update();
    }

    @Override
    @Transactional
    public void reRegister(Long activityRegistrationId) {
        Long userId = Context.getCurrentId();
        ActivityRegistration registration = Db.lambdaQuery(ActivityRegistration.class).eq(ActivityRegistration::getActivityRegistrationId, activityRegistrationId).one();
        registration.setState(Constants.PartakeState.REGISTERED);
        Long detailId = registration.getActivityDetailId();
        isExist(userId, detailId);

        // 扣减活动详情表的剩余人数
        boolean update = updateNum(detailId, registration.getPosition(), -1);

        if (!update) {
            throw new CustomException("报名失败，请稍后重试");
        }
        registration.setCreateTime(LocalDateTime.now());
        Db.updateById(registration);
    }

    @Override
    public Page<ActivityRecordVO> myList(RecordSearchParameter param) {
        Integer state = param.getState();
        Long userId = Context.getCurrentId();
        Page<ActivityRegistration> page = new Page<>(param.getPageNo(), param.getPageSize());

        page = this.lambdaQuery().eq(ActivityRegistration::getUserId, userId).eq(state != null, ActivityRegistration::getState, state).orderByAsc(ActivityRegistration::getCreateTime).page(page);
        // 数据处理
        List<ActivityRegistration> registrations = page.getRecords();
        Page<ActivityRecordVO> pageVO = new Page<>();
        BeanUtil.copyProperties(page, pageVO, "records");
        if (registrations == null || registrations.isEmpty()) return pageVO;

        List<Activity> activities = Db.lambdaQuery(Activity.class).in(Activity::getActivityId, registrations.stream().map(ActivityRegistration::getActivityId).collect(Collectors.toSet())).list();
        HashMap<Long, Activity> id2activity = new HashMap<>();
        activities.forEach(a -> id2activity.put(a.getActivityId(), a));

        List<ActivityDetail> activityDetails = Db.lambdaQuery(ActivityDetail.class).in(ActivityDetail::getActivityDetailId, registrations.stream().map(ActivityRegistration::getActivityDetailId).collect(Collectors.toSet())).list();
        HashMap<Long, ActivityDetail> id2activityDetail = new HashMap<>();
        activityDetails.forEach(d -> id2activityDetail.put(d.getActivityDetailId(), d));

        List<Organization> orgs = Db.lambdaQuery(Organization.class).in(Organization::getOrganizationId, activities.stream().map(Activity::getOrganizationId).collect(Collectors.toSet())).select(Organization::getOrganizationId, Organization::getOrganizationName).list();
        HashMap<Long, Organization> id2org = new HashMap<>();
        orgs.forEach(o -> id2org.put(o.getOrganizationId(), o));

        List<ActivityRecordVO> vos = registrations.stream().map(r -> {
            ActivityRecordVO vo = new ActivityRecordVO();

            vo.setActivityRegistrationId(r.getActivityRegistrationId());
            Activity activity = id2activity.get(r.getActivityId());
            BeanUtil.copyProperties(activity, vo, "state");
            vo.setOrganizationName(id2org.get(activity.getOrganizationId()).getOrganizationName());

            ActivityDetail detail = id2activityDetail.get(r.getActivityDetailId());
            BeanUtil.copyProperties(detail, vo, "state");
            if (r.getState().equals(Constants.VOL)) {
                vo.setPosition("志愿者");
                vo.setVolHours(detail.getVolunteerHours());
            } else if (r.getState().equals(Constants.LEADER)) {
                vo.setPosition("负责人");
                vo.setVolHours(detail.getVolunteerHours().add(detail.getExtraHours()));
            }
            // 报名时间
            vo.setParticipateTime(r.getCreateTime());
            vo.setState(r.getState());
          /*  switch (r.getState()) {
                case Constants.PartakeState.REGISTERED:
                    vo.setState("报名成功");
                case Constants.PartakeState.CANCEL:
                    vo.setState("取消成功");
                case Constants.PartakeState.FAIL:
                    vo.setState("报名未参与");
                case Constants.PartakeState.SUCCESS:
                    vo.setState("报名且参加");
            }*/
            return vo;
        }).collect(Collectors.toList());

        pageVO.setRecords(vos);
        return pageVO;
    }

    @Override
    @Transactional
    public void updateState(RecordUpdateStateDto dto) {
        List<Long> ids = dto.getIds();
        Integer newState = dto.getNewState();
        this.lambdaUpdate().in(ActivityRegistration::getActivityRegistrationId, ids).set(ActivityRegistration::getState, newState).update();
        // 考勤通过，更新个人活动参与统计表
        if (newState.equals(Constants.PartakeState.SUCCESS)) {
            List<ActivityRegistration> registrations = this.listByIds(ids);
            ActivityDetail detail = Db.lambdaQuery(ActivityDetail.class).eq(ActivityDetail::getActivityDetailId, registrations.get(0).getActivityDetailId()).one();
            Map<Long, ActivityRegistration> id2reg = new HashMap<>(registrations.size());
            registrations.forEach(r -> id2reg.put(r.getUserId(), r));
            Set<Long> userIds = id2reg.keySet();

            List<ActivityParticipation> participations = Db.lambdaQuery(ActivityParticipation.class).select(ActivityParticipation::getUserId, ActivityParticipation::getTotalCount, ActivityParticipation::getTotalVolHours).in(ActivityParticipation::getUserId, userIds).list();

            participations.forEach(p -> {
                ActivityRegistration reg = id2reg.get(p.getUserId());
                BigDecimal hours = detail.getVolunteerHours();
                if (reg.getPosition().equals(Constants.LEADER)) {
                    hours = hours.add(detail.getExtraHours());
                }
                p.setTotalCount(p.getTotalCount() + 1); // 活动次数 + 1
                p.setTotalVolHours(p.getTotalVolHours().add(hours)); //志愿时增加
            });
            Db.updateBatchById(participations);
        }
    }
}
