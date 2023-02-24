<template>
    <div class="image-manage-box">
        <el-card class="image-action-bar">
            <el-scrollbar>
                <div style="display:flex;">
                    <div
                        style="flex:left;text-align: center; font-weight: 600; font-size: 18px; margin-top:auto; margin-bottom:auto; min-width:90px">
                        图片附件库</div>
                    <el-button style="margin-left:auto;" type="success" @click="getImages" :icon="refreshIcon">刷新图片
                    </el-button>
                    <el-button type="primary" @click="add" :icon="addIcon">上传图片</el-button>
                    <el-popconfirm title="确认批量删除图片嘛?" @confirm="batchFree">
                        <template #reference>
                            <el-button type="danger" :icon="deleteIcon">批量删除</el-button>
                        </template>
                    </el-popconfirm>
                    <el-button type="warning" @click="changeAllSelect" :icon="selectIcon">{{ selectText }}</el-button>
                </div>
            </el-scrollbar>
        </el-card>

        <div class="image-list-container">
            <div style="display:flex;">
                <el-card shadow="hover" v-for="(item, index) in images.slice(0, 5)" :key="index"
                    id="image-row-container-card">
                    <template #header>
                        <el-image hide-on-click-modal preview-teleported fit="cover"
                            :src="$backendUrl + '/portal/image/' + item.url" lazy :preview-src-list="imageUrlList"
                            :initial-index="index" v-if="item.url !== '' && item.url !== undefined"
                            style=" width: 100%;height: 230px;border-radius: 5px 5px 1px 1px;margin: auto;">
                            <template #error>
                                <div class="image-slot">
                                    <el-icon>
                                        <component :is="errorIcon" />
                                    </el-icon>
                                </div>
                            </template>
                        </el-image>

                    </template>
                    <div v-if="item.name !== '' && item.name !== undefined" style="display: flex;">
                        <span class="image-name-span">
                            <el-checkbox @change="selectImageChange(index)" size="small" v-model="isSelected[index]"
                                label="true">
                            <span v-if="item.name.split('.')[0].length > 10">
                                {{item.name.split('.')[0].substring(0,10)+"..."}}
                            </span>
                        <span v-else>{{item.name.split('.')[0]}} </span></el-checkbox>
                        </span>
                        <span class="image-operation" style="margin-left:auto;">
                            <el-button size="small" type="primary" plain @click="copyImgUrl(item)">复制</el-button>
                            <el-popconfirm :title="'确定删除图片' + item.name + '嘛?'" @confirm="free(item.id, index)">
                                <template #reference>
                                    <el-button size="small" type="danger" plain :loading="isFreeing[index]">删除
                                    </el-button>
                                </template>
                            </el-popconfirm>
                        </span>
                    </div>
                </el-card>
            </div>
            <div style="display:flex;">
                <el-card shadow="hover" v-for="(item, index) in images.slice(5, 10)" :key="index"
                    id="image-row-container-card">
                    <template #header>
                        <el-image hide-on-click-modal preview-teleported fit="cover"
                            :src="$backendUrl + '/portal/image/' + item.url" lazy :preview-src-list="imageUrlList"
                            :initial-index="index + 5" v-if="item.url !== '' && item.url !== undefined"
                            style=" width: 100%;height: 230px;border-radius: 5px 5px 1px 1px;margin: auto;">
                            <template #error>
                                <div class="image-slot">
                                    <el-icon>
                                        <component :is="errorIcon" />
                                    </el-icon>
                                </div>
                            </template>
                        </el-image>
                    </template>
                    <div v-if="item.name !== '' && item.name !== undefined" style="display: flex;">
                        <span class="image-name-span">
                            <el-checkbox @change="selectImageChange(index + 5)" size="small"
                                v-model="isSelected[index + 5]" label="true">{{
                                    item.name.split('.')[0]
                                }}</el-checkbox>
                        </span>
                        <span class="image-operation" style="margin-left:auto;">
                            <el-button size="small" type="primary" plain @click="copyImgUrl(item)">复制</el-button>
                            <el-popconfirm :title="'确定删除图片' + item.name + '嘛?'" @confirm="free(item.id, index + 5)">
                                <template #reference>
                                    <el-button size="small" type="danger" plain :loading="isFreeing[index + 5]">删除
                                    </el-button>
                                </template>
                            </el-popconfirm>
                        </span>
                    </div>
                </el-card>
            </div>
        </div>
        <div class="image-list-page-navigation-box">
            <el-pagination center background layout="prev, pager, next, jumper" :current-page="pageInfo.curPage"
                @current-change="onCurPageChange" :page-size="pageInfo.defSize" :total="pageInfo.totalSize"
                :hide-on-single-page="false" />
        </div>

        <div class="image-dialog-box">
            <pic-upload ref="uploadRef" field="file" :imgName="imageName" @crop-upload-success="cropUploadSuccess"
                @crop-upload-fail="cropUploadFail" @src-file-set="srcFileSet" v-model="showCrop" url="/admin/image"
                :maxSize="5120" img-format="png">
            </pic-upload>
        </div>  </div>
