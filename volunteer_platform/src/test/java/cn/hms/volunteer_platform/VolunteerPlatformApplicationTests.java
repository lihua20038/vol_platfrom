package cn.hms.volunteer_platform;

import cn.hms.volunteer_platform.entity.vo.RecruitDetailInfo;
import cn.hms.volunteer_platform.mapper.ActivityDetailMapper;
import cn.hms.volunteer_platform.service.IActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
class VolunteerPlatformApplicationTests {

    @Autowired
    ActivityDetailMapper mapper;

    @Autowired
    IActivityService activityService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testGroupBy() {
        List<Long> ids = Arrays.asList(7L, 9L);
        List<Map<String, Object>> result = mapper.groupByActivityId(ids);

        System.out.println(result);
    }

    @Test
    public void testRecruitDetailInfoById() {
        RecruitDetailInfo result = activityService.getRecruitDetailInfoById(7L);

        System.out.println(result.getActivityName());
        System.out.println(result.getEndDateTime());
        result.getDetails().forEach(System.out::println);
    }

}
