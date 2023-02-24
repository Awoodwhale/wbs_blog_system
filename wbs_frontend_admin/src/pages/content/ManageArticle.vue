<template>
    <div class="article-manage-box">

        <el-tabs type="border-card" v-model="activeName" @tab-click="tabChange">
            <!-- 已发布文章模块 -->
            <el-tab-pane label="已发布" name="posted">
                <!-- 已发布文章的action bar -->
                <div class="article-posted-action-bar" style="display:flex;">
                    <el-button type="success" @click="getPostedArticles" :icon="refreshIcon">刷新文章
                    </el-button>
                    <el-button type="primary" @click="add" :icon="addIcon">写文章</el-button>
                    <span class="article-posted-search-bar" style="margin-left:auto;">
                        <el-form :inline="true" :model="searchInfo">
                            <el-form-item label="标题" style="margin-right:6px;">
                                <el-input v-model="searchInfo.keyword" placeholder="请输入文章标题" clearable />
                            </el-form-item>
                            <el-form-item label="状态" style="margin-right:6px;">
                                <el-select v-model="searchInfo.state" placeholder="请选择文章状态" clearable>
                                    <el-option v-for="item in states" :key="item.state" :label="item.name"
                                        :value="item.state" />
                                </el-select>
                            </el-form-item>
                            <el-form-item label="分类" style="margin-right:6px;">
                                <el-select v-model="searchInfo.categoryId" placeholder="请选择文章分类" clearable>
                                    <el-option v-for="item in categories" :key="item.id" :label="item.name"
                                        :value="item.id" />
                                </el-select>
                            </el-form-item>
                            <el-form-item style="margin-right:0;">
                                <el-button type="primary" @click="doSearch" :icon="searchIcon">搜 索</el-button>
                                <el-button type="warning" @click="doClear" :icon="failedIcon" v-if="!isSearching">清 空
                                </el-button>
                                <el-button type="warning" @click="doClear" :icon="failedIcon" v-else>重 置</el-button>
                            </el-form-item>
                        </el-form>
                    </span>
                </div>

                <!-- 表格模块 -->
                <div class="article-posted-list-content">
                    <el-table :data="postedArticles" style="width: 100%">
                        <el-table-column label="标题" align="center">
                            <template v-slot="scope">
                                <!-- 限制20个字数的标题 -->
                                <div v-if="scope.row.state !== '0'">
                                    <span style="font-size:16px; color:#409eff;" v-if="scope.row.title.length <= 20"> {{
                                            scope.row.title
                                    }}</span>
                                    <span style="font-size:16px; color:#409eff;" v-else> {{
                                            scope.row.title.substring(0, 20)
                                    }}...</span>
                                </div>
                                <div v-else>
                                    <span style="font-size:16px; color:#bfbfbf;" v-if="scope.row.title.length <= 20"> {{
                                            scope.row.title
                                    }}</span>
                                    <span style="font-size:16px; color:#bfbfbf;" v-else> {{
                                            scope.row.title.substring(0, 20)
                                    }}...</span>
                                </div>
                            </template>
                        </el-table-column>

                        <el-table-column label="状态" align="center" width="85" :filters="
                        [
                            { text: '已发布', value: '1' },
                            { text: '回收站', value: '0' },
                            { text: '置顶中', value: '2' },
                        ]" :filter-method="filterPostedArticleState">
                            <template v-slot="scope">
                                <el-tag v-if="scope.row.state === '1'" type="success" effect="light" round>已发布</el-tag>
                                <el-tag v-else-if="scope.row.state === '0'" type="danger" effect="light" round>回收站
                                </el-tag>
                                <el-tag v-else-if="scope.row.state === '2'" type="warning" effect="light" round>置顶中
                                </el-tag>
                            </template>
                        </el-table-column>

                        <el-table-column label="分类" align="center">
                            <template v-slot="scope">
                                <el-tag style="font-size:14px" effect="plain">
                                    {{ categoryName[scope.row.categoryId] }}
                                </el-tag>
                            </template>
                        </el-table-column>

                        <el-table-column label="标签" align="center">
                            <template v-slot="scope">
                                <el-tag v-for="(item, index) in scope.row.labelTags" style="font-size:14px;margin:4px;"
                                    effect="plain" type="warning" :key="index">
                                    {{ item }}
                                </el-tag>
                            </template>
                        </el-table-column>

                        <el-table-column label="作者" align="center">
                            <template v-slot="scope">
                                <span id="username-span">
                                    {{ scope.row.user.username }}
                                </span>
                            </template>
                        </el-table-column>

                        <el-table-column label="浏览" align="center" sortable prop="viewCount" width="80" />

                        <el-table-column prop="createTime" label="创建日期" align="center" width="170" sortable />

                        <el-table-column fixed="right" label="操作" align="center" width="250">
                            <template v-slot="scope">
                                <el-button type="warning" @click="edit(scope.row)" round plain
                                    v-if="scope.row.state === '1' || scope.row.state === '2'">编 辑</el-button>
                                <el-button type="success" @click="recover(scope.row)" round plain
                                    v-else-if="scope.row.state === '0'">恢 复</el-button>

                                <el-button type="danger" @click="free(scope.row)" round plain
                                    v-if="scope.row.state === '1' || scope.row.state === '2'">回 收</el-button>
                                <el-button type="danger" @click="fullFree(scope.row)" round plain
                                    v-else-if="scope.row.state === '0'">删 除</el-button>

                                <el-button type="primary" round plain @click="setArticle(scope.row)">设 置</el-button>
                            </template>
                        </el-table-column>


                    </el-table>

                    <!-- 分页栏模块 -->
                    <div class="posted-article-list-page-navigation-box">
                        <el-pagination center background layout="prev, pager, next, jumper"
                            :current-page="pageInfo.curPage" @current-change="onPostedArticleCurPageChange"
                            :page-size="pageInfo.defSize" :total="pageInfo.totalSize" :hide-on-single-page="false" />
                    </div>

                </div>

            </el-tab-pane>

            <!-- 草稿箱模块 -->
            <el-tab-pane label="草稿箱" name="sketch">
                <!-- 草稿的action bar -->
                <div class="article-sketch-action-bar" style="display:flex;">
                    <el-button type="success" @click="getSketchArticles" :icon="refreshIcon">刷新草稿
                    </el-button>
                    <el-button type="primary" @click="add" :icon="addIcon">写草稿</el-button>
                </div>
                <!-- 表格模块 -->
                <div class="article-sketch-list-content">
                    <el-table :data="sketchArticles" style="width: 100%">
                        <el-table-column label="标题" align="center">
                            <template v-slot="scope">
                                <div style="font-size:16px; color:#409eff;">
                                    {{ scope.row.title }}
                                </div>

                            </template>
                        </el-table-column>

                        <el-table-column label="状态" align="center" width="85">
                            <el-tag type="info" effect="dark" round>草稿箱</el-tag>
                        </el-table-column>

                        <el-table-column label="分类" align="center">
                            <template v-slot="scope">
                                <el-tag style="font-size:14px" effect="plain" v-if="scope.row.categoryId">
                                    {{ categoryName[scope.row.categoryId] }}
                                </el-tag>
                                <span v-else>暂无分类</span>
                            </template>
                        </el-table-column>

                        <el-table-column label="标签" align="center">
                            <template v-slot="scope">
                                <span v-if="scope.row.labels">
                                    <el-tag v-for="(item, index) in scope.row.labelTags"
                                        style="font-size:14px;margin:4px;" effect="plain" type="warning" :key="index">
                                        {{ item }}
                                    </el-tag>
                                </span>
                                <span v-else>暂无标签</span>
                            </template>
                        </el-table-column>

                        <el-table-column prop="createTime" label="创建日期" align="center" width="170" sortable />
                        <el-table-column prop="updateTime" label="更新日期" align="center" width="170" sortable />

                        <el-table-column fixed="right" label="操作" align="center" width="250">
                            <template v-slot="scope">
                                <el-button type="primary" @click="edit(scope.row)" round plain>编 辑</el-button>
                                <el-button type="danger" @click="fullFree(scope.row)" round plain>删 除</el-button>
                            </template>
                        </el-table-column>

                    </el-table>

                    <!-- 分页栏模块 -->
                    <div class="posted-article-list-page-navigation-box">
                        <el-pagination center background layout="prev, pager, next, jumper"
                            :current-page="sketchPageInfo.curPage" @current-change="onCurSketchPageChange"
                            :page-size="sketchPageInfo.defSize" :total="sketchPageInfo.totalSize"
                            :hide-on-single-page="false" />
                    </div>

                </div>

            </el-tab-pane>

        </el-tabs>

        <!-- 发布文章的抽屉信息 -->
        <div class="article-edit-drawer-or-dialog-box">
            <div class="article-setting-drawer">
                <el-drawer v-model="drawerShow" title="文章设置">
                    <el-scrollbar>
                        <div style="padding:0px 24px;">
                            <div style="margin-bottom:20px">基本设置</div>
                            <el-form :model="article" label-position="top">
                                <el-form-item label="发布日期">
                                    <el-date-picker value-format="YYYY-MM-DD HH:mm:ss" v-model="article.createTime"
                                        type="datetime" placeholder="请选择发布时间"
                                        style="width: 100%; max-width: 10000px;" />
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
                                            <el-image
                                                v-if="article.cover !== '' && article.cover.indexOf('image') !== -1"
                                                style="width: 100%; height: 220px;" :src="article.cover"
                                                fit="scale-down" />
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
                            <el-button type="primary" @click="doSaveArticleSetting()">保 存</el-button>
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
                        <el-pagination center background layout="prev, pager, next, jumper"
                            :current-page="imgPageInfo.curPage" @current-change="onCurImagePageChange"
                            :page-size="imgPageInfo.defSize" :total="imgPageInfo.totalSize"
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

            <!-- 单个删除的dialog -->
            <el-dialog v-model="editStateDialogShow" :title="operation + '文章'" width="25%" draggable center>
                <span>您确认要 {{ operation }} 文章: <span style="color:#409eff">{{ dialogMessage }}</span> 吗？</span>
                <span v-if="operation === '数据库中删除' || operation === '删除草稿'" style="color:red;">该操作不可逆!</span>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button type="primary" @click="editStateDialogShow = false">取 消</el-button>
                        <!-- 确认回收 -->
                        <el-button v-if="operation === '回收'" type="danger" @click="doFreePostedArticle">确 定</el-button>
                        <!-- 确认恢复 -->
                        <el-button v-else-if="operation === '恢复'" type="danger" @click="doRecoverRemovedArticle">确 定
                        </el-button>
                        <!-- 确认数据库中删除 -->
                        <el-button v-else-if="operation === '数据库中删除' || operation === '删除草稿'" type="danger"
                            @click="doFullFreeFreedArticle">确 定
                        </el-button>
                    </span>
                </template>
            </el-dialog>
        </div>

    </div>
