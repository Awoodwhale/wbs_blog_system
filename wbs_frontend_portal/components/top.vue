<template>
    <header class="top">
        <!--web端顶部-->
        <nav class="nav-action-bar" id="nav" @mouseenter="headerBarHover" @mouseleave="headerBarLeave">
            <div class="header-action-bar" id="header">
                <div @mouseenter="titleHover" @mouseleave="titleLeave">
                    <nuxt-link to="#" class="header-blog-title" id="blog-title">woodwhale</nuxt-link>
                </div>

                <div class="header-menus">
                    <div class="header-menu">
                        <div class="hvr-underline-from-left">
                            <a class="hvr-icon-pulse-shrink blog-page" @click.stop="showSearch"
                            >
                                <i
                                    class="fa fa-search hvr-icon" aria-hidden="true"></i> 搜索
                            </a>
                        </div>
                        <div class="hvr-underline-from-left">
                            <nuxt-link to="/" class="hvr-icon-pulse-shrink blog-page"
                            >
                                <i class="fa fa-home hvr-icon"
                                   aria-hidden="true"></i> 首页
                            </nuxt-link>
                        </div>
                        <div class="hvr-underline-from-left">
                            <nuxt-link to="/articles" class="hvr-icon-pulse-shrink blog-page"
                            >
                                <i
                                    class="fa fa-book hvr-icon" aria-hidden="true"></i> 文章
                            </nuxt-link>
                        </div>
                        <div class="hvr-underline-from-left">
                            <nuxt-link to="/categories" class="hvr-icon-pulse-shrink blog-page"
                            >
                                <i
                                    class="fa fa-server hvr-icon" aria-hidden="true"></i> 分类
                            </nuxt-link>
                        </div>
                        <div class="hvr-underline-from-left">
                            <nuxt-link to="/diary" class="hvr-icon-pulse-shrink blog-page"
                            >
                                <i
                                    class="fa fa-rocket hvr-icon" aria-hidden="true"></i> 说说
                            </nuxt-link>
                        </div>

                        <div class="hvr-underline-from-left">
                            <nuxt-link to="/link" class="hvr-icon-pulse-shrink blog-page"
                            >
                                <i
                                    class="fa fa-child hvr-icon" aria-hidden="true"></i> 友友
                            </nuxt-link>
                        </div>

                        <div class="hvr-underline-from-left" v-if="$store.state.userInfo.id === undefined">
                            <nuxt-link to="/login" class="hvr-icon-pulse-shrink blog-page"
                            >
                                <i
                                    class="fa fa-user hvr-icon" aria-hidden="true"></i> 登录
                            </nuxt-link>
                        </div>
                        <div class="hvr-underline-from-left" v-else-if="$store.state.userInfo.id !== undefined">
                            <nuxt-link to="/mine" class="hvr-icon-pulse-shrink blog-page"
                            >
                                <i
                                    class="fa fa-user hvr-icon" aria-hidden="true"></i> 设置
                            </nuxt-link>
                        </div>

                    </div>
                    <div class="header-menu-phone" @click="showSidebar">
                        <i class="fa fa-align-right" id="phone-menu-icon" aria-hidden="true"></i>
                    </div>
                </div>

            </div>
        </nav>

        <!--手机端显示-->
        <div id="header-sidbar" class="header-sidbar-no-show">
            <div class="header-sidebar-mask" @click="hideSidebar"></div>
            <div class="header-sidebar-menus" id="sidebar">
                <!-- 轮播管理员图片 -->
                <el-carousel v-if="$store.state.adminInfos !== undefined" indicator-position="outside"
                             trigger="click" :autoplay="false" class="side-carousel-container" :interval="5555">
                    <el-carousel-item v-for="(item) in $store.state.adminInfos" :key="item.id">
                        <div class="center">
                            <el-image fit="contain" class="user-avatar" :src="item.avatar">
                            </el-image>
                            <div class="user-name">{{ item.username }}</div>
                            <div class="user-desc">{{ item.sign }}</div>
                        </div>
                    </el-carousel-item>
                </el-carousel>
                <div class="sidebar-user-data">
                    <nuxt-link to="/articles" class="sidebar-user-data-item" @click.native="hideSidebar">
                        <div class="headline">文章</div>
                        <div class="length_num" v-if="$store.state.blogInfos.article !== undefined">
                            {{ $store.state.blogInfos.article }}
                        </div>
                    </nuxt-link>

                    <nuxt-link to="/categories" class="sidebar-user-data-item" @click.native="hideSidebar">
                        <div class="headline">分类</div>
                        <div class="length_num" v-if="$store.state.blogInfos.category !== undefined">
                            {{ $store.state.blogInfos.category }}
                        </div>
                    </nuxt-link>

                    <nuxt-link to="/diary" class="sidebar-user-data-item" @click.native="hideSidebar">
                        <div class="headline">说说</div>
                        <div class="length_num" v-if="$store.state.blogInfos.diary !== undefined">
                            {{ $store.state.blogInfos.diary }}
                        </div>
                    </nuxt-link>
                </div>
                <hr class="sidebar-hr">
                <ul class="sidebar-menus-items">
                    <li class="sidebar-menu-item-li">
                        <a @click.stop="hideSidebar();showSearch()">搜索</a>
                    </li>
                    <li class="sidebar-menu-item-li">
                        <nuxt-link to="/" @click.native="hideSidebar" class="hvr-icon-pulse-shrink">
                            首页
                        </nuxt-link>
                    </li>
                    <li class="sidebar-menu-item-li">
                        <nuxt-link to="/articles" @click.native="hideSidebar">文章</nuxt-link>
                    </li>
                    <li class="sidebar-menu-item-li">
                        <nuxt-link to="/categories" @click.native="hideSidebar">分类</nuxt-link>
                    </li>
                    <li class="sidebar-menu-item-li">
                        <nuxt-link to="/diary" @click.native="hideSidebar">说说</nuxt-link>
                    </li>

                    <li class="sidebar-menu-item-li">
                        <nuxt-link to="/link" @click.native="hideSidebar">友友</nuxt-link>
                    </li>
                    <li class="sidebar-menu-item-li" v-if="$store.state.userInfo.id === undefined">
                        <nuxt-link to="/login" @click.native="hideSidebar">登录</nuxt-link>
                    </li>
                    <li class="sidebar-menu-item-li" v-else>
                        <nuxt-link to="/mine" @click.native="hideSidebar">设置</nuxt-link>
                    </li>
                </ul>
            </div>
        </div>

        <!--搜索框-->
        <div id="search-content" class="js-search search-form search-form--modal">
            <div class="search-form__inner">
                <div>
                    <p class="micro mb-">输入后按回车搜索 ...</p>
                    <i class="fa fa-search"></i>
                    <input @keyup.enter="doSearch" id="search-keyword" class="text-input" type="search"
                           name="keyword" placeholder="Search" required="">
                </div>
            </div>

            <div class="hvr-bounce-out search_close" @click="showSearch"></div>
        </div>
    </header>

