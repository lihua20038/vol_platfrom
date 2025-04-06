package cn.hms.volunteer_platform.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lihua
 * @since 2025/3/29
 */
@Data
public class OrgVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 组织ID
     */
    private Long organizationId;

    /**
     * 组织名称(登录账号)
     */
    private String organizationName;

    /**
     * 学院名称
     */
    private String collegeName;

    /**
     * 学院ID
     */
    private Integer collegeId;
}
