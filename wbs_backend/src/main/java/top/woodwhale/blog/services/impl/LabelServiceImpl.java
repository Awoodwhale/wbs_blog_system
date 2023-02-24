package top.woodwhale.blog.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.woodwhale.blog.dao.LabelDao;
import top.woodwhale.blog.pojo.Label;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.services.ILabelService;
import top.woodwhale.blog.utils.SnowflakeIdUtils;
import top.woodwhale.blog.utils.TextUtils;

import java.util.Date;
import java.util.List;

import static top.woodwhale.blog.utils.Constants.Label.LABEL_INIT_COUNT;

@Slf4j
@Service
@Transactional
public class LabelServiceImpl implements ILabelService {

    @Autowired
    private LabelDao labelDao;

    @Autowired
    private SnowflakeIdUtils idUtils;

    /**
     * 获取标签集合
     * @return ResponseResult
     */
    @Override
    public ResponseResult listLabels() {
        List<Label> labelList = labelDao.findAll();
        return ResponseResult.SUCCESS(TextUtils.successGet("标签列表")).setData(labelList);
    }

    /**
     * 更新label
     * @param labelId labelId
     * @param label label
     * @return ResponseResult
     */
    @Override
    public ResponseResult updateLabel(String labelId, Label label) {
        // 判断参数是否合法
        if (TextUtils.isEmpty(labelId)) {
            return ResponseResult.FAILED(TextUtils.notNullable("标签Id"));
        }
        if (TextUtils.isEmpty(label.getName())) {
            return ResponseResult.FAILED(TextUtils.notNullable("标签名称"));
        }
        // 如果MySQL中存在，就可以修改
        Label labelInMysql = labelDao.findOneById(labelId);
        if (labelInMysql != null) {
            label.setCount(labelInMysql.getCount());
            label.setId(labelInMysql.getId());
            label.setCreateTime(labelInMysql.getCreateTime());
            label.setUpdateTime(new Date());
            labelInMysql = label;
            labelDao.save(labelInMysql);
            return ResponseResult.SUCCESS(TextUtils.successUpdate("标签"));
        }
        return ResponseResult.FAILED(TextUtils.notExist("标签"));
    }

    /**
     * 删除标签
     * @param labelId 标签id
     * @return ResponseResult
     */
    @Override
    public ResponseResult deleteLabel(String labelId) {
        if (TextUtils.isEmpty(labelId)) {
            return ResponseResult.FAILED(TextUtils.notNullable("标签Id"));
        }
        if (labelId.contains("_") && labelId.split("_").length > 0) {
            log.info(labelId);
            boolean flag = true;
            for (String id : labelId.split("_")) {
                int res = labelDao.deleteOneById(id);
                if (res == 0) {
                    log.info("标签" + id + " 不存在");
                    flag = false;
                }
            }
            return flag ? ResponseResult.SUCCESS(TextUtils.successDelete("标签")) :
                    ResponseResult.FAILED(TextUtils.failDelete("部分标签"));
        } else {
            int res = labelDao.deleteOneById(labelId);
            return res > 0 ? ResponseResult.SUCCESS(TextUtils.successDelete("标签")) :
                    ResponseResult.FAILED(TextUtils.failDelete("标签"));
        }
    }

    /**
     * 通过id获取标签
     * @param id 标签id
     * @return ResponseResult
     */
    @Override
    public ResponseResult getLabelById(String id) {
        if (TextUtils.isEmpty(id)) {
            return ResponseResult.FAILED(TextUtils.notNullable("标签Id"));
        }
        Label labelInMysql = labelDao.findOneById(id);
        if (labelInMysql != null) {
            return ResponseResult.SUCCESS(TextUtils.successGet("标签")).setData(labelInMysql);
        }
        return ResponseResult.FAILED(TextUtils.notExist("标签"));
    }

    /**
     * 添加标签
     * @param label 标签bean类
     * @return ResponseResult
     */
    @Override
    public ResponseResult addLabel(Label label) {
        if (TextUtils.isEmpty(label.getName())) {
            return ResponseResult.FAILED(TextUtils.notNullable("标签名称"));
        }
        // 判断MySQL中是否存在
        Label labelInMysql = labelDao.findOneByName(label.getName());
        if (labelInMysql != null) {
            return ResponseResult.FAILED(TextUtils.hasExisted("标签名称"));
        }
        // 如果不存在就新建
        label.setId(String.valueOf(idUtils.nextId()));
        label.setCount(LABEL_INIT_COUNT);
        label.setCreateTime(new Date());
        label.setUpdateTime(new Date());
        labelDao.save(label);
        return ResponseResult.SUCCESS(TextUtils.successAdd("标签"));
    }


}
