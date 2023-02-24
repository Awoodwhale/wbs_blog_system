package top.woodwhale.blog.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.woodwhale.blog.pojo.Diary;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.IDiaryService;

@RestController
@RequestMapping("/admin/diary")
@PreAuthorize("@Permission.admin()")
public class DiaryAdminApi {

    /**
     * 日记service
     */
    @Autowired
    private IDiaryService diaryService;

    /**
     * 发表日记
     * @param diary 日记
     * @return ResponseResult
     */
    @PostMapping
    public ResponseResult postDiary(@RequestBody Diary diary) {
        return diaryService.postDiary(diary);
    }

    /**
     * 日记列表
     * @param page 页数
     * @param size 每页大小
     * @return ResponseResult
     */
    @GetMapping("/list")
    public ResponseResult listDiary(@RequestParam("page")int page, @RequestParam("size") int size) {
        return diaryService.listDiary(page,size);
    }

    /**
     * 删除日记
     * @param diaryId 日记id
     * @return ResponseResult
     */
    @DeleteMapping("/{diaryId}")
    public ResponseResult deleteDiary(@PathVariable("diaryId") String diaryId) {
        return diaryService.deleteDiary(diaryId);
    }

    /**
     * 更改日记内容
     * @param diaryId 日记id
     * @param diary 日记
     * @return ResponseResult
     */
    @PutMapping("/{diaryId}")
    public ResponseResult updateDiary(@PathVariable("diaryId") String diaryId, @RequestBody Diary diary) {
        return diaryService.updateDiary(diaryId,diary);
    }
}
