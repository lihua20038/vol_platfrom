package cn.hms.volunteer_platform.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lihua
 * @since 2025/4/2
 * 报名表
 */
@Data
public class RegVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long activityRegistrationId;
    /**
     * 报名岗位
     */
    private String position;
    /**
     * 姓名
     */
    private String name;
    /**
     * 所属学院
     */
    private String collegeName;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 报名状态
     */
    private Integer state;
}
