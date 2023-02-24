<template>
    <div class="articles-box">
        <div class="articles-header">
            <IndexBgHeader></IndexBgHeader>
        </div>

        <div class="index-content" id="index-content">
            <IndexContent>
                <template v-slot:default>
                    <!--友链页面-->
                    <div class="articles-container">
                        <div class="link-container" v-if="$store.state.links.length">
                            <a :href="item.url" class="link-card" target="_blank" v-for="(item,index) in $store.state.links"
                               :key="item.id">
                                <div class="link-info"
                                     :style="link_card_styles[_keyword % link_card_styles.length]">
                                    <img :src="item.logo" alt/>
                                    <span>{{ item.name }}</span>
                                </div>
                                <div class="desc">前往 {{ item.name }} 的网页</div>
                            </a>
                        </div>
                        <div style="text-align: center; font-size: 20px" v-else>暂无友链，快来申请吧！</div>
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
import {showLinks, SUCCESS} from "@/assets/api/api";

export default {
    name: 'categories',
    components: {IndexBgHeader, IndexContent},
    data() {
        return {
            link_card_styles: [
                "background: linear-gradient(to top, rgb(254, 173, 166) 0%, rgb(245, 239, 239) 100%);",
                "background: linear-gradient(to top, rgb(150, 251, 196) 0%, rgb(249, 245, 134) 100%);",
                "background: linear-gradient(to right, rgb(116, 235, 213) 0%, rgb(159, 172, 230) 100%);",
                "background-image: linear-gradient(to top, #48c6ef 0%, #6f86d6 100%);",
                "background: linear-gradient(to top, rgb(255, 241, 235) 0%, rgb(172, 224, 249) 100%);",
                "background: linear-gradient(to top, rgb(253, 219, 146) 0%, rgb(209, 253, 255) 100%);"
            ]
        }
    },
    // 异步执行，请求数据
    async asyncData(ctx) {
        await getPageNeededData(ctx)
        const linksRes = await showLinks()
        if (linksRes.code === SUCCESS) {
            ctx.store.commit("setLinks", linksRes.data)
        }
    },
    mounted() {
        // 设置标题内容
        document.getElementById("pages-title").innerHTML = "友情链接"
    },
}
</script>

<style>

</style>
