package cn.hms.volunteer_platform.common;

import cn.hms.volunteer_platform.entity.po.College;
import cn.hms.volunteer_platform.mapper.CollegeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lihua
 * @since 2025/3/10
 */
@Configuration
public class Context {

    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void remove() {
        threadLocal.remove();
    }

    @Autowired
    private CollegeMapper collegeMapper;
    private static final Map<Integer, String> colId2colName = new HashMap<>();

    @PostConstruct
    public void init() {
        List<College> colleges = collegeMapper.selectList(null);
        colleges.forEach(college -> colId2colName.put(college.getCollegeId(), college.getCollegeName()));
        colId2colName.put(0, "无限制");
    }

    public static String mapToName(Integer id) {
        return colId2colName.get(id);
    }
}
