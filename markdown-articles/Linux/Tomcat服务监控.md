> Tomcat版本： apache-tomcat-8.5.61.tar.gz

# Tomcatmanager监控

使用tomcat自带的manager应用对服务进行监控。
![image.png](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302012104922.png)
![image.png](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302012104084.png)

1. 需要创建角色manager和对应的用户才能使用。

- 先找到***webapps/manager/META-INF/context.xml***文件，将这行内容注释掉，取消IP限制

![image.png](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302012106344.png)

- 找到***conf/tomcat-users.xml***配置文件，添加角色账号：

```xml
<role rolename="manager-gui"/>
<user username="tomcat" password="123456" roles="manager-gui"/>
```

![image.png](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302012105799.png)
![image.png](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302012105711.png)

2. 启动或重启Tomcat，访问[http://ip:port/manager/html](http://124.71.56.21:8020/manager/html)，输入tomcat/123456：

![image.png](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302012105637.png) 

# psi-probe监控

> 相比较自带的manager更为强大

1. 地址：<https://github.com/psi-probe/psi-probe/releases>
2. 下载war包后丢到webapps下，通过[http://ip:port/probe](http://124.71.56.21:8020/probe)访问，输入tomcat/123456：

![image.png](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302012106456.png)

> Data Sources 	查看tomcat连接池
> Deployment 	项目部署
> Logs 		查看日志文件
> Threads 		查看线程
> Cluster
> System		可以查看内存的使用情况以及电脑硬件信息
> Connectors	查看tomcat连接情况
