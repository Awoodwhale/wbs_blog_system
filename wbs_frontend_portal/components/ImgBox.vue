<template>
    <div>
        <figure id="header-background" class="header-background">
            <div class="header-focus-info" v-if="$route.path === '/'">
                <!--炫酷的悬浮-->
                <h1 class="hvr-bob center-text glitch header-foucs-title" :data-text="$store.state
                    .h1WithSign.h1">{{
                        $store.state
                            .h1WithSign.h1
                    }}</h1>
                <div class="header-foucs-info-main">
                    <p>
                        <client-only><i class="fa fa-quote-left"/></client-only>
                        <client-only>
                            <vue-typer :text='[$store.state.h1WithSign.sign]' :pre-type-delay='1500'
                                       :type-delay='200'
                                       :pre-erase-delay='1500' :erase-delay='65' erase-style='backspace'
                                       caret-animation='smooth'>
                            </vue-typer>
                        </client-only>
                        <client-only><i class="fa fa-quote-right"/></client-only>
                    </p>
                    <!--其他媒体信息-->
                    <div class="top-social_v2" v-if="$store.state.socialMedias !== undefined">
                        <li id="bg-pre">
                            <img class="flipx"
                                 src="/next-b.svg">
                        </li>
                        <li v-if="$store.state.socialMedias.github !== undefined && $store.state.socialMedias.github !== ''">
                            <a :href="$store.state.socialMedias.github" target="_blank" class="social-github i18n"
                               data-iname="icon_alt.github" data-iattr="title" title="Github">
                                <img
                                    src="/github.png">
                            </a>
                        </li>
                        <li v-if="$store.state.socialMedias.bilibili !== undefined && $store.state.socialMedias.bilibili !== ''">
                            <a :href="$store.state.socialMedias.bilibili" target="_blank" class="social-bili i18n"
                               data-iname="icon_alt.bili" data-iattr="title" title="哔哩哔哩">
                                <img
                                    src="/bilibili.png">
                            </a>
                        </li>
                        <li v-if="$store.state.socialMedias.music163 !== undefined && $store.state.socialMedias.music163 !== ''">
                            <a :href="$store.state.socialMedias.music163" target="_blank"
                               class="social-wangyiyun i18n" data-iname="icon_alt.cloudmusic" data-iattr="title"
                               title="网易云音乐">
                                <img
                                    src="/music163.png">
                            </a>
                        </li>
                        <li v-if="$store.state.socialMedias.csdn !== undefined && $store.state.socialMedias.csdn !== ''">
                            <a :href="$store.state.socialMedias.csdn" target="_blank"
                               class="i18n" data-iname="icon_alt.csdn" data-iattr="title" title="CSDN">
                                <img
                                    src="/csdn.png">
                            </a>
                        </li>
                        <li v-if="$store.state.socialMedias.mail !== undefined && $store.state.socialMedias.mail !== ''">
                            <a @click.stop="copyEmail($store.state.socialMedias.mail)" title="邮箱">
                                <img style="cursor: pointer;"
                                     src="/email.svg">
                            </a>
                        </li>
                        <li id="bg-next">
                            <img src="/next-b.svg">
                        </li>
                    </div>
                </div>
            </div>

            <!--文章标题显示-->
            <div class="types-title">
                <h1 class="center-text glitch" id="pages-title"></h1>
                <div class="by_post_meta center" v-if="$route.name === 'articles-id'"
                     style="color: var(--light-grey) !important;">
                    <div class="by_post_wrap">
                        <div
                            style="display: -ms-inline-flexbox;display: inline-flex;-webkit-box-align: center;-ms-flex-align: center;align-items: center;">
                            <svg style="width: auto;height: 16px;margin-right: 5px;" t="1642927072061" class="icon"
                                 viewBox="0 0 1024 1024" version="1.1"
                                 xmlns="http://www.w3.org/2000/svg" p-id="5358" width="200" height="200">
                                <path
                                    d="M863.75416667 953.64444445H183.12916667C149.37916667 953.64444445 121.25416667 925.51944445 121.25416667 891.76944445V211.14444445c0-33.75 28.125-61.875 61.875-61.875h61.875v33.75c0 50.625 39.375 95.625 95.625 95.625 50.625 0 95.625-39.375 95.625-95.625v-33.75h185.625v33.75c0 50.625 39.375 95.625 95.625 95.625s95.625-39.375 95.625-95.625v-33.75h61.875c33.75 0 61.875 28.125 61.875 61.875v680.625c-11.25 33.75-33.75 61.875-73.125 61.875z m0-590.625H183.12916667v528.75h680.625V363.01944445zM340.62916667 520.51944445H245.00416667V424.89444445h95.625v95.625z m0 151.875H245.00416667V582.39444445h95.625V672.39444445z m0 157.5H245.00416667v-95.625h95.625v95.625z m151.875-309.375H402.50416667V424.89444445h95.625v95.625z m0 151.875H402.50416667V582.39444445h95.625V672.39444445z m0 157.5H402.50416667v-95.625h95.625v95.625z m157.5-309.375H554.37916667V424.89444445h95.625v95.625z m0 151.875H554.37916667V582.39444445h95.625V672.39444445z m0 157.5H554.37916667v-95.625h95.625v95.625z m151.875-309.375h-95.625V424.89444445h95.625v95.625z m0 151.875h-95.625V582.39444445h95.625V672.39444445z m0 157.5h-95.625v-95.625h95.625v95.625zM711.87916667 239.26944445c-33.75 0-61.875-28.125-61.875-61.875V115.51944445c0-33.75 28.125-61.875 61.875-61.875 33.75 0 61.875 28.125 61.875 61.875v61.875c0 33.75-28.125 61.875-61.875 61.875z m-376.875 0c-33.75 0-61.875-28.125-61.875-61.875V115.51944445C278.75416667 81.76944445 301.25416667 53.64444445 335.00416667 53.64444445c33.75 0 61.875 28.125 61.875 61.875v61.875c0 33.75-28.125 61.875-61.875 61.875z m0 0"
                                    fill="#ffffff" p-id="5359"></path>
                            </svg>
                            <time datetime="2022-04-11" title="2022-04-11">
                                发布于 {{ $store.state.articleData.createTime.split(" ")[0] }}
                            </time>
                            <span class="separator"> |</span>
                        </div>
                        <div
                            style="display: -ms-inline-flexbox;display: inline-flex;-webkit-box-align: center;-ms-flex-align: center;align-items: center;">
                            <svg style="width: auto;height: 16px;margin-right: 5px;" t="1642927036992" class="icon"
                                 viewBox="0 0 1024 1024" version="1.1"
                                 xmlns="http://www.w3.org/2000/svg" p-id="4986" width="200" height="200">
                                <path
                                    d="M481.28 801.792h61.44v77.824h-61.44zM798.72 481.28h77.824v61.44H798.72zM136.192 481.28h77.824v61.44h-77.824z"
                                    fill="#ffffff" p-id="4987"></path>
                                <path
                                    d="M512 955.392C267.264 955.392 68.608 756.736 68.608 512c0-125.952 54.272-246.784 147.456-330.752l54.272 61.44C193.536 311.296 150.528 409.6 150.528 512c0 199.68 162.816 361.472 361.472 361.472S873.472 711.68 873.472 512C873.472 274.432 675.84 150.528 481.28 150.528v-81.92c235.52 0 474.112 152.576 474.112 443.392 0 244.736-198.656 443.392-443.392 443.392z"
                                    fill="#ffffff" p-id="4988"></path>
                                <path d="M481.28 75.776h81.92v443.392h-81.92z" fill="#ffffff" p-id="4989"></path>
                                <path d="M499.67104 468.7872l63.6928 51.52768-108.83072 134.5536-63.6928-51.52768z"
                                      fill="#ffffff" p-id="4990"></path>
                            </svg>
                            <time>更新于 {{ $store.state.articleData.updateTime.split(" ")[0] }}
                            </time>
                            <span class="separator"> |</span>
                        </div>

                        <div class="ato"
                             style="display: -ms-inline-flexbox;display: inline-flex;-webkit-box-align: center;-ms-flex-align: center;align-items: center;">
                            <svg style="width: auto;height: 16px;margin-right: 5px;" t="1642927001676" class="icon"
                                 viewBox="0 0 1039 1024" version="1.1"
                                 xmlns="http://www.w3.org/2000/svg" p-id="7723" width="200" height="200">
                                <path
                                    d="M327.834275 482.109228h-115.706215c-84.851224 0-154.274953-69.423729-154.274953-154.274953v-115.706215c0-84.851224 69.423729-154.274953 154.274953-154.274953h115.706215c84.851224 0 154.274953 69.423729 154.274953 154.274953v115.706215c0 84.851224-69.423729 154.274953-154.274953 154.274953z m-115.706215-347.118644c-42.425612 0-77.137476 34.711864-77.137476 77.137476v115.706215c0 42.425612 34.711864 77.137476 77.137476 77.137476h115.706215c42.425612 0 77.137476-34.711864 77.137476-77.137476v-115.706215c0-42.425612-34.711864-77.137476-77.137476-77.137476h-115.706215zM327.834275 983.502825h-115.706215c-84.851224 0-154.274953-69.423729-154.274953-154.274953v-115.706215c0-84.851224 69.423729-154.274953 154.274953-154.274953h115.706215c84.851224 0 154.274953 69.423729 154.274953 154.274953v115.706215c0 84.851224-69.423729 154.274953-154.274953 154.274953z m-115.706215-347.118644c-42.425612 0-77.137476 34.711864-77.137476 77.137476v115.706215c0 42.425612 34.711864 77.137476 77.137476 77.137476h115.706215c42.425612 0 77.137476-34.711864 77.137476-77.137476v-115.706215c0-42.425612-34.711864-77.137476-77.137476-77.137476h-115.706215zM829.227872 983.502825h-115.706215c-84.851224 0-154.274953-69.423729-154.274953-154.274953v-115.706215c0-84.851224 69.423729-154.274953 154.274953-154.274953h115.706215c84.851224 0 154.274953 69.423729 154.274953 154.274953v115.706215c0 84.851224-69.423729 154.274953-154.274953 154.274953z m-115.706215-347.118644c-42.425612 0-77.137476 34.711864-77.137476 77.137476v115.706215c0 42.425612 34.711864 77.137476 77.137476 77.137476h115.706215c42.425612 0 77.137476-34.711864 77.137476-77.137476v-115.706215c0-42.425612-34.711864-77.137476-77.137476-77.137476h-115.706215z"
                                    fill="#ffffff" p-id="7724"></path>
                                <path
                                    d="M761.73258 482.109228c-36.640301 0-75.20904-13.499058-104.135593-42.425612l-75.20904-75.20904c-55.92467-55.92467-57.853107-148.489642 0-206.342749l77.137477-75.20904c57.853107-55.92467 150.418079-55.92467 206.342749 0l75.20904 75.20904c55.92467 55.92467 57.853107 148.489642 1.928437 204.414312l-1.928437 1.928437-75.20904 75.20904c-28.926554 28.926554-67.495292 42.425612-104.135593 42.425612z m-125.348399-268.052731c-25.06968 25.06968-25.06968 69.423729 1.928437 96.421846l75.209039 75.209039c26.998117 26.998117 71.352166 26.998117 98.350283 0l77.137476-77.137476c25.06968-26.998117 25.06968-69.423729 0-96.421846l-75.209039-75.209039c-26.998117-26.998117-71.352166-26.998117-98.350283 0l-79.065913 77.137476z"
                                    fill="#ffffff" p-id="7725"></path>
                            </svg>
                            <nuxt-link
                                v-if="$store.state.articleData !== undefined"
                                style="color: var(--light-grey) !important;"
                                :to="{name:'categories-categoryId',
                                params:{name:$store.state.categoryMap[$store.state.articleData.categoryId],
                                categoryId:$store.state.articleData.categoryId}}">
                                {{ $store.state.categoryMap[$store.state.articleData.categoryId] }}
                            </nuxt-link>
                        </div>
                    </div>
                    <div class="by_post_wrap">
                        <div
                            style="display: -ms-inline-flexbox;display: inline-flex;-webkit-box-align: center;-ms-flex-align: center;align-items: center;">
                            <svg style="width: auto;height: 16px;margin-right: 5px;" t="1642926930137" class="icon"
                                 viewBox="0 0 1024 1024" version="1.1"
                                 xmlns="http://www.w3.org/2000/svg" p-id="6088" width="200" height="200">
                                <path
                                    d="M418 206.99V502c0 57.35 46.65 104 104 104h295.01C803.22 678.61 766 745 710.47 795.15c-30.69 27.72-65.67 49.4-103.95 64.42C566.91 875.12 525.01 883 482 883c-46.06 0-90.7-9.01-132.7-26.77-40.6-17.17-77.07-41.77-108.41-73.11-31.34-31.34-55.94-67.82-73.11-108.41C150.01 632.7 141 588.06 141 542c0-43.01 7.88-84.9 23.43-124.52 15.03-38.29 36.7-73.26 64.42-103.95C279 258 345.39 220.78 418 206.99m24.24-67.85c-1.43 0-2.86 0.08-4.32 0.23C234.98 161.33 77 333.22 77 542c0 223.66 181.35 405 405 405 0.01 0-0.01 0 0 0 208.78 0 380.66-157.98 402.63-360.92 2.55-23.57-16.09-44.08-39.79-44.08H522c-22.09 0-40-17.91-40-40V179.16c0-22.24-18.06-40.02-39.76-40.02z"
                                    p-id="6089" fill="#ffffff"></path>
                                <path
                                    d="M606 146.98c29.4 5.57 57.84 14.99 84.86 28.12 34.21 16.63 65.25 38.76 92.27 65.77 27.02 27.01 49.14 58.06 65.77 92.27A337.507 337.507 0 0 1 877.02 418H606V146.98m-24.09-67.82c-21.76 0-39.91 17.71-39.91 40.02V442c0 22.09 17.91 40 40 40h322.83c23.79 0 42.35-20.64 39.78-44.29C924.12 249.42 774.58 99.88 586.29 79.39c-1.47-0.16-2.93-0.23-4.38-0.23z"
                                    p-id="6090" fill="#ffffff"></path>
                            </svg>
                            <span>字数统计：{{ $store.state.articleData.content.length }}</span>
                            <span class="separator"> |</span>
                        </div>
                        <div
                            style="display: -ms-inline-flexbox;display: inline-flex;-webkit-box-align: center;-ms-flex-align: center;align-items: center;">
                            <svg style="width: auto;height: 16px;margin-right: 5px;" t="1642926729862" class="icon"
                                 viewBox="0 0 1024 1024" version="1.1"
                                 xmlns="http://www.w3.org/2000/svg" p-id="2181" width="200" height="200">
                                <path
                                    d="M854.72 138.666667v64h-106.581333v189.184l-179.264 118.464 179.264 118.464v207.146666h106.581333v64h-682.666667v-64h106.56v-207.146666l179.285334-118.464-179.285334-118.464V202.666667H172.053333v-64h682.666667zM513.365333 550.357333l-170.752 112.832v172.736h341.525334l-0.021334-172.736-170.752-112.832zM620.245333 704l0.170667 64-213.866667 0.533333-0.170666-64L620.245333 704zM684.16 202.666667h-341.546667l0.021334 154.773333 170.752 112.832 170.752-112.832V202.666667z"
                                    fill="#ffffff" p-id="2182"></path>
                            </svg>
                            <span>阅读时长：{{ Math.ceil(parseInt($store.state.articleData.content.length) / 500) }}分</span>
                            <span class="separator"> |</span>
                        </div>

                        <div
                            style="display: -ms-inline-flexbox;display: inline-flex;-webkit-box-align: center;-ms-flex-align: center;align-items: center;">
                            <svg style="width: auto;height: 16px;margin-right: 5px;" t="1642926729862" class="icon"
                                 viewBox="0 0 1024 1024" version="1.1"
                                 xmlns="http://www.w3.org/2000/svg" p-id="2181" width="200" height="200">
                                <path
                                    d="M854.72 138.666667v64h-106.581333v189.184l-179.264 118.464 179.264 118.464v207.146666h106.581333v64h-682.666667v-64h106.56v-207.146666l179.285334-118.464-179.285334-118.464V202.666667H172.053333v-64h682.666667zM513.365333 550.357333l-170.752 112.832v172.736h341.525334l-0.021334-172.736-170.752-112.832zM620.245333 704l0.170667 64-213.866667 0.533333-0.170666-64L620.245333 704zM684.16 202.666667h-341.546667l0.021334 154.773333 170.752 112.832 170.752-112.832V202.666667z"
                                    fill="#ffffff" p-id="2182"></path>
                            </svg>
                            <span>作者：{{ $store.state.articleData.user.username }}</span>
                        </div>

                    </div>
                </div>
            </div>
        </figure>

        <!--下标-->
        <div class="headertop-down" v-if="$route.path === '/'">
            <div @click="downClick()" style="margin:auto; cursor: pointer;" class="hvr-icon-drop"><i
                class="fa fa-chevron-down hvr-icon" aria-hidden="true"></i></div>
        </div>
    </div>
