package top.woodwhale.blog.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.woodwhale.blog.dao.DiaryDao;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.IDiaryService;

@RestController
@RequestMapping("/portal/diary")
public class DiaryPortalApi {

    /**
     * diaryDao
     */
    @Autowired
    private DiaryDao diaryDao;

    /**
     * diaryService
     */
    @Autowired
    private IDiaryService diaryService;

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
}
