<template>
    <div class="index-content-box">
        <!-- 文章栏 -->
        <div class="index-content-container">
            <!--插槽-->
            <slot></slot>
            <el-backtop id="back-to-top" target=".el-scrollbar__wrap"></el-backtop>
        </div>

        <!-- 侧边信息栏 -->
        <aside class="index-content-aside">
            <!-- 博主信息卡片 -->
            <div class="user-info-card aside-card" style="padding:1rem 0;"
                 v-if="$route.name !== 'mine' && $route.name !== 'articles-id' &&  $store.state.blogInfos !== undefined">
                <div v-if="$store.state.adminInfos !== undefined && $store.state.adminInfos.length > 1"
                     class="admin-info">博主们の信息
                </div>
                <div v-else class="admin-info">博主の信息</div>
                <!-- 轮播管理员图片 -->
                <el-carousel v-if="$store.state.adminInfos !== undefined" indicator-position="outside"
                             class="carousel-container" :interval="5555">
                    <el-carousel-item v-for="(item) in $store.state.adminInfos" :key="item.id">
                        <div class="center">
                            <el-image fit="contain" class="user-avatar" :src="item.avatar">
                                <div slot="placeholder" class="image-slot" style="width: 100%; height: 100%;">
                                    <img src="/loading.gif" style="width: 110px;height: 110px;">
                                </div>
                                <div slot="error" class="image-slot" style="width: 100%; height: 100%;">
                                    <img src="/loading.gif" style="width: 100%;height: 100%;">
                                </div>
                            </el-image>
                            <div class="user-name">{{ item.username }}</div>
                            <div class="user-desc">{{ item.sign }}</div>
                        </div>
                    </el-carousel-item>
                </el-carousel>

                <div class="center user-data">
                    <nuxt-link class="user-data-item" to="/articles">
                        <div class="headline">文章</div>
                        <div class="length-num" v-if="$store.state.blogInfos.article !== undefined">
                            {{ $store.state.blogInfos.article }}
                        </div>
                    </nuxt-link>
                    <nuxt-link class="user-data-item" to="/categories">
                        <div class="headline">分类</div>
                        <div class="length-num" v-if="$store.state.blogInfos.category !== undefined">
                            {{ $store.state.blogInfos.category }}
                        </div>
                    </nuxt-link>
                    <nuxt-link class="user-data-item" to="/diary">
                        <div class="headline">说说</div>
                        <div class="length-num" v-if="$store.state.blogInfos.diary !== undefined">
                            {{ $store.state.blogInfos.diary }}
                        </div>
                    </nuxt-link>
                </div>

                <div style="padding: 0 1.2rem;">
                    <nuxt-link to="#" class="btn hvr-icon-wobble-horizontal">
                        <i class="fa fa-hand-o-right  hvr-icon"
                           style="margin-right:6px;"
                           aria-hidden="true"></i>
                        <span v-if="$store.state.adminInfos !== undefined && $store.state.adminInfos.length > 1"
                              style="margin-right:20px">关注我们</span>
                        <span v-else style="margin-right:20px">关注我</span>
                    </nuxt-link>
                </div>

            </div>

            <!--个人用户的信息-->
            <div class="user-info-card aside-card login-user-data" v-if="$route.name === 'mine'">
                <div class="admin-info user-info">用户の信息</div>
                <el-carousel v-if="$store.state.userInfo.id !== undefined" indicator-position="outside"
                             class="carousel-container" :interval="5555">
                    <el-carousel-item>
                        <div class="center">
                            <el-image fit="contain" class="user-avatar" :src="$store.state.userInfo.avatar">
                                <div slot="placeholder" class="image-slot" style="width: 100%; height: 100%;">
                                    <img src="/loading.gif" style="width: 110px;height: 110px;">
                                </div>
                                <div slot="error" class="image-slot" style="width: 100%; height: 100%;">
                                    <img src="/loading.gif" style="width: 100%;height: 100%;">
                                </div>
                            </el-image>
                            <div class="user-name">{{ $store.state.userInfo.username }}</div>
                            <div class="user-desc">{{ $store.state.userInfo.sign }}</div>

                        </div>
                    </el-carousel-item>
                </el-carousel>
                <div v-else style="text-align: center">用户未登录，请先<nuxt-link to="/login">前往登录</nuxt-link>!
                </div>
            </div>

            <!--用户设置页面不显示，只在其他页面显示-->
            <div v-if="$route.name !== 'mine'" :style="rightListStyle[isArticle] + 'transition: top 0.5s;'"
                 id="right-title-list">
                <!--右侧目录栏，只有在显示文章详情才会出现-->
                <div class="title-list aside-card" v-if="$route.name === 'articles-id'">
                    <div style="padding-bottom: 6px;font-size: 1rem;">
                        <i class="fa fa-list-alt" aria-hidden="true"></i>
                        <span>目录</span>
                    </div>
                    <el-skeleton :rows="4" animated :loading="navList === undefined"/>
                    <div class="right-title-list">
                        <el-tabs v-if="navList !== undefined && navList.length !== 0" @tab-click="handleClick"
                                 v-model="activeName"
                                 :tab-position="tabPosition"
                                 style="height:60vh; text-decoration:none; overflow: scroll;">
                            <el-tab-pane :name="'tab'+index"
                                         :class="item.lev"
                                         v-for="(item, index) in navList"
                                         :key="index"
                                         :label="item.name"></el-tab-pane>
                        </el-tabs>
                        <div v-else-if="navList !== undefined && navList.length === 0" style="text-align: center">
                            该文章暂无目录
                        </div>
                    </div>

                </div>

                <!--首页显示最新文章-->
                <div class="latest-articles aside-card">
                    <div class="card-icon-title">
                        <i class="fa fa-clock-o" aria-hidden="true"></i>
                        <span v-if="$route.name === 'articles-id'">推荐文章</span>
                        <span v-else>最新文章</span>
                    </div>
                    <!-- 最新文章列表 -->
                    <div class="latest-list"
                         v-if="$route.name !== 'articles-id' && $store.state.articles !== undefined">
                        <!-- 循环给出5个 -->
                        <div class="latest-list-item" v-for="(item, index) in $store.state.articles.slice(0, 5)"
                             :key="index">
                            <nuxt-link :to="'/articles/'+item.id" class="thumbnail">
                                <el-image lazy fit="cover" :src="item.cover" v-if="item.cover !== ''">
                                    <div slot="placeholder" class="image-slot" style="width: 100%; height: 100%;">
                                        <img src="/loading.gif" style="width: 80px;height: 80px;" alt="">
                                    </div>
                                    <div slot="error" class="image-slot" style="width: 100%; height: 100%;">
                                        <img src="/loading.gif" style="width: 100%;height: 100%;" alt="">
                                    </div>
                                </el-image>
                                <el-image lazy fit="cover" :src="$store.state.randomPicUrl + '?' + item.id"
                                          v-else>
                                    <div slot="placeholder" class="image-slot" style="width: 100%; height: 100%;">
                                        <img src="/loading.gif" style="width: 80px;height: 80px;" alt="">
                                    </div>
                                    <div slot="error" class="image-slot" style="width: 100%; height: 100%;">
                                        <img src="/loading.gif" style="width: 100%;height: 100%;" alt="">
                                    </div>
                                </el-image>
                            </nuxt-link>
                            <div class="latest-content">
                                <nuxt-link :to="'/articles/'+item.id" class="content-title" style="overflow: hidden">
                                    <span
                                        v-if="item.title.length > 22"> {{
                                            item.title.trim().substring(0, 22) + "..."
                                        }}</span>
                                    <span v-else>{{ item.title.trim() }}</span>
                                </nuxt-link>
                                <time class="time" v-if="item.createTime !== undefined">
                                    {{ item.createTime.split(" ")[0] }}
                                </time>
                            </div>
                        </div>
                    </div>
                    <!--推荐文章列表-->
                    <div v-else-if="$route.name === 'articles-id' && $store.state.recommendArticles !== undefined">
                        <!-- 循环给出5个 -->
                        <div class="latest-list-item" v-for="(item, index) in $store.state.recommendArticles"
                             :key="index">
                            <nuxt-link :to="'/articles/'+item.id" class="thumbnail">
                                <el-image lazy fit="cover" :src="item.cover" v-if="item.cover !== ''">
                                    <div slot="placeholder" class="image-slot" style="width: 100%; height: 100%;">
                                        <img src="/loading.gif" style="width: 80px;height: 80px;" alt="">
                                    </div>
                                    <div slot="error" class="image-slot" style="width: 100%; height: 100%;">
                                        <img src="/loading.gif" style="width: 100%;height: 100%;" alt="">
                                    </div>
                                </el-image>
                                <el-image lazy fit="cover" :src="$store.state.randomPicUrl + '?' + item.id"
                                          v-else>
                                    <div slot="placeholder" class="image-slot" style="width: 100%; height: 100%;">
                                        <img src="/loading.gif" style="width: 80px;height: 80px;" alt="">
                                    </div>
                                    <div slot="error" class="image-slot" style="width: 100%; height: 100%;">
                                        <img src="/loading.gif" style="width: 100%;height: 100%;" alt="">
                                    </div>
                                </el-image>
                            </nuxt-link>
                            <div class="latest-content">
                                <nuxt-link :to="'/articles/'+item.id" class="content-title">
                                    <span
                                        v-if="item.title.length > 22"> {{
                                            item.title.trim().substring(0, 22) + "..."
                                        }}</span>
                                    <span v-else>{{ item.title.trim() }}</span>
                                </nuxt-link>
                                <time class="time" v-if="item.createTime !== undefined">
                                    {{ item.createTime.split(" ")[0] }}
                                </time>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 分类列表 -->
                <div class="aside-categories aside-card" v-if="$store.state.categories !== undefined">
                    <div class="card-icon-title">
                        <i class="fa fa-list-ul" aria-hidden="true"></i>
                        <span>文章分类</span>
                    </div>

                    <div class="aside-category-list">
                        <nuxt-link :to="{name:'categories-categoryId',params:{name:item.name,categoryId:item.id}}"
                                   class="aside-category-item" v-for="(item, index) in $store.state.categories"
                                   :key="index">
                            <span class="ellipsis">{{ item.name }}</span>
                            <span class="ellipsis">{{ item.count }}</span>
                        </nuxt-link>
                    </div>
                </div>

                <!--文章标签-->
                <div class="aside-labels aside-card" v-if="$store.state.labels !== undefined">
                    <div class="card-icon-title">
                        <i class="fa fa-tag" aria-hidden="true"></i>
                        <span>文章标签</span>
                    </div>

                    <div class="aside-labels-list">
                        <nuxt-link :to="{name:'labels-name',params:{name:item.name}}"
                                   v-for="(item, index) in $store.state.labels" :key="index">{{ item.name }}
                        </nuxt-link>
                    </div>
                </div>
            </div>

        </aside>
    </div>
