<template>
    <div>
      <h1>Select组件的使用</h1>
      <el-select v-model="cityName">
        <el-option value="北京">北京</el-option>
        <el-option value="天津">天津</el-option>
      </el-select>

      <h1>使用远程数据加载下拉列表</h1>
      <el-select ref="selects" v-model="cityId" @change="aaa" multiple clearable>
        <el-option v-for="option in options" :label="option.name" :value="option.id" :key="option.id"></el-option>
      </el-select>

      <el-button @click="bbb">测试方法</el-button>
    </div>
</template>

<script>
    export default {
        name: "Select",
        data(){
            return{
                options:[
                    {id:'1',name:"研发部"},
                    {id:'2',name:"小卖部"},
                    {id:'3',name:"小米部"},
                ],
                cityId:'',
                cityName:''
            }
        },
        methods:{
            aaa(value){
                console.log(value);
            },
            bbb(){
                this.$refs.selects.focus();
            }
        }
    }
</script>

<style scoped>

</style>
