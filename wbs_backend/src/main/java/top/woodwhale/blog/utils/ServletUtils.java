package top.woodwhale.blog.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 获取 HttpServletRequest 和 HttpServletResponse 的工具类
 */
public class ServletUtils {
    /**
     * 获取HttpServletRequest对象
     * @return HttpServletRequest对象
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return (Objects.requireNonNull(requestAttributes)).getRequest();
    }

    /**
     * 获取HttpServletResponse对象
     * @return HttpServletResponse对象
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Objects.requireNonNull(requestAttributes).getResponse();
    }
}