</template>

<script>

import ColorTags from "@/components/ColorTags";
import CloudTags from "@/components/CloudTags";
import {showArticles, showDiaries, SUCCESS} from "@/assets/api/api";
import {showMessage} from "@/assets/utils/utils";
import {mapMutations} from 'vuex'

export default {
    components: {CloudTags, ColorTags},
    inject: ["scrollRef"],
    data() {
        return {
            activeName: 'tab0',
            tabPosition: 'right',
            navList: undefined,
            isArticle: false,
            rightListStyle: {
                true: 'position: sticky;top: 20px;',
                false: 'padding-top: 1rem;',
            },
            markdownContent: undefined,
            useScroll: true,
            href: '',
        }
    },
    methods: {
        // 导入vuex方法
        ...mapMutations(['setArticles', 'setArticlesCurPage', 'setDiaries', 'setDiariesCurPage']),
        // 切换文章页面
        onCurArticlesPageChange(page) {
            this.setArticlesCurPage(page)
            this.getArticles()
        },
        // 获取新文章
        getArticles() {
            showArticles(this.$store.state.articlesPageInfo.curPage).then(res => {
                if (res.code === SUCCESS) {
                    this.setArticles(res.data)
                    document.getElementById("index-content").scrollIntoView({behavior: 'smooth'});
                } else {
                    showMessage("获取分页信息失败,请重试!", 'error')
                }
            })
        },
        // 切换日记说说页面
        onCurDiariesPageChange(page) {
            this.setDiariesCurPage(page)
            this.getDiaries()
        },
        // 获取日记
        getDiaries() {
            showDiaries(this.$store.state.diariesPageInfo.curPage).then(res => {
                if (res.code === SUCCESS) {
                    this.setDiaries(res.data)
                    document.getElementById("index-content").scrollIntoView({behavior: 'smooth'});
                } else {
                    showMessage("获取分页信息失败,请重试!", 'error')
                }
            })
        },
        // 点击 分类 tag
        clickTagItem(tag) {
            this.$router.push({name: 'categories-categoryId', params: {name: tag.name, categoryId: tag.id}})
        },
        // 处理代码复制
        handleCopyCodeSuccess() {
            showMessage("复制成功", "success", 1500)
        },
        // 修改主题
        changeTheme() {
            // TODO:修改主题按钮设置
            showMessage("修改主题", "warning")
        },
        // 右侧目录栏跳转
        handleClick(tab, event) {
            if (this.useScroll) {
                this.useScroll = false
                this.jump(tab.index)
                setTimeout(() => {
                    this.useScroll = true
                }, 900)
            }
        },
        // 跳转实现
        jump(index) {
            let jump = document.querySelectorAll("h1,h2,h3,h4,h5,h6");
            jump = Array.from(jump);
            jump.splice(0, 1)
            jump[index].scrollIntoView({behavior: 'smooth'})
        },
        // 跳转标题查询
        selectAllTitle() {
            let title = document.querySelectorAll("h1,h2,h3,h4,h5,h6");
            this.navList = Array.from(title)
            this.navList.splice(0, 1)
            this.navList.forEach(item => {
                item.name = item.innerHTML.replaceAll('amp;', '')
            })
            this.navList.forEach(el => {
                let index = el.localName.indexOf('h')
                el.lev = 'lev' + el.localName.substring(index + 1, el.localName.length)
            })
        },
        // 复制文本到粘贴板
        copyText(copyText) {
            let input = document.createElement("input") // 直接构建input
            input.value = copyText; // 设置内容
            document.body.appendChild(input); // 添加临时实例
            input.select(); // 选择实例内容
            try {
                document.execCommand("Copy")
                showMessage("复制文章链接成功!")
            } catch (e) {
                showMessage("复制文章链接失败,请检查浏览器权限!", 'error')
            } finally {
                document.body.removeChild(input); // 删除临时实例
            }
        },
    },
    mounted() {
        // 跳转到当前页面需要回到顶部
        const hb = document.getElementById("header-background")
        if (hb) {
            hb.scrollIntoView({behavior: 'smooth'})
        }
        // 如果是文章详情，需要显示右侧目录栏
        if (this.$route.name === 'articles-id') {
            this.markdownContent = this.$store.state.articleData.content
            this.isArticle = true
            this.href = document.location.href
            this.$nextTick(() => {
                // scroll代表滚动条距离页面顶部距离
                const bar = this.scrollRef().$refs.wrap
                bar.onscroll = () => {
                    if (this.useScroll) {
                        let self = this;
                        for (let i = self.navList.length - 1; i >= 0; i--) {
                            if (bar.scrollTop >= self.navList[i].offsetTop + document.getElementById("header-background").offsetHeight) {
                                self.activeName = 'tab' + i
                                break;
                            }
                        }
                    }
                }
                this.selectAllTitle();
                setTimeout(() => {
                    let navs = document.querySelectorAll('.el-tabs__item');
                    for (let i = navs.length - 1; i >= 0; i--) {
                        // 从lev1到lev5分别添加不同到样式
                        document.querySelector('#' + navs[i].id).style.padding = "0";
                        switch (this.navList[i].lev) {
                            case 'lev1':
                                document.querySelector('#' + navs[i].id).style.paddingLeft = "10px";
                                break;
                            case 'lev2':
                                document.querySelector('#' + navs[i].id).style.paddingLeft = "25px";
                                break;
                            case 'lev3':
                                document.querySelector('#' + navs[i].id).style.paddingLeft = "40px";
                                break;
                            case 'lev4':
                                document.querySelector('#' + navs[i].id).style.paddingLeft = "55px";
                                document.querySelector('#' + navs[i].id).style.fontWeight = "400";
                                break;
                            case 'lev5':
                                document.querySelector('#' + navs[i].id).style.paddingLeft = "70px";
                                document.querySelector('#' + navs[i].id).style.fontWeight = "400";
                                break;
                        }
                    }
                });
            })
        }

    }
}
</script>