</template>

<script>
import { Refresh, Delete, Picture, Close, CirclePlus, CircleClose } from '@element-plus/icons-vue'
import { markRaw } from 'vue'
import { showImages, SUCCESS, deleteImage } from "../../api/api";
import { showMessage, showNotify } from '../../utils/utils';
import picUpload from "vue-image-crop-upload";
import "babel-polyfill";

export default {
    components: {
        "pic-upload": picUpload,
    },
    data() {
        return {
            refreshIcon: markRaw(Refresh),
            deleteIcon: markRaw(Delete),
            addIcon: markRaw(Picture),
            errorIcon: markRaw(Close),
            selectIcon: markRaw(CirclePlus),
            selectText: '全选该页',
            showCrop: false,
            imageName: '',
            // 获取的图片
            images: [
                { url: '' }
            ],
            // 分页数据
            pageInfo: {
                curPage: 1,
                defSize: 10,
                totalSize: 0
            },
            imageUrlList: [],
            selectedImages: [],
            isAllSelect: false,
            isSelected: [false, false, false, false, false, false, false, false, false, false],
            isFreeing: [false, false, false, false, false, false, false, false, false, false],   // 是否正在删除
        }
    },
    methods: {
        // 批量删除
        batchFree() {
            if (this.selectedImages.length > 0) {
                let ids = ''
                this.selectedImages.forEach((value) => {
                    ids += value + "_"
                })
                this.isFreeing = this.isSelected
                deleteImage(ids).then(res => {
                    if (res.code === SUCCESS) {
                        showNotify("批量删除", res.message, 'success', 3500)
                    } else {
                        showNotify("批量删除", res.message, 'error', 3500)
                    }
                    this.getImages()
                })
            } else {
                showMessage("请至少选择一张图片!", 'warning')
            }
        },
        // 上传图片
        add() {
            this.showCrop = true
            this.imageName = ''
        },
        // 获取图片图片列表
        getImages() {
            const loading = this.$loading({
                lock: true,
                text: '图片列表获取中...',
                target: '.image-list-container',
            });
            showImages(this.pageInfo.curPage, this.pageInfo.defSize).then(res => {
                loading.close()
                this.handleImageRes(res)
            })
        },
        // 处理图片结果
        handleImageRes(res) {
            if (res.code === SUCCESS) {
                this.images = res.data.content
                this.isAllSelect = false
                this.selectText = '全选该页'
                this.selectIcon = markRaw(CirclePlus)
                this.selectedImages = []
                this.imageUrlList = []
                this.isSelected = [false, false, false, false, false, false, false, false, false, false]
                this.isFreeing = [false, false, false, false, false, false, false, false, false, false]
                this.images.forEach((value) => {
                    this.imageUrlList.push(this.$backendUrl + '/portal/image/' + value.url)
                })
                // 设置页码
                this.pageInfo.curPage = res.data.number + 1
                this.pageInfo.totalSize = res.data.totalElements
            } else {
                showMessage("获取图片列表失败", 'error')
            }
        },
        // 图片列表分页
        onCurPageChange(page) {
            this.pageInfo.curPage = page
            this.getImages()
        },
        // 上传成功
        cropUploadSuccess(jsonData, field) {
            if (jsonData.code === SUCCESS) {
                this.showCrop = false;
                showNotify("上传成功! 图片ID👇", jsonData.data.id);
            } else {
                showMessage(jsonData.message, "error");
            }
            this.imageName = "";
            this.$refs.uploadRef.off();
            this.getImages()
        },
        // 上传失败
        cropUploadFail(status, field) {
            showMessage("上传图片失败!", "error");
            this.imageName = "";
            this.$refs.uploadRef.off();
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
            if (fileSize / 1024 / 1024 > 5) {
                showMessage("请选择小于5MB的图片!", "error");
                this.showCrop = false;
                return;
            }
            this.imageName = fileName.split(".")[0];
        },
        // 确认后的删除图片
        free(id, index) {
            this.isFreeing[index] = true
            deleteImage(id).then(res => {
                this.isFreeing[index] = false
                if (res.code === SUCCESS) {
                    showNotify("删除图片", "图片 " + this.images[index].name + " 删除成功!", 'success', 3500)
                    this.getImages()
                } else {
                    showNotify("删除图片", res.message, 'error', 3500)
                }
            })
        },
        // 是否全选
        changeAllSelect() {
            // 如果当前没全选，那么就去全选
            if (!this.isAllSelect) {
                this.isSelected = [true, true, true, true, true, true, true, true, true, true]
                this.images.forEach((value) => {
                    if (!this.selectedImages.includes(value.id)) {
                        this.selectedImages.push(value.id)
                    }
                })
                this.isAllSelect = true
                this.selectText = '取消选择'
                this.selectIcon = markRaw(CircleClose)
            } else {
                this.isSelected = [false, false, false, false, false, false, false, false, false, false]
                this.selectedImages = []
                this.isAllSelect = false
                this.selectText = '全选该页'
                this.selectIcon = markRaw(CirclePlus)
            }

        },
        // 选中的图片列表的改变
        selectImageChange(index) {
            let selectedId = this.images[index].id
            let cur = this.selectedImages.indexOf(selectedId)
            // 如果删除列表中不存在，就添加进去
            if (cur === -1) {
                this.selectedImages.push(selectedId)
            } else {
                this.selectedImages.splice(cur, 1)
            }
        },
        // 复制图片的url
        copyImgUrl(item) {
            let copyText = this.$backendUrl + '/portal/image/' + item.url;
            let input = document.createElement("input"); // 直接构建input
            input.value = copyText; // 设置内容
            document.body.appendChild(input); // 添加临时实例
            input.select(); // 选择实例内容
            if (document.execCommand("Copy")) {
                document.execCommand("Copy")
                showMessage("复制图片 " + item.name + " 地址成功!")
            } else {
                showMessage("复制图片地址失败,请检查浏览器权限!", 'error')
            }
            document.body.removeChild(input); // 删除临时实例
        },
    },
    mounted() {
        this.getImages()
    }
}
</script>

