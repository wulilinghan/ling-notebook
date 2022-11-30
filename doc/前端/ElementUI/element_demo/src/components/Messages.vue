<template>
    <div>
      <h1>alert消息提示</h1>
      <el-alert title="成功信息提示" @close="close" effect="dark" :show-icon="true" center :closable="false" type="success">
        <div slot>我是辅助信息</div>
      </el-alert>
      <el-alert title="成功信息提示" @close="close" :show-icon="true" close-text="关闭" type="info"></el-alert>
      <el-alert title="成功信息提示" :show-icon="true" type="warning"></el-alert>
      <el-alert title="成功信息提示" :show-icon="true" type="error"></el-alert>

      <h1>Message消息提示</h1>
      <el-button type="primary" @click="showMsg">点击展示消息提示</el-button>
      <el-button type="primary" @click="closeMsg">调用关闭提示的方法</el-button>
    </div>
</template>

<script>
    export default {
        name: "Message",
        methods:{
            close(){
                console.log("xxxx");
            },
            showMsg(){
                const h = this.$createElement;
                this.$message({
                    message:'这是信息提示',
                    type:"success",
                    showClose:true,
                    center:true,
                    iconClass:'el-icon-user-solid',
                    duration:0
                })
            },
            closeMsg(){
              this.$message.closeAll();
            }
        }
    }
</script>

<style scoped>

</style>
