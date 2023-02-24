<template>
    <div class="user-box">
        <el-card>
            <template #header>
                <div style="text-align: center; font-weight: 600; font-size: 18px">
                    <span>用户列表</span>
                </div>
            </template>
            <div class="user-action-bar">
                <!-- <el-button type="primary" @click="add" size="large">添加用户</el-button> -->
                <el-button type="success" @click="doReflush" :icon="refreshIcon">刷新用户</el-button>
                <el-button type="danger" @click="batchFree" :icon="deleteIcon">批量删除</el-button>
                <span class="user-action-bar-search">
                    <el-form :inline="true" :model="searchInfo">
                        <el-form-item label="用户名">
                            <el-input v-model="searchInfo.username" placeholder="请输入用户名" />
                        </el-form-item>
                        <el-form-item label="邮箱地址">
                            <el-input v-model="searchInfo.email" placeholder="请输入邮箱地址" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="doSearch" :icon="searchIcon">搜 索</el-button>
                            <el-button type="warning" @click="doClear" :icon="failedIcon">清 空</el-button>
                        </el-form-item>
                    </el-form>
                </span>
            </div>
            <div class="user-list-box">
                <el-table :data="users" style="width: 100%" @selection-change="handleSelectionChange">
                    <el-table-column type="selection" fixed />
                    <el-table-column prop="id" label="ID" align="center" width="175" />
                    <el-table-column prop="username" label="用户名" align="center" width="150" />
                    <el-table-column label="用户权限" align="center" width="110" :filters="
                    [
                        { text: '管理员', value: 'role_admin' },
                        { text: '普通用户', value: 'role_normal' }
                    ]" :filter-method="filterUserRole">
                        <template v-slot="scope">
                            <el-tag v-if="scope.row.roles === 'role_admin'" type="warning">管理员</el-tag>
                            <el-tag v-else>普通用户</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="email" label="邮箱" align="center" />
                    <el-table-column prop="sign" label="签名" align="center">
                        <template v-slot="scope">
                            <span v-if="scope.row.sign">{{ scope.row.sign }}</span>
                            <span v-else>这个人很懒，什么也没留下...</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="状态" align="center" width="80" sortable>
                        <template v-slot="scope">
                            <el-tag v-if="scope.row.state > 0" size="large" type="success">活跃</el-tag>
                            <el-tag v-else size="large" type="danger">注销</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="头像" align="center" width="70">
                        <template v-slot="scope">
                            <el-avatar shape="square" :src="scope.row.avatar" fit="fill" />
                        </template>
                    </el-table-column>
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
            <div class="page-navigation-box">
                <el-pagination center background layout="prev, pager, next, jumper" :current-page="pageInfo.curPage"
                    @current-change="onCurPageChange" :page-size="pageInfo.defSize" :total="pageInfo.totalSize"
                    :hide-on-single-page="true" />
            </div>
        </el-card>
        <!-- dialog -->
        <div class="user-dialog-box">
            <!-- 单个删除 -->
            <el-dialog v-model="deleteShow" title="删除提示" width="25%" draggable center>
                <span>您确认要删除用户: {{ deleteMessage }} 吗？</span>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="danger" @click="doFree">确 定</el-button>
                    </span>
                </template>
            </el-dialog>

            <!-- 批量删除 -->
            <el-dialog v-model="deleteBatchShow" title="批量删除" width="25%" draggable center>
                <span>您确认要批量删除用户吗？</span>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doCancel">取 消</el-button>
                        <el-button type="danger" @click="doBatchFree">确 定</el-button>
                    </span>
                </template>
            </el-dialog>

            <!-- 更改userInfo的dialog -->
            <el-dialog v-model="editShow" title="编辑用户信息" width="25%" draggable center>
                <el-form :model="userForm">
                    <el-form-item label="用户名称">
                        <el-input v-model="userForm.username" autocomplete="off" placeholder="请输入用户名" />
                    </el-form-item>
                    <el-form-item label="用户权限">
                        <el-switch v-model="userForm.roles" active-text="Admin" inactive-text="Normal" />
                    </el-form-item>
                    <el-form-item label="个性签名">
                        <el-input v-model="userForm.sign" autocomplete="off" type="textarea" maxlength="128"
                            placeholder="请输入用户个性签名" />
                    </el-form-item>
                    <el-form-item label="用户状态">
                        <el-select v-model="userForm.state" placeholder="Select">
                            <el-option v-for="item in userStates" :key="item.value" :label="item.label"
                                :value="item.value" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="头像地址">
                        <el-input v-model="userForm.avatar" autocomplete="off" placeholder="请输入用户头像地址" />
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
import { showUsers, SUCCESS, deleteUser, editUser } from "../../api/api"
import { showMessage } from "../../utils/utils"
import { Search, Failed, Refresh, Delete } from '@element-plus/icons-vue'
import { markRaw } from 'vue'

