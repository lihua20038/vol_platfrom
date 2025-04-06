package cn.hms.volunteer_platform.entity.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lihua
 * @since 2025/3/7
 */
@Data
public class ActivityVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动描述
     */
    private String activityDesc;

    /**
     * 发布组织ID
     */
    private Long organizationId;

    /**
     * 发布组织名称
     */
    private String organizationName;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 报名截止时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    /**
     * 发布时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;

    /**
     * 报名限制 -- 仅指定学院学生可参加该活动
     */
    private List<String> allowedColleges;

    /**
     * 活动状态（0：招募中、1：已结束报名、2：未发布、3：活动结束）
     */
    private Integer state;

    /**
     * 额外说明
     */
    private String extraInfo;
}
