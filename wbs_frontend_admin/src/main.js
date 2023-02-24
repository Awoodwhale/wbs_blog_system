import { createApp } from "vue"
import App from "./App.vue"
import { router } from "./route"
import zhCn from "element-plus/es/locale/lang/zh-cn"
import ElementPlus from "element-plus"
import "element-plus/dist/index.css"
import axios from "axios"
import VueAxios from "vue-axios"
import { checkInit, checkToken, SUCCESS } from "./api/api"
import { showNotify } from "./utils/utils"
// markdown基础
import VMdEditor from "@kangc/v-md-editor"
import "@kangc/v-md-editor/lib/style/base-editor.css"
// github主题
import githubTheme from "@kangc/v-md-editor/lib/theme/github.js"
import "@kangc/v-md-editor/lib/theme/style/github.css"
// emoji表情
import createEmojiPlugin from "@kangc/v-md-editor/lib/plugins/emoji/index"
import "@kangc/v-md-editor/lib/plugins/emoji/emoji.css"
// 流程图
import createMermaidPlugin from "@kangc/v-md-editor/lib/plugins/mermaid/cdn"
import "@kangc/v-md-editor/lib/plugins/mermaid/mermaid.css"
// todo list
import createTodoListPlugin from "@kangc/v-md-editor/lib/plugins/todo-list/index"
import "@kangc/v-md-editor/lib/plugins/todo-list/todo-list.css"
// 复制代码
import createCopyCodePlugin from "@kangc/v-md-editor/lib/plugins/copy-code/index"
import "@kangc/v-md-editor/lib/plugins/copy-code/copy-code.css"
// 代码高亮
import hljs from "highlight.js"


// markdown使用
VMdEditor.use(githubTheme, {
  Hljs: hljs,
  codeHighlightExtensionMap: {
    vue: "xml",
  },
})
// emoji使用
VMdEditor.use(createEmojiPlugin())
// 流程图使用
VMdEditor.use(createMermaidPlugin())
// todo list 使用
VMdEditor.use(createTodoListPlugin())
// 复制代码允许
VMdEditor.use(createCopyCodePlugin())

// 创建全局app
const app = createApp(App)
// 注册VMdEditor
app.use(VMdEditor)
// 引入全局变量
// TODO: 需要修改
app.config.globalProperties.$backendUrl = "http://120.48.126.17:11453" // 域名+端口
// app.config.globalProperties.$backendUrl = "http://localhost:2022" // 域名+端口

// 注册路由
app.use(router)

// 路由监听
router.beforeEach((to, from, next) => {
  // 如果是登录界面，则需要放行
  if (to.path === "/login") {
    next()
  } else if (to.path === "/init") {
    checkInit().then(res => {
      if (res.code === SUCCESS) {
        next({
          path: "/login",
        })  // 初始化过就去login
      } else {
        // 否则就去初始化
        next()
      }
    })
  } else {
    // 否则检查用户角色，如果是admin，就去后台，如果是普通user，那么就去首页
    checkToken().then(userData => {
      if (userData.code === SUCCESS) {
        if (userData.data.roles === "role_admin") {
          next()
        } else {
          showNotify("WBS登录", "您没有权限登录管理后台, 1s后跳转到门户页面!", "warning", 1000)
          setTimeout(() => {
            location.href = "http://120.48.126.17:11451"
          }, 1000)
        }
      } else {
        checkInit().then(res => {
          if (res.code === SUCCESS) {
            next({
              path: "/login",
            })  // 初始化过就去login
          } else {
            // 否则就去初始化
            next({
              path: "/init",
            })
          }
        })
      }
    })
  }
})

// 注册element ui
app.use(ElementPlus, {
  locale: zhCn, //使用中文语言
})

// 使用axios
app.use(VueAxios, axios)

app.mount("#app")
