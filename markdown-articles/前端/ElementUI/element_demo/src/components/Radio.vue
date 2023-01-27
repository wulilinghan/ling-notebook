<template>
  <div>

    <h1>Radio组件使用</h1>

    <!--组件创建 属性-->
    <el-radio v-model="label" @change="aa" name="sex" label="男">男</el-radio>
    <el-radio v-model="label" @change="aa" name="sex" border size="small" label="女">女</el-radio>

    <h1>单选按钮组</h1>
    <!--创建单选按钮组-->
    <el-radio-group v-model="radio" @change="aa" fill="#CCCCCC" text-color="#EEEEEE"  size="medium">
      <el-radio-button :label="3" border>备选项3</el-radio-button>
      <el-radio-button :label="6" border>备选项6</el-radio-button>
      <el-radio-button :label="9" border>备选项9</el-radio-button>
    </el-radio-group>
  </div>
</template>

<script>
    export default {
        name: "Radio",
        data() {
            return {
                label: '男',
                radio: 6
            }
        },
        methods: {
            aa() {
                console.log(this.radio);
            }
        }
    }
</script>

<style scoped>

</style>
