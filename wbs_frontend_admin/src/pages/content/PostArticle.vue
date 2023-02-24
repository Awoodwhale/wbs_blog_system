<template>
    <!-- 主内容、写文章 -->
    <div class="article-post-box">
        <el-card>
            <template #header>
                <div style="font-weight: 600; font-size: 22px">
                    <span v-if="article.title">{{ article.title }}</span>
                    <span v-else>新文章</span>
                </div>
            </template>
            <!-- 标题 -->
            <div class="article-title-part">
                <el-input placeholder="请输入标题" v-model="article.title" maxlength="100" clearable
                    style="height: 40px; margin-bottom: 15px"></el-input>
            </div>
            <!-- 文章编辑 -->
            <div class="article-post-part">
                <v-md-editor v-model="article.content" height="555px" :left-toolbar="toolbarStr" :disabled-menus="[]"
                    @upload-image="editorUploadImg" @save="doSave"></v-md-editor>
            </div>
            <!-- 分类、封面、标签 -->
            <div class="article-post-settings-part"></div>
            <!-- 发布、草稿、预览 -->
            <div class="article-post-action-bar">
                <div class="action-btn-container">
                    <el-button type="danger" @click="doSave()" plain>{{ sketchArticleText }}</el-button>
                    <el-button type="primary" @click="handlePost()" plain>{{ postArticleText }}</el-button>
                </div>
            </div>
        </el-card>

        <!-- 发布文章的抽屉信息 -->
        <div class="article-post-drawer-box">
            <el-drawer v-model="drawerShow" title="发表文章">
                <el-scrollbar>
                    <div style="padding:0px 24px;">
                        <div style="margin-bottom:20px">基本设置</div>
                        <el-form :model="article" label-position="top">

                            <el-form-item label="发布日期">
                                <el-date-picker value-format="YYYY-MM-DD HH:mm:ss" v-model="article.createTime"
                                    type="datetime" placeholder="请选择发布时间" style="width: 100%; max-width: 10000px;" />
                            </el-form-item>

                            <el-form-item label="是否置顶">
                                <el-switch v-model="article.state" inline-prompt active-text="是" inactive-text="否"
                                    active-value="2" inactive-value="1" />
                            </el-form-item>

                            <el-divider />

                            <el-form-item label="文章标题">
                                <el-input v-model="article.title" placeholder="请输入文章标题" clearable />
                            </el-form-item>

                            <el-form-item label="文章分类">
                                <el-select v-model="article.categoryId" placeholder="请选择分类"
                                    style="width: 100%; max-width: 10000px;">
                                    <el-option v-for="item in categories" :key="item.id" :label="item.name"
                                        :value="item.id">
                                        <span style="float: left">{{ item.name }}</span>
                                        <span
                                            style="float: right;color: var(--el-text-color-secondary);font-size: 13px;">{{
                                                item.description
                                            }}</span>
                                    </el-option>
                                </el-select>
                            </el-form-item>

                            <el-form-item label="文章标签">
                                <el-select tag-type="success" v-model="article.labelTags" multiple filterable
                                    allow-create clearable default-first-option :reserve-keyword="false"
                                    placeholder="请选择或输入标签" @change="onLabelSelectChange"
                                    style="width: 100%; max-width: 10000px;">
                                    <el-option v-for="item in labels" :key="item.id" :label="item.name"
                                        :value="item.name" />
                                </el-select>
                            </el-form-item>

                            <el-divider />
                            <el-form-item label="文章摘要">
                                <el-input type="textarea" :rows="5" v-model="article.summary" autocomplete="off"
                                    placeholder="请输入文章摘要,若不填写默认为文章的前55字"></el-input>
                            </el-form-item>

                            <el-divider />
                            <el-form-item label="封面图片">
                                <el-tooltip content="仅支持小于5MB的jpg、png、gif文件" placement="left">
                                    <el-upload style="width:100%;" @click="preCoverUpload" action="/admin/image"
                                        disabled class="coverUploader">
                                        <!-- 选择后 -->
                                        <el-image v-if="article.cover !== '' && article.cover.indexOf('image') !== -1"
                                            style="width: 100%; height: 220px;" :src="article.cover" fit="scale-down" />
                                        <!-- 选择前 -->
                                        <el-icon v-else class="cover-uploader">
                                            <component :is="plusIcon" />
                                        </el-icon>
                                    </el-upload>
                                </el-tooltip>
                            </el-form-item>

                            <el-button color="#626aef" plain @click="dropArticleCover">移除图片</el-button>
                        </el-form>
                    </div>
                </el-scrollbar>
                <!-- 提交板块 -->
                <template #footer>
                    <div style="flex: auto">
                        <el-button @click="drawerShow = false">取 消</el-button>
                        <el-button type="danger" @click="doSave()">{{ sketchArticleText }}</el-button>
                        <el-button type="primary" @click="doPost()">{{ postArticleText }}</el-button>
                    </div>
                </template>
            </el-drawer>
        </div>

        <!-- 选择图片的dialog -->
        <div class="article-post-dialog-box">
            <el-dialog title="图片操作" v-model="selectCoverImgShow" width="50%" center>
                <div class="image-selector-box">
                    <div class="image-action-bar" style="margin-bottom:10px;">
                        <el-upload action="/admin/image" :show-file-list="false" :on-success="uploadSuccess"
                            :on-error="uploadError" :before-upload="beforeUpload">
                            <el-button type="primary">上传图片</el-button>
                            <template #tip>
                                <span style="font-size:12px;padding-left:10px;">请上传小于5MB的png/jpg/gif文件</span>
                            </template>
                        </el-upload>
                    </div>
                    <div class="image-list">
                        <el-radio-group v-model="article.cover">
                            <el-radio-button :label="image.url" border v-for="image in images" :key="image.id">
                                <el-image :src="$backendUrl + '/portal/image/' + image.url" fit="scale-down"
                                    style="border-radius: 4px;">
                                </el-image>
                            </el-radio-button>
                        </el-radio-group>
                    </div>
                </div>
                <div class="image-page-navigation-box">
                    <el-pagination center background layout="prev, pager, next, jumper" :current-page="pageInfo.curPage"
                        @current-change="onCurPageChange" :page-size="pageInfo.defSize" :total="pageInfo.totalSize"
                        :hide-on-single-page="false" />
                </div>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="selectCoverImgShow = false">取 消</el-button>
                        <el-button type="primary" @click="onCoverImgSelected">确 定</el-button>
                    </span>
                </template>
            </el-dialog>
        </div>
    </div>

