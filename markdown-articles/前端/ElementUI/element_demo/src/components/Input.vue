<template>
    <div>
      <h1>Input组件的使用</h1>
      <el-input v-model="name" disabled type="textarea"></el-input>

      <el-input v-model="price" :maxlength="10" show-word-limit :minlength="5"></el-input>

      <el-input prefix-icon="el-icon-user-solid" placeholder="请输入用户名" clearable v-model="username"></el-input>


      <el-input suffix-icon="el-icon-star-off" placeholder="请输入密码" show-password type="password" clearable v-model="password"></el-input>


      <h1>事件的使用</h1>
      <el-input v-model="username" @blur="aaa" @focus="bbb" @clear="clears" clearable @input="ccc"></el-input>

      <h1>方法的使用</h1>
      <el-input v-model="username" ref="inputs"></el-input>

      <el-button @click="focusInputs">focus方法</el-button>
      <el-button @click="blurInputs">blur方法</el-button>


    </div>
</template>

<script>
    export default {
        name: "Input",
        data() {
            return {
                restaurants: [],
                state1: '',
                state2: '',
                name:'xiaochen',
                price:0.0,
                username:"",
                password:"",
            };
        },
        methods:{
            aaa(){
                console.log('失去焦点');
                ;
            },
            bbb(){
                console.log("获取焦点");
            },
            ccc(value){
                console.log("改变:"+value);
            },
            clears(){
                console.log("清楚");
            },

            focusInputs(){
                this.$refs.inputs.focus();
            },
            blurInputs(){
                this.$refs.inputs.blur();
            }


        }
    }
</script>

<style scoped>

</style>
