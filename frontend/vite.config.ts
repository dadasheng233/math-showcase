import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:9193',
        changeOrigin: true,
        configure: (proxy) => {
          proxy.on('error', (err) => console.error('proxy error:', err))
        }
      }
    }
  }
})
