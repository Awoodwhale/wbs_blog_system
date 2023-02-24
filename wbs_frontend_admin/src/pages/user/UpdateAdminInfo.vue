<template>
    <div class="admin-info-box">
        <div class="admin-info-left">
            <el-row :gutter="20">
                <el-col :span="10">
                    <el-card class="admin-info-card">
                        <template #header>
                            <!-- 用户信息 -->
                            <div class="admin-info-card-content">
                                <div class="admin-avatar-container">
                                    <el-avatar :size="104" :src="userInfo.avatar"></el-avatar>
                                </div>
                                <div class="admin-name">{{ userInfo.username }}</div>
                                <div class="admin-sign">{{ userInfo.sign }}</div>
                            </div>
                        </template>

                        <!-- 用户label -->
                        <div class="admin-detail-info">
                            <div>
                                <el-space :size="8">
                                    <el-icon :size="15">
                                        <component :is="icons[0]" />
                                    </el-icon>
                                    <span>{{ userInfo.email }}</span>
                                </el-space>
                            </div>
                            <div>
                                <el-space :size="8">
                                    <el-icon :size="16">
                                        <component :is="icons[1]" />
                                    </el-icon>
                                    <span>博客已运行 {{ userBlogInfo.runTime }} 天</span>
                                </el-space>
                            </div>
                        </div>
                        <!-- 分割线 -->
                        <el-divider></el-divider>
                        <div class="admin-detail-info">
                            <div>
                                <el-space :size="8">
                                    <el-icon :size="15">
                                        <component :is="icons[2]" />
                                    </el-icon>
                                    <span v-if="userInfo.profession">{{
                                            userInfo.profession
                                    }}</span>
                                    <span v-else>暂无职业信息</span>
                                </el-space>
                            </div>
                            <div>
                                <el-space :size="8">
                                    <el-icon :size="15">
                                        <component :is="icons[3]" />
                                    </el-icon>
                                    <span v-if="userInfo.major">{{ userInfo.major }}</span>
                                    <span v-else>暂无专业信息</span>
                                </el-space>
                            </div>
                            <div>
                                <el-space :size="8">
                                    <el-icon :size="15">
                                        <component :is="icons[4]" />
                                    </el-icon>
                                    <span v-if="userInfo.location">{{ userInfo.location }}</span>
                                    <span v-else>暂无位置信息</span>
                                </el-space>
                            </div>
                        </div>
                        <!-- 分割线 -->
                        <el-divider></el-divider>
                        <!-- 信息 -->
                        <div class="admin-blog-info-container">
                            <div class="admin-blog-info-content">累计发表文章 {{userBlogInfo.article}} 篇</div>
                            <el-divider border-style="dotted" />
                            <div class="admin-blog-info-content">累计创建分类 {{userBlogInfo.category}} 个</div>
                            <el-divider border-style="dotted" />
                            <div class="admin-blog-info-content">累计获得评论 {{userBlogInfo.comment}} 条</div>
                            <el-divider border-style="dotted" />
                            <div class="admin-blog-info-content">累计添加友链 {{userBlogInfo.friendLink}} 个</div>
                            <el-divider border-style="dotted" />
                            <div class="admin-blog-info-content">累计文章阅读 {{userBlogInfo.viewCount}} 次</div>
                           
                        </div>
                    </el-card>
                </el-col>
                <!-- 管理员资料修改 -->
                <el-col :span="14" style="padding-left: 0px; padding-right: 0px">
                    <el-card class="admin-update-info-card">
                        <template #header>
                            <div class="admin-update-info-card-header">
                                <span>个人资料信息修改</span>
                            </div>
                        </template>
                        <el-tabs>
                            <!-- 修改个人信息 -->
                            <el-tab-pane label="修改信息">
                                <el-scrollbar height="500px">
                                    <div class="admin-update-base-info-tag-content">
                                        <el-form :model="userInfo" :rules="rules" label-position="right"
                                            label-width="80px">
                                            <el-form-item label="用户名称" prop="username">
                                                <el-input v-model="userInfo.username" autocomplete="off"
                                                    placeholder="请输入用户名" />
                                            </el-form-item>
                                            <el-form-item label="用户权限">
                                                <el-switch v-model="isAdmin" disabled active-text="Admin"
                                                    inactive-text="Normal" />
                                            </el-form-item>
                                            <el-form-item label="个性签名" prop="sign">
                                                <el-input style="width: 420px" v-model="userInfo.sign"
                                                    autocomplete="off" type="textarea" maxlength="128"
                                                    placeholder="请输入用户个性签名" />
                                            </el-form-item>
                                            <el-form-item label="头像方式">
                                                <el-switch active-text="图片地址" inactive-text="上传图片"
                                                    v-model="avatarWay" />
                                            </el-form-item>
                                            <el-form-item label="上传图片" prop="avatar" v-if="avatarWay === false">
                                                <el-tooltip content="仅支持小于2MB的jpg、png、gif文件" placement="right">
                                                    <el-upload class="avatar-uploader" @click="preAvatarUpload"
                                                        action="/admin/image" disabled>
                                                        <img v-if="adminNewAvatarUrl !== ''" :src="adminNewAvatarUrl"
                                                            class="avatar" />
                                                        <el-icon v-else class="avatar-uploader-icon">
                                                            <component :is="plusIcon" />
                                                        </el-icon>
                                                    </el-upload>
                                                </el-tooltip>
                                            </el-form-item>
                                            <el-form-item label="图片地址" prop="avatar" v-else>
                                                <el-input v-model="userInfo.avatar" autocomplete="off"
                                                    placeholder="请输入用户头像地址" width="100" />
                                            </el-form-item>
                                            <el-form-item label="个人职位" prop="profession">
                                                <el-input v-model="userInfo.profession" autocomplete="off"
                                                    placeholder="请输入当前职位" />
                                            </el-form-item>
                                            <el-form-item label="学习专业" prop="major">
                                                <el-input v-model="userInfo.major" autocomplete="off"
                                                    placeholder="请输入主修专业" />
                                            </el-form-item>
                                            <el-form-item label="当前地址" prop="location">
                                                <el-input v-model="userInfo.location" autocomplete="off"
                                                    placeholder="请输入当前地址" />
                                            </el-form-item>
                                        </el-form>
                                    </div>
                                </el-scrollbar>
                                <div>
                                    <el-button type="primary" @click="updateAdminInfo" :icon="saveFormIcon">保 存
                                    </el-button>
                                    <el-button type="danger" @click="resetAdminUserInfo" :icon="reloadFormIcon">重 置
                                    </el-button>
                                </div>
                            </el-tab-pane>
                            <!-- 修改密码 -->
                            <el-tab-pane label="修改密码">
                                <el-scrollbar height="500px">
                                    <div class="admin-update-base-info-tag-content">
                                        <el-form :model="passwordInfo" :rules="rules" label-position="top">
                                            <el-form-item label="当前密码" prop="oldPassword">
                                                <el-input v-model="passwordInfo.oldPassword" autocomplete="off"
                                                    type="password" show-password placeholder="请输入当前的密码" />
                                            </el-form-item>
                                            <el-form-item label="更新密码" prop="newPassword">
                                                <el-input v-model="passwordInfo.newPassword" autocomplete="off"
                                                    type="password" show-password placeholder="请输入更新的密码" />
                                            </el-form-item>
                                            <el-form-item label="确认密码" prop="surePassword">
                                                <el-input v-model="passwordInfo.surePassword" autocomplete="off"
                                                    type="password" show-password placeholder="请输入再次输入更新密码" />
                                            </el-form-item>
                                        </el-form>
                                    </div>
                                </el-scrollbar>
                                <div>
                                    <el-button type="primary" @click="updatePasswordInfo" :icon="saveFormIcon">保 存
                                    </el-button>
                                    <el-button type="danger" @click="resetPasswordInfo" :icon="reloadFormIcon">重 置
                                    </el-button>
                                </div>
                            </el-tab-pane>

                            <!-- 修改邮箱 -->
                            <el-tab-pane label="修改邮箱">
                                <el-scrollbar height="500px">
                                    <div class="admin-update-base-info-tag-content">
                                        <el-form :model="emailInfo" :rules="rules" label-position="top">
                                            <el-form-item label="当前密码" prop="password">
                                                <el-input v-model="emailInfo.password" autocomplete="off"
                                                    type="password" show-password placeholder="请输入当前的密码" />
                                            </el-form-item>
                                            <el-form-item label="新的邮箱" prop="newEmail">
                                                <el-input v-model="emailInfo.newEmail" autocomplete="off"
                                                    placeholder="请输入更新的邮箱" />
                                            </el-form-item>
                                            <el-form-item label="邮箱验证" prop="verifyCode">
                                                <el-input v-model="emailInfo.verifyCode" autocomplete="off"
                                                    placeholder="请输入新邮箱中的验证码" />
                                            </el-form-item>
                                            <el-button id="email-btn" :disabled="disClick" plain round type="primary"
                                                @click="getVerifyCode()">获取邮箱验证码</el-button>
                                        </el-form>
                                    </div>
                                </el-scrollbar>
                                <div>
                                    <el-button type="primary" @click="updateEmailInfo" :icon="saveFormIcon">保 存
                                    </el-button>
                                    <el-button type="danger" @click="resetEmailInfo" :icon="reloadFormIcon">重 置
                                    </el-button>
                                </div>


                            </el-tab-pane>
                        </el-tabs>
                    </el-card>
                </el-col>
            </el-row>
        </div>
        <div class="admin-dialog-box">
            <pic-upload ref="uploadRef" field="file" :imgName="adminNewAvatarName"
                @crop-upload-success="cropUploadSuccess" @crop-upload-fail="cropUploadFail" @src-file-set="srcFileSet"
                v-model="showCrop" url="/admin/image" :maxSize="2048" img-format="png">
            </pic-upload>
        </div>
    </div>
