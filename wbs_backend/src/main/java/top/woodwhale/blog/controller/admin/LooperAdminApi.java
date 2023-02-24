package top.woodwhale.blog.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.woodwhale.blog.interceptor.CheckTooFrequentCommit;
import top.woodwhale.blog.pojo.Looper;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.ILoopService;

/**
 * 轮播图API
 */
@RestController
@RequestMapping("/admin/loop")
@PreAuthorize("@Permission.admin()")
public class LooperAdminApi {

    /**
     * 注入的loopService层
     */
    @Autowired
    private ILoopService loopService;

    /**
     * 上传轮播图
     * @param looper 轮播图Bean类
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PostMapping
    public ResponseResult addLoop(@RequestBody Looper looper) {
        return loopService.addLoop(looper);
    }

    /**
     * 根据轮播图ID删除轮播图
     * @param looperId 轮播图ID
     * @return ResponseResult
     */
    @DeleteMapping("/{looperId}")
    public ResponseResult deleteLoop(@PathVariable("looperId") String looperId) {
        return loopService.deleteLoop(looperId);
    }

    /**
     * 根据轮播图ID更新轮播图
     * @param looperId 轮播图ID
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PutMapping("/{looperId}")
    public ResponseResult updateLoop(@PathVariable("looperId") String looperId, @RequestBody Looper loop) {
        return loopService.updateLoop(looperId,loop);
    }

    /**
     * 根据轮播图ID获取轮播图
     * @param looperId 轮播图
     * @return ResponseResult
     */
    @GetMapping("/{looperId}")
    public ResponseResult getLoop(@PathVariable("looperId") String looperId) {
        return loopService.getLoop(looperId);
    }

    /**
     * 获取轮播图列表
     * @return ResponseResult
     */
    @GetMapping("/list")
    public ResponseResult listLooper() {
        return loopService.listLoops();
    }
}