<style>
.image-manage-box .image-list-container .el-card__header {
    padding: 0;
    border: 0;
    height: 230px;
}

.image-action-bar .el-card__body {
    padding: 10px 20px;
}

.image-action-bar {
    margin-bottom: 10px;
}

.image-list-container .el-row {
    margin-bottom: 25px;
}

.image-list-container .el-card__body {
    height: 20px;
    padding-left: 12px;
    padding-right: 4px;
    padding-top: 6px;
    padding-bottom: 6px;
    line-height: 20px;
}

.image-list-container .el-card__body div .el-checkbox__label {
    width: 100%;
    margin-bottom: 3px;
    color: rgba(0, 0, 0, .45);
    font-size: 14px;
    font-variant: tabular-nums;
    line-height: 20px;
}

.image-list-container .el-card__body div .el-checkbox__label:hover {
    color: #409eff;
}

.image-list-container .el-card__body div .el-checkbox__inner {
    margin-bottom: 3px;
}

.image-list-container .el-card__body .image-operation {
    color: #409eff;
}

.image-list-container .el-card__body .image-operation .el-button {
    line-height: 18px;
    height: 18px;
    min-height: 18px;
    max-height: 18px;
    padding: 1px 11px;
    margin-bottom: 2px;
    margin-left: 4px;;
    border: 0;
}

.image-list-container .el-card__body .image-operation .el-button span {
    font-size: 10px;
}

.image-list-page-navigation-box .el-pagination {
    float: right;
    font-weight: 600;
}

#image-row-container-card {
    flex: left;
    min-width: 18%;
    width: 20%;
    margin-bottom: 15px;
    margin-right: 5px;
    margin-left: 5px;
}
</style>