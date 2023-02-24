<template>
    <div class="friend-link-box">
        <el-card>
            <template #header>
                <div style="text-align: center; font-weight: 600; font-size: 18px">
                    <span>友链管理</span>
                </div>
            </template>
            <!-- 顶部操作按钮 -->
            <div class="friend-link-action-bar">
                <el-button type="success" @click="getFriendLinkList" :icon="refreshIcon">刷新友链</el-button>
                <el-button type="primary" @click="add" :icon="addIcon">添加友链</el-button>
                <el-button type="danger" @click="batchFree" :icon="deleteIcon">批量删除</el-button>
            </div>
            <!-- 表单 -->
            <div class="category-list-box">
                <el-table :data="friendLinkList" style="width: 100%" @selection-change="handleSelectionChange">
                    <el-table-column type="selection" fixed />
                    <el-table-column prop="id" label="ID" align="center" width="175" />
                    <el-table-column label="友链名称与url" align="center">
                        <template v-slot="scope">
                            <a style="text-decoration: none;" :href="scope.row.url" target="_blank">
                                <el-tag style="font-size:14px" effect="plain">{{ scope.row.name }}</el-tag>
                            </a>
                        </template>
                    </el-table-column>
                    <el-table-column label="友链logo" align="center" width="100">
                        <template v-slot="scope">
                            <a :href="scope.row.logo" target="_blank">
                                <el-image style="width: 60px; height: 60px" :src="scope.row.logo" fit="scale-down"
                                    lazy />
                            </a>
                        </template>
                    </el-table-column>
                    <el-table-column prop="order" label="顺序" align="center" width="80" sortable />
                    <el-table-column prop="createTime" label="创建日期" align="center" width="170" sortable />
                    <el-table-column prop="updateTime" label="更新日期" align="center" width="170" sortable />
                    <el-table-column fixed="right" label="操作" align="center" width="180">
                        <template v-slot="scope">
                            <el-button type="primary" @click="edit(scope.row)" plain :round="true">编 辑</el-button>
                            <el-button type="danger" @click="free(scope.row)" plain :round="true">删 除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </el-card>
        <div class="friend-link-dialog-box">
            <!-- 单个删除 -->
            <el-dialog v-model="deleteShow" title="删除提示" width="25%" draggable center>
                <span>您确认要删除友链: {{ deleteMessage }} 吗？</span>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="danger" @click="doFree">确 定</el-button>
                    </span>
                </template>
            </el-dialog>

            <!-- 批量删除 -->
            <el-dialog v-model="deleteBatchShow" title="批量删除" width="25%" draggable center>
                <span>您确认要批量删除友链吗？</span>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="danger" @click="doBatchFree">确 定</el-button>
                    </span>
                </template>
            </el-dialog>

            <!-- 添加/修改友链dialog -->
            <el-dialog v-model="addShow" :title="formTitle" width="25%" draggable center>
                <el-form :model="friendLinkForm" ref="friendLinkFormRef" label-position="right" label-width="80px">
                    <el-form-item label="友链名称">
                        <el-input v-model="friendLinkForm.name" autocomplete="off" placeholder="请输入友链名称(必填!)" />
                    </el-form-item>
                    <el-form-item label="友链链接">
                        <el-input v-model="friendLinkForm.url" autocomplete="off"
                            placeholder="请输入友链url,http或https开头(必填!)" />
                    </el-form-item>
                    <el-form-item label="图片方式">
                        <el-switch active-text="图片地址" inactive-text="上传图片" v-model="logoWay" />
                    </el-form-item>
                    <!-- 上传图片 -->
                    <el-form-item label="上传图片" v-if="logoWay === false" id="logo-uploader-form-item">
                        <el-tooltip content="仅支持小于2MB的jpg、png、gif文件" placement="right">
                            <el-upload class="logo-uploader" action="/admin/image" :show-file-list="false"
                                :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload">
                                <img v-if="logoNewUrl !== ''" :src="logoNewUrl" class="friend-link-logo" />
                                <el-icon v-else class="logo-uploader-icon">
                                    <component :is="plusIcon"></component>
                                </el-icon>
                            </el-upload>
                        </el-tooltip>
                    </el-form-item>
                    <!-- 图片地址 -->
                    <el-form-item label="图片地址" v-else>
                        <el-input v-model="friendLinkForm.logo" autocomplete="off" placeholder="请输入友链logo地址"
                            width="100" />
                    </el-form-item>
                    <el-form-item label="排序顺序">
                        <el-input-number v-model="friendLinkForm.order" :min="1" :max="10" align="center" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="success" @click="doAdd" v-if="formTitle === '添加友链'">确 定</el-button>
                        <el-button type="success" @click="doEdit" v-else>更 新</el-button>
                    </span>
                </template>
            </el-dialog>
        </div>  </div>
</template>

<script>
import { Refresh, Delete, DocumentAdd,Plus } from '@element-plus/icons-vue'
import { markRaw } from 'vue'
import { showFriendLinkList, SUCCESS, deleteFriendLink, addFriendLink, editFriendLink } from '../../api/api'
import { showMessage } from '../../utils/utils'

