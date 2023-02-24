package top.woodwhale.blog.services;

import top.woodwhale.blog.pojo.SocialMedia;
import top.woodwhale.blog.response.ResponseResult;

import java.util.Map;

public interface IWebSiteInfoService {
    ResponseResult putWebSiteTitle(String title);

    ResponseResult getWebSiteSeoInfo();

    ResponseResult putWebSiteSeoInfo(String keywords, String description);

    ResponseResult getWebSiteViewCount();

    ResponseResult getWebSiteTitle();

    void updateWebSiteViewCount();

    ResponseResult getWebSiteRunTime();

    ResponseResult getUserBlogInfo();

    ResponseResult getUserBlogInfoFromPortal();

    ResponseResult getBlogSvg();

    ResponseResult setBlogSvg(Map<String, String> svg);

    ResponseResult getBlogH1();

    ResponseResult setBlogH1(String h1, String sign);

    ResponseResult getBlogRandomImg();

    ResponseResult setBlogRandomImg(String url);

    ResponseResult getSureComment();

    ResponseResult setSureComment();

    ResponseResult getSocialMedia();

    ResponseResult setSocialMedia(SocialMedia sm);
}
