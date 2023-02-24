<template>
    <div class="loop-box">
        <el-card>
            <template #header>
                <div style="text-align: center; font-weight: 600; font-size: 18px">
                    <span>轮播图设置</span>
                </div>
            </template>
            <div class="loop-action-bar">
                <el-button type="success" @click="getLoopList" :icon="refreshIcon">刷新图片</el-button>
                <el-button type="primary" @click="add" :icon="addIcon">添加图片</el-button>
                <el-button type="danger" @click="batchFree" :icon="deleteIcon">批量删除</el-button>
            </div>
            <div class="loop-list-box">
                <el-table :data="loops" style="width: 100%" @selection-change="handleSelectionChange">
                    <el-table-column type="selection" fixed />
                    <el-table-column prop="id" label="ID" align="center" width="175" />
                    <el-table-column label="目标名称与地址" align="center">
                        <template v-slot="scope">
                            <a class="loop-url" :href="scope.row.targetUrl" target="_blank">
                                <el-tag style="font-size:14px" effect="plain" type="success">
                                    {{ scope.row.title }}
                                </el-tag>
                            </a>
                        </template>
                    </el-table-column>
                    <el-table-column label="图片预览与地址" align="center">
                        <template v-slot="scope">
                            <a :href="scope.row.imageUrl" target="_blank">
                                <el-image style="width: 120px; height: 120px" :src="scope.row.imageUrl" fit="scale-down"
                                    lazy />
                            </a>
                        </template>
                    </el-table-column>
                    <el-table-column prop="order" label="顺序" align="center" width="80" sortable />
                    <el-table-column prop="createTime" label="创建日期" align="center" width="170" sortable />
                    <el-table-column prop="updateTime" label="更新日期" align="center" width="170" sortable />
                    <el-table-column fixed="right" label="操作" align="center" width="180">
                        <template v-slot="scope">
                            <el-button type="primary" @click="edit(scope.row)" :round="true" plain>编 辑</el-button>
                            <el-button type="danger" @click="free(scope.row)" :round="true" plain>删 除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </el-card>
        <div class="loop-dialog-box">
            <!-- 批量删除轮播图dialog -->
            <el-dialog v-model="deleteBatchShow" title="批量删除" width="25%" draggable center>
                <span>您确认要批量删除轮播图吗？</span>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="danger" @click="doBatchFree">确 定</el-button>
                    </span>
                </template>
            </el-dialog>

            <!-- 删除轮播图的dialog -->
            <el-dialog v-model="deleteShow" title="删除提示" width="25%" draggable center>
                <span>您确认要删除轮播图: {{ deleteMessage }} 吗？</span>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="danger" @click="doFree">确 定</el-button>
                    </span>
                </template>
            </el-dialog>

            <!-- 添加轮播图的dialog -->
            <el-dialog v-model="addShow" title="添加轮播图" width="25%" draggable center>
                <el-form :model="loopForm">
                    <el-form-item label="轮播图名">
                        <el-input v-model="loopForm.title" autocomplete="off" placeholder="请输入轮播图名称" />
                    </el-form-item>
                    <el-form-item label="目标地址">
                        <el-input v-model="loopForm.targetUrl" autocomplete="off" type="textarea" maxlength="128"
                            placeholder="请输入轮播图目标地址" />
                    </el-form-item>
                    <el-form-item label="图片地址">
                        <el-input v-model="loopForm.imageUrl" autocomplete="off" type="textarea" maxlength="128"
                            placeholder="请输入轮播图图片地址" />
                    </el-form-item>
                    <el-form-item label="排序顺序">
                        <el-input-number v-model="loopForm.order" :min="1" :max="10" align="center" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="success" @click="doAdd">确 定</el-button>
                    </span>
                </template>
            </el-dialog>

            <!-- 更改轮播图的dialog -->
            <el-dialog v-model="editShow" title="编辑轮播图" width="25%" draggable center>
                <el-form :model="loopForm">
                    <el-form-item label="轮播图名">
                        <el-input v-model="loopForm.title" autocomplete="off" placeholder="请输入轮播图名称" />
                    </el-form-item>
                    <el-form-item label="目标地址">
                        <el-input v-model="loopForm.targetUrl" autocomplete="off" type="textarea" maxlength="128"
                            placeholder="请输入轮播图目标地址" />
                    </el-form-item>
                    <el-form-item label="图片地址">
                        <el-input v-model="loopForm.imageUrl" autocomplete="off" type="textarea" maxlength="128"
                            placeholder="请输入轮播图图片地址" />
                    </el-form-item>
                    <el-form-item label="排序顺序">
                        <el-input-number v-model="loopForm.order" :min="1" :max="10" align="center" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="success" @click="doEdit">确 定</el-button>
                    </span>
                </template>
            </el-dialog>
        </div>  </div>
</template>

<script>
import { showLoops, SUCCESS, deleteLoop, addLoop, editLoop } from "../../api/api"
import { showMessage } from "../../utils/utils"
import { Refresh, Delete, DocumentAdd } from '@element-plus/icons-vue'
import { markRaw } from 'vue'

