package cn.hms.volunteer_platform.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 活动报名表
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@Data
@Accessors(chain = true)
@TableName("activity_registration")
public class ActivityRegistration implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 活动报名ID
     */
    @TableId(value = "activity_registration_id", type = IdType.AUTO)
    private Long activityRegistrationId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 活动详情ID
     */
    private Long activityDetailId;

    /**
     * 岗位（0:志愿者、1:负责人）
     */
    private Integer position;

    /**
     * 状态（0:报名成功、1:取消报名、2:报名未参与、3:报名且参与）
     */
    private Integer state;

    /**
     * 创建时间(报名时间)
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


}
