<template>
    <div class="diary-box">
        <div class="articles-header">
            <IndexBgHeader></IndexBgHeader>
        </div>

        <div class="index-content" id="index-content">
            <IndexContent ref="icRef">
                <template v-slot:default>
                    <!--说说页面-->
                    <div class="articles-container">
                        <!--说说item-->
                        <div class="diary-item" v-for="(item) in $store.state.diaries" :key="item.id">
                            <div class="diary-meta" v-if="item.createTime !== undefined"> {{
                                    item.createTime.split(" ")[0]
                                }}
                            </div>
                            <div class="diary-item-content" style="display: flex;align-items: center;">
                                <p style="flex:1;padding-right: 5px;font-size: 16px">{{ item.content }}</p>
                                <el-image lazy fit="contain" :src="item.user.avatar"
                                          style="width: 50px;height:50px;float: right;border-radius: 100%;border: 1px solid #ddd;vertical-align: center">
                                </el-image>
                            </div>
                        </div>
                        <!-- 说说分页栏 -->
                        <div class="index-content-page-navigation-box">
                            <el-pagination center background layout="prev, pager, next, jumper"
                                           :current-page="$store.state.diariesPageInfo.curPage"
                                           @current-change="onCurDiariesPageChange"
                                           :page-size="$store.state.diariesPageInfo.defSize"
                                           :total="$store.state.diariesPageInfo.totalSize"
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
import {showArticles, showDiaries, SUCCESS} from "@/assets/api/api";

export default {
    name: 'diary',
    components: {IndexBgHeader, IndexContent},
    methods: {
        onCurDiariesPageChange(page) {
            this.$refs.icRef.onCurDiariesPageChange(page)
        }
    },
    // 异步执行，请求数据
    async asyncData(ctx) {
        await getPageNeededData(ctx)
        const diariesRes = await showDiaries(1)  // 缓存第一页说说就可以
        if (diariesRes.code === SUCCESS) {
            ctx.store.commit("setDiaries", diariesRes.data)
        }
    },
    mounted() {
        // 设置标题内容
        document.getElementById("pages-title").innerHTML = "随便说说"
    }
}
</script>

<style>

</style>
