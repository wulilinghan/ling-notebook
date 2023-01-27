<template>
    <div>
      <h1>layout组件使用</h1>
      <!--创建页面中一行-->
      <el-row>
        <el-col :span="6"><div style="border: 1px red solid;">占用6份</div></el-col>
        <el-col :span="6"><div style="border: 1px red solid;">占用6份</div></el-col>
        <el-col :span="6"><div style="border: 1px red solid;">占用6份</div></el-col>
        <el-col :span="6"><div style="border: 1px red solid;">占用6份</div></el-col>
      </el-row>
      <br>
      <!--创建页面中一行-->
      <el-row :gutter="50" tag="span">
        <el-col :span="4"><div style="border: 1px red solid;">占用4份</div></el-col>
        <el-col :span="8"><div style="border: 1px red solid;">占用8份</div></el-col>
        <el-col :span="3"><div style="border: 1px red solid;">占用3份</div></el-col>
        <el-col :span="9"><div style="border: 1px red solid;">占用9份</div></el-col>
      </el-row>

      <h1>layout组件中使用偏移</h1>
      <el-row>
        <el-col :span="12" :offset="9" :psuh="3" xs><div style="border: 1px blue solid;">我是占用12分</div></el-col>
        <el-col :span="6"><div style="border: 1px blue solid;">我是占用6分</div></el-col>
      </el-row>

    </div>
</template>

<script>
    export default {
        name: "Layout"
    }
</script>

<style scoped>

</style>
