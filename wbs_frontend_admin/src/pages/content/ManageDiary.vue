<template>
    <div class="diary-box">
        <el-card>
            <template #header>
                <div style="text-align: center; font-weight: 600; font-size: 18px">
                    <span>日记管理</span>
                </div>
            </template>

            <div class="diary-action-bar">
                <el-button type="success" @click="getDiarys" :icon="refreshIcon">刷新日记</el-button>
                <el-button type="primary" @click="add" :icon="addIcon">写 日 记</el-button>
            </div>

            <div class="diary-content">
                <div>
                    <ul class="diary-ul">
                        <template v-for="(item, index) in diarys" :key="index">
                            <li class="diary-li">
                                <div class="diary-main">
                                    <div class="diary-avatar">
                                        <el-avatar :size="60" :src="item.user.avatar" />
                                    </div>

                                    <div class="diary-text">
                                        <h4 class="diary-text-time">{{ item.updateTime }} <span
                                                style="margin-left:5px;margin-right:5px;font-style:oblique;">by</span>
                                            <span style="color:cornflowerblue;font-size:16px;font-weight:555;">{{
                                                    item.user.username
                                            }}</span>
                                        </h4>
                                        <div class="diary-text-main">
                                            {{ item.content }}</div>
                                    </div>

                                    <div class="diary-actions">
                                        <el-button type="primary" @click="edit(item)" plain :round="true">编 辑
                                        </el-button>

                                        <el-popconfirm title="您确认要删除这篇日记吗?" @confirm="free(item)">
                                            <template #reference>
                                                <el-button type="danger" plain :round="true">删 除
                                                </el-button>
                                            </template>
                                        </el-popconfirm>

                                    </div>
                                </div>
                            </li>
                            <el-divider />
                        </template>

                    </ul>
                </div>

                <!-- 分页栏模块 -->
                <div class="diary-list-page-navigation-box">
                    <el-pagination center background layout="prev, pager, next, jumper" :current-page="pageInfo.curPage"
                        @current-change="onCurDiaryPageChange" :page-size="pageInfo.defSize" :total="pageInfo.totalSize"
                        :hide-on-single-page="false" />
                </div>

            </div>
        </el-card>

        <!-- dialog模块 -->
        <div>
            <el-dialog v-model="addOrEditShow" :title="state" width="30%" draggable>
                <el-input clearable show-word-limit maxlength="2048" v-model="diaryContent"
                    :autosize="{ minRows: 7, maxRows: 7 }" type="textarea" placeholder="请输入日记内容" />
                <template #footer>
                    <span class="dialog-footer">
                        <el-button :loading="doAddOrEditLoading" type="primary" @click="doAddOrEdit">确 定</el-button>
                    </span>
                </template>
            </el-dialog>
        </div>

    </div>
</template>

<script>
import { markRaw } from 'vue'
import { Refresh, Delete, DocumentAdd } from '@element-plus/icons-vue'
import { addDiary, deleteDiary, editDiary, showDiarys, SUCCESS } from '../../api/api'
import { showMessage, showNotify } from '../../utils/utils'


export default {
    data() {
        return {
            refreshIcon: markRaw(Refresh),
            deleteIcon: markRaw(Delete),
            addIcon: markRaw(DocumentAdd),
            editDiaryId: '',
            addOrEditShow: false,
            diaryContent: '',
            state: '',
            // 分页数据
            pageInfo: {
                curPage: 1,
                defSize: 10,
                totalSize: 0
            },
            diarys: {},
            doAddOrEditLoading: false,
        }
    },
    methods: {
        // 获取日记列表
        getDiarys() {
            const loading = this.$loading({
                lock: true,
                text: '日记列表获取中...',
                target: '.el-main',
            });
            showDiarys(this.pageInfo.curPage, this.pageInfo.defSize).then(res => {

                loading.close()
                if (res.code === SUCCESS) {
                    this.diarys = res.data.content
                    // 设置页码
                    this.pageInfo.curPage = res.data.number + 1
                    this.pageInfo.totalSize = res.data.totalElements

                } else {
                    showMessage("获取日记列表失败,请重试!", 'error')
                }
            })
        },
        // 切换页数监听
        onCurDiaryPageChange(page) {
            this.pageInfo.curPage = page
            this.getDiarys()
        },
        // 编辑
        edit(item) {
            this.editDiaryId = item.id
            this.state = '编辑日记'
            this.addOrEditShow = true
            this.diaryContent = item.content
        },
        // 添加
        add() {
            this.editDiaryId = ''
            this.state = '写 日 记'
            this.diaryContent = ''
            this.addOrEditShow = true
        },
        // 删除
        free(item) {
            deleteDiary(item.id).then(res => {
                if (res.code === SUCCESS) {
                    showNotify("删除日记", res.message)
                    this.getDiarys()
                } else {
                    showNotify("删除日记", "删除日记失败,请重试!", 'error')
                }
            })
        },
        // 确认编辑或者添加
        doAddOrEdit() {
            if (this.diaryContent === '') {
                showMessage("请输入日记内容!", 'warning')
                return
            }
            this.doAddOrEditLoading = true
            if (this.state === '编辑日记') {
                editDiary(this.editDiaryId, this.diaryContent).then(res => {
                    if (res.code === SUCCESS) {
                        showNotify("编辑日记", res.message)
                        this.getDiarys()
                    } else {
                        showNotify("编辑日记", "更新日记失败,请重试!", 'error')
                    }
                    this.doAddOrEditLoading = false
                    this.addOrEditShow = false
                })
            } else {
                addDiary(this.diaryContent).then(res => {
                    if (res.code === SUCCESS) {
                        showNotify("发表日记", res.message)
                        this.getDiarys()
                    } else {
                        showNotify("发表日记", "发表日记失败,请重试!", 'error')
                    }
                    this.doAddOrEditLoading = false
                    this.addOrEditShow = false
                })
            }
        }
    },
    mounted() {
        this.getDiarys()
    }
}
</script>

<style scoped>
.diary-ul {
    margin: 0;
    padding: 0;
    list-style: none;
    outline: none;
}

.diary-li {
    align-items: normal;
    display: flex;
    justify-content: space-between;
    padding: 12px 0;
    list-style: none;
}

.diary-main {
    display: flex;
    width: 100%;
}

.diary-avatar {
    margin-left: 10px;
    margin-right: 26px;
}

.diary-text {
    flex: 1 0;
    overflow-x: auto;
}

.diary-text-main {
    margin-top: 12px;
    color: rgba(0, 0, 0, .45);
    font-size: 14px;
    line-height: 22px;
    font-family: -apple-system, BlinkMacSystemFont, Segoe UI, PingFang SC, Hiragino Sans GB, Microsoft YaHei, Helvetica Neue, Helvetica, Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji, Segoe UI Symbol;
}

.diary-text-time {
    font-weight: 500;
    color: rgba(0, 0, 0, .85);
    font-size: 16px;
    line-height: 24px;
    font-family: -apple-system, BlinkMacSystemFont, Segoe UI, PingFang SC, Hiragino Sans GB, Microsoft YaHei, Helvetica Neue, Helvetica, Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji, Segoe UI Symbol;
}

.diary-content .el-divider {
    margin-top: 10px;
    margin-bottom: 10px;
}

.diary-action-bar {
    margin-bottom: 10px;
}

.diary-actions {
    margin-top: 30px;
    margin-right: auto;
}
</style>