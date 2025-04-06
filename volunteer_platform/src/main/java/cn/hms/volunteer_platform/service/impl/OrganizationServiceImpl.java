package cn.hms.volunteer_platform.service.impl;

import cn.hms.volunteer_platform.common.Context;
import cn.hms.volunteer_platform.entity.dto.OrgDto;
import cn.hms.volunteer_platform.entity.po.ActivityParticipation;
import cn.hms.volunteer_platform.entity.po.College;
import cn.hms.volunteer_platform.entity.po.Organization;
import cn.hms.volunteer_platform.entity.po.User;
import cn.hms.volunteer_platform.entity.vo.OrgVO;
import cn.hms.volunteer_platform.entity.vo.UserVO;
import cn.hms.volunteer_platform.exception.CustomException;
import cn.hms.volunteer_platform.mapper.OrganizationMapper;
import cn.hms.volunteer_platform.service.IOrganizationService;
import cn.hms.volunteer_platform.util.JwtUtils;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * <p>
 * 组织表 服务实现类
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationMapper, Organization> implements IOrganizationService {

    @Override
    public String login(OrgDto dto) {
        String organizationName = dto.getOrganizationName();
        String password = dto.getPassword();
        Organization org = this.lambdaQuery().eq(Organization::getOrganizationName, organizationName).one();

        if (org == null || !password.equals(org.getPassword())) {
            return null;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("orgId", org.getOrganizationId());
        map.put("orgName", org.getOrganizationName());

        // 生成Jwt令牌
        return JwtUtils.generateJwt(map);
    }

    @Override
    public OrgVO getInfo() {
        Long orgId = Context.getCurrentId();
        Organization org = this.getById(orgId);
        OrgVO vo = new OrgVO();
        BeanUtil.copyProperties(org, vo);

        return vo;
    }

    @Override
    public String add(OrgDto dto) {
        String code = dto.getCode();
        College col = Db.lambdaQuery(College.class)
                .select(College::getCode)
                .eq(College::getCollegeId, dto.getCollegeId())
                .one();
        if (col == null || !col.getCode().equals(code)) { // 注册码不正确
            return "注册码不正确，请联系管理员获取";
        }
        Organization org = this.lambdaQuery().eq(Organization::getOrganizationName, dto.getOrganizationName()).one();
        if (org != null) return "该组织名称已存在";
        this.save(dto);
        return "注册成功";
    }

    @Override
    public String updatePw(String oldPassword, String newPassword) {
        Long orgId = Context.getCurrentId();
        boolean update = this.lambdaUpdate().eq(Organization::getOrganizationId, orgId).eq(Organization::getPassword, oldPassword).set(Organization::getPassword, newPassword).update();
        if (update) {
            return "密码修改成功";
        } else {
            return "旧密码错误";
        }
    }

    @Override
    public void updateInfo(OrgDto dto) {
        Long orgId = Context.getCurrentId();
        Organization org = this.lambdaQuery().eq(dto.getOrganizationName() != null, Organization::getOrganizationName, dto.getOrganizationName()).one();
        if (org != null) {
            throw new CustomException("该组织名称已存在");
        }
        dto.setOrganizationId(orgId);
        this.updateById(dto);
    }
}
