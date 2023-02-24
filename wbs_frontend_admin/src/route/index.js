import { createRouter, createWebHashHistory } from "vue-router"
import {
  Reading,
  CoffeeCup,
  Stopwatch,
  EditPen,
  Document,
  ChatDotRound,
  Picture,
  UserFilled,
  User,
  InfoFilled,
  DataLine,
  Menu,
  Position,
  PictureRounded,
  Setting,
  CreditCard,
  Link,
} from "@element-plus/icons-vue"
import { markRaw } from "vue"

// 布局
const baseView = () => import("../layout/BaseView.vue")
const rightView = () => import("../layout/RightConent.vue")
// 登录页面
const login = () => import("../pages/login/login.vue")
const init = () => import("../pages/login/init.vue")
// 内容部分
const postArticle = () => import("../pages/content/PostArticle.vue")
const articleManage = () => import("../pages/content/ManageArticle.vue")
const imageManage = () => import("../pages/content/ManageImage.vue")
const commentManage = () => import("../pages/content/ManageComment.vue")
const diaryManage = () => import("../pages/content/ManageDiary.vue")
// 仪表盘主页
const index = () => import("../pages/dashboard/Index.vue")
// 运营部分
const categoryManage = () => import("../pages/operation/ManageCategory.vue")
const loopManage = () => import("../pages/operation/ManagerLoop.vue")
const labelManage = () => import("../pages/operation/ManageLabels.vue")
// 设置
const friendLink = () => import("../pages/settings/FriendLink.vue")
const websiteInfo = () => import("../pages/settings/WebSiteInfo.vue")
// 用户
const userList = () => import("../pages/user/ListUser.vue")
const adminInfo = () => import("../pages/user/UpdateAdminInfo.vue")

// 路由器
export const routes = [
  {
    path: "",
    component: baseView,
    redirect: "/index",
    children: [
      {
        path: "/index",
        name: "仪表盘",
        icon: markRaw(Stopwatch),
        component: index,
      },
      {
        path: "/content",
        name: "内容",
        icon: markRaw(Reading),
        component: rightView,
        children: [
          {
            path: "post-article",
            name: "发表文章",
            icon: markRaw(EditPen),
            component: postArticle,
          },
          {
            path: "manage-article",
            name: "文章管理",
            icon: markRaw(Document),
            component: articleManage,
          },
          {
            path: "manage-diary",
            name: "日记管理",
            icon: markRaw(CoffeeCup),
            component: diaryManage,
          },
          {
            path: "manage-comment",
            name: "评论管理",
            icon: markRaw(ChatDotRound),
            component: commentManage,
          },
          {
            path: "manage-image",
            name: "图片管理",
            icon: markRaw(Picture),
            component: imageManage,
          },
        ],
      },
      {
        path: "/user",
        name: "用户",
        icon: markRaw(User),
        component: rightView,
        children: [
          {
            path: "list",
            name: "用户列表",
            icon: markRaw(UserFilled),
            component: userList,
          },
          {
            path: "info",
            name: "管理信息",
            icon: markRaw(InfoFilled),
            component: adminInfo,
          },
        ],
      },
      {
        path: "/operation",
        name: "运营",
        icon: markRaw(DataLine),
        component: rightView,
        children: [
          {
            path: "category",
            name: "分类管理",
            icon: markRaw(Menu),
            component: categoryManage,
          },
          {
            path: "label",
            name: "标签管理",
            icon: markRaw(Position),
            component: labelManage,
          },
          {
            path: "loop",
            name: "轮播图管理",
            icon: markRaw(PictureRounded),
            component: loopManage,
          },
        ],
      },
      {
        path: "/settings",
        name: "设置",
        icon: markRaw(Setting),
        component: rightView,
        children: [
          {
            path: "website-info",
            name: "站点信息",
            icon: markRaw(CreditCard),
            component: websiteInfo,
          },
          {
            path: "friend-link",
            name: "友情链接",
            icon: markRaw(Link),
            component: friendLink,
          },
        ],
      },
    ],
  },
  {
    path: "/login",
    name: "登录",
    component: login,
  },
  {
    path: "/init",
    name: "初始化",
    component: init,
  },
]

export const router = createRouter({
  history: createWebHashHistory(),
  routes,
})
