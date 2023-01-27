---
title: mysql 查询 时间段内 数据 并 获取每一天的日期
url: https://www.yuque.com/tangsanghegedan/ilyegg/gwgt3o
---

 

```sql
-- 1.建一张 日期表 sys_date 表 获取时间段内 每一天的 日期
SELECT  
  d.rep_date  
FROM  
  sys_date d  
WHERE  
  to_days( d.rep_date ) >= to_days( '2020-04-01' )  
AND  
  to_days( d.rep_date ) <= to_days( '2020-07-01' )
 
 
-- 使用变量 获取时间段内 每一天的 日期
SELECT
	@date := DATE_ADD( @date, INTERVAL + 1 DAY ) days  
FROM
	( SELECT @date := DATE_ADD( "2020-04-01", INTERVAL - 1 DAY ) FROM technology_team_visitor ) time  
WHERE
	to_days( @date ) < to_days( '2020-07-01' )


```
