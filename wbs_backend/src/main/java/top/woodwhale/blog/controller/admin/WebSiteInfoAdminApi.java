package top.woodwhale.blog.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.woodwhale.blog.interceptor.CheckTooFrequentCommit;
import top.woodwhale.blog.pojo.SocialMedia;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.IUserService;
import top.woodwhale.blog.services.IWebSiteInfoService;

import java.util.Map;

/**
 * 站点信息API
 */
@RestController
@RequestMapping("/admin/web_site_info")
@PreAuthorize("@Permission.admin()")
public class WebSiteInfoAdminApi {

    /**
     * 注入webSiteInfoService 服务层
     */
    @Autowired
    private IWebSiteInfoService webSiteInfoService;

    /**
     * 注入userService
     */
    @Autowired
    private IUserService userService;

    /**
     * 获取站点标题
     *
     * @return ResponseResult
     */
    @GetMapping("/title")
    public ResponseResult getWebSiteTitle() {
        return webSiteInfoService.getWebSiteTitle();
    }

    /**
     * 更新站点标题
     *
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PutMapping("/title")
    public ResponseResult updateWebSiteTitle(@RequestParam("title") String title) {
        return webSiteInfoService.putWebSiteTitle(title);
    }

    /**
     * 获取网站SEO
     *
     * @return ResponseResult
     */
    @GetMapping("/seo")
    public ResponseResult getSeoInfo() {
        return webSiteInfoService.getWebSiteSeoInfo();
    }

    /**
     * 更新网站SEO
     *
     * @param keywords    关键字
     * @param description 描述
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PutMapping("/seo")
    public ResponseResult putSeoInfo(@RequestParam("keywords") String keywords,
                                     @RequestParam("description") String description) {
        return webSiteInfoService.putWebSiteSeoInfo(keywords, description);
    }

    /**
     * 获取网站浏览次数
     *
     * @return ResponseResult
     */
    @GetMapping("/view_count")
    public ResponseResult getWebSiteViewCount() {
        return webSiteInfoService.getWebSiteViewCount();
    }

    /**
     * 获取用户文章发布等信息
     *
     * @return ResponseResult
     */
    @GetMapping("/user_blog_info")
    public ResponseResult getUserBlogInfo() {
        return webSiteInfoService.getUserBlogInfo();
    }

    /**
     * 获取网站svg标签图
     *
     * @return ResponseResult
     */
    @GetMapping("/blog_svg")
    public ResponseResult getBlogSvg() {
        return webSiteInfoService.getBlogSvg();
    }

    /**
     * 设置网站svg标签 base64形式
     *
     * @param svgMap base64
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PostMapping("/blog_svg")
    public ResponseResult setBlogSvg(@RequestBody Map<String, String> svgMap) {
        return webSiteInfoService.setBlogSvg(svgMap);
    }

    /**
     * 获取首页文字信息
     *
     * @return ResponseResult
     */
    @GetMapping("/blog_h1")
    public ResponseResult getBlogH1() {
        return webSiteInfoService.getBlogH1();
    }

    /**
     * 设置首页文字信息
     *
     * @param h1   h1
     * @param sign sign
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PutMapping("/blog_h1")
    public ResponseResult setBlogH1(@RequestParam("h1") String h1, @RequestParam("sign") String sign) {
        return webSiteInfoService.setBlogH1(h1, sign);
    }

    /**
     * 获取博客随机图地址
     *
     * @return ResponseResult
     */
    @GetMapping("/blog_random_img")
    public ResponseResult getBlogRandomImg() {
        return webSiteInfoService.getBlogRandomImg();
    }

    /**
     * 更新随机图地址
     *
     * @param url url
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PutMapping("/blog_random_img")
    public ResponseResult setBlogRandomImg(@RequestParam("url") String url) {
        return webSiteInfoService.setBlogRandomImg(url);
    }

    /**
     * 获取是否可以评论的状态
     *
     * @return ResponseResult
     */
    @GetMapping("/sure_comment")
    public ResponseResult getSureComment() {
        return webSiteInfoService.getSureComment();
    }

    /**
     * 设置是否有权限评论
     *
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PutMapping("/sure_comment")
    public ResponseResult setSureComment() {
        return webSiteInfoService.setSureComment();
    }


    /**
     * 获取社交媒体信息
     *
     * @return ResponseResult
     */
    @GetMapping("/social_media")
    public ResponseResult getSocialMedia() {
        return webSiteInfoService.getSocialMedia();
    }

    /**
     * 设置社交媒体信息
     *
     * @param sm SocialMedia
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PostMapping("/social_media")
    public ResponseResult setSocialMedia(@RequestBody SocialMedia sm) {
        return webSiteInfoService.setSocialMedia(sm);
    }
}