</template>

<script>
import {showMessage} from "@/assets/utils/utils";

export default {
    methods: {
        // 首页图片高度自动适应
        autoImgHeight() {
            document.getElementById("header-background").style.height = document.documentElement.clientHeight + "px";
        },
        // 绑定页面大小修改
        bindWindowsResize() {
            window.addEventListener("resize", this.autoImgHeight, true)
        },
        // 取消绑定
        unbindWindowsResize() {
            window.removeEventListener("resize", this.autoImgHeight, true)
        },
        // 滚动到下方
        downClick() {
            document.getElementById("index-content").scrollIntoView({behavior: 'smooth'});
        },
        // 复制邮箱
        copyEmail(copyText) {
            let input = document.createElement("input") // 直接构建input
            input.value = copyText; // 设置内容
            document.body.appendChild(input); // 添加临时实例
            input.select(); // 选择实例内容
            try {
                document.execCommand("Copy")
                showMessage("复制邮箱 " + copyText + " 成功!")
            } catch (e) {
                showMessage("复制邮箱失败,请检查浏览器权限!", 'error')
            } finally {
                document.body.removeChild(input); // 删除临时实例
            }
        }
    },
    mounted() {
        if (this.$route.path === '/') {
            this.bindWindowsResize()
        }
    },
    beforeDestroy() {
        if (this.$route.path === '/') {
            this.unbindWindowsResize()
        }
    },
    beforeUpdate() {
        if (this.$route.path === '/') {
            this.unbindWindowsResize()
        }
    }
}
</script>

