<template>
    <div>
      <h1>容器组件的使用</h1>
      <!--创建容器-->
      <el-container>
        <!--header-->
        <el-header height="80px"><div><h1>我是标题</h1></div></el-header>
        <el-container>
          <!--aside-->
          <el-aside width="200px"><div><h1>我是菜单</h1></div></el-aside>
          <!--main-->
          <el-main><div><h1>我是中心内容</h1></div></el-main>
        </el-container>
        <el-footer height="100px"><div><h1>我是页脚</h1></div></el-footer>
      </el-container>
    </div>
</template>

<script>
    export default {
        name: "Container"
    }
</script>

<style scoped>
  .el-header, .el-footer {
    background-color: #B3C0D1;
    color: #333;
    text-align: center;
    line-height: 60px;
  }

  .el-aside {
    background-color: #D3DCE6;
    color: #333;
    text-align: center;
    line-height: 200px;
  }

  .el-main {
    background-color: #E9EEF3;
    color: #333;
    text-align: center;
    line-height: 160px;
  }

  body > .el-container {
    margin-bottom: 40px;
  }

  .el-container:nth-child(5) .el-aside,
  .el-container:nth-child(6) .el-aside {
    line-height: 260px;
  }

  .el-container:nth-child(7) .el-aside {
    line-height: 320px;
  }
</style>