export default {
    data() {
        return {
            refreshIcon: markRaw(Refresh),
            deleteIcon: markRaw(Delete),
            addIcon: markRaw(DocumentAdd),
            loops: [],
            deleteShow: false,
            addShow: false,
            editShow: false,
            deleteBatchShow: false,
            deleteMessage: '',
            deleteLoopId: '',
            loopForm: {
                title: '',
                targetUrl: '',
                imageUrl: '',
                order: 1
            },
            editLoopId: '',
            selectedLoops: [],
            deleteloopIds: '',
        }
    },
    methods: {
        handleSelectionChange(value) {
            this.selectedLoops = value
        },
        edit(item) {
            this.editLoopId = item.id
            this.loopForm.title = item.title
            this.loopForm.targetUrl = item.targetUrl
            this.loopForm.imageUrl = item.imageUrl
            this.loopForm.order = item.order
            this.editShow = true
        },
        doEdit() {
            if (this.checkLoopForm()) {
                editLoop(this.editLoopId, this.loopForm).then(res => {
                    if (res.code === SUCCESS) {
                        showMessage("轮播图更新成功")
                        this.getLoopList()
                    } else {
                        showMessage(res.message, 'error');
                    }
                    this.doCancel()
                    this.resetLoopForm()
                })
            }
            this.editLoopId = ''

        },
        free(item) {
            // 不是立马删除，而是弹出个dialog
            this.deleteMessage = item.title
            this.deleteLoopId = item.id
            this.deleteShow = true
        },
        doCancel() {
            this.deleteShow = false
            this.addShow = false
            this.editShow = false
            this.deleteBatchShow = false
        },
        doFree() {
            deleteLoop(this.deleteLoopId).then(res => {
                // 删除成功
                if (res.code === SUCCESS) {
                    showMessage("轮播图 " + this.deleteMessage + " 删除成功")
                    this.getLoopList()
                }
                this.doCancel()
                // 刷新表单和列表
                this.deleteLoopId = ''
            })
        },
        batchFree() {
            if (this.selectedLoops.length === 0) {
                showMessage("请选择删除的轮播图", "warning")
                return
            }
            this.deleteloopIds = ''
            this.selectedLoops.forEach((item) => {
                this.deleteloopIds += item.id + '_'
            })
            this.deleteBatchShow = true
        },
        doBatchFree() {
            deleteLoop(this.deleteloopIds).then(res => {
                if (res.code === SUCCESS) {
                    showMessage("批量删除轮播图成功")
                    this.getLoopList()
                } else {
                    showMessage(res.message, 'error')
                }
                // 重置
                this.doCancel()
                this.deleteloopIds = ''
            })
        },
        add() {
            this.resetLoopForm()
            this.addShow = true
        },
        doAdd() {
            if (this.checkLoopForm()) {
                addLoop(this.loopForm).then(res => {
                    // 成功就message一下,同时更新列表
                    if (res.code === SUCCESS) {
                        showMessage("轮播图添加成功")
                        this.getLoopList()
                    } else {
                        showMessage(res.message, 'error')
                    }
                    this.doCancel()
                    // 提交完了清空表单数据
                    this.resetLoopForm()
                })
            }


        },
        getLoopList() {
            const loading = this.$loading({
                lock: true,//lock的修改符--默认是false
                text: '轮播图获取中...',//显示在加载图标下方的加载文案
                target: document.querySelector('.el-main')//loadin覆盖的dom元素节点
            });
            showLoops().then(res => {
                loading.close()
                if (res.code == SUCCESS) {
                    this.loops = res.data
                } else {
                    showMessage("获取用户列表失败", 'error')
                }
            })
        },
        resetLoopForm() {
            this.loopForm.title = ''
            this.loopForm.targetUrl = ''
            this.loopForm.imageUrl = ''
            this.loopForm.order = 1
        },
        checkLoopForm() {
            if (this.loopForm.title === '') {
                showMessage("轮播图名称不可为空", 'error')
                return false
            }
            if (this.loopForm.targetUrl === '') {
                showMessage("轮播图目标地址不可为空", 'error')
                return false
            }
            if (this.loopForm.imageUrl === '') {
                showMessage("轮播图图片地址不可为空", 'error')
                return false
            }
            if (!(this.loopForm.targetUrl.startsWith("https://") || this.loopForm.targetUrl.startsWith("http://"))) {
                showMessage("轮播图目标地址格式错误", 'error')
                return false
            }
            if (!(this.loopForm.imageUrl.startsWith("https://") || this.loopForm.imageUrl.startsWith("http://"))) {
                showMessage("轮播图图片地址格式错误", 'error')
                return false
            }
            return true
        },
    },
    mounted() {
        this.getLoopList()
    }
}
</script>

<style>
.loop-action-bar {
    padding-bottom: 10px;
}

.loop-url {
    text-decoration: none;
}
</style>