<style>
.separator {
    margin: 0 0.3rem;
}

#header-background {
    width: 100%;
    height: 400px;
    margin: 0;
    background-repeat: no-repeat;
    background-attachment: fixed;
    background-size: cover;
    z-index: -1;
    background-position: center;
    background-image: url("/background.jpg");
    transition: all 0.5s;
}

.index-header #header-background {
    height: 100vh;
}

.header-focus-info {
    display: none;
}

.types-title {
    position: relative;
    max-width: 800px;
    padding: 0 10px;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    -webkit-transform: translate(-50%, -50%);
    text-align: center;
    z-index: 89;
    transition: .4s ease all
}

.types-title h1 {
    font-size: 2.85em;
    color: var(--white);
}

.index-header .header-focus-info {
    display: block;
    position: relative;
    max-width: 800px;
    padding: 0 10px;
    top: 45%;
    left: 50%;
    transform: translate(-50%, -50%);
    -webkit-transform: translate(-50%, -50%);
    text-align: center;
    z-index: 89;
    transition: .4s ease all
}

.header-focus-info img {
    width: 130px;
    height: auto;
    padding: 2px;
    border-radius: 100%
}

.header-foucs-info-main,
.header-focus-info .header-tou img,
.top-social img,
.center-text {
    transition: color 1s;
    transition: background 1s;
}

