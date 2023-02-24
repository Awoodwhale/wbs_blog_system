package top.woodwhale.blog.response;

/**
 * api请求返回结果
 */
public class ResponseResult {
    private boolean success;
    private int code;
    private String message;
    private Object data;

    /**
     * 返回自定义状态
     * @param state state
     * @return ResponseResult
     */
    public static ResponseResult GET(ResponseState state) {
        return new ResponseResult(state);
    }

    /**
     * 正常的SUCCESS返回
     * @return ResponseResult
     */
    public static ResponseResult SUCCESS() {
        return new ResponseResult(ResponseState.SUCCESS);
    }

    /**
     * 带message参数的SUCCESS返回
     * @param message 返回信息
     * @return ResponseResult
     */
    public static ResponseResult SUCCESS(String message) {
        ResponseResult responseResult = new ResponseResult(ResponseState.SUCCESS);
        responseResult.setMessage(message);
        return responseResult;
    }

    /**
     * 正常的FAILED返回
     * @return ResponseResult
     */
    public static ResponseResult FAILED() {
        return new ResponseResult(ResponseState.FAILED);
    }

    /**
     * 带message参数的FAILED返回
     * @param message 返回信息
     * @return ResponseResult
     */
    public static ResponseResult FAILED(String message) {
        ResponseResult responseResult = new ResponseResult(ResponseState.FAILED);
        responseResult.setMessage(message);
        return responseResult;
    }

    public ResponseResult(ResponseState responseState) {
        this.success = responseState.isSuccess();
        this.code = responseState.getCode();
        this.message = responseState.getMessage();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public ResponseResult setData(Object data) {
        this.data = data;
        return this;
    }
}
