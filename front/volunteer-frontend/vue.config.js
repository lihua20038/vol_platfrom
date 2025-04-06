const {
  defineConfig
} = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: ['element-plus'],
  devServer: {
    host: '127.0.0.1',
    port: 8081, // 设置开发服务器的端口号
    proxy: {
      '/api': {
        target: 'http://localhost:8080/',
        changeOrigin: true,
        ws: true, //是否代理 websocket
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  }
})