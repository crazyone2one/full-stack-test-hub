import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vite.dev/config/
export default defineConfig(({mode}) => {
    const isProduction = mode === 'production'
    return {
        plugins: [vue()],
        server: !isProduction ? {
            proxy: {
                '/front': {
                    target: 'http://127.0.0.1:8080/',
                    changeOrigin: true,
                    rewrite: (path: string) => path.replace(new RegExp('^/front'), ''),
                },
            }
        } : {},
        resolve: {
            alias: [
                {
                    find: /\/@\//,
                    replacement: path.resolve(__dirname, '.', 'src') + '/',
                },
            ]
        },
    }
});
