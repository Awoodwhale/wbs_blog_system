package top.woodwhale.blog.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.woodwhale.blog.pojo.User;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.IUserService;

/**
 * 用户API
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserApi {

    /**
     * 注入 userService
     */
    @Autowired
    private IUserService userService;

    /**
     * 初始化管理员账号
     *
     * @return ResponseResult
     */
    @PostMapping("/admin_account")
    public ResponseResult initManagerAccount(@RequestBody User user) {
        return userService.initManagerAccount(user);
    }

    /**
     * 用户注册
     *
     * @param user       用户信息
     * @param verifyCode 邮箱验证码
     *                   //     * @param captchaCode 图灵验证码
     *                   //     * @param key         验证码key
     * @return ResponseResult
     */
    @PostMapping("/register")
    public ResponseResult register(@RequestBody User user,
                                   @RequestParam("verify_code") String verifyCode) {
        return userService.register(user, verifyCode);
    }

    /**
     * 登录(需要图灵验证码) 支持双端登录（web、手机）
     * 需要提交的数据：
     * 1. 用户账号（名称、邮箱）
     * 2. 密码
     * 3. 图灵验证码
     * 4. 图灵验证码的Key
     *
     * @param captchaCode 验证码
     * @param key         验证码key
     * @param user        用户信息
     * @return ResponseResult
     */
    @PostMapping("/login")
    public ResponseResult login(@RequestParam("captcha_code") String captchaCode,
                                @RequestParam("captcha_code_key") String key,
                                @RequestBody User user) {
        return userService.doLogin(captchaCode, key, user);
    }

    @GetMapping("/logout")
    public ResponseResult logout() {
        return userService.doLogout();
    }

    /**
     * 获取图灵验证码
     *
     * @param captchaKey 时间戳
     */
    @GetMapping("/captcha")
    public void getCaptcha(@RequestParam("captcha_code_key") String captchaKey) {
        userService.createCaptcha(captchaKey);
    }

    /**
     * 发送邮件email
     * 业务场景：注册、找回密码、更改邮箱
     * 注册： 如果已经注册过了，就提示该邮箱已经注册
     * 找回密码： 如果没有注册过，提示该邮箱没有注册
     * 更改邮箱（新邮箱）： 如果已经注册，提示该邮箱已注册
     *
     * @param emailAddress 邮箱地址
     * @return ResponseResult
     */
    @GetMapping("/verify_code")
    public ResponseResult sendVerifyCode(@RequestParam("type") String type,
                                         @RequestParam("email") String emailAddress) {
        log.info("email --> " + emailAddress);
        return userService.sendVerifyCode(type, emailAddress);
    }

    /**
     * 修改密码：对比旧密码
     * 找回密码：发送邮箱验证码重新设置
     *
     * @param user user
     * @return ResponseResult
     */
    @PutMapping("/password")
    public ResponseResult updatePassword(@RequestParam("verify_code") String verifyCode, @RequestBody User user) {
        return userService.updatePassword(verifyCode, user);
    }

    @PutMapping("/email")
    public ResponseResult updateEmail(@RequestParam("verify_code") String verifyCode,
                                      @RequestParam("new_email") String newEmail,
                                      @RequestParam("now_password") String password) {
        return userService.updateEmail(verifyCode, newEmail, password);
    }

    /**
     * 通过userId获取userInfo
     *
     * @param userId userId
     * @return ResponseResult
     */
    @GetMapping("/user_info/{userId}")
    public ResponseResult getUserInfo(@PathVariable("userId") String userId) {
        return userService.getUserInfo(userId);
    }

    @GetMapping("/admin_info")
    public ResponseResult getAdminInfo() {
        return userService.getAdminInfo();
    }

    /**
     * 修改用户信息
     * 1. 头像
     * 2. 用户名
     * 3. 签名
     * 4. 密码
     * 5. 邮箱
     *
     * @param user user
     * @return ResponseResult
     */
    @PutMapping("/user_info/{userId}")
    public ResponseResult updateUserInfo(@PathVariable("userId") String userId, @RequestBody User user) {
        return userService.updateUserInfo(userId, user);
    }

    /**
     * 检查邮箱是否存在，给前端提供ajax接口
     *
     * @param email 邮箱地址
     * @return ResponseResult
     */
    @GetMapping("/email")
    public ResponseResult checkEmail(@RequestParam("email") String email) {
        return userService.checkEmail(email);
    }

    /**
     * 检查用户名是否存在，给前端提供ajax接口
     *
     * @param username username
     * @return ResponseResult
     */
    @GetMapping("/user_name")
    public ResponseResult checkUsername(@RequestParam("username") String username) {
        return userService.checkUsername(username);
    }

    /**
     * 获取用户列表，需要管理员权限
     *
     * @param page 页数
     * @param size 每页容量
     * @return ResponseResult
     */
    @PreAuthorize("@Permission.admin()")
    @GetMapping("/list")
    public ResponseResult listUsers(@RequestParam("page") int page, @RequestParam("size") int size,
                                    @RequestParam(value = "username", required = false) String userName,
                                    @RequestParam(value = "email", required = false) String email) {
        return userService.listUsers(page, size, userName, email);
    }

    /**
     * 根据ID删除用户
     *
     * @param userId 用户ID
     * @return ResponseResult
     */
    @PreAuthorize("@Permission.admin()")
    @DeleteMapping("/delete/{userId}")
    public ResponseResult deleteUser(@PathVariable("userId") String userId) {
        return userService.deleteUserById(userId);
    }

    /**
     * 前端发送tokenKey来获取user信息
     *
     * @return ResponseResult
     */
    @GetMapping("/check_token")
    public ResponseResult parseToken(@RequestParam(value = "token",required = false) String token) {
        return userService.parseToken(token);
    }

    /**
     * 获取当前token中用户的MySQL中的数据
     *
     * @return ResponseResult
     */
    @GetMapping("/user_info")
    public ResponseResult getCurrentUserInfo() {
        return userService.getCurrentUserInfo();
    }

    /**
     * 重设管理员密码
     *
     * @return ResponseResult
     */
    @PreAuthorize("@Permission.admin()")
    @PutMapping("/reset_password")
    public ResponseResult resetAdminPassword(@RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        return userService.resetAdminPassword(oldPassword, newPassword);
    }


    /**
     * 判断管理员是否初始化过
     *
     * @return ResponseResult
     */
    @GetMapping("/check_init")
    public ResponseResult checkInit() {
        return userService.checkInit();
    }
}
