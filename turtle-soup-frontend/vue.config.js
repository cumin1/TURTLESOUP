const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false, // 暂时禁用ESLint检查
  devServer: {
    port: 8081,
    open: true
  }
})
