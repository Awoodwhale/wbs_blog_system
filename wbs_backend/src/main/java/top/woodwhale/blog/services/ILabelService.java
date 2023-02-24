package top.woodwhale.blog.services;

import top.woodwhale.blog.pojo.Label;
import top.woodwhale.blog.response.ResponseResult;

public interface ILabelService {

    ResponseResult listLabels();

    ResponseResult addLabel(Label label);

    ResponseResult updateLabel(String labelId, Label label);

    ResponseResult deleteLabel(String labelId);

    ResponseResult getLabelById(String id);
}
