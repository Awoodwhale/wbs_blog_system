package top.woodwhale.blog.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.woodwhale.blog.dao.SettingDao;
import top.woodwhale.blog.pojo.Setting;
import top.woodwhale.blog.pojo.User;
import top.woodwhale.blog.services.IUserService;
import top.woodwhale.blog.utils.CookieUtils;
import top.woodwhale.blog.utils.TextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static top.woodwhale.blog.utils.Constants.Settings.WEB_SITE_SURE_COMMENT;
import static top.woodwhale.blog.utils.Constants.User.COOKIE_KEY_TOKEN;
import static top.woodwhale.blog.utils.Constants.User.ROLE_ADMIN;

/**
 * 权限鉴别
 */
@Service("Permission")
public class PermissionService {

    @Autowired
    private IUserService userService;

    @Autowired
    private SettingDao settingDao;

    /**
     * 判断是否是管理员
     *
     * @return 是否为管理员 true or false
     */
    public boolean admin() {
        // 拿到request和response
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();
        String tokenKey = CookieUtils.getCookie(request, COOKIE_KEY_TOKEN);
        // 如果没有登录肯定不是管理员
        if (TextUtils.isEmpty(tokenKey)) {
            return false;
        }
        // 无法获取用户也肯定不是管理员
        User user = userService.checkUser();
        if (user == null) {
            return false;
        }
        // 只有role为admin才是管理员
        return user.getRoles().equals(ROLE_ADMIN);
    }

    /**
     * 判断是否开启评论API
     *
     * @return 是否开启评论 true or false
     */
    public boolean sureComment() {
        Setting settingInMysql = settingDao.findOneByKey(WEB_SITE_SURE_COMMENT);
        return settingInMysql == null || settingInMysql.getValue().equals("true");
    }
}
