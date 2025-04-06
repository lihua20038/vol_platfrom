package cn.hms.volunteer_platform.entity.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 活动详情表
 * </p>
 *
 * @author lihua
 * @since 2025-03-09
 */
@Data
public class ActivityDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动详情ID
     */
    private Long activityDetailId;

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
     * 活动地点
     */
    private String activityLocation;

    /**
     * 志愿者人数
     */
    private Integer volunteerNum;

    /**
     * 志愿者剩余人数
     */
    private Integer volLeft;

    /**
     * 负责人人数
     */
    private Integer leaderNum;

    /**
     * 负责人剩余人数
     */
    private Integer leaderLeft;

    /**
     * 志愿时
     */
    private BigDecimal volunteerHours;

    /**
     * 负责人额外志愿时
     */
    private BigDecimal extraHours;

    /**
     * 活动ID
     */
    private Long activityId;

}
