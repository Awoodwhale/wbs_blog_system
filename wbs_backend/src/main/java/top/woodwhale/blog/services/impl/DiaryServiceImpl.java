package top.woodwhale.blog.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.woodwhale.blog.dao.DiaryDao;
import top.woodwhale.blog.pojo.Diary;
import top.woodwhale.blog.pojo.User;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.response.ResponseState;
import top.woodwhale.blog.services.IDiaryService;
import top.woodwhale.blog.services.IUserService;
import top.woodwhale.blog.utils.SnowflakeIdUtils;
import top.woodwhale.blog.utils.TextUtils;

import java.util.Date;

import static top.woodwhale.blog.utils.Constants.Page.DEFAULT_PAGE;
import static top.woodwhale.blog.utils.Constants.Page.MAX_SIZE;

@Slf4j
@Service
@Transactional
public class DiaryServiceImpl implements IDiaryService {

    /**
     * userService
     */
    @Autowired
    private IUserService userService;

    /**
     * diaryDao
     */
    @Autowired
    private DiaryDao diaryDao;

    /**
     * idUtils
     */
    @Autowired
    private SnowflakeIdUtils idUtils;

    /**
     * 发表日记
     *
     * @param diary 日记pojo
     * @return ResponseResult
     */
    @Override
    public ResponseResult postDiary(Diary diary) {
        try {
            User user = userService.checkUser();
            if (user == null) {
                return ResponseResult.GET(ResponseState.NOT_LOGIN);
            }

            // 判空检测
            if (TextUtils.isEmpty(diary.getContent())) {
                return ResponseResult.FAILED(TextUtils.notNullable("日记内容"));
            }
            // 存入数据库
            diary.setId(idUtils.nextId() + "");
            diary.setUserId(user.getId());
            diary.setCreateTime(new Date());
            diary.setUpdateTime(new Date());
            diaryDao.save(diary);
            return ResponseResult.SUCCESS(TextUtils.successAdd("日记"));
        } catch (Exception e) {
            log.error("日记添加错误", e);
            return ResponseResult.FAILED(TextUtils.failAdd("日记"));
        }
    }

    /**
     * 日记列表
     *
     * @param page 页数
     * @param size 每页容量
     * @return ResponseResult
     */
    @Override
    public ResponseResult listDiary(int page, int size) {
        page = Math.max(DEFAULT_PAGE, page);
        size = Math.min(MAX_SIZE, Math.max(size, 1));
        Page<Diary> all = diaryDao.findAll(
                PageRequest.of(page - 1, size,
                        Sort.by(Sort.Direction.DESC, "updateTime")));
        return ResponseResult.SUCCESS(TextUtils.successGet("日记列表")).setData(all);
    }

    /**
     * 删除日记
     * @param diaryId 日记id
     * @return ResponseResult
     */
    @Override
    public ResponseResult deleteDiary(String diaryId) {
        int res = diaryDao.deleteOneById(diaryId);
        if (res > 0) {
            return ResponseResult.SUCCESS(TextUtils.successDelete("日记"));
        }
        return ResponseResult.FAILED(TextUtils.notExist("日记Id"));
    }

    /**
     * 更新日记
     * @param diaryId 日记id
     * @param diary 日记
     * @return ResponseResult
     */
    @Override
    public ResponseResult updateDiary(String diaryId, Diary diary) {
        if (TextUtils.isEmpty(diary.getContent())) {
            return ResponseResult.FAILED(TextUtils.notNullable("日记内容"));
        }
        Diary diaryInMysql = diaryDao.findOneById(diaryId);
        if (diaryInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("日记"));
        }
        Diary copiedDiary = new Diary();

        BeanUtils.copyProperties(diaryInMysql, copiedDiary);
        copiedDiary.setContent(diary.getContent());
        copiedDiary.setUpdateTime(new Date());
        diaryInMysql =copiedDiary;

        diaryDao.save(diaryInMysql);
        return ResponseResult.SUCCESS(TextUtils.successUpdate("日记"));
    }
}