</template>

<script>
import {solrSearch, SUCCESS} from "@/assets/api/api";
import {showMessage, showNotify} from "@/assets/utils/utils";

export default {
    data() {
        return {
            isTop: true,
            isHover: false,
            preScrollTop: 0,
            isSidebarShow: false,
            isShowSearch: false,
            searchContent: "",
        }
    },
    methods: {
        // 绑定滚动条
        bindScroll() {
            document.getElementById("indexScroll").addEventListener("scroll", this.listenHeaderBarShow, true)
        },
        // 取消绑定滚动条监听
        unbindScroll() {
            document.getElementById("indexScroll").removeEventListener("scroll", this.listenHeaderBarShow, true)
        },
        // 监听头部框的背景显示
        listenHeaderBarShow() {
            const nav = document.getElementById("nav")
            const header = document.getElementById("header")
            const title = document.getElementById("blog-title")
            const pages = document.getElementsByClassName("blog-page")
            const icon = document.getElementById("phone-menu-icon")
            const scroll = this.$parent.$refs
            if (scroll !== undefined) {
                const height2Top = scroll.wrap.scrollTop
                // 判断是否是向下滑动
                if (height2Top > this.preScrollTop &&
                    height2Top >= 60) {
                    // 隐藏headerbar
                    nav.style.height, header.style.height = '0'
                    nav.style.opacity = '0'
                    document.getElementById("right-title-list").style.top = '20px'
                } else if (height2Top < this.preScrollTop) {
                    // 显示headerbar
                    nav.style.height, header.style.height = "60px"
                    nav.style.opacity = '1'
                    document.getElementById("right-title-list").style.top = '70px'
                }
                this.preScrollTop = height2Top
                if (height2Top === 0) {
                    this.isTop = true
                    nav.className = "nav-action-bar"
                    title.style.color = "#eee"
                    icon.style.color = "#eee"
                    for (let i = 0; i < pages.length; i++) {
                        pages[i].style.color = "#fff"
                    }
                } else {
                    if (this.isTop === true) {
                        this.isTop = false
                    }
                    if (nav.className === "nav-action-bar") {
                        nav.className = "nav-action-bar-active"
                        icon.style.color = "#4c4948"
                        title.style.color = "#4c4948"
                        for (let i = 0; i < pages.length; i++) {
                            pages[i].style.color = "#4c4948"
                        }
                    }

                }
            }
        },
        // 监听鼠标是否移动到bar上
        headerBarHover() {
            this.isHover = true
            const nav = document.getElementById("nav")
            const title = document.getElementById("blog-title")
            const pages = document.getElementsByClassName("blog-page")
            const icon = document.getElementById("phone-menu-icon")
            nav.className = this.isTop ? "nav-action-bar-active" : "nav-action-bar-hover"
            icon.style.color = "#4c4948"
            title.style.color = "#4c4948"
            for (let i = 0; i < pages.length; i++) {
                pages[i].style.color = "#4c4948"
            }
        },
        // 监听鼠标离开bar
        headerBarLeave() {
            this.isHover = false
            const nav = document.getElementById("nav")
            const title = document.getElementById("blog-title")
            const pages = document.getElementsByClassName("blog-page")
            const icon = document.getElementById("phone-menu-icon")
            // 如果在顶部，那么离开要透明
            if (this.isTop) {
                nav.className = "nav-action-bar"
                title.style.color = "#eee"
                icon.style.color = "#eee"
                for (let i = 0; i < pages.length; i++) {
                    pages[i].style.color = "#fff"
                }
            } else {
                // 如果不在顶部，要白色模糊
                nav.className = "nav-action-bar-active"
                title.style.color = "#4c4948"
                icon.style.color = "#4c4948"
                for (let i = 0; i < pages.length; i++) {
                    pages[i].style.color = "#4c4948"
                }
            }
        },
        // headerbar触碰反馈
        titleHover() {
            document.getElementById('blog-title').style.color = '#409EFF'
        },
        // headerbar离开反馈
        titleLeave() {
            document.getElementById('blog-title').style.color = this.isTop ?
                this.isHover ? '#4c4948' : '#eee' : '#4c4948'
        },
        // 点击显示siderBar
        showSidebar() {
            const container = document.getElementById("header-sidbar")
            container.className = "header-sidbar"
            const sidebar = document.getElementById("sidebar")
            sidebar.style.width = "300px"
        },
        // 点击隐藏siderBar
        hideSidebar() {
            const container = document.getElementById("header-sidbar")
            container.className = "header-sidbar-no-show"
            const sidebar = document.getElementById("sidebar")
            sidebar.style.width = '0'
        },
        // 显示search
        showSearch() {
            this.$nextTick(() => {
                const list = document.getElementById("search-content").classList
                if (this.isShowSearch) {
                    list.remove("is-visible")
                } else {
                    list.add("is-visible")
                }
                this.isShowSearch = !this.isShowSearch
            })
        },
        // 搜索功能
        doSearch() {
            const cnt = document.getElementById("search-keyword").value
            if (cnt !== "" && cnt !== undefined && cnt.length > 0) {
                this.showSearch()
                // 跳转到search页面进行搜索，就不在这里进行搜索查询
                this.$router.push({name: 'search-keyword', params: {keyword: cnt}})
            } else {
                showMessage("请输入搜索内容!", "warning")
            }
        }
    },
    mounted() {
        // mount的时候绑定
        this.bindScroll()

    },
    beforeDestroy() {
        // 销毁之前解除绑定
        this.unbindScroll()
    }
}
</script>

