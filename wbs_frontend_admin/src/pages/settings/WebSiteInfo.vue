<template>
    <div class="website-info-box">
        <el-row :gutter="10">
            <el-col :span="10">
                <!-- 左侧card -->
                <el-card>
                    <template #header>
                        <div class="website-info-card-header">
                            <span>当前站点信息</span>
                        </div>
                    </template>
                    <el-scrollbar>
                        <div>
                            <strong style="font-size:15px">网站标题 : </strong>
                            <el-tag v-if="normalWebSiteInfo.title !== ''" effect="plain" id="website-title">{{
                                    normalWebSiteInfo.title
                            }}</el-tag>
                        </div>
                        <el-divider></el-divider>
                        <div>
                            <strong style="font-size:15px">站关键词 : </strong>
                            <template v-for="(item, index) in webSiteKeywords" :key="index">
                                <el-tag style="margin-right:5px;">{{ item }}</el-tag>
                            </template>
                        </div>
                        <el-divider></el-divider>
                        <div>
                            <strong style="font-size:15px">网站描述 : </strong>
                            <span id="description-span">{{ normalWebSiteInfo.description }}</span>
                        </div>
                        <el-divider></el-divider>
                        <div>
                            <strong style="font-size:15px">网站svg : </strong>
                            <el-image lazy
                                style="border-radius: 50%;width: 87px; height: 87px;margin-left: 10px;vertical-align:middle;"
                                :src="normalWebSiteInfo.svg"></el-image>
                        </div>
                    </el-scrollbar>
                </el-card>
            </el-col>
            <!-- 右侧card -->
            <el-col :span="14">
                <el-card class="website-info-card">
                    <el-tabs v-model="activeName" @tab-click="handleTabClick">
                        <el-tab-pane label="站点设置" name="first">
                            <el-scrollbar>
                                <div>
                                    <el-form :model="normalWebSiteInfo" :rules="rules" label-position="right"
                                        label-width="80px">
                                        <el-form-item label="网站标题" prop="title">
                                            <el-input v-model="normalWebSiteInfo.title" autocomplete="off"
                                                placeholder="请输入博客站点标题" />
                                        </el-form-item>
                                        <el-form-item label="站关键词" prop="keywords">
                                            <el-input v-model="normalWebSiteInfo.keywords" autocomplete="off"
                                                placeholder="请输入博客站点关键词,使用 ',' 隔开" />
                                        </el-form-item>
                                        <el-form-item label="网站描述" prop="description">
                                            <el-input style="width: 400px;max-width:400px" type="textarea"
                                                v-model="normalWebSiteInfo.description" autocomplete="off"
                                                placeholder="请输入博客站点描述" />
                                        </el-form-item>
                                        <el-form-item label="SVG设置" prop="avatar">
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
                                    </el-form>
                                </div>
                            </el-scrollbar>
                            <el-button type="primary" @click="updateWebSiteInfo" :icon="saveFormIcon">保 存</el-button>
                            <el-button type="danger" @click="getWebSiteInfo" :icon="reloadFormIcon">重 置</el-button>
                        </el-tab-pane>
                        <el-tab-pane label="博客设置" name="second">
                            <el-scrollbar>
                                <div>
                                    <el-form :model="blogSetting" label-position="right" label-width="85px">
                                        <el-form-item label="首页屏标题">
                                            <el-input v-model="blogSetting.h1" autocomplete="off"
                                                placeholder="请输入首页屏标题" />
                                        </el-form-item>
                                        <el-form-item label="首页屏签名">
                                            <el-input v-model="blogSetting.sign" autocomplete="off"
                                                placeholder="请输入首页屏签名" />
                                        </el-form-item>
                                        <el-form-item label="随机图URL">
                                            <el-input v-model="blogSetting.randomImgUrl" autocomplete="off"
                                                placeholder="请输入随机图地址,以http开头的形式" />
                                        </el-form-item>
                                        <el-form-item label="是否能评论">
                                            <el-switch v-model="blogSetting.sureComment" active-text="开启"
                                                inactive-text="禁止" />
                                        </el-form-item>

                                    </el-form>
                                </div>
                            </el-scrollbar>
                            <el-button type="primary" @click="updateBlogSetting" :icon="saveFormIcon">保 存
                            </el-button>
                            <el-button type="danger" @click="getBlogSetting" :icon="reloadFormIcon">重 置
                            </el-button>
                        </el-tab-pane>
                        <el-tab-pane label="媒体设置" name="third">
                            <el-scrollbar>
                                <div>
                                    <el-form :model="socialMedia" label-position="left" label-width="60px">
                                        <el-form-item label="Github">
                                            <el-input v-model="socialMedia.github" autocomplete="off"
                                                placeholder="请输入Github主页链接" />
                                        </el-form-item>
                                        <el-form-item label="B站">
                                            <el-input v-model="socialMedia.bilibili" autocomplete="off"
                                                placeholder="请输入B站主页链接" />
                                        </el-form-item>
                                        <el-form-item label="网易云">
                                            <el-input v-model="socialMedia.music163" autocomplete="off"
                                                placeholder="请输入网易云音乐主页链接" />
                                        </el-form-item>
                                        <el-form-item label="csdn">
                                            <el-input v-model="socialMedia.csdn" autocomplete="off"
                                                placeholder="请输入csdn主页链接" />
                                        </el-form-item>
                                        <el-form-item label="邮箱">
                                            <el-input v-model="socialMedia.mail" autocomplete="off"
                                                placeholder="请输入您的邮箱" />
                                        </el-form-item>
                                    </el-form>
                                </div>
                            </el-scrollbar>
                            <el-button type="primary" @click="updateSocialMedia" :icon="saveFormIcon">保 存
                            </el-button>
                            <el-button type="danger" @click="getSocialMedia" :icon="reloadFormIcon">重 置
                            </el-button>
                        </el-tab-pane>
                    </el-tabs>

                </el-card>
            </el-col>
        </el-row>
        <div class="website-dialog-box">
            <pic-upload ref="uploadRef" field="file" :imgName="adminNewAvatarName"
                @crop-upload-success="cropUploadSuccess" @crop-upload-fail="cropUploadFail" @src-file-set="srcFileSet"
                v-model="showCrop" url="/admin/image" :maxSize="2048" img-format="png">
            </pic-upload>
        </div>
    </div>