<style>
.user-name {
    font-weight: 500;
    font-size: 1.57em;
}

.user-desc {
    margin-top: -0.3rem;
}

.title-list .el-tabs__nav-wrap::after {
    display: none;
}

.carousel-container {
    border-radius: 25px;
    padding: 0;
}

.carousel-container .el-carousel__container {
    height: 185px !important;
}

.el-pager li.active {
    background-color: #00c4b6 !important;
}

.el-pager li.number:hover {
    color: #00c4b6 !important;
}

.el-pager li.active:hover {
    color: #fff !important;
}

.el-pagination__jump .el-input__inner:focus {
    border-color: #00c4b6;
}

.right-title-list {
    max-height: 80vh;
    overflow-y: auto;
}

.right-title-list .el-tabs__header {
    margin-left: 0 !important;
    float: none !important;
}

.right-title-list .el-tabs--right .el-tabs__nav-wrap.is-right {
    margin-left: 2px !important;
}

.right-title-list .el-tabs__item {
    height: auto !important;
    line-height: 30px;
}

.right-title-list .el-tabs__active-bar.is-right {
    display: none;
}

.right-title-list .el-tabs__nav {
    white-space: normal !important;
}

.right-title-list .is-active {
    background-color: var(--btn-bg);
    color: #fff;
    transition: all 0.2s ease-in-out;
    border-radius: 8px;
}

