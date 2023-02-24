<template>
    <div class="comment-box">
        <el-card>
            <template #header>
                <div style="text-align: center; font-weight: 600; font-size: 18px">
                    <span>评论管理</span>
                </div>
            </template>

            <div class="comment-action-bar">
                <el-button type="success" @click="getComments" :icon="refreshIcon">刷新评论</el-button>

            </div>

            <div class="comment-list-box">
                <el-table :data="comments" style="width: 100%">
                    <el-table-column prop="id" label="ID" align="center" width="175" fixed />
                    <el-table-column prop="username" label="昵称" align="center" width="175">
                        <template v-slot="scope">
                            <span style="color: #409eff">{{ scope.row.username }}</span>
                        </template>
                    </el-table-column>

                    <el-table-column prop="state" label="状态" align="center" width="90" :filters="
                    [
                        { text: '已发布', value: '1' },
                        { text: '回收站', value: '0' },
                        { text: '置顶中', value: '2' },
                    ]" :filter-method="filterCommentState">
                        <template v-slot="scope">
                            <el-tag effect="plain" :type="commentStates[scope.row.state].type">
                                {{ commentStates[scope.row.state].text }}</el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column prop="content" label="内容" align="center">
                        <template v-slot="scope">
                            <span style="color: teal;font-size:14px;">{{ fun(scope.row.content) }}</span>
                        </template>
                    </el-table-column>

                    <el-table-column label="评论文章" align="center">
                        <template v-slot="scope">
                            <span style="color: #409eff">{{ commentArticles[scope.row.articleId] }}</span>
                        </template>
                    </el-table-column>

                    <el-table-column prop="createTime" label="创建日期" align="center" width="170" sortable />

                    <el-table-column fixed="right" label="操作" align="center" width="180">
                        <template v-slot="scope">
                            <el-button type="primary" @click="reply(scope.row)" plain :round="true"
                                v-if="scope.row.state !== '0'">回 复</el-button>
                            <el-button type="success" @click="recover(scope.row)" plain :round="true" v-else>恢 复
                            </el-button>
                            <el-button type="danger" @click="free(scope.row)" plain :round="true"
                                v-if="scope.row.state !== '0'">回 收</el-button>
                            <el-button type="danger" @click="fullFree(scope.row)" plain :round="true" v-else>删 除
                            </el-button>
                        </template>
                    </el-table-column>

                </el-table>

                <!-- 分页栏模块 -->
                <div class="comment-list-page-navigation-box">
                    <el-pagination center background layout="prev, pager, next, jumper" :current-page="pageInfo.curPage"
                        @current-change="onCurCommentPageChange" :page-size="pageInfo.defSize"
                        :total="pageInfo.totalSize" :hide-on-single-page="false" />
                </div>

            </div>
        </el-card>

        <!-- 回复功能的dialog -->
        <div class="reply-dialog-box">
            <el-dialog v-model="replyShow" :title="'回复给: ' + replyUser" width="30%" draggable>
                <el-input clearable show-word-limit maxlength="1024" v-model="replyComment.content"
                    :autosize="{ minRows: 5, maxRows: 5 }" type="textarea" placeholder="请输入回复内容" />
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="doReply">回 复</el-button>
                    </span>
                </template>
            </el-dialog>
        </div>
    </div>
</template>

<script>
import { Refresh, Delete, DocumentAdd } from '@element-plus/icons-vue'
import { markRaw, h } from 'vue'
import { addComment, deleteComment, editComment, showArticleTitle, showComments, SUCCESS } from '../../api/api'
import { showMessage, showMessageBox, showNotify, entitiestoUtf16 } from '../../utils/utils'

