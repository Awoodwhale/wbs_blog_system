<template>
  <div>
    <!-- 面包屑 -->
    <div class="content-title-box">
      <el-card id="breadcrumb-card">
        <el-breadcrumb>
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <template v-for="(item, index) in breadList">
            <el-breadcrumb-item v-if="item.name" :key="index" :to="item.path">{{
              item.name
            }}</el-breadcrumb-item>
          </template>
        </el-breadcrumb>
      </el-card>
    </div>
    <!-- 路由内容 -->
    <div class="content-container-box">
      <div class="content">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      breadList: [],
    };
  },
  methods: {
    getMatched() {
      this.breadList = this.$route.matched;
    },
  },
  created() {
    this.getMatched();
  },
  watch: {
    $route(to, from) {
      this.breadList = this.$route.matched;
    },
  },
};
</script>

<style>
.content-title-box {
  padding-left: 0px;
  padding-right: 0px;
  padding-top: 0px;
  padding-bottom: 5px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Roboto", "Oxygen",
    "Ubuntu", "Cantarell", "Fira Sans", "Droid Sans", "Helvetica Neue",
    sans-serif;
  vertical-align: middle;
  position: sticky;
  top: 0;
  z-index: 100;
}

.content-container-box {
  padding-left: 20px;
  padding-right: 20px;
  padding-top: 5px;
  padding-bottom: 5px;
}

.el-breadcrumb {
  font-size: 15px;
}

.content-title-box #breadcrumb-card {
  border-radius: 0;
  border-left: 0;
  border-right: 0;
  background: var(--headBg);
  border-top: 1px solid #dddddd;
  border-bottom: 1px solid #dddddd;
}

.content .el-card {
  border-radius: 5px;
}
</style>
