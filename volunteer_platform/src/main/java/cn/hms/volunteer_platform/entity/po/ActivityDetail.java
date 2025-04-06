package cn.hms.volunteer_platform.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
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
 * @since 2025-03-05
 */
@Data
@Accessors(chain = true)
@TableName("activity_detail")
public class ActivityDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动详情ID
     */
    @TableId(value = "activity_detail_id", type = IdType.AUTO)
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
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 活动ID
     */
    private Long activityId;


}
