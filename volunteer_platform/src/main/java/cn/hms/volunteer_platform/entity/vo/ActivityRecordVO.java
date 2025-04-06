package cn.hms.volunteer_platform.entity.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动记录vo
 *
 * @author lihua
 * @since 2025/3/25
 */
@Data
public class ActivityRecordVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 活动报名ID
     */
    private Long activityRegistrationId;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 活动地点
     */
    private String activityLocation;

    /**
     * 报名岗位
     */
    private String position;

    /**
     * 活动开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    /**
     * 活动结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 参与状态 （0:报名成功、1:取消报名、2:报名未参与、3:报名且参与）
     */
    private Integer state;

    /**
     * 发布组织名称
     */
    private String organizationName;

    /**
     * 活动描述
     */
    private String activityDesc;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 额外说明
     */
    private String extraInfo;

    /**
     * 志愿时（负责人可能有额外）
     */
    private BigDecimal volHours;

    /**
     * 报名时间
     */
    private LocalDateTime participateTime;
}
