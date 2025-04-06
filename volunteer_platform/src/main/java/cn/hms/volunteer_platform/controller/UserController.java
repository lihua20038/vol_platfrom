package cn.hms.volunteer_platform.controller;


import cn.hms.volunteer_platform.common.Context;
import cn.hms.volunteer_platform.common.Result;
import cn.hms.volunteer_platform.entity.dto.UserDto;
import cn.hms.volunteer_platform.entity.vo.UserVO;
import cn.hms.volunteer_platform.service.IUserService;
import cn.hms.volunteer_platform.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @PostMapping("/register")
    public Result<String> add(@RequestBody UserDto dto) {
        String msg = userService.add(dto);
        if (!msg.equals("注册成功")) return Result.error(msg);
        return Result.successWithNoData(msg);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody UserDto dto) {
        String jwt = userService.login(dto);
        if (jwt != null) {
            return Result.success(jwt, "登录成功");
        }
        return Result.error("账号或密码错误");
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.removeAttribute("user");
        Context.remove();
        return Result.successWithNoData("退出成功");
    }

    @PutMapping("/update")
    public Result<String> updateInfo(@RequestBody UserDto dto) {
        userService.updateInfo(dto);

        return Result.successWithNoData("修改信息成功");
    }

    @PutMapping("/updatepw")
    public Result<String> updatePw(@RequestBody Map<String, String> map) {
        String msg = userService.updatePw(map.get("oldPassword"), map.get("newPassword"));
        return Result.successWithNoData(msg);
    }

    @DeleteMapping
    public Result<String> delete(HttpSession session) {
        userService.delete();
        session.removeAttribute("user");
        return Result.successWithNoData("删除账号成功");
    }

    @GetMapping("/info")
    public Result<UserVO> getUserInfo() {
        UserVO userVO = userService.getUserInfo();
        return Result.success(userVO);
    }
}
