package cn.hms.volunteer_platform.service;

import cn.hms.volunteer_platform.entity.dto.UserDto;
import cn.hms.volunteer_platform.entity.po.User;
import cn.hms.volunteer_platform.entity.vo.UserVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
public interface IUserService extends IService<User> {

    String add(UserDto dto);

    String login(UserDto dto);

    void delete();

    UserVO getUserInfo();

    void updateInfo(UserDto dto);

    String updatePw(String oldPassword, String newPassword);
}
