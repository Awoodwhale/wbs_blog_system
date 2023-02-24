<template>
    <div class="articles-box">
        <div class="articles-header">
            <IndexBgHeader></IndexBgHeader>
        </div>

        <div class="index-content" id="index-content">
            <IndexContent ref="icRef">
                <template v-slot:default>
                    <!--文章内容-->
                    <div class="articles-container">
                        <el-skeleton :rows="6" animated :loading="markdownContent === undefined"/>
                        <v-md-preview id="v-md" v-if="markdownContent !== undefined"
                                      :text="markdownContent"
                                      v-viewer
                                      @copy-code-success="handleCopyCodeSuccess"></v-md-preview>

                        <div class="by_copyright" v-if="markdownContent !== undefined">
                            <div class="by_copyright_author">
                                <span class="by_copyright_meta">文章作者:</span>
                                <span class="by_copyright_info"><nuxt-link
                                    to="/">{{ $store.state.articleData.user.username }}</nuxt-link></span>
                            </div>

                            <div class="by_copyright_author ellipsis">
                                <span class="by_copyright_meta">文章链接:</span>
                                <span class="by_copyright_info"><a target="_blank" :href="href">{{
                                        href
                                    }}</a></span>
                            </div>

                            <div class="by_copyright_author ellipsis">
                                <span class="by_copyright_meta">版权声明:</span>
                                <span class="by_copyright_info">本站点所有文章除特别声明外，均采用 <a
                                    href="https://creativecommons.org/licenses/by-nc-sa/4.0/"
                                    rel="external nofollow noreferrer"
                                    target="_blank">CC BY-NC-SA 4.0</a> 许可协议。转载请著名来自 <nuxt-link
                                    to="/">{{ $store.state.articleData.user.username }}</nuxt-link>！</span>
                            </div>
                        </div>

                        <div class="by_post_tag_share" v-if="markdownContent !== undefined &&
                        $store.state.articleData.labelTags !== [] &&
                          $store.state.articleData.labelTags !== undefined &&
                          $store.state.articleData.labelTags !== ''">
                            <div class="tag_list">
                                <nuxt-link :to="{name:'labels-name',params:{name:item}}"
                                           v-for="(item, index) in $store.state.articleData.labelTags"
                                           :key="index">{{ item }}
                                </nuxt-link>
                            </div>

                            <div class="share_list">
                                <a class="icon-share-link" style="cursor:pointer;" rel="noopener noreferrer"
                                   title="复制文章链接"
                                   @click="copyText(href)">
                                    <svg class="icon" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg"
                                         p-id="3509" width="30" height="30">
                                        <path
                                            d="M515.2 64c-246.4 0-448 198.4-448 444.8S268.8 953.6 512 953.6c246.4 0 444.8-201.6 444.8-444.8S758.4 64 515.2 64z m-118.4 441.6c-9.6 9.6-28.8 9.6-41.6 0l-22.4-25.6-12.8-9.6c-19.2-22.4-32-48-35.2-76.8 0-28.8 9.6-57.6 28.8-76.8 22.4-19.2 48-28.8 76.8-28.8 28.8 0 60.8 12.8 83.2 35.2l86.4 86.4c22.4 22.4 35.2 51.2 35.2 80 0 25.6-9.6 51.2-28.8 70.4-9.6 9.6-28.8 9.6-41.6 0-9.6-9.6-9.6-28.8 0-38.4 19.2-19.2 16-51.2-3.2-73.6l-86.4-86.4c-12.8-12.8-25.6-19.2-41.6-19.2-9.6 0-22.4 3.2-32 12.8-12.8 12.8-12.8 25.6-12.8 35.2 0 12.8 6.4 28.8 19.2 38.4l12.8 12.8 22.4 22.4c3.2 12.8 3.2 28.8-6.4 41.6z m307.2 201.6c-19.2 19.2-44.8 28.8-73.6 28.8-28.8 0-60.8-12.8-83.2-35.2l-86.4-86.4c-22.4-22.4-35.2-51.2-35.2-80 0-25.6 9.6-51.2 28.8-70.4 9.6-9.6 28.8-9.6 41.6 0 9.6 9.6 9.6 28.8 0 38.4-19.2 19.2-16 51.2 3.2 73.6l86.4 86.4c12.8 12.8 25.6 19.2 41.6 19.2 9.6 0 22.4-3.2 32-12.8 12.8-12.8 12.8-25.6 12.8-35.2 0-12.8-6.4-28.8-19.2-38.4l-25.6-25.6-9.6-9.6c-9.6-9.6-9.6-28.8 0-38.4 9.6-9.6 28.8-9.6 41.6 0l12.8 12.8 19.2 19.2c22.4 19.2 35.2 48 35.2 73.6 9.6 32-3.2 57.6-22.4 80z"
                                            fill="#989b9e"></path>
                                    </svg>
                                </a>
                                <a :href="'http://service.weibo.com/share/share.php?sharesource=weibo&title=分享：'+$store.state.articleData.title+'，原文链接：'+href+'&amp;pic='"
                                   target="_blank" rel="noopener noreferrer" title="分享到新浪微博">
                                    <svg class="icon" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg"
                                         width="30"
                                         height="30">
                                        <path
                                            d="M480.8 457.76a271.04 271.04 0 0 0-37.28 2.72c-96 13.44-166.72 75.04-157.92 137.44s93.6 101.92 189.6 88.48 166.72-75.04 157.92-137.44c-7.68-54.08-73.12-91.04-152.32-91.2zm-23.36 211.52a122.08 122.08 0 0 1-24 2.4c-48 0-88-27.52-96-68.32-9.28-48 29.44-95.2 86.56-106.24s110.88 18.4 120 66.08-29.44 95.04-86.56 106.08z"
                                            fill="#F56467"></path>
                                        <path
                                            d="M512 73.28A438.72 438.72 0 1 0 950.72 512 438.72 438.72 0 0 0 512 73.28zm-43.84 666.88c-150.24 0-272-78.56-272-176S378.56 314.72 448 314.72c29.28 0 86.56 21.76 46.4 90.88a246.24 246.24 0 0 0 34.08-10.08c32-9.12 76.96-18.24 107.68 0 51.04 29.6 0 77.12 0 82.4s102.4 5.28 102.4 87.2c.32 96.48-120.16 175.04-270.4 175.04zm213.76-358.88a56 56 0 0 0-47.2-16 16.96 16.96 0 0 1-17.28-14.4 12.16 12.16 0 0 0 0 2.4v-4.8a12.16 12.16 0 0 0 0 2.4 20.48 20.48 0 0 1 17.28-17.28 77.28 77.28 0 0 1 68.32 18.56c32 28.48 18.72 75.68 18.72 75.68a21.28 21.28 0 0 1-20.48 17.28h-1.76a12.48 12.48 0 0 1-12.8-16.8 49.44 49.44 0 0 0-4.8-47.04zm120.16 60.64A29.6 29.6 0 0 1 776 467.84a22.08 22.08 0 0 1-19.68-25.92A139.2 139.2 0 0 0 736 336c-34.88-50.08-109.92-41.28-109.92-41.28A26.24 26.24 0 0 1 599.84 272v2.88-5.6V272a26.56 26.56 0 0 1 26.24-23.52 188.32 188.32 0 0 1 136.16 47.04c58.08 55.04 39.84 146.4 39.84 146.4z"
                                            fill="#F56467"></path>
                                        <path
                                            d="M459.36 547.04a17.6 17.6 0 1 0 17.6 17.6 17.6 17.6 0 0 0-17.6-17.6zm-44.32 23.2a43.52 43.52 0 0 0-18.4 4.32A32 32 0 0 0 376 613.12a32 32 0 0 0 42.88 9.12 32 32 0 0 0 20.64-38.72 25.76 25.76 0 0 0-24.48-13.28z"
                                            fill="#F56467"></path>
                                    </svg>
                                </a>
                                <a :href="'https://sns.qzone.qq.com/cgi-bin/qzshare/cgi_qzshare_onekey?url='+href+'&sharesource=qzone&title='+$store.state.articleData.title+'&pics='"
                                   target="_blank" rel="noopener noreferrer" title="分享到QQ空间">
                                    <svg class="icon" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg"
                                         width="30"
                                         height="30">
                                        <path
                                            d="M512 73.28A438.72 438.72 0 1 0 950.72 512 438.72 438.72 0 0 0 512 73.28zM829.92 432c5.6 16-150.24 146.4-150.24 146.4s2.08 12.64 4.16 22.08c0 0-72.64 2.24-132.32 0-32-1.28-69.12-7.04-69.12-7.04L656 470.24a1005.44 1005.44 0 0 0-125.76-13.6A908 908 0 0 0 380 463.36c-6.4 1.76 44.8 1.6 103.04 6.88 40.8 3.68 94.56 13.44 94.56 13.44l-172.8 128s73.92 4.48 140.32 4.16c74.72 0 142.24-9.92 142.72-8 12.96 56.16 36.96 167.52 28 176-12.16 12.32-185.6-97.6-185.6-97.6S368 785.6 345.92 785.6a3.68 3.68 0 0 1-2.08 0c-10.72-8.8 35.52-206.72 35.52-206.72S222.72 448 229.12 432s208-30.24 208-30.24 74.88-188 92.48-188 92.8 188 92.8 188S824.32 416 829.92 432z"
                                            fill="#F5BE3F"></path>
                                    </svg>
                                </a>
                                <a :href="'https://connect.qq.com/widget/shareqq/index.html?url='+href+'&title='+$store.state.articleData.title+'&pics='"
                                   target="_blank" rel="noopener noreferrer" title="分享到QQ">
                                    <svg class="icon" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg"
                                         width="30"
                                         height="30">
                                        <path
                                            d="M512 73.28A438.72 438.72 0 1 0 950.72 512 438.72 438.72 0 0 0 512 73.28zM759.84 646.4c-9.6 8.16-20.8-6.08-29.76-20.32s-14.88-26.72-16-21.76a158.4 158.4 0 0 1-37.44 70.72c-1.28 1.6 28.8 12.48 37.44 35.68s24 57.6-80 68.8a145.76 145.76 0 0 1-80-16c-16.96-8.32-27.52-16-29.6-16a73.6 73.6 0 0 1-13.28 0 108 108 0 0 1-14.4 0c-1.76 0-22.24 32-113.12 32-70.4 0-88.64-44.32-74.4-68.8s37.76-32 34.4-35.36a192 192 0 0 1-34.4-57.6 98.56 98.56 0 0 1-4.16-13.44c-1.28-4.64-6.56 8.64-13.92 21.76s-14.4 22.72-22.88 22.72a11.52 11.52 0 0 1-6.56-2.4c-20.96-16-19.2-55.2-5.44-93.12s48-75.04 48-83.2c1.28-30.24-3.04-35.2 0-43.2 6.56-17.76 14.72-10.88 14.72-20.16 0-116.32 86.4-210.56 192.96-210.56s192.96 94.24 192.96 210.56c0 4.48 11.68 0 17.12 20.16a196.96 196.96 0 0 1 0 43.2c0 11.04 29.44 24.48 44.8 83.2S768 640 759.84 646.4z"
                                            fill="#68A5E1"></path>
                                    </svg>
                                </a>
                            </div>
                        </div>

                        <client-only>
                            <comment
                                :key-map="keyMap"
                                :showNumber="2"
                                :order-by="'create_time'"
                                :comment-datas="commentData"
                                @submitComment="submitComment">
                            </comment>
                        </client-only>
                    </div>
                </template>
            </IndexContent>
        </div>
    </div>
