---
title: CASE WHEN用法
url: https://www.yuque.com/tangsanghegedan/ilyegg/fe7izr
---

Case具有两种格式。简单Case函数和Case搜索函数。

***

 第一种 格式 : 简单Case函数 :
格式说明    
　　　　case    列名
　　　　when   条件值1    then  选择项1
　　　　when   条件值2    then  选项2.......
　　　　else     默认值      end
eg:
　　　　select
　　　　case 　job\_level
　　　　when     '1'     then    '1111'
　　　　when　  '2'     then    '2222'
　　　　when　  '3'     then    '3333'
　　　　else      'eee'   end
　　　　from     dbo.employee

***

 第二种  格式 :Case搜索函数
格式说明    
　　　　case  
　　　　when  列名= 条件值1   then  选择项1
　　　　when  列名=条件值2    then  选项2.......
　　　　else    默认值 end
eg:
　　　　update  employee
　　　　set         e\_wage =
　　　　case
　　　　when   job\_level = '1'    then e\_wage*1.97
　　　　when   job\_level = '2'   then e\_wage*1.07
　　　　when   job\_level = '3'   then e\_wage*1.06
　　　　else     e\_wage*1.05
　　　　end

***

提示:通常我们在写Case When的语句的时候,会容易忘记 end 这个结束,一定要记得哟!
比较: 两种格式，可以实现相同的功能。
  　　简单Case函数的写法相对比较简洁，但是和Case搜索函数相比，功能方面会有些限制，比如写判断式。还有一个需要注意的问题，Case函数只返回第一个符合条件的     值，剩下的Case部分将会被自动忽略。