</template>

<script>
import { editSocialMedia, showSocialMedia, editBlogH1, editBlogRandomUrl, editBlogSureComment, showBlogH1, showBlogRandomUrl, showBlogSureComment, showWebSiteSeo, showWebSiteTitle, SUCCESS, editWebSiteSeo, editWebSiteTitle, showWebSiteSvg, editWebSiteSvg } from '../../api/api'
import { showMessage, showNotify } from '../../utils/utils'
import { DocumentChecked, RefreshRight, Plus } from '@element-plus/icons-vue'
import { markRaw } from 'vue'
import picUpload from "vue-image-crop-upload"

export default {
    components: {
        "pic-upload": picUpload,
    },
    data() {
        return {
            plusIcon: markRaw(Plus),
            saveFormIcon: markRaw(DocumentChecked),
            reloadFormIcon: markRaw(RefreshRight),
            normalWebSiteInfo: {
                keywords: '',
                description: '',
                title: '',
                svg: '',
            },
            webSiteKeywords: [],
            rules: {
                title: [{ required: true, message: "请输入博客站点标题", tigger: 'blur' }],
            },
            showCrop: false,
            adminNewAvatarUrl: "",
            adminNewAvatarName: "",
            activeName: "first",
            isLoadBlogSetting: false,
            blogSetting: {      // 博客设置
                h1: '',
                sign: '',
                randomImgUrl: '',
                sureComment: true,
            },
            fristSureComment: true,
            socialMedia: {  // 社交媒体
                github: '',
                bilibili: '',
                music163: '',
                csdn: '',
                mail: '',
            },
            isLoadSocialMedia: false,
        }
    },
    methods: {
        // tab点击事件
        handleTabClick(tab, event) {
            let name = tab.props.name
            if (name === "second" && !this.isLoadBlogSetting) {
                this.isLoadBlogSetting = true
                this.getBlogSetting()
            } else if (name === 'third' && !this.isLoadSocialMedia) {
                this.isLoadSocialMedia = true
                this.getSocialMedia()
            }
        },
        // 获取社交媒体信息
        getSocialMedia() {
            const loading = this.$loading({
                lock: true,
                text: '社交媒体获取中...',
                target: document.querySelector('.el-main')
            })
            showSocialMedia().then(res => {
                if (res.code === SUCCESS) {
                    this.socialMedia = res.data
                }
                loading.close()
            })
        },
        // 更新社交媒体信息
        updateSocialMedia() {
            editSocialMedia(this.socialMedia).then(res => {
                if (res.code === SUCCESS) {
                    showMessage(res.message)
                } else {
                    showMessage(res.message,'error')
                }
            })
        },
        // 获取博客信息
        getBlogSetting() {
            const loading = this.$loading({
                lock: true,
                text: '博客信息获取中...',
                target: document.querySelector('.el-main')
            })
            showBlogH1().then(res => {
                if (res.code === SUCCESS) {
                    this.blogSetting.h1 = res.data['web_site_h1']
                    this.blogSetting.sign = res.data['web_site_sign']
                }
                showBlogRandomUrl().then(res => {
                    if (res.code === SUCCESS) {
                        this.blogSetting.randomImgUrl = res.data['web_site_random_img']
                    }
                    showBlogSureComment().then(res => {
                        if (res.code === SUCCESS) {
                            this.blogSetting.sureComment = res.data === 'true' ? true : false
                            this.fristSureComment = this.blogSetting.sureComment
                        }
                        loading.close()
                    })
                })
            })
        },
        // 更新博客信息
        updateBlogSetting() {
            const loading = this.$loading({
                lock: true,
                text: '博客信息更新中...',
                target: document.querySelector('.el-main')
            });
            editBlogH1(this.blogSetting.h1, this.blogSetting.sign).then(res => {
                if (res.code === SUCCESS) {
                    showMessage(res.message)
                } else {
                    showMessage(res.message, 'error')
                }
                if (this.blogSetting.randomImgUrl !== ''
                    && this.blogSetting.randomImgUrl.indexOf("http") !== -1
                    && this.blogSetting.randomImgUrl.indexOf("://") !== -1) {
                    editBlogRandomUrl(this.blogSetting.randomImgUrl).then(res => {
                        if (res.code === SUCCESS) {
                            showMessage(res.message)
                        } else {
                            showMessage(res.message, 'error')
                        }
                        if (this.fristSureComment !== this.blogSetting.sureComment) {
                            editBlogSureComment().then(res => {
                                if (res.code === SUCCESS) {
                                    showMessage("博客评论状态更新成功")
                                } else {
                                    showMessage("博客评论状态更新失败", 'error')
                                }
                                loading.close()
                                this.getBlogSetting()
                            })
                        } else {
                            loading.close()
                            this.getBlogSetting()
                        }
                    })
                } else {
                    if (this.blogSetting.randomImgUrl === '') {
                        // 没有修改
                        if (this.fristSureComment !== this.blogSetting.sureComment) {
                            editBlogSureComment().then(res => {
                                if (res.code === SUCCESS) {
                                    showMessage("博客评论状态更新成功")
                                } else {
                                    showMessage("博客评论状态更新失败", 'error')
                                }
                                loading.close()
                                this.getBlogSetting()
                            })
                        } else {
                            loading.close()
                            this.getBlogSetting()
                        }
                    } else {
                        showMessage("博客随机图格式错误", 'error')
                    }
                }
            })

        },
        // 获取站点信息
        getWebSiteInfo() {
            this.adminNewAvatarUrl = ''
            const loading = this.$loading({
                lock: true,
                text: '站点信息获取中...',
                target: document.querySelector('.el-main')
            });
            showWebSiteTitle().then(res => {
                if (res.code === SUCCESS) {
                    this.normalWebSiteInfo.title = res.data['web_site_title']
                } else {
                    showMessage("站点标题信息获取失败", 'error');
                }
                showWebSiteSeo().then(res => {
                    if (res.code === SUCCESS) {
                        this.normalWebSiteInfo.keywords = res.data['web_site_keywords']
                        this.normalWebSiteInfo.description = res.data['web_site_description']
                        this.webSiteKeywords = this.normalWebSiteInfo.keywords.split(",")
                    } else {
                        showMessage("站点SEO信息获取失败", 'error');
                    }
                    showWebSiteSvg().then(res => {
                        if (res.code === SUCCESS) {
                            this.normalWebSiteInfo.svg = res.data["web_site_svg"]
                        } else {
                            showMessage("站点SVG信息获取失败", "error")
                        }
                        loading.close()
                    })
                })
            })

        },
        // 更新站点信息
        updateWebSiteInfo() {
            if (this.normalWebSiteInfo.title === '') {
                showMessage("网站标题不可为空!", 'error')
                return
            }
            editWebSiteSeo(this.normalWebSiteInfo).then(res => {
                if (res.code === SUCCESS) {
                    showMessage(res.message)
                } else {
                    showMessage(res.message, "error")
                }
                editWebSiteTitle(this.normalWebSiteInfo.title).then(res => {
                    if (res.code === SUCCESS) {
                        showMessage(res.message)
                    } else {
                        showMessage(res.message, 'error')
                    }
                    if (this.adminNewAvatarUrl !== '') {
                        editWebSiteSvg(
                            {
                                svg: this.adminNewAvatarUrl
                            }
                        ).then(res => {
                            if (res.code === SUCCESS) {
                                showMessage(res.message)
                            } else {
                                showMessage(res.message, "error")
                            }
                            this.getWebSiteInfo()
                        }).catch(e => {
                            showMessage("发生错误，请重新选择！", 'error')
                        })
                    } else {
                        this.getWebSiteInfo()
                    }
                })
            })
        },
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
                this.normalWebSiteInfo.svg = this.adminNewAvatarUrl;
                this.showCrop = false;
                showNotify("上传成功! 图片ID👇", jsonData.data.id);
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
    },
    mounted() {
        this.getWebSiteInfo()
    }
}
</script>

<style>
.website-info-card .el-scrollbar {
    height: 280px;
}

.website-info-card-header {
    text-align: center;
    font-weight: 600;
}

.website-info-box .el-col {
    height: 264px;
}

.website-info-box .el-row .el-card__body {
    height: 100%;
}

#website-title span {
    font-size: 15px;
}

#description-span {
    font-family: -apple-system, BlinkMacSystemFont, Segoe UI, PingFang SC, Hiragino Sans GB, Microsoft YaHei, Helvetica Neue, Helvetica, Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji, Segoe UI Symbol;
    color: rgba(0, 0, 0, .65);
    font-size: 18px;
    line-height: 22px;
}
</style>

<style scoped>
:deep(.avatar-uploader .el-upload) {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

:deep(.el-icon.avatar-uploader-icon) {
    font-size: 28px;
    color: #8c939d;
    width: 80px;
    height: 80px;
    text-align: center;
}

:deep(.avatar-uploader .el-upload .avatar) {
    width: 80px;
    height: 80px;
}
</style>