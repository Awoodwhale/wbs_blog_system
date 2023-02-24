package top.woodwhale.blog.services.impl;

import com.pig4cloud.captcha.GifCaptcha;
import com.pig4cloud.captcha.SpecCaptcha;
import com.pig4cloud.captcha.base.Captcha;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import top.woodwhale.blog.dao.*;
import top.woodwhale.blog.pojo.RefreshToken;
import top.woodwhale.blog.pojo.Setting;
import top.woodwhale.blog.pojo.User;
import top.woodwhale.blog.pojo.UserNoPassword;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.response.ResponseState;
import top.woodwhale.blog.services.IUserService;
import top.woodwhale.blog.utils.*;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static top.woodwhale.blog.utils.Constants.CaptchaCfg.FONT_TYPES;
import static top.woodwhale.blog.utils.Constants.CaptchaCfg.FONT_TYPES_LENGTH;
import static top.woodwhale.blog.utils.Constants.Page.DEFAULT_PAGE;
import static top.woodwhale.blog.utils.Constants.Page.MAX_SIZE;
import static top.woodwhale.blog.utils.Constants.Settings.MANAGER_ACCOUNT_FOUND_STATE;
import static top.woodwhale.blog.utils.Constants.Settings.MANAGER_ACCOUNT_INIT_STATE;
import static top.woodwhale.blog.utils.Constants.TimeValue.*;
import static top.woodwhale.blog.utils.Constants.User.*;

/**
 * 用户服务层实现类
 */
@Slf4j
@Service
@Transactional
public class UserServiceImpl implements IUserService {

    /**
     * 用户的userDao类
     */
    @Autowired
    private UserDao userDao;

    /**
     * 用户没有密码的userNoPasswordDao
     */
    @Autowired
    private UserNoPasswordDao userNoPasswordDao;

    /**
     * 用户状态的settingDao类
     */
    @Autowired
    private SettingDao settingDao;

    /**
     * refreshTokenDao类
     */
    @Autowired
    private RefreshTokenDao refreshTokenDao;

    /**
     * 雪花算法ID生成器
     */
    @Autowired
    private SnowflakeIdUtils idWorker;

    /**
     * SpringSecurity密码加密器
     */
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * RedisUtils工具类管理Redis
     */
    @Autowired
    private RedisUtils redisUtils;

    /**
     * mailService邮箱服务实现
     */
    @Autowired
    private MailService mailService;

    /**
     * 文章dao
     */
    @Autowired
    private ArticleDao articleDao;

    /**
     * 分类dao
     */
    @Autowired
    private CategoryDao categoryDao;

    /**
     * 评论dao
     */
    @Autowired
    private CommentDao commentDao;

    /**
     * 友链dao
     */
    @Autowired
    private FriendLinkDao friendLinkDao;

    /**
     * 初始化管理员账号
     *
     * @param user user
     * @return ResponseResult
     */
    @Override
    public ResponseResult initManagerAccount(User user) {
        // 1. 检查是否具有初始化
        Setting managerAccountState = settingDao.findOneByKey(MANAGER_ACCOUNT_INIT_STATE);
        if (managerAccountState != null) {
            return ResponseResult.FAILED("管理员账号已初始化");
        }
        // 2. 检查数据
        if (TextUtils.isEmpty(user.getUsername())) {
            return ResponseResult.FAILED(TextUtils.notNullable("用户名"));
        }
        if (TextUtils.isEmpty(user.getPassword())) {
            return ResponseResult.FAILED(TextUtils.notNullable("密码"));
        }
        if (TextUtils.isEmpty(user.getEmail())) {
            return ResponseResult.FAILED(TextUtils.notNullable("邮箱"));
        }

        // 3. 设置管理员初始数据
        HttpServletRequest request = ServletUtils.getRequest();
        user.setId(String.valueOf(idWorker.nextId()));
        user.setRoles(ROLE_ADMIN);
        user.setAvatar(DEFAULT_AVATAR);
        user.setState(ACCOUNT_NORMAL_STATE);
        user.setLoginIp(request.getRemoteAddr());
        user.setRegIp(request.getRemoteAddr());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 4. 管理员信息保存到数据库中
        userDao.save(user);

        // 5. 更新已经添加的标记
        Setting setting = new Setting();
        setting.setId(String.valueOf(idWorker.nextId()));
        setting.setCreateTime(new Date());
        setting.setUpdateTime(new Date());
        setting.setKey(MANAGER_ACCOUNT_INIT_STATE);
        setting.setValue(MANAGER_ACCOUNT_FOUND_STATE);

        // 6. 用户状态保存到数据库中
        settingDao.save(setting);
        return ResponseResult.SUCCESS("管理员账号初始化成功");
    }