#v-md .github-markdown-body {
    font-family: var(--base-font-family) !important;
    padding: 0 15px 20px !important;
}

#v-md .github-markdown-body h1 {
    text-align: center;
    font-size: 28px;
}

#v-md .github-markdown-body h1,
#v-md .github-markdown-body h2,
#v-md .github-markdown-body h3,
#v-md .github-markdown-body h4,
#v-md .github-markdown-body h5,
#v-md .github-markdown-body h6 {
    border-bottom: 0;
}

#v-md .github-markdown-body pre[class*="v-md-hljs-"] {
    background: #282c34;
    border-radius: 0 10px 10px 0;
}

#v-md .github-markdown-body pre {
    margin-bottom: 16px;
    padding: 16px 16px 16px 2px;
    overflow: auto;
    font-size: 85%;
    line-height: 1.45;
    word-wrap: normal;
    background-color: #f6f8fa;
    border-radius: 3px;
}

#v-md .github-markdown-body pre[class*="v-md-hljs-"] code {
    color: #abb2bf;
    font-family: "Fira Code", "Fira Mono", Menlo, Consolas, "DejaVu Sans Mono", monospace;
}

#v-md .github-markdown-body code:not([class]) {
    color: #f47466;
    font-family: consolas, Menlo, "PingFang SC", "Microsoft JhengHei", "Microsoft YaHei", sans-serif;
    font-size: 16px;
    background: rgba(27, 31, 35, 0.05);
}

