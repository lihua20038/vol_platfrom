package cn.hms.volunteer_platform.controller;

import cn.hms.volunteer_platform.entity.dto.OrgDto;
import cn.hms.volunteer_platform.common.Result;
import cn.hms.volunteer_platform.entity.vo.OrgVO;
import cn.hms.volunteer_platform.entity.vo.UserVO;
import cn.hms.volunteer_platform.service.IOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 组织表 前端控制器
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@RestController
@RequestMapping("/org")
public class OrganizationController {
    @Autowired
    private IOrganizationService service;

    @PostMapping("/register")
    public Result<String> add(@RequestBody OrgDto dto) {
        String msg = service.add(dto);
        if (!msg.equals("注册成功")) return Result.error(msg);
        return Result.successWithNoData(msg);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody OrgDto dto) {
        String jwt = service.login(dto);
        if (jwt != null) {
            return Result.success(jwt, "登录成功");
        }
        return Result.error("账号或密码错误");
    }

    @PutMapping("/update")
    public Result<String> updateInfo(@RequestBody OrgDto dto) {
        service.updateInfo(dto);
        return Result.successWithNoData("修改信息成功");
    }
    /*

       @DeleteMapping
        public Result<String> delete(HttpSession session) {
            userService.delete();
            session.removeAttribute("user");
            return Result.successWithNoData("删除账号成功");
        }
    */
    @PutMapping("/updatepw")
    public Result<String> updatePw(@RequestBody Map<String, String> map) {
        String msg = service.updatePw(map.get("oldPassword"), map.get("newPassword"));
        return Result.successWithNoData(msg);
    }

    @GetMapping("/info")
    public Result<OrgVO> getInfo() {
        OrgVO vo = service.getInfo();
        return Result.success(vo);
    }
}
