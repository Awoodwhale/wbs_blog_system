<template>
    <div>
        <RightContent></RightContent>
        <div class="index-box">
            <el-row :gutter="20">
                <el-col :span="6">
                    <el-card>
                        <template #header>
                            <div class="card-header">
                                <span class="card-header-span">文 章</span>
                                <a href="#/content/post-article"><i aria-label="图标: plus"><svg class="icon-svg"
                                            viewBox="64 64 896 896" data-icon="plus" width="1em" height="1em"
                                            fill="currentColor" aria-hidden="true" focusable="false">
                                            <path d="M482 152h60q8 0 8 8v704q0 8-8 8h-60q-8 0-8-8V160q0-8 8-8z"></path>
                                            <path d="M176 474h672q8 0 8 8v60q0 8-8 8H176q-8 0-8-8v-60q0-8 8-8z"></path>
                                        </svg></i></a>
                            </div>
                        </template>

                        <div style=" text-align: center;" v-loading="loading" :element-loading-spinner="svg"
                            element-loading-svg-view-box="-10, -10, 50, 50" element-loading-background="#ffffff">
                            <span v-if="parseInt(blogInfo.article) === -1"
                                style="height:40px;line-height:40px;color:#ffffff;"> ...
                            </span>
                            <NumberCounter v-else :value="parseInt(blogInfo.article)">
                            </NumberCounter>
                        </div>

                    </el-card>

                    <el-card>
                        <template #header>
                            <div class="card-header">
                                <span class="card-header-span">草 稿</span>
                                <a href="#/content/manage-article"><i aria-label="图标: plus"><svg class="icon-svg"
                                            viewBox="64 64 896 896" data-icon="plus" width="1em" height="1em"
                                            fill="currentColor" aria-hidden="true" focusable="false">
                                            <path
                                                d="M755.2 857.6H262.4c-54.4 0-98.5-44.1-98.5-98.6V512.7h49.3v-312c0-27.2 22.1-49.3 49.3-49.3h492.7c27.2 0 49.3 22.1 49.3 49.3v312h49.3v246.4c-0.1 54.4-44.2 98.5-98.6 98.5z m0-656.9H262.4v312h147.8v98.6h197.1v-98.6h147.8v-312zM804.4 562H656.6v49.3c0 27.2-22.1 49.3-49.3 49.3H410.2c-27.2 0-49.3-22.1-49.3-49.3V562H213.1v197.1c0 27.2 22.1 49.3 49.3 49.3h492.7c27.2 0 49.3-22.1 49.3-49.3V562z m0 0"
                                                fill="" p-id="15117"></path>
                                            <path
                                                d="M656.8 330.2H361.2c-13.6 0-24.6-11-24.6-24.6 0-13.6 11-24.6 24.6-24.6h295.6c13.6 0 24.6 11 24.6 24.6 0.1 13.6-10.9 24.6-24.6 24.6z m0 0M484.4 445.3H361.2c-13.6 0-24.6-11-24.6-24.6s11-24.6 24.6-24.6h123.2c13.6 0 24.6 11 24.6 24.6s-11 24.6-24.6 24.6z m0 0"
                                                fill="" p-id="15118"></path>
                                        </svg></i></a>
                            </div>
                        </template>

                        <div style=" text-align: center;" v-loading="loading" :element-loading-spinner="svg"
                            element-loading-svg-view-box="-10, -10, 50, 50" element-loading-background="#ffffff">
                            <span v-if="parseInt(blogInfo.sketch) === -1"
                                style="height:40px;line-height:40px;color:#ffffff;"> ...
                            </span>
                            <NumberCounter v-else :value="parseInt(blogInfo.sketch)">
                            </NumberCounter>
                        </div>
                    </el-card>

                    <el-card>
                        <template #header>
                            <div class="card-header">
                                <span class="card-header-span">分 类</span>
                                <a href="#/operation/category"><i aria-label="图标: plus"><svg class="icon-svg"
                                            viewBox="64 64 896 896" data-icon="unordered-list" width="1em" height="1em"
                                            fill="currentColor" aria-hidden="true" focusable="false">
                                            <path
                                                d="M912 192H328c-4.4 0-8 3.6-8 8v56c0 4.4 3.6 8 8 8h584c4.4 0 8-3.6 8-8v-56c0-4.4-3.6-8-8-8zm0 284H328c-4.4 0-8 3.6-8 8v56c0 4.4 3.6 8 8 8h584c4.4 0 8-3.6 8-8v-56c0-4.4-3.6-8-8-8zm0 284H328c-4.4 0-8 3.6-8 8v56c0 4.4 3.6 8 8 8h584c4.4 0 8-3.6 8-8v-56c0-4.4-3.6-8-8-8zM104 228a56 56 0 1 0 112 0 56 56 0 1 0-112 0zm0 284a56 56 0 1 0 112 0 56 56 0 1 0-112 0zm0 284a56 56 0 1 0 112 0 56 56 0 1 0-112 0z">
                                            </path>
                                        </svg></i></a>
                            </div>
                        </template>

                        <div style=" text-align: center;" v-loading="loading" :element-loading-spinner="svg"
                            element-loading-svg-view-box="-10, -10, 50, 50" element-loading-background="#ffffff">
                            <span v-if="parseInt(blogInfo.category) === -1"
                                style="height:40px;line-height:40px;color:#ffffff;"> ...
                            </span>
                            <NumberCounter v-else :value="parseInt(blogInfo.category)">
                            </NumberCounter>
                        </div>
                    </el-card>

                    <el-card>
                        <template #header>
                            <div class="card-header">
                                <span class="card-header-span">标 签</span>
                                <a href="#/operation/label"><i aria-label="图标: plus"><svg class="icon-svg"
                                            viewBox="64 64 896 896" data-icon="plus" width="1.1em" height="1.1em"
                                            fill="currentColor" aria-hidden="true" focusable="false">
                                            <path
                                                d="M498.4832 815.9232a60.2112 60.2112 0 0 1-18.8416-2.8672 61.44 61.44 0 0 1-40.96-48.3328l-23.3472-138.4448a20.0704 20.0704 0 0 0-16.7936-16.7936l-138.4448-23.3472a61.44 61.44 0 0 1-33.1776-104.0384l259.2768-259.2768A62.2592 62.2592 0 0 1 528.7936 204.8h228.9664A61.44 61.44 0 0 1 819.2 266.24v228.9664a62.2592 62.2592 0 0 1-18.0224 43.4176l-259.2768 259.2768a61.0304 61.0304 0 0 1-43.4176 18.0224zM528.7936 245.76a19.2512 19.2512 0 0 0-14.336 6.144l-259.2768 259.2768a20.48 20.48 0 0 0-5.3248 21.2992 21.2992 21.2992 0 0 0 16.384 13.9264l138.4448 22.9376a61.44 61.44 0 0 1 50.3808 50.3808l22.9376 138.4448a21.2992 21.2992 0 0 0 13.9264 16.384 20.48 20.48 0 0 0 20.8896-5.3248l259.2768-259.2768a19.2512 19.2512 0 0 0 6.144-14.336V266.24a20.48 20.48 0 0 0-20.48-20.48z"
                                                p-id="4877"></path>
                                            <path
                                                d="M655.36 368.64m-40.96 0a40.96 40.96 0 1 0 81.92 0 40.96 40.96 0 1 0-81.92 0Z"
                                                p-id="4878"></path>
                                        </svg></i></a>
                            </div>
                        </template>

                        <div style=" text-align: center;" v-loading="loading" :element-loading-spinner="svg"
                            element-loading-svg-view-box="-10, -10, 50, 50" element-loading-background="#ffffff">
                            <span v-if="parseInt(blogInfo.label) === -1"
                                style="height:40px;line-height:40px;color:#ffffff;"> ...
                            </span>
                            <NumberCounter v-else :value="parseInt(blogInfo.label)">
                            </NumberCounter>
                        </div>
                    </el-card>
                </el-col>

                <el-col :span="12">
                    <el-card id="quick-write">
                        <template #header>
                            <div class="card-header">
                                <span style="font-size:24px;">记录此刻</span>

                                <el-tooltip trigger="click" effect="dark" content="发表的内容会记录在日记页面中" placement="top-end">
                                    <i style="cursor:pointer" aria-label="图标: info-circle-o"
                                        class="cursor-pointer anticon anticon-info-circle-o"><svg class="icon-svg"
                                            viewBox="64 64 896 896" data-icon="info-circle" width="1em" height="1em"
                                            fill="currentColor" aria-hidden="true" focusable="false">
                                            <path
                                                d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z">
                                            </path>
                                            <path
                                                d="M464 336a48 48 0 1 0 96 0 48 48 0 1 0-96 0zm72 112h-48c-4.4 0-8 3.6-8 8v272c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8V456c0-4.4-3.6-8-8-8z">
                                            </path>
                                        </svg></i>
                                </el-tooltip>

                            </div>
                        </template>

                        <el-input show-word-limit maxlength="2048" type="textarea" :autosize="{ minRows: 7, maxRows: 7 }" placeholder="写点什么吧..."
                            v-model="quickWriteContent">
                        </el-input>

                        <div style="margin-top:10px;display:flex;">
                            <el-button id="post-diary-button" :loading="postDiaryLoading" size="small" plain type="primary" @click="postDiary">
                                <template #loading>
                                    <div class="custom-loading">
                                        <svg class="circular" viewBox="-10, -10, 50, 50">
                                            <path class="path" d="
                                                M 30 15
                                                L 28 17
                                                M 25.61 25.61
                                                A 15 15, 0, 0, 1, 15 30
                                                A 15 15, 0, 1, 1, 27.99 7.5
                                                L 15 15
                                            " style="stroke-width: 4px; fill: rgba(0, 0, 0, 0)" />
                                        </svg>
                                    </div>
                                </template>发 表
                            </el-button>
                            <el-button size="small" plain type="warning" @click="quickWriteContent = ''">清 空</el-button>
                             <el-button style="margin-left:auto;" type="success" size="small" plain  @click="$router.push('/content/manage-diary')">日记管理</el-button>
                        </div>

                    </el-card>

                    <el-card id="news-card">
                        <template #header>
                            <div class="card-header">
                                <span style="font-size:24px;">最新动态</span>

                                <el-tooltip trigger="click" effect="dark" content="您可以在这里查看最新文章和评论" placement="top-end">
                                    <i style="cursor:pointer" aria-label="图标: info-circle-o"
                                        class="cursor-pointer anticon anticon-info-circle-o"><svg class="icon-svg"
                                            viewBox="64 64 896 896" data-icon="info-circle" width="1em" height="1em"
                                            fill="currentColor" aria-hidden="true" focusable="false">
                                            <path
                                                d="M512 64C264.6 64 64 264.6 64 512s200.6 448 448 448 448-200.6 448-448S759.4 64 512 64zm0 820c-205.4 0-372-166.6-372-372s166.6-372 372-372 372 166.6 372 372-166.6 372-372 372z">
                                            </path>
                                            <path
                                                d="M464 336a48 48 0 1 0 96 0 48 48 0 1 0-96 0zm72 112h-48c-4.4 0-8 3.6-8 8v272c0 4.4 3.6 8 8 8h48c4.4 0 8-3.6 8-8V456c0-4.4-3.6-8-8-8z">
                                            </path>
                                        </svg></i>
                                </el-tooltip>

                            </div>
                        </template>
                        <div>
                            <el-tabs @tab-click="tabClick">
                                <el-tab-pane label="最新文章">
                                    <el-skeleton :rows="4" animated :loading="articleLoading">
                                        <template #default>

                                            <div v-for="(item, index) in articles" :key="index" id="article-div">
                                                <div style="display:flex;">
                                                    <span id="article-span">{{ item.title }}</span>
                                                    <span id="time-span" style="color:#00000073">{{ item.updateTime
                                                    }}</span>
                                                </div>
                                                <el-divider v-if="index !== articles.length - 1" />
                                            </div>

                                        </template>
                                    </el-skeleton>

                                </el-tab-pane>
                                <el-tab-pane label="最新评论">
                                    <el-skeleton :rows="4" animated :loading="commentLoading">
                                        <template #default>
                                            <div v-for="(item, index) in comments" :key="index" id="comment-div">
                                                <div style="display:flex">
                                                    <div>
                                                        <!-- 头像 -->
                                                        <el-avatar shape="square" :size="50" :src="item.user.avatar">
                                                        </el-avatar>
                                                    </div>
                                                    <div style="width:100%">
                                                        <div style="display:flex;">
                                                            <span style="margin-left:20px;font-size:13px;"
                                                                id="time-span">
                                                                <span style="color:cornflowerblue">{{
                                                                        item.username
                                                                }}</span>
                                                                发表在

                                                                <span style="font-size:12px;">《{{
                                                                        commentArticles[item.articleId]
                                                                }}》</span></span>
                                                            <span
                                                                style="margin-left:auto;font-size:12px;color:#00000073;"
                                                                id="time-span">{{
                                                                        item.createTime
                                                                }}</span>
                                                        </div>
                                                        <div style="margin-top:5px;">
                                                            <div id="article-span"
                                                                style="text-align: center;margin-right:60px;color:#000000a6">
                                                                {{ decodeEmoji(item.content) }}</div>
                                                        </div>
                                                    </div>
                                                </div>
                                                <el-divider v-if="index !== comments.length - 1" />
                                            </div>
                                        </template>
                                    </el-skeleton>
                                </el-tab-pane>
                            </el-tabs>
                        </div>
                    </el-card>

                </el-col>

                <el-col :span="6">
                    <el-card>
                        <template #header>
                            <div class="card-header">
                                <span class="card-header-span">运行天数</span>
                                <el-tooltip trigger="click" effect="dark"
                                    :content="'博客已在后台运行' + parseInt(blogInfo.runTime) + '天'" placement="top">
                                    <i style="cursor:pointer" aria-label="图标: plus"><svg class="icon-svg"
                                            viewBox="64 64 896 896" data-icon="info-circle" width="1em" height="1em"
                                            fill="currentColor" aria-hidden="true" focusable="false">
                                            <path
                                                d="M512 96C282.624 96 96 282.624 96 512s186.624 416 416 416 416-186.624 416-416S741.376 96 512 96z m0 768c-194.08 0-352-157.92-352-352s157.92-352 352-352 352 157.92 352 352-157.92 352-352 352z"
                                                p-id="12949"></path>
                                            <path
                                                d="M466.816 324.96A32 32 0 0 0 416 350.848v339.776a32 32 0 0 0 50.816 25.856l233.6-169.888a32 32 0 0 0 0-51.776l-233.6-169.856z"
                                                p-id="12950"></path>
                                        </svg></i>
                                </el-tooltip>
                            </div>
                        </template>

                        <div style="text-align: center;" v-loading="loading" :element-loading-spinner="svg"
                            element-loading-svg-view-box="-10, -10, 50, 50" element-loading-background="#ffffff">
                            <span v-if="parseInt(blogInfo.runTime) === -1"
                                style="height:40px;line-height:40px;color:#ffffff;"> ...
                            </span>
                            <NumberCounter v-else :value="parseInt(blogInfo.runTime)">
                            </NumberCounter>
                        </div>

                    </el-card>

                    <el-card>
                        <template #header>
                            <div class="card-header">
                                <span class="card-header-span">阅读总量</span>
                                <el-tooltip trigger="click" effect="dark"
                                    :content="'博客累计阅读' + parseInt(blogInfo.viewCount) + '次'" placement="top"><i
                                        style="cursor:pointer" aria-label="图标: plus"><svg viewBox="64 64 896 896"
                                            data-icon="info-circle" width="1em" height="1em" fill="currentColor"
                                            aria-hidden="true" focusable="false" class="icon-svg">
                                            <path
                                                d="M512 608a96 96 0 1 1 0-192 96 96 0 0 1 0 192m0-256c-88.224 0-160 71.776-160 160s71.776 160 160 160 160-71.776 160-160-71.776-160-160-160"
                                                p-id="9452"></path>
                                            <path
                                                d="M512 800c-212.064 0-384-256-384-288s171.936-288 384-288 384 256 384 288-171.936 288-384 288m0-640C265.248 160 64 443.008 64 512c0 68.992 201.248 352 448 352s448-283.008 448-352c0-68.992-201.248-352-448-352"
                                                p-id="9453"></path>
                                        </svg></i></el-tooltip>
                            </div>
                        </template>

                        <div style=" text-align: center;" v-loading="loading" :element-loading-spinner="svg"
                            element-loading-svg-view-box="-10, -10, 50, 50" element-loading-background="#ffffff">
                            <span v-if="parseInt(blogInfo.viewCount) === -1"
                                style="height:40px;line-height:40px;color:#ffffff;"> ...
                            </span>
                            <NumberCounter v-else :value="parseInt(blogInfo.viewCount)">
                            </NumberCounter>
                        </div>

                    </el-card>

                    <el-card>
                        <template #header>
                            <div class="card-header">
                                <span class="card-header-span">累计评论</span>
                                <a href="#/content/manage-comment"><i aria-label="图标: plus"><svg class="icon-svg"
                                            viewBox="64 64 896 896" width="1em" height="1em" fill="currentColor"
                                            aria-hidden="true" focusable="false">
                                            <path
                                                d="M895.469672 127.981836 127.941927 127.981836c-35.322483 0-63.956637 28.634154-63.956637 63.956637l0 511.693008c0 35.322483 28.634154 63.956637 63.956637 63.956637l127.921461 0 0 160.1597c0 0.048095 0.00921 0.093121 0.010233 0.141216 0.214894 17.477047 14.44192 31.581276 31.970132 31.581276 10.175757 0 19.234063-4.758374 25.091468-12.165064l180.350554-179.717128 402.182872 0c35.322483 0 63.956637-28.634154 63.956637-63.956637L959.425286 191.938474C959.426309 156.61599 930.792155 127.981836 895.469672 127.981836zM895.469672 671.904896c0 17.520025-14.202467 31.722492-31.722492 31.722492L456.955336 703.627389 319.824119 838.711993 319.824119 703.627389l-32.238238 0L159.66442 703.627389c-17.520025 0-31.722492-14.202467-31.722492-31.722492L127.941927 223.665059c0-17.520025 14.202467-31.722492 31.722492-31.722492l704.08276 0c17.520025 0 31.722492 14.202467 31.722492 31.722492L895.469672 671.904896z"
                                                p-id="7557"></path>
                                            <path
                                                d="M767.54821 352.101243c0 17.520025-14.202467 31.722492-31.722492 31.722492L287.585881 383.823736c-17.520025 0-31.722492-14.202467-31.722492-31.722492l0-0.515746c0-17.520025 14.202467-31.722492 31.722492-31.722492l448.239837 0c17.520025 0 31.722492 14.202467 31.722492 31.722492L767.54821 352.101243z"
                                                p-id="7558"></path>
                                            <path
                                                d="M639.627772 480.022705c0 17.520025-14.202467 31.722492-31.722492 31.722492L287.585881 511.745197c-17.520025 0-31.722492-14.202467-31.722492-31.722492l0-0.515746c0-17.520025 14.202467-31.722492 31.722492-31.722492l320.318376 0c17.520025 0 31.722492 14.202467 31.722492 31.722492L639.626749 480.022705z"
                                                p-id="7559"></path>
                                        </svg></i></a>
                            </div>
                        </template>

                        <div style=" text-align: center;" v-loading="loading" :element-loading-spinner="svg"
                            element-loading-svg-view-box="-10, -10, 50, 50" element-loading-background="#ffffff">
                            <span v-if="parseInt(blogInfo.comment) === -1"
                                style="height:40px;line-height:40px;color:#ffffff;"> ...
                            </span>
                            <NumberCounter v-else :value="parseInt(blogInfo.comment)">
                            </NumberCounter>
                        </div>

                    </el-card>

                    <el-card>
                        <template #header>
                            <div class="card-header">
                                <span class="card-header-span">友情链接</span>
                                <a href="#/settings/friend-link"><i aria-label="图标: plus">
                                        <svg class="icon-svg" viewBox="64 64 896 896" width="1em" height="1em"
                                            fill="currentColor" aria-hidden="true" focusable="false">
                                            <path
                                                d="M663.466667 384c0-140.8-115.2-256-256-256s-256 115.2-256 256c0 102.4 59.733333 189.866667 147.2 232.533333-113.066667 38.4-204.8 134.4-232.533334 256-2.133333 10.666667 4.266667 23.466667 14.933334 25.6H85.333333c10.666667 0 19.2-6.4 21.333334-17.066666C140.8 742.4 266.666667 640 407.466667 640s256-115.2 256-256z m-469.333334 0c0-117.333333 96-213.333333 213.333334-213.333333s213.333333 96 213.333333 213.333333-96 213.333333-213.333333 213.333333-213.333333-96-213.333334-213.333333zM725.333333 554.666667c-12.8 0-21.333333 10.666667-19.2 23.466666 0 10.666667 10.666667 19.2 21.333334 19.2h2.133333c98.133333-6.4 168.533333-87.466667 168.533333-192 0-102.4-70.4-183.466667-168.533333-192-12.8-2.133333-21.333333 8.533333-23.466667 19.2 0 12.8 8.533333 21.333333 19.2 23.466667 74.666667 6.4 128 68.266667 128 149.333333 0 83.2-55.466667 145.066667-128 149.333334zM960 763.733333c-14.933333-57.6-49.066667-110.933333-96-147.2-8.533333-6.4-23.466667-6.4-29.866667 4.266667-6.4 8.533333-6.4 23.466667 4.266667 29.866667 40.533333 32 68.266667 74.666667 81.066667 123.733333 2.133333 10.666667 10.666667 17.066667 21.333333 17.066667h4.266667c10.666667-4.266667 17.066667-17.066667 14.933333-27.733334zM608 661.333333l-19.2-12.8c-10.666667-6.4-23.466667-4.266667-29.866667 6.4-6.4 10.666667-4.266667 23.466667 6.4 29.866667l19.2 12.8c61.866667 42.666667 104.533333 108.8 123.733334 183.466667 2.133333 10.666667 10.666667 17.066667 21.333333 17.066666h4.266667c10.666667-2.133333 19.2-14.933333 14.933333-25.6-21.333333-87.466667-70.4-162.133333-140.8-211.2z"
                                                p-id="3398"></path>
                                        </svg></i></a>
                            </div>
                        </template>

                        <div style=" text-align: center;" v-loading="loading" :element-loading-spinner="svg"
                            element-loading-svg-view-box="-10, -10, 50, 50" element-loading-background="#ffffff">
                            <span v-if="parseInt(blogInfo.friendLink) === -1"
                                style="height:40px;line-height:40px;color:#ffffff;"> ...
                            </span>
                            <NumberCounter v-else :value="parseInt(blogInfo.friendLink)">
                            </NumberCounter>
                        </div>

                    </el-card>

                </el-col>

            </el-row>
        </div>

    </div>
