package cn.hms.volunteer_platform.service;

import cn.hms.volunteer_platform.entity.dto.ActivityRegistrationDto;
import cn.hms.volunteer_platform.entity.dto.RecordSearchParameter;
import cn.hms.volunteer_platform.entity.dto.RecordUpdateStateDto;
import cn.hms.volunteer_platform.entity.po.ActivityRegistration;
import cn.hms.volunteer_platform.entity.vo.ActivityRecordVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 活动报名表 服务类
 * </p>
 *
 * @author lihua
 * @since 2025-03-05
 */
public interface IActivityRegistrationService extends IService<ActivityRegistration> {

    void add(ActivityRegistrationDto dto);

    void cancel(Long activityRegistrationId);

    void reRegister(Long activityRegistrationId);

    Page<ActivityRecordVO> myList(RecordSearchParameter parameter);

    void updateState(RecordUpdateStateDto dto);
}
