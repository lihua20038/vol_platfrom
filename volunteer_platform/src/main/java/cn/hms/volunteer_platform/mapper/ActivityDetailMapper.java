package cn.hms.volunteer_platform.mapper;

import cn.hms.volunteer_platform.entity.po.ActivityDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 活动详情表 Mapper 接口
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@Mapper
public interface ActivityDetailMapper extends BaseMapper<ActivityDetail> {

    /**
     * 按照 activityId 分组，查询每个 activityId 的志愿者人数总和、剩余人数总和、负责人总人数、负责人剩余人数总和
     * 仅查询 activityId 在给定的 ids 集合中的记录
     *
     * @param ids activityId 的集合
     * @return 查询结果
     */
    @Select("<script>" +
            "SELECT activity_id, " +
            "SUM(volunteer_num) AS total_volunteer_num, " +
            "SUM(vol_left) AS total_vol_left, " +
            "SUM(leader_num) AS total_leader_num, " +
            "SUM(leader_left) AS total_leader_left " +
            "FROM activity_detail " +
            "WHERE activity_id IN " +
            "<foreach item='item' collection='ids' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "GROUP BY activity_id" +
            "</script>")
    List<Map<String, Object>> groupByActivityId(@Param("ids") List<Long> ids);
}
