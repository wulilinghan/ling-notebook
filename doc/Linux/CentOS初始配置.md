

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
