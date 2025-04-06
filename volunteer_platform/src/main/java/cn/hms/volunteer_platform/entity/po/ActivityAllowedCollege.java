package cn.hms.volunteer_platform.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 活动报名限制表
 * </p>
 *
 * @author lihua
 * @since 2025-03-13
 */
@Data
@Accessors(chain = true)
@TableName("activity_allowed_college")
public class ActivityAllowedCollege implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 学院ID
     */
    private Integer collegeId;

    public ActivityAllowedCollege(Long activityId, Integer collegeId) {
        this.activityId = activityId;
        this.collegeId = collegeId;
    }

    public ActivityAllowedCollege(Integer collegeId) {
        this.collegeId = collegeId;
    }

    public ActivityAllowedCollege(Long id, Long activityId, Integer collegeId) {
        this.id = id;
        this.activityId = activityId;
        this.collegeId = collegeId;
    }
}
