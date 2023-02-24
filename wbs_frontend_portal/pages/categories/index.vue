<template>
    <div class="categories-box">
        <div class="articles-header">
            <IndexBgHeader></IndexBgHeader>
        </div>

        <div class="index-content" id="index-content">
            <IndexContent ref="icRef">
                <template v-slot:default>
                    <!--分类页面内容-->
                    <div class="articles-container">
                        <div style="padding: 0 10px 10px;text-align: left;font-size: 2em;font-weight: 550;">标签
                        </div>
                        <ColorTags v-if="$store.state.labels !== undefined"/>
                        <div style="padding:10px;text-align: left;font-size: 2em;font-weight: 550;">分类
                        </div>
                        <CloudTags v-if="$store.state.categories !== undefined" @clickTag="clickTagItem"/>
                    </div>
                </template>
            </IndexContent>
        </div>
    </div>
</template>

<script>
import ColorTags from "@/components/ColorTags";
import CloudTags from "@/components/CloudTags";
import IndexBgHeader from "@/components/IndexBgHeader";
import IndexContent from "@/components/IndexContent";
import {getPageNeededData} from "@/assets/utils/utils";

export default {
    name: 'categories',
    components: {IndexBgHeader, IndexContent,CloudTags, ColorTags},
    methods: {
        clickTagItem(tag) {
            this.$refs.icRef.clickTagItem(tag)
        }
    },
    // 异步执行，请求数据
    async asyncData(ctx) {
        await getPageNeededData(ctx)
    },
    mounted() {
        // 设置标题内容
        document.getElementById("pages-title").innerHTML = "标签与分类"
    }
}
</script>

<style>

</style>
