package cn.hms.volunteer_platform.entity.dto;

import cn.hms.volunteer_platform.entity.po.ActivityRegistration;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 活动报名dto
 * </p>
 *
 * @author lihua
 * @since 2025-03-09
 */
@Data
public class ActivityRegistrationDto implements Serializable {
    private static final long serialVersionUID = 1L;

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

}
