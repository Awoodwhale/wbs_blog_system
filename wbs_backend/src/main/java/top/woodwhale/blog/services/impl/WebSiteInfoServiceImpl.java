package top.woodwhale.blog.services.impl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.woodwhale.blog.dao.*;
import top.woodwhale.blog.pojo.*;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.response.ResponseState;
import top.woodwhale.blog.services.IUserService;
import top.woodwhale.blog.services.IWebSiteInfoService;
import top.woodwhale.blog.utils.RedisUtils;
import top.woodwhale.blog.utils.SnowflakeIdUtils;
import top.woodwhale.blog.utils.TextUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static top.woodwhale.blog.utils.Constants.Settings.*;

@Slf4j
@Service
@Transactional
public class WebSiteInfoServiceImpl implements IWebSiteInfoService {

    @Autowired
    private RedisUtils redisUtils;

    /**
     * setting的dao层
     */
    @Autowired
    private SettingDao settingDao;

    /**
     * id生成器
     */
    @Autowired
    private SnowflakeIdUtils idUtils;

    /**
     * userService
     */
    @Autowired
    private IUserService userService;

    /**
     * articleDao
     */
    @Autowired
    private ArticleDao articleDao;

    /**
     * categoryDao
     */
    @Autowired
    private CategoryDao categoryDao;

    /**
     * friendLinkDao
     */
    @Autowired
    private FriendLinkDao friendLinkDao;

    /**
     * commentDao
     */
    @Autowired
    private CommentDao commentDao;

    /**
     * labelDao
     */
    @Autowired
    private LabelDao labelDao;

    /**
     * diaryDao
     */
    @Autowired
    private DiaryDao diaryDao;

    /**
     * articleSketchNoContentDao
     */
    @Autowired
    private ArticleSketchNoContentDao articleSketchNoContentDao;

