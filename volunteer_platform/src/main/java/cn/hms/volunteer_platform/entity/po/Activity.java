package cn.hms.volunteer_platform.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 活动表
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@Data
@Accessors(chain = true)
@TableName("activity")
public class Activity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 活动ID
     */
    @TableId(value = "activity_id", type = IdType.AUTO)
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
     * 联系方式
     */
    private String contactInfo;

    /**
     * 报名截止时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    /**
     * 活动状态（0：招募中、1：已结束报名、2：未发布、3：活动结束）
     */
    private Integer state;

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
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 额外说明
     */
    private String extraInfo;
}
