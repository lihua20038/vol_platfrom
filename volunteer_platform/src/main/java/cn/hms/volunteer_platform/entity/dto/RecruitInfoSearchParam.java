package cn.hms.volunteer_platform.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lihua
 * @since 2025/4/2
 */
@Data
public class RecruitInfoSearchParam implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 当前页码
     */
    private Integer pageNo;
    /**
     * 每页数据条数
     */
    private Integer pageSize;
    /**
     * 活动状态（0：招募中、1：已结束报名、2：未发布、3：活动结束）
     */
    private Integer state;

    private String activityName;
}