</template>

<script>
import { Refresh, Delete, DocumentAdd, Search, Failed, Plus } from '@element-plus/icons-vue'
import { markRaw } from 'vue'
import { showArticles, showCategories, SUCCESS, editArticleState, deleteArticle, showLabelList, showImages, editPostedArticle, showSketchs, deleteSketchArticle } from '../../api/api'
import { showMessage, showNotify } from '../../utils/utils'

export default {
    data() {
        return {
            failedIcon: markRaw(Failed),
            refreshIcon: markRaw(Refresh),
            deleteIcon: markRaw(Delete),
            addIcon: markRaw(DocumentAdd),
            searchIcon: markRaw(Search),
            plusIcon: markRaw(Plus),
            editStateDialogShow: false,
            selectCoverImgShow: false,
            drawerShow: false,
            operation: "",  // 操作名称
            dialogMessage: '',  // 删除的文章标题
            editingArticleId: '',  // 删除的文章id
            activeName: 'posted',
            searchInfo: {
                categoryId: '',
                keyword: '',
                state: '',
            },
            article: {
                id: '',
                title: "",
                categoryId: "",
                createTime: '',
                state: '1',
                labels: '',
                summary: '',
                cover: '',  // 封面图片
                labelTags: [],
            },
            labels: '',
            states: [{ name: '回收站', state: '0' }, { name: "已发布", state: '1' }, { name: '置顶中', state: '2' }],  // 文章状态
            // 已发布文章
            postedArticles: [],
            // 所有草稿
            sketchArticles: [],
            // 所有分类
            categories: [],
            // 所有分类名称,键值对
            categoryName: {},
            // 已发布文章分页数据
            pageInfo: {
                curPage: 1,
                defSize: 10,
                totalSize: 0
            },
            // 草稿文章分页数据
            sketchPageInfo: {
                curPage: 1,
                defSize: 10,
                totalSize: 0
            },
            // 图片分页数据
            imgPageInfo: {
                curPage: 1,
                defSize: 10,
                totalSize: 0
            },
            // 是否处于搜索状态
            isSearching: false,
            images: [], // 图片信息
            isLoadedSketch: false,  // 是否打开过草稿箱
        }
    },
    methods: {
        // 标签页选择更改
        tabChange(curProp) {
            if (curProp.props.name === 'sketch') {
                // 仅仅加载一次草稿，就是第一次打开的时刻
                if (!this.isLoadedSketch) {
                    this.getSketchArticles()
                    this.isLoadedSketch = true
                }
            }
        },
        // 标签选择改变的触发函数
        onLabelSelectChange() {
            let tags = ''
            for (let i = 0; i < this.article.labelTags.length; i++) {
                tags += this.article.labelTags[i] + ","
            }
            tags = tags.substring(0, tags.length - 1)
            this.article.labels = tags
        },
        // 写文章，直接跳转到“发布文章”页面
        add() {
            this.$router.push("/content/post-article")
        },
        // 编辑文章
        edit(item) {
            if (this.activeName === 'posted') {
                // 编辑文章就是用路由跳转到 发送文章的页面，需要携带信息
                this.$router.push({
                    path: '/content/post-article',
                    query: {
                        state: 'doEdit',
                        articleId: item.id,
                    }
                })
            } else if (this.activeName === 'sketch') {
                // 如果是草稿，就去编辑草稿
                this.$router.push({
                    path: '/content/post-article',
                    query: {
                        state: 'doSketch',
                        articleId: item.id,
                    }
                })
            }

        },
        // 恢复文章
        recover(item) {
            this.operation = '恢复'
            this.dialogMessage = item.title
            this.editingArticleId = item.id
            this.editStateDialogShow = true
        },
        // 确认恢复
        doRecoverRemovedArticle() {
            editArticleState(this.editingArticleId, 'recover').then(res => {
                if (res.code === SUCCESS) {
                    showNotify("恢复文章", res.message)
                } else {
                    showNotify("恢复文章", "恢复文章失败,请重试!", 'error')
                }
                this.getPostedArticles()
                this.editingArticleId = ''
                this.editStateDialogShow = false
            })
        },
        // 彻底删除文章
        fullFree(item) {
            if (this.activeName === 'posted') {
                this.operation = '数据库中删除'
            } else if (this.activeName === 'sketch') {
                this.operation = '删除草稿'
            }
            this.dialogMessage = item.title
            this.editingArticleId = item.id
            this.editStateDialogShow = true
        },
        // 确认完全删除文章
        doFullFreeFreedArticle() {
            if (this.activeName === 'posted') {
                deleteArticle(this.editingArticleId).then(res => {
                    if (res.code === SUCCESS) {
                        showNotify("删除文章", res.message)
                    } else {
                        showNotify("删除文章", "删除文章失败,请重试!", 'error')
                    }
                    this.getPostedArticles()
                    this.editingArticleId = ''
                    this.editStateDialogShow = false
                })
            } else if (this.activeName === 'sketch') {
                deleteSketchArticle(this.editingArticleId).then(res => {
                    if (res.code === SUCCESS) {
                        showNotify("删除草稿", res.message)
                    } else {
                        showNotify("删除草稿", "删除草稿失败,请重试!", 'error')
                    }
                    this.getSketchArticles()
                    this.editingArticleId = ''
                    this.editStateDialogShow = false
                })
            }

        },
        // 回收文章
        free(item) {
            this.operation = '回收'
            this.dialogMessage = item.title
            this.editingArticleId = item.id
            this.editStateDialogShow = true
        },
        // 确认删除文章
        doFreePostedArticle() {
            editArticleState(this.editingArticleId, 'remove').then(res => {
                if (res.code === SUCCESS) {
                    showNotify("回收文章", res.message)
                } else {
                    showNotify("回收文章", "回收文章失败,请重试!", 'error')
                }
                this.getPostedArticles()
                this.editingArticleId = ''
                this.editStateDialogShow = false
            })
        },
        // 获取已发布文章
        getPostedArticles() {
            const loading = this.$loading({
                lock: true,
                text: '文章列表获取中...',
                target: '.el-tabs__content',
            });
            showArticles(this.pageInfo.curPage, this.pageInfo.defSize).then(res => {
                this.handlePostedArticleRes(res, loading)
            })
        },
        // 处理已发布文章的信息
        handlePostedArticleRes(res, loading) {
            if (res.code === SUCCESS) {
                this.postedArticles = res.data.content
                // 设置页码
                this.pageInfo.curPage = res.data.number + 1
                this.pageInfo.totalSize = res.data.totalElements
                // 获取分类
                if (this.categories.length === 0) {
                    showCategories().then(catRes => {
                        if (catRes.code === SUCCESS) {
                            this.categories = catRes.data
                            this.categories.forEach((value) => {
                                this.categoryName[value.id] = value.name
                            })
                        } else {
                            showMessage("获取文章分类失败,请重试!", 'error')
                        }
                        loading.close()
                    })
                } else {
                    loading.close()
                }

            } else {
                loading.close()
                showMessage("获取文章列表失败,请重试!", 'error')
            }
        },
        // 已发布文章列表分页
        onPostedArticleCurPageChange(page) {
            this.pageInfo.curPage = page
            this.getPostedArticles()
        },

        // 处理发布文章的状态选择
        filterPostedArticleState(value, row) {
            return row.state === value
        },
        // 搜索已发布文章
        doSearch() {
            // 如果搜索内容没有设置，那么就提示筛选
            if (this.searchInfo.categoryId === '' && this.searchInfo.keyword === '' && this.searchInfo.state === '') {
                showMessage("请筛选文章搜索条件!", 'warning')
                return
            }
            const loading = this.$loading({
                lock: true,
                text: '筛选文章搜索中...',
                target: '.el-tabs__content',
            });
            // 还原分页数据
            Object.assign(this.$data.pageInfo, this.$options.data().pageInfo)
            showArticles(this.pageInfo.curPage, this.pageInfo.defSize,
                this.searchInfo.keyword, this.searchInfo.categoryId, this.searchInfo.state).then(res => {
                    this.isSearching = true
                    loading.close()
                    this.handlePostedArticleRes(res)
                })
        },
        // 清空搜索文章标题
        doClear() {
            Object.assign(this.$data.searchInfo, this.$options.data().searchInfo)
            // 如果是搜索状态，那么直接还原所有搜索
            if (this.isSearching) {
                Object.assign(this.$data.pageInfo, this.$options.data().pageInfo)
                this.getPostedArticles()
                this.isSearching = false
            }
        },
        // 更新设置文章信息
        setArticle(item) {
            Object.keys(this.article).forEach((value) => {
                this.article[value] = item[value]
            })
            this.drawerShow = true
        },
        // 点击上传封面
        preCoverUpload() {
            // 获取图片
            this.getImages()
            // 打开dialog
            this.selectCoverImgShow = true
        },
        // 获取图片列表
        getImages() {
            const loading = this.$loading({
                lock: true,
                text: '图片列表获取中...',
                target: '.image-list',
            });
            document.querySelector(".el-loading-mask").style.zIndex = '114514';
            showImages(this.imgPageInfo.curPage, this.imgPageInfo.defSize).then(res => {
                loading.close()
                if (res.code === SUCCESS) {
                    this.images = res.data.content
                    // 设置页码
                    this.imgPageInfo.curPage = res.data.number + 1
                    this.imgPageInfo.totalSize = res.data.totalElements
                } else {
                    showMessage("获取图片列表失败", 'error')
                }
            })
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
                    Object.assign(this.$data.imgPageInfo, this.$options.data().imgPageInfo)
                }
            })
        },
        // 上传失败
        uploadError() {
            showMessage("图片上传失败,请重试!", 'error')
        },
        // 图片列表分页
        onCurImagePageChange(page) {
            this.imgPageInfo.curPage = page
            this.getImages()
        },
        // 更新文章设置
        doSaveArticleSetting() {
            const loading = this.$loading({
                lock: true,
                text: '文章更新提交中...',
                target: '.el-drawer__body',
            });
            editPostedArticle(this.article.id, this.article).then(res => {
                loading.close()
                if (res.code === SUCCESS) {
                    showNotify("更新文章", res.message)
                    Object.assign(this.$data.article, this.$options.data().article)
                    this.drawerShow = false
                    this.getPostedArticles()
                } else {
                    showNotify("更新文章", res.message, 'error')
                }
            })
        },
        // 去除文章封面
        dropArticleCover() {
            this.article.cover = ''
        },
        // 获取草稿文章
        getSketchArticles() {
            const loading = this.$loading({
                lock: true,
                text: '草稿文章获取中...',
                target: '.el-tabs__content',
            });
            showSketchs(this.sketchPageInfo.curPage, this.sketchPageInfo.defSize).then(res => {
                loading.close()
                if (res.code === SUCCESS) {
                    this.sketchArticles = res.data.content
                    // 设置页码
                    this.sketchPageInfo.curPage = res.data.number + 1
                    this.sketchPageInfo.totalSize = res.data.totalElements
                } else {
                    showMessage("草稿文章列表获取失败,请重试!", 'error')
                }
            })
        },
        // 草稿文章切换页面
        onCurSketchPageChange(page) {
            this.sketchPageInfo.curPage = page
            this.getSketchArticles()
        },
    },
    mounted() {
        this.getPostedArticles()
        // 获取标签信息
        showLabelList().then(res => {
            if (res.code === SUCCESS) {
                this.labels = res.data
            } else {
                showMessage("标签数据获取失败,请重试", 'error')
            }
        })
    }
}
</script>