.center-text {
    margin: auto;
    font-size: 80px;
}

.header-foucs-title {
    font-size: 4.85em;
    position: relative;
    color: #fff;
    mix-blend-mode: lighten;
    overflow: hidden;
    white-space: nowrap;

}

.header-foucs-title + .header-foucs-info-main:before {
    bottom: 94px;
    content: "";
}

.header-foucs-title:before,
.header-foucs-title:after {
    content: attr(data-text);
    position: absolute;
    top: 0;
    width: 100%;
    background: rgba(0, 0, 0, 0);
    clip: rect(0, 0, 0, 0)
}

.header-foucs-info-main {
    width: 60%;
    margin: auto;
    font-size: 16px;
    color: #eaeadf;
    background: rgba(0, 0, 0, .5);
    padding: 15px;
    margin-top: 22px;
    letter-spacing: 0;
    line-height: 30px;
    border-radius: 10px;
    box-sizing: initial;
    white-space: nowrap;
}

.header-foucs-info-main:before {
    position: absolute;
    left: 30%;
    margin-left: -30px;
    border-width: 15px;
    border-style: solid;
    border-color: transparent transparent rgba(0, 0, 0, .5) transparent;
}

.header-foucs-info-main p {
    height: 34px;
    padding: 2px;
    font-size: 20px;
    margin: 0;
    font-weight: 700;
    overflow: hidden;
    white-space: nowrap;
    color: #eaeadf;
}

