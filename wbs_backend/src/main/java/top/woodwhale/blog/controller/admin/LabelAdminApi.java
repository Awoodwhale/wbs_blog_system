package top.woodwhale.blog.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.woodwhale.blog.pojo.Label;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.ILabelService;

/**
 * 标签admin的api
 * 标签仅仅是统计有多少文章的
 * 删除标签不会删除文章的标签，而是不统计该标签
 */
@RestController
@RequestMapping("/admin/label")
@PreAuthorize("@Permission.admin()")
public class LabelAdminApi {

    @Autowired
    private ILabelService labelService;

    /**
     * 添加标签
     * @param label label的bean类
     * @return ResponseResult
     */
    @PostMapping
    public ResponseResult addLabel(@RequestBody Label label) {
        return labelService.addLabel(label);
    }

    /**
     * 更新标签
     * @param labelId labelId
     * @param label label
     * @return ResponseResult
     */
    @PutMapping("/{labelId}")
    public ResponseResult updateLabel(@PathVariable("labelId") String labelId, @RequestBody Label label) {
        return labelService.updateLabel(labelId,label);
    }

    /**
     * 根据id删除标签
     * @param labelId labelId
     * @return ResponseResult
     */
    @DeleteMapping("/{labelId}")
    public ResponseResult deleteLabel(@PathVariable("labelId") String labelId) {
        return labelService.deleteLabel(labelId);
    }

    /**
     * 通过标签id获取标签
     * @param id 标签id
     * @return ResponseResult
     */
    @GetMapping("/{labelId}")
    public ResponseResult showLabel(@PathVariable("labelId") String id) {
        return labelService.getLabelById(id);
    }

    /**
     * 获取标签集合
     * @return ResponseResult
     */
    @GetMapping("/list")
    public ResponseResult listLabels() {
        return labelService.listLabels();
    }

}
