import {
    Message
} from "element-ui";
import {
    Notification
} from "element-ui";
import {
    showAdminInfo,
    showArticles,
    showBlogInfo,
    showCategories,
    showLabels, showRandomUrl,
    showWebSiteSeo,
    showWebSiteSvg,
    showWebSiteTitle,
    SUCCESS
} from "@/assets/api/api";

/**
 * 弹出message
 * @param content
 * @param state
 * @param isCenter
 * @param isClose
 * @param time
 */
export const showMessage = (
    content,
    state = "success",
    time = 3000,
    isCenter = true,
    isClose = true
) => {
    Message({
        showClose: isClose,
        message: content,
        center: isCenter,
        type: state,
        duration: time,
    });
};

/**
 * 弹出notify
 * @param head
 * @param content
 * @param state
 * @param time
 */
export const showNotify = (head, content, state = "success", time = 3000) => {
    Notification({
        title: head,
        message: content,
        type: state,
        duration: time
    });
};

/**
 * 每次打开页面，异步执行的请求数据操作
 * @param ctx 上下文
 * @returns {Promise<void>}
 */
export const getPageNeededData = async (ctx) => {
    if (ctx.store.state.randomPicUrl === undefined) {
        const urlRes = await showRandomUrl()    // 随机图url
        if (urlRes.code === SUCCESS) {
            ctx.store.commit("setRandomUrl", urlRes.data['web_site_random_img'])
        } else {
            ctx.store.commit("setRandomUrl", '/background1.jpg')
        }
    }
    if (ctx.store.state.adminInfos === undefined) {
        const adminInfosRes = await showAdminInfo() // 博客管理员信息
        if (adminInfosRes.code === SUCCESS) {
            ctx.store.commit("setAdminInfos", adminInfosRes.data)
        }
    }
    if (ctx.store.state.categories === undefined) {
        const categoriesRes = await showCategories()  // 获取文章分类
        if (categoriesRes.code === SUCCESS) {
            ctx.store.commit("setCategories", categoriesRes.data)
        }
    }
    if (ctx.store.state.labels === undefined) {
        const labelsRes = await showLabels()  // 获取文章标签
        if (labelsRes.code === SUCCESS) {
            ctx.store.commit("setLabels", labelsRes.data)
        }
    }
    if (ctx.store.state.blogInfos === undefined) {
        const blogInfoRes = await showBlogInfo()  // 博客基本信息
        if (blogInfoRes.code === SUCCESS) {
            ctx.store.commit("setBlogInfos", blogInfoRes.data)
        }
    }
    await getSeoInfos(ctx)
    const articlesRes = await showArticles(1, 10)  // 文章,缓存第一页就可以
    // 数据给到 vuex
    if (articlesRes.code === SUCCESS) {
        ctx.store.commit("setArticles", articlesRes.data)
    }
}

/**
 * 获取seo信息
 * @param ctx
 * @returns {Promise<void>}
 */
export const getSeoInfos = async (ctx) => {
    if (ctx.store.state.seoFlag !== 'wcx') {
        const res1 = await showWebSiteTitle()
        if (res1.code === SUCCESS) {
            ctx.app.head.title = res1.data['web_site_title']
            ctx.store.commit("setSeoFlag", "w")
        }
        const res2 = await showWebSiteSeo()
        if (res2.code === SUCCESS) {
            ctx.app.head.mate = [
                {
                    charset: 'utf-8'
                },
                {
                    name: "keywords",
                    content: res2.data['web_site_keywords'],
                },
                {
                    hid: 'description',
                    name: "description",
                    content: res2.data['web_site_description'],
                }
            ]
            ctx.store.commit("setSeoFlag", "c")
        }
        const res3 = await showWebSiteSvg()
        if (res3.code === SUCCESS) {
            let link = ctx.app.head.link
            if (link.length === 0) {
                link.push({
                    rel: 'icon',
                    href: res3.data['web_site_svg'] || '/favicon.ico',
                    type: 'image/x-icon'
                })
            } else {
                link.find((item, index) => {
                    if (item.rel === 'icon') {
                        link[index] = {
                            rel: 'icon',
                            href: res3.data['web_site_svg'] || '/favicon.ico',
                            type: 'image/x-icon'
                        }
                    }
                })
            }
            ctx.app.head.link = link
            ctx.store.commit("setSeoFlag", "x")
        }
    }
}

/**
 * 判断是否是空字符
 * @param str 字符串
 * @returns {boolean} 结果
 */
export const isEmpty = (str) => {
    return str === undefined || str === null || str === ''
}

/**
 * 判断是否是邮箱格式
 * @param email
 * @returns {boolean}
 */
export const isEmail = (email) => {
    const res = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/
    return res.test(email)
}
