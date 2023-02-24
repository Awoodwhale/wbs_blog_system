<template style="height: 100%">
  <div class="base-view-container">
    <div class="common-layout">
      <!-- 左部分 -->
      <el-container>
        <el-card id="left-card-container">
          <div id="left-container">
            <el-container direction="vertical" class="el-left-container">
              <div class="logo" id="logo">
                <h1 id="logo-name">WBS Dashboard</h1>
              </div>
              <el-aside id="left-menu-list-box" width="200">
                <div class="nav" id="nav">
                  <el-menu :default-active="curPath" :unique-opened="true" :collapse-transition="false"
                    :background-color="menuBg" :text-color="menuFontBg" :active-text-color="menuFontActiveBg"
                    :collapse="isCollapse" id="left-menu">
                    <template v-for="(item, mainIndex) in menuList" :key="mainIndex + ''">
                      <router-link :to="item.path" v-if="!item.children">
                        <el-tooltip effect="dark" content="仪表盘" placement="right" v-if="isCollapse">
                          <el-menu-item :index="item.path" show-timeout="350" hide-timeout="350">
                            <el-icon :size="20">
                              <component :is="item.icon" />
                            </el-icon>
                          </el-menu-item>
                        </el-tooltip>
                        <el-menu-item :index="item.path" v-else show-timeout="350" hide-timeout="350">
                          <el-icon :size="20">
                            <component :is="item.icon" />
                          </el-icon>
                          <span>{{ item.name }}</span>
                        </el-menu-item>
                      </router-link>
                      <el-sub-menu :index="item.path" v-else>
                        <template #title>
                          <el-icon :size="20">
                            <component :is="item.icon" />
                          </el-icon>
                          <span>{{ item.name }}</span>
                        </template>
                        <router-link :to="item.path + '/' + subItem.path" v-for="(subItem, subIndex) in item.children"
                          :key="subIndex + ''">
                          <el-menu-item :index="item.path + '/' + subItem.path" show-timeout="350" hide-timeout="350">
                            <el-icon :size="20">
                              <component :is="subItem.icon" />
                            </el-icon>
                            <span>{{ subItem.name }}</span>
                          </el-menu-item>
                        </router-link>
                      </el-sub-menu>
                    </template>
                  </el-menu>
                </div>
              </el-aside>
            </el-container>
          </div>
        </el-card>
        <!-- 右部分 -->
        <el-container class="el-main-container">
          <el-header id="admin-header-box">
            <div class="header-container">
              <div class="header-left-box">
                <el-button :icon="menuIcon"  v-if="themeType === 'light'" circle id="menu-btn" @click="menuClick()" />
                <el-button :icon="menuIcon" type="info" color="#77736f" v-else circle id="menu-btn"
                  @click="menuClick()" />
              </div>
            </div>

            <div class="header-right-box">
              <div style="margin:auto;">
                <ClockTime :numbers="curTime" numberColor="#309eff" v-if="themeType === 'light'"></ClockTime>
                 <ClockTime :numbers="curTime" numberColor="#4e4e4e" v-else></ClockTime>
              </div>

              <ul class="header-right-menu">
                <li>
                  <el-tooltip content="主题切换" placement="bottom">
                    <el-button :icon="themeIcon" style="color:#ffffff" :dark="false"  color="#7a80f9" circle @click="changeTheme()" />
                  </el-tooltip>
                </li>
                <li>
                  <el-tooltip content="退出登录" placement="bottom">
                    <el-button :icon="logoutIcon" type="danger" circle @click="logout()" />
                  </el-tooltip>
                </li>
                <li>
                  <el-tooltip :content="userInfo.username" placement="bottom">
                    <el-avatar :src="userInfo.avatar" id="admin-avatar-img" fit="scale-down" />
                  </el-tooltip>
                </li>
              </ul>
            </div>
          </el-header>
          <!-- 菜单对应的内容view -->
          <el-main>
            <el-scrollbar>
              <div class="router-view-container">
                <router-view v-slot="{ Component, route }">
                  <transition name="MainFade" mode="out-in">
                    <component :is="Component" :key="route.path" />
                  </transition>
                </router-view>
              </div>
            </el-scrollbar>
          </el-main>
        </el-container>
      </el-container>
    </div>
  </div>
</template>

