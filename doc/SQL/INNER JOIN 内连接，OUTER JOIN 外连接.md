---
title: INNER JOIN 内连接，OUTER JOIN 外连接
url: https://www.yuque.com/tangsanghegedan/ilyegg/izvhde
---

1. INNER JOIN
2. OUTER JOIN包括

LEFT \[OUTER] JOIN
RIGHT \[OUTER] JOIN
FULL \[OUTER] JOIN 

3. CROSS JOIN

**总结：**
**            两表直接笛卡尔积的结果数量是两表的数据量相乘**
**            带where条件id相等的笛卡尔积和inner join结果相同，但是inner join效率快一点**
**            left join：TEST\_A表的ID为空时拼接TEST\_B表的内容为空，right join则相反**
**            full join：等于left join和right join的并集**
**![image.png](..\assets\izvhde\1605154609959-cd0ad5b2-a730-4744-89da-6d37f8a14aeb.png)**
在使用数据库查询语句时，单表的查询有时候不能满足项目的业务需求，在项目开发过程中，有很多需求都是要涉及到多表的连接查询，总结一下mysql中的多表关联查询 <a name="cvTYo"></a>

# （1）内连接查询

是指所有查询出的结果都是能够在连接的表中有对应记录的。
以t\_employee(员工表)和t\_dept(部门表)为例：
t\_employee表中的记录如下：dept代表该员工所在的部门
![](..\assets\izvhde\1604915577621-b98e3618-5dc5-43de-a38d-2faa8b1c2442.png)
t\_dept表中记录如下：
![](..\assets\izvhde\1604915577589-261d84de-db11-4765-b1bd-355ea5f36be4.png)
可以发现，其中人力资源部里没有员工（这里只是举例，可能与实际不符，但主要在于逻辑关系），而赵七没有对应的部门，现在想要查询出员工姓名以及其对应的部门名称：
此时，就要使用内连接查询，关键字（inner join）
在这里说一下关联查询sql编写的思路，1，先确定所连接的表，2，再确定所要查询的字段，3，确定连接条件以及连接方式

    1. select
    2. e.empName,d.deptName
    3. from t_employee e
    4. INNER JOIN t_dept d
    5. ON e.dept = d.id;

查询的结果如下：
![](..\assets\izvhde\1604915577593-f2e199c8-21a0-41ad-a2c9-78d68e2892d0.png)
其中，没有部门的人员和部门没有员工的部门都没有被查询出来，这就是内连接的特点，只查询在连接的表中能够有对应的记录，其中e.dept = d.id是连接条件

<a name="RuwXH"></a>

# （2）左外连接查询

是指以左边的表的数据为基准，去匹配右边的表的数据，如果匹配到就显示，匹配不到就显示为null。例如：
查询所有员工姓名以及他所在的部门名称：在内连接中赵七没有被查出来，因为他没有对应的部门，现在想要把赵七也查出来，就要使用左外连接：

    1. SELECT e.empName,d.deptName
    2. from t_employee e
    3. LEFT OUTER JOIN t_dept d 
    4. on d.id = e.dept;

在这里，t\_employee就是左表，也就是基准表，用基准表的数据去匹配右表的数据，所以左表的记录是全部会查询出来的，如果右表没有记录对应的话就显示null
查询结果：
![](..\assets\izvhde\1604915577626-7cac843f-6133-4968-a53a-c662fe8e81d2.png)
关键字是left outer join，等效于left join，在关联查询中，做外连接查询就是左连接查询，两者是一个概念 <a name="hJyyv"></a>

# （3）右外连接是同理的，只是基准表的位置变化了而已

比如：查询所有的部门和对应的员工：

    1. SELECT e.empName,d.deptName
    2. from t_employee e
    3. RIGHT OUTER JOIN t_dept d 
    4. on d.id = e.dept;

这里只是把left修改成了right，但是基准表变化了，是以右表的数据去匹配左表，所以左外连接能做到的查询，右外连接也能做到
查询结果：
 ![](..\assets\izvhde\1604915577623-d3964bd4-75ee-48ba-af4d-0d37861709b3.png) <a name="TkTur"></a>

# （4）全外连接

顾名思义，把两张表的字段都查出来，没有对应的值就显示null，想要达到全外连接的效果，可以使用union关键字连接左外连接和右外连接。例如：

```sql
1. select e.empName,d.deptName
2. FROM t_employee e 
3. left JOIN t_dept d
4. ON e.dept = d.id
5. UNION
6. select e.empName,d.deptName
7. FROM t_employee e 
8. RIGHT JOIN t_dept d
9. ON e.dept = d.id;
```

查询结果：
![](..\assets\izvhde\1604915577686-250e6ef2-c90b-4891-a698-763002e972ac.png)
如果在oracle中，直接就使用full outer join关键字连接两表就行了

<a name="SpIBX"></a>

# （5）自连接查询

自连接查询就是当前表与自身的连接查询，关键点在于虚拟化出一张表给一个别名
例如：查询员工以及他的上司的名称，由于上司也是员工，所以这里虚拟化出一张上司表

    1. SELECT e.empName,b.empName
    2. from t_employee e
    3. LEFT JOIN t_employee b
    4. ON e.bossId = b.id;

查询结果：
 ![](..\assets\izvhde\1604915577620-aa66a413-aaa7-4536-85ef-fa8b0340d530.png)
在这里，b表是虚拟化出的表，我们可以通过查询了解b表的记录：

```sql
1. SELECT e.empName,b.empName,b.*
2. from t_employee e
3. LEFT JOIN t_employee b
4. ON e.bossId = b.id;
```

查询结果：
![](..\assets\izvhde\1604915577667-0dde17ce-7151-4f50-8f0a-b6378d83dafe.png)
后面的四个字段就是虚拟化出的b表的所有记录，但看这四个字段其实就是记录所有是上司的员工的信息
所以，自连接查询一般用作表中的某个字段的值是引用另一个字段的值，比如权限表中，父权限也属于权限。
