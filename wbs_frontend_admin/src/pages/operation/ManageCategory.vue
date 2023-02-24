<template>
    <div class="catagory-box">
        <el-card>
            <template #header>
                <div style="text-align: center; font-weight: 600; font-size: 18px">
                    <span>分类设置</span>
                </div>
            </template>
            <div class="category-action-bar">
                <el-button type="success" @click="getCategoryList" :icon="refreshIcon">刷新分类</el-button>
                <el-button type="primary" @click="add" :icon="addIcon">添加分类</el-button>
                <el-button type="danger" @click="batchFree" :icon="deleteIcon">批量删除</el-button>
            </div>
            <div class="category-list-box">
                <el-table :data="categories" style="width: 100%" @selection-change="handleSelectionChange">
                    <el-table-column type="selection" fixed />
                    <el-table-column prop="id" label="ID" align="center" width="175" />
                    <el-table-column prop="name" label="分类名" align="center" width="150">
                        <template v-slot="scope">
                            <el-tag>{{ scope.row.name }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="pinyin" label="拼音" align="center" width="130" />
                    <el-table-column prop="description" label="描述" align="center" />
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
        <div class="category-dialog-box">
            <!-- 批量删除 -->
            <el-dialog v-model="deleteBatchShow" title="批量删除" width="25%" draggable center>
                <span>您确认要批量删除分类吗？</span>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="danger" @click="doBatchFree">确 定</el-button>
                    </span>
                </template>
            </el-dialog>
            <!-- 单个删除 -->
            <el-dialog v-model="deleteShow" title="删除提示" width="25%" draggable center>
                <span>您确认要删除分类: {{ deleteMessage }} 吗？</span>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="danger" @click="doFree">确 定</el-button>
                    </span>
                </template>
            </el-dialog>
            <!-- 编辑分类 -->
            <el-dialog v-model="editShow" title="编辑分类" width="25%" draggable center>
                <el-form :model="categoryForm">
                    <el-form-item label="分类名称">
                        <el-input v-model="categoryForm.name" autocomplete="off" placeholder="请输入分类名称" />
                    </el-form-item>
                    <el-form-item label="分类拼音">
                        <el-input v-model="categoryForm.pinyin" autocomplete="off" placeholder="请输入分类拼音" />
                    </el-form-item>
                    <el-form-item label="分类描述">
                        <el-input v-model="categoryForm.description" autocomplete="off" type="textarea" maxlength="128"
                            placeholder="请输入分类描述" />
                    </el-form-item>
                    <el-form-item label="分类顺序">
                        <el-input-number v-model="categoryForm.order" :min="1" :max="10" align="center" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="success" @click="doEdit">更新</el-button>
                    </span>
                </template>
            </el-dialog>

            <!-- 添加分类 -->
            <el-dialog v-model="addShow" title="添加分类" width="25%" draggable center>
                <el-form :model="categoryForm">
                    <el-form-item label="分类名称">
                        <el-input v-model="categoryForm.name" autocomplete="off" placeholder="请输入分类名称" />
                    </el-form-item>
                    <el-form-item label="分类拼音">
                        <el-input v-model="categoryForm.pinyin" autocomplete="off" placeholder="请输入分类拼音" />
                    </el-form-item>
                    <el-form-item label="分类描述">
                        <el-input v-model="categoryForm.description" autocomplete="off" type="textarea" maxlength="128"
                            placeholder="请输入分类描述" />
                    </el-form-item>
                    <el-form-item label="排序顺序">
                        <el-input-number v-model="categoryForm.order" :min="1" :max="10" align="center" />
                    </el-form-item>
                </el-form>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="success" @click="doAdd">确 定</el-button>
                    </span>
                </template>
            </el-dialog>
        </div>  </div>
</template>

<script>
import {
    showCategories,
    deleteCategory,
    addCategory,
    editCategory,
    SUCCESS,
} from "../../api/api";
import { showMessage } from "../../utils/utils"
import { Refresh, Delete, DocumentAdd } from '@element-plus/icons-vue'
import { markRaw } from 'vue'

export default {
    data() {
        return {
            refreshIcon: markRaw(Refresh),
            deleteIcon: markRaw(Delete),
            addIcon: markRaw(DocumentAdd),
            loading: false,
            categories: [],
            deleteShow: false,
            deleteBatchShow: false,
            deleteMessage: "",
            deleteCatagoryId: "",
            editShow: false,
            addShow: false,
            categoryForm: {
                name: "",
                pinyin: "",
                description: "",
                order: 1,
            },
            editCategoryId: "",
            selectedCategories: [],
            deleteCategoryIds: '',
        };
    },
    methods: {
        handleSelectionChange(value) {
            // 将选中的用户放入数组
            this.selectedCategories = value
        },
        edit(item) {
            // 编辑前先把内容获取
            this.editCategoryId = item.id
            this.categoryForm.name = item.name
            this.categoryForm.pinyin = item.pinyin
            this.categoryForm.description = item.description
            this.categoryForm.order = item.order
            this.editShow = true
        },
        free(item) {
            // 不是立马删除，而是弹出一个dialog
            this.deleteMessage = item.name;
            this.deleteCatagoryId = item.id;
            this.deleteShow = true;
        },
        batchFree() {
            if (this.selectedCategories.length === 0) {
                showMessage("请选择删除的分类", "warning")
                return
            }
            this.deleteCategoryIds = ''
            this.selectedCategories.forEach((item) => {
                this.deleteCategoryIds += item.id + '_'
            })
            this.deleteBatchShow = true
        },
        add() {
            // 添加前，先把表单中的数据清除
            this.resetCategoryForm()
            this.addShow = true
        },
        doCancel() {
            this.deleteShow = false
            this.editShow = false
            this.addShow = false
            this.deleteBatchShow = false
            this.deleteCategoryIds = ''
        },
        doFree() {
            deleteCategory(this.deleteCatagoryId).then((res) => {
                if (res.code === SUCCESS) {
                    showMessage("分类 " + this.deleteMessage + " 删除成功")
                    this.getCategoryList()
                } else {
                    showMessage(res.message, 'error')
                }
                // 刷新表单和列表
                this.doCancel();
                this.deleteCatagoryId = ''
            });
        },
        doBatchFree() {
            // 批量删除
            deleteCategory(this.deleteCategoryIds).then(res => {
                if (res.code === SUCCESS) {
                    showMessage("批量删除分类成功");
                    this.getCategoryList();
                } else {
                    showMessage(res.message, 'error')
                }
                // 重置
                this.doCancel();
                this.deleteCategoryIds = ''
            })
        },
        doEdit() {
            if (this.checkCategoryForm()) {
                // 更新数据
                editCategory(this.editCategoryId, this.categoryForm).then(res => {
                    if (res.code === SUCCESS) {
                        showMessage("分类更新成功");
                        this.getCategoryList();
                    } else {
                        showMessage(res.message, 'error');
                    }
                    this.doCancel()
                    this.resetCategoryForm()
                })
            }
            this.editCategoryId = ''
        },
        doAdd() {
            if (this.checkCategoryForm()) {
                // 提交数据
                addCategory(this.categoryForm).then((res) => {
                    if (res.code === SUCCESS) {
                        showMessage("添加分类成功");
                        this.getCategoryList();
                    } else {
                        showMessage(res.message, 'error');
                    }
                    this.doCancel();
                    // 提交完了清空表单数据
                    this.resetCategoryForm()
                });
            }

        },
        checkCategoryForm() {
            // 检查内容
            if (this.categoryForm.name === "") {
                showMessage("分类名称不可为空", 'error');
                return false;
            }
            if (this.categoryForm.pinyin === "") {
                showMessage("分类拼音不可为空", 'error');
                return false;
            }
            if (this.categoryForm.description === "") {
                showMessage("分类描述不可为空", 'error');
                return false;
            }
            return true;
        },
        resetCategoryForm() {
            this.categoryForm.name = ''
            this.categoryForm.pinyin = ''
            this.categoryForm.description = ''
            this.categoryForm.order = 1
        },
        getCategoryList() {
            const loading = this.$loading({
                lock: true,
                text: '分类获取中...',
                target: document.querySelector('.el-main')
            });
            showCategories().then((res) => {
                loading.close()
                if (res.code === SUCCESS) {
                    this.categories = res.data;
                } else {
                    showMessage("获取用户列表失败", 'error')
                }
            });
        },
    },
    mounted() {
        this.getCategoryList();
    },
};
</script>

<style>
.category-action-bar {
    padding-bottom: 10px;
}
</style>
