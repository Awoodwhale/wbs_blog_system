package top.woodwhale.blog.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.woodwhale.blog.dao.LooperDao;
import top.woodwhale.blog.pojo.Looper;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.ILoopService;
import top.woodwhale.blog.utils.SnowflakeIdUtils;
import top.woodwhale.blog.utils.TextUtils;

import java.util.Date;
import java.util.List;

import static top.woodwhale.blog.utils.Constants.Image.IMAGE_NORMAL_STATE;
import static top.woodwhale.blog.utils.Constants.Image.IMAGE_REMOVE_STATE;

@Slf4j
@Service
@Transactional
public class LoopServiceImpl implements ILoopService {

    /**
     * 注入ID生成器
     */
    @Autowired
    private SnowflakeIdUtils idUtils;

    @Autowired
    private LooperDao looperDao;

    /**
     * 添加轮播图
     * @param looper 轮播图信息
     * @return ResponseResult
     */
    @Override
    public ResponseResult addLoop(Looper looper) {
        if (looper == null) {
            return ResponseResult.FAILED(TextUtils.notNullable("轮播图信息"));
        }
        if (TextUtils.isEmpty(looper.getTitle())) {
            return ResponseResult.FAILED(TextUtils.notNullable("轮播图标题"));
        }
        if (TextUtils.isEmpty(looper.getImageUrl())) {
            return ResponseResult.FAILED(TextUtils.notNullable("轮播图展示Url"));
        }
        if (TextUtils.isEmpty(looper.getTargetUrl())) {
            return ResponseResult.FAILED(TextUtils.notNullable("轮播图跳转Url"));
        }
        looper.setState(IMAGE_NORMAL_STATE);
        looper.setId(String.valueOf(idUtils.nextId()));
        looper.setCreateTime(new Date());
        looper.setUpdateTime(new Date());

        looperDao.save(looper);
        return ResponseResult.SUCCESS(TextUtils.successAdd("轮播图"));
    }

    /**
     * 获取轮播图
     * @param looperId 轮播图id
     * @return ResponseResult
     */
    @Override
    public ResponseResult getLoop(String looperId) {
        Looper loop = looperDao.findOneById(looperId);
        if (loop == null || loop.getState().equals(IMAGE_REMOVE_STATE)) {
            return ResponseResult.FAILED(TextUtils.notExist("轮播图"));
        }
        return ResponseResult.SUCCESS(TextUtils.successGet("轮播图")).setData(loop);
    }

    /**
     * 获取轮播图列表
     * @return ResponseResult
     */
    @Override
    public ResponseResult listLoops() {
        List<Looper> loopLists = looperDao.findAll((Specification<Looper>)
                        (root, query, criteriaBuilder) ->
                                criteriaBuilder.equal(root.get("state").as(String.class), IMAGE_NORMAL_STATE)
                , Sort.by(Sort.Direction.DESC, "createTime"));
        return ResponseResult.SUCCESS(TextUtils.successGet("轮播图列表")).setData(loopLists);
    }

    /**
     * 更新轮播图
     * @param looperId 轮播图id
     * @param looper loop bean
     * @return ResponseResult
     */
    @Override
    public ResponseResult updateLoop(String looperId, Looper looper) {
        // 判空
        if (TextUtils.isEmpty(looperId)) {
            return ResponseResult.FAILED(TextUtils.notNullable("轮播图Id"));
        }
        Looper loopInMysql = looperDao.findOneById(looperId);
        if (loopInMysql == null) {
            return ResponseResult.FAILED(TextUtils.notExist("轮播图"));
        }
        // 复制一份looper
        Looper copiedLooper = new Looper();
        BeanUtils.copyProperties(loopInMysql,copiedLooper);
        if (!TextUtils.isEmpty(looper.getTitle())) {
            copiedLooper.setTitle(looper.getTitle());
        } else {
            return ResponseResult.FAILED(TextUtils.notNullable("轮播图标题"));
        }
        if (!TextUtils.isEmpty(looper.getTargetUrl())) {
            copiedLooper.setTargetUrl(looper.getTargetUrl());
        } else {
            return ResponseResult.FAILED(TextUtils.notNullable("轮播图跳转Url"));
        }
        if (!TextUtils.isEmpty(looper.getImageUrl())) {
            copiedLooper.setImageUrl(looper.getImageUrl());
        } else {
            return ResponseResult.FAILED(TextUtils.notNullable("轮播图展示Url"));
        }
        copiedLooper.setOrder(looper.getOrder());
        copiedLooper.setUpdateTime(new Date());
        // 保存到MySQL中
        loopInMysql = copiedLooper;
        looperDao.save(loopInMysql);
        return ResponseResult.SUCCESS(TextUtils.successUpdate("轮播图"));
    }

    /**
     * 根据ID删除轮播图
     * @param looperId 轮播图id
     * @return ResponseResult
     */
    @Override
    public ResponseResult deleteLoop(String looperId) {
        if (TextUtils.isEmpty(looperId)) {
            return ResponseResult.FAILED(TextUtils.notNullable("轮播图Id"));
        }
        // 判断是不是多项删除
        if (looperId.contains("_") && looperId.split("_").length > 0) {
            for (String id : looperId.split("_")) {
                int res = looperDao.deleteOneById(id);
                if (res <= 0) {
                    return ResponseResult.FAILED(TextUtils.failDelete("轮播图id " + id + " "));
                }
            }
            return ResponseResult.SUCCESS(TextUtils.successDelete("轮播图"));
        } else {
            int res = looperDao.deleteOneById(looperId);
            if (res > 0) {
                return ResponseResult.SUCCESS(TextUtils.successDelete("轮播图"));
            }
        }
        return ResponseResult.FAILED(TextUtils.failDelete("轮播图id " + looperId + " "));
    }
}
