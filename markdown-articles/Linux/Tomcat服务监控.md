---
title: Tomcat服务监控
url: https://www.yuque.com/tangsanghegedan/ilyegg/vdiu42
---

> Tomcat版本： apache-tomcat-8.5.61.tar.gz

<a name="AK1xe"></a>

# Tomcat manager监控

使用tomcat自带的manager应用对服务进行监控。
![image.png](..\assets\vdiu42\1619351409073-f0d3a754-9fc9-4f81-9cc9-7e1de932891a.png)
![image.png](..\assets\vdiu42\1619351225996-2f9d4055-043a-4502-b9c9-c4ddc7797a63.png)

1. 需要创建角色manager和对应的用户才能使用。

- 先找到***webapps/manager/META-INF/context.xml***文件，将这行内容注释掉，取消IP限制

![image.png](..\assets\vdiu42\1619352903245-bf2fbc1c-cc97-4a48-9361-f4359b79cb1d.png)

- 找到***conf/tomcat-users.xml***配置文件，添加角色账号：

```xml
<role rolename="manager-gui"/>
<user username="tomcat" password="123456" roles="manager-gui"/>
```

![image.png](..\assets\vdiu42\1619351834718-e57c8b6f-1037-4a1a-92cf-f6ca5f8fa560.png)
![image.png](..\assets\vdiu42\1619352190087-7e93b660-a7dd-455c-bb75-02cd42eb6564.png)

2. 启动或重启Tomcat，访问[http://ip:port/manager/html](http://124.71.56.21:8020/manager/html)，输入tomcat/123456：

![image.png](..\assets\vdiu42\1619353039231-e868e55e-a4d3-4916-97b7-1f75852adce8.png) <a name="wzyEe"></a>

# psi-probe监控

> 相比较自带的manager更为强大

1. 地址：<https://github.com/psi-probe/psi-probe/releases>
2. 下载war包后丢到webapps下，通过[http://ip:port/probe](http://124.71.56.21:8020/probe)访问，输入tomcat/123456：

![image.png](..\assets\vdiu42\1619353715820-69bc65c8-43c2-4143-9b6b-6e0f236ff06e.png)

> Data Sources 	查看tomcat连接池
> Deployment 	项目部署
> Logs 		查看日志文件
> Threads 		查看线程
> Cluster
> System		可以查看内存的使用情况以及电脑硬件信息
> Connectors	查看tomcat连接情况
