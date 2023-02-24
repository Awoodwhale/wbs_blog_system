package top.woodwhale.blog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static top.woodwhale.blog.utils.Constants.TimeValue.JWT_2_HOURS;

public class JWTUtils {

    /**
     * 盐的value
     */
    private static String key = DigestUtils.md5DigestAsHex("*woodwhale_blog_system*".getBytes(StandardCharsets.UTF_8));

    private static long ttl = JWT_2_HOURS;//2个小时

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        JWTUtils.key = key;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        JWTUtils.ttl = ttl;
    }

    /**
     * @param claims 载荷内容
     * @param ttl    有效时长
     * @return String
     */
    public static String createToken(Map<String, Object> claims, long ttl) {
        JWTUtils.ttl = ttl;
        return createToken(claims);
    }


    /**
     * 创建refreshToken，只保存userId
     * @param userId  userId
     * @param ttl 时长
     * @return refreshToken
     */
    public static String createRefreshToken(String userId, long ttl) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder().setId(userId)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, key);
        if (ttl > 0) {
            builder.setExpiration(new Date(nowMillis + ttl));
        }
        return builder.compact();
    }

    /**
     * 创建Token，包含除去user密码的所有信息
     * @param claims 载荷
     * @return token
     */
    public static String createToken(Map<String, Object> claims) {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, key);
        if (claims != null) {
            builder.setClaims(claims);
        }
        if (ttl > 0) {
            builder.setExpiration(new Date(nowMillis + ttl));
        }
        return builder.compact();
    }

    /**
     * 将JWT转为包含信息的Claims
     * @param jwtStr jwtStr
     * @return Claims
     */
    public static Claims parseJWT(String jwtStr) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwtStr)
                .getBody();
    }
}