    /**
     * 获取网站title
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult getWebSiteTitle() {
        Setting settingInMysql = settingDao.findOneByKey(WEB_SITE_TITLE);
        if (settingInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("站点标题"));
        }
        HashMap<String, String> map = new HashMap<>();
        map.put(settingInMysql.getKey(), settingInMysql.getValue());
        return ResponseResult.SUCCESS(TextUtils.successGet("站点标题")).setData(map);
    }

    /**
     * 设置网站title
     *
     * @param title 网站标题
     * @return ResponseResult
     */
    @Override
    public ResponseResult putWebSiteTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            return ResponseResult.FAILED(TextUtils.notNullable("站点标题"));
        }
        Setting settingInMysql = settingDao.findOneByKey(WEB_SITE_TITLE);
        if (settingInMysql == null) {
            settingInMysql = new Setting();
            settingInMysql.setId(String.valueOf(idUtils.nextId()));
            settingInMysql.setCreateTime(new Date());
            settingInMysql.setKey(WEB_SITE_TITLE);
        }
        settingInMysql.setUpdateTime(new Date());
        settingInMysql.setValue(title);
        settingDao.save(settingInMysql);
        return ResponseResult.SUCCESS(TextUtils.successUpdate("站点标题"));
    }

    /**
     * 获取seo信息
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult getWebSiteSeoInfo() {
        Setting keywords = settingDao.findOneByKey(WEB_SITE_KEYWORDS);
        Setting description = settingDao.findOneByKey(WEB_SITE_DESCRIPTION);
        if (keywords == null || description == null) {
            return ResponseResult.FAILED(TextUtils.notExist("站点SEO"));
        }
        HashMap<String, String> map = new HashMap<>();
        map.put(keywords.getKey(), keywords.getValue());
        map.put(description.getKey(), description.getValue());
        return ResponseResult.SUCCESS(TextUtils.successGet("站点SEO")).setData(map);
    }

    /**
     * 更新seo信息
     *
     * @param keywords    关键词
     * @param description 描述
     * @return ResponseResult
     */
    @Override
    public ResponseResult putWebSiteSeoInfo(String keywords, String description) {
        if (TextUtils.isEmpty(keywords)) {
            return ResponseResult.FAILED(TextUtils.notNullable("关键字"));
        }
        if (TextUtils.isEmpty(description)) {
            return ResponseResult.FAILED(TextUtils.notNullable("描述"));
        }
        Setting keywordsSetting = settingDao.findOneByKey(WEB_SITE_KEYWORDS);
        Setting descriptionSetting = settingDao.findOneByKey(WEB_SITE_DESCRIPTION);
        if (keywordsSetting == null) {
            keywordsSetting = new Setting();
            keywordsSetting.setId(String.valueOf(idUtils.nextId()));
            keywordsSetting.setCreateTime(new Date());
            keywordsSetting.setKey(WEB_SITE_KEYWORDS);
        }
        keywordsSetting.setUpdateTime(new Date());
        keywordsSetting.setValue(keywords);
        settingDao.save(keywordsSetting);
        if (descriptionSetting == null) {
            descriptionSetting = new Setting();
            descriptionSetting.setId(String.valueOf(idUtils.nextId()));
            descriptionSetting.setCreateTime(new Date());
            descriptionSetting.setKey(WEB_SITE_DESCRIPTION);
        }
        descriptionSetting.setUpdateTime(new Date());
        descriptionSetting.setValue(description);
        settingDao.save(descriptionSetting);
        return ResponseResult.SUCCESS(TextUtils.successUpdate("站点SEO"));
    }

    /**
     * 获取网站浏览次数
     * 在拦截器中添加浏览次数
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult getWebSiteViewCount() {
        // 去MySQL中获取上一次更新的访问量
        Setting viewCount = judgeWebSiteViewCountIsNull();
        // 从redis中获取最新的访问量
        String viewCountInRedis = (String) redisUtils.get(REDIS_KEY_WEB_SITE_VIEW_COUNT);
        if (TextUtils.isEmpty(viewCountInRedis)) {
            viewCountInRedis = viewCount.getValue();
            redisUtils.set(REDIS_KEY_WEB_SITE_VIEW_COUNT, viewCountInRedis);
        } else {
            // 把redis中的值传入mysql
            viewCount.setValue(viewCountInRedis);
            viewCount.setUpdateTime(new Date());
            settingDao.save(viewCount);
        }
        HashMap<String, String> map = new HashMap<>();
        map.put(viewCount.getKey(), viewCount.getValue());
        return ResponseResult.SUCCESS(TextUtils.successGet("站点浏览量")).setData(map);
    }

    /**
     * 更新站点访问量
     */
    @Override
    public void updateWebSiteViewCount() {
        Object viewCount = redisUtils.get(REDIS_KEY_WEB_SITE_VIEW_COUNT);
        if (viewCount == null) {
            Setting setting = judgeWebSiteViewCountIsNull();
            String viewCountStr = setting.getValue();
            redisUtils.set(REDIS_KEY_WEB_SITE_VIEW_COUNT, viewCountStr);
        }
        // 自增
        redisUtils.incr(REDIS_KEY_WEB_SITE_VIEW_COUNT, 1);
    }

    /**
     * 获取站点运行时间
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult getWebSiteRunTime() {
        String runtime = (String) redisUtils.get(REDIS_KEY_WEB_SITE_RUN_TIME);
        if (TextUtils.isEmpty(runtime)) {
            redisUtils.set(REDIS_KEY_WEB_SITE_RUN_TIME, "1");
            runtime = "1";
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("web_site_runtime", runtime);
        return ResponseResult.SUCCESS().setData(map);
    }

    /**
     * 判断MySQL中的访问量setting是否初始化
     *
     * @return Setting
     */
    private Setting judgeWebSiteViewCountIsNull() {
        Setting setting = settingDao.findOneByKey(WEB_SITE_VIEW_COUNT);
        if (setting == null) {
            setting = new Setting();
            setting.setId(String.valueOf(idUtils.nextId()));
            setting.setKey(WEB_SITE_VIEW_COUNT);
            setting.setCreateTime(new Date());
            setting.setUpdateTime(new Date());
            setting.setValue(DEFAULT_VIEW_COUNT);
            settingDao.save(setting);
        }
        return setting;
    }


    /**
     * 获取博客信息
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult getUserBlogInfo() {
        User user = userService.checkUser();
        if (user == null) {
            return ResponseResult.GET(ResponseState.NOT_LOGIN);
        }
        try {
            // 文章数量
            int articleCount = articleDao.findAll((Specification<Article>) (root, query, cb) ->
                    cb.and(cb.equal(root.get("userId").as(String.class), user.getId()),
                            cb.notEqual(root.get("state").as(String.class), "0"))).size();
            // 草稿数量
            int sketchCount = articleSketchNoContentDao.findAll((Specification<ArticleSketchNoContent>) (root, query, cb) ->
                    cb.notEqual(root.get("userId").as(String.class), user.getId())).size();
            // 分类数量
            int categoryCount = categoryDao.findAll((Specification<Category>) (root, query, cb) ->
                    cb.notEqual(root.get("state").as(String.class), "0")).size();
            // 标签数量
            int labelCount = labelDao.findAll().size();
            // 评论数量
            int commentCount = commentDao.findAll((Specification<Comment>) (root, query, cb) ->
                    cb.notEqual(root.get("state").as(String.class), "0")).size();
            // 友链数量
            int friendLinkCount = friendLinkDao.findAll((Specification<FriendLink>) (root, query, cb) ->
                    cb.notEqual(root.get("state").as(String.class), "0")).size();
            // 阅读数量
            String viewCountInRedis = (String) ((HashMap<?, ?>) getWebSiteViewCount().getData()).get("web_site_view_count");
            int viewCount = 0;
            if (!TextUtils.isEmpty(viewCountInRedis)) {
                viewCount = Integer.parseInt(viewCountInRedis);
            }
            // 站点运行时间
            String runTime = (String) redisUtils.get(REDIS_KEY_WEB_SITE_RUN_TIME);
            if (TextUtils.isEmpty(runTime)) {
                redisUtils.set(REDIS_KEY_WEB_SITE_RUN_TIME, "1");
                runTime = "1";
            }
            // 发送数据
            HashMap<String, String> map = new HashMap<>();
            map.put("article", articleCount + "");
            map.put("sketch", sketchCount + "");
            map.put("category", categoryCount + "");
            map.put("label", labelCount + "");
            map.put("comment", commentCount + "");
            map.put("friendLink", friendLinkCount + "");
            map.put("viewCount", viewCount + "");
            map.put("runTime", runTime);

            return ResponseResult.SUCCESS(TextUtils.successGet("博客信息")).setData(map);
        } catch (Exception e) {
            log.error("获取博客信息失败" + e);
            return ResponseResult.FAILED(TextUtils.smtError("博客信息"));
        }
    }

    /**
     * 前端门户获取文章信息
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult getUserBlogInfoFromPortal() {
        try {
            // 文章数量
            int articleCount = articleDao.findAll((Specification<Article>) (root, query, cb) ->
                    cb.notEqual(root.get("state").as(String.class), "0")).size();
            // 评论数量
            int commentCount = commentDao.findAll((Specification<Comment>) (root, query, cb) ->
                    cb.notEqual(root.get("state").as(String.class), "0")).size();
            // 说说数量
            int diaryCount = diaryDao.findAll().size();
            // 分类数量
            int categoryCount = categoryDao.findAll((Specification<Category>) (root, query, cb) ->
                    cb.notEqual(root.get("state").as(String.class), "0")).size();
            // 站点运行时间
            String runTime = (String) redisUtils.get(REDIS_KEY_WEB_SITE_RUN_TIME);
            if (TextUtils.isEmpty(runTime)) {
                redisUtils.set(REDIS_KEY_WEB_SITE_RUN_TIME, "1");
                runTime = "1";
            }
            // 发送数据
            HashMap<String, String> map = new HashMap<>();
            map.put("diary", diaryCount + "");
            map.put("article", articleCount + "");
            map.put("category", categoryCount + "");
            map.put("comment", commentCount + "");
            map.put("runTime", runTime);
            return ResponseResult.SUCCESS(TextUtils.successGet("博客信息")).setData(map);
        } catch (Exception e) {
            log.error("获取博客信息失败" + e);
            return ResponseResult.FAILED(TextUtils.smtError("博客信息"));
        }
    }

    /**
     * 获取网站svg图标
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult getBlogSvg() {
        Setting settingInMysql = settingDao.findOneByKey(WEB_SITE_SVG);
        if (settingInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("SVG信息"));
        }
        HashMap<String, String> map = new HashMap<>();
        map.put(settingInMysql.getKey(), settingInMysql.getValue());
        return ResponseResult.SUCCESS(TextUtils.successGet("SVG信息")).setData(map);
    }

    /**
     * 设置网站svg
     *
     * @param svg base64
     * @return ResponseResult
     */
    @Override
    public ResponseResult setBlogSvg(Map<String, String> svg) {
        String url = svg.get("svg");
        if (TextUtils.isEmpty(url)) {
            return ResponseResult.FAILED(TextUtils.notNullable("SVG信息"));
        }
        Setting settingInMysql = settingDao.findOneByKey(WEB_SITE_SVG);
        if (settingInMysql == null) {
            Setting setting = new Setting();
            setting.setId(idUtils.nextId() + "");
            setting.setKey(WEB_SITE_SVG);
            setting.setValue(url);
            setting.setCreateTime(new Date());
            setting.setUpdateTime(new Date());
            settingDao.save(setting);
            return ResponseResult.SUCCESS(TextUtils.successUpdate("SVG信息"));
        }
        settingInMysql.setUpdateTime(new Date());
        settingInMysql.setValue(url);
        settingDao.save(settingInMysql);
        return ResponseResult.SUCCESS(TextUtils.successUpdate("SVG信息"));
    }

    /**
     * 获取博客首页屏幕的文字
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult getBlogH1() {
        Setting h1Setting = settingDao.findOneByKey(WEB_SITE_H1);
        Setting signSetting = settingDao.findOneByKey(WEB_SITE_SIGN);
        if (h1Setting == null || signSetting == null) {
            return ResponseResult.FAILED(TextUtils.notExist("文字信息"));
        }
        String h1 = h1Setting.getValue();
        String sign = signSetting.getValue();
        HashMap<String, String> map = new HashMap<>();
        map.put(h1Setting.getKey(), h1);
        map.put(signSetting.getKey(), sign);
        return ResponseResult.SUCCESS(TextUtils.successGet("文字信息")).setData(map);
    }

    /**
     * 设置博客首页屏幕的文字
     *
     * @param h1   文字标题
     * @param sign 文字签名
     * @return ResponseResult
     */
    @Override
    public ResponseResult setBlogH1(String h1, String sign) {
        if (TextUtils.isEmpty(h1) || TextUtils.isEmpty(sign)) {
            return ResponseResult.FAILED(TextUtils.notNullable("文字信息"));
        }
        Setting h1SettingInMysql = settingDao.findOneByKey(WEB_SITE_H1);
        if (h1SettingInMysql == null) {
            Setting setting = new Setting();
            setting.setKey(WEB_SITE_H1);
            setting.setId(idUtils.nextId() + "");
            setting.setValue(h1);
            setting.setCreateTime(new Date());
            setting.setUpdateTime(new Date());
            settingDao.save(setting);
        } else {
            h1SettingInMysql.setUpdateTime(new Date());
            h1SettingInMysql.setValue(h1);
            settingDao.save(h1SettingInMysql);
        }
        Setting signSettingInMysql = settingDao.findOneByKey(WEB_SITE_SIGN);
        if (signSettingInMysql == null) {
            Setting setting = new Setting();
            setting.setValue(sign);
            setting.setKey(WEB_SITE_SIGN);
            setting.setId(idUtils.nextId() + "");
            setting.setCreateTime(new Date());
            setting.setUpdateTime(new Date());
            settingDao.save(setting);
        } else {
            signSettingInMysql.setUpdateTime(new Date());
            signSettingInMysql.setValue(sign);
            settingDao.save(signSettingInMysql);
        }
        return ResponseResult.SUCCESS(TextUtils.successUpdate("文字信息"));
    }

    /**
     * 获取博客随机图地址
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult getBlogRandomImg() {
        Setting settingInMysql = settingDao.findOneByKey(WEB_SITE_RANDOM_IMG);
        if (settingInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("随机图信息"));
        }
        HashMap<String, String> map = new HashMap<>();
        map.put(settingInMysql.getKey(), settingInMysql.getValue());
        return ResponseResult.SUCCESS(TextUtils.successGet("随机图信息")).setData(map);
    }

    /**
     * 设置博客随机图地址
     *
     * @param url url
     * @return ResponseResult
     */
    @Override
    public ResponseResult setBlogRandomImg(String url) {
        if (TextUtils.isEmpty(url)) {
            return ResponseResult.FAILED(TextUtils.notNullable("随机图信息"));
        }
        if (!(url.startsWith("http") && url.contains("://"))) {
            return ResponseResult.FAILED("URL格式错误");
        }
        Setting settingInMysql = settingDao.findOneByKey(WEB_SITE_RANDOM_IMG);
        if (settingInMysql == null) {
            Setting setting = new Setting();
            setting.setId(idUtils.nextId() + "");
            setting.setValue(url);
            setting.setKey(WEB_SITE_RANDOM_IMG);
            setting.setCreateTime(new Date());
            setting.setUpdateTime(new Date());
            settingDao.save(setting);
            return ResponseResult.SUCCESS(TextUtils.successUpdate("随机图信息"));
        }
        settingInMysql.setValue(url);
        settingInMysql.setUpdateTime(new Date());
        settingDao.save(settingInMysql);
        return ResponseResult.SUCCESS(TextUtils.successUpdate("随机图信息"));
    }

    /**
     * 获取是否可以评论的结果
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult getSureComment() {
        Setting settingInMysql = settingDao.findOneByKey(WEB_SITE_SURE_COMMENT);
        if (settingInMysql == null || settingInMysql.getValue().equals("true")) {
            return ResponseResult.SUCCESS(TextUtils.successGet("评论权限")).setData("true");
        }
        return ResponseResult.SUCCESS(TextUtils.successGet("评论权限")).setData("false");
    }

    /**
     * 设置是否可以评论
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult setSureComment() {
        Setting settingInMysql = settingDao.findOneByKey(WEB_SITE_SURE_COMMENT);
        if (settingInMysql == null) {
            Setting setting = new Setting();
            setting.setId(idUtils.nextId() + "");
            setting.setValue("true");
            setting.setKey(WEB_SITE_SURE_COMMENT);
            setting.setCreateTime(new Date());
            setting.setUpdateTime(new Date());
            settingDao.save(setting);
            return ResponseResult.SUCCESS("评论权限初始化成功").setData("true");
        }
        String p = settingInMysql.getValue();
        settingInMysql.setValue(p.equals("true") ? "false" : "true");
        settingInMysql.setUpdateTime(new Date());
        settingDao.save(settingInMysql);
        return ResponseResult
                .SUCCESS(TextUtils.successUpdate("评论权限"))
                .setData(p.equals("true") ? "false" : "true");
    }

    /**
     * 获取社交媒体信息
     *
     * @return ResponseResult
     */
    @Override
    public ResponseResult getSocialMedia() {
        Setting settingInMysql = settingDao.findOneByKey(WEB_SITE_SOCIAL_MEDIA);
        if (settingInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("媒体信息"));
        }
        return ResponseResult
                .SUCCESS(TextUtils.successGet("媒体信息"))
                .setData(new Gson().fromJson(settingInMysql.getValue(),SocialMedia.class));
    }

    /**
     * 设置社交媒体信息
     *
     * @param sm sm
     * @return ResponseResult
     */
    @Override
    public ResponseResult setSocialMedia(SocialMedia sm) {
        if (!TextUtils.isMail(sm.getMail())) {
            return ResponseResult.FAILED("邮箱格式错误");
        }
        if (!sm.checkIsUrl()) {
            return ResponseResult.FAILED("网站URL格式错误");
        }
        Setting settingInMysql = settingDao.findOneByKey(WEB_SITE_SOCIAL_MEDIA);
        if (settingInMysql == null) {
            Setting setting = new Setting();
            setting.setValue(new Gson().toJson(sm));
            setting.setCreateTime(new Date());
            setting.setUpdateTime(new Date());
            setting.setId(idUtils.nextId() + "");
            setting.setKey(WEB_SITE_SOCIAL_MEDIA);
            settingDao.save(setting);
            return ResponseResult.SUCCESS(TextUtils.successUpdate("媒体信息"));
        }
        settingInMysql.setUpdateTime(new Date());
        settingInMysql.setValue(new Gson().toJson(sm));
        return ResponseResult.SUCCESS(TextUtils.successUpdate("媒体信息"));
    }
}
