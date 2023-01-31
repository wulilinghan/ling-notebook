

# SSH公钥

文件管理器输入框输入  %homepath%  就是用户目录了

```
ssh-keygen -o
```

> 首先 `ssh-keygen` 会确认密钥的存储位置（默认是 `.ssh/id_rsa`），然后它会要求你输入两次密钥口令。 如果你不想在使用密钥时输入口令，将其留空即可。 然而，如果你使用了密码，那么请确保添加了 `-o` 选项，它会以比默认格式更能抗暴力破解的格式保存私钥。 你也可以用 `ssh-agent` 工具来避免每次都要输入密码。

用户目录下`.ssh`目录中的 `id_rsa.pub` 文件内容就是公钥内容，把它放入github或者gitlab中

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