#v-md .github-markdown-body a {
    color: #49b1f5;
    text-decoration: none;
}

#v-md .github-markdown-body .hljs-attribute {
    color: #61afef;
}

#v-md .github-markdown-body .hljs-selector-class {
    color: #cca073;
}

#v-md .github-markdown-body .hljs-built_in {
    color: #60b8c3 !important;
}

#v-md .github-markdown-body div[class*=v-md-pre-wrapper-].line-numbers-mode .line-numbers-wrapper {
    width: 2.5rem;
}

#v-md .github-markdown-body div[class*=v-md-pre-wrapper-].line-numbers-mode:after {
    position: absolute;
    top: 0;
    left: 0;
    width: 2.5rem;
    height: 100%;
    background-color: #282c34;
    border-right: 0;
    border-radius: 10px 0 0 10px;
    content: "";
}

#v-md .github-markdown-body div[class*=v-md-pre-wrapper-].line-numbers-mode pre {
    margin-left: 2.5rem;
}

#v-md .github-markdown-body div[class*=v-md-pre-wrapper-].line-numbers-mode .line-numbers-wrapper .line-number {
    font-size: 16px;
}

#v-md .github-markdown-body .hljs-keyword, .github-markdown-body .hljs-selector-tag, .github-markdown-body .hljs-subst {
    color: #f97bb0 !important;
}

#v-md .github-markdown-body .hljs-section, .github-markdown-body .hljs-selector-id, .github-markdown-body .hljs-title {
    color: #6ac198 !important;
}

#v-md .github-markdown-body .hljs-doctag, .github-markdown-body .hljs-string {
    color: #ff7f5b !important;
}

#v-md .github-markdown-body .hljs-literal, .github-markdown-body .hljs-number, .github-markdown-body .hljs-tag .hljs-attr, .github-markdown-body .hljs-template-variable, .github-markdown-body .hljs-variable {
    color: #cbc673 !important;
}

#v-md .github-markdown-body .hljs-attribute, #v-md .github-markdown-body .hljs-name, #v-md .github-markdown-body .hljs-tag {
    color: #fc77a2 !important;
    font-weight: 400;
}

/*滚动条*/
#v-md .github-markdown-body pre[class*="v-md-hljs-"]::-webkit-scrollbar {
    height: 0.7em;
    z-index: 3;
}

#v-md .github-markdown-body pre[class*="v-md-hljs-"]::-webkit-scrollbar-thumb {
    background-color: #afafaf;
    background-image: -webkit-linear-gradient(45deg, hsla(0, 0%, 100%, 0.4) 25%, transparent 0, transparent 50%, hsla(0, 0%, 100%, 0.4) 0, hsla(0, 0%, 100%, 0.4) 75%, transparent 0, transparent);
    border-radius: 2em;
}

#v-md .github-markdown-body pre[class*="v-md-hljs-"]::-webkit-scrollbar-track {
    border-radius: 2em;
    z-index: 3;
}

@media screen and (max-width: 860px) {
    #v-md .github-markdown-body {
        font-size: 15px;
    }

    #v-md .github-markdown-body h1,
    #v-md .github-markdown-body h2,
    #v-md .github-markdown-body h3,
    #v-md .github-markdown-body h4,
    #v-md .github-markdown-body h5,
    #v-md .github-markdown-body h6 {
        margin: 15px 0;
        font-size: 1.3rem;
    }

    #v-md .github-markdown-body pre[class*="v-md-hljs-"] code:not([class]) {
        font-size: 13px;
    }

    #v-md .github-markdown-body div[class*=v-md-pre-wrapper-].line-numbers-mode .line-numbers-wrapper .line-number {
        font-size: 13px;
    }

}