<script>
import { showUser, SUCCESS, logoutAdmin } from "../api/api";
import { onBeforeRouteUpdate } from "vue-router";
import { routes } from "../route";
import {
  DArrowLeft,
  DArrowRight,
  SwitchButton,
  Moon,
  Sunny,
} from "@element-plus/icons-vue";
import { markRaw } from "vue";
import { showMessageBox, showMessage } from "../utils/utils";
import ClockTime from '../components/ClockTime.vue'
import dayjs from "dayjs";

export default {
  components: {
    ClockTime
  },
  data() {
    return {
      themeType: "light", // 默认亮色
      userInfo: [],
      menuIcon: markRaw(DArrowLeft),
      logoutIcon: markRaw(SwitchButton),
      themeIcon: markRaw(Moon),
      curPath: "",
      menuList: [],
      isCollapse: false,
      menuBg: "#ffffff", // 手动调节
      menuFontBg: "#303133",
      menuFontActiveBg: "#309EFF",
      curTime: [],
      intervalId: '',
    };
  },
  methods: {
    // 初始化
    init() {
      // 初始化时间
      this.curTime = [dayjs().format('YYYY-MM-DD HH:mm:ss') + '']
      // 根据当前时间选择主题
      let curTime = new Date().getHours()
      this.themeType = curTime > "18" || curTime < "6" ? "dark" : "light"
      this.changeMenuTheme();
      document.head.querySelector("#theme").setAttribute("href", `/themes/${this.themeType}.css`)
      this.curPath = this.$route.fullPath
      this.menuList = routes[0].children
      showUser().then((res) => {
        if (res.code === SUCCESS) {
          this.userInfo = res.data
        } else {
          showMessage("获取用户信息失败", "error")
        }
      });
    },
    // 修改menu的主题
    changeMenuTheme() {
      if (this.themeType === "light") {
        this.themeIcon = markRaw(Moon)
        this.menuBg = "#ffffff"
        this.menuFontBg = "#303133"
        this.menuFontActiveBg = "#309EFF"
      } else {
        this.themeIcon = markRaw(Sunny)
        this.menuBg = "#212120"
        this.menuFontBg = "#fff"
        this.menuFontActiveBg = "#ffd04b"
      }
    },
    // 缩放菜单栏
    menuClick() {
      let leftMenuCard = document.getElementById("left-card-container")
      let leftMenuBox = document.getElementById("left-container")
      let logoName = document.getElementById("logo-name")
      let logo = document.getElementById("logo")
      if (this.isCollapse) {
        this.isCollapse = false;
        this.menuIcon = markRaw(DArrowLeft);
        leftMenuCard.style.minWidth = "220px";
        leftMenuCard.style.width = "220px";
        leftMenuCard.children[0].style.width = "200px";
        leftMenuBox.style.width = "200px";
        logoName.innerHTML = "WBS Dashboard";
        logoName.style.width = "200px";
        logo.style.width = "200px";
      } else {
        this.isCollapse = true;
        this.menuIcon = markRaw(DArrowRight);
        leftMenuCard.style.minWidth = "84px";
        leftMenuCard.style.width = "84px";
        leftMenuCard.children[0].style.width = "64px";
        leftMenuBox.style.width = "64px";
        logoName.innerHTML = "WBS";
        logoName.style.width = "64px";
        logo.style.width = "64px";
      }
    },
    // 退出登录
    logout() {
      showMessageBox("确认退出登录吗？需要重新输入密码登录！", "退出登录").then(
        () => {
          logoutAdmin().then((res) => {
            if (res.code === SUCCESS) {
              showMessage(this.userInfo.username + " 退出成功！")
              setTimeout("location.reload()", 500)
            } else {
              showMessage(this.userInfo.username + " 退出失败！")
            }
          });
        }
      ).catch(() => { showMessage("取消退出", 'info') });
    },
    // 修改主题
    changeTheme() {
      if (this.themeType === "light") {
        // 改为dark
        this.themeType = "dark"
        this.themeIcon = markRaw(Sunny)
      } else {
        // 改为light
        this.themeType = "light";
        this.themeIcon = markRaw(Moon)
      }
      this.changeMenuTheme();
      document.head
        .querySelector("#theme")
        .setAttribute("href", `/themes/${this.themeType}.css`)
    },
  },
  mounted() {
    this.init();
    // 监听路由，修改面包屑
    onBeforeRouteUpdate((to) => {
      this.curPath = to.path
    });
    // 设置时钟
    this.intervalId = setInterval(() => {
      this.curTime = [dayjs().format('YYYY-MM-DD HH:mm:ss') + '']
    }, 1000);
  },
  beforeUnmount() {
    if (this.intervalId) {
      clearInterval(this.intervalId)
    }
  }

};
</script>

