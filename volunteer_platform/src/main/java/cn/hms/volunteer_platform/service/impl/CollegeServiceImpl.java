package cn.hms.volunteer_platform.service.impl;

import cn.hms.volunteer_platform.common.Context;
import cn.hms.volunteer_platform.entity.po.College;
import cn.hms.volunteer_platform.entity.vo.CollegeVO;
import cn.hms.volunteer_platform.mapper.CollegeMapper;
import cn.hms.volunteer_platform.service.ICollegeService;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 学院表 服务实现类
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
@Service
public class CollegeServiceImpl extends ServiceImpl<CollegeMapper, College> implements ICollegeService {

    @Override
    public List<CollegeVO> getColleges() {
        List<College> colleges = this.list();
        return colleges.stream().map(c -> {
            CollegeVO vo = new CollegeVO();
            BeanUtil.copyProperties(c, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
