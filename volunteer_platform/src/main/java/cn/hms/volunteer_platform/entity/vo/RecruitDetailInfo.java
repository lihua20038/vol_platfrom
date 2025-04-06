package cn.hms.volunteer_platform.entity.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author lihua
 * @since 2025/4/2
 */
@Data
public class RecruitDetailInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String activityName;

    private LocalDateTime endDateTime;
    /**
     * 各时段具体招募名单
     */
    private List<RecruitDetail> details;
}
