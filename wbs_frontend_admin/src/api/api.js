import http from "./http.js"

// 响应成功返回的code
export const SUCCESS = 20000

// 解析token
export const checkToken = () => {
  return http.requestGet("/user/check_token")
}

// 判断是否管理员初始化成功
export const checkInit = () => {
  return http.requestGet("/user/check_init")
}

// 管理员初始化
export const adminInit = (user) => {
  return http.requestPost("/user/admin_account", user)
}

// 登录
export const userLogin = (captchaCode, captchaKey, user) => {
  return http.requestPost(
    "/user/login/?captcha_code=" +
    captchaCode +
    "&captcha_code_key=" +
    captchaKey,
    user
  )
}

// 获取分类列表
export const showCategories = () => {
  return http.requestGet("/admin/category/list")
}

// 删除分类
export const deleteCategory = (categoryId) => {
  return http.requestDelete("/admin/category/" + categoryId)
}

// 新增分类
export const addCategory = (category) => {
  return http.requestPost("/admin/category", category)
}

// 更新分类
export const editCategory = (categoryId, category) => {
  return http.requestPut("/admin/category/" + categoryId, category)
}

// 获取轮播图列表
export const showLoops = () => {
  return http.requestGet("/admin/loop/list")
}

// 删除轮播图
export const deleteLoop = (loopId) => {
  return http.requestDelete("/admin/loop/" + loopId)
}

// 添加轮播图
export const addLoop = (loop) => {
  return http.requestPost("/admin/loop", loop)
}

// 更新轮播图
export const editLoop = (loopId, loop) => {
  return http.requestPut("/admin/loop/" + loopId, loop)
}

// 获取用户列表
export const showUsers = (curPage, size, username = "", email = "") => {
  if (username !== "" && email === "") {
    return http.requestGet(
      "/user/list?page=" + curPage + "&size=" + size + "&username=" + username
    )
  }
  if (username === "" && email !== "") {
    return http.requestGet(
      "/user/list?page=" + curPage + "&size=" + size + "&email=" + email
    )
  }
  if (username !== "" && email !== "") {
    return http.requestGet(
      "/user/list?page=" +
      curPage +
      "&size=" +
      size +
      "&username=" +
      username +
      "&email=" +
      email
    )
  }
  return http.requestGet("/user/list?page=" + curPage + "&size=" + size)
}

// 删除用户
export const deleteUser = (userId) => {
  return http.requestDelete("/user/delete/" + userId)
}

// 更新用户
export const editUser = (userId, user) => {
  return http.requestPut("/user/user_info/" + userId, user)
}

// 获取用户信息
export const showUser = (userId = "") => {
  if (userId != "") {
    return http.requestGet("/user/user_info/" + userId)
  } else {
    return http.requestGet("/user/user_info")
  }
}

// 获取用户文章数据详情
export const showUserBlogInfo = () => {
  return http.requestGet("/admin/web_site_info/user_blog_info")
}

// 修改管理员密码
export const editAdminPassword = (passwordInfo) => {
  return http.requestPut(
    "/user/reset_password?oldPassword=" +
    passwordInfo.oldPassword +
    "&newPassword=" +
    passwordInfo.newPassword
  )
}

// 退出登录
export const logoutAdmin = () => {
  return http.requestGet("/user/logout")
}

// 获取邮箱验证码
export const showVerifyCode = (type, email) => {
  return http.requestGet("/user/verify_code?type=" + type + "&email=" + email)
}

// 修改邮箱
export const editEmail = (emailInfo) => {
  return http.requestPut(
    "/user/email?verify_code=" +
    emailInfo.verifyCode +
    "&new_email=" +
    emailInfo.newEmail +
    "&now_password=" +
    emailInfo.password
  )
}

// 获取站点信息SEO
export const showWebSiteSeo = () => {
  return http.requestGet("/admin/web_site_info/seo")
}

// 获取站点title
export const showWebSiteTitle = () => {
  return http.requestGet("/admin/web_site_info/title")
}

// 获取站点svg
export const showWebSiteSvg = () => {
  return http.requestGet("/admin/web_site_info/blog_svg")
}

// 更改站点svg
export const editWebSiteSvg = (svg) => {
  return http.requestPost("/admin/web_site_info/blog_svg", svg)
}

// 更改站点seo
export const editWebSiteSeo = (normalWebSiteInfo) => {
  return http.requestPut(
    "/admin/web_site_info/seo?keywords=" +
    normalWebSiteInfo.keywords +
    "&description=" +
    normalWebSiteInfo.description
  )
}

// 更改站点标题
export const editWebSiteTitle = (title) => {
  return http.requestPut("/admin/web_site_info/title?title=" + title)
}

// 获取友链列表
export const showFriendLinkList = () => {
  return http.requestGet("/admin/friend_link/list")
}

// 删除友链
export const deleteFriendLink = (friendLinkId) => {
  return http.requestDelete("/admin/friend_link/" + friendLinkId)
}

// 添加友链
export const addFriendLink = (friendLinkForm) => {
  return http.requestPost("/admin/friend_link", friendLinkForm)
}

// 修改友链
export const editFriendLink = (friendLinkId, friendLinkForm) => {
  return http.requestPut("/admin/friend_link/" + friendLinkId, friendLinkForm)
}

// 获取标签列表
export const showLabelList = () => {
  return http.requestGet("/admin/label/list")
}

// 删除标签
export const deleteLabel = (labelId) => {
  return http.requestDelete("/admin/label/" + labelId)
}

