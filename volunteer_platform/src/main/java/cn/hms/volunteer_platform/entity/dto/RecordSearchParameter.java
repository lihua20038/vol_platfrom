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
public class RecordSearchParameter implements Serializable {
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
     * 状态（0:报名成功、1:取消报名、2:报名未参与、3:报名且参与）
     */
    private Integer state;
}
