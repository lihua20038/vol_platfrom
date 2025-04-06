package cn.hms.volunteer_platform.entity.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lihua
 * @since 2025/4/2
 * 每个时间段招募明细（包括报名名单）
 */
@Data
public class RecruitDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 活动地点
     */
    private String activityLocation;

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
     * 报名名单
     */
    private List<RegVO> regList;
}
