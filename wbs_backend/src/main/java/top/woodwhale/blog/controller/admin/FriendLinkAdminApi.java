package top.woodwhale.blog.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.woodwhale.blog.interceptor.CheckTooFrequentCommit;
import top.woodwhale.blog.pojo.FriendLink;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.IFriendLinkService;

/**
 * 友情链接API
 */
@RestController
@RequestMapping("/admin/friend_link")
@PreAuthorize("@Permission.admin()")
public class FriendLinkAdminApi {

    /**
     * 载入service
     */
    @Autowired
    private IFriendLinkService friendLinkService;

    /**
     * 添加友情链接
     * @param friendLink 友链
     * @return ResponseResult
     */

    @CheckTooFrequentCommit
    @PostMapping
    public ResponseResult addFriendLink(@RequestBody FriendLink friendLink) {
        return friendLinkService.addFriendLink(friendLink);
    }

    /**
     * 根据友链URL删除友情链接
     * @param friendLinkId 友链URL
     * @return ResponseResult
     */
    @DeleteMapping("/{friendLinkId}")
    public ResponseResult deleteFriendLink(@PathVariable("friendLinkId") String friendLinkId) {
        return friendLinkService.deleteFriendLink(friendLinkId);
    }

    /**
     * 根据友链URL更新友情链接
     * @param friendLinkId 友链URL
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PutMapping("/{friendLinkId}")
    public ResponseResult updateFriendLink(@PathVariable("friendLinkId") String friendLinkId,
                                           @RequestBody FriendLink friendLinkInfo) {
        return friendLinkService.updateFriendLink(friendLinkId,friendLinkInfo);
    }

    /**
     * 根据友链URL获取友链信息
     * @param friendLinkId 友链URL
     * @return ResponseResult
     */
    @GetMapping("/{friendLinkId}")
    public ResponseResult getFriendLink(@PathVariable("friendLinkId") String friendLinkId) {
        return friendLinkService.getFriendLink(friendLinkId);
    }

    /**
     * 获取友链列表
     * @return ResponseResult
     */
    @GetMapping("/list")
    public ResponseResult listFriendLinks() {
        return friendLinkService.listFriendLinks();
    }
}