    /**
     * 创建图灵验证码
     *
     * @param captchaKey 时间戳
     */
    @Override
    public void createCaptcha(String captchaKey) {
        // captchaKey为空,大于15或者小于13都是不合法的
        if (TextUtils.isEmpty(captchaKey) || captchaKey.length() < 13 || captchaKey.length() >= 15) {
            return;
        }
        long key;
        try {
            key = Long.parseLong(captchaKey);
        } catch (Exception e) {
            log.error("long转化错误", e);
            return;
        }
        HttpServletResponse response = ServletUtils.getResponse();
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        var captchaType = TextUtils.getRandomInt(2);
        Captcha captcha;
        if (captchaType == 0) {// 默认类型
            captcha = new SpecCaptcha(120, 45, 5);
        } else {// 设置gif类型
            captcha = new GifCaptcha(120, 45);
        }
        // 设置字体
        try {
            captcha.setFont(FONT_TYPES[TextUtils.getRandomInt(FONT_TYPES_LENGTH)]);
        } catch (IOException | FontFormatException e) {
            log.error("font setting error ", e);
        }
        log.info("captcha type --> " + captchaType);
        log.info("user key --> " + key);
        String verifyCode = captcha.text().toLowerCase();
        log.info("captcha str --> " + verifyCode);
        // 存入redis
        redisUtils.set(REDIS_KEY_CAPTCHA_CONTENT + key, verifyCode, REDIS_5_MINS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("图灵验证码生成失败", e);
        }
    }

    /**
     * 发送邮件email
     * 业务场景：注册、找回密码、更改邮箱
     * 注册： 如果已经注册过了，就提示该邮箱已经注册
     * 找回密码： 如果没有注册过，提示该邮箱没有注册
     * 更改邮箱（新邮箱）： 如果已经注册，提示该邮箱已注册
     *
     * @param type         业务类型
     * @param emailAddress 邮箱地址
     * @return ResponseResult
     */
    @Override
    public ResponseResult sendVerifyCode(String type, String emailAddress) {
        // 1. 判断邮箱的格式是否正确
        if (TextUtils.isEmpty(emailAddress) || !TextUtils.isMail(emailAddress)) {
            return ResponseResult.FAILED(TextUtils.smtError("邮箱地址"));
        }
        // 2. 根据类型查询邮箱是否存在
        if (type.equals("register") || type.equals("update")) {
            User user = userDao.findOneByEmail(emailAddress);
            if (user != null) {
                return ResponseResult.FAILED("该邮箱已被注册");
            }
        } else if (type.equals("forget")) {
            User user = userDao.findOneByEmail(emailAddress);
            if (user == null) {
                return ResponseResult.FAILED("该邮箱未被注册");
            }
        }
        // 3. 判断IP是否频发发送
        HttpServletRequest request = ServletUtils.getRequest();
        String remoteAddr = request.getRemoteAddr().replace(":", "-");
        log.info("remoteAddr --> " + remoteAddr);
        String ipSendTime = (String) redisUtils.get(REDIS_KEY_EMAIL_SEND_IP + remoteAddr);
        if (ipSendTime != null && Integer.parseInt(ipSendTime) > 10) {
            return ResponseResult.FAILED("请勿频繁请求发送验证码");
        }
        // 4. 判断邮箱地址是否频繁发送
        log.info("emailAddress --> " + emailAddress);
        if (redisUtils.get(REDIS_KEY_EMAIL_SEND_ADDR + emailAddress) != null) {
            return ResponseResult.FAILED("请勿频繁请求发送验证码");
        }
        // 5. 检查邮箱地址是否正确
        if (!TextUtils.isMail(emailAddress)) {
            return ResponseResult.FAILED(TextUtils.smtError("邮箱地址格式"));
        }
        // 6. 发送6位数验证码
        String sixDigitalCode = TextUtils.getDigitalCode(6);
        log.info("email code --> " + sixDigitalCode);
        try {
            mailService.sendVerificationCodeEmail(emailAddress, sixDigitalCode);
        } catch (Exception e) {
            return ResponseResult.FAILED("验证码邮件发送异常，请重试");
        }
        // 7. 将IP记录存入Redis，有效期是1小时
        if (ipSendTime == null) {
            ipSendTime = "0";
        }
        ipSendTime = String.valueOf(Integer.parseInt(ipSendTime) + 1);
        redisUtils.set(REDIS_KEY_EMAIL_SEND_IP + remoteAddr, ipSendTime, 60 * 60);
        // 8. 将邮箱地址存入Redis，标记其已经发送邮件，需要过55s重新发送
        redisUtils.set(REDIS_KEY_EMAIL_SEND_ADDR + emailAddress, "hasSend", 55);
        // 9. 将验证码存入Redis， 存放五分钟
        redisUtils.set(REDIS_KEY_EMAIL_CODE_CONTENT + emailAddress, sixDigitalCode, 60 * 5);
        return ResponseResult.SUCCESS("验证码发送成功,5分钟内有效");
    }