</template>

<script>
import {
    showUser,
    SUCCESS,
    showUserBlogInfo,
    editUser,
    editAdminPassword,
    logoutAdmin,
    showVerifyCode,
    editEmail
} from "../../api/api";
import { showMessage, showNotify } from "../../utils/utils";
import {
    DocumentChecked,
    RefreshRight,
    Plus,
    Message,
    Calendar,
    Position,
    Monitor,
    Location,
} from "@element-plus/icons-vue";
import { markRaw } from "vue";
import picUpload from "vue-image-crop-upload";
import "babel-polyfill";

export default {
    components: {
        "pic-upload": picUpload,
    },
    data() {
        return {
            saveFormIcon: markRaw(DocumentChecked),
            reloadFormIcon: markRaw(RefreshRight),
            plusIcon: markRaw(Plus),
            icons: [
                markRaw(Message),
                markRaw(Calendar),
                markRaw(Position),
                markRaw(Monitor),
                markRaw(Location),
            ],
            showCrop: false,
            disClick: false,
            passwordInfo: {
                oldPassword: "",
                newPassword: "",
                surePassword: "",
            },
            emailInfo: {
                password: "",
                newEmail: "",
                verifyCode: "",
            },
            userInfo: [],
            userBlogInfo: [],
            loadingFlag: false,
            userForm: [],
            isAdmin: true,
            rules: {
                password: [{ required: true, message: "请输入旧密码", tigger: "blur" }],
                verifyCode: [
                    { required: true, message: "请输入验证码", tigger: "blur" },
                ],
                newEmail: [
                    { required: true, message: "请输入新邮箱", tigger: "blur" },
                    {
                        validator: this.checkEmail,
                        trigger: "blur",
                    },
                ],
                surePassword: [
                    { required: true, message: "请确认新密码", tigger: "blur" },
                ],
                newPassword: [
                    { required: true, message: "请输入新密码", tigger: "blur" },
                ],
                oldPassword: [
                    { required: true, message: "请输入旧密码", tigger: "blur" },
                ],
                username: [{ required: true, message: "请输入用户名", tigger: "blur" }],
                sign: [{ required: false, tigger: "blur" }],
                avatar: [{ required: false, tigger: "blur" }],
            },
            avatarWay: false,
            adminNewAvatarUrl: "",
            adminNewAvatarName: "",
        };
    },
    methods: {
        // 选取图片之后的钩子
        srcFileSet(fileName, fileType, fileSize) {
            if (
                fileName.indexOf(".") === -1 ||
                fileName.charAt(fileName.length - 1) === "."
            ) {
                showMessage("上传图片名不合法!", "error");
                this.showCrop = false;
                return;
            }
            if (
                fileType !== "image/jpeg" &&
                fileType !== "image/png" &&
                fileType !== "image/gif"
            ) {
                showMessage("请使用正确的图片格式!", "error");
                this.showCrop = false;
                return;
            }
            if (fileSize / 1024 / 1024 > 2) {
                showMessage("请选择小于2MB的图片!", "error");
                this.showCrop = false;
                return;
            }
            this.adminNewAvatarName = fileName.split(".")[0];
        },
        // 点击上传的方法
        preAvatarUpload() {
            this.showCrop = true;
        },
        // 上传成功
        cropUploadSuccess(jsonData, field) {
            if (jsonData.code === SUCCESS) {
                this.adminNewAvatarUrl =
                    this.$backendUrl + "/portal/image/" + jsonData.data.id;
                this.userInfo.avatar = this.adminNewAvatarUrl;
                this.showCrop = false;
                showNotify("上传成功! 图片ID👇", jsonData.data.id);
                let adminAvatar =
                    document.getElementById("admin-avatar-img").children[0];
                adminAvatar.src = this.adminNewAvatarUrl;
            } else {
                showMessage(jsonData.message, "error");
            }
            this.adminNewAvatarName = "";
            this.$refs.uploadRef.off();
        },
        // 上传失败
        cropUploadFail(status, field) {
            showMessage("上传图片失败!", "error");
            this.adminNewAvatarName = "";
            this.$refs.uploadRef.off();
        },
        // 获取管理员信息表单
        getAdminUserInfo() {
            const loading = this.$loading({
                lock: true,
                text: "管理信息获取中...",
                target: document.querySelector(".el-main"),
            });
            showUser().then((res) => {
                if (res.code === SUCCESS) {
                    this.userInfo = res.data;
                    if (!this.loadingFlag) {
                        showUserBlogInfo().then((res) => {
                            loading.close();
                            if (res.code === SUCCESS) {
                                this.loadingFlag = true
                                this.userBlogInfo = res.data;
                            } else {
                                showMessage("获取博客信息失败", "error");
                            }

                        });
                    } else {
                        loading.close();
                    }
                } else {
                    showMessage("获取用户信息失败", "error");
                }
            });
        },
        // 重置管理员信息表单
        resetAdminUserInfo() {
            const loading = this.$loading({
                lock: true, //lock的修改符--默认是false
                text: "管理信息获取中...", //显示在加载图标下方的加载文案
                target: document.querySelector(".el-main"), //loadin覆盖的dom元素节点
            });
            showUser().then((res) => {
                if (res.code === SUCCESS) {
                    this.userInfo = res.data;
                } else {
                    showMessage("重置用户信息失败", "error");
                }
                this.adminNewAvatarUrl = "";
                loading.close();
            });
        },
        // 检查管理员表单内容
        checkAdminInfo() {
            if (this.userInfo.username === "") {
                showMessage("用户名不可为空!", "error");
                return false;
            }
            if (this.userInfo.avatar.trim() === "") {
                this.userInfo.avatar =
                    "https://cdn.jsdelivr.net/gh/Awoodsheep/pic/imgs/avatar.png";
            }
            return true;
        },
        // 提交管理员信息表单
        updateAdminInfo() {
            if (this.checkAdminInfo()) {
                const loading = this.$loading({
                    lock: true, //lock的修改符--默认是false
                    text: "新信息提交中...", //显示在加载图标下方的加载文案
                    target: document.querySelector(".el-main"), //loadin覆盖的dom元素节点
                });
                editUser(this.userInfo.id, this.userInfo).then((res) => {
                    if (res.code === SUCCESS) {
                        loading.close();
                        showMessage("更新管理员资料成功");
                        this.adminNewAvatarUrl = "";
                    } else {
                        showMessage(res.message, "error");
                    }
                });
            }
        },
        // 修改管理员密码
        updatePasswordInfo() {
            let oldPwd = this.passwordInfo.oldPassword;
            let newPwd = this.passwordInfo.newPassword;
            let surePwd = this.passwordInfo.surePassword;
            if (oldPwd === "" || newPwd === "" || surePwd === "") {
                showMessage("请将密码信息输入完整!", "error");
                return;
            }
            if (oldPwd === newPwd) {
                showMessage("新密码不可与旧密码相同!", "warning");
                this.resetPasswordInfo();
                return;
            }
            if (surePwd !== newPwd) {
                showMessage("两次的新密码不同!请重试!", "warning");
                this.resetPasswordInfo();
                return;
            }
            editAdminPassword(this.passwordInfo).then((res) => {
                if (res.code === SUCCESS) {
                    showNotify(this.userInfo.username + "修改密码", res.message);
                    this.resetAdminUserInfo();
                    logoutAdmin().then((res) => {
                        if (res.code === SUCCESS) {
                            location.reload();
                        }
                    });
                } else {
                    showNotify(this.userInfo.username + "修改密码", res.message, "error");
                    this.resetPasswordInfo();
                }
            });
        },
        // 重置密码表单
        resetPasswordInfo() {
            this.passwordInfo.oldPassword = "";
            this.passwordInfo.newPassword = "";
            this.passwordInfo.surePassword = "";
        },
        // 获取邮箱验证码
        getVerifyCode() {
            if (this.emailInfo.newEmail === "") {
                showMessage("请输入新邮箱地址!", "error");
                return;
            }
            // 验证邮箱格式
            let regEmail = /^\w+@[a-zA-Z0-9]{2,10}(?:\.[a-z]{2,4}){1,3}$/;
            if (!regEmail.test(this.emailInfo.newEmail)) {
                showMessage("邮箱地址格式错误!", "error");
                return;
            }
            // 设置倒计时
            var emailBtn = document.getElementById("email-btn");
            let num = 60;
            let timer = setInterval(() => {
                num--;
                emailBtn.innerHTML = num + "秒后重新获取";
                this.disClick = true;
                if (num === 0) {
                    this.disClick = false;
                    emailBtn.innerHTML = "获取邮箱验证码";
                    clearInterval(timer);
                }
            }, 1000);
            // 后端获取验证码
            showVerifyCode("update", this.emailInfo.newEmail).then((res) => {
                if (res.code === SUCCESS) {
                    showNotify("邮箱验证成功", res.message);
                } else {
                    showNotify("邮箱验证错误", res.message, "error");
                }
            });
        },
        // 检查邮箱格式
        checkEmail(rule, value, callback) {
            let regEmail = /^\w+@[a-zA-Z0-9]{2,10}(?:\.[a-z]{2,4}){1,3}$/;
            if (regEmail.test(value)) {
                return callback();
            }
            callback(new Error("邮箱格式有误"));
        },
        // 提交邮箱更改信息
        updateEmailInfo() {
            // 验证表单数据
            if (this.emailInfo.password === "") {
                showMessage("请输入当前账户密码!", "error");
                return;
            }
            if (this.emailInfo.newEmail === "") {
                showMessage("请输入新邮箱地址!", "error");
                return;
            }
            if (this.emailInfo.verifyCode === "") {
                showMessage("请输入邮箱验证码!", "error");
                return;
            }
            // 后端提交
            editEmail(this.emailInfo).then((res) => {
                if (res.code === SUCCESS) {
                    showNotify(this.userInfo.username + "修改邮箱", res.message);
                    this.resetEmailInfo();
                    logoutAdmin().then((res) => {
                        if (res.code === SUCCESS) {
                            location.reload();
                        }
                    });
                } else {
                    showNotify(this.userInfo.username + "修改邮箱", res.message, "error");
                    this.resetPasswordInfo();
                }
            });
        },
        // 重置邮箱表单
        resetEmailInfo() {
            this.emailInfo.password = "";
            this.emailInfo.newEmail = "";
            this.emailInfo.verifyCode = "";
        },
    },
    mounted() {
        // 进来就加载当前账号信息
        this.getAdminUserInfo();
    },
};
</script>

