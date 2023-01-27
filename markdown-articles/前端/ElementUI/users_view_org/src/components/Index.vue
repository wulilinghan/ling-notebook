<template>
    <div>
      <el-carousel indicator-position="outside">
        <el-carousel-item v-for="item in imgs"  :key="item">
          <el-image
            :src="item"
            style="height:600px"
            fit="fill"></el-image>
        </el-carousel-item>
      </el-carousel>
    </div>
</template>

<script>
    import homeImg1 from "../assets/indexImgs/1.jpg";
    import homeImg2 from "../assets/indexImgs/2.jpg";
    import homeImg3 from "../assets/indexImgs/3.jpg";
    import homeImg4 from "../assets/indexImgs/4.jpg";
    import homeImg5 from "../assets/indexImgs/5.jpg";
    import homeImg6 from "../assets/indexImgs/6.jpg";
    export default {
        name: "Index",
        data(){
            return {
                imgs:[homeImg1,homeImg2,homeImg3,homeImg4,homeImg5,homeImg6]
            }
        }

    }
</script>

<style scoped>

</style>
