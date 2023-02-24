<template>
    <div class="admin-login-box">
        <!-- 后台登录入口，只有登录，没有注册、没有找回密码 -->
        <!-- 顶部内容 -->
        <div class="admin-login-header-box">
            <div class="admin-login-header-center">
                <div class="admin-login-logo">WBS 门户登录</div>
            </div>
        </div>
        <!-- 中间内容 -->
        <div class="admin-login-center-box">
            <div class="login-box">
                <div class="login-box-title">用户登录</div>
                <el-form ref="userRef" :model="user" :rules="rules" label-width="80px">
                    <el-form-item label="账号" prop="username">
                        <el-input v-model="user.username" class="admin-input-info" placeholder="请输入用户名或邮箱"/>
                    </el-form-item>
                    <el-form-item label="密码" prop="password">
                        <el-input v-model="user.password" type="password" show-password class="admin-input-info"
                                  placeholder="请输入密码" @keyup.enter.native="doLogin"/>
                    </el-form-item>

                    <el-row :gutter="14">
                        <el-col :span="17" class="admin-captcha-col">
                            <el-form-item label="验证码" prop="captchaCode" class="admin-captcha">
                                <el-input v-model="user.captchaCode" class="admin-input-info admin-captcha"
                                          placeholder="请输入验证码" @keyup.enter.native="doLogin"/>
                            </el-form-item>
                        </el-col>
                        <el-col :span="7" class="captcha-col">
                            <el-image :src="captchaPath" @click="updateCaptcha" class="captcha-code hvr-grow"
                                      fit="contain"/>
                        </el-col>
                    </el-row>

                    <el-form-item class="login-button-container">
                        <el-button plain type="primary" size="medium" @click="doLogin" style="margin-left: 0;"
                                   icon="el-icon-check">登录
                        </el-button>
                        <el-button plain type="warning" size="medium" @click="$refs.userRef.resetFields()"
                                   style="margin-left:auto;" icon="el-icon-refresh-right">清空
                        </el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script>
import {checkUserLogin, SUCCESS, userLogin} from '@/assets/api/api'
import {getSeoInfos, showMessage, showNotify} from '@/assets/utils/utils'
import md5 from 'js-md5';

export default {
    layout: 'normal',
    data() {
        return {
            user: {
                email: '',
                username: '',
                password: '',
                captchaCode: '',
            },
            postData: {
                email: '',
                username: '',
                password: '',
                captchaCode: '',
            },
            captchaPath: '',
            captchaKey: '',
            rules: {
                username: [{required: true, message: "请输入账号", tigger: 'blur'}],
                password: [{required: true, message: "请输入密码", tigger: 'blur'}],
                captchaCode: [{required: true, message: "请输入验证码", tigger: 'blur'}]
            }
        }
    },
    methods: {
        // 响应式布局

        // 登录
        doLogin() {
            if (this.user.username === '') {
                showMessage("请输入用户名或邮箱!", 'error')
                return
            }
            this.user.email = this.user.username
            if (this.user.password === '') {
                showMessage("请输入密码!", 'error')
                return
            }
            if (this.user.captchaCode === '') {
                showMessage("请输入图灵验证码!", 'error')
                return
            }
            Object.assign(this.postData, this.user)
            this.postData.password = md5(this.user.password)
            // 发起登录，检查数据
            userLogin(this.user.captchaCode, this.captchaKey, this.postData).then(data => {
                if (data.code === 20002) {
                    showNotify("WBS登录", data.message)
                    checkUserLogin().then(res => {
                        if (res.code === SUCCESS) {
                            this.$store.commit("setUserInfo", res.data)
                        } else {
                            showNotify("WBS用户信息", "用户信息异常，请登录重试！", "error")
                        }
                        // 跳转到主页
                        this.$router.push("/")
                    })
                } else {
                    showNotify("WBS登录", data.message, 'error')
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
    // 获取必要数据
    async asyncData(ctx) {
        await getSeoInfos(ctx)
    }
}
</script>

<style>
.admin-login-box {
    height: 100%;
}

.admin-login-box .admin-login-header-box {
    width: 100%;
    height: 60px;
    background: rgba(255, 255, 255, 0.8);
    margin-bottom: 15px;
    vertical-align: middle;
    position: fixed;
    top: 0;
}

.admin-login-box .admin-login-center-box {
    height: 100%;
    padding-top: 200px;
}

.admin-login-box .admin-login-header-center {
    line-height: 46px;
}

.admin-login-box .admin-login-logo {
    text-align: center;
    color: var(--font-color);
    font-size: 24px;
    font-weight: 600;
    height: 60px;
    line-height: 60px;
}

.admin-login-box .login-box {
    margin: auto;
    width: 450px;
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
    vertical-align: middle;
    color: #343436;
}

.admin-login-box .login-box .el-form-item label {
    color: #000;
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
    display: flex !important;
}


.admin-login-box .el-image__placeholder {
    background: url("/icon_loading.png") center center no-repeat;
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
        padding-left: 10px;
        padding-right: 10px;
    }

    .admin-login-box .login-box {
        width: 100%;
        box-shadow: 0 1px 10px #afafaf;
        border-radius: 15px;
        padding: 10px;
        padding-left: 33px;
        padding-right: 33px;
        background: rgba(255, 255, 255, 0.4);
    }

    .admin-captcha {
        width: 100px;
    }

    .admin-captcha-col {
        width: 61.5% !important;
    }

}

</style>
