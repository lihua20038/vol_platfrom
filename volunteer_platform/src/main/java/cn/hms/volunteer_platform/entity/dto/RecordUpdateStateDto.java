package cn.hms.volunteer_platform.entity.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lihua
 * @since 2025/4/3
 */
@Data
public class RecordUpdateStateDto  implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Long> ids;

    private Integer newState;
}
