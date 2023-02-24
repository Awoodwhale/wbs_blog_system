<template>
    <div class="admin-login-box">
        <!-- 后台登录入口，只有登录，没有注册、没有找回密码 -->
        <!-- 顶部内容 -->
        <div class="admin-login-header-box">
            <div class="admin-login-header-center">
                <div class="admin-login-logo">WBS博客系统 | 管理员初始化</div>
            </div>
        </div>
        <!-- 中间内容 -->
        <div class="admin-login-center-box">
            <div class="login-box">
                <div class="login-box-title">管理员初始化</div>
                <el-form :model="user" :rules="rules" label-width="83px" style="max-width: 390px">
                    <el-form-item label="用户名" prop="username">
                        <el-input v-model="user.username" class="admin-input-info" placeholder="请输入用户名" />
                    </el-form-item>
                    <el-form-item label="邮箱" prop="email">
                        <el-input v-model="user.email" class="admin-input-info" placeholder="请输入邮箱" />
                    </el-form-item>
                    <el-form-item label="密码" prop="password">
                        <el-input v-model="user.password" type="password" show-password class="admin-input-info"
                            placeholder="请输入密码" />
                    </el-form-item>
                    <el-form-item label="确认密码" prop="password">
                        <el-input v-model="user.password2" type="password2" show-password class="admin-input-info"
                            placeholder="请确认密码" @keypress.enter="doInit" />
                    </el-form-item>
                    <el-form-item class="login-button-container">
                        <el-button type="primary" @click="doInit" class="login-button">
                            <el-icon class="el-icon--left">
                                <Check />
                            </el-icon>初始化
                        </el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script setup>
import { Check } from '@element-plus/icons-vue'
import { adminInit, SUCCESS } from '../../api/api'
import { useRouter } from 'vue-router'
import { showMessage, showNotify } from '../../utils/utils'
import { reactive, toRaw } from 'vue'
import md5 from 'js-md5'

const router = useRouter()  // router要写在外面，不然无法读取

let user = reactive({
    username: '',
    password: '',
    password2: '',
    email: "",
})
let rules = {
    email: [{ required: true, message: "请输入邮箱", tigger: 'blur' }],
    username: [{ required: true, message: "请输入账号", tigger: 'blur' }],
    password: [{ required: true, message: "请输入密码", tigger: 'blur' }],
    password2: [{ required: true, message: "请再次确认密码", tigger: 'blur' }],
}
const doInit = () => {
    if (user.username === '') {
        showMessage("请输入用户名!", 'error')
        return
    }
    if (user.email === '') {
        showMessage("请输入邮箱!", 'error')
        return
    }
    if (user.password === '' || user.password2 === '') {
        showMessage("请输入密码!", 'error')
        return
    }
    if (user.password !== user.password2) {
        showMessage("两次密码不一致，请重试！", 'error')
        return
    }
    let userPostData = toRaw(user)
    delete userPostData.password2   // 删除重复的密码
    userPostData.password = md5(userPostData.password)
    adminInit(userPostData).then(res => {
        if (res.code === SUCCESS) {
            showNotify("管理员初始化", "管理员信息初始化成功，即将前往登录界面！",'success',200)
            router.push("/login")
        } else {
            showNotify("管理员初始化", "管理员信息初始化失败，请稍后重试！", 'error')
        }
    })
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