package top.woodwhale.blog.services;

import top.woodwhale.blog.pojo.Diary;
import top.woodwhale.blog.response.ResponseResult;

public interface IDiaryService {
    ResponseResult postDiary(Diary diary);

    ResponseResult listDiary(int page, int size);

    ResponseResult deleteDiary(String diaryId);

    ResponseResult updateDiary(String diaryId, Diary diary);
}
