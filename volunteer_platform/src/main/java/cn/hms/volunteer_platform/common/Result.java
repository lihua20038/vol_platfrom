package cn.hms.volunteer_platform.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private Integer code; //编码：1表示成功，0或其它数字为失败

    private String msg; //提示信息

    private T data; //数据


    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.data = data;
        r.code = 1;
        return r;
    }

    public static <T> Result<T> successWithNoData(String msg) {
        return success(null, msg);
    }

    public static <T> Result<T> success(T data, String msg) {
        Result<T> r = new Result<>();
        r.data = data;
        r.code = 1;
        r.msg = msg;
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.msg = msg;
        r.code = 0;
        return r;
    }


}
