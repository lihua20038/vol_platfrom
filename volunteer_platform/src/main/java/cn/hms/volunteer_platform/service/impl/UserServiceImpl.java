package cn.hms.volunteer_platform.service.impl;

import cn.hms.volunteer_platform.common.Context;
import cn.hms.volunteer_platform.entity.dto.UserDto;
import cn.hms.volunteer_platform.entity.po.ActivityParticipation;
import cn.hms.volunteer_platform.entity.po.User;
import cn.hms.volunteer_platform.entity.vo.UserVO;
import cn.hms.volunteer_platform.exception.CustomException;
import cn.hms.volunteer_platform.mapper.UserMapper;
import cn.hms.volunteer_platform.service.IUserService;
import cn.hms.volunteer_platform.util.JwtUtils;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    @Transactional
    public String add(UserDto dto) {
        User u1 = this.lambdaQuery().eq(User::getUserId, dto.getUserId()).one();
        if (u1 != null) return "该学号已注册";
        User u2 = this.lambdaQuery().eq(User::getPhoneNumber, dto.getPhoneNumber()).one();
        if (u2 != null) return "该手机号已绑定其他账号";
        this.save(dto);
        // 添加活动记录表
        ActivityParticipation activityParticipation = new ActivityParticipation();
        activityParticipation.setUserId(dto.getUserId());
        activityParticipation.setTotalCount(0);
        activityParticipation.setTotalVolHours(new BigDecimal(0));
        Db.save(activityParticipation);
        return "注册成功";
    }

    @Override
    public String login(UserDto dto) {
        Long userId = dto.getUserId();
        String pw = dto.getPassword();
        User user = this.getById(userId);
        if (user == null || !pw.equals(user.getPassword())) {
            return null;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("name", user.getName());

        // 生成Jwt令牌
        return JwtUtils.generateJwt(map);
    }

    @Override
    public void delete() {
        Long userId = Context.getCurrentId();
        this.removeById(userId);
    }

    public UserVO getUserInfo() {
        Long userId = Context.getCurrentId();
        User user = this.getById(userId);
        UserVO vo = new UserVO();
        BeanUtil.copyProperties(user, vo);

        ActivityParticipation record = Db.lambdaQuery(ActivityParticipation.class).eq(ActivityParticipation::getUserId, userId).one();
        if (record != null) {
            vo.setTotalCount(record.getTotalCount());
            vo.setTotalVolHours(record.getTotalVolHours());
        }
        return vo;
    }

    @Override
    public void updateInfo(UserDto dto) {
        Long userId = Context.getCurrentId();
        if (dto.getPhoneNumber() != null) {
            User user = this.lambdaQuery().eq(dto.getPhoneNumber() != null, User::getPhoneNumber, dto.getPhoneNumber()).one();
            if (user != null) {
                throw new CustomException("该手机号已被绑定");
            }
        }
        dto.setUserId(userId);
/*        if (dto.getCollegeId() != null) {
            dto.setCollegeName(Context.mapToName(dto.getCollegeId()));
        }*/
        this.updateById(dto);
    }

    @Override
    public String updatePw(String oldPassword, String newPassword) {
        Long userId = Context.getCurrentId();
        boolean update = this.lambdaUpdate().eq(User::getUserId, userId).eq(User::getPassword, oldPassword).set(User::getPassword, newPassword).update();
        if (update) {
            return "密码修改成功";
        } else {
            return "旧密码错误";
        }
    }
}