export default {
    data() {
        return {
            refreshIcon: markRaw(Refresh),
            deleteIcon: markRaw(Delete),
            addIcon: markRaw(DocumentAdd),
            // 回复功能需要使用的变量
            replyShow: false,
            replyUser: '',
            replyComment: {
                content: '',
                articleId: '',
                parentId: '',
            },
            // 评论的三个状态
            commentStates: {
                '0': {
                    text: '回收站',
                    type: 'danger'
                },
                "1": {
                    text: '已发布',
                    type: ''
                },
                '2': {
                    text: '置顶中',
                    type: 'warning'
                },
            },
            hasArticle: false,
            // 分页数据
            pageInfo: {
                curPage: 1,
                defSize: 10,
                totalSize: 0
            },
            comments: [],
            commentArticles: [],
            fun: entitiestoUtf16,   // 评论解码
        }
    },
    methods: {
        // 获取评论列表
        getComments() {
            const loading = this.$loading({
                lock: true,
                text: '评论列表获取中...',
                target: '.el-main',
            });
            showComments(this.pageInfo.curPage, this.pageInfo.defSize).then(res => {
                if (res.code === SUCCESS) {
                    this.comments = res.data.content
                    // 设置页码
                    this.pageInfo.curPage = res.data.number + 1
                    this.pageInfo.totalSize = res.data.totalElements
                    // 只请求一次文章数据
                    if (!this.hasArticle) {
                        let ids = ''
                        for (let i = 0; i < this.comments.length; i++) {
                            ids += this.comments[i].articleId + '_'
                        }
                        if (ids !== '') {
                            showArticleTitle(ids).then(titleRes => {
                                loading.close()
                                if (titleRes.code === SUCCESS) {
                                    this.commentArticles = titleRes.data
                                    this.hasArticle = true
                                } else {
                                    showMessage("获取评论文章失败,请重试!", 'error')
                                }
                            })
                        } else {
                            loading.close()
                        }
                    }
                    else {
                        loading.close()
                    }
                } else {
                    showMessage("获取评论列表失败,请重试!", 'error')
                }
            })
        },
        // 评论状态过滤
        filterCommentState(value, row) {
            return row.state === value
        },
        // 回复评论
        reply(item) {

            // 处理数据
            this.replyComment.content = ''
            this.replyComment.articleId = item.articleId
            this.replyComment.parentId = item.id
            this.replyShow = true
            this.replyUser = item.username
        },
        // 进行回复处理
        doReply() {
            if (this.replyComment.content === '') {
                showMessage("回复内容不可为空!", 'error')
                return
            }
            // 回复评论
            addComment(this.replyComment).then(res => {
                this.replyShow = false
                if (res.code === SUCCESS) {
                    showNotify("回复评论", "回复评论成功!")
                    this.getComments()
                } else {
                    showNotify("回复评论", res.message, 'error')
                }
            })
        },
        // 回收评论
        free(item) {
            showMessageBox("确认 回收 id为 " + item.id + " 的评论吗?", "回收评论").then(() => {
                editComment(item.id, 'remove').then(res => {
                    if (res.code === SUCCESS) {
                        showNotify("回收评论", res.message)
                    } else {
                        showMessage("回收评论", res.message, 'error')
                    }
                    this.getComments()
                })
            }).catch(() => { })
        },
        // 恢复评论
        recover(item) {
            showMessageBox("确认 恢复 id为 " + item.id + " 的评论吗?", "恢复评论").then(() => {
                editComment(item.id, 'recover').then(res => {
                    if (res.code === SUCCESS) {
                        showNotify("恢复评论", res.message)
                    } else {
                        showMessage("恢复评论", res.message, 'error')
                    }
                    this.getComments()
                })
            }).catch(() => { })
        },
        // 彻底删除评论
        fullFree(item) {
            showMessageBox(h('div', null, [
                h('div', { style: 'text-align: center;' }, '确认 删除 id为 ' + item.id + ' 的评论吗?'),
                h('div', { style: 'color: red; text-align: center;' }, '该操作不可逆!'),
            ]), "删除评论").then(() => {
                deleteComment(item.id).then(res => {
                    if (res.code === SUCCESS) {
                        showNotify("删除评论", res.message)
                    } else {
                        showNotify("删除评论", res.message, 'error')
                    }
                    this.getComments()
                })
            }).catch(() => { })
        },
        // 分页切换监听
        onCurCommentPageChange(page) {
            this.pageInfo.curPage = page
            this.getComments()
        },
    },
    mounted() {
        this.getComments()
    }
}
</script>

<style scoped>
.comment-list-page-navigation-box {
    margin-top: 15px;
}
</style>