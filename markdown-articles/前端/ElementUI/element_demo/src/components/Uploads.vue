<template>
  <div>
    <h1>upload组件的使用</h1>
    <el-upload ref="uploads" :limit="3" :on-exceed="exceed" :multiple="false" :before-remove="beforeRemove" :on-remove="remove" :on-preview="show" :drag="true" accept=".txt,.png" :show-file-list="true" name="aaa" :data="info" action="https://jsonplaceholder.typicode.com/posts/"
               :file-list="fileList">
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <div class="el-upload__tip" slot="tip">只能上传jpg/png文件，且不超过500kb</div>
    </el-upload>

    <el-button @click="clearFiles">调用方法</el-button>


  </div>
</template>

<script>
    export default {
        name: "Uploads",
        data() {
            return {
                fileList: [{
                    name: 'food.jpeg',
                    url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100'
                }, {
                    name: 'food2.jpeg',
                    url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100'
                }],
                info: {id:"21"},
                form: {
                    name: '',
                    region: '',
                    date1: '',
                    date2: '',
                    delivery: false,
                    type: [],
                    resource: '',
                    desc: ''
                }
            }

        },
        methods:{
            show(file){
                console.log(file);
            },
            remove(file,fileList){
                console.log(file);
                console.log(fileList);
                //alert(fileList.length)
            },
            beforeRemove(file,fileList){
                if(fileList.length<3){
                    alert("上传文件不能少于3个")
                    return false;
                }
            },
            exceed(file,fileList){
                alert("文件超出上传的个数限制")
            },
            clearFiles(){
                this.$refs.uploads.clearFiles();
            },
            onSubmit() {
                console.log('submit!');
            }
        }
    }
</script>

<style scoped>

</style>
