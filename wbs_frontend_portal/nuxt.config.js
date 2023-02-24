// const httpUrl = "http://localhost:2022"
const httpUrl = "http://120.48.126.17:11453"
const getUrl = (str) => {
    return httpUrl + "/" + str + "/"
}

export default {
    // Global page headers: https://go.nuxtjs.dev/config-head
    head: {
        title: 'blog_system_portal',
        htmlAttrs: {
            lang: 'en'
        },
        meta: [{
            charset: 'utf-8'
        },
            {
                name: 'viewport',
                content: 'width=device-width, initial-scale=1'
            },
            {
                hid: 'description',
                name: 'description',
                content: ''
            },
            {
                name: 'format-detection',
                content: 'telephone=no'
            }
        ],
        link: [{
            rel: 'icon',
            type: 'image/x-icon',
            href: '/favicon.ico'
        }, {
            rel: "stylesheet",
            href: "/themes/light.css",
            id: "theme"
        },],
    },

    // Global CSS: https://go.nuxtjs.dev/config-css
    css: [
        'element-ui/lib/theme-chalk/index.css',
        '@/assets/css/font-awesome/css/font-awesome.min.css',
        '@/assets/css/MyHover.css',
    ],

    // Plugins to run before rendering page: https://go.nuxtjs.dev/config-plugins
    plugins: [
        '@/plugins/element-ui',
        '@/plugins/aos.client',
        '@/plugins/vue-typer.client',
        '@/plugins/v-md-editor.client',
        {src: '@/plugins/viewer', ssr: false},
        '@/plugins/check-user',
    ],

    // Auto import components: https://go.nuxtjs.dev/config-components
    components: true,

    // Modules for dev and build (recommended): https://go.nuxtjs.dev/config-modules
    buildModules: [],

    // Modules: https://go.nuxtjs.dev/config-modules
    modules: [
        '@nuxtjs/axios', '@nuxtjs/proxy', 'cookie-universal-nuxt', ['cookie-universal-nuxt', {parseJSON: true}],
    ],

    // Build Configuration: https://go.nuxtjs.dev/config-build
    build: {
        transpile: [/^element-ui/],
        vendor: ['axios'],
        postcss: null,
    },

    axios: {
        proxy: true,
        credentials: true
    },

    proxy: {
        '/user': {
            target: getUrl('user'),
            changeOrigin: true,
            pathRewrite: {
                '^/user': ''
            }
        },
        "/admin": {
            target: getUrl('admin'),
            changeOrigin: true,
            pathRewrite: {
                '^/admin': ''
            }
        },
        "/portal": {
            target: getUrl('portal'),
            changeOrigin: true,
            pathRewrite: {
                '^/portal': ''
            }
        },
    },
}
