package cn.hms.volunteer_platform.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lihua
 * @since 2025/3/21
 */

@Data
public class CollegeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学院ID
     */
    private Integer collegeId;

    /**
     * 学院名称
     */
    private String collegeName;

}

