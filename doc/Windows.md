

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

