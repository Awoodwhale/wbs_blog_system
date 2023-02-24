package top.woodwhale.blog.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import top.woodwhale.blog.response.ResponseResult;
import top.woodwhale.blog.response.ResponseState;

/**
 * 错误结果页面
 */
@RestController
public class ErrorPageController {

    /**
     * 无法找到有效页面
     * @return ResponseResult
     */
    @GetMapping("/404")
    public ResponseResult page404() {
        return ResponseResult.GET(ResponseState.ERROR_404);
    }

    /**
     * 权限不足
     * @return ResponseResult
     */
    @GetMapping("/403")
    public ResponseResult page403() {
        return ResponseResult.GET(ResponseState.ERROR_403);
    }

    /**
     * 系统繁忙
     * @return ResponseResult
     */
    @GetMapping("/504")
    public ResponseResult page504() {
        return ResponseResult.GET(ResponseState.ERROR_504);
    }

    /**
     * 参数不支持
     * @return ResponseResult
     */
    @GetMapping("/505")
    public ResponseResult page505() {
        return ResponseResult.GET(ResponseState.ERROR_505);
    }
}
