import http from "./http";

// 成功状态码
export const SUCCESS = 20000

// 获取站点信息SEO
export const showWebSiteSeo = () => {
    return http.requestGet("/portal/web_site_info/seo");
}

// 获取站点title
export const showWebSiteTitle = () => {
    return http.requestGet("/portal/web_site_info/title");
}

// 获取站点svg
export const showWebSiteSvg = () => {
    return http.requestGet("/portal/web_site_info/blog_svg");
}

// 获取随机图url
export const showRandomUrl = () => {
    return http.requestGet("/portal/web_site_info/blog_random_img")
}

// 获取首页文字信息
export const showH1WithSign = () => {
    return http.requestGet("/portal/web_site_info/blog_h1")
}

// 用户登录（目的仅仅是为了评论）
export const userLogin = (captchaCode, captchaKey, user) => {
    return http.requestPost('/user/login/?captcha_code=' + captchaCode + '&captcha_code_key=' + captchaKey, user)
}

// 获取某个用户的id
export const showAdminInfo = () => {
    return http.requestGet("/user/admin_info")
}

// 获取博客信息
export const showBlogInfo = () => {
    return http.requestGet("/portal/web_site_info/user_blog_info")
}

// 获取普通文章信息
export const showArticles = (page, size = '10') => {
    return http.requestGet("/portal/article/list/" + page + "/" + size)
}

// 获取置顶文章信息
export const showTopArticles = () => {
    return http.requestGet("/portal/article/list/top")
}

// 获取文章分类
export const showCategories = () => {
    return http.requestGet("/portal/article/categories")
}

// 获取文章标签
export const showLabels = () => {
    return http.requestGet("/portal/article/labels")
}

// 获取说说日记
export const showDiaries = (page, size = '10') => {
    return http.requestGet("/portal/diary/list?page=" + page + "&size=" + size)
}

// 获取友链
export const showLinks = () => {
    return http.requestGet("/portal/web_site_info/friend_link")
}

// 获取某个分类的文章
export const showArticlesByCategoryId = (categoryId, page, size = '10') => {
    return http.requestGet("/portal/article/list/category/" + categoryId + "/" + page + "/" + size)
}

// 获取某个标签的文章
export const showArticlesByLabelName = (labelId, page, size = '10') => {
    return http.requestGet("/portal/article/list/label/" + labelId + "/" + page + "/" + size)
}

// 通过id获取文章详情
export const showArticleById = (articleId) => {
    return http.requestGet("/portal/article/" + articleId)
}

// 通过文章id获取推荐文章
export const showRecommendArticleById = (articleId, size = '5') => {
    return http.requestGet("/portal/article/list/recommend/" + articleId + "/" + size)
}

// 通过文章id获取文章评论
export const showCommentById = (articleId) => {
    return http.requestGet("/portal/comment/list/" + articleId)
}

// 对文章进行评论
export const addComment = (info) => {
    return http.requestPost("/portal/comment", info)
}

// 判断用户是否登录
export const checkUserLogin = () => {
    return http.requestGet("/user/check_token")
}

// 通过token判断是否登录
export const checkUserLoginByToken = (token) => {
    return http.requestGet("/user/check_token?token=" + token)
}

// 获取社交媒体
export const showSocial = () => {
    return http.requestGet("/portal/web_site_info/social_media")
}

// 判断邮箱是否注册
export const showEmailIsRegister = (email) => {
    return http.requestGet("/user/email?email=" + email)
}

// 获取邮箱验证码
export const sendEmailCaptcha = (type, email) => {
    return http.requestGet("/user/verify_code?type=" + type + "&email=" + email)
}

// 用户注册
export const userRegister = (user, captcha) => {
    return http.requestPost("/user/register?verify_code=" + captcha, user)
}

// 更新用户信息
export const updateUserInfo = (userId, user) => {
    return http.requestPut("/user/user_info/" + userId, user)
}

// 根据用户id获取用户信息
export const showUserInfo = (userId) => {
    return http.requestGet("/user/user_info/" + userId)
}

// 退出登录
export const logout = () => {
    return http.requestGet("/user/logout")
}

// solr搜索
export const solrSearch = (keyword, page, size = '10', categoryId = "") => {
    let url = "/portal/search"
    url += "?keyword=" + encodeURIComponent(keyword) + "&page=" + page + "&size=" + size
    if (categoryId !== "") {
        url += "&categoryId=" + categoryId
    }
    return http.requestGet(url)
}
