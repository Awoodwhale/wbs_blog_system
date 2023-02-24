<template>
    <div class="mine-box">
        <div class="mine-header">
            <IndexBgHeader></IndexBgHeader>
        </div>

        <div class="index-content" id="index-content">
            <IndexContent ref="icRef">
                <template v-slot:default>
                    <div class="articles-container">
                        <el-form class="user-data-form" ref="form" :model="form" label-width="80px">
                            <el-form-item label="用户名称">
                                <el-input v-model="form.username" autocomplete="off"
                                          placeholder="请输入用户名（必填）"></el-input>
                            </el-form-item>
                            <el-form-item label="头像链接">
                                <el-input v-model="form.avatar" autocomplete="off"
                                          placeholder="请输入用户头像链接（必填）"></el-input>
                            </el-form-item>
                            <el-form-item label="个性签名">
                                <el-input v-model="form.sign"
                                          autocomplete="off" type="textarea" maxlength="128"
                                          placeholder="请输入用户个性签名（可选）"/>
                            </el-form-item>
                            <el-form-item label="个人职位" prop="profession">
                                <el-input v-model="form.profession" autocomplete="off"
                                          placeholder="请输入当前职位（可选）"/>
                            </el-form-item>
                            <el-form-item label="学习专业" prop="major">
                                <el-input v-model="form.major" autocomplete="off"
                                          placeholder="请输入主修专业（可选）"/>
                            </el-form-item>
                            <el-form-item label="当前地址" prop="location">
                                <el-input v-model="form.location" autocomplete="off"
                                          placeholder="请输入当前地址（可选）"/>
                            </el-form-item>
                            <el-form-item style="display: flex" id="user-submit-buttons-group">
                                <div style="width: 100%; margin-left: auto" >
                                    <el-button type="danger" icon="el-icon-switch-button" @click="onLogout" plain>退出登录</el-button>
                                    <el-button type="primary" icon="el-icon-edit" @click="onSubmit" plain>更新信息</el-button>
                                    <el-button type="warning" icon="el-icon-delete" @click="onDelete" plain>清空表单</el-button>
                                    <el-button type="success" icon="el-icon-refresh-right" @click="onRecover" plain>恢复当前</el-button>
                                </div>
                            </el-form-item>
                        </el-form>
                    </div>
                </template>
            </IndexContent>
        </div>
    </div>

</template>


<script>
import {getPageNeededData, isEmpty, showMessage, showNotify} from "@/assets/utils/utils";
import {logout, showUserInfo, SUCCESS, updateUserInfo} from "@/assets/api/api";

export default {
    data() {
        return {
            form: {
                id:'',
                username: '',
                sign: '',
                avatar: '',
                profession: '',
                major: '',
                location: ''
            }
        }
    },
    methods: {
        // 提交表单
        onSubmit() {
            if (isEmpty(this.form.username)) {
                showMessage("用户名不可为空!", "warning", 1500)
                return
            }
            if (isEmpty(this.form.avatar)) {
                showMessage("用户头像不可为空", "warning", 1500)
                return
            }
            updateUserInfo(this.form.id, this.form).then(res => {
                if (res.code === SUCCESS) {
                    showUserInfo(this.form.id).then(ress => {
                        if (ress.code === SUCCESS) {
                            this.$store.commit("setUserInfo", ress.data)
                        }
                        showNotify("个人信息","更新个人信息成功!")
                        location.reload();
                    })
                } else {
                    showNotify("个人信息",res.message,"error")
                }
            })
        },
        // 清空表单
        onDelete() {
            showMessage("清空表单成功!",'success',1000)
            for (let key in this.form) {
                this.form[key] = ''
            }
        },
        // 退出登录
        onLogout() {
            logout().then(res => {
                if (res.code === SUCCESS) {
                    showNotify("个人中心","退出登录成功，即将刷新页面!")
                    location.reload()
                } else {
                    showNotify("个人中心", res.message)
                }
            })
        },
        // 恢复当前的表单信息
        onRecover() {
            this.form.id = this.$store.state.userInfo.id
            this.form.username = this.$store.state.userInfo.username
            this.form.avatar = this.$store.state.userInfo.avatar
            this.form.sign = this.$store.state.userInfo.sign
            this.form.profession = this.$store.state.userInfo.profession
            this.form.major = this.$store.state.userInfo.major
            this.form.location = this.$store.state.userInfo.location
        }
    },
    // 异步执行，请求数据
    async asyncData(ctx) {
        await getPageNeededData(ctx)
    },
    mounted() {
        // 设置标题内容
        document.getElementById("pages-title").innerHTML = "用户设置"
        this.onRecover()
    }
}
</script>


<style scoped>
/deep/ #user-submit-buttons-group .el-form-item__content {
    margin-left: auto !important;
}
/deep/.user-data-form .el-input__inner , /deep/.user-data-form .el-textarea__inner {
    font-family: var(--base-font-family) !important;
}
</style>