</template>

<script>
import comment from "@/components/comment";
import IndexBgHeader from "@/components/IndexBgHeader";
import IndexContent from "@/components/IndexContent";
import {getPageNeededData, isEmpty, showNotify} from "@/assets/utils/utils";
import {
    showArticleById,
    showRecommendArticleById,
    showCommentById,
    checkUserLogin,
    SUCCESS,
    addComment
} from "@/assets/api/api";

export default {
    components: {IndexBgHeader, IndexContent, comment},
    data() {
        return {
            markdownContent: undefined,
            href: '',
            keyMap: {
                pid: "parent_comment_id",
                id: "id",
                isAdmin: "admin_comment"
            },
            commentData: [],
        }
    },
    methods: {
        handleCopyCodeSuccess() {
            this.$refs.icRef.handleCopyCodeSuccess()
        },
        copyText(copyText) {
            this.$refs.icRef.copyText(copyText)
        },
        // 评论的回调
        submitComment(item) {
            // 判断评论是否为空
            if (isEmpty(item.content)) {
                showNotify("文章评论", "评论内容不可为空！", "error")
                return
            }
            // 添加评论
            const fun = () => {
                // 检测出有账号登录，那么就进行评论
                addComment({
                    articleId: this.$store.state.articleData.id,
                    content: item.content,
                    parentId: item.pid,
                }).then(addRes => {
                    // 评论成功
                    if (addRes.code === SUCCESS) {
                        showNotify("文章评论", addRes.message)
                        // 重新获取评论
                        showCommentById(this.$store.state.articleData.id).then(res => {
                            if (res.code === SUCCESS) {
                                this.handleCommentArray(res.data)
                            } else {
                                showNotify("文章评论", "获取评论列表失败，请稍后重试！", "error")
                            }
                        })
                    } else {
                        showNotify("文章评论", addRes.message, "error")
                    }
                }).catch(e => {
                    showNotify("文章评论", "本站已关闭评论功能！", "error")
                })
            }
            // 未登录，就去检车登录信息
            if (this.$store.state.userInfo.id === undefined) {
                checkUserLogin().then(res => {
                    if (res.code === SUCCESS) {
                        this.$store.commit("setUserInfo", res.data)
                        fun()
                    } else {
                        showNotify("文章评论", "请先登录账号后再进行评论！", "error")
                        // 跳转到login页面
                        this.$router.push("/login")
                    }
                })
            } else {    // 如果登录了，直接评论
                fun()
            }
        },
        // 处理评论数组的方法
        handleCommentArray(tmpMap) {
            let resMap = []
            for (let i = 0; i < tmpMap.length; i++) {
                let tmp = {
                    id: tmpMap[i].id,
                    parent_comment_id: tmpMap[i].parentId,
                    admin_comment: tmpMap[i].userId === this.$store.state.articleData.userId ? 1 : 0,
                    avatar: tmpMap[i].user.avatar,
                    nickname: tmpMap[i].user.username,
                    content: tmpMap[i].content,
                    create_time: tmpMap[i].createTime
                }
                resMap.push(tmp)
            }
            this.commentData = resMap
        }
    },
    // 异步执行，请求数据
    async asyncData(ctx) {
        await getPageNeededData(ctx)
        const res = await showArticleById(ctx.route.params.id)
        // 覆盖设置文本内容
        if (res.code === SUCCESS) {
            ctx.store.commit("setArticleData", res.data)
        }
        // 获取推荐文章
        const recommendRes = await showRecommendArticleById(ctx.route.params.id)
        if (recommendRes.code === SUCCESS) {
            ctx.store.commit("setRecommendArticles", recommendRes.data)
        }
        // 获取评论列表
        await showCommentById(ctx.route.params.id).then(commentRes => {
            if (commentRes.code === SUCCESS) {
                ctx.store.commit("setArticleComments", commentRes.data)
            } else {
                ctx.store.commit("setArticleComments", [])
            }
        }).catch(e => {
        })

    },
    mounted() {
        // 设置标题内容
        if (this.$store.state.articleData !== undefined) {
            document.getElementById("pages-title").innerHTML =
                this.$store.state.articleData.title.length > 25 ?
                    this.$store.state.articleData.title.substring(0, 25) + '...' : this.$store.state.articleData.title
        }
        this.markdownContent = this.$refs.icRef.markdownContent
        this.href = this.$refs.icRef.href
        this.handleCommentArray(this.$store.state.articleComments)  // 处理评论数组
        this.$nextTick(() => {
            document.querySelectorAll("img").forEach(it => {
                it.style.cursor = 'pointer'
                it.parentElement.style.textAlign = 'center'
            })
        })
    },
}
</script>

<style>

</style>
