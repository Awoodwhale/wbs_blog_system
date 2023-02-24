package top.woodwhale.blog.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static top.woodwhale.blog.utils.Constants.TimeValue.COOKIE_1_YEAR;

/**
 * Cookie工具类
 */
public class CookieUtils {
    // 默认域名
    public static final String domain = "localhost" ;

    /**
     * 设置默认1年的cookie
     * @param response response
     * @param key key
     * @param value value
     */
    public static void setUpCookies(HttpServletResponse response, String key, String value) {
        setUpCookies(response,key,value,COOKIE_1_YEAR);
    }

    /**
     * 设置time时间的cookie，单位为s
     * @param response response
     * @param key key
     * @param value value
     * @param time time，单位为s
     */
    public static void setUpCookies(HttpServletResponse response, String key, String value, int time) {
        Cookie cookie = new Cookie(key,value);
        cookie.setPath("/");
//        cookie.setDomain(domain);
        cookie.setMaxAge(time);
        response.addCookie(cookie);
    }

    /**
     * 获取Cookie中的tokenKey
     * @param request request
     * @param key cookie的key
     * @return tokenKey
     */
    public static String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 删除cookie，将cookie的时间设置为0
     * @param response  response
     * @param key cookie key
     */
    public static void deleteCookie(HttpServletResponse response, String key) {
        setUpCookies(response,key,null,0);
    }
}
