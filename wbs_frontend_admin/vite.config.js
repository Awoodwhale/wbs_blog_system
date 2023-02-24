import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";

// TODO:上线修改域名端口
// const domain = "http://localhost:2022"
const domain = "http://localhost:11453"

// https://vitejs.dev/config/
export default defineConfig({
  build: {
    rollupOptions: {
      output: {
        chunkFileNames: 'static/js/[name]-[hash].js',
        entryFileNames: 'static/js/[name]-[hash].js',
        assetFileNames: 'static/[ext]/[name]-[hash].[ext]',
      }
    }
  },
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),
  ],
  server: {
    host: "0.0.0.0",
    proxy: {
      '/user': {
        target: domain + '/user/',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/user/, ""),
      },
      "/admin": {
        target: domain + '/admin/',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/admin/, ""),
      },
      "/portal": {
        target: domain + '/portal/',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/portal/, ""),
      },
    },
    port: 11452,
  },
  preview: {
    port: 11452
  }
});