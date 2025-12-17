import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
    plugins: [vue()],
    base:  '/frontend/', 
    build: {
        outDir: 'dist', // 打包输出目录，可自定义
    },
    resolve: {
        alias: {
            '@': resolve(__dirname, 'src')
        }
    },
    server: {
        port: 33300,
        open: {
            app: {
                name: 'google chrome'
            }
        },
        proxy: {
            // 开发环境：代理所有API请求到后端服务器
            // 生产环境：需要在 Nginx 中配置相同的代理规则
            '/api': {
                target: 'http://39.97.60.191:33380',
                changeOrigin: true,
                secure: false,
                configure: (proxy, _options) => {
                    proxy.on('error', (err, _req, _res) => {
                        console.log('API proxy error', err);
                    });
                    proxy.on('proxyReq', (proxyReq, req, _res) => {
                        console.log('API Request:', req.method, req.url);
                    });
                    proxy.on('proxyRes', (proxyRes, req, _res) => {
                        console.log('API Response:', proxyRes.statusCode, req.url);
                    });
                }
            }
        }
    }
})