<style>
.admin-info-left {
    padding-right: 10px;
}

.admin-blog-info-container .el-divider--horizontal {
    margin: 12px 0;
    background: 0 0;
    border-top: 1px dotted #e8eaec;
}

.admin-info-card-content {
    margin-bottom: 10px;
}

.admin-info-card-content div {
    text-align: center;
}

.admin-name {
    font-size: 20px;
    font-weight: 666;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Roboto", "Oxygen",
        "Ubuntu", "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue",
        sans-serif;
    vertical-align: middle;
    margin-top: 5px;
}

.admin-sign {
    font-size: 14px;
}

.admin-detail-info div {
    margin-top: 1px;
    margin-bottom: 1px;
}

.admin-info-box .el-divider--horizontal {
    margin: 15px 0;
    background: 0 0;
    border-top: 1px double #e8eaec;
}

.admin-update-info-card-header {
    text-align: center;
    font-weight: 600;
}

.admin-info-box .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

.admin-info-box .avatar-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.admin-info-box .avatar-uploader .el-upload .avatar {
    width: 80px;
    height: 80px;
}

.admin-info-box .el-icon.avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 80px;
    height: 80px;
    text-align: center;
}

.admin-info-box .el-card__header {
    padding: 10px;
}

.admin-update-info-card .el-card__body {
    padding-top: 0px;
    height: 435px;
}

.admin-info-card .el-card__body {
    padding-top: 10px;
    padding-bottom: 10px;
    height: 435px;
}

.admin-info-box .el-col {
    height: 620px;
}

.admin-update-info-card {
    height: 645px;
}

.admin-blog-info-content {
    margin: auto;
}


</style>