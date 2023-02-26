# 初始化 trigger SQL文件报错

SQL脚本中有创建trigger的语句，使用默认的分隔符执行报错

解决办法是更换分隔符，默认是 **`;`** ，我这里换成 **`^;`**

```
spring:
    datasource:
        url: jdbc:sqlite:dbs/sqlite/jmb.db
        driver-class-name: org.sqlite.JDBC
    sql:
        init:
            mode: ALWAYS
            separator: ^; # 使用默认 ; 分隔符执行trigger报错，这里换成 ^;
            schema-locations:
                - classpath:dbs/sqlite/schema.sql
                - classpath:dbs/sqlite/trigger.sql
```

<img src="https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302051144255.png" alt="image-20230205114131782"  />

![image-20230205114200631](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302051628455.png)