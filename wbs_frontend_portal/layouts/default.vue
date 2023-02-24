<template>
    <el-scrollbar :always="true" id="indexScroll" style="height:100%;" ref="scroll">
        <Top></Top>
        <Nuxt/>
        <Footer></Footer>
    </el-scrollbar>

</template>

<script>
import Top from "../components/top.vue";
import Footer from "../components/footer.vue";
import Ribbons from "@/assets/js/Ribbon";

export default {
    data() {
        return {
            scrollRefData: undefined
        }
    },
    provide() {
        return {
            scrollRef:() => this.scrollRefData
        }
    },
    components: {Footer, Top},
    mounted() {
        // 开启背景动画
        Ribbons.start()
        this.scrollRefData = this.$refs.scroll
    },
    beforeDestroy() {
        // 关闭背景动画
        Ribbons.stop()
    }
}
</script>

<style>
</style>
