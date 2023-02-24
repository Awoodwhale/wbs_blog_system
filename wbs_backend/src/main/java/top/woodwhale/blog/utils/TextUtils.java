package top.woodwhale.blog.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符工具类
 */
public class TextUtils {
    /**
     * 判断字符转是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断邮箱格式是否正确
     */
    private static String regEx = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    public static boolean isMail(String str) {
        if (str.isEmpty()) return false;
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    /**
     * 返回随机数
     */
    public static int getRandomInt(int num) {
        return new Random().nextInt(num);   // 不能使用curtime
    }

    /**
     * 返回count位邮箱验证码
     * @param count 验证码位数
     * @return 验证码
     */
    public static String getDigitalCode(int count) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            sb.append(getRandomInt(10));
        }
        return sb.toString();
    }

    /**
     * 返回图片后缀格式
     * @param type contentType
     * @return 图片格式
     */
    public static String getImageType(String type) {
        switch (type) {
            case "image/jpeg": return ".jpg";
            case "image/png": return ".png";
            default: return ".gif";
        }
    }

    /**
     * 图片保存需要用到的常量
     */
    public static final String[] types = new String[]{"image/jpeg","image/png","image/gif"};
    public static final String[] endNames = new String[]{"png","jpg","gif"};

    /**
     * 判断是否是图片文件
     * @param file 图片文件
     * @return true or false
     */
    public static boolean isImage(MultipartFile file) {
        if (file == null) {
            return false;
        }
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();
        return !isEmpty(originalFilename) &&
                !isEmpty(contentType) &&
                originalFilename.length() > 4 &&
                Arrays.asList(types).contains(contentType) &&
                Arrays.asList(endNames).contains(originalFilename.split("\\.")[1]);
    }

    public static String notNullable(String str) {
        return str + "不可为空";
    }

    public static String notExist(String str) {
        return str + "不存在";
    }

    public static String hasExisted(String str) {
        return "该" + str + "已存在";
    }

    public static String successDelete(String str) {
        return str + "删除成功";
    }

    public static String failDelete(String str) {
        return str + "删除失败";
    }

    public static String successAdd(String str) {
        return str + "添加成功";
    }

    public static String failAdd(String str) {
        return str + "添加失败";
    }

    public static String successUpdate(String str) {
        return str + "更新成功";
    }

    public static String failUpdate(String str) {
        return str + "更新失败";
    }

    public static String successGet(String str) {
        return str + "获取成功";
    }

    public static String smtError(String str) {
        return str + "错误";
    }
}
