package cn.hms.volunteer_platform.entity.dto;

import cn.hms.volunteer_platform.entity.po.Organization;

/**
 * @author lihua
 * @since 2025/3/29
 */
public class OrgDto extends Organization {
    /**
     * 注册码，需填写对应学院的注册码才能注册
     */
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
