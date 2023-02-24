package top.woodwhale.blog.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.IFriendLinkService;
import top.woodwhale.blog.services.ILoopService;
import top.woodwhale.blog.services.IWebSiteInfoService;

/**
 * 站点信息门户 API
 */
@RestController
@RequestMapping("/portal/web_site_info")
public class WebSiteInfoPortalApi {

    /**
     * webSiteInfoService层
     */
    @Autowired
    private IWebSiteInfoService webSiteInfoService;

    /**
     * 友链service层
     */
    @Autowired
    private IFriendLinkService friendLinkService;

    /**
     * 轮播图service层
     */
    @Autowired
    private ILoopService loopService;

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
     * 获取站点浏览次数
     *
     * @return ResponseResult
     */
    @GetMapping("/view_count")
    public ResponseResult getWebSiteViewCount() {
        return webSiteInfoService.getWebSiteViewCount();
    }

    /**
     * 统计页面访问量
     * 简单的递增统计
     * 统计信息通过redis来更新，数据存入mysql
     * 只有当用户获取访问量，去将redis中的数据存入mysql
     */
    @PutMapping("/view_count")
    public void updateWebSiteViewCount() {
        webSiteInfoService.updateWebSiteViewCount();
    }

    /**
     * 获取站点SEO信息
     *
     * @return ResponseResult
     */
    @GetMapping("/seo")
    public ResponseResult getWebSiteSeoInfo() {
        return webSiteInfoService.getWebSiteSeoInfo();
    }

    /**
     * 获取轮播图
     *
     * @return ResponseResult
     */
    @GetMapping("/loop")
    public ResponseResult getLoops() {
        return loopService.listLoops();
    }

    /**
     * 获取友情链接
     *
     * @return ResponseResult
     */
    @GetMapping("/friend_link")
    public ResponseResult getLinks() {
        return friendLinkService.listFriendLinks();
    }

    /**
     * 获取博客运行时间
     *
     * @return ResponseResult
     */
    @GetMapping("/runtime")
    public ResponseResult getRunTime() {
        return webSiteInfoService.getWebSiteRunTime();
    }

    /**
     * 获取博客信息
     *
     * @return ResponseResult
     */
    @GetMapping("/user_blog_info")
    public ResponseResult getUserBlogInfo() {
        return webSiteInfoService.getUserBlogInfoFromPortal();
    }

    /**
     * 获取站点svg
     *
     * @return ResponseResult
     */
    @GetMapping("/blog_svg")
    public ResponseResult getBlogSvg() {
        return webSiteInfoService.getBlogSvg();
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
     * 获取博客随机图地址
     *
     * @return ResponseResult
     */
    @GetMapping("/blog_random_img")
    public ResponseResult getBlogRandomImg() {
        return webSiteInfoService.getBlogRandomImg();
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
     * 获取社交媒体信息
     *
     * @return ResponseResult
     */
    @GetMapping("/social_media")
    public ResponseResult getSocialMedia() {
        return webSiteInfoService.getSocialMedia();
    }
}
