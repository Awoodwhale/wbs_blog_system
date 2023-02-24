package top.woodwhale.blog.utils;

import com.pig4cloud.captcha.base.Captcha;

/**
 * 常量接口
 */
public interface Constants {
    /**
     * 有关用户的常量
     */
    interface User {
        // 管理员
        String ROLE_ADMIN = "role_admin";
        // 普通用户
        String ROLE_NORMAL = "role_normal";
        // 默认头像地址
        String DEFAULT_AVATAR = "https://cdn.jsdelivr.net/gh/Awoodsheep/pic/imgs/avatar.png";
        // 账号正常状态
        String ACCOUNT_NORMAL_STATE = "1";
        // 账号注销状态
        String ACCOUNT_REMOVE_STATE = "0";
        // redis中图灵验证码内容的key
        String REDIS_KEY_CAPTCHA_CONTENT = "redis_key_captcha_content_";
        // redis中邮箱验证码内容的key
        String REDIS_KEY_EMAIL_CODE_CONTENT = "redis_key_email_code_content_";
        // redis中ip地址内容的key
        String REDIS_KEY_EMAIL_SEND_IP = "redis_key_email_send_ip_";
        // redis中邮箱地址内容的key
        String REDIS_KEY_EMAIL_SEND_ADDR = "redis_key_email_send_addr_";
        // redis中token的key
        String REDIS_KEY_TOKEN = "redis_key_token_";
        // redis中存放user提交评论的频率
        String REDIS_KEY_COMMIT_FREQUENT = "redis_key_commit_frequent_";
        // cookie中token转为md5的key
        String COOKIE_KEY_TOKEN = "blog_system_token";
        // claims中id的key
        String CLAIMS_ID_KEY = "id";
        // claims中username的key
        String CLAIMS_USERNAME_KEY = "username";
        // claims中role的key
        String CLAIMS_ROLE_KEY = "role";
        // claims中avatar的key
        String CLAIMS_AVATAR_KEY = "avatar";
        // claims中email的key
        String CLAIMS_EMAIL_KEY = "email";
        // claims中sign的key
        String CLAIMS_SIGN_KEY = "sign";
        // 密码最大长度，都是md5加密，32位
        int PASSWORD_MAX_LENGTH = 32;
    }

    /**
     * 有关分类的常量
     */
    interface Category {
        String CATEGORY_NORMAL_STATE = "1";
        String CATEGORY_REMOVE_STATE = "0";
    }

    interface Comment {
        String COMMENT_NORMAL_STATE = "1";
        String COMMENT_REMOVE_STATE = "0";
        String COMMENT_TOP_STATE = "2";
    }

    /**
     * 有关友链的常量
     */
    interface FriendLink {
        String FRIEND_LINK_NORMAL_STATE = "1";
        String FRIEND_LINK_REMOVE_STATE = "0";
        String DEFAULT_LOGO = "https://cdn.jsdelivr.net/gh/Awoodsheep/pic/imgs/friend_link.png";
    }

    /**
     * 有关image表的常量
     */
    interface Image {
        String IMAGE_NORMAL_STATE = "1";
        String IMAGE_REMOVE_STATE = "0";
    }

    /**
     * 有关setting表的常量
     */
    interface Settings {
        // 管理员初始化的key
        String MANAGER_ACCOUNT_INIT_STATE = "manager_account_init_state";
        String MANAGER_ACCOUNT_FOUND_STATE = "1";
        String DEFAULT_VIEW_COUNT = "1";
        String WEB_SITE_TITLE = "web_site_title";
        String WEB_SITE_DESCRIPTION = "web_site_description";
        String WEB_SITE_KEYWORDS = "web_site_keywords";
        String WEB_SITE_VIEW_COUNT = "web_site_view_count";
        String WEB_SITE_SVG = "web_site_svg";
        String WEB_SITE_H1 = "web_site_h1";
        String WEB_SITE_SIGN = "web_site_sign";
        String WEB_SITE_RANDOM_IMG = "web_site_random_img";
        String WEB_SITE_SURE_COMMENT = "web_site_sure_comment";
        String WEB_SITE_SOCIAL_MEDIA = "web_site_social_media";
        String REDIS_KEY_WEB_SITE_VIEW_COUNT = "redis_key_web_site_view_count";
        String REDIS_KEY_WEB_SITE_RUN_TIME = "redis_key_web_site_run_time";
    }

    interface Article {
        // 文章删除状态
        String ARTICLE_REMOVE_STATE = "0";
        // 文章发布状态
        String ARTICLE_PUBLISH_STATE = "1";
        // 文章置顶状态
        String ARTICLE_TOP_STATE = "2";
        // 文章非删除状态
        String ARTICLE_NOT_REMOVE_STATE = "4";
        // markdown格式
        String ARTICLE_MARKDOWN_TYPE = "1";
        // 富文本格式
        String ARTICLE_RICH_TYPE = "0";
        // redis中存放的文章阅读量的key
        String REDIS_KEY_ARTICLE_CACHE = "redis_key_article_cache_";
        // redis中文章对象的json解析的键
        String REDIS_KEY_ARTICLE_VIEW_COUNT = "redis_key_article_view_count_";
        int ARTICLE_TITLE_MAX_LENGTH = 128;
        int ARTICLE_SUMMARY_MAX_LENGTH = 256;
        int ARTICLE_INIT_VIEW_COUNT = 0;
    }

    interface Label {
        int LABEL_INIT_COUNT = 1;
    }

    /**
     * 有关分页排序的常量
     */
    interface Page {
        // 默认page
        int DEFAULT_PAGE = 1;
        // 每页最大
        int MAX_SIZE = 10;
    }

    /**
     * 有关验证码设置的常量
     */
    interface CaptchaCfg {
        // 图灵验证码的字体types
        int[] FONT_TYPES = {
                Captcha.FONT_1, Captcha.FONT_2, Captcha.FONT_3, Captcha.FONT_4, Captcha.FONT_5,
                Captcha.FONT_6, Captcha.FONT_7, Captcha.FONT_8, Captcha.FONT_9, Captcha.FONT_10
        };
        // 图灵验证码字体的种类数量
        int FONT_TYPES_LENGTH = 10;
    }

    /**
     * 有关时间有效期的常量
     */
    interface TimeValue {
        // 2小时的JWT缓存时间 给redis中的token使用
        long JWT_2_HOURS = 60 * 60 * 2 * 1000L;
        // 7天的JWT缓存时间 给mysql中的refreshToken使用
        long JWT_1_WEEK = 60 * 60 * 24 * 7 * 1000L;
        // 1年的cookie有效期
        int COOKIE_1_YEAR = 60 * 60 * 24 * 365;
        // 2小时的redis缓存时间
        int REDIS_2_HOURS = 60 * 60 * 2;
        // 5分钟的redis缓存时间
        int REDIS_5_MINS = 60 * 5;
        // 5秒的redis缓存事件
        int REDIS_5_SECS = 5;
    }
}