<style>
#nav {
  background: var(--leftBg);
}

#nav .el-menu {
  border-right: none;
}

#nav .el-card {
  transition: all 0s;
}

.el-menu span,
.el-menu a {
  text-decoration: none;
}

.el-header {
  background: var(--headBg);
  color: #66615b;
  text-align: center;
  vertical-align: middle;
  line-height: 60px !important;
}

.el-main {
  padding: 0px;
  background: var(--rightBg);
}

#left-container .logo {
  width: 200px;
  height: 60px;
  line-height: 60px;
  text-align: center;
  transition: all 0.3s;
  vertical-align: middle;
}

.el-menu-item:hover {
  background: var(--menuHoverBg);
  border-radius: 5px;
}

.el-menu .is-active {
  background-color: var(--menuActiveBg);
  border-radius: 5px;
}

#left-container .el-menu .is-active:after {
  transform: scaleY(1);
  opacity: 1;
  transition: transform 0.15s cubic-bezier(0.645, 0.045, 0.355, 1),
    opacity 0.15s cubic-bezier(0.645, 0.045, 0.355, 1);
}

#left-container .logo h1 {
  vertical-align: middle;
  color: var(--headFontBg);
  font-size: 21px;
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Roboto", "Oxygen",
    "Ubuntu", "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue",
    sans-serif;
  font-weight: 700;
  vertical-align: middle;
}

#admin-header-box {
  display: flex;
  max-height: 60px;
  height: 60px;
  line-height: 60px;
}

#logo {
  border-bottom: 1px solid #dddddd;
}

.header-container {
  display: table-cell;
  align-items: center;
  justify-content: center;
  vertical-align: middle;
  text-align: justify;
  line-height: 60px;
  height: 60px;
  overflow: hidden;
}

#menu-btn {
  position: absolute;
  margin-top: 16px;
}

#left-container,
#logo {
  width: 200px;
  transition: width 0.26s;
  -webkit-transition: width 0.26s;
  -moz-transition: width 0.26s;
  -webkit-transition: width 0.26s;
  -o-transition: width 0.26s;
}

.header-left-box {
  height: 60px;
  line-height: 60px;
}

.header-right-box {
  margin-left: auto;
  vertical-align: middle;
  text-align: center;
  line-height: 60px;
  display: flex !important;
  flex-basis: auto;
}

.header-right-box .el-avatar {
  margin-top: 10px;
}

.header-right-menu {
  flex-direction: row;
  -webkit-box-orient: horizontal;
  -webkit-box-direction: normal;
  margin-left: auto !important;
}

.header-right-menu li {
  float: left;
  display: list-item;
  text-align: -webkit-match-parent;
  list-style: none;
  margin-left: 15px;
}

.el-main-container {
  bottom: 0;
}

#app,
html,
body,
.common-layout,
.el-container,
.el-left-container,
#left-container,
.base-view-container,
#left-container .el-container {
  height: 100%;
}

#left-menu-list-box {
  overflow: hidden;
}

#left-container .el-container {
  background: var(--leftBg);
}

/* 左侧菜单卡片 */
#left-card-container {
  border-radius: 0;
  border-top: 0;
  border-left: 0;
  border-bottom: 0;
  min-width: 220px;
  background: var(--leftBg);
}

#left-card-container .el-card__body {
  padding-top: 0;
  padding-bottom: 0;
  padding-left: 10px;
  padding-right: 10px;
  width: 200px;
}

/* 渐变设置 */
.MainFade-enter-from,
.MainFade-leave-to {
  transform: translateX(20px);
  opacity: 0;
}

.MainFade-enter-to,
.MainFade-leave-from {
  opacity: 1;
}

.MainFade-enter-active {
  transition: all 0.7s ease;
}

.MainFade-leave-active {
  transition: all 0.3s cubic-bezier(0.68, -0.15, 0.27, 0.55);
}
</style>