    /**
     * register注册
     * 1. 检查当前用户名是否被注册
     * 2. 检查邮箱是否正确
     * 3. 检查该邮箱是否被注册
     * 4. 检查邮箱验证码是否正确
     * 5. 检查图灵验证码是否正确
     * 6. 对密码进行加密
     * 7. 补充user数据
     * 8. 保存user信息到数据库中
     * 9. 返回结果
     *
     * @param user       userBean类
     * @param verifyCode 邮箱验证码
     *                   //     * @param captchaCode 图灵验证码
     *                   //     * @param key         图灵验证码key
     * @return ResponseResult
     */
    @Override
    public ResponseResult register(User user, String verifyCode) {
        // 1. 检查当前用户名是否被注册
        String username = user.getUsername();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(user.getPassword())) {
            return ResponseResult.FAILED(TextUtils.notNullable("用户名或密码"));
        }
        // 判断密码长度
        if (user.getPassword().length() > PASSWORD_MAX_LENGTH) {
            return ResponseResult.FAILED(TextUtils.smtError("密码长度"));
        }
        if (userDao.findOneByUsername(username) != null) {
            return ResponseResult.FAILED(TextUtils.hasExisted("用户名"));
        }

        // 2. 检查邮箱格式是否正确
        if (TextUtils.isEmpty(user.getEmail()) ||
                !TextUtils.isMail(user.getEmail())) {
            return ResponseResult.FAILED(TextUtils.smtError("邮箱地址"));
        }

        // 3. 判断邮箱是否被注册过了
        if (userDao.findOneByEmail(user.getEmail()) != null) {
            return ResponseResult.FAILED(TextUtils.hasExisted("邮箱"));
        }

        // 4. 判断邮件验证码是否正确
        String verifyCodeInRedis = (String) redisUtils.get(REDIS_KEY_EMAIL_CODE_CONTENT + user.getEmail());
        if (TextUtils.isEmpty(verifyCodeInRedis) || !verifyCode.equals(verifyCodeInRedis)) {
            return ResponseResult.FAILED(TextUtils.smtError("邮箱验证码"));
        } else {
            // 如果正确，就将Redis中的验证码缓存删除
            redisUtils.del(REDIS_KEY_EMAIL_CODE_CONTENT + user.getEmail());
        }

        /* 想想算了，就不要搞图灵验证码注册了
         // 5. 判断图灵验证码是否正确
        String captchaCodeInRedis = (String) redisUtils.get(REDIS_KEY_CAPTCHA_CONTENT + key);
        if (TextUtils.isEmpty(captchaCodeInRedis) || !captchaCode.equals(captchaCodeInRedis)) {
            return ResponseResult.FAILED(TextUtils.smtError("图灵验证码"));
        } else {
            // 如果正确，删除Redis中的缓存
            redisUtils.del(REDIS_KEY_CAPTCHA_CONTENT + key);
        }
         */

        // 6. 对密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 7. 补全user信息
        HttpServletRequest request = ServletUtils.getRequest();
        user.setId(String.valueOf(idWorker.nextId()));
        user.setRegIp(request.getRemoteAddr());
        user.setLoginIp(request.getRemoteAddr());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setAvatar(DEFAULT_AVATAR);
        user.setRoles(ROLE_NORMAL);
        user.setState(ACCOUNT_NORMAL_STATE);
        if (TextUtils.isEmpty(user.getSign())) {
            user.setSign("这个人很懒，什么也没留下...");
        }

        // 8. 保存到mysql中
        userDao.save(user);

        // 9. 成功就删除IP记录
        redisUtils.del(REDIS_KEY_EMAIL_SEND_IP + request.getRemoteAddr().replace(":", "-"));