</template>

<script>
import { showCategories, SUCCESS, showLabelList, showImages, addImage, addSketchArticle, addArticle, showPostedArticle, editPostedArticle, showSketchArticle, editSketchArticle, deleteSketchArticle } from "../../api/api";
import { showMessage, showMessageBox, showNotify } from '../../utils/utils';
import { markRaw } from "vue";
import { Plus } from "@element-plus/icons-vue";
import dayjs from 'dayjs';

export default {
    data() {
        return {
            plusIcon: markRaw(Plus),
            sketchArticleText: '保存草稿',  // 默认是保存，如果是 doEdit状态，就是 备份草稿， 如果是 doSketch状态， 就是 更新草稿
            postArticleText: '发表文章',    // 默认是发表，如果是 doEdit状态，那么就是 更新文章， 如果是 doSketch状态， 就也是 发表文章
            postState: 'doPost',  // 文章状态，默认是doPost， doEdit是编辑，doSketch是编辑草稿
            drawerShow: false,
            selectCoverImgShow: false,
            toolbarStr:
                "undo redo clear | emoji h bold italic strikethrough quote | ul ol table hr | link image code | save",
            article: {
                title: "",
                content: "",
                categoryId: "",
                createTime: '',
                state: '1',
                labels: '',
                summary: '',
                cover: '',  // 封面图片
                labelTags: [],
            },
            categories: '',
            labels: '',
            images: [],
            // 分页数据
            pageInfo: {
                curPage: 1,
                defSize: 10,
                totalSize: 0
            },
        };
    },
    methods: {
        // 标签选择改变的触发函数
        onLabelSelectChange() {
            let tags = ''
            for (let i = 0; i < this.article.labelTags.length; i++) {
                tags += this.article.labelTags[i] + ","
            }
            tags = tags.substring(0, tags.length - 1)
            this.article.labels = tags
        },
        // 图片数据转化
        data2blob(data, mime) {
            data = data.split(',')[1];
            data = window.atob(data);
            var ia = new Uint8Array(data.length);
            for (var i = 0; i < data.length; i++) {
                ia[i] = data.charCodeAt(i);
            };
            return new Blob([ia], {
                type: mime
            });
        },
        // markdown编辑器处理图片上传
        editorUploadImg(event, insertImage, files) {
            const loading = this.$loading({
                lock: true,
                text: '图片上传中...',
                target: document.querySelector('.article-post-part')
            });
            files = files[0]
            if (this.beforeUpload(files)) {
                let imgName = files.name
                let imgType = files.type
                let fileReader = new FileReader()
                fileReader.readAsDataURL(files)
                fileReader.onload = () => {
                    let imgData = fileReader.result
                    let fmData = new FormData()
                    fmData.append("file", this.data2blob(imgData, imgType), imgName)
                    addImage(fmData).then(res => {
                        loading.close()
                        // 插入到编辑器中
                        insertImage({
                            url: this.$backendUrl + '/portal/image/' + res.data.id,
                            desc: res.data.name.split(".")[0],
                        });
                    })
                }
            }
        },
        // 获取分类列表
        getNeededData() {
            showCategories().then(res => {
                if (res.code === SUCCESS) {
                    this.categories = res.data
                } else {
                    showMessage("分类数据获取失败,请重试", 'error')
                }
            })
            showLabelList().then(res => {
                if (res.code === SUCCESS) {
                    this.labels = res.data
                } else {
                    showMessage("标签数据获取失败,请重试", 'error')
                }
            })
        },
        // 点击发布文章按钮，弹出抽屉交互
        handlePost() {
            // 判断分类、标签信息是否获取到
            if (this.categories === '') {
                const loading = this.$loading({
                    lock: true,
                    text: '分类获取中...',
                    target: document.querySelector('.el-main')
                });
                showCategories().then(res => {
                    if (res.code === SUCCESS) {
                        this.categories = res.data
                        loading.close()
                        this.drawerShow = true
                    } else {
                        showNotify("分类获取", res.message, 'error')
                        this.drawerShow = false
                    }
                })
            } else if (this.labels === '') {
                const loading = this.$loading({
                    lock: true,
                    text: '标签获取中...',
                    target: document.querySelector('.el-main')
                });
                showCategories().then(res => {
                    if (res.code === SUCCESS) {
                        this.labels = res.data
                        loading.close()
                        this.drawerShow = true
                    } else {
                        showNotify("标签获取", res.message, 'error')
                        this.drawerShow = false
                    }
                })
            } else {
                this.drawerShow = true
            }
            if (this.article.createTime === '') {
                // 设置默认发布时间
                this.article.createTime = dayjs().format('YYYY-MM-DD HH:mm:ss')
            }
        },
        // 保存草稿
        doSave() {
            if (this.article.createTime === '') {
                this.article.createTime = dayjs().format('YYYY-MM-DD HH:mm:ss')
            }
            showMessageBox("确认" + this.sketchArticleText + "吗?", "保存").then(() => {
                if (this.article.title === '' || this.article.title.trim() === '') {
                    showMessage("请输入文章草稿的标题!", 'warning')
                    return
                }
                if (this.article.content === '' || this.article.content === "") {
                    showMessage("请输入文章草稿的内容!", 'warning')
                    return
                }
                // 如果是编辑草稿，那么去请求 更新草稿 的api
                if (this.postState === 'doSketch') {
                    if (this.$route.query.articleId !== '' &&
                        this.$route.query.articleId !== undefined &&
                        this.$route.query.articleId !== null) {
                        editSketchArticle(this.$route.query.articleId, this.article).then(res => {
                            if (res.code === SUCCESS) {
                                if (this.drawerShow) {
                                    this.drawerShow = false
                                }
                                showNotify("更新草稿", res.message + "\n已清空编辑器!", 'success', 3500)
                                Object.assign(this.$data, this.$options.data.call(this))
                                this.$router.push("/content/post-article")
                            } else {
                                showNotify("更新草稿", res.message + "\n请检查重试!", 'error', 3500)
                            }
                        })
                    }

                } else {
                    // 否则就是 新增草稿 的api
                    addSketchArticle(this.article).then(res => {
                        if (res.code === SUCCESS) {
                            if (this.drawerShow) {
                                this.drawerShow = false
                            }
                            showNotify("添加草稿", res.message + "\n已清空编辑器!", 'success', 3500)
                            // 草稿新增成功，就将文章信息重置
                            if (this.postState === 'doEdit') {
                                Object.assign(this.$data, this.$options.data.call(this))
                                this.$router.push("/content/post-article")
                            } else {
                                Object.assign(this.$data.article, this.$options.data().article)
                            }
                        } else {
                            showNotify("添加草稿", res.message + "\n请检查重试!", 'error', 3500)
                        }
                    })
                }

            }).catch(() => { showMessage("取消保存", 'info') });
        },
        // 发布文章\更新文章
        doPost() {
            if (this.checkPostArticleParmars()) {
                if (this.postState === 'doPost') {
                    const loading = this.$loading({
                        lock: true,
                        text: '文章发布提交中...',
                        target: '.el-drawer__body',
                    });
                    addArticle(this.article).then(res => {
                        loading.close()
                        if (res.code === SUCCESS) {
                            if (this.drawerShow) {
                                this.drawerShow = false
                            }
                            showNotify("发表文章", res.message + "\n已清空编辑器!", 'success', 3500)
                            Object.assign(this.$data.article, this.$options.data().article)
                        } else {
                            showNotify("发表文章", res.message + "\n请检查重试!", 'error', 3500)
                        }
                    })
                } else if (this.postState === 'doSketch') {
                    const loading = this.$loading({
                        lock: true,
                        text: '文章发布提交中...',
                        target: '.el-drawer__body',
                    });
                    addArticle(this.article).then(res => {
                        loading.close()
                        if (res.code === SUCCESS) {
                            if (this.drawerShow) {
                                this.drawerShow = false
                            }
                            showNotify("发表文章", res.message + "\n已清空编辑器!", 'success', 3500)
                            // 如果是编辑草稿，需要询问是否保留草稿
                            showMessageBox("是否删除该草稿?", "删除草稿").then(() => {
                                const loading = this.$loading({
                                    lock: true,
                                    text: '草稿文章删除中...',
                                    target: '.el-main',
                                });
                                if (this.$route.query.articleId !== '' &&
                                    this.$route.query.articleId !== undefined &&
                                    this.$route.query.articleId !== null) {
                                    deleteSketchArticle(this.$route.query.articleId).then(res => {
                                        loading.close()
                                        if (res.code === SUCCESS) {
                                            showMessage(res.message)
                                        } else {
                                            showMessage("草稿删除失败,请重试!", 'error')
                                        }
                                        Object.assign(this.$data, this.$options.data.call(this))
                                        this.$router.push("/content/post-article")
                                    })
                                }

                            }).catch(() => {
                                showMessage("仍然保存草稿!")
                                Object.assign(this.$data, this.$options.data.call(this))
                                this.$router.push("/content/post-article")
                            });
                        } else {
                            showNotify("发表文章", res.message + "\n请检查重试!", 'error', 3500)
                        }
                    })
                } else if (this.postState === 'doEdit') {
                    if (this.$route.query.articleId !== '' &&
                        this.$route.query.articleId !== undefined &&
                        this.$route.query.articleId !== null) {
                        const loading = this.$loading({
                            lock: true,
                            text: '文章更新提交中...',
                            target: '.el-drawer__body',
                        });
                        editPostedArticle(this.$route.query.articleId, this.article).then(res => {
                            loading.close()
                            if (res.code === SUCCESS) {
                                if (this.drawerShow) {
                                    this.drawerShow = false
                                }
                                showNotify("更新文章", res.message + "\n已清空编辑器!")
                                Object.assign(this.$data, this.$options.data.call(this))
                                this.$router.push("/content/post-article")
                            } else {
                                showNotify("更新文章", res.message + "\n请检查重试!", 'error')
                            }
                        })
                    } else {
                        showMessage("未选择编辑的文章Id!", 'error')
                    }
                }

            }
        },
        // 检查发布文章的必要参数
        checkPostArticleParmars() {
            // 检测参数
            if (this.article.title === '' || this.article.title.trim() === '') {
                showMessage("请输入文章的标题!", 'warning')
                return false
            }
            if (this.article.content === '' || this.article.content === "") {
                showMessage("请输入文章的内容!", 'warning')
                return false
            }
            if (this.article.categoryId === '' || this.article.categoryId.trim() === '') {
                showMessage("请选择文章的分类!", "warning")
                return false
            }
            if (this.article.labels === '') {
                showMessage("请选择文章的标签!", 'warning')
                return false
            }
            if (this.article.summary === '') {
                this.article.summary = this.article.content.length > 55 ? this.article.content.substring(0, 55) : this.article.content
            }
            if (this.article.createTime === '') {
                this.article.createTime = dayjs().format('YYYY-MM-DD HH:mm:ss')
            }
            return true
        },
        // 处理图片结果
        handleImageRes(res) {
            if (res.code === SUCCESS) {
                this.images = res.data.content
                // 设置页码
                this.pageInfo.curPage = res.data.number + 1
                this.pageInfo.totalSize = res.data.totalElements
            } else {
                showMessage("获取图片列表失败", 'error')
            }
        },
        // 获取图片列表
        getImages() {
            this.article.cover = ''     // 重置选择的图片
            const loading = this.$loading({
                lock: true,
                text: '图片列表获取中...',
                target: '.image-list',
            });
            document.querySelector(".el-loading-mask").style.zIndex = '114514';
            showImages(this.pageInfo.curPage, this.pageInfo.defSize).then(res => {
                this.handleImageRes(res)
                loading.close()
            })
        },
        // 取消文章封面图片
        dropArticleCover() {
            this.article.cover = ''
        },
        // 点击上传封面
        preCoverUpload() {
            // 获取图片
            this.getImages()
            // 打开dialog
            this.selectCoverImgShow = true
        },
        // 进行图片选择
        onCoverImgSelected() {
            this.article.cover = this.$backendUrl + '/portal/image/' + this.article.cover
            this.selectCoverImgShow = false
        },
        // 上传封面检查
        beforeUpload(rawFile) {
            if (!(rawFile.type === 'image/jpeg' || rawFile.type === 'image/png' || rawFile.type === 'image/gif')) {
                showMessage("请使用正确的图片格式!", 'error')
                return false
            }
            if (rawFile.size / 1024 / 1024 > 5) {
                showMessage("请选择小于5MB的图片!", 'error')
                return false
            }
            return true
        },
        // 上传成功
        uploadSuccess(response, uploadFile) {
            showNotify("上传成功! 图片ID👇", response.data.id);
            this.article.cover = response.data.id
            showImages(1, 10).then(res => {
                if (res.code === SUCCESS) {
                    this.images = res.data.content
                    Object.assign(this.$data.pageInfo, this.$options.data().pageInfo)
                }
            })
        },
        // 上传失败
        uploadError() {
            showMessage("图片上传失败,请重试!", 'error')
        },
        // 图片列表分页
        onCurPageChange(page) {
            this.pageInfo.curPage = page
            this.getImages()
        },
        // 检查是否是编辑旧文章，或者编辑草稿
        checkIsEditOrSketch() {
            let curState = this.$route.query.state
            let curArticleId = this.$route.query.articleId
            if (curState === 'doEdit') {
                const loading = this.$loading({
                    lock: true,
                    text: '文章数据请求中...',
                    target: document.querySelector('.article-post-box')
                });
                this.postState = curState
                this.sketchArticleText = "备份草稿"
                this.postArticleText = "更新文章"
                // 如果是编辑文章，获取这个文章的数据
                showPostedArticle(curArticleId).then(res => {
                    loading.close()
                    if (res.code === SUCCESS) {
                        Object.keys(this.article).forEach((value) => {
                            this.article[value] = res.data[value]
                        })
                    } else {
                        showMessage("获取文章信息失败,请重试!", 'error')
                    }
                })
            } else if (curState === 'doSketch') {
                const loading = this.$loading({
                    lock: true,
                    text: '草稿数据请求中...',
                    target: document.querySelector('.article-post-box')
                });
                this.postState = curState
                this.sketchArticleText = "更新草稿"
                this.postArticleText = "发布文章"
                // 获取草稿数据
                showSketchArticle(curArticleId).then(res => {
                    loading.close()
                    if (res.code === SUCCESS) {
                        Object.keys(this.article).forEach((value) => {
                            this.article[value] = res.data[value]
                        })
                        if (this.article.labels === '') {
                            this.article.labelTags = []
                        }
                    } else {
                        showMessage("获取草稿信息失败,请重试!", 'error')
                    }
                })
            }
        },
    },
    mounted() {
        this.getNeededData()
        this.checkIsEditOrSketch()
    }
};
</script>

