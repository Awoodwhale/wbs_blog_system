package top.woodwhale.blog.response;

/**
 * 使用枚举，快速返回错误或者成功类型
 */
public enum ResponseState {
    SUCCESS(20000,"success", true),
    FAILED(40000,"error", false),
    REGISTER_SUCCESS(20001,"注册成功",true),
    LOGIN_SUCCESS(20002,"登录成功",true),
    NO_PERMISSION(40001,"权限不足",false),
    NO_PERMISSION_LOGIN(40001,"无登录权限",false),
    NOT_LOGIN(40002,"账号未登录",false),
    ERROR_403(40003,"权限不足",false),
    ERROR_404(40004,"页面丢失",false),
    ERROR_504(40005,"系统繁忙",false),
    ERROR_505(40006,"请求错误",false);

    /**
     * ResponseState的构造器
     * @param code 状态
     * @param message 信息
     * @param success 是否成功
     */
    ResponseState(int code, String message, boolean success) {
        this.code = code;
        this.message = message;
        this.success = success;
    }

    private int code;
    private String message;
    private boolean success;

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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