</template>

<script>
import RightContent from "../../layout/RightConent.vue"
import NumberCounter from "../../components/NumberCounter.vue"
import { showArticles, showComments, showUserBlogInfo, showArticleTitle, SUCCESS, addDiary } from '../../api/api'
import { showMessage, showNotify,entitiestoUtf16 } from '../../utils/utils'

export default {
    components: {
        RightContent,
        NumberCounter,
    },
    data() {
        return {
            blogInfo: {
                article: '-1',
                comment: '-1',
                viewCount: '-1',
                runTime: '-1',
                category: '-1',
                label: '-1',
                sketch: '-1',
                friendLink: '-1',
            },
            loading: true,
            svg: `
                <path class="path" d="
                M 30 15
                L 28 17
                M 25.61 25.61
                A 15 15, 0, 0, 1, 15 30
                A 15 15, 0, 1, 1, 27.99 7.5
                L 15 15
                " style="stroke-width: 4px; fill: rgba(0, 0, 0, 0)"/>
            `,
            quickWriteContent: '',
            articles: {},
            comments: {},
            commentArticles: {},
            articleLoading: true,
            commentLoading: true,
            postDiaryLoading: false,
            decodeEmoji: entitiestoUtf16,
        }
    },
    methods: {
        // 获取仪表盘信息
        getShowData() {
            showUserBlogInfo().then(res => {
                if (res.code === SUCCESS) {
                    this.blogInfo = res.data
                    this.loading = false
                } else {
                    showNotify("仪表盘信息", "仪表盘信息获取失败,请重试!", 'error')
                }
            })
        },
        // 获取文章列表
        getArticleData() {
            showArticles(1, 5).then(res => {
                this.articleLoading = false
                if (res.code === SUCCESS) {
                    this.articles = res.data.content
                } else {
                    showNotify("最新文章", "获取最新文章信息失败,请重试!", 'error')
                }
            })
        },
        // 获取评论列表
        getCommentData() {
            showComments(1, 5).then(res => {
                if (res.code === SUCCESS) {
                    this.comments = res.data.content
                    let ids = ''
                    for (let i = 0; i < this.comments.length; i++) {
                        ids += this.comments[i].articleId + '_'
                    }
                    showArticleTitle(ids).then(titleRes => {
                        this.commentLoading = false
                        if (titleRes.code === SUCCESS) {
                            this.commentArticles = titleRes.data
                        } else {
                            showNotify("评论文章", "获取评论文章信息失败,请重试!", 'error')
                        }
                    })
                } else {
                    this.commentLoading = false
                    showNotify("最新评论", "获取最新评论信息失败,请重试!", 'error')
                }
            })
        },
        // 切换最新消息的tab
        tabClick(item) {
            if (item.props.label === "最新评论" && this.commentLoading) {
                this.getCommentData()
            }
        },
        // 发表日记
        postDiary() {
            if (this.quickWriteContent === '') {
                showMessage("请输入记录内容!", 'warning')
                return
            }
            this.postDiaryLoading = true
            addDiary(this.quickWriteContent).then(res => {
                this.postDiaryLoading = false
                if (res.code === SUCCESS) {
                    showNotify("发表记录",res.message)
                    this.quickWriteContent = ''
                } else {
                    showNotify("发表记录","发表记录失败,请重试!",'error')
                }
            })
        }
    },
    mounted() {
        this.getShowData()
        this.getArticleData()
    },
    components: {
        RightContent,
        NumberCounter
    }
}