</style>

<style scoped>
.index-content-box {
    max-width: 1200px;
    display: flex;
    margin: 0 auto;
    padding: 2rem 15px;
    position: relative;
}

.index-content-container {
    width: 75%;
    transition: all 0.3s;
    align-self: flex-start;
}

.index-content-container a:hover {
    color: #49b1f5 !important;
}

.index-content-item {
    display: flex;
    flex-direction: row;
    align-items: center;
    height: 20em;
    border-radius: 25px;
    background: var(--card-bg);
    box-shadow: var(--card-box-shadow);
    transition: all 0.3s;
}

.articles-container {
    width: 100%;
    transition: all 0.3s;
    align-self: flex-start;
    padding: 50px 40px;
    border-radius: 25px;
    background: var(--card-bg);
    box-shadow: var(--card-box-shadow);
}

.articles-title {
    position: relative;
    margin-left: 10px;
    padding-bottom: 20px;
    padding-left: 20px;
    font-size: 1.72rem;
}

.articles-title:before {
    position: absolute;
    top: calc((100% - 36px) / 2);
    left: -9px;
    z-index: 1;
    width: 10px;
    height: 10px;
    border: 5px solid #49b1f5;
    border-radius: 10px;
    background: var(--card-bg);
    content: '';
    line-height: 10px;
    -webkit-transition: all .2s ease-in-out;
    transition: all .2s ease-in-out;
}

.articles-title:after {
    position: absolute;
    bottom: 0;
    left: 0;
    z-index: 0;
    width: 2px;
    height: 2.5rem;
    background: #aadafa;
    content: '';
}

.articles-sort {
    margin-left: 10px;
    padding-left: 20px;
    border-left: 3px solid #aadafa;
}

.articles-item {
    position: relative;
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
    margin: 0 0 20px 10px;
}

.articles-item:before {
    position: absolute;
    left: calc(-20px - 17px);
    width: 6px;
    height: 6px;
    border: 3px solid #49b1f5;
    border-radius: 6px;
    background: var(--card-bg);
    content: '';
    transition: all .2s ease-in-out;
}

.articles-item-info {
    webkit-box-flex: 1;
    -ms-flex: 1;
    flex: 1;
    padding: 0 16px;
}

.articles-item-info-item {
    color: #858585;
    font-size: 95%;
}

.articles-item-info-title {
    color: var(--font-color);
    font-size: .9rem;
    -webkit-transition: all .3s;
    transition: all .3s;
}

/deep/ .article-img-cover {
    overflow: hidden;
    width: 45%;
    height: 100%;
    border-radius: 25px 0 0 25px;
}

/deep/ .article-img-cover .el-image {
    width: 100%;
    height: 100%;
    border-radius: 25px 0 0 25px;
    overflow: hidden;
}


.index-content-item .article-img-cover img {
    width: 100%;
    height: 100%;
    transition: all 0.6s;
    object-fit: cover;
}

.article-info {
    display: inline-block;
    overflow: hidden;
    padding: 0 35px;
    width: 55%;
}

.article-title {
    margin-bottom: 0.3rem;
    color: var(--text-highlight-color);
    font-size: 1.72em;
    line-height: 1.4;
    transition: all 0.2s ease-in-out;
    -webkit-line-clamp: 2;
    display: -webkit-box;
    overflow: hidden;
    -webkit-box-orient: vertical;
}

.article-meta {
    color: #858585;
    font-size: 90%;
}

.article-meta a {
    color: #858585;
}

.article-wrap {
    display: -webkit-box;
    overflow: hidden;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 1;
}

.article-wrap > div {
    display: inline-flex;
    align-items: center;
}

.article-wrap svg {
    width: auto;
    height: 16px;
    margin-right: 5px;
    vertical-align: -0.15em;
    fill: currentColor;
    overflow: hidden;
    font-size: 20px;
}

.article-wrap .ato a {
    margin-right: 5px;
}

.separator {
    margin: 0 0.3rem;
}

.article-summary {
    margin-top: 0.3rem;
    -webkit-line-clamp: 3;
    display: -webkit-box;
    overflow: hidden;
    -webkit-box-orient: vertical;
}

.index-content-item:not(:first-child) {
    margin-top: 1rem;
}

.index-content-aside {
    width: 25%;
    padding-left: 15px;
}

