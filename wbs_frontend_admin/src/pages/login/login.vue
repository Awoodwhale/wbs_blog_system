<template>
    <div class="admin-login-box">
        <!-- 后台登录入口，只有登录，没有注册、没有找回密码 -->
        <!-- 顶部内容 -->
        <div class="admin-login-header-box">
            <div class="admin-login-header-center">
                <div class="admin-login-logo">WBS博客系统 | 管理员登录</div>
            </div>
        </div>
        <!-- 中间内容 -->
        <div class="admin-login-center-box">
            <div class="login-box">
                <div class="login-box-title">用户登录</div>
                <el-form :model="user" :rules="rules" label-width="83px" style="max-width: 390px">
                    <el-form-item label="账号" prop="username">
                        <el-input v-model="user.username" class="admin-input-info" placeholder="请输入用户名或邮箱" />
                    </el-form-item>
                    <el-form-item label="密码" prop="pwd">
                        <el-input v-model="user.pwd" type="password" show-password class="admin-input-info"
                            placeholder="请输入密码" />
                    </el-form-item>

                    <el-row :gutter="6">
                        <el-col :span="17">
                            <el-form-item label="验证码" prop="captchaCode">
                                <el-input v-model="user.captchaCode" class="admin-input-info" placeholder="请输入验证码"
                                    @keypress.enter="doLogin" />
                            </el-form-item>
                        </el-col>
                        <el-col :span="7">
                            <el-image :src="captchaPath" @click="updateCaptcha" class="captcha-code" fit="scale-down" />
                        </el-col>
                    </el-row>
                    <el-form-item class="login-button-container">
                        <el-button type="primary" @click="doLogin" class="login-button">
                            <el-icon class="el-icon--left">
                                <Check />
                            </el-icon>登录
                        </el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script>
import { Check } from '@element-plus/icons-vue'
import { userLogin,checkToken } from '../../api/api'
import { showMessage, showNotify } from '../../utils/utils'
import md5 from 'js-md5';

export default {
    components: {
        Check,
    },
    data() {
        return {
            user: {
                pwd: '',
                email: '',
                username: '',
                password: '',
                captchaCode: '',
            },
            captchaPath: '',
            captchaKey: '',
            rules: {
                username: [{ required: true, message: "请输入账号", tigger: 'blur' }],
                pwd: [{ required: true, message: "请输入密码", tigger: 'blur' }],
                captchaCode: [{ required: true, message: "请输入验证码", tigger: 'blur' }]
            }
        }
    },
    methods: {
        doLogin() {
            if (this.user.username === '') {
                showMessage("请输入用户名或邮箱!", 'error')
                return
            } else {
                this.user.email = this.user.username
            }
            if (this.user.pwd === '') {
                showMessage("请输入密码!", 'error')
                return
            } else {
                this.user.password = md5(this.user.pwd)  // 前端md5加密
            }
            if (this.user.captchaCode === '') {
                showMessage("请输入图灵验证码!", 'error')
                return
            }
            // 发起登录，检查数据
            userLogin(this.user.captchaCode, this.captchaKey, this.user).then(res => {
                if (res.code === 20002) {
                    showNotify("WBS登录", res.message, "success", 1000)
                    this.$router.push("/index")
                } else {
                    showNotify("WBS登录", res.message, "error", 1000)
                    this.updateCaptcha()
                    this.user.captchaCode = ''
                }
            })
        },
        updateCaptcha() {
            this.captchaPath = '/user/captcha?captcha_code_key=' + this.captchaKey + "&random=" + Math.random();
        }
    },
    created() {
        this.captchaKey = Date.parse(new Date());
        this.updateCaptcha();
    },
}
</script>

<style>
.admin-login-box .admin-login-header-box {
    width: 100%;
    height: 60px;
    background: dodgerblue;
    margin-bottom: 15px;
    vertical-align: middle;
    position: fixed;
    top: 0;
}

.admin-login-box .admin-login-center-box {
    height: 100%;
    width: 100%;

}

.admin-login-box .admin-login-header-center {
    line-height: 46px;
}

.admin-login-box .admin-login-logo {
    text-align: center;
    color: #fff;
    font-size: 24px;
    font-weight: 600;
    height: 60px;
    line-height: 60px;
}

.admin-login-box .login-box {
    margin: auto;
    margin-top: 20vh;
    width: 400px;
    box-shadow: 0 1px 10px #afafaf;
    border-radius: 15px;
    padding: 10px;
    padding-left: 33px;
    padding-right: 33px;
    background: rgba(255, 255, 255, 0.4);
}

.admin-login-box .login-box-title {
    text-align: center;
    font-size: 20px;
    padding: 20px;
    padding-left: 10px;
    padding-right: 10px;
    font-weight: 555;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Roboto",
        "Oxygen", "Ubuntu", "Cantarell", "Fira Sans", "Droid Sans",
        "Helvetica Neue", sans-serif;
    vertical-align: middle;
    color: #343436;
}

.admin-login-box .login-box .el-form-item label {
    color: #000;
    padding-top: 4px;
    padding-right: 2px;
    height: 42px;
    text-align: center;
    background: #fbfbfb;
    border-left: solid 1px #e6e6e6;
    border-top: solid 1px #e6e6e6;
    border-bottom: solid 1px #e6e6e6;
    border-top-left-radius: 4px;
    border-bottom-left-radius: 4px;
}

.admin-login-box .admin-input-info .el-input__inner {
    height: 42px;
    border-radius: 0px;
    border-top-right-radius: 4px;
    border-bottom-right-radius: 4px;
}

.admin-login-box .captcha-code {
    cursor: pointer;
    width: 100px;
    height: 40px;
    border: solid 1px #dcdfe6;
    border-radius: 4px;
}

.admin-login-box .login-button-container .el-form-item__content {
    margin: 0 !important;
}

.admin-login-box .login-button {
    margin: 0 auto;
}

.admin-login-box .el-image__placeholder {
    background: url("../../assets/icon_loading.png") center center no-repeat;
    background-size: auto 50%;
    border: solid 1px #dcdfe6;
    width: 100px;
    height: 40px;
}

.admin-login-box .el-col {
    height: 60px !important;
}

/* 响应式 */
@media screen and (max-width: 860px) {
    .admin-login-box .admin-login-center-box {
        padding-left: 0;
        padding-right: 0;
    }

    .admin-login-box .login-box {
        box-shadow: 0 1px 10px #afafaf;
        border-radius: 15px;
        padding: 10px;
        padding-left: 33px;
        padding-right: 33px;
        background: rgba(255, 255, 255, 0.4);
    }

    .captcha-code {
        width: 100px;
    }

    .admin-captcha-col {
        width: 61.5% !important;
    }

}
</style>