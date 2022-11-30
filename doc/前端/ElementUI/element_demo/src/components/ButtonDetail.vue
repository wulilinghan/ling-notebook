<template>
  <div>
    <h1>创建简单按钮</h1>
    <el-button>默认按钮</el-button>

    <h1>使用按钮的属性</h1>
    <el-button type="success" size="mini" autofocus plain round loading>primary</el-button>
    <el-button type="success" size="small" disabled plain round>primary</el-button>
    <el-button type="success" size="medium" plain round circle icon="el-icon-loading"></el-button>

    <h1>按钮组的使用</h1>
    <el-button-group>
      <el-button type="primary" icon="el-icon-back">上一页</el-button>
      <el-button type="primary" icon="el-icon-right">下一页</el-button>
    </el-button-group>

  </div>
</template>

<script>
    export default {
        name: "ButtonDetail"
    }
</script>

<style scoped>

</style>
