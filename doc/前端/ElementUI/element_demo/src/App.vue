<template>
  <div id="app">
    <a href="#/button">点我显示button</a>
    <a href="#/buttondetail">按钮组件的详细使用</a>
    <a href="#/link">文字链接组件</a>
    <a href="#/layout">布局组件的使用</a>
    <a href="#/container">容器布局组件的使用</a>
    <a href="#/containerexam">容器布局组件实例</a>
    <a href="#/radio">radio组件的使用</a>
    <a href="#/checkbox">checkbox组件的使用</a>
    <a href="#/input">input组件的使用</a>
    <a href="#/select">Select组件的使用</a>
    <a href="#/switch">switch组件的使用</a>
    <a href="#/datePickers">DatePicker组件的使用</a>
    <a href="#/upload">Upload组件的使用</a>
    <a href="#/form">Form组件的使用</a>
    <a href="#/msg">Message组件的使用</a>
    <a href="#/table">Table组件的使用</a>
    <br>
    <br>
    <br>
    <router-view/>

  </div>
</template>

<script>
    export default {
        name: 'App',

    }
</script>

<style>

</style>