<style>
.posted-article-list-page-navigation-box {
    margin-top: 15px;
}

.article-edit-drawer-or-dialog-box .article-setting-drawer .el-drawer__body {
    padding-left: 0;
    padding-right: 0;
}

.article-edit-drawer-or-dialog-box .article-setting-drawer .el-drawer__header {
    font-size: 18px;
    font-weight: 600;
    padding: 16px 18px;
    margin: 0;
    border-bottom: 1px solid #dddddd;
}

.article-edit-drawer-or-dialog-box .article-setting-drawer .el-drawer__footer {
    padding: 12px 16px;
    border-top: 1px solid #dddddd;
}

.article-edit-drawer-or-dialog-box .article-setting-drawer .coverUploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: var(--el-transition-duration-fast);
    background: #fdfdfd;
}

.article-edit-drawer-or-dialog-box .article-setting-drawer .el-icon.cover-uploader {
    font-size: 28px;
    color: #8c939d;
    width: 100%;
    height: 220px;
    text-align: center;
}


.article-edit-drawer-or-dialog-box .article-setting-drawer .coverUploader .el-upload:hover {
    border-color: var(--el-color-primary);
}

.article-edit-drawer-or-dialog-box .article-setting-drawer .coverUploader .el-upload--text {
    width: 100%;
    height: 220px;
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

#username-span {
    color: rgba(0, 0, 0, .85);
    font-size: 16px;
    font-style: italic;
    font-weight: 555;
    color: cadetblue;
    font-family: -apple-system, BlinkMacSystemFont, Segoe UI, PingFang SC, Hiragino Sans GB, Microsoft YaHei, Helvetica Neue, Helvetica, Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji, Segoe UI Symbol;

}
</style>