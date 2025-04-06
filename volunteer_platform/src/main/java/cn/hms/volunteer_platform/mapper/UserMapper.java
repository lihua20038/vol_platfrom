package cn.hms.volunteer_platform.mapper;

import cn.hms.volunteer_platform.entity.po.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
