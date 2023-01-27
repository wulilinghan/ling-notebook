<template>
  <div>
    <h1>Table表格组件的使用</h1>
    <el-table  ref="mytable" :data="tableData.filter(data => !search || data.name.toLowerCase().includes(search.toLowerCase()))" @select="selectRow" empty-text="暂无数据" :row-class-name="showCss" highlight-current-row :show-header="true" :fit="true"
              size="mini" :height="600" border>
      <el-table-column type="selection"></el-table-column>
      <el-table-column prop="id" :resizable="false" header-align="left" align="center" fixed="left" width="200px;"
                       label="编号">
      </el-table-column>
      <el-table-column prop="name" label="姓名"></el-table-column>
      <el-table-column prop="age" :sort-method="sorts" sortable label="年龄"></el-table-column>
      <el-table-column prop="email" label="邮箱"></el-table-column>
      <el-table-column prop="dept.name" :formatter="showDept" label="部门"></el-table-column>
      <el-table-column>
        <template slot="header" slot-scope="scope">
          <el-input
            v-model="search"
            size="mini"
            placeholder="输入关键字搜索"/>
        </template>
        <template slot-scope="scope">
          <el-button
            size="mini"
            @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
          <el-button
            size="mini"
            type="danger"
            @click="handleDelete(scope.$index, scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-button type="primary" @click="clearSelect">清空选择</el-button>
  </div>
</template>

<script>
    export default {
        name: "Tables",
        data() {
            return {
                tableData: [
                    {
                        id: 21, name: "小陈", age: 23, email: "60037647@qq.com",
                        dept: {id: 1, name: "研发部"}
                    },
                    {
                        id: 22, name: "小张", age: 25, email: "60038647@qq.com",
                        dept: {id: 1, name: "小卖部"}
                    },
                    {
                        id: 23, name: "小李", age: 25, email: "60038657@qq.com",
                        dept: {}
                    },
                    {
                        id: 24, name: "小四", age: 25, email: "60038657@qq.com",
                        dept: {}
                    },
                ],
                search: ''
            }
        },
        methods: {
            sorts(a, b) {
                return a.age - b.age;
            },
            showDept(row, column, cellValue, index) {
                if (cellValue) {
                    return cellValue
                }
                return "暂无部门";
            },
            showCss({row, rowIndex}) {
                if (rowIndex % 2 == 0) {
                    return "warning-row";
                }
                return "success-row";
            },
            selectRow(selection, row){
                console.log(selection);
                console.log(row);
            },
            clearSelect(){
                this.$refs.mytable.clearSelection();
            },
            handleEdit(index,row){
                console.log(index);
                console.log(row);
            },
            handleDelete(index,row){
                console.log(index);
                console.log(row);
            }
        }
    }
</script>

<style>
  .el-table .warning-row {
    background: oldlace;
  }

  .el-table .success-row {
    background: #f0f9eb;
  }
</style>
