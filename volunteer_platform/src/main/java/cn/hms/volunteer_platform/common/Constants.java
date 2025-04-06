package cn.hms.volunteer_platform.common;

import lombok.Data;

/**
 * @author lihua
 * @since 2025/3/13
 */
@Data
public class Constants {
    public enum ActivityState {

        RECRUIT(0, "招募中"),
        OVER(1, "已结束报名"),
        UNPUBLISH(2, "未发布"),
        END(3, "活动结束");

        private Integer code;
        private String info;

        ActivityState(Integer code, String info) {
            this.code = code;
            this.info = info;
        }

        public Integer getCode() {
            return code;
        }

        public String getInfo() {
            return info;
        }
    }

    /**
     * 无报名限制
     */
    public static final Integer UNLIMITED = 0;

    public static final Integer VOL = 0;
    public static final Integer LEADER = 1;

    public static final class PartakeState {
        /**
         * 报名成功
         */
        public static final int REGISTERED = 0;
        /**
         * 取消报名
         */
        public static final int CANCEL = 1;
        /**
         * 报名未参与
         */
        public static final int FAIL = 2;
        /**
         * 报名且参与
         */
        public static final int SUCCESS = 3;
    }
}
