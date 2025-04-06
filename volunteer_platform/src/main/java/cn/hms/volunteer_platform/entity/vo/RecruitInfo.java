package cn.hms.volunteer_platform.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author lihua
 * @since 2025/4/2
 * 招募情况
 */
@Data
public class RecruitInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long activityId;

    private String activityName;

    private LocalDateTime publishTime;
    /**
     * 已招志愿者人数
     */
    private Integer volNum;
    /**
     * 志愿者总需人数
     */
    private Integer volTotal;
    /**
     * 已招负责人人数
     */
    private Integer leaderNum;
    /**
     * 负责人总需人数
     */
    private Integer leaderTotal;
    /**
     * 活动状态
     */
    private Integer state;
}
