---
title: Windows
url: https://www.yuque.com/tangsanghegedan/ilyegg/okgc7t
---

<a name="ZViOa"></a>

# MySQL

<a name="qkQ5i"></a>

## 卸载服务

mysqld --remove

注册表删除
**HKEY\_LOCAL\_MACHINE\SYSTEM\ControlSet001\Services\Eventlog\Application\MySQL**
**HKEY\_LOCAL\_MACHINE\SYSTEM\ControlSet002\Services\Eventlog\Application\MySQL**
**HKEY\_LOCAL\_MACHINE\SYSTEM\CurrentControlSet\Services\Eventlog\Application\MySQL**
删除MySQL文件夹即可

<a name="TuHbP"></a>

## 服务安装

初始化MySQL
mysqld.exe --install mysql --defaults-file="my.ini"

不安装服务启动MySQL
mysqld.exe --defaults-file="my.ini"

安装服务启动
mysqld --install MySQL --defaults-file=D:\developmentEnv\mysql\my.ini

> - mysqld –-console # 显示控制台输出
> - mysqld --install # 安装为服务，缺省服务名为 mysql，服务自动启动
> - mysqld --install-manual # 安装为服务，缺省服务名为 mysql，服务手动启动
> - mysqld --install MySQL(serviceName) --defaults-file=C:\my-opts.ini
> - mysqld --remove # 移除服务，服务名为 mysql

<a name="Q2AvR"></a>

# RabbitMQ

安装erlang和rabbitmq后
进入rabbitmq\sbin目录,打开cmd
输入rabbitmq-plugins enable rabbitmq\_management命令开启web页面插件
