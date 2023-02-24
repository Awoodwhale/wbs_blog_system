package top.woodwhale.blog.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.woodwhale.blog.interceptor.CheckTooFrequentCommit;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.IImageService;

/**
 * 图片API
 */
@RestController
@RequestMapping("/admin/image")
@PreAuthorize("@Permission.admin()")
public class ImageAdminApi {

    /**
     * 注入图片service
     */
    @Autowired
    private IImageService imageService;

    /**
     * ResponseResult
     * @param file 图片文件
     * @return ResponseResult
     */
    @CheckTooFrequentCommit
    @PostMapping
    public ResponseResult uploadImage(@RequestPart("file") MultipartFile file) {
        return imageService.uploadImage(file);
    }

    /**
     * 根据图片ID删除图片
     * @param imageId 图片ID
     * @return ResponseResult
     */
    @DeleteMapping("/{imageId}")
    public ResponseResult deleteImage(@PathVariable("imageId") String imageId) {
        return imageService.deleteImage(imageId);
    }

    /**
     * 获取图片列表
     * @param page 页数
     * @param size 每页容量
     * @return ResponseResult
     */
    @GetMapping("/list")
    public ResponseResult listImages(@RequestParam("page")int page, @RequestParam("size") int size) {
        return imageService.listImages(page,size);
    }
}
