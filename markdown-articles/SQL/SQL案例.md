---
title: 
url: 
---

 

# 1.mysql 查询时间段内数据并获取每一天的日期

```sql
-- 方法1 建一张 日期表 sys_date 表获取时间段内每一天的日期
SELECT  
  d.rep_date  
FROM  
  sys_date d  
WHERE  
  to_days( d.rep_date ) >= to_days( '2020-04-01' )  
AND  
  to_days( d.rep_date ) <= to_days( '2020-07-01' )
 
 
-- 方法2 使用变量获取时间段内每一天的日期
SELECT
	@date := DATE_ADD( @date, INTERVAL + 1 DAY ) days  
FROM
	( SELECT @date := DATE_ADD( "2020-04-01", INTERVAL - 1 DAY ) FROM technology_team_visitor ) time  
WHERE
	to_days( @date ) < to_days( '2020-07-01' )


```



# 2. 用多个ID作为查询条件，但是表里一个ID会有多条数据，我想根据创建时间排序，然后取每个ID的第二条数据，SQL该怎么写

```sql
SELECT id, 
		created_time
FROM (
  SELECT id, created_time, 
    ROW_NUMBER() OVER (PARTITION BY id ORDER BY created_time DESC) AS row_num
  FROM your_table
  WHERE id IN (1, 2, 3, 4) -- 这里列出了多个ID作为查询条件，你可以根据实际情况进行修改
) ranked
WHERE row_num = 2;
```

> MySQL 5.7 版本并不支持该函数

可以使用 MySQL 版本中的用户变量（user-defined variables）来模拟 `ROW_NUMBER()` 窗口函数的效果。

```sql
SELECT id, created_time
FROM (
  SELECT id, created_time
    @row_number := IF(@current_id = id, @row_number + 1, 1) AS row_number,
    @current_id := id
  FROM your_table, (SELECT @row_number := 0, @current_id := NULL) AS t
  WHERE id IN (1, 2, 3, 4) -- 这里列出了多个ID作为查询条件，你可以根据实际情况进行修改
  ORDER BY id, created_time DESC
) ranked
WHERE row_number = 2;
```

在这个查询语句中，我们使用了一个用户变量 `@row_number` 和一个用户变量 `@current_id` 来模拟 `ROW_NUMBER()` 窗口函数的效果。`@row_number` 变量记录行号，`@current_id` 变量记录当前 ID 的值。当当前行的 ID 与上一行的 ID 相同时，行号加 1；否则，行号重置为 1。最后通过 `WHERE` 子句筛选出行号为 2 的记录。