</script>

<style >
.index-box .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.index-box {
    padding-left: 20px;
    padding-right: 20px;
    padding-top: 0;
    padding-bottom: 5px;
}

.index-box .card-header .card-header-span {
    font-size: 18px;
    color: #777777;
}

.index-box .number-grow {
    color: var(--indexCardNumberBg);
    font-size: 30px;
    font-weight: 545;
    line-height: 40px;
    height: 40px;
}

.index-box .icon-svg {
    margin-top: 3px;
    color: var(--indexCardSvgBg)
}

.index-box .el-col .el-card {
    margin-bottom: 15px;
}


.index-box .el-textarea__inner {
    font-size: 16px;
}

.index-box #quick-write .el-card__body {
    padding: 15px;
}

.index-box #news-card .el-card__header,
.index-box #quick-write .el-card__header {
    padding-top: 12px;
    padding-bottom: 12px;
    padding-left: 15px;
    padding-right: 15px;
}


.index-box #quick-write .el-textarea__inner:focus {
    outline: 0;
    box-shadow: 0 0 0 1px var(--quickWriteFoucsBg) inset;
}

.index-box #news-card .el-card__body {
    padding-top: 0;
    padding-left: 17px;
    padding-right: 17px;
}

#article-span {
    font-family: -apple-system, BlinkMacSystemFont, Segoe UI, PingFang SC, Hiragino Sans GB, Microsoft YaHei, Helvetica Neue, Helvetica, Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji, Segoe UI Symbol;
    color: rgba(0, 0, 0, .65);
    font-size: 15px;
    line-height: 22px;
    font-weight: 550;
}

#time-span {
    font-family: -apple-system, BlinkMacSystemFont, Segoe UI, PingFang SC, Hiragino Sans GB, Microsoft YaHei, Helvetica Neue, Helvetica, Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji, Segoe UI Symbol;
    color: rgba(0, 0, 0, .65);
    font-size: 14px;
    font-variant: tabular-nums;
    line-height: 1.5;
    font-feature-settings: "tnum";
    margin-left: auto;
}

#comment-div .el-divider,
#article-div .el-divider {
    margin-top: 8px;
    margin-bottom: 8px;
}

#news-card .el-card__body {
    padding-bottom: 10px;
}

#post-diary-button .custom-loading .circular {
  margin-right: 6px;
  width: 18px;
  height: 18px;
  animation: loading-rotate 2s linear infinite;
}
#post-diary-button .custom-loading .circular .path {
  animation: loading-dash 1.5s ease-in-out infinite;
  stroke-dasharray: 90, 150;
  stroke-dashoffset: 0;
  stroke-width: 2;
  stroke: var(--el-button-text-color);
  stroke-linecap: round;
}
</style>