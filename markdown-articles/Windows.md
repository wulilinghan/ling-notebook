

# 系统问题

## 更换主板后遇见的问题

### 1 无法进入已安装系统

我的情况是能识别到磁盘，但是无法进入到系统，处理办法

进入BIOS ，启动》CMS   开启CMS，然后选择启动盘重启即可

###  2 有线连接无网络

处理办法：重新安装网络驱动

下个360驱动大师网卡版安装即可

http://dl.360safe.com/drvmgr/gwwk__360DrvMgrInstaller_net.exe



## 游戏问题

### 1 win11打游戏弹出快速助手弹窗自动切换至桌面问题

win11打LOL经常不小心碰到 CTRL+WIN+Q快捷键，游戏就会切到桌面弹出快速助手，特别是打团狂按键盘的时候最容易触发了，快恶心的雅痞。

![image-20230430175936839](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230430175943910.png)

解决办法：下一个 [PowerToys](https://docs.microsoft.com/zh-cn/windows/powertoys/install) ，设置一下快捷键映射

![image-20230430180246506](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230430180246544.png)



# IPV6

参考文档：https://ipw.cn/doc/ipv6/user/enable_ipv6.html

## 1. 查看网络前缀访问优先级

```
netsh interface ipv6 show prefixpolicies
```

我们先看下Windows10/11 中各个访问 IPv4/IPv6的优先级，可以看到 IPv6(`::/0`)比 IPv4(`::ffff:0:0/96`) 的优先级高，会被优先访问。

> 第一列优先循序越大优先级越高，会优先访问。

```
E:\wuliling\Desktop>netsh interface ipv6 show prefixpolicies
查询活动状态...

优先顺序    标签   前缀
----------  -----  --------------------------------
        50      0  ::1/128
        40      1  ::/0
        35      4  ::ffff:0:0/96
        30      2  2002::/16
         5      5  2001::/32
         3     13  fc00::/7
         1     11  fec0::/10
         1     12  3ffe::/16
         1      3  ::/96


E:\wuliling\Desktop>
```



# SSH公钥

文件管理器输入框输入  %homepath%  就是用户目录了

```
ssh-keygen -o
```

> 首先 `ssh-keygen` 会确认密钥的存储位置（默认是 `.ssh/id_rsa`），然后它会要求你输入两次密钥口令。 如果你不想在使用密钥时输入口令，将其留空即可。 然而，如果你使用了密码，那么请确保添加了 `-o` 选项，它会以比默认格式更能抗暴力破解的格式保存私钥。 你也可以用 `ssh-agent` 工具来避免每次都要输入密码。

用户目录下`.ssh`目录中的 `id_rsa.pub` 文件内容就是公钥内容，把它放入github或者gitlab中

# Maven

maven config：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <localRepository>D:\work-space\repo\repository_boot</localRepository>

  <pluginGroups></pluginGroups>
  <proxies></proxies>
  <servers></servers>
  <mirrors></mirrors>

  <profiles>
	<profile>  
		<id>aliyun</id>
		<repositories>
			<!-- repo1 -->
			<repository>
				<id>aliyunmaven</id>
				<name>aliyunmaven</name>
				<url>https://maven.aliyun.com/repository/public</url>
			</repository>
			<!-- repo2 -->
			<repository>
				<id>aliyunmavenApache</id>
				<url>https://maven.aliyun.com/repository/apache-snapshots</url>
			</repository>
			<!-- repo3 -->
			<repository>
				<id>spring</id>
				<url>https://maven.aliyun.com/repository/spring</url>
				<releases>
					<enabled>true</enabled>
				</releases>
				<snapshots>
					<enabled>true</enabled>
				</snapshots>
			</repository>
		</repositories>  
	</profile>
	<profile>  
		<id>camunda.com</id>
		<repositories>
			<repository> 
			  <id>activiti-repos2</id> 
			  <name>Activiti Repository 2</name> 
			  <url>https://app.camunda.com/nexus/content/groups/public</url> 
			</repository> 
		</repositories>  
	</profile>
	<profile>  
		<id>MavenCentral</id>
		<repositories>
			<!-- repo1 -->
			<repository> 
			  <id>central-repos</id> 
			  <name>Central Repository</name> 
			  <url>http://repo.maven.apache.org/maven2</url> 
			</repository>
			<!-- repo2 -->
			<repository> 
				<id>central-repos1</id> 
				<name>Central Repository 2</name> 
				<url>http://repo1.maven.org/maven2/</url> 
			</repository>
		</repositories>  
	</profile>
	</profiles>
</settings>
```





# Redis

启动脚本

```bat
@echo off
REM 替换为 Redis 的安装路径
set REDIS_PATH="D:\SoftEnv\redis\redis-server.exe"
set REDIS_CONF="D:\SoftEnv\redis\redis.windows.conf"

REM 检查 Redis 服务器是否已经运行
tasklist /fi "imagename eq redis-server.exe" | find "redis-server.exe" > nul
if %errorlevel% equ 0 (
    echo Redis server is already running.
    cmd /k echo
)

REM 替换为 Redis 的安装路径
cd /d D:\SoftEnv\redis

REM 启动 Redis 服务器
REM start %REDIS_PATH% %REDIS_CONF%
redis-server.exe redis.windows.conf

echo Starting Redis server....
timeout /t 5 > nul

REM 检查 Redis 服务器是否成功启动
tasklist /fi "imagename eq redis-server.exe" | find "redis-server.exe" > nul
if %errorlevel% equ 0 (
    echo Redis server has been successfully started.
) else (
    echo Redis server failed to start.
)

```



# MySQL

## 卸载服务

卸载服务
```
mysqld --remove
```

注册表删除
```
HKEY\_LOCAL\_MACHINE\SYSTEM\ControlSet001\Services\Eventlog\Application\MySQL
HKEY\_LOCAL\_MACHINE\SYSTEM\ControlSet002\Services\Eventlog\Application\MySQL
HKEY\_LOCAL\_MACHINE\SYSTEM\CurrentControlSet\Services\Eventlog\Application\MySQL
```

然后删除MySQL安装文件夹即可

## 服务安装

初始化MySQL

```
mysqld.exe --install mysql --defaults-file="my.ini"
```

不安装服务启动MySQL

```
mysqld.exe --defaults-file="my.ini"
```

安装服务启动

```
mysqld --install MySQL --defaults-file=D:\developmentEnv\mysql\my.ini
```

> - mysqld –-console 	       # 显示控制台输出
> - mysqld --install                # 安装为服务，缺省服务名为 mysql，服务自动启动
> - mysqld --install-manual # 安装为服务，缺省服务名为 mysql，服务手动启动
> - mysqld --install MySQL(serviceName) --defaults-file=C:\my-opts.ini
> - mysqld --remove             # 移除服务，服务名为 mysql



# RabbitMQ

安装erlang和rabbitmq后，进入rabbitmq\sbin目录,打开cmd，cmd输入以下命令开启web页面插件

```
rabbitmq-plugins enable rabbitmq_management
```

# Alist

> 下载地址：https://github.com/alist-org/alist/releases

解压下载 `alist-windows-xxxx.zip`文件，我这里下载的版本是：[alist-windows-386.zip](https://github.com/alist-org/alist/releases/download/v3.9.0/alist-windows-386.zip)

## 安装成服务

我用了 `InstallUtil.exe`工具 和 `sc` 命令都安装不了

![image-20230131205436599](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202301312054627.png)

![image-20230131205515823](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202301312055846.png)



### winsw

> 下载地址：https://github.com/winsw/winsw/releases

我这用 winsw来安装，这是我下载的版本：https://github.com/winsw/winsw/releases/download/v2.12.0/WinSW.NET4.exe

下载放到`alist.exe`同级目录下，然后新增一个跟下载下来winsw同名的xml配置文件 `WinSW.NET4.xml `

```xml
<configuration>
  
  <!-- ID of the service. It should be unique accross the Windows system-->
  <id>alist</id>
  <!-- Display name of the service -->
  <name>alist</name>
  <!-- Service description -->
  <description>一个支持多种存储的文件列表程序，使用 Gin 和 Solidjs。</description>
  
  <!-- Path to the executable, which should be started --> 	
  <executable>D:\Soft\Alist\alist.exe</executable>
  <arguments>server</arguments>
    
  <startmode>Automatic</startmode>

</configuration>
```

> id：安装windows服务后的服务ID，必须是唯一的。
> name：服务名称，也必须是唯一的。一般和id一致即可。
> description：服务说明，可以使用中文，可做备注使用。
> executable：执行的命令，比如启动springboot应用的命令java。
> arguments：命令执行参数，比如 包路径，类路径等。

![image-20230131211349633](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202301312113657.png)

打开 cmd 

![](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301312035044.png)

注册服务，我这里cmd窗口没用管理员权限打开，会弹出窗口提示授权，确认是

```
WinSW.NET4.exe install
```
- uninstall：删除服务
- start：启动服务
- stop：停止服务
- restart：重启服务
- status：查看状态
- help：查看帮助

![image-20230131211524344](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202301312115369.png)



获取管理员账号信息，进入alist.exe目录下打开cmd，执行以下命令

```
.\alist.exe admin
```

> 记得需要在 alist 服务停止的情况下执行

![image-20230131220221371](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202301312202398.png)

win + r 输入 `services.msc`，找到 alist 启动



http://localhost:5244/@login

admin

rM2PB92W