<style scoped>
.nav-action-bar {
    position: absolute;
    top: 0;
    z-index: 91;
    width: 100%;
    transition: all 0.5s;
}

.nav-action-bar-active {
    position: absolute;
    top: 0;
    z-index: 91;
    width: 100%;
    transition: all 0.5s;
    background: rgba(255, 255, 255, 0.8);
    box-shadow: 0 5px 6px -5px rgb(133 133 133 / 60%);
}

.nav-action-bar-hover {
    position: absolute;
    top: 0;
    z-index: 91;
    width: 100%;
    transition: all 0.5s;
    background: rgba(255, 255, 255, 1);
    box-shadow: 0 5px 6px -5px rgb(133 133 133 / 60%);
}

.header-action-bar {
    padding: 0 36px;
    display: flex;
    align-items: center;
    height: 60px;
    transition: all 0.5s;
    overflow: hidden;
}

.header-blog-title {
    font-size: 1.3em;
    text-shadow: 2px 2px 4px rgb(0 0 0 / 15%);
    font-weight: bold;
    color: #eee;
    transition: color .2s ease-out, border .2s ease-out, opacity .2s ease-out;
    outline: 0;
    background-color: transparent;
    text-decoration: none;
    font-family: 'Noto Serif SC', 'Source Han Serif SC', 'Source Han Serif', source-han-serif-sc, 'PT Serif', 'SongTi SC', 'MicroSoft Yahei', Georgia, serif;
}

