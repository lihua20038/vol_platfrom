package cn.hms.volunteer_platform.interceptor;

import cn.hms.volunteer_platform.common.Context;
import cn.hms.volunteer_platform.common.Result;
import cn.hms.volunteer_platform.util.JwtUtils;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * 登录拦截器
 * </p>
 *
 * @author lihua
 * @since 2025-03-22
 */
@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override //目标资源方法运行前运行, 返回true: 放行, 放回false, 不放行
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        String url = req.getRequestURL().toString();
        log.info("请求的url: {}", url);
        // 设置返回的Content-Type
        resp.setContentType("application/json;charset=utf-8");

/*        if (url.contains("login")) {
            log.info("登录操作, 放行...");
            return true;
        }*/

        String jwt = req.getHeader("Authorization");
        log.info("token: {}", jwt);
        if (!StringUtils.hasLength(jwt)) {
            log.info("请求头token为空,返回未登录的信息");
            setResp(resp, "您未登录");
            return false;
        }

        try {
            Claims claims = JwtUtils.parseJWT(jwt);
            Long userId = claims.get("userId", Long.class);
            if (userId != null) {
                Context.setCurrentId(userId);
            } else {
                Context.setCurrentId(claims.get("orgId", Long.class));
            }
        } catch (ExpiredJwtException e) {
            // token过期
            setResp(resp, "登录已过期，请重新登录");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            log.info("解析令牌失败, 返回未登录错误信息");
            setResp(resp, "未登录");
            return false;
        }

        log.info("令牌合法, 放行");

        return true;
    }

    @Override //目标资源方法运行后运行
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        System.out.println("postHandle ...");
    }

    @Override //视图渲染完毕后运行, 最后运行
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        System.out.println("afterCompletion...");
        Context.remove();
    }

    private static void setResp(HttpServletResponse resp, String msg) throws IOException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        resp.getWriter().write(JSONObject.toJSONString(Result.error(msg)));
    }
}
