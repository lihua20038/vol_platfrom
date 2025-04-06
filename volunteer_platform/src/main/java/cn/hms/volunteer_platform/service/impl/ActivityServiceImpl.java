package cn.hms.volunteer_platform.service.impl;

import cn.hms.volunteer_platform.common.Constants;
import cn.hms.volunteer_platform.common.Context;
import cn.hms.volunteer_platform.entity.dto.ActivityDto;
import cn.hms.volunteer_platform.entity.dto.PageParameter;
import cn.hms.volunteer_platform.entity.dto.RecruitInfoSearchParam;
import cn.hms.volunteer_platform.entity.po.*;
import cn.hms.volunteer_platform.entity.vo.*;
import cn.hms.volunteer_platform.mapper.ActivityDetailMapper;
import cn.hms.volunteer_platform.mapper.ActivityMapper;
import cn.hms.volunteer_platform.service.IActivityService;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 活动表 服务实现类
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements IActivityService {
    @Autowired
    private ActivityDetailMapper activityDetailMapper;

    @Override
    @Transactional
    public void add(ActivityDto dto) {
        // dto转po
        Activity activity = new Activity();
        BeanUtil.copyProperties(dto, activity);
        activity.setOrganizationId(Context.getCurrentId());

        List<ActivityDetail> activityDetailList = dto.getActivityDetails();
        // 插入活动
        save(activity);

        // 插入活动限制
        Long activityId = activity.getActivityId();
        List<Integer> allowedColleges = dto.getAllowedColleges();
        if (allowedColleges.contains(0)) { // 无报名限制，只需插入一条
            Db.save(new ActivityAllowedCollege(activityId, 0));
        } else {
            List<ActivityAllowedCollege> colleges = allowedColleges.stream().map(collegeId -> new ActivityAllowedCollege(activityId, collegeId)).collect(Collectors.toList());
            Db.saveBatch(colleges);
        }
        // 插入活动详情

        activityDetailList.forEach(detail -> {
            detail.setActivityId(activityId);
            detail.setVolLeft(detail.getVolunteerNum());
            detail.setLeaderLeft(detail.getLeaderNum());
        });

        Db.saveBatch(activityDetailList);
    }

    @Override
    @Transactional
    public void update(ActivityDto dto) {
        Activity activity = new Activity();
        BeanUtil.copyProperties(dto, activity);

        boolean flag = this.updateById(activity);
        Long activityId = dto.getActivityId();

        // 更新活动报名限制  先删除再添加 fixme 优化
        Db.lambdaUpdate(ActivityAllowedCollege.class).eq(ActivityAllowedCollege::getActivityId, activityId).remove();

        List<Integer> allowedColleges = dto.getAllowedColleges();
        if (allowedColleges.contains(0)) { // 无报名限制，只需插入一条
            Db.save(new ActivityAllowedCollege(activityId, 0));
        } else {
            List<ActivityAllowedCollege> colleges = allowedColleges.stream().map(collegeId -> new ActivityAllowedCollege(activityId, collegeId)).collect(Collectors.toList());
            Db.saveBatch(colleges);
        }

        // 更新活动详情 简单方式：先删除全部旧的详情，再插入新的详情 fixme 优化
        Db.lambdaUpdate(ActivityDetail.class).eq(ActivityDetail::getActivityId, activityId).remove();
        List<ActivityDetail> details = dto.getActivityDetails();
        details.forEach(detail -> {
            detail.setActivityId(activityId);
            detail.setVolLeft(detail.getVolunteerNum());
            detail.setLeaderLeft(detail.getLeaderNum());
        });
        Db.saveBatch(details);
    }

    @Override
    public List<ActivityVO> listByOrgId() {
        Long orgId = Context.getCurrentId();
        List<Activity> activities = this.lambdaQuery().eq(Activity::getOrganizationId, orgId).list();

        String orgName = getOrgName(orgId);
        Map<Long, List<Integer>> map = getAllowedCollegeNames(activities);

        return activities.stream().map(activity -> {
            ActivityVO vo = new ActivityVO();
            vo.setOrganizationName(orgName);

            List<String> colNames = map.get(activity.getActivityId()).stream().map(id -> Context.mapToName(id)).collect(Collectors.toList());
            vo.setAllowedColleges(colNames);

            BeanUtil.copyProperties(activity, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 获取活动报名限制
     */
    private static Map<Long, List<Integer>> getAllowedCollegeNames(List<Activity> activities) {
        Set<Long> ids = activities.stream().map(Activity::getActivityId).collect(Collectors.toSet());
        List<ActivityAllowedCollege> list = Db.lambdaQuery(ActivityAllowedCollege.class).select(ActivityAllowedCollege::getCollegeId, ActivityAllowedCollege::getActivityId).in(ActivityAllowedCollege::getActivityId, ids).list();
        HashMap<Long, List<Integer>> map = new HashMap<>();
        list.forEach(a -> {
            List<Integer> colIds = map.getOrDefault(a.getActivityId(), new ArrayList<>());
            colIds.add(a.getCollegeId());
            map.put(a.getActivityId(), colIds);
        });
        return map;
    }

    private static String getOrgName(Long orgId) {
        return Db.lambdaQuery(Organization.class).select(Organization::getOrganizationName).eq(Organization::getOrganizationId, orgId).one().getOrganizationName();
    }


    @Override
    public Page<ActivityVO> pageSearch(PageParameter param) {
        String activityName = param.getActivityName();
        LocalDateTime leftTime = param.getLeftTime();
        LocalDateTime rightTime = param.getRightTime();
        Integer collegeId = param.getCollegeId();

        Page<Activity> activityPage = new Page<>(param.getPageNo(), param.getPageSize());

        // 1.根据时间条件筛选
        Set<Long> activityIds = filterByTime(leftTime, rightTime);
        // 2.筛选可参加的活动
        if (collegeId != null) {
            Set<Long> ids = filterByCollegeId(collegeId);
            activityIds = intersectionIds(activityIds, ids);
        }

        // 分页查找
        if (activityIds == null || !activityIds.isEmpty()) {
            activityPage = this.lambdaQuery()
                    .select(Activity::getActivityId, Activity::getActivityName, Activity::getOrganizationId, Activity::getEndDateTime)
                    .like(activityName != null && !activityName.equals(""), Activity::getActivityName, activityName)
                    .eq(Activity::getState, Constants.ActivityState.RECRUIT)
                    .in(activityIds != null, Activity::getActivityId, activityIds)
                    .orderByAsc(Activity::getEndDateTime)
                    .page(activityPage);
        }
        // po -> vo
        List<Activity> activities = activityPage.getRecords();
        if (activities == null || activities.isEmpty()) {
            Page<ActivityVO> voPage = new Page<>();
            BeanUtil.copyProperties(activityPage, voPage);
            return voPage;
        }
        // 1. orgId -> orgName
        Set<Long> orgIds = activities.stream().map(Activity::getOrganizationId).collect(Collectors.toSet());
        List<Organization> organizations = Db.lambdaQuery(Organization.class)
                .select(Organization::getOrganizationId, Organization::getOrganizationName)
                .in(Organization::getOrganizationId, orgIds)
                .list();

        Map<Long, String> id2name = new HashMap<>();
        if (organizations != null && !organizations.isEmpty()) {
            for (Organization o : organizations) {
                id2name.put(o.getOrganizationId(), o.getOrganizationName());
            }
        }
        Map<Long, List<Integer>> map = getAllowedCollegeNames(activities);

        List<ActivityVO> vos = activities.stream().map(activity -> {
            ActivityVO vo = new ActivityVO();
            vo.setOrganizationName(id2name.get(activity.getOrganizationId()));
            List<String> colNames = map.get(activity.getActivityId()).stream().map(id -> Context.mapToName(id)).collect(Collectors.toList());
            vo.setAllowedColleges(colNames);

            BeanUtil.copyProperties(activity, vo);
            return vo;
        }).collect(Collectors.toList());

        Page<ActivityVO> voPage = new Page<>();
        BeanUtil.copyProperties(activityPage, voPage, "records");
        voPage.setRecords(vos);

        return voPage;
    }

    @Override
    public ActivityVO getByActivityId(Long activityId) {
        Activity activity = this.getById(activityId);
        ArrayList<Activity> list = new ArrayList<>();
        list.add(activity);

        Map<Long, List<Integer>> map = getAllowedCollegeNames(list);
        List<String> colNames = map.get(activity.getActivityId()).stream().map(id -> Context.mapToName(id)).collect(Collectors.toList());

        ActivityVO vo = new ActivityVO();
        BeanUtil.copyProperties(activity, vo);
        vo.setAllowedColleges(colNames);
        vo.setOrganizationName(getOrgName(activity.getOrganizationId()));
        return vo;
    }

    @Override
    public Page<ActivityVO> orgList(PageParameter param) {
        String activityName = param.getActivityName();
        Integer state = param.getState();
        LocalDateTime leftTime = param.getLeftTime();
        LocalDateTime rightTime = param.getRightTime();
        Long orgId = Context.getCurrentId();

        Page<Activity> activityPage = new Page<>(param.getPageNo(), param.getPageSize());

        // 1.根据时间条件筛选
        Set<Long> activityIds = filterByTime(leftTime, rightTime);

        // 分页查找
        if (activityIds == null || !activityIds.isEmpty()) {
            activityPage = this.lambdaQuery()
                    .select(Activity::getActivityId, Activity::getActivityName, Activity::getEndDateTime, Activity::getPublishTime, Activity::getState)
                    .like(activityName != null && !activityName.equals(""), Activity::getActivityName, activityName)
                    .eq(state != null, Activity::getState, state)
                    .eq(Activity::getOrganizationId, orgId)
                    .in(activityIds != null, Activity::getActivityId, activityIds)
                    .orderByDesc(Activity::getPublishTime)
                    .page(activityPage);
        }
        // po -> vo
        List<Activity> activities = activityPage.getRecords();
        if (activities == null || activities.isEmpty()) {
            Page<ActivityVO> voPage = new Page<>();
            BeanUtil.copyProperties(activityPage, voPage);
            return voPage;
        }
        Page<ActivityVO> voPage = new Page<>();
        Map<Long, List<Integer>> map = getAllowedCollegeNames(activities);
        try {
            List<ActivityVO> vos = activities.stream().map(activity -> {
                ActivityVO vo = new ActivityVO();
                List<String> colNames = map.get(activity.getActivityId()).stream().map(id -> Context.mapToName(id)).collect(Collectors.toList());
                vo.setAllowedColleges(colNames);

                BeanUtil.copyProperties(activity, vo);
                return vo;
            }).collect(Collectors.toList());

            BeanUtil.copyProperties(activityPage, voPage, "records");
            voPage.setRecords(vos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return voPage;
    }

    @Override
    public void updateState(Long activityId, Integer newState) {
        this.lambdaUpdate().eq(Activity::getActivityId, activityId)
                .set(Activity::getState, newState)
                .set(newState.equals(Constants.ActivityState.RECRUIT.getCode()), Activity::getPublishTime, LocalDateTime.now())
                .update();
    }

    @Override
    @Transactional
    public void delete(Long activityId) {
        Db.lambdaUpdate(ActivityDetail.class).eq(ActivityDetail::getActivityId, activityId).remove();
        Db.lambdaUpdate(ActivityAllowedCollege.class).eq(ActivityAllowedCollege::getActivityId, activityId).remove();
        this.removeById(activityId);
    }

    @Override
    public Page<RecruitInfo> getRecruitInfo(RecruitInfoSearchParam param) {
        Integer state = param.getState();
        String activityName = param.getActivityName();
        Long orgId = Context.getCurrentId();

        List<Integer> states = Arrays.asList(Constants.ActivityState.RECRUIT.getCode(), Constants.ActivityState.END.getCode(), Constants.ActivityState.OVER.getCode());
        Page<Activity> activityPage = new Page<>(param.getPageNo(), param.getPageSize());
        activityPage = this.lambdaQuery()
                .select(Activity::getActivityId, Activity::getActivityName, Activity::getPublishTime, Activity::getState)
                .eq(Activity::getOrganizationId, orgId)
                .eq(state != null, Activity::getState, state)
                .in(state == null, Activity::getState, states)
                .like(activityName != null && !activityName.equals(""), Activity::getActivityName, activityName)
                .orderByDesc(Activity::getPublishTime)
                .page(activityPage);

        List<Activity> activities = activityPage.getRecords();
        Page<RecruitInfo> info = new Page<>();
        // 无数据
        if (activities == null || activities.isEmpty()) {
            BeanUtil.copyProperties(activityPage, info);
            return info;
        }

        // 数据处理
        List<Long> ids = activities.stream().map(Activity::getActivityId).collect(Collectors.toList());

        List<Map<String, Object>> maps = activityDetailMapper.groupByActivityId(ids);

        List<RecruitInfo> infos = activities.stream().map(a -> {
            RecruitInfo recruitInfo = new RecruitInfo();
            BeanUtil.copyProperties(a, recruitInfo);
            for (Map<String, Object> map : maps) {
                if (map.get("activity_id").equals(a.getActivityId())) {
                    BigDecimal totalLeaderNum = (BigDecimal) map.get("total_leader_num");
                    BigDecimal totalVolunteerNum = (BigDecimal) map.get("total_volunteer_num");
                    BigDecimal totalVolLeft = (BigDecimal) map.get("total_vol_left");
                    BigDecimal totalLeaderLeft = (BigDecimal) map.get("total_leader_left");

                    recruitInfo.setLeaderTotal(totalLeaderNum.intValue());
                    recruitInfo.setVolTotal(totalVolunteerNum.intValue());
                    recruitInfo.setVolNum(recruitInfo.getVolTotal() - totalVolLeft.intValue());
                    recruitInfo.setLeaderNum(recruitInfo.getLeaderTotal() - totalLeaderLeft.intValue());
                    break;
                }
            }
            return recruitInfo;
        }).collect(Collectors.toList());

        info.setRecords(infos);
        BeanUtil.copyProperties(activityPage, info, "records");
        return info;
    }

    @Override
    public RecruitDetailInfo getRecruitDetailInfoById(Long activityId) {
        Activity activity = this.getById(activityId);
        RecruitDetailInfo recruitDetailInfo = new RecruitDetailInfo();
        recruitDetailInfo.setEndDateTime(activity.getEndDateTime());
        recruitDetailInfo.setActivityName(activity.getActivityName());

        List<ActivityDetail> details = Db.lambdaQuery(ActivityDetail.class).eq(ActivityDetail::getActivityId, activityId).list();

        List<ActivityRegistration> registrations = Db.lambdaQuery(ActivityRegistration.class)
                .eq(ActivityRegistration::getActivityId, activityId)
                .in(ActivityRegistration::getState, Arrays.asList(0, 2, 3))
                .list();
        // 没有报名记录直接返回
        if (registrations == null || registrations.isEmpty()) {
            List<RecruitDetail> list = details.stream().map(d -> {
                RecruitDetail recruitDetail = new RecruitDetail();
                BeanUtil.copyProperties(d, recruitDetail);

                return recruitDetail;
            }).collect(Collectors.toList());
            recruitDetailInfo.setDetails(list);
            return recruitDetailInfo;
        }

        Set<Long> userIds = registrations.stream().map(ActivityRegistration::getUserId).collect(Collectors.toSet());

        List<User> users = Db.lambdaQuery(User.class).in(User::getUserId, userIds).list();
        Map<Long, User> id2user = new HashMap<>();
        users.forEach(u -> id2user.put(u.getUserId(), u));

        Map<Long, List<ActivityRegistration>> detailId2regs = new HashMap<>();
        registrations.forEach(r -> {
            List<ActivityRegistration> value = detailId2regs.computeIfAbsent(r.getActivityDetailId(), k -> new ArrayList<>());
            value.add(r);
        });

        List<RecruitDetail> recruitDetails = details.stream().map(d -> {
            RecruitDetail recruitDetail = new RecruitDetail();
            BeanUtil.copyProperties(d, recruitDetail);

            List<ActivityRegistration> regs = detailId2regs.get(d.getActivityDetailId());
            List<RegVO> regList = null;
            if (regs != null && !regs.isEmpty()) { // 该时间段有报名才处理
                regList = regs.stream().map(reg -> {
                    RegVO regVO = new RegVO();
                    User user = id2user.get(reg.getUserId());
                    regVO.setName(user.getName());
                    regVO.setContactInfo(user.getContactInfo());
                    regVO.setCollegeName(user.getCollegeName());
                    regVO.setState(reg.getState());
                    regVO.setPosition(reg.getPosition().equals(Constants.VOL) ? "志愿者" : "负责人");
                    regVO.setActivityRegistrationId(reg.getActivityRegistrationId());

                    return regVO;
                }).collect(Collectors.toList());
            }
            recruitDetail.setRegList(regList);

            return recruitDetail;
        }).collect(Collectors.toList());

        recruitDetailInfo.setDetails(recruitDetails);
        return recruitDetailInfo;
    }

    private static Set<Long> filterByCollegeId(Integer collegeId) {
        List<ActivityAllowedCollege> list = Db.lambdaQuery(ActivityAllowedCollege.class).select(ActivityAllowedCollege::getActivityId).eq(ActivityAllowedCollege::getCollegeId, Constants.UNLIMITED).or().eq(ActivityAllowedCollege::getCollegeId, collegeId).list();
        return list.stream().map(ActivityAllowedCollege::getActivityId).collect(Collectors.toSet());
    }

    /**
     * 筛选符号时间段的活动id
     *
     * @param leftTime  时间段左端点
     * @param rightTime 时间段右端点
     * @return 活动id集合
     */
    private static Set<Long> filterByTime(LocalDateTime leftTime, LocalDateTime rightTime) {
        if (leftTime != null && rightTime != null) {
            List<ActivityDetail> details = Db.lambdaQuery(ActivityDetail.class).select(ActivityDetail::getActivityId).ge(ActivityDetail::getBeginTime, leftTime)  // leftTime <= beginTime <= rightTime
                    .le(ActivityDetail::getBeginTime, rightTime).list();
            if (details != null) {
                return details.stream().map(ActivityDetail::getActivityId).collect(Collectors.toSet());
            }
        }
        // 没有时间条件
        return null;
    }

    private static Set<Long> intersectionIds(Set<Long> ids1, Set<Long> ids2) {
        // ids1 == null 表示无时间条件， ids1 == 空 表示无符号时间的活动，ids2 == 空表示无满足报名条件的活动
        if (ids1 == null) return ids2;
        return ids1.stream().filter(ids2::contains).collect(Collectors.toSet());
    }
}