export default {
    data() {
        return {
            formTitle: '',
            refreshIcon: markRaw(Refresh),
            deleteIcon: markRaw(Delete),
            addIcon: markRaw(DocumentAdd),
            plusIcon: markRaw(Plus),
            friendLinkList: [],
            friendLinkForm: {
                id: '',
                name: '',
                url: '',
                logo: '',
                order: 1,
            },
            selectedFriendLinks: [],
            deleteFriendLinkIds: '',
            deleteMessage: '',
            deleteShow: false,
            deleteBatchShow: false,
            addShow: false,
            deleteFriendLinkId: '',
            logoWay: true,
            logoNewUrl: '',
        }
    },
    methods: {
        add() {
            this.resetFriendLinkForm()
            this.formTitle = '添加友链'
            this.addShow = true
        },
        // 添加友链
        doAdd() {
            if (this.checkFriendLinkForm()) {
                addFriendLink(this.friendLinkForm).then(res => {
                    if (res.code === SUCCESS) {
                        showMessage(res.message)
                        this.getFriendLinkList()
                    } else {
                        showMessage(res.message, 'error')
                    }
                    this.doCancel()
                    this.resetFriendLinkForm()
                })
            }
        },
        // 检验表单输入
        checkFriendLinkForm() {
            if (this.friendLinkForm.name === '') {
                showMessage("友链名称不可为空!", 'error')
                return false
            }
            if (this.friendLinkForm.url === '') {
                showMessage("友链url不可为空!", 'error')
                return false
            }
            if (this.friendLinkForm.logo.trim() === '') {
                this.friendLinkForm.logo = "https://cdn.jsdelivr.net/gh/Awoodsheep/pic/imgs/friend_link.png"
            }
            return true
        },
        // 重置表单
        resetFriendLinkForm() {
            this.friendLinkForm.id = ''
            this.friendLinkForm.name = ''
            this.friendLinkForm.url = ''
            this.friendLinkForm.logo = ''
            this.friendLinkForm.order = 1
            this.logoNewUrl = ''
            this.deleteFriendLinkId = ''
            this.deleteFriendLinkIds = ''
            this.logoWay = true
        },
        edit(item) {
            this.formTitle = '编辑友链'
            this.friendLinkForm.id = item.id
            this.friendLinkForm.name = item.name
            this.friendLinkForm.url = item.url
            this.friendLinkForm.logo = item.logo
            this.friendLinkForm.order = 1
            this.addShow = true
        },
        // 更新友链
        doEdit() {
            if (this.checkFriendLinkForm()) {
                editFriendLink(this.friendLinkForm.id, this.friendLinkForm).then(res => {
                    if (res.code === SUCCESS) {
                        showMessage(res.message)
                        this.getFriendLinkList()
                    } else {
                        showMessage(res.message, 'error')
                    }
                    this.doCancel()
                    this.resetFriendLinkForm()
                })
            }
        },
        batchFree() {
            if (this.selectedFriendLinks.length === 0) {
                showMessage("请选择删除的友链", "warning")
                return
            }
            this.deleteFriendLinkIds = ''
            this.selectedFriendLinks.forEach((item) => {
                this.deleteFriendLinkIds += item.id + '_'
            })
            this.deleteBatchShow = true
        },
        // 批量删除
        doBatchFree() {
            deleteFriendLink(this.deleteFriendLinkIds).then(res => {
                if (res.code === SUCCESS) {
                    showMessage(res.message)
                    this.getFriendLinkList()
                } else {
                    showMessage(res.message, 'error')
                }
                this.deleteFriendLinkIds = ''
                this.doCancel()
            })
        },
        free(item) {
            this.deleteMessage = item.name
            this.deleteFriendLinkId = item.id
            this.deleteShow = true
        },
        // 删除友链
        doFree() {
            deleteFriendLink(this.deleteFriendLinkId).then(res => {
                if (res.code === SUCCESS) {
                    showMessage("删除友链" + this.deleteMessage + "成功!")
                    this.getFriendLinkList()
                } else {
                    showMessage("删除友链" + this.deleteMessage + "失败!", 'error')
                }
                this.deleteMessage = ''
                this.deleteFriendLinkId = ''
                this.doCancel()
            })
        },
        doCancel() {
            this.deleteShow = false
            this.deleteBatchShow = false
            this.addShow = false
        },
        handleSelectionChange(value) {
            this.selectedFriendLinks = value
        },
        getFriendLinkList() {
            this.resetFriendLinkForm()
            const loading = this.$loading({
                lock: true,
                text: '友链获取中...',
                target: document.querySelector('.el-main')
            });
            showFriendLinkList().then(res => {
                loading.close()
                if (res.code === SUCCESS) {
                    this.friendLinkList = res.data
                } else {
                    showMessage("获取友链列表失败", 'error')
                }
            })
        },
        // 处理图片上传
        handleAvatarSuccess(response) {
            if (response.code === SUCCESS) {
                this.logoNewUrl = this.$backendUrl + "/portal/image/" + response.data.id
                this.friendLinkForm.logo = this.logoNewUrl
                showNotify('上传成功! 图片ID👇', response.data.id)
            } else {
                showMessage(response.message, "error")
            }
        },
        // 检验图片上传
        beforeAvatarUpload(rawFile) {
            if (rawFile.type !== 'image/jpeg') {
                showMessage("请使用正确的图片格式!", 'error')
                return false
            } else if (rawFile.size / 1024 / 1024 > 2) {
                showMessage("图片必须小于2MB!", 'error')
                return false
            }
            return true
        },
    },
    mounted() {
        this.getFriendLinkList()
    }
}
</script>

<style>
.friend-link-action-bar {
    padding-bottom: 10px;
}

#logo-uploader-form-item .logo-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
}

#logo-uploader-form-item .logo-uploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

#logo-uploader-form-item .el-icon.logo-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 80px;
    height: 80px;
    text-align: center;
}

#logo-uploader-form-item .friend-link-logo {
    width: 80px;
    height: 80px;
    text-align: center;
}
</style>