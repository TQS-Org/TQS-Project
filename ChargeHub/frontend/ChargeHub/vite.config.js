import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    host: '0.0.0.0', 
    allowedHosts: ['deti-tqs-23.ua.pt'],
    proxy: {
      '/api': {
        target: 'http://backend:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  }
})
