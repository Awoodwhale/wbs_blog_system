package top.woodwhale.blog.services;

import top.woodwhale.blog.pojo.Looper;
import top.woodwhale.blog.response.ResponseResult;

public interface ILoopService {
    ResponseResult addLoop(Looper looper);

    ResponseResult getLoop(String looperId);

    ResponseResult listLoops();

    ResponseResult updateLoop(String looperId, Looper loop);

    ResponseResult deleteLoop(String looperId);
}