.aside-card {
    padding: 1rem 1.2rem;
    border-radius: 25px;
    background: var(--card-bg);
    box-shadow: var(--card-box-shadow);
    transition: all 0.3s;
}

.admin-info {
    text-align: center;
    font-size: 1.5em;
    padding-bottom: 10px;
    font-weight: 550;
}

.aside-card:not(:first-child) {
    margin-top: 1rem;
}

.center {
    text-align: center;
}

.user-avatar {
    overflow: hidden;
    margin: 0 auto;
    width: 110px;
    height: 110px;
    border-radius: 70px;
}

.user-data {
    padding: 0 1.2rem;
    display: table;
    margin: 0 0 4px;
    width: 100%;
    table-layout: fixed;
}

.headline {
    color: var(--font-color);
    font-size: 1rem;
}

.length-num {
    margin-top: -0.42em;
    color: var(--text-highlight-color);
    font-size: 1.2rem;
}

.user-data-item {
    display: table-cell;
}

.btn {
    display: block;
    margin-top: 0.7rem;
    background-color: var(--btn-bg);
    color: var(--btn-color);
    text-align: center;
    line-height: 2.4;
    border-radius: 25px;
    overflow: hidden;
    transition: color 1s;
    position: relative;
    z-index: 1;
}

.btn:before {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: -1;
    background: var(--btn-hover-color);
    content: '';
    transition: transform 0.5s ease-out;
    transform: scaleX(0);
    transform-origin: 0 50%;
}

.btn:hover:before {
    transition-timing-function: cubic-bezier(0.45, 1.64, 0.47, 0.66);
    transform: scaleX(1);
}

.latest-articles {
}

.card-icon-title {
    padding-bottom: 6px;
    font-size: 1rem;
}

.card-icon-title i {
    /* font-size: 1.1rem; */

}

.card-icon-title span {
    margin-left: 3px;
}

.late-list-item {
    display: flex;
    align-items: center;
    padding: 6px 0;
    border-bottom: 1px dashed #f5f5f5;
}

.thumbnail {
    overflow: hidden;
    width: 4rem;
    height: 4rem;
    border-radius: 5px;
}

.thumbnail .el-image {
    width: 4rem;
    height: 4rem;
    border-radius: 5px;
    transition: all 0.6s;
}

.latest-content {
    flex: 1;
    padding-left: 10px;
    word-break: break-all;
}

.content-title {
    display: block;
    color: var(--font-color);
    font-size: 95%;
    line-height: 1.5 !important;
    -webkit-line-clamp: 1;
}

.content-title:hover {
    color: #49b1f5;
}

.time {
    display: block;
    color: #858585;
    font-size: 85%;
}

.latest-list-item {
    display: flex;
    align-items: center;
    padding: 6px 0;
    border-bottom: 1px dashed #f5f5f5;
    margin: 0;
}