.header-blog-title:hover {
    color: #409EFF;
}

.header-menu {
    margin-left: auto;
}

.header-menu div {
    padding: 8px 0.6rem;
}

.header-menu a {
    text-decoration: none;
    color: #ffff;
}

.header-menu-phone {
    display: none;
    cursor: pointer;
}

.header-menus {
    margin-left: auto;
}

.header-sidbar {
    visibility: visible;
}

.header-sidbar-no-show {
    visibility: hidden;
}

@keyframes fadeIn {
    0% {
        opacity: 0; /*初始状态 透明度为0*/
    }
    50% {
        opacity: .5; /*中间状态 透明度为0.5*/
    }
    100% {
        opacity: 1; /*结尾状态 透明度为1*/
    }
}

@keyframes showOn {
    0% {
        opacity: 1; /*初始状态 透明度为0*/
    }
    50% {
        opacity: .5; /*中间状态 透明度为0.5*/
    }
    100% {
        opacity: 0; /*结尾状态 透明度为1*/
    }
}

.header-sidebar-mask {
    display: block;
    position: fixed;
    top: 0;
    z-index: 102;
    overflow-x: hidden;
    overflow-y: auto;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.8);
    animation-name: showOn; /*动画名称*/
    animation-duration: 0.5s; /*动画持续时间*/
    animation-iteration-count: 1; /*动画次数*/
    animation-delay: 0s; /*延迟时间*/

}

.header-sidbar .header-sidebar-mask {
    animation-name: fadeIn; /*动画名称*/
    animation-duration: 0.5s; /*动画持续时间*/
    animation-iteration-count: 1; /*动画次数*/
    animation-delay: 0s; /*延迟时间*/
}

.header-sidebar-menus {
    transform: translate3d(-100%, 0, 0);
    position: fixed;
    top: 0;
    right: -300px;
    z-index: 103;
    overflow-x: hidden;
    overflow-y: auto;
    width: 0;
    height: 100%;
    background: var(--sidebar-bg);
    transition: all 0.5s;
}

.sidebar-user-avatar {
    animation: sidebarItem 0.2s;
    margin: 20px auto;
    overflow: hidden;
    width: 110px;
    height: 110px;
    border-radius: 70px;
    display: block;
}

.sidebar-user-avatar img {
    max-width: 100%;
}

.sidebar-user-data {
    animation: sidebarItem 0.4s;
    display: table;
    padding: 0 8px;
    width: 100%;
    table-layout: fixed;
    text-align: center;
}

.sidebar-user-data-item {
    display: table-cell;
}

