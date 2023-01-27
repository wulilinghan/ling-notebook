<template>
    <div>
      <h1>使用文字链接组件</h1>
      <el-link>默认链接</el-link>
      <el-link type="primary" target="_blank" href="http://www.baidu.com" :underline="false">默认链接</el-link>
      <el-link type="success" disabled>默认链接</el-link>
      <el-link type="info" icon="el-icon-platform-eleme">默认链接</el-link>
      <el-link type="warning">默认链接</el-link>
      <el-link type="danger">默认链接</el-link>
    </div>
</template>

<script>
    export default {
        name: "Link",

    }
</script>

<style scoped>

</style>
