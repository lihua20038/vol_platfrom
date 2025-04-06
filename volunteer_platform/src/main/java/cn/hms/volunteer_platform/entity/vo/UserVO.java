package cn.hms.volunteer_platform.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@Data
public class UserVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String name;

    private String gender;

    /**
     * 学院名称
     */
    private String collegeName;

    private Integer collegeId;

    /**
     * i志愿绑定手机号
     */
    private Long phoneNumber;

    /**
     * 联系方式
     */
    private String contactInfo;


    /**
     * 总参与次数
     */
    private Integer totalCount;

    /**
     * 总志愿时
     */
    private BigDecimal totalVolHours;
}