.ellipsis {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.aside-category-item span:not(:last-child) {
    margin-bottom: 0.5rem;
}

.aside-category-item span:first-child {
    flex: 1;
}

.aside-category-item:hover {
    padding: 3px 15px 3px 30px;
    color: #49b1f5;
}

.aside-category-item {
    position: relative;
    display: flex;
    flex-direction: row;
    padding: 3px 10px 3px 27px;
    color: var(--font-color);
    transition: all 0.4s;
}

.aside-category-item:before {
    position: absolute;
    top: 0.75rem;
    left: 0;
    width: 0.43rem;
    height: 0.43rem;
    border: 0.215rem solid #49b1f5;
    border-radius: 0.43rem;
    background: 0 0;
    content: '';
    cursor: pointer;
    transition: all 0.3s ease-out;
}

.aside-category-item:hover:before {
    border-color: #fc4b4b;
}

.aside-labels-list a {
    display: inline-block;
    padding: 0 0.2rem;
    color: #99a9bf;
    text-decoration: none;
    transition: all 0.2s;
    overflow-wrap: break-word;
}

.index-content-page-navigation-box {
    margin: 20px 0;
    text-align: center;
}

.diary-item {
    position: relative;
}

.diary-item:after,
.diary-item:before {
    content: "";
    z-index: 1;
    position: absolute;
    background: rgba(68, 215, 182, .5);
    width: 2px;
    left: 7px;
}

.diary-item:before {
    top: 0;
    height: 20px;
}

.diary-item:after {
    top: 26px;
    height: calc(100% - 26px);
}

.diary-item .diary-meta {
    position: relative;
    color: var(--tab-botton-color);
    font-size: 15px;
    line-height: 32px;
    height: 32px;
    left: 27px;
    font-weight: 700;
    display: block;
}

.diary-item .diary-meta:before {
    background: rgba(68, 215, 182, .5);
    width: 16px;
    height: 16px;
    border-radius: 8px;
    content: "";
    position: absolute;
    top: 8px;
    z-index: 2;
    left: -27px;
}

.diary-item .diary-meta:after {
    content: "";
    position: absolute;
    top: 8px;
    z-index: 2;
    left: -27px;
    background: #44d7b6;
    margin-left: 2px;
    margin-top: 2px;
    width: 12px;
    height: 12px;
    border-radius: 6px;
    -webkit-transform: scale(.5);
    transform: scale(.5);
}

.diary-item .diary-item-content {
    margin: 4px 0 16px 24px;
    padding: 16px;
    border-radius: 8px;
    background: var(--blockquote-bg);
}

.link-card {
    border-radius: 0 0 29px 5px;
    overflow: hidden;
}

.link-card .link-info {
    border-radius: 5px 5px 0 0;
    background-image: linear-gradient(to top, #48c6ef 0%, #6f86d6 100%);
    padding-left: 32px;
    display: flex;
    align-items: center;
    -webkit-line-clamp: 1;
    height: 70px;
}

.link-card img {
    width: 50px;
    height: 50px;
    border-radius: 50%;
    border: 2px solid #fff;
    transition: all 0.2s;
}

.link-card .desc {
    padding: 8px 20px;
    height: 42px;
    -webkit-line-clamp: 1;
    background: rgba(240, 235, 246, 0.4);
}

.link-card span {
    color: #fff;
    margin-left: 14px;
    border-bottom: 2px solid;
    font-size: 1rem;
    font-weight: bold;
}

.link-container {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
}

.title-list {
    max-height: 100vh;
    position: relative;
}

.by_copyright {
    position: relative;
    margin: 10px 10px 10px;
    padding: 10px 16px;
    border: 1px solid var(--light-grey);
    -webkit-transition: -webkit-box-shadow .3s ease-in-out;
    transition: box-shadow .3s ease-in-out;
    transition: box-shadow .3s ease-in-out, -webkit-box-shadow .3s ease-in-out;
    border-radius: 6px;
}

.by_copyright_meta {
    color: #49b1f5;
    font-weight: 700;
}

.by_post_tag_share .tag_list {
    display: inline-block;
}

.by_post_tag_share .tag_list a {
    display: inline-block;
    margin: 0 8px 0 0;
    padding: 0 12px 0 12px;
    width: -moz-fit-content;
    width: fit-content;
    border: 1px solid var(--tab-botton-bg);
    border-radius: 20px;
    color: var(--headline-presudo);
    font-size: .85em;
    background: var(--tab-botton-bg);
    position: relative;
}

.share_list {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-align: center;
    -ms-flex-align: center;
    align-items: center;
}

.share_list a {
    margin-left: 5px;
}

.by_post_tag_share {
    display: -webkit-box;
    display: -ms-flexbox;
    display: flex;
    -webkit-box-pack: justify;
    -ms-flex-pack: justify;
    justify-content: space-between;
    -ms-flex-wrap: wrap;
    flex-wrap: wrap;
    margin-left: 5px;
    margin-right: 5px;
}


@media screen and (max-width: 860px) {
    .index-content-aside {
        width: 100%;
        padding-left: 0;
        margin-top: 15px;
    }

    .index-content-container {
        width: 100%;
    }

    .article-img-cover {
        -webkit-box-ordinal-group: 2;
        order: 1;
        width: 100%;
        height: 230px;
        border-radius: 25px 25px 0 0;
        overflow: hidden;
    }

    .article-img-cover .el-image {
        width: 100%;
        height: 230px;
        border-radius: 25px 25px 0 0;
    }

    .article-info {
        -webkit-box-ordinal-group: 3;
        order: 2;
        padding: 1rem 1rem 1.5rem;
        width: 100%;
    }

    .index-content-item {
        -webkit-box-orient: vertical;
        -webkit-box-direction: normal;
        -ms-flex-direction: column;
        flex-direction: column;
        height: auto;
        display: flex;
    }

    .index-content-box {
        -webkit-box-orient: vertical;
        -webkit-box-direction: normal;
        -ms-flex-direction: column;
        flex-direction: column;
        padding: 20px 5px;
    }

    .link-container {
        grid-template-columns: repeat(1, 1fr);
    }

    #v-md .github-markdown-body {
        padding: 0;
    }

    .articles-container {
        padding: 1.5rem 0.5rem;
    }

    #back-to-top {
        right: 5px !important;
        width: 35px;
        height: 35px;
        border-radius: 5px;
        background-color: var(--right-button-color);
    }

}
</style>

