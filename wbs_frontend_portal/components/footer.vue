<template>
    <footer class="footer">
        <p>
            2022 ©
            <nuxt-link to="#"> woodwhale</nuxt-link>
            - <a href="https://beian.miit.gov.cn" rel="noopener noreferrer">赣ICP备2021006148号-1</a>
        </p>
    </footer>
</template>

<script>
export default {}
</script>

<style scoped>
.footer {
    text-align: center;
    width: 100%;
    margin: 0 auto;
    padding: 1.3rem 2rem;
    color: #909399;
    background: rgba(255, 255, 255, 0.15);
    backdrop-filter: blur(3px);
    transition: all 0.3s;
    animation: bottom-top 1s;
}

.footer p {
    margin: 0;
}
</style>