// 添加标签
export const addLabel = (labelForm) => {
  return http.requestPost("/admin/label", labelForm)
}

// 更新标签
export const editLabel = (labelForm) => {
  return http.requestPut("/admin/label/" + labelForm.id, labelForm)
}

// 获取图片列表
export const showImages = (page, size = "10") => {
  return http.requestGet("/admin/image/list?page=" + page + "&size=" + size)
}

// 上传图片
export const addImage = (imgFile) => {
  return http.requestPost("/admin/image", imgFile)
}

// 上传草稿
export const addSketchArticle = (sketchArticle) => {
  return http.requestPost("/admin/article/sketch", sketchArticle)
}

// 发表文章
export const addArticle = (article) => {
  return http.requestPost("/admin/article", article)
}

// 删除图片
export const deleteImage = (imageId) => {
  return http.requestDelete("/admin/image/" + imageId)
}

// 获取已发布或者已删除的文章列表
export const showArticles = (
  page,
  size = 10,
  keyword = "",
  categoryId = "",
  state = ""
) => {
  let url = "/admin/article/list?page=" + page + "&size=" + size
  if (keyword !== "") {
    url += "&keyword=" + keyword
  }
  if (categoryId !== "") {
    url += "&categoryId=" + categoryId
  }
  if (state !== "") {
    url += "&state=" + state
  }
  return http.requestGet(url)
}

// 修改文章状态为 删除、正常、置顶、取消置顶
export const editArticleState = (articleId, targetState) => {
  let baseUrl = "/admin/article/state/" + articleId
  if (targetState === "remove") {
    return http.requestDelete(baseUrl + "/delete")
  } else if (targetState === "recover") {
    return http.requestPut(baseUrl + "/recover")
  } else if (targetState === "top") {
    return http.requestPut(baseUrl + "/top")
  }
}

// 数据库中彻底删除文章
export const deleteArticle = (articleId) => {
  return http.requestDelete("/admin/article/" + articleId)
}

// 获取已发布文章信息
export const showPostedArticle = (articleId) => {
  return http.requestGet("/admin/article/" + articleId)
}

// 更新已发布文章
export const editPostedArticle = (articleId, article) => {
  return http.requestPut("/admin/article/" + articleId, article)
}

// 获取草稿文章列表
export const showSketchs = (page, size) => {
  return http.requestGet(
    "/admin/article/sketch/list?page=" + page + "&size=" + size
  )
}

// 获取草稿数据
export const showSketchArticle = (sketchId) => {
  return http.requestGet("/admin/article/sketch/" + sketchId)
}

// 更新草稿
export const editSketchArticle = (sketchId, sketch) => {
  return http.requestPut("/admin/article/sketch/" + sketchId, sketch)
}

// 删除草稿
export const deleteSketchArticle = (sketchId) => {
  return http.requestDelete("/admin/article/sketch/" + sketchId)
}

// 评论列表
export const showComments = (page, size) => {
  return http.requestGet("/admin/comment/list?page=" + page + "&size=" + size)
}

// 获取文章标题
export const showArticleTitle = (articleId) => {
  return http.requestGet("/admin/article/" + articleId + "/title")
}

// 修改评论状态
export const editComment = (commentId, state) => {
  let url = "/admin/comment/" + commentId
  if (state === "remove") {
    url += "/delete"
  } else if (state === "recover") {
    url += "/recover"
  } else if (state === "top") {
    url += "/top"
  }
  return http.requestPut(url)
}

// 数据库删除评论
export const deleteComment = (commentId) => {
  return http.requestDelete("/admin/comment/" + commentId)
}

// 发布评论（回复评论）
export const addComment = (comment) => {
  return http.requestPost("/admin/comment", comment)
}

// 发布日记
export const addDiary = (diaryContent) => {
  let diary = {
    content: diaryContent,
  }
  return http.requestPost("/admin/diary", diary)
}

// 获取日记列表
export const showDiarys = (page, size = "10") => {
  return http.requestGet("/admin/diary/list?page=" + page + "&size=" + size)
}

// 编辑日记
export const editDiary = (diaryId, diaryContent) => {
  let diary = {
    content: diaryContent,
  }
  return http.requestPut("/admin/diary/" + diaryId, diary)
}

// 删除日记
export const deleteDiary = (diaryId) => {
  return http.requestDelete("/admin/diary/" + diaryId)
}

// 获取博客首页屏信息设置
export const showBlogH1 = () => {
  return http.requestGet("/admin/web_site_info/blog_h1")
}

// 获取博客随机图设置
export const showBlogRandomUrl = () => {
  return http.requestGet("/admin/web_site_info/blog_random_img")
}

// 获取博客是否可以评论
export const showBlogSureComment = () => {
  return http.requestGet("/admin/web_site_info/sure_comment")
}

// 修改博客首页屏信息
export const editBlogH1 = (h1, sign) => {
  return http.requestPut("/admin/web_site_info/blog_h1?h1=" + h1 + "&sign=" + sign)
}

// 设置博客随机图
export const editBlogRandomUrl = (url) => {
  return http.requestPut("/admin/web_site_info/blog_random_img?url=" + url)
}

// 设置博客是否可以评论
export const editBlogSureComment = () => {
  return http.requestPut("/admin/web_site_info/sure_comment")
}

// 获取社交媒体信息
export const showSocialMedia = () => {
  return http.requestGet("/admin/web_site_info/social_media")
}

// 修改社交媒体信息
export const editSocialMedia = (infos) => {
  return http.requestPost("/admin/web_site_info/social_media", infos)
}