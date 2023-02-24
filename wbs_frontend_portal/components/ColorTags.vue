<template>
    <div>
        <nuxt-link :to="{name:'labels-name',params:{name:item.name}}" v-for="(item,index) in labels" :key="item.id" class="color-tag hvr-float-shadow"
        :style="colors[Math.floor(Math.random()*(colors.length+1))]">
            {{item.name}}
        </nuxt-link>
    </div>
</template>

<script>
export default {
    data() {
        return {
            labels: this.$store.state.labels,
            colors: ["background: rgb(245, 238, 248);", "background: rgb(215, 189, 226);", "background: rgb(163, 228, 215);",
                "background: rgb(213, 245, 227);", "background: rgb(249, 231, 159);", "background: rgb(133, 193, 233);",
                "background: rgb(245, 238, 248);", "background: rgb(248, 249, 249);", "background: rgb(254, 249, 231);",
                "background: rgb(249, 235, 234);"]
        }
    }
}
</script>

<style scoped>
.color-tag {
    background-color: #f9ebea;
    margin: 10px;
    padding: 19px 14px;
    display: -ms-inline-flexbox;
    display: inline-flex;
    line-height: 0;
    font-size: 1rem;
    font-weight: 500;
    border-radius: 5px;
    cursor: pointer;
    -webkit-box-shadow: 0 3px 5px rgb(0 0 0 / 12%);
    box-shadow: 0 3px 5px rgb(0 0 0 / 12%);
    color: #34495e;
    text-decoration: none;
    transition: all 0.2s;
    overflow-wrap: break-word;
}
</style>
