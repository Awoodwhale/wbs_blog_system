package top.woodwhale.blog.services;

import top.woodwhale.blog.pojo.User;
import top.woodwhale.blog.response.ResponseResult;

public interface IUserService {
    ResponseResult initManagerAccount(User user);

    void createCaptcha(String captchaKey);

    ResponseResult sendVerifyCode(String type, String emailAddress);

    ResponseResult register(User user, String verifyCode);

    ResponseResult doLogin(String captchaCode, String key, User user);

    User checkUser();

    User checkUser(String token);

    ResponseResult getUserInfo(String userId);

    ResponseResult checkEmail(String email);

    ResponseResult checkUsername(String username);

    ResponseResult updateUserInfo(String userId, User user);

    ResponseResult deleteUserById(String userId);

    ResponseResult listUsers(int page, int size, String userName, String email);

    ResponseResult updatePassword(String verifyCode, User user);

    ResponseResult updateEmail(String verifyCode, String newEmail, String password);

    ResponseResult doLogout();

    ResponseResult parseToken(String token);

    ResponseResult getCurrentUserInfo();

    ResponseResult resetAdminPassword(String oldPassword, String newPassword);

    ResponseResult getAdminInfo();

    ResponseResult checkInit();
}
