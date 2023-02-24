import Vue from "vue"
import Vuex from "vuex"

Vue.use(Vuex)

export const state = () => ({
    h1WithSign: {   // 首页文字信息
        h1: undefined,
        sign: undefined
    },
    randomPicUrl: undefined,    // 博客随机图url
    seoFlag: '',    // seo的flag
    adminInfos: undefined, // 博客管理员信息
    blogInfos: undefined,   // 博客信息
    articles: undefined,  // 文章
    recommendArticles: undefined,   // 推荐文章
    diaries: undefined,  // 日记说说
    categories: undefined,  // 分类
    categoryMap: {},    // 分类的map
    labels: undefined,  // 标签
    labelMap: {},   // 标签的map
    links: undefined,   // 友情链接
    articlesPageInfo: { // 文章分页数据
        curPage: 1,
        defSize: 10,
        totalSize: 0
    },
    diariesPageInfo: {  // 日记说说分页数据
        curPage: 1,
        defSize: 10,
        totalSize: 0
    },
    articleData: undefined, // 文章详情
    articleComments: [],   // 文章评论
    userInfo: { // 用户信息
        id: undefined,
        username: undefined,
        password: undefined,
        roles: undefined,
        avatar: undefined,
        email: undefined,
        sign: undefined,
        state: undefined,
        createTime: undefined,
        profession: undefined,
        major: undefined,
        location: undefined
    },
    socialMedias: undefined,// 社交媒体信
})

const mutations = {
    setH1WithSign(state, infos) {
        state.h1WithSign.h1 = infos.h1
        state.h1WithSign.sign = infos.sign
    },
    setSeoFlag(state, infos) {
        state.seoFlag += infos
    },
    setRandomUrl(state, infos) {
        state.randomPicUrl = infos
    },
    setAdminInfos(state, infos) {
        state.adminInfos = infos
    },
    setBlogInfos(state, infos) {
        state.blogInfos = infos
    },
    setDiaries(state, infos) {
        state.diaries = infos.content
        state.diariesPageInfo.curPage = infos.number + 1
        state.diariesPageInfo.totalSize = infos.totalElements

    },
    setDiariesCurPage(state, curPage) {
        state.diariesPageInfo.curPage = curPage
    },
    setArticles(state, infos) {
        state.articles = infos.content
        state.articlesPageInfo.curPage = infos.number + 1
        state.articlesPageInfo.totalSize = infos.totalElements
    },
    setCategories(state, infos) {
        state.categories = infos
        for (let i = 0; i < infos.length; i++) {
            state.categoryMap[infos[i].id] = infos[i].name
        }
    },
    setLabels(state, infos) {
        state.labels = infos
        for (let i = 0; i < infos.length; i++) {
            state.labelMap[infos[i].id] = infos[i].name
        }
    },
    setArticlesCurPage(state, curPage) {
        state.articlesPageInfo.curPage = curPage
    },
    setLinks(state, infos) {
        state.links = infos
    },
    setArticleData(state, infos) {
        state.articleData = infos
    },
    setRecommendArticles(state, infos) {
        state.recommendArticles = infos
    },
    setArticleComments(state, infos) {
        state.articleComments = infos
    },
    setUserInfo(state, infos) {
        state.userInfo = infos
    },
    setSocialMedias(state, infos) {
        state.socialMedias = infos
    },
}

export default {
    namespaced: true,
    state,
    mutations,
}
