package cn.hms.volunteer_platform.service;

import cn.hms.volunteer_platform.entity.dto.OrgDto;
import cn.hms.volunteer_platform.entity.po.Organization;
import cn.hms.volunteer_platform.entity.vo.OrgVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 组织表 服务类
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
public interface IOrganizationService extends IService<Organization> {

    String login(OrgDto dto);

    OrgVO getInfo();

    String add(OrgDto dto);

    String updatePw(String oldPassword, String newPassword);

    void updateInfo(OrgDto dto);
}
