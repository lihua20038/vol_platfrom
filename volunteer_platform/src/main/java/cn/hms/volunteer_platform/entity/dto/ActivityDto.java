package cn.hms.volunteer_platform.entity.dto;

import cn.hms.volunteer_platform.entity.po.Activity;
import cn.hms.volunteer_platform.entity.po.ActivityDetail;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lihua
 * @since 2025/3/5
 */
@Data
public class ActivityDto extends Activity {

    private List<ActivityDetail> activityDetails = new ArrayList<>();

    /**
     *
     */
    private List<Integer> allowedColleges = new ArrayList<>();
}