export default {
    data() {
        return {
            searchIcon: markRaw(Search),
            failedIcon: markRaw(Failed),
            refreshIcon: markRaw(Refresh),
            deleteIcon: markRaw(Delete),
            deleteShow: false,
            deleteBatchShow: false,
            editShow: false,
            deleteMessage: '',
            deleteUserId: '',
            searchInfo: {
                username: '',
                email: '',
            },
            pageInfo: {
                curPage: 1,
                defSize: 10,
                totalSize: 0
            },
            users: [],
            selectedUsers: [],
            deleteUserIds: '',
            userForm: {
                id: '',
                username: '',
                roles: false,
                sign: '',
                state: '',
                avatar: '',
            },
            userStates: [
                { value: "1", label: "活跃" },
                { value: "0", label: "注销" }
            ]
        }
    },
    methods: {
        filterUserRole(value, row) {
            // 筛选用户角色
            return row.roles === value
        },
        handleSelectionChange(value) {
            // 将选中的用户放入数组
            this.selectedUsers = value
        },
        batchFree() {
            // 批量删除用户
            if (this.selectedUsers.length === 0) {
                showMessage("请选择删除的用户", "warning")
                return
            }
            this.selectedUsers.forEach((item) => {
                this.deleteUserIds += item.id + '_'
            })
            this.deleteBatchShow = true
        },
        doBatchFree() {
            deleteUser(this.deleteUserIds).then(res => {
                if (res.code === SUCCESS) {
                    showMessage("批量删除用户成功!")
                    // 重新获取列表数据
                    this.getUserList()
                } else {
                    showMessage(res.message, 'error')
                }
                // 重置数据
                this.deleteUserIds = ''
                this.doCancel()
            })

        },
        edit(item) {
            this.userForm.id = item.id
            this.userForm.roles = item.roles === 'role_normal' ? false : true
            this.userForm.username = item.username
            this.userForm.sign = item.sign
            this.userForm.state = item.state
            this.userForm.avatar = item.avatar
            this.editShow = true
        },
        doEdit() {
            // 提交更新的时候，检查数据是否都填写了
            if (this.userForm.username === '') {
                showMessage("用户名不可为空!", 'error')
                return
            }
            if (this.userForm.avatar.trim() === '') {
                this.userForm.avatar = "https://cdn.jsdelivr.net/gh/Awoodsheep/pic/imgs/avatar.png"
            }
            this.userForm.roles = this.userForm.roles === true ? "role_admin" : "role_normal"
            editUser(this.userForm.id, this.userForm).then(res => {
                if (res.code === SUCCESS) {
                    showMessage("更新用户信息成功!")
                    this.getUserList()
                } else {
                    showMessage(res.message, 'error')
                }
                // 重置现场
                this.doCancel()
                this.userForm.id = ''
                this.userForm.roles = false
                this.userForm.username = ''
                this.userForm.sign = ''
                this.userForm.state = ''
                this.userForm.avatar = ''
            })
        },
        free(item) {
            this.deleteShow = true
            this.deleteMessage = item.username
            this.deleteUserId = item.id
        },
        getUserList() {
            const loading = this.$loading({
                lock: true,//lock的修改符--默认是false
                text: '用户列表获取中...',//显示在加载图标下方的加载文案
                target: document.querySelector('.el-main')//loadin覆盖的dom元素节点
            });
            showUsers(this.pageInfo.curPage, this.pageInfo.defSize).then(res => {
                loading.close()
                this.handleUserRes(res)
            })
        },
        doSearch() {
            const loading = this.$loading({
                lock: true,
                text: '用户列表获取中...',
                target: document.querySelector('.el-main')
            });
            showUsers(this.pageInfo.curPage, this.pageInfo.defSize, this.searchInfo.username, this.searchInfo.email)
                .then(res => {
                    loading.close()
                    this.handleUserRes(res)
                })
        },
        doClear() {
            this.searchInfo.username = ''
            this.searchInfo.email = ''
        },
        doReflush() {
            this.doClear()
            this.getUserList()
        },
        handleUserRes(res) {
            if (res.code === SUCCESS) {
                this.users = res.data.content
                // 设置页码
                this.pageInfo.curPage = res.data.number + 1
                this.pageInfo.totalSize = res.data.totalElements
            } else {
                showMessage("获取用户列表失败", 'error')
            }
        },
        onCurPageChange(page) {
            this.pageInfo.curPage = page
            this.getUserList()
        },
        doCancel() {
            this.deleteShow = false
            this.deleteBatchShow = false
            this.editShow = false
        },
        // 删除用户
        doFree() {
            deleteUser(this.deleteUserId).then(res => {
                if (res.code === SUCCESS) {
                    showMessage("删除用户 " + this.deleteMessage + " 成功!")
                    // 重新获取列表数据
                    this.getUserList()
                } else {
                    showMessage("删除用户 " + this.deleteMessage + " 失败!", 'error')
                }
                this.deleteMessage = ''
                this.deleteUserId = ''
                this.doCancel()
            })
        }

    },
    mounted() {
        this.getUserList()
    }
}
</script>

<style>
.user-action-bar {
    display: flex;
    height: 45px;
}

.user-action-bar .user-action-bar-search {
    margin-left: auto;
}

.user-action-bar .el-form {
    display: flex;
}

.page-navigation-box {
    padding-top: 15px;
}

.page-navigation-box .el-pagination {
    /* 居中 */
    /* justify-content: center; */
    float: right;
}
</style>