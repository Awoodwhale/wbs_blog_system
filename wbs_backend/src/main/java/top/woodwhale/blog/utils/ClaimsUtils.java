package top.woodwhale.blog.utils;

import io.jsonwebtoken.Claims;
import top.woodwhale.blog.pojo.User;

import java.util.HashMap;
import java.util.Map;

import static top.woodwhale.blog.utils.Constants.User.*;

/**
 * Claims工具类
 */
public class ClaimsUtils {

    /**
     * 将user转化为Claims
     * @param user user
     * @return Claims Map
     */
    public static Map<String, Object> user2Claims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIMS_ID_KEY, user.getId());
        claims.put(CLAIMS_USERNAME_KEY, user.getUsername());
        claims.put(CLAIMS_ROLE_KEY, user.getRoles());
        claims.put(CLAIMS_AVATAR_KEY, user.getAvatar());
        claims.put(CLAIMS_EMAIL_KEY, user.getEmail());
        claims.put(CLAIMS_SIGN_KEY, user.getSign());
        return claims;
    }

    /**
     * 将Claims转为user
     * @param claims Claims
     * @return User
     */
    public static User claims2User(Claims claims) {
        User user = new User();
        user.setId((String) claims.get(CLAIMS_ID_KEY));
        user.setUsername((String) claims.get(CLAIMS_USERNAME_KEY));
        user.setRoles((String) claims.get(CLAIMS_ROLE_KEY));
        user.setAvatar((String) claims.get(CLAIMS_AVATAR_KEY));
        user.setEmail((String) claims.get(CLAIMS_EMAIL_KEY));
        user.setSign((String) claims.get(CLAIMS_SIGN_KEY));
        return user;
    }
}