        // 10. 相应注册成功
        return ResponseResult.GET(ResponseState.REGISTER_SUCCESS);
    }

    /**
     * login用户登录
     *
     * @param captchaCode 图灵验证码
     * @param key         图灵验证码key
     * @param user        用户Bean类
     * @return ResponseResult
     */
    @Override
    public ResponseResult doLogin(String captchaCode, String key, User user) {
        // 1. 判断用户账号是否正确，如果用户名和邮箱需要其中至少一个，密码一定不为空
        if ((TextUtils.isEmpty(user.getUsername()) && TextUtils.isEmpty(user.getEmail())) ||
                TextUtils.isEmpty(user.getPassword())) {
            return ResponseResult.FAILED(TextUtils.notNullable("用户名或密码"));
        }
        // 2. 判断图灵验证码是否正确
        String captchaCodeInRedis = (String) redisUtils.get(REDIS_KEY_CAPTCHA_CONTENT + key);
        if (!captchaCode.equals(captchaCodeInRedis)) {
            return ResponseResult.FAILED(TextUtils.smtError("图灵验证码"));
        } else {
            // 如果正确，删除Redis中的缓存
            redisUtils.del(REDIS_KEY_CAPTCHA_CONTENT + key);
        }

        // 3. 判断该用户名是否存在
        User userFound = userDao.findOneByUsername(user.getUsername());
        if (userFound == null) {
            log.info("使用邮箱 --> " + user.getEmail() + "进行查找");
            userFound = userDao.findOneByEmail(user.getEmail());
        }
        if (userFound == null) {
            log.info("登录时用户不存在");
            return ResponseResult.FAILED(TextUtils.smtError("用户名或密码"));
        }

        // 4. 如果用户存在，对比密码是否正确
        if (!passwordEncoder.matches(user.getPassword(), userFound.getPassword())) {
            log.info(userFound.getUsername() + "的登录密码错误");
            return ResponseResult.FAILED(TextUtils.smtError("用户名或密码"));
        }

        // 5. 判断用户状态，如果是注销，返回结果
        if (userFound.getState().equals(ACCOUNT_REMOVE_STATE)) {
            return ResponseResult.GET(ResponseState.NO_PERMISSION_LOGIN);
        }

        // 6. 修改更新时间和登录ip
        userFound.setLoginIp(ServletUtils.getRequest().getRemoteAddr());
        userFound.setUpdateTime(new Date());
        userDao.save(userFound);

        // 6. 创建token和refreshToken
        String tokenKey = createTokenWithRefreshToken(ServletUtils.getResponse(), userFound);
        log.info(userFound.getUsername() + "的tokenKey是 --> " + tokenKey);

        // 7. 相应登录成功
        return ResponseResult.GET(ResponseState.LOGIN_SUCCESS);
    }

    /**
     * 抽取出来的创建token和refreshToken的方法
     *
     * @param response  response
     * @param userFound user对象
     * @return tokenKey
     */
    private String createTokenWithRefreshToken(HttpServletResponse response, User userFound) {
        // 1. 先把MySQL中的refreshToken删除
        int res = refreshTokenDao.deleteAllByUserId(userFound.getId());
        if (res > 0) {
            log.info("删除refreshToken成功");
        }
        // 2. 生成token
        String token = JWTUtils.createToken(ClaimsUtils.user2Claims(userFound));

        // 3. 返回token的md5值，token会保存到redis中
        // 前端访问的时候，携带token的md5值，我们从redis中获取即可
        String tokenKey = DigestUtils.md5DigestAsHex(token.getBytes(StandardCharsets.UTF_8));
        // 把token写入redis中，有效期2小时，key是tokenKey
        redisUtils.set(REDIS_KEY_TOKEN + tokenKey, token, REDIS_2_HOURS);
        // 把token key写入cookies中
        CookieUtils.setUpCookies(response, COOKIE_KEY_TOKEN, tokenKey);

        // 4. 生成1周的refreshToken
        String refreshTokenValue = JWTUtils.createRefreshToken(userFound.getId(), JWT_1_WEEK);

        // 5. 将refreshToken存入MySQL
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setId(String.valueOf(idWorker.nextId()));
        refreshToken.setRefreshToken(refreshTokenValue);
        refreshToken.setUserId(userFound.getId());
        refreshToken.setTokenKey(tokenKey);
        refreshToken.setCreateTime(new Date());
        refreshToken.setUpdateTime(new Date());
        refreshTokenDao.save(refreshToken);

        return tokenKey;
    }

    /**
     * 检查用户是否有登录，如果登录了就返回用户信息（通过cookie中的tokenKey）
     *
     * @return User
     */
    @Override
    public User checkUser() {
        HttpServletRequest request = ServletUtils.getRequest();
        // 获取前端cookie中的md5 tokenKey
        String tokenKey = CookieUtils.getCookie(request, COOKIE_KEY_TOKEN);
        return checkUser(tokenKey);
    }

    /**
     * 通过tokenKey 获取 user对象
     *
     * @param tokenKey token
     * @return User
     */
    @Override
    public User checkUser(String tokenKey) {
        HttpServletResponse response = ServletUtils.getResponse();
        if (!TextUtils.isEmpty(tokenKey)) {
            // 2. 如果redis的2小时期限没有过期，就成功返回这个user
            User user = tokenKey2User(tokenKey);
            if (user != null) {
                return user;
            }

            // 3. 如果过期了，就去mysql中查询refreshToken
            RefreshToken refreshToken = refreshTokenDao.findOneByTokenKey(tokenKey);

            // 4. 如果refreshToken不存在，就是没有登录
            if (refreshToken == null) {
                log.info("refreshToken不存在");
                return null;
            }

            try {
                // 5. 如果refreshToken存在，就解析refreshToken获取userId
                Claims claims = JWTUtils.parseJWT(refreshToken.getRefreshToken());
                User userInDb = userDao.findOneById(claims.getId());
                // 6. 只需要通过refreshToken创建新的tokenKey，refreshToken不重建
                String token = JWTUtils.createToken(ClaimsUtils.user2Claims(userInDb));
                String newTokenKey = DigestUtils.md5DigestAsHex(token.getBytes(StandardCharsets.UTF_8));
                redisUtils.set(REDIS_KEY_TOKEN + newTokenKey, token, REDIS_2_HOURS);
                CookieUtils.setUpCookies(response, COOKIE_KEY_TOKEN, newTokenKey);
                log.info(userInDb.getUsername() + " 创建新的tokenKey");
                int res = refreshTokenDao.updateNewTokenKeyByUserId(newTokenKey, userInDb.getId());
                if (res > 0) {
                    log.info(userInDb.getUsername() + " 在MySQL的refreshToken中创建新的tokenKey");
                }
                // 7. 返回创建token之后的用户
                return tokenKey2User(newTokenKey);
            } catch (Exception e) {
                log.error("refreshToken过期了,重新登录 " + e);
                // 8. 如果refreshToken过期了，就提示用户登录
                return null;
            }
        }
        return null;
    }

    /**
     * 前端md5的tokenKey经过redis查询转化为user
     *
     * @param tokenKey 前端md5的tokenKey
     * @return user实体
     */
    private User tokenKey2User(String tokenKey) {
        String token = (String) redisUtils.get(REDIS_KEY_TOKEN + tokenKey);
        if (!TextUtils.isEmpty(token)) {
            try {
                Claims claims = JWTUtils.parseJWT(token);
                return ClaimsUtils.claims2User(claims);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 通过userId获取用户，从数据库中获取
     *
     * @param userId userId
     * @return ResponseResult
     */
    @Override
    public ResponseResult getUserInfo(String userId) {
        User user = userDao.findOneById(userId);
        if (user == null) {
            return ResponseResult.FAILED(TextUtils.notExist("用户"));
        }
        // copy一个对象进行渲染处理，防止数据库数据被摸出
        User resUser = getCopiedUser(user);
        resUser.setPassword("undefined");
        resUser.setEmail("undefined");
        resUser.setLoginIp("undefined");
        resUser.setRegIp("undefined");
        return ResponseResult.SUCCESS(TextUtils.successGet("用户")).setData(resUser);
    }


    /**
     * copy一个user对象
     *
     * @param userFromMysql user
     * @return user
     */
    @NotNull
    private User getCopiedUser(User userFromMysql) {
        User newUser = new User();
        BeanUtils.copyProperties(userFromMysql, newUser);
        return newUser;
    }

    /**
     * 从数据库中检查是否存在当前邮箱
     *
     * @param email 邮箱地址
     * @return ResponseResult
     */
    @Override
    public ResponseResult checkEmail(String email) {
        User user = userDao.findOneByEmail(email);
        return user == null ? ResponseResult.FAILED("该邮箱未注册") : ResponseResult.SUCCESS("该邮箱已注册");
    }

    /**
     * 从数据库中检查是否存在当前用户名
     *
     * @param username 用户名
     * @return ResponseResult
     */
    @Override
    public ResponseResult checkUsername(String username) {
        User user = userDao.findOneByUsername(username);
        return user == null ? ResponseResult.FAILED("该用户名未注册") : ResponseResult.SUCCESS("该用户名已注册");
    }

    /**
     * 更新用户信息
     *
     * @param userId userId
     * @param user   user
     * @return ResponseResult
     */
    @Override
    public ResponseResult updateUserInfo(String userId, User user) {
        // 判断是否存在这个id
        User editedUser = userDao.findOneById(userId);
        if (editedUser == null) {
            return ResponseResult.FAILED(TextUtils.notExist("用户"));
        }
        // 1. 判断是否登录、是否具有权限
        User userFromTokenKey = checkUser();
        if (userFromTokenKey == null) {
            return ResponseResult.GET(ResponseState.NOT_LOGIN);
        }
        User userFromMysql = userDao.findOneById(userFromTokenKey.getId());
        if (!(userFromMysql.getId().equals(userId) || userFromMysql.getRoles().equals(ROLE_ADMIN))) {
            return ResponseResult.GET(ResponseState.NO_PERMISSION);
        }
        // 2. 创造一个copy对象进行修改
        User newUser = getCopiedUser(editedUser);
        // 3. 修改用户名
        if (!TextUtils.isEmpty(user.getUsername())) {
            User tmpUser = userDao.findOneByUsername(user.getUsername());
            if (tmpUser != null && !userId.equals(tmpUser.getId())) {
                return ResponseResult.FAILED(TextUtils.hasExisted("用户名"));
            }
            newUser.setUsername(user.getUsername());
        } else {
            return ResponseResult.FAILED(TextUtils.notNullable("用户名"));
        }
        // 4. 修改头像
        if (!TextUtils.isEmpty(user.getAvatar())) {
            newUser.setAvatar(user.getAvatar());
        } else {
            return ResponseResult.FAILED(TextUtils.notNullable("头像"));
        }
        // 5. 修改权限，前提是操作用户是admin
        if (userFromMysql.getRoles().equals(ROLE_ADMIN)) {
            // 不能将管理员改为普通用户
            if (!TextUtils.isEmpty(user.getRoles())) {
                if (user.getRoles().equals(ROLE_NORMAL) &&
                        editedUser.getRoles().equals(ROLE_ADMIN)) {
                    return ResponseResult.GET(ResponseState.NO_PERMISSION);
                } else {
                    newUser.setRoles(user.getRoles());
                }
            }
            // 不能将管理员注销
            if (!TextUtils.isEmpty(user.getState())) {
                if (user.getState().equals(ACCOUNT_REMOVE_STATE) &&
                        editedUser.getRoles().equals(ROLE_ADMIN)) {
                    return ResponseResult.GET(ResponseState.NO_PERMISSION);
                } else {
                    newUser.setState(user.getState());
                }
            }
        }
        // 6. 修改杂项
        newUser.setSign(user.getSign());
        newUser.setProfession(user.getProfession());
        newUser.setMajor(user.getMajor());
        newUser.setLocation(user.getLocation());
        // 7. 修改update时间
        newUser.setUpdateTime(new Date());
        // 8. 保存修改内容
        editedUser = newUser;
        userDao.save(editedUser);
        return ResponseResult.SUCCESS(TextUtils.successUpdate("用户信息"));
    }

    /**
     * 删除用户，并非从数据库中抹除这条数据，而是将user的state改变，需要管理员权限进行修改
     *
     * @param userId userID
     * @return ResponseResult
     */
    @Override
    public ResponseResult deleteUserById(String userId) {
        User userInToken = checkUser();
        // 判断是否是多个用户删除
        if (userId.contains("_") && userId.split("_").length > 0) {
            String[] ids = userId.split("_");
            // 先检查一遍是否具有管理员身份的id
            for (String id : ids) {
                if (userInToken.getId().equals(id) ||
                        userDao.findOneById(id).getRoles().equals(ROLE_ADMIN)) {
                    return ResponseResult.GET(ResponseState.NO_PERMISSION);
                }
            }
            // 如果没有管理员身份的id，进行删除
            for (String id : ids) {
                int res = userDao.deleteUserByState(id);
                if (res <= 0) {
                    return ResponseResult.FAILED(TextUtils.failDelete("用户id " + id + " "));
                }
            }
            return ResponseResult.SUCCESS(TextUtils.successDelete("用户"));
        } else {
            // 管理员自己不能删除自己，同时不能删除管理员
            if (userInToken.getId().equals(userId) ||
                    userDao.findOneById(userId).getRoles().equals(ROLE_ADMIN)) {
                return ResponseResult.GET(ResponseState.NO_PERMISSION);
            }
            // dao层删除用户
            int res = userDao.deleteUserByState(userId);
            if (res > 0) {
                return ResponseResult.SUCCESS(TextUtils.successDelete("用户"));
            }
        }
        return ResponseResult.FAILED(TextUtils.failDelete("用户id " + userId + " "));
    }

    /**
     * 获取用户列表，需要管理员权限
     *
     * @param page     page
     * @param size     size
     * @param userName username
     * @param email    email
     * @return ResponseResult
     */
    @Override
    public ResponseResult listUsers(int page, int size, String userName, String email) {
        // 分页查询
        page = Math.max(page, DEFAULT_PAGE);
        size = Math.min(MAX_SIZE, Math.max(size, 1));
        // 根据create_time来排序用户
        Page<UserNoPassword> users = userNoPasswordDao.findAll((Specification<UserNoPassword>) (root, query, cb) -> {
            ArrayList<Predicate> list = new ArrayList<>();
            if (!TextUtils.isEmpty(userName)) {
                list.add(cb.like(root.get("username").as(String.class), "%" + userName + "%"));
            }
            if (!TextUtils.isEmpty(email)) {
                list.add(cb.equal(root.get("email").as(String.class), email));
            }
            Predicate[] predicates = list.toArray(new Predicate[0]);
            return cb.and(predicates);
        }, PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime")));
        return ResponseResult.SUCCESS(TextUtils.successGet("用户列表")).setData(users);
    }

    /**
     * 修改密码和找回密码
     * 两者其实本质是一样的，我们肯定不能将密码发送到邮箱中
     * 所以每次找回密码都是修改新密码
     * 区别在于，修改密码只需要校验当前密码，输入新密码
     * 而找回密码，需要发送邮箱验证码
     *
     * @param verifyCode 邮箱验证码
     * @param user       user
     * @return ResponseResult
     */
    @Override
    public ResponseResult updatePassword(String verifyCode, User user) {
        // 1. 检查邮箱是否有填写
        String email = user.getEmail();
        if (TextUtils.isEmpty(email)) {
            return ResponseResult.FAILED(TextUtils.notNullable("邮箱"));
        }
        // 2. 检查密码是否填写
        if (TextUtils.isEmpty(user.getPassword())) {
            return ResponseResult.FAILED(TextUtils.notNullable("新密码"));
        }
        // 3. 根据邮箱去redis中验证
        String verifyCodeInRedis = (String) redisUtils.get(REDIS_KEY_EMAIL_CODE_CONTENT + email);
        if (TextUtils.isEmpty(verifyCodeInRedis) || !verifyCode.equals(verifyCodeInRedis)) {
            return ResponseResult.FAILED(TextUtils.smtError("验证码"));
        }
        // 4. 删除redis中的验证码缓存
        redisUtils.del(REDIS_KEY_EMAIL_CODE_CONTENT + email);
        // 5. 根据mysql修改结果处理返回数据
        int res = userDao.updatePasswordByEmail(passwordEncoder.encode(user.getPassword()), email);
        return res > 0 ? ResponseResult.SUCCESS(TextUtils.successUpdate("密码")) :
                ResponseResult.FAILED(TextUtils.failUpdate("密码"));
    }

    /**
     * 更新绑定的邮箱
     * 需要新的邮箱验证码
     * 当前账号的密码和token解析后的密码对比，相同就证明是同一个密码
     *
     * @param verifyCode 邮箱验证码
     * @param newEmail   新邮箱
     * @param password   当前账号密码
     * @return ResponseResult
     */
    @Override
    public ResponseResult updateEmail(String verifyCode, String newEmail, String password) {
        // 1. 确保当前用户登录了
        User user = checkUser();
        if (user == null) {
            return ResponseResult.GET(ResponseState.NOT_LOGIN);
        }
        // 2. 对比验证码
        String verifyCodeInRedis = (String) redisUtils.get(REDIS_KEY_EMAIL_CODE_CONTENT + newEmail);
        log.info("redis中的" + newEmail + "的验证码" + verifyCodeInRedis);
        if (TextUtils.isEmpty(verifyCodeInRedis) || !verifyCode.equals(verifyCodeInRedis)) {
            return ResponseResult.FAILED(TextUtils.smtError("验证码"));
        }
        // 3. 对比密码
        User userWithPwd = userDao.findOneById(user.getId());
        if (!passwordEncoder.matches(password, userWithPwd.getPassword())) {
            log.info(userWithPwd.getUsername() + "更改邮箱密码错误");
            return ResponseResult.FAILED(TextUtils.smtError("当前密码"));
        }
        // 4. 删除redis中的邮箱验证码缓存
        redisUtils.del(REDIS_KEY_EMAIL_CODE_CONTENT + newEmail);
        // 5. 更改绑定邮箱
        int res = userDao.updateEmailById(newEmail, user.getId());
        return res > 0 ? ResponseResult.SUCCESS(TextUtils.successUpdate("邮箱") + "，即将退出!") :
                ResponseResult.FAILED(TextUtils.failUpdate("邮箱"));
    }

    /**
     * 退出登录
     * <p>
     * 1. redis中的token缓存删除
     * 2. cookie中的tokenKey缓存删除
     * 3. MySQL中的refreshToken删除
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult doLogout() {
        // 1. 删除redis中的token缓存
        String tokenKey = CookieUtils.getCookie(ServletUtils.getRequest(), COOKIE_KEY_TOKEN);
        if (!TextUtils.isEmpty(tokenKey)) {
            redisUtils.del(REDIS_KEY_TOKEN + tokenKey);
        } else {
            return ResponseResult.GET(ResponseState.NOT_LOGIN);
        }

        // 2. 删除cookie中的tokenKey缓存
        CookieUtils.deleteCookie(ServletUtils.getResponse(), COOKIE_KEY_TOKEN);

        // 3. 删除MySQL中的refreshToken
        int res = refreshTokenDao.deleteAllByTokenKey(tokenKey);
        if (res > 0) {
            log.info("删除refreshToke成功");
        }
        return ResponseResult.SUCCESS("退出登录成功");
    }

    /**
     * 根据前端的tokenKey获取user信息
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult parseToken(String token) {
        User user =  TextUtils.isEmpty(token) ? checkUser() : checkUser(token);
        if (user == null) {
            return ResponseResult.GET(ResponseState.NOT_LOGIN);
        }
        return ResponseResult.SUCCESS(TextUtils.successGet("用户信息")).setData(user);
    }

    /**
     * 从MySQL中获取token中用户的数据
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult getCurrentUserInfo() {
        User user = checkUser();
        if (user == null) {
            return ResponseResult.GET(ResponseState.NOT_LOGIN);
        }
        String id = user.getId();
        UserNoPassword userInMysql = userNoPasswordDao.findOneById(id);
        if (userInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("用户"));
        }
        return ResponseResult.SUCCESS(TextUtils.successGet("当前用户数据")).setData(userInMysql);
    }

    /**
     * 重设管理员密码
     *
     * @param oldPassword 验证旧密码
     * @param newPassword 更新新密码
     * @return ResponseResult
     */
    @Override
    public ResponseResult resetAdminPassword(String oldPassword, String newPassword) {
        if (TextUtils.isEmpty(oldPassword.trim()) || TextUtils.isEmpty(newPassword.trim())) {
            return ResponseResult.FAILED(TextUtils.notNullable("密码"));
        }
        User user = checkUser();
        User userInMysql = userDao.findOneById(user.getId());
        // 判断权限
        if (userInMysql == null || !userInMysql.getRoles().equals(ROLE_ADMIN)) {
            return ResponseResult.GET(ResponseState.NOT_LOGIN);
        }
        // 判断旧密码是否正确
        boolean isMatches = passwordEncoder.matches(oldPassword, userInMysql.getPassword());
        if (isMatches) {
            // 如果旧密码匹配，那么就更新密码
            User copiedUser = getCopiedUser(userInMysql);
            copiedUser.setPassword(passwordEncoder.encode(newPassword));
            userInMysql = copiedUser;
            userDao.save(userInMysql);
            return ResponseResult.SUCCESS(TextUtils.successUpdate("管理员密码") + "，即将退出!");
        }
        return ResponseResult.FAILED(TextUtils.smtError("旧密码"));
    }

    /**
     * 获取管理员信息
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult getAdminInfo() {
        List<UserNoPassword> admins = userNoPasswordDao.findAll((Specification<UserNoPassword>) (root, query, cb) ->
                        cb.equal(root.get("roles").as(String.class), ROLE_ADMIN),
                Sort.by(Sort.Direction.ASC, "createTime"));
        return ResponseResult.SUCCESS(TextUtils.successGet("管理员信息")).setData(admins);
    }

    /**
     * 判断管理员是否初始化成功
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult checkInit() {
        Setting managerAccountState = settingDao.findOneByKey(MANAGER_ACCOUNT_INIT_STATE);
        if (managerAccountState == null) {
            // 如果为空，那么就是没有初始化
            return ResponseResult.FAILED("管理员账号未初始化");
        }
        return ResponseResult.SUCCESS("管理员账号已初始化");
    }
}
