package top.woodwhale.blog.interceptor;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.utils.CookieUtils;
import top.woodwhale.blog.utils.RedisUtils;
import top.woodwhale.blog.utils.TextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

import static top.woodwhale.blog.utils.Constants.TimeValue.REDIS_5_SECS;
import static top.woodwhale.blog.utils.Constants.User.COOKIE_KEY_TOKEN;
import static top.woodwhale.blog.utils.Constants.User.REDIS_KEY_COMMIT_FREQUENT;

/**
 * 拦截器
 */

@Component
@Slf4j
public class ApiInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 处理事件前拦截
     * @param request request
     * @param response response
     * @param handler handler
     * @return boolean
     * @throws Exception e
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        // 仅仅是某一些需要提价的请求需要拦截
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 通过@CheckTooFrequentCommit注解来判断是否需要己写拦截
            CheckTooFrequentCommit methodAnnotation = handlerMethod.getMethodAnnotation(CheckTooFrequentCommit.class);
            if (methodAnnotation != null) {
                // 判断是否真的提交太频繁了
                String name  = handlerMethod.getMethod().getName();
                // 验证tokenKey
                String tokenKey = CookieUtils.getCookie(request, COOKIE_KEY_TOKEN);
                if (!TextUtils.isEmpty(tokenKey)) {
                    // 从Redis中读取数据
                    String hasCommit = (String) redisUtils.get(REDIS_KEY_COMMIT_FREQUENT + name + tokenKey);
                    if (hasCommit != null) {
                        response.setCharacterEncoding("utf-8");
                        response.setContentType("application/json");
                        PrintWriter writer = response.getWriter();
                        writer.write(new Gson().toJson(ResponseResult.FAILED("提交过于频繁，请稍后！")));
                        writer.flush();
                        return false;
                    } else {
                        // 如果没有提交过，那么就直接放行，同时设置redis中缓存
                        redisUtils.set(REDIS_KEY_COMMIT_FREQUENT + name + tokenKey,"true", REDIS_5_SECS);
                        return true;
                    }
                }
            }
        }
        return true;
    }
}