.vue-typer .custom.char.typed {
    color: #eaeadf;
}

.vue-typer .custom.caret {
    margin-left: 3px;
    margin-right: 3px;
    width: 3px;
    background-color: #eaeadf;
}

.faa-float.animated {
    -webkit-animation: float 2s linear infinite;
    animation: float 2s linear infinite;
}

.headertop-down {
    display: none;
}

.index-header .headertop-down {
    position: relative;
    width: 100%;
    display: flex;
    bottom: 80px;
    z-index: 5;
}

.index-header .headertop-down i {
    font-size: 28px;
    color: #fff;
    -ms-transform: scale(1.5, 1);
    /* IE 9 */
    -webkit-transform: scale(1.5, 1);
    /* Safari */
    transform: scale(1.5, 1);
    /* Standard syntax */
}


@-webkit-keyframes glitch-loop-1 {
    0% {
        clip: rect(36px, 9999px, 9px, 0)
    }

    25% {
        clip: rect(25px, 9999px, 99px, 0)
    }

    50% {
        clip: rect(50px, 9999px, 102px, 0)
    }

    75% {
        clip: rect(30px, 9999px, 92px, 0)
    }

    100% {
        clip: rect(91px, 9999px, 98px, 0)
    }
}

@keyframes glitch-loop-1 {
    0% {
        clip: rect(36px, 9999px, 9px, 0)
    }

    25% {
        clip: rect(25px, 9999px, 99px, 0)
    }

    50% {
        clip: rect(50px, 9999px, 102px, 0)
    }

    75% {
        clip: rect(30px, 9999px, 92px, 0)
    }

    100% {
        clip: rect(91px, 9999px, 98px, 0)
    }
}

