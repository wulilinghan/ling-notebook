

# 1.网卡配置
- 修改网卡的配置文件
```shell
# 网卡配置文件路径
cd /etc/sysconfig/network-scripts

# ifcfg-eth0为网卡名称，各机器名字不一样，有的机器会有多个
vi /etc/sysconfig/network-scripts/ifcfg-eth0 
```

- 修改信息如下：

```shell 
E=eth0 #网卡名称
HWADDR=00:0c:29:5c:94:74 #物理地址

ONBOOT=yes  #将ONBOOT="no"改为ONBOOT="yes"
BOOTPROTO=static #将dhcp改为static

IPADDR=192.168.1.98  # 修改IP地址
NETMASK=255.255.255.0  # 修改子网掩码
GATEWAY=192.168.1.1  # 修改网关
DNS1=8.8.8.8  # 修改DNS服务器

```

- 重启网络服务
```shell
service network restart 
```



# 2.安装epel-release软件包

```shell
yum -y install epel-release
```



# 3.centos7 升级 centos8

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