<style>
.article-post-box .el-card__header {
    padding-top: 15px;
    padding-bottom: 11px;
}

.article-title-part .el-input__inner {
    height: 40px;
    font-size: 16px;
}

.action-btn-container {
    float: right;

    padding-top: 15px;
    padding-bottom: 15px;
}

.article-post-box .el-card__body {
    padding-top: 15px;
}

.article-post-drawer-box .el-drawer__header {
    font-size: 18px;
    font-weight: 600;
    padding: 16px 18px;
    margin: 0;
    border-bottom: 1px solid #dddddd;
}

.article-post-drawer-box .el-drawer__footer {
    padding: 12px 16px;
    border-top: 1px solid #dddddd;
}

.article-post-drawer-box .el-drawer__body {
    padding-left: 0;
    padding-right: 0;
}

.article-post-dialog-box .el-dialog__body {
    padding-top: 10px;
    padding-bottom: 10px;
}

.article-post-drawer-box .coverUploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
    background: #fdfdfd;
}

.article-post-drawer-box .coverUploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.article-post-drawer-box .coverUploader .el-upload--text {
    width: 100%;
    height: 220px;
}

.article-post-drawer-box .el-icon.cover-uploader {
    font-size: 28px;
    color: #8c939d;
    width: 100%;
    height: 220px;
    text-align: center;
}

.image-selector-box .image-list img {
    padding: 0;
}

.image-selector-box .image-list .el-radio-button {
    width: 20%;
}

.image-selector-box .image-list .el-radio-button__inner {
    padding: 2px;
    margin-right: 5px;
    margin-bottom: 5px;
    margin-top: 5px;
    border: 0;
    border-radius: 4px;
}

.image-selector-box .image-list .el-radio-button:first-child .el-radio-button__inner,
.image-selector-box .image-list .el-radio-button:last-child .el-radio-button__inner {
    padding: 2px;
    margin-right: 5px;
    margin-right: 5px;
    margin-bottom: 5px;
    margin-top: 5px;
    border: 0;
    border-radius: 4px;
}

.article-title-part .el-input__wrapper {
    height: 40px;
}
</style>