.headline {
    color: var(--font-color);
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.length_num {
    color: var(--text-highlight-color);
    font-size: 1.2rem;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.sidebar-hr {
    animation: sidebarItem 0.6s;
    margin: 20px auto;
    position: relative;
    border: 2px dashed var(--hr-border);
    width: calc(100% - 4px);
    box-sizing: content-box;
    height: 0;
    overflow: visible;
}

.sidebar-hr:before {
    font-family: "by-font";
    position: absolute;
    top: -10px;
    left: 5%;
    z-index: 1;
    content: "";
    font-size: 20px;
    line-height: 1;
    transition: all 1s ease-in-out;
    display: inline-block;
    font-weight: 600;
    font-style: normal;
    font-variant: normal;
    text-rendering: auto;
    -webkit-font-smoothing: antialiased;
}

.sidebar-menus-items {
    animation: sidebarItem 0.8s;
    padding: 0 10px 40px;
    margin: 0;
}

.sidebar-menus-items ul,
.sidebar-menus-items li {
    list-style: none;
}

.sidebar-menu-item-li {
    position: relative;
    display: block;
    padding: 8px 30px 8px 22px;
    text-align: -webkit-match-parent;
}

.sidebar-menu-item-li a {
    width: 100%;
    height: auto;
    display: inline-block;
    color: var(--font-color);
    font-size: 1rem;
}

.search-form.is-visible {
    visibility: visible;
    opacity: .90;
    -webkit-animation: elastic .5s;
    animation: elastic .5s;
    background-repeat: no-repeat;
    background-position: bottom right;
}

.search-form--modal {
    transition: visibility .25s ease, opacity .25s ease;
    overflow: hidden;
    z-index: 2000;
    position: fixed;
    top: 0;
    right: 0;
    left: 0;
    bottom: 0;
    background: #fff;
    visibility: hidden;
    opacity: 0;
}

.search-form--modal .search-form__inner {
    max-width: 640px;
    padding: 0 20px;
    margin: auto;
    text-align: left;
    position: absolute;
    width: 100%;
    left: 0;
    right: 0;
    height: 285px;
    top: 0;
    bottom: 0;
}

.search-form .search_close {
    position: absolute;
    width: 35px;
    height: 35px;
    background: 0 0;
    top: 20px;
    right: 15px;
    cursor: pointer;
}

.search-form div {
    position: relative;
}

.search-form--modal .search-form__inner p {
    padding-left: 24px;
}

.search-form i {
    /*font-size: 32px;*/
    font-size: 2rem;
    line-height: 1;
    color: #ddd;
    position: absolute;
    bottom: 15px;
    margin-top: -16px;
    left: 16px;
}

.search_close:after, .search_close:before {
    background-color: #222;
    position: absolute;
    content: "";
    width: 30px;
    height: 2px;
    top: 17px;
    left: 2px;
}

.search_close:after {
    transform: rotate(45deg);
    -webkit-transform: rotate(45deg);
}

.search_close:before {
    transform: rotate(-45deg);
    -webkit-transform: rotate(-45deg);
}

.search-form input {
    /*font-size: 24px;*/
    font-size: 1.5rem;
    background: #fff;
    padding: 12px 24px 12px 64px;
    width: 100%;
    outline: 0;
    border-radius: 50px;
    box-sizing: border-box;
    color: #666;
    border: 1px solid #ccc;
    line-height: 1.5;
    margin: 0;
    appearance: auto;
    writing-mode: horizontal-tb !important;
    transition: background 1s;

}

.micro {
    display: block;
    margin-block-start: 1em;
    margin-block-end: 1em;
    margin-inline-start: 0;
    margin-inline-end: 0;
    color: #404040;
    font-size: 18px;
    line-height: 1.5;
    font-family: var(--base-font-family)
}

#search-content {
    transition: .4s ease all
}

@media screen and (max-width: 860px) {

    /* 不显示菜单 */
    .header-menu {
        display: none;
    }

    #header {
        padding: 0 16px;
    }

    .header-menu-phone {
        display: inline-block;
        padding: 2px 0 0 6px;
        vertical-align: top;
        flex: 1;
    }

    .header-menu-phone i {
        font-size: 1.5rem;
        color: var(--light-grey);
    }

    .side-carousel-container {
        border-radius: 25px;
        padding: 0;

    }

    .side-carousel-container {
        height: 235px;
    }

    .side-carousel-container .el-carousel__container {
        height: 85%;
    }

    /deep/ .header-sidebar-menus .center {
        width: 100%;
        height: 100%;
        text-align: center;
        margin-top: 15px;
    }


    /deep/ .user-avatar {
        overflow: hidden;
        margin-left: auto;
        width: 110px;
        height: 110px;
        border-radius: 70px;
    }

}
</style>
