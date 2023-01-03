

# 静态 IP 配置
- 修改网卡的配置文件
```shell
# 网卡配置文件路径
cd /etc/sysconfig/network-scripts

# ifcfg-eth0为网卡名称，各机器名字不一样，有的机器会有多个
vi /etc/sysconfig/network-scripts/ifcfg-eth0 
```

- 修改或增加以下配置：

```shell 
ONBOOT=yes  # 将ONBOOT="no"改为ONBOOT="yes"
BOOTPROTO=static # 将dhcp改为static

IPADDR=192.168.1.98  # 配置的静态IP地址
NETMASK=255.255.255.0  # 修改子网掩码
GATEWAY=192.168.1.1  # 修改网关
DNS1=8.8.8.8  # 修改DNS服务器

```

- 重启网络服务
```shell
service network restart 
```



# 安装epel-release软件包

```shell
yum -y install epel-release
```



# 禁用 SELinux 

>  SELinux：即安全增强型 Linux（Security-Enhanced Linux）

​		它是一个 Linux 内核模块，也是 Linux 的一个安全子系统，主要作用是最大限度地减小系统中服务进程可访问的资源（最小权限原则），有的软件对于selinux的安全规则支持不够好，就会建议在安装前把selinux先关闭

SELinux  的三种运行模式：

- enforcing：强制模式，SELinux 正在运行中，已经在限制 domain/type

- permissive：宽容模式：SELinux 正在运行中，但仅发出警告信息,并不会实际限制 domain/type 的存取

(permissive模式可以用在测试环境中供调试规则时使用)

- disabled：关闭，SELinux 没有实际运行。

## 查看 SELinux 运行状态

使用 sestatus 或者 getenforce 查看 SELinux 运行状态：

```markdown
# sestatus
[root@localhost ~]# sestatus -v
SELinux status:                 enabled
SELinuxfs mount:                /sys/fs/selinux
SELinux root directory:         /etc/selinux
Loaded policy name:             targeted
Current mode:                   enforcing
Mode from config file:          enforcing
Policy MLS status:              enabled
Policy deny_unknown status:     allowed
Max kernel policy version:      31

Process contexts:
Current context:                unconfined_u:unconfined_r:unconfined_t:s0-s0:c0.c1023
Init context:                   system_u:system_r:init_t:s0
/usr/sbin/sshd                  unconfined_u:unconfined_r:unconfined_t:s0-s0:c0.c1023

File contexts:
Controlling terminal:           unconfined_u:object_r:user_devpts_t:s0
/etc/passwd                     system_u:object_r:passwd_file_t:s0
/etc/shadow                     system_u:object_r:shadow_t:s0
/bin/bash                       system_u:object_r:shell_exec_t:s0
/bin/login                      system_u:object_r:login_exec_t:s0
/bin/sh                         system_u:object_r:bin_t:s0 -> system_u:object_r:shell_exec_t:s0
/sbin/agetty                    system_u:object_r:getty_exec_t:s0
/sbin/init                      system_u:object_r:bin_t:s0 -> system_u:object_r:init_exec_t:s0
/usr/sbin/sshd                  system_u:object_r:sshd_exec_t:s0

# getenforce
[root@localhost ~]# getenforce
Enforcing
```

> Current mode:          enforcing

## 永久关闭 SELinux 

```markdown
vi /etc/selinux/config 

#修改配置项为
SELINUX=disabled
```

> 默认值是: #SELINUX=enforcing



# centos7 升级 centos8

```markdown
#查看当前版本
[root@localhost ~]# cat /etc/redhat-release
CentOS Linux release 7.9.2009 (Core)

#查看版本内核
[root@localhost ~]# uname -r
3.10.0-1160.80.1.el7.x86_64
```

参考：

https://www.cnblogs.com/zeny/p/16578221.html#CentOS_79CentOS_85__4

https://m2.zhugh.com:26800/centos76-sheng-jicentos83/#toc_3
