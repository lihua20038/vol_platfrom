package cn.hms.volunteer_platform.entity.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author lihua
 * @since 2025/3/9
 */
@Data
public class PageParameter implements Serializable {
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
     * 活动名称
     */
    private String activityName;
    /**
     * 时间搜索条件的左区间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime leftTime;
    /**
     * 时间搜索条件的右区间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rightTime;
    /**
     * 学院id，用于筛选该学院的学生可报名的活动
     */
    private Integer collegeId;
    /**
     * 活动状态（0：招募中、1：已结束报名、2：未发布、3：活动结束）
     */
    private Integer state;
}
