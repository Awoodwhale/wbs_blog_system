<template>
    <div class="label-box">
        <el-card>
            <template #header>
                <div style="text-align: center; font-weight: 600; font-size: 18px">
                    <span>标签设置</span>
                </div>
            </template>
            <div class="label-action-bar">
                <el-button type="success" @click="getLabelList" :icon="refreshIcon">刷新标签</el-button>
                <el-button type="primary" @click="add" :icon="addIcon">添加标签</el-button>
                <el-button type="danger" @click="batchFree" :icon="deleteIcon">批量删除</el-button>
            </div>
            <div class="label-list-box">
                <el-table :data="labels" style="width: 100%" @selection-change="handleSelectionChange">
                    <el-table-column type="selection" fixed />
                    <el-table-column prop="id" label="ID" align="center" width="175" />
                    <el-table-column prop="name" label="标签名" align="center">
                        <template v-slot="scope">
                            <el-tag type="success">{{ scope.row.name }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="count" label="文章数量" align="center" />
                    <el-table-column prop="createTime" label="创建日期" align="center" sortable />
                    <el-table-column prop="updateTime" label="更新日期" align="center" sortable />
                    <el-table-column fixed="right" label="操作" align="center" width="180">
                        <template v-slot="scope">
                            <el-button type="primary" @click="edit(scope.row)" plain :round="true">编 辑</el-button>
                            <el-button type="danger" @click="free(scope.row)" plain :round="true">删 除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
        </el-card>
        <div class="label-dialog-box">
            <!-- 单个删除 -->
            <el-dialog v-model="deleteShow" title="删除提示" width="25%" draggable center>
                <span>您确认要删除标签: {{ deleteMessage }} 吗？</span>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="danger" @click="doFree">确 定</el-button>
                    </span>
                </template>
            </el-dialog>

            <!-- 批量删除 -->
            <el-dialog v-model="deleteBatchShow" title="批量删除" width="25%" draggable center>
                <span>您确认要批量删除标签吗？</span>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="danger" @click="doBatchFree">确 定</el-button>
                    </span>
                </template>
            </el-dialog>

            <!-- 编辑、添加标签 -->
            <el-dialog v-model="editShow" :title="editDialogTitle" width="25%" draggable center>
                <el-form :model="labelForm">
                    <el-form-item label="标签名称">
                        <el-input v-model="labelForm.name" autocomplete="off" placeholder="请输入标签名称" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="success" @click="doEdit" v-if="editDialogTitle === '编辑标签'">更 新</el-button>
                        <el-button type="success" @click="doAdd" v-else>确 定</el-button>
                    </span>
                </template>
            </el-dialog>
        </div>  </div>
</template>

<script>
import { showLabelList, SUCCESS, deleteLabel, addLabel, editLabel } from "../../api/api"
import { showMessage } from "../../utils/utils"
import { Refresh, Delete, DocumentAdd } from '@element-plus/icons-vue'
import { markRaw } from 'vue'

export default {
    data() {
        return {
            refreshIcon: markRaw(Refresh),
            deleteIcon: markRaw(Delete),
            addIcon: markRaw(DocumentAdd),
            labels: [],
            selectedLabels: [],
            deleteMessage: '',
            deleteLabelId: '',
            deleteLabelIds: '',
            deleteShow: false,
            deleteBatchShow: false,
            editShow: false,
            labelForm: {
                id: '',
                name: ''
            },
            editDialogTitle: '编辑标签',
        }
    },
    methods: {
        getLabelList() {
            const loading = this.$loading({
                lock: true,//lock的修改符--默认是false
                text: '分类获取中...',//显示在加载图标下方的加载文案
                target: document.querySelector('.el-main')//loadin覆盖的dom元素节点
            });
            showLabelList().then(res => {
                loading.close()
                if (res.code === SUCCESS) {
                    this.labels = res.data
                } else {
                    showMessage(res.message, 'error')
                }
            })
        },
        add() {
            this.resetLableForm()
            this.editShow = true
            this.editDialogTitle = '添加标签'
        },
        // 添加标签
        doAdd() {
            if (this.labelForm.name !== '') {
                addLabel(this.labelForm).then(res => {
                    if (res.code === SUCCESS) {
                        showMessage(res.message)
                        this.getLabelList()
                    } else {
                        showMessage(res.message, 'error')
                    }
                    this.doCancel()
                    this.resetLableForm()
                })
            } else {
                showMessage("标签名称不可为空!", 'error')
            }
        },
        edit(item) {
            this.resetLableForm()
            this.editShow = true
            this.editDialogTitle = '编辑标签'
            this.labelForm.id = item.id
            this.labelForm.name = item.name
        },
        // 更新
        doEdit() {
            if (this.checkLabelForm()) {
                editLabel(this.labelForm).then(res => {
                    if (res.code === SUCCESS) {
                        showMessage(res.message)
                        this.getLabelList()
                    } else {
                        showMessage(res.message, 'error')
                    }
                    this.doCancel()
                    this.resetLableForm()
                })
            }
        },
        // 判断表单
        checkLabelForm() {
            if (this.labelForm.id === '') {
                showMessage("标签Id不可为空!", 'error')
                return false
            }
            if (this.labelForm.name === '') {
                showMessage("标签名称不可为空!", 'error')
                return false
            }
            return true
        },
        batchFree() {
            if (this.selectedLabels.length === 0) {
                showMessage("请选择删除的分类", "warning")
                return
            }
            this.deleteLabelIds = ''
            this.selectedLabels.forEach((item) => {
                this.deleteLabelIds += item.id + '_'
            })
            this.deleteBatchShow = true
        },
        doBatchFree() {
            // 批量删除
            deleteLabel(this.deleteLabelIds).then(res => {
                if (res.code === SUCCESS) {
                    showMessage("批量删除标签成功");
                    this.getLabelList();
                } else {
                    showMessage(res.message, 'error')
                }
                // 重置
                this.doCancel();
                this.deleteLabelIds = ''
            })
        },
        free(item) {
            this.deleteMessage = item.name
            this.deleteLabelId = item.id
            this.deleteShow = true
        },
        // 删除标签
        doFree() {
            if (this.deleteLabelId !== '') {
                deleteLabel(this.deleteLabelId).then(res => {
                    if (res.code === SUCCESS) {
                        showMessage("标签 " + this.deleteMessage + " 删除成功")
                        this.getLabelList()
                    } else {
                        showMessage("标签 " + this.deleteMessage + " 删除失败", 'error')
                    }
                    this.doCancel()
                })
            }
        },
        doCancel() {
            this.deleteShow = false
            this.deleteBatchShow = false
            this.editShow = false
            this.deleteLabelIds = ''
            this.deleteLabelId = ''
            this.deleteMessage = ''
        },
        handleSelectionChange(value) {
            this.selectedLabels = value
        },
        resetLableForm() {
            this.labelForm.id = ''
            this.labelForm.name = ''
        },
    },
    mounted() {
        this.getLabelList()
    }
}
</script>

<style>
.label-action-bar {
    padding-bottom: 10px;
}
</style>