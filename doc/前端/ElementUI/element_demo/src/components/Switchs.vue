<template>
    <div>
      <h1>Switch组件的使用</h1>
      <el-switch ref="sw" v-model="value" @change="aaa" active-text="打开" active-color="#13ce66" inactive-color="#ff4949" :active-value="true" :inactive-value="false" inactive-text="关闭" :width="200"></el-switch>


      <el-button @click="bbb">调用方法</el-button>
    </div>
</template>

<script>
    export default {
        name: "Switchs",
        data(){
            return{
                value:true
            }
        }
        ,
        methods:{
            aaa(value){
                console.log(value);
            },
            bbb(){
                alert();
                this.$refs.sw.focus();//方法调用
            }
        }
    }
</script>

<style scoped>
  .aa{
    background-color: red;
  }
</style>