@-webkit-keyframes glitch-loop-2 {
    0% {
        top: -1px;
        left: 1px;
        clip: rect(65px, 9999px, 119px, 0)
    }

    25% {
        top: -6px;
        left: 4px;
        clip: rect(79px, 9999px, 19px, 0)
    }

    50% {
        top: -3px;
        left: 2px;
        clip: rect(68px, 9999px, 11px, 0)
    }

    75% {
        top: 0;
        left: -4px;
        clip: rect(95px, 9999px, 53px, 0)
    }

    100% {
        top: -1px;
        left: -1px;
        clip: rect(31px, 9999px, 149px, 0)
    }
}

@keyframes glitch-loop-2 {
    0% {
        top: -1px;
        left: 1px;
        clip: rect(65px, 9999px, 119px, 0)
    }

    25% {
        top: -6px;
        left: 4px;
        clip: rect(79px, 9999px, 19px, 0)
    }

    50% {
        top: -3px;
        left: 2px;
        clip: rect(68px, 9999px, 11px, 0)
    }

    75% {
        top: 0;
        left: -4px;
        clip: rect(95px, 9999px, 53px, 0)
    }

    100% {
        top: -1px;
        left: -1px;
        clip: rect(31px, 9999px, 149px, 0)
    }
}

.glitch:hover:before {
    text-shadow: 4px 0 #ff3f1a;
    animation: glitch-loop-1 .8s infinite ease-in-out alternate-reverse;
}

.glitch:hover:after {
    text-shadow: -5px 0 #00a7e0;
    animation: glitch-loop-2 .8s infinite ease-in-out alternate-reverse;
}

.glitch:before {
    left: -1px;
    text-shadow: 1px 0 #ff3f1a;
}

.glitch:after {
    left: 1px;
    text-shadow: -1px 0 #00a7e0;
}

.glitch:after,
.glitch:before {
    content: attr(data-text);
    position: absolute;
    top: 0;
    width: 100%;
    background: rgba(0, 0, 0, 0);
    clip: rect(0, 0, 0, 0);
}

.vue-typer {
    font-family: var(--base-font-family);
}

@media screen and (max-width: 860px) {
    .header-foucs-title {
        font-size: 2.5em;
    }

    .header-foucs-info-main p {
        font-size: 16px;
    }

    .types-title h1 {
        font-size: 1.85em;
    }

    .types-title {
        padding: 0 8%;
        text-align: left;
        top: 65%;
    }

    #header-background {
        height: 350px;
    }


}
</style>


<style scoped>
.top-social_v2 {
    height: 32px;
    margin-bottom: -15px;
    margin-left: 10px;
    list-style: none;
    display: inline-block;
}

.top-social_v2 li {
    float: left;
    margin-right: 13px;
}

.top-social_v2 img {
    height: 35px;
    width: 35px;
    padding: 6px;
    background: 0 0;
}

.flipx {
    transform: scaleX(-1);
}
</style>
