<template>

    <Nuxt/>

</template>

<script>
import Ribbons from "@/assets/js/Ribbon";

export default {
    mounted() {
        // 开启背景动画
        Ribbons.start()
    },
    beforeDestroy() {
        // 关闭背景动画
        Ribbons.stop()
    }
}
</script>

<style>
</style>
