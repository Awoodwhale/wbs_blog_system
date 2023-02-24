package top.woodwhale.blog.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.ISolrService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 搜索门户 API
 */
@RestController
@RequestMapping("/portal/search")
public class SearchPortalApi {

    /**
     * 注入进来的solrService
     */
    @Autowired
    private ISolrService solrService;

    /**
     * 根据关键词搜索
     * @param keyword 关键词
     * @param page 页数
     * @return ResponseResult
     */
    @GetMapping
    public ResponseResult doSearch(@RequestParam("keyword") String keyword,
        @RequestParam("page") int page,
        @RequestParam("size") int size,
        @RequestParam(value = "categoryId",required = false) String categoryId,
        @RequestParam(value = "sort",required = false) Integer sort) {
        keyword = URLDecoder.decode(keyword, StandardCharsets.UTF_8);
        System.out.println("keyword --> "+keyword);
        return solrService.doSearch(keyword,page,size,categoryId,sort);
    }
}
