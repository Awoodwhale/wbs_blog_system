<template>
    <div class="articles-box">
        <div class="articles-header">
            <IndexBgHeader></IndexBgHeader>
        </div>

        <div class="index-content" id="index-content">
            <IndexContent ref="icRef">
                <template v-slot:default>
                    <!--文章归档信息-->
                    <div class="articles-container">
                        <div class="articles-title" v-if="$store.state.blogInfos.article !== undefined">
                            文章归档 - {{ $store.state.blogInfos.article }}篇
                        </div>
                        <div class="articles-sort">
                            <div v-for="(item, index) in $store.state.articles" :key="index" class="articles-item">
                                <nuxt-link class="hvr-grow" :to="'/articles/'+item.id"
                                           style="overflow: hidden;width: 80px;height: 80px;border-radius: 5px;">
                                    <el-image lazy fit="cover" :src="item.cover" v-if="item.cover !== ''"
                                              style="width: 80px;height: 80px;">
                                        <div slot="placeholder" class="image-slot" style="width: 100%; height: 100%;">
                                            <img src="/loading.gif" style="width: 80px;height: 80px;">
                                        </div>
                                        <div slot="error" class="image-slot" style="width: 100%; height: 100%;">
                                            <img src="/loading.gif" style="width: 100%;height: 100%;">
                                        </div>
                                    </el-image>
                                    <el-image lazy fit="cover" :src="$store.state.randomPicUrl + '?' + item.id" v-else
                                              style="width: 80px;height: 80px;">
                                        <div slot="placeholder" class="image-slot" style="width: 100%; height: 100%;">
                                            <img src="/loading.gif" style="width: 80px;height: 80px;">
                                        </div>
                                        <div slot="error" class="image-slot" style="width: 100%; height: 100%;">
                                            <img src="/loading.gif" style="width: 100%;height: 100%;">
                                        </div>
                                    </el-image>
                                </nuxt-link>
                                <div class="articles-item-info">
                                    <div class="articles-item-info-item">
                                        <i class="fa fa-calendar" aria-hidden="true"></i>
                                        <time style="margin-left: 10px">
                                            {{ item.createTime.split(" ")[0] }}
                                        </time>
                                    </div>
                                    <nuxt-link :to="'/articles/'+item.id"
                                               class="articles-item-info-title">
                                        {{ item.title.slice(0, 25) + (item.title.length > 25 ? "..." : "") }}
                                    </nuxt-link>
                                </div>
                            </div>
                        </div>
                        <!-- 分页栏模块 -->
                        <div class="index-content-page-navigation-box">
                            <el-pagination center background layout="prev, pager, next, jumper"
                                           :current-page="$store.state.articlesPageInfo.curPage"
                                           @current-change="onCurArticlesPageChange"
                                           :page-size="$store.state.articlesPageInfo.defSize"
                                           :total="$store.state.articlesPageInfo.totalSize"
                                           :hide-on-single-page="true"/>
                        </div>
                    </div>
                </template>
            </IndexContent>
        </div>
    </div>
</template>

<script>
import IndexBgHeader from "@/components/IndexBgHeader";
import IndexContent from "@/components/IndexContent";
import {getPageNeededData} from "@/assets/utils/utils";

export default {
    scrollToTop: true,
    name: 'articles',
    components: {IndexBgHeader, IndexContent},
    methods: {
        onCurArticlesPageChange(page) {
            this.$refs.icRef.onCurArticlesPageChange(page)
        }
    },
    // 异步执行，请求数据
    async asyncData(ctx) {
        await getPageNeededData(ctx)
    },
    mounted() {
        // 设置标题内容
        document.getElementById("pages-title").innerHTML = "文章归档"
    }
}
</script>

<style>

</style>
