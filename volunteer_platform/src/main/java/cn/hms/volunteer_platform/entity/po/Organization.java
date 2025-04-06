package cn.hms.volunteer_platform.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 组织表
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@Data
@Accessors(chain = true)
@TableName("organization")
public class Organization implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 组织ID
     */
    @TableId(value = "organization_id", type = IdType.AUTO)
    private Long organizationId;

    /**
     * 组织名称(登录账号)
     */
    private String organizationName;

    /**
     * 学院名称
     */
    private String collegeName;

    /**
     * 学院ID
     */
    private Integer collegeId;

    /**
     * 密码
     */
    private String password;

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


}
