<template>
    <div>
      <h1>DatePicker组件的使用</h1>
      <el-date-picker
      v-model="createDate"
      :editable="false"
      :clearable="false"
      placeholder="请输入创建时间"
      type="daterange"
      start-placeholder="生产时间"
      end-placeholder="过期时间"
      format="yyyy/MM/dd"
    >
    </el-date-picker>
      <h1>日期配置</h1>
      <el-date-picker
        v-model="createDate"
        type="date"
        placeholder="请输入时间"
        :picker-options="pickerOptions"
        @change="aaa"
      >
      </el-date-picker>

    </div>
</template>

<script>
    export default {
        name: "DatePrickers",
        data(){
            return{
                createDate:"",
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() < Date.now();
                    },
                    shortcuts: [{
                        text: '今天',
                        onClick(picker) {
                            picker.$emit('pick', new Date());
                        }
                    }, {
                        text: '昨天',
                        onClick(picker) {
                            const date = new Date();
                            date.setTime(date.getTime() - 3600 * 1000 * 24);
                            picker.$emit('pick', date);
                        }
                    }, {
                        text: '一周前',
                        onClick(picker) {
                            const date = new Date();
                            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7);
                            picker.$emit('pick', date);
                        }
                    }]
                },
                fileList: [{name: 'food.jpeg', url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100'}, {name: 'food2.jpeg', url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100'}]

            }
        },
        methods:{
            aaa(value){
                console.log(value);
            }
        }
    }
</script>

<style scoped>

</style>
