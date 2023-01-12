# qinglong

> https://github.com/whyour/qinglong
>
> 支持python3、javaScript、shell、typescript 的定时任务管理面板

```Markdown
# 1.拉取镜像
	docker pull whyour/qinglong:latest

# 2.启动命令
	docker run -dit -v $PWD/ql/config:/ql/config -v $PWD/ql/log:/ql/log -v $PWD/ql/db:/ql/db -p 5700:5700 --name qinglong --hostname qinglong --restart always whyour/qinglong:latest 

# 3.初次访问
 	http://192.168.3.50:5700
	去自己映射目录config下找 auth.json，查看里面的 password
	docker exec -it qinglong cat /ql/config/auth.json 

# 4.访问地址
	http://192.168.3.50:5700

```

# UnblockNeteaseMusic

> 项目地址：https://github.com/UnblockNeteaseMusic/server
> 网易云历史版本：https://blog.amarea.cn/archives/netease-cloudmusic-history-version.html

```markdown
# 1.拉取命令
docker pull pan93412/unblock-netease-music-enhanced

# 2.启动命令
docker run --name unblock-netease-music --restart always -p 18080:8080 -e ENABLE_FLAC=true -e ENABLE_LOCAL_VIP=true -e JSON_LOG=true -e LOG_LEVEL=debug -d pan93412/unblock-netease-music-enhanced  -o bilibili kugou kuwo 


```

# AdGuardHome

> GitHub：https://github.com/AdguardTeam/AdGuardHome
>
> DockerHub：https://hub.docker.com/r/adguard/adguardhome
>
> AdGuard Home是一款全网广告拦截与反跟踪软件。



```markdown
#
mkdir -p /opt/docker/adguardhome/{work,conf}

#
docker run -d --name adguardhome \
--restart unless-stopped \
-v /opt/docker/adguardhome/work:/opt/adguardhome/work \
-v /opt/docker/adguardhome/conf:/opt/adguardhome/conf \
-p 53:53/tcp -p 53:53/udp\
-p 67:67/udp -p 68:68/udp\
-p 80:80/tcp -p 443:443/tcp -p 443:443/udp -p 3000:3000/tcp\
-p 853:853/tcp\
-p 784:784/udp -p 853:853/udp -p 8853:8853/udp\
-p 5443:5443/tcp -p 5443:5443/udp\
adguard/adguardhome
```


```
上游 DNS 服务器：
tls://dns.pub
https://dns.pub/dns-query
tls://dns.alidns.com
https://dns.alidns.com/dns-query

tls://dns.google
https://dns.google/dns-query
tls://dns11.quad9.net
https://dns11.quad9.net/dns-query

Bootstrap DNS 服务器：
119.29.29.29
119.28.28.28
223.5.5.5
223.6.6.6

8.8.8.8
8.8.4.4
9.9.9.11
149.112.112.11

DNS 拦截列表：
1. Easylist 官方规则  
https://easylist.to/easylist/easylist.txt
2. EasyList China 中文补充规则  
https://easylist-downloads.adblockplus.org/easylistchina.txt
3. EasyList Lite 中文精简规则  
https://raw.githubusercontent.com/cjx82630/cjxlist/master/cjxlist.txt
4. EasyPrivacy 隐私保护  
https://easylist-downloads.adblockplus.org/easyprivacy.txt
5. ChinaList 国内大部分视频网站的广告过滤 （广告净化器）
https://raw.githubusercontent.com/hopol/ChinaList2.0/master/ChinaList2.0.txt

```

# PowerDNS

> 使用PowerDNS实现内网域名解析

## 1.PowerDNS

> GitHub：https://github.com/PowerDNS/pdns
>
> 官方文档：https://doc.powerdns.com/md/
>
> Docker文档：https://github.com/PowerDNS/pdns/blob/master/Docker-README.md
>
> 官方镜像
>
> - pdns-auth：https://hub.docker.com/r/powerdns/pdns-auth-master
>
> - pdns-recursor：https://hub.docker.com/r/powerdns/pdns-recursor-master
>
> - pdns-dnsdist：https://hub.docker.com/r/powerdns/dnsdist-master
>

PowerDNS全家桶中包含PowerDNS Authoritative、Recursor、DNSList三个组件。

- PowerDNS Authoritative：DNS权威服务器，用于提供企业私有域名的管理和解析；
- PowerDNS Recursor：DNS递归服务器，用于接受客户端DNS查询请求，并根据目标域转发配置转发到不同的上游DNS服务器进行解析，并对DNS解析记录进行缓存；
- PowerDNS-Admin：DNS权威服务器的Web管理页面；

```markdown
# 创建数据目录 
mkdir -p /opt/docker/pdns/config && touch /opt/docker/pdns/config/pdns.conf
  
# powerdns/pdns-auth-master:20220225 
docker run -d --name powerdns \
  -p 5300:5300 \
  -p 5300:5300/udp \
  -p 8888:8888 \
  -v /etc/localtime:/etc/localtime:ro \
  -v /opt/docker/pdns/config/pdns.conf:/etc/powerdns/pdns.conf \
  powerdns/pdns-auth-master:20220225 
```

> PowerDNS Authoritative Server 配置文件 `/etc/powerdns/pdns.conf`

### pdns.conf

> pdns.conf：https://doc.powerdns.com/authoritative/settings.html

```properties
api=yes
#自定义密钥，PowerDNS-Admin settings -> PDNS API KEY
api-key=qwerty 
loglevel=7

# 我这里使用mysql数据库
launch=gmysql
gmysql-host=192.168.3.50
gmysql-port=3306
gmysql-dbname=pdns
gmysql-user=root
gmysql-password=root

#pdns服务监听的地址，多个IP可以使用英文逗号隔开
local-address=0.0.0.0
local-port=5300

setgid=pdns
setuid=pdns
#master=yes
#master=no

webserver=yes
webserver-address=0.0.0.0
webserver-allow-from=0.0.0.0/0,::/0
# PowerDNS-Admin settings -> PDNS API URL
webserver-port=8888

default-ttl=3600
```

> https://doc.powerdns.com/md/authoritative/backend-generic-mysql/
>
> 创建基表，执行 Default Schema 语句



### powerdns httpapi

> 文档：https://doc.powerdns.com/md/httpapi/README/

```markdown
#查看 PowerDNS 版本
curl http://192.168.3.50:8888 | grep PowerDNS

#查看添加的域名（qwerty 是配置文件中的api-key）
curl -H 'X-API-Key: qwerty' http://192.168.3.50:8888/api/v1/servers/localhost/zones

```



## 2.PowerDNS Recursor

> Docker：https://hub.docker.com/r/powerdns/pdns-recursor-master

```markdown
# 创建数据目录 
mkdir -p /opt/docker/pdns-recursor/config && touch /opt/docker/pdns-recursor/zones && touch /opt/docker/pdns-recursor/config/recursor.conf

# powerdns/pdns-recursor-master
docker run -d --name powerdns-recursor \
  -p 5301:5301 \
  -p 5301:5301/udp \
  -p 8889:8889 \
  -v /etc/localtime:/etc/localtime:ro \
  -v /opt/docker/pdns-recursor/zones:/etc/pdns-recursor/zones \
  -v /opt/docker/pdns-recursor/config/recursor.conf:/etc/powerdns/recursor.conf \
  powerdns/pdns-recursor-master
  
```

> Powerdns Recursor的配置文件为`/etc/powerdns/recursor.conf`
>
> 上面配置映射不行就用这个试试`/etc/pdns-recursor/recursor.conf`

### recursor.conf

> recursor.conf：https://docs.powerdns.com/recursor/settings.html#

```properties
#对应权威服务器的allow-recursion 允许哪些ip进行递归 (允许所有用户端请求 0.0.0.0/0)
allow-from=0.0.0.0/0     
#哪些域名需要自己的权威服务器来解析，格式：域名=权威服务器ip:端口
#forward-zones=mydomain.com=192.168.3.50:5300    
#也可以使用配置文件来配置哪些域名强制走内网dns解析
forward-zones-file=/etc/pdns-recursor/zones
# 除forward-zones外其他所有的请求发至 223.5.5.5 DNS服务器
forward-zones-recurse=.=223.5.5.5        

#关闭dnssec
dnssec=off

#服务监听地址
local-address=0.0.0.0
local-port=5301

webserver=yes
webserver-address=0.0.0.0
webserver-allow-from=0.0.0.0/0,::/0
webserver-port=8889

#setgid=pdns-recursor
#setuid=pdns-recursor
```

### zones

> [forward-zones-file](https://docs.powerdns.com/recursor/settings.html#forward-zones-file)
>
> 后面的地址对应自建的权威服务器ip:端口

```
+b0x0.ling=192.168.3.50:5300
+ling.pub=192.168.3.50:5300
```

> 带 + 号开头为递归解析，不带 + 号开头为迭代解析



## 3.PowerDNS-Admin

> Github：https://github.com/PowerDNS-Admin/PowerDNS-Admin
>
> Docker：https://hub.docker.com/r/powerdnsadmin/pda-legacy

```markdown
#  powerdnsadmin/pda-legacy
docker run -d --name powerdns-admin \
-v /etc/localtime:/etc/localtime:ro \
-v /etc/timezone:/etc/timezone:ro \
-e SQLALCHEMY_DATABASE_URI=mysql://root:root@192.168.3.50/pdns \
-e GUNICORN_TIMEOUT=60 \
-e GUNICORN_WORKERS=2 \
-e GUNICORN_LOGLEVEL=DEBUG \
-p 9191:80 \
powerdnsadmin/pda-legacy

# 访问
http://192.168.3.50:9191
```

> SQLALCHEMY_DATABASE_URI
>
> The database URI that should be used for the connection. Examples:
>
> - sqlite:////tmp/test.db
> - mysql://username:password@server/db

### 初始化配置

​		初次进去PowerDNS-Admin，要创建个账号然后设置powerdns地址，PDNS API KEY 是powerdns配置文件中设置的api-key

![image-20230112193257480](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301121932525.png)

![image-20230112193615699](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301121936735.png)

​		配置后进入主页，点击侧边导航中的 PDNS 就可以看到 PDNS 的各项配置信息。如果填写有误，则没有任何信息。

![image-20230112193938954](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301121939045.png)

### 新增域名

​		点击左侧 New Domain 来新增私有域名， Submit 提交。

![image-20230112194241736](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301121942810.png)

![image-20230112202626766](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301122026851.png)

### 新增解析记录

​	点击域名记录右侧 Mange 按钮，点击 Add Record 添加 CNAME 和 A解析，然后点击 Apply Changes保存。



## 验证

### Windows安装 dig工具

​		下载dig安装包，下载地址：https://www.isc.org/download/ ，选择BIND，选择 Current-Stable 版本，选择 [ISC-maintained Packages](https://isc.org/blogs/bind-9-packages/)

![image-20230112204826121](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301122048179.png)

这个网站下载好像还要填写一些信息才能下载，这里我提供个网盘链接

> BIND9.11.3.x64.zip下载:https://wwwv.lanzouw.com/iHbJx0kurekh 密码:cwzs

将下载包放到自己喜欢的位置进行解压，打开目录，以管理员身份运行bindinstall.exe，修改安装目录；options选项卡中，只选tools only，其余全部去掉，最后点击安装。

![image-20230112212605876](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301122126918.png)

会提示安装visual，我这已经安装过了，点击close。![image-20230112212639608](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301122126648.png)

右键 此电脑》高级系统设置》环境变量》系统变量 ，在`Path`变量后面新增`C:\Program Files\ISC BIND 9\bin`

### 使用 dig

> **netstat -tunlp** 显示 tcp，udp 的端口和进程情况
>
> netstat -ntunlp |grep 53
>
> - -t (tcp) 仅显示tcp相关选项
> - -u (udp)仅显示udp相关选项
> - -n 拒绝显示别名，能显示数字的全部转化为数字
> - -l 仅列出在Listen(监听)的服务状态
> - -p 显示建立相关链接的程序名



打开CMD，输入如下命令：

*# 不指定dns服务器地址* 

```
dig www.baidu.com 
```

*# 指定dns服务器地址* 

```
dig @114.114.114.114 www.baidu.com
```

![image-20230112214605178](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301122146270.png)

dig 命令默认的输出信息比较丰富，大概可以分为 5 个部分：
1	显示 dig 命令的版本和输入的参数。
2	显示服务返回的一些技术详情，比较重要的是 status。如果 status 的值为 NOERROR 则说明本次查询成功结束。

status四种状态：
- `NXDOMAIN`否定回答，不存在此记录,
- `NOERROR` 没有错误，查询成功
- `REFUSED` DNS服务器拒绝回答，记录未生效
- `SERFAIL` DNS服务器停机或DNSsec响应验证失败

3	"QUESTION SECTION" 显示我们要查询的域名。
4	 "ANSWER SECTION" 是查询到的结果。
5	本次查询的一些统计信息，比如用了多长时间，查询了哪个 DNS 服务器，在什么时间进行的查询等等。

**默认情况下 dig 命令查询 A 记录**，上图中显示的 A 即说明查询的记录类型为 A 记录。

> 参考：[linux dig 命令 ](https://www.cnblogs.com/sparkdev/p/7777871.html)

**常见 DNS 记录的类型**

| 类型  | 目的                                                         |
| ----- | ------------------------------------------------------------ |
| A     | 地址记录，用来指定域名的 IPv4 地址，如果需要将域名指向一个 IP 地址，就需要添加 A 记录。 |
| AAAA  | 用来指定主机名(或域名)对应的 IPv6 地址记录。                 |
| CNAME | 如果需要将域名指向另一个域名，再由另一个域名提供 ip 地址，就需要添加 CNAME 记录。 |
| MX    | 如果需要设置邮箱，让邮箱能够收到邮件，需要添加 MX 记录。     |
| NS    | 域名服务器记录，如果需要把子域名交给其他 DNS 服务器解析，就需要添加 NS 记录。 |
| SOA   | SOA 这种记录是所有区域性文件中的强制性记录。它必须是一个文件中的第一个记录。 |
| TXT   | 可以写任何东西，长度限制为 255。绝大多数的 TXT记录是用来做 SPF 记录(反垃圾邮件)。 |

Find IP address of a website or domain (A record):

```
dig example.com A
```

Find IPv6 address of a domain (AAAA record):

```
dig example.com AAAA
```

We can specify the nameserver we'd like to query:

```
dig A example.com @8.8.8.8
```

Looks up MX records:

```
dig mx example.com
```

使用 dig 的 `-x` 选项来反向解析 IP 地址对应的域名

```
dig -x 8.8.8.8
```

### 验证私有域名

`8888`端口对应pdns-api，`8889` 端口对应pdns-recursor-api



验证权威 DNS 是否正常

```
dig @192.168.3.50 -p 5300 www.ling.pub
```



```cmd
dig @192.168.3.50 -p 5300 portainner.ling.pub
```



验证递归 DNS 是否正常

```cmd
#使用自建递归dns
dig @192.168.3.50 -p 5301 www.baidu.com

#使用阿里dns
dig @223.5.5.5 -p 5301 www.baidu.com
```



## 4.问题

### 4.1添加域名报错

powerdns-admin错误日志：

![image-20230112001842808](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301120018933.png)

查看powerdns日志，发现有条SQL执行报错：

![image-20230112002614011](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301120026054.png)

~~解决，删除原表，新建~~

> 表结构：https://doc.powerdns.com/md/authoritative/backend-generic-mysql/

​		发现官方文档给的表结构也没日志中报错的表字段，看来不能用 latest 版本镜像，更换镜像版本为 powerdns/pdns-auth-master:20220225 解决此问题




# OpenWrt

> OpenWrt作为旁路由



网卡配置路径：/etc/sysconfig/network-scripts/

```
进入网卡配置目录
cd /etc/sysconfig/network-scripts/
```

我机器上有四个网口，现在在用的是enp2s0网口

​	![](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202211201455238.png)

### 1.打开网卡混杂模式

```shell
ip link set enp2s0 promisc on
```
> 混杂模式就是接收所有经过网卡的数据包，包括不是发给本机的包。默认情况下网卡只把发给本机的包（包括广播包）传递给上层程序，其它的包一律丢弃。简单的讲,混杂模式就是指网卡能接受所有通过它的数据流，不管是什么格式，什么地址的。当网卡处于这种”混杂”方式时，该网卡具备”广播地址”，它对所有遇到的每一个数据帧都 产生一个硬件中断以便提醒操作系统处理流经该物理媒体上的每一个报文包。
>
> https://caoaman.cn/543.html

### 2.创建docker网络

服务器IP是**192.156.3.50**，主路由网关ip是**192.168.3.1**，**op_net**是自定义的名称

```shell
docker network create -d macvlan --subnet=192.168.3.0/24 --gateway=192.168.3.1 -o parent=enp2s0 op_net
```

使用 **docker network ls** 命令查看docker网络，可以看到**op_net**

![](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202211201506251.png)

### 3.安装sulinggg/openwrt

这里安装的是 [sulinggg/openwrt](https://hub.docker.com/r/sulinggg/openwrt)

查看内核版本`cat /proc/version`，我的的是**x86_64** ，不同的平台拉取不同的镜像

```shell
[root@localhost ~]# cat /proc/version
Linux version 3.10.0-1160.el7.x86_64 (mockbuild@kbuilder.bsys.centos.org) (gcc version 4.8.5 20150623 (Red Hat 4.8.5-44) (GCC) ) #1 SMP Mon Ox86_64 ct 19 16:18:59 UTC 2020
```

```Markdown
# 1.拉取命令 二选一执行
Docker官方仓库
docker pull sulinggg/openwrt:x86_64
阿里云镜像仓库 (上海)
docker pull registry.cn-shanghai.aliyuncs.com/suling/openwrt:x86_64

# 2.创建并启动容器
docker run --restart always  --name openwrt -d --network op_net --privileged sulinggg/openwrt:x86_64 /sbin/init 

命令说明：
--restart always			自启；
--name openwrt 				定义了容器的名称；
-d 							后台运行容器；
--network macnet			将容器加入 maxnet网络；
--privileged 				容器运行在特权模式下；
sulinggg/openwrt:x86_64 	拉取的Docker镜像名，我这里使用官方镜像仓库拉取的
/sbin/init 					容器启动后执行的命令。


# 3.修改配置
进入容器
docker exec -it openwrt bash

## 3.1编辑配置 固定IP访问openwrt
vi /etc/config/network
原始配置：
config interface 'loopback'
        option ifname 'lo'
        option proto 'static'
        option ipaddr '127.0.0.1'
        option netmask '255.0.0.0'

config globals 'globals'
        option packet_steering '1'

config interface 'lan'
        option type 'bridge'
        option ifname 'eth0'
        option proto 'static'
        option netmask '255.255.255.0'
        option ip6assign '60'
        option ipaddr '192.168.123.100'
        option gateway '192.168.123.1'
        option dns '192.168.123.1'

config interface 'vpn0'
        option ifname 'tun0'
        option proto 'none'        
以下需要更改lan处的配置，192.168.3.4就是openwrt的访问地址，192.168.3.1是我主路由的ip
        option ipaddr '192.168.3.4'
        option gateway '192.168.3.1'
        option dns '192.168.3.1'
修改后保存退出

## 3.2在容器里执行命令
/etc/init.d/network restart

# 4.访问地址
https://192.168.3.4/
用户名：root
密码：password
```

### 4.openwrt配置

#### 4.1LAN接口调整

网络》接口，修改名为LAN的接口。

- `基本配置`，关闭DHCP（忽略此接口打上勾）
- `IPV6设置`中，`路由通告服务`、`DHCPv6 服务`和`NDP 代理`全部选择`已禁用`

#### 4.2防火墙调整

网络》防火墙。取消勾选`启用 SYN-flood 防御`；`入站数据`、`出站数据`和`转发`选择接受；

#### 4.3客户端配置

在完成上述配置后，就可以将你的客户端网关指向新搭建的openwrt了。此时，应该可以正常访问外部网站，也能访问内网任意地址。

# Frp （内网穿透工具）

> 官方配置文档：https://gofrp.org/docs/reference/server-configures/
>
> 内网穿透工具

## 1.  服务端( snowdreamtech/frps)

```markdown
# 首先在Docker目录下创建fpr客户端的映射目录
mkdir -p /opt/docker/frp

# 在映射目录下面创建映射文件frpc.ini
进入映射目录
cd /opt/docker/frp
编辑映射文件
vi frps.ini

# 填入如下内容
[common]
bind_addr=0.0.0.0
bind_port=7000
token=your_frps_password		# 客户端和服务端都要配而且要一样

dashboard_port=7500 		#控制台登录端口，自定义
dashboard_user=admin		#控制台登录名
dashboard_pwd=xxxx		#控制台登录密码

# 运行命令
docker run  --network host -d --restart=always -v /opt/docker/frp/frps.ini:/etc/frp/frps.ini --name frps snowdreamtech/frps
```


## 2. 客户端(snowdreamtech/frpc)
```markdown
# 首先在Docker目录下创建fpr客户端的映射目录
mkdir -p /opt/docker/frp

# 在映射目录下面创建映射文件frpc.ini
进入映射目录
cd /opt/docker/frp
编辑映射文件
vi frpc.ini

# 填入如下内容
[common]
#打开浏览器通过 http://127.0.0.1:7400 访问 Admin UI
admin_addr = 127.0.0.1
admin_port = 7400
admin_user = admin
admin_pwd = admin
# 服务器frps信息
server_addr=xxx.xx.x.xx 	 	   #你的frps服务端的地址(公网IP或者域名)
server_port=7000	   				#你的frps服务端端口号(可以自己单独指定)
token=your_frps_password    		       #frps服务端token认证 (这个可以加也可以不加，需要根据服务端配置文件来对应)

[blog]
type=tcp
local_ip=127.0.0.1 	#本地需要映射可以通过外网访问的应用IP
local_port=8081  		#本地应用端口
remote_port=6001 		#映射公网服务器端口

[kibana]
type=tcp
local_ip=127.0.0.1	#本地需要映射可以通过外网访问的应用IP
local_port=5601  		#本地应用端口
remote_port=5601 		#映射公网服务器端口

# 启动命令
docker run --network bridge -d --restart=always -v /opt/docker/frp/frpc.ini:/etc/frp/frpc.ini --name=frpc snowdreamtech/frpc
```


# Dashy （仪表板应用程序）

> https://github.com/lissy93/dashy
>
> 文档：https://dashy.to/docs/quick-start
>
> 一个开源、高度可定制、易于使用、尊重隐私的仪表板应用程序

```markdown
# 初次启动生成默认配置文件
docker run -d -p 4000:80 --name dashy lissy93/dashy

#复制配置文件
docker cp dashy:/app/public/conf.yml /opt/docker/dashy

# 删除默认容器
docker stop dashy
docker rm dashy
      
# 重新启动
docker run -d \
  -p 4000:80 \
  -v /opt/docker/dashy/conf.yml:/app/public/conf.yml \
  --name dashy \
  --restart=always \
  lissy93/dashy

# Icon图标文档
https://github.com/Lissy93/dashy/blob/master/docs/icons.md#favicons
#图标1
https://simpleicons.org/
使用 si 开头
si-portainer，si-freenas，si-nextcloudsi-homeassistant...
#图标2
https://github.com/walkxcode/dashboard-icons/tree/main/png
使用 hl 开头
hl-home-assistant，hl-cockpit，hl-docker，hl-portainer....
```
# Keycloak （开源身份验证系统）

>https://github.com/keycloak/keycloak
>
>Keycloak 是一个基于 Java 的开源、高性能、安全的身份验证系统，由 RedHat 提供支持。

```markdown
# 运行
docker run -d \
  -p 4001:8080 \
  --name Keycloak-auth-server \
  -e KEYCLOAK_USER=admin \
  -e KEYCLOAK_PASSWORD=admin \
  quay.io/keycloak/keycloak:15.0.2
  
# Web访问
http://192.168.0.50:4001
用户名/密码：admin/admin

```

## 1. 对接Dashy

```
登陆控制台：http://192.168.0.50:4001，用户名/密码：admin/admin

Keycloak 使用领域（类似于租户）来创建隔离的用户组。必须先创建一个领域，然后才能添加第一个用户
```

![image-20221218141855647](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212181419782.png)



![image-20221218142351646](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212181423694.png)

```markdown
我这里访问提示 HTTPS required

stackoverflow地址：
https://stackoverflow.com/questions/30622599/https-required-while-logging-in-to-keycloak-as-admin

# 进入容器
docker exec -it Keycloak-auth-server bash
# 进入bin目录
cd /opt/jboss/keycloak/bin

# 陆续执行命令
./kcadm.sh config credentials --server http://localhost:8080/auth --realm master --user admin
输入以上命令后会提示输入密码： admin

./kcadm.sh update realms/master -s sslRequired=NONE

# 再次访问页面，点击 Administration Console ，进去就可以登录了 用 admin/admin 登录
```

```markdown
1. 创建 realm ，name我这里定义为 dashy ，点击 Create
```

![image-20221218143846482](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212181438515.png)

![image-20221218143941650](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212181439695.png)

```markdown
2. 创建后 会自动切入到 dashy 这个领域来，点击左侧下方 Users ，然后点击右上方 Add user
```

![image-20221218144337652](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212181443707.png)

```markdown
3. 给账号 dashy 设置密码
```

![image-20221218144432829](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212181444886.png)

```markdown
4. 创建 client ，点击左侧 Clients 标签，再点击右侧 Create
这里我定义如下 
Client ID：			id_dashy
Client Protocol：	openid-connect
Root URL : 			http://43.139.204.230:4000

Root URL中的url就是我的 dashy 面板的访问地址

# 保存后会跳转到我们穿法刚建的client设置页面，我们只需要关注以下两个url即可，不用改动

Valid Redirect URIs 重定向url，即认证过后跳转的url
Web Origins 跳转地址
```

![image-20221218145510401](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212181455441.png)



### 1.1 在 Dashy 配置文件中启用 Kecloak

```yaml
vim /data/dashy/my-conf.yml

vim /data/dashy/my-conf.yml
appConfig:
	...
    auth:
        enableKeycloak: true
        keycloak:
       		# keycloak 地址
            serverUrl: 'http://192.168.0.50:4001'
            realm: 'Dashy'
            clientId: 'id_dashy'
            # Keycloak V 17 或更早版本需要配置legacySupport: true
            #legacySupport: true

# 重启dashy
docker restart dashy

```



# File Browser （文件浏览器）

> https://github.com/filebrowser/filebrowser
>
> filebrowser 是一个使用go语言编写的软件，功能是可以通过浏览器对服务器上的文件进行管理。可以是修改文件，或者是添加删除文件，甚至可以分享文件，是一个很棒的文件管理器，你甚至可以当成一个网盘来使用。总之使用非常简单方便，功能很强大。

```markdown

# 运行命令
docker run -d  \
-p 8002:80 \
-v /opt:/srv \
-v /opt/docker/filebrowser/filebrowserconfig.json:/etc/config.json \
-v /opt/docker/filebrowser/database.db:/etc/database.db \
--name filebrowser  \
--restart=always  \
filebrowser/filebrowser

# 相关说明
-v /opt:/srv：这行命令意思是filebrowser将以这个 /opt 目录作为根目录，是挂载的宿主机目录
默认账号密码 admin/admin
```

![](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212142018068.png)

![](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212142017568.png)


# prometheus
> https://github.com/prometheus/prometheus
> 用于事件监视和警报的免费软件应用程序。它将实时指标记录在使用HTTP拉模型构建的时间序列数据库中，具有灵活的查询和实时警报

![img](https://cdn.rawgit.com/prometheus/prometheus/e761f0d/documentation/images/architecture.svg)

## 1. prometheus（收集数据）

```markdown
#初始化配置文件
docker run --name prometheus -d -p 9090:9090 prom/prometheus
# 从容器中复制默认配置文件出来
mkdir -p /opt/docker/prometheus
docker cp prometheus:/etc/prometheus/prometheus.yml /opt/docker/prometheus/prometheus.yml
# 删除
docker stop prometheus
docker rm prometheus

# 重新运行
docker run -d -p 9090:9090 -v /opt/docker/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml --name prometheus prom/prometheus

# 访问地址
http://192.168.3.50:9090
```

## 2. node-exporter（监控程序）
```markdown
# 安装 node-exporter 监控服务器
docker run -d -p 9080:9100 \
  -v /proc:/host/proc:ro \
  -v /sys:/host/sys:ro \
  -v /:/rootfs:ro \
  --name node-exporter prom/node-exporter
  
# 访问
http://192.168.3.50:9080/metrics
  
# 修改prometheus.yml，新增job ,ip及地址对应 node-exporter
  - job_name: "linux"
    static_configs:
      - targets: ['192.168.3.50:9080']
```

![](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212172115235.png)
```markdown
# 重启  prometheus
docker restart prometheus

# 访问
http://192.168.3.50:9090/targets
```
![](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212172125122.png)



## 3. grafana（图形面板）

```markdown
# 创建数据目录
mkdir -p /opt/docker/grafana
# 修改目录权限，不然可能服务起不来
chmod 777 -R /opt/docker/grafana

# 运行
docker run -d \
  -p 3000:3000 \
  -v /opt/docker/grafana:/var/lib/grafana \
  --name=grafana \
  grafana/grafana
  
# 访问 默认账号密码都是 admin,登录后强制更改密码 我这里改为 admin@123
http://192.168.3.50:3000/

# 点击中间 data sources ，我这用的最新版本
```

![](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212172136008.png)

```markdown
# 选择 prometheus，在url处 填写 prometheus IP地址，点击下面的保存，绿色提示说明正常
```

![image-20221217213746542](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212172137605.png)

![image-20221217214217881](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212172142921.png)

![image-20221217214243465](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212172142493.png)

```markdown
#点击左侧添加 Dashboard ，点击 Add a new panel
```

![image-20221217214419056](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212172144107.png)

```markdown
# 在这里输入条件 查询想要监控的数据 
```

![image-20221217215928810](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212172159901.png)

# cadvisor （容器监控工具）

> https://github.com/google/cadvisor
> 一款由 Google 开源的容器监控工具。它可以实时统计容器运行时占用的资源，包括 CPU 利用率、内存使用量、网络传输等信息。提供了 Web 可视化页面，能方便用户分析和监控容器运行状态，支持包括 Docker 在内的几乎所有类型的容器。

```
 docker run \
  --volume=/:/rootfs:ro \
  --volume=/var/run:/var/run:ro \
  --volume=/sys:/sys:ro \
  --volume=/var/lib/docker/:/var/lib/docker:ro \
  --volume=/dev/disk/:/dev/disk:ro \
  --publish=8080:8080 \
  --detach=true \
  --name=cadvisor \
  --privileged \
  --device=/dev/kmsg \
  google/cadvisor
```
##1. 查看主机监控
```
访问 http://192.168.3.50:8080/containers/ 地址，在首页可以看到主机的资源使用情况，包含 CPU、内存、文件系统、网络等资源
```
##2. 查看容器监控
```
访问 http://192.168.3.50:8080/docker/  这个页面会列出 Docker 的基本信息和运行的容器情况
```

# ntopng (流量监控)

> https://github.com/ntop/ntopng
>
> ntopng 是一款网络流量监控工具，提供了直观的 Web 用户界面，用于浏览实时和历史网络流量信息。



```markdown
# 
mkdir -p /opt/docker/ntopng
chmod 777 /opt/docker/ntopng

# 其中的 enp2s0 换成你的网络接口，ifconfig 或者 ip addr 查看网络接口名称
docker run -d \
    --restart=always \
    -p 3000:3000 \
    -v /opt/docker/ntopng/ntopng.license:/etc/ntopng.license:ro \
    --net=host \
    --name ntopng \
    ntop/ntopng -i enp2s0

# 访问
http://192.168.3.50:3000

```

# vnstat（网络流量监控）

> vnstat：https://github.com/vergoh/vnstat
>
> vnstat-docker：https://github.com/vergoh/vnstat-docker
>
> docker：https://hub.docker.com/r/vergoh/vnstat
>
> vnStat - a network traffic monitor for Linux and BSD。

```markdown
mkdir -p /opt/docker/vnstat

docker run -d \
    --restart=unless-stopped \
    -p 8685:8685 \
    -v /opt/docker/vnstat:/var/lib/vnstat \
    -v /etc/localtime:/etc/localtime:ro \
    -v /etc/timezone:/etc/timezone:ro \
    --name vnstat \
    vergoh/vnstat
    
# 访问
http://server_ip:8685
```



# Uptime Kuma (轻量级网络监控)

> [Uptime Kuma](https://uptime.kuma.pet/?utm_source=nicelinks.site) 
> 一个开源自托管监控工具
> 可监控 HTTP(s) / TCP / Ping / [DNS](https://www.isolves.com/e/tags/?tagname=DNS) 等网络
> 支持 Telegram、Discord、Gotify、Slack、Pushover、电子邮件 (SMTP)多种通知方式

```markdown
# 创建数据目录
mkdir -p /opt/docker/uptime-kuma

# 安装命令
docker run -d --restart=always -p 3001:3001 -v /opt/docker/uptime-kuma:/app/data --name uptime-kuma louislam/uptime-kuma

# 访问
http://{servcer_ip}:3001 
```

# Netdata（服务器监控）

> https://hub.docker.com/r/netdata/netdata
>
> https://learn.netdata.cloud/docs/agent/packaging/docker
>
> 文档：https://learn.netdata.cloud/docs/
>
> netdata是一个用于分布式实时性能和运行状况监视的系统。 它使用现代的交互式Web仪表板实时提供无与伦比的洞察力，以实时了解其运行的系统上发生的所有事情（包括Web和数据库服务器之类的应用程序）。
>
> netdata快速高效，旨在永久在所有系统（物理和虚拟服务器，容器，IoT设备）上运行，而不会中断其核心功能。

```markdown
# 暂时发现自定义目录挂在有问题，此步骤可忽略
mkdir -p /opt/docker/netdata/{netdatalib,netdataconfig,netdatacache}

# 启动
docker run -d --name=netdata \
  -p 19999:19999 \
  -v netdataconfig:/etc/netdata \
  -v netdatalib:/var/lib/netdata \
  -v netdatacache:/var/cache/netdata \
  -v /etc/passwd:/host/etc/passwd:ro \
  -v /etc/group:/host/etc/group:ro \
  -v /proc:/host/proc:ro \
  -v /sys:/host/sys:ro \
  -v /etc/os-release:/host/etc/os-release:ro \
  -v /etc/timezone:/etc/timezone:ro \
  -v /etc/localtime:/etc/localtime:ro \
  --restart unless-stopped \
  --cap-add SYS_PTRACE \
  --security-opt apparmor=unconfined \
  netdata/netdata:stable
  
  # 访问
  http://server_ip:19999
  
  # 查看netdata默认配置文件
  http://server_ip:19999/netdata.conf
  
  # 更改配置文件,前面将挂载的配置文件宿主机目录在此
	vi /var/lib/docker/volumes/netdataconfig/_data/netdata.conf
```

## Netdata集群管理方案

> 原文档地址：https://juejin.cn/post/6875163119836987406#heading-6

上面展示的只是单一服务器的监控数据，而且netdata有一个缺点就是所有被监控的服务器都需要安装agent，所以，这里就是出现一个问题，就是如何将监控数据统一管理与展示？

netdata官方并没设计主从模式，像zabbix那样，可以一台做为主服务器，其它的做为从服务器，将数据收集到主服务器统一处理与展示，但是，官方也给出了相关的解决方案。

1、netdata.cloud 使用自带的 netdata.cloud，也就是每一个安装节点WEB界面右上角的signin。只要我们使用同一个账号登录netdata.cloud（需要kexue上网），之后各个节点之间就可以轻松通过一个账号控制。每个节点开启19999端口与允许管理员查看数据，然后控制中心通过前端从各节点的端口收集的数据，传给netdata.cloud记录并展示。

这是一种被动的集群监控，本质上还是独立的机器，且不方便做自定义的集群dashboard。

2、stream 插件 所以，为了解决上面这种方案的弊端，netdata又提供了另一种方法，将各节点的数据集中汇总到一台（主）服务器，数据处理也在这台服务器上，其它节点无需开放19999端口。 算是一种主动传输模式，把收集到的数据发送到主服务器上，这样在主服务器上可以进行自定义的dashboard开发。

缺点：主服务器流量、负载都会比较大（在集群服务器数量较多的情况下），如果主服务器负载过高，我们可以通过设置节点服务器的数据收集周期（update every）来解决这个问题。

## Netdata集群监控配置

很多文章都只是介绍了其安装与一些界面的展示结果，并没有提供集群监控这一解决方案与其具体的配置，民工哥也是查了很多的资料，现在将其配置过程分享给大家。

对于streaming的配置不熟悉的可以参考官方的文档说明：https://learn.netdata.cloud/docs/metrics-storage-management/enable-streaming

1、节点服务器配置

```ini
[root@CentOS7-1 ~]# cd /etc/netdata/
[root@CentOS7-1 netdata]# vim netdata.conf
#修改配置如下
[global]
    memory mode = none
    hostname = [建议修改成你的主机名]
[web]
    mode = none
```

然后，在/etc/netdata/目录下新建一个文件stream.conf，然后将其配置为如下：

```ini
[stream]
    enabled = yes
    destination = MASTER_SERVER_IP:PORT
    api key = xxxx-xxxx-xxxx-xxxx-xxxx

#参数说明如下
 destination = MASTER_SERVER_IP:PORT  主服务器地址与端口
 api key 必需为uuid的字符串，Linux系统中可以使用下面的命令自动生成。
 [root@CentOS7-1 netdata]# uuidgen
 480fdc8c-d1ac-4d6f-aa26-128eba744089
```

配置完成之后，需要重启节点的netdata服务即可完成整个配置。

```ini
[root@CentOS7-1 ~]# systemctl restart netdata
```

2、主服务器配置

在netdata.conf的同一目录下新建stream.conf并写入如下配置：

```ini
[API_KEY]/[480fdc8c-d1ac-4d6f-aa26-128eba744089]
    enabled = yes
    default history = 3600
    default memory mode = save
    health enabled by default = auto
    allow from = *
[API_KEY]
    enabled = yes
    default history = 3600
    default memory mode = save
    health enabled by default = auto
    allow from = *
#其中，API_KEY对应节点服务器的api key(字符串)，allow from可以设置数据流的允许来源以保证安全。
#如果有多个节点服务器，则一起写在stream.conf里面
```

完成配置后重启netdata：

```
systemctl restart netdata
```

所有的配置完成后，就可以在主服务器的WEB界面右上角看到下拉菜单（主机名），点击即可看到相关的监控信息了。



# Ward（单服务器监控工具）

> 官方项目地址：https://github.com/B-Software/Ward
>
> 第三方fork：https://github.com/AntonyLeons/Ward
>
> Ward 是一个使用 Java 开发的简单而简约的服务器监控工具。Ward 支持自适应设计系统，它还支持深色主题，它只显示服务器的主要信息。Ward 在所有流行的操作系统上运行良好，因为它使用 [OSHI](https://github.com/oshi/oshi)。



```Markdown
# 创建数据目录
mkdir -p /opt/docker/Ward

# 安装，官方镜像是ward，但是疑似遗弃了，这里使用antonyleons/ward
docker run -d --name ward  \
-p 4000:4000 \
-e WARD_PORT=4000 \
--privileged=true \
--restart always \
antonyleons/ward

```

# ServerStatus（多服务器监控）

> 项目地址：https://github.com/cppla/ServerStatus
>
> ServerStatus中文版是一个酷炫高逼格的云探针、云监控、服务器云监控、多服务器探针~。



# Glances（系统监控工具）

> 官网：https://nicolargo.github.io/glances/
>
> 项目地址：https://github.com/nicolargo/glances
>
> DockerHub：https://hub.docker.com/r/nicolargo/glances
>
> Glances 是一个跨平台的、基于命令行的系统监控工具，由 Python 语言编写，使用 Python 的 psutil 库来抓取系统数据。可以监控 CPU、负载均衡、内存、网络设备、磁盘 I/O、进程和文件系统使用等。

```markdown
# 创建数据目录
mkdir -p /opt/docker/glances

# 控制台查看（临时） ctrl+c 退出
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock:ro --pid host --network host -it nicolargo/glances:latest-full

#启动
docker run --name glances -d --restart=always \
-p 61208-61209:61208-61209 \
-e GLANCES_OPT="-w" \
-v /var/run/docker.sock:/var/run/docker.sock:ro \
--pid host \
--network host \
nicolargo/glances:3.3.0.4-full

```



# Flare (网址导航)

> https://github.com/soulteary/docker-flare
>
> https://github.com/soulteary/docker-flare/blob/main/docs/advanced-startup.md
>
> https://hub.docker.com/r/soulteary/flare
>
> 一个简洁的网址导航

```markdown
# 创建数据目录
mkdir -p  /opt/docker/flare

# 安装命令
docker run --rm -it --name flare -p 5005:5005 -v /opt/docker/flare:/app soulteary/flare:0.4.0

# 访问
http://{servcer_ip}:5005 

# 设置用户账号,先删除旧容器
docker rm flare
# 启动
docker run -d --name flare \
-p 5005:5005 \
-e FLARE_DISABLE_LOGIN=0 \
-e FLARE_USER=flare \
-e FLARE_PASS=flare123 \
-v /opt/docker/flare:/app soulteary/flare:0.4.0


# 更改链接的图标，图标访问此地址
http://{servcer_ip}:5005/icons/

```

# Sonic (博客平台)

>[Sonic](https://github.com/go-sonic/sonic/blob/master/doc/README_ZH.md)
>[Sonic Docker Hub](https://hub.docker.com/r/gosonic/sonic)
>一个用Golang开发的博客平台

```markdown
# 创建数据目录
mkdir -p  /opt/docker/sonic

# 安装命令
docker run --name sonic-server -d -e LOGGING_LEVEL_APP=info -p 8080:8080 -v /opt/docker/sonic:/sonic gosonic/sonic

# 使用 mysql 作为数据源
docker run --name sonic-server \
-e SQLITE3_ENABLE=false \
-e LOGGING_LEVEL_APP=info  \
-e MYSQL_HOST=172.17.0.5 \
-e MYSQL_PORT=3306 \
-e MYSQL_DB=sonicdb \
-e MYSQL_USERNAME=root \
-e MYSQL_PASSWORD=root \
-p 8088:8080 \
-v /opt/docker/sonic:/sonic -d gosonic/sonic

```

# Halo（博客平台）

> **Halo** [ˈheɪloʊ]，好用又强大的开源建站工具。
>
> 官方docker安装文档：https://docs.halo.run/getting-started/install/docker
>
> 主题地址：https://halo.run/themes
>
> 与 Halo 相关的周边生态资源列表：https://github.com/halo-sigs/awesome-halo
>
> 
>
> Halo 论坛：https://bbs.halo.run/
>
> demo地址：https://demo.halo.run/console/#/dashboard
>
> 账号：demo 	密码：P@ssw0rd123..



目前 Halo 2 并未更新 Docker 的 latest 标签镜像，主要因为 Halo 2 不兼容 1.x 版本，防止使用者误操作。我们推荐使用固定版本的标签，比如 `halohub/halo:2.1.0`。

```markdown
# 创建数据目录
mkdir -p  /opt/docker/halo2

# 我这里使用mysql作为数据源
docker run -d \
  --name halo2-server \
  -p 8090:8090 \
  -v /opt/docker/halo2:/root/.halo2 \
  halohub/halo:2.1.0 \
  --spring.r2dbc.url=r2dbc:pool:mysql://192.168.3.50:3306/halo2 \
  --spring.r2dbc.username=root \
  --spring.r2dbc.password=root@123 \
  --spring.sql.init.platform=mysql \
  --halo.external-url=http://192.168.3.50:8090/ \
  --halo.security.initializer.superadminuser=admin \
  --halo.security.initializer.superadminpassword=P@88w0rd
  
# 机器宿主ip是192.168.3.50，映射宿主机端口8090，访问地址为
博客地址：http://192.168.3.50:8090/
控制台：http://192.168.3.50:8090/console/

```

## 安装插件

> https://github.com/halo-sigs/awesome-halo

### 流量统计分析工具

- [plugin-umami](https://github.com/halo-sigs/plugin-umami) - Halo 2.0 对 Umami 的集成

### 编辑器

- [plugin-stackedit](https://github.com/halo-sigs/plugin-stackedit) - 为 Halo 2.0 集成 StackEdit 编辑器
- [plugin-bytemd](https://github.com/halo-sigs/plugin-bytemd) - 为 Halo 2.0 集成 ByteMD 编辑器

### 图片选择

需要注册 https://unsplash.com/developers 获取Access Key，在unsplash设置中配置token

- [plugin-unsplash](https://github.com/halo-sigs/plugin-unsplash) - Halo 2.0 的 Unsplash 插件，支持从 Unsplash 选择图片

### 代码高亮

- [plugin-highlightjs](https://github.com/halo-sigs/plugin-highlightjs) - 提供对 [highlight.js](https://github.com/highlightjs/highlight.js) 的集成，支持在内容页高亮显示代码块

### 评论组件

- [plugin-comment-widget](https://github.com/halo-sigs/plugin-comment-widget) - Halo 2.0 的前台评论组件插件

# go-md-book（markdown博客）

> Github地址：https://github.com/hedongshu/go-md-book
>
> DockerHub：https://hub.docker.com/r/willgao/markdown-blog
>
> 基于 go 快速将 markdown 文件发布成可以web访问的book。



```markdown
# 创建数据目录
mkdir -p /opt/docker/md-blog/{md,cache,config}

# ctrl+c 结束
docker run -it --name md-blog \
-p 5060:5006 \
-v /opt/docker/md-blog/md:/md \
-v /opt/docker/md-blog/cache:/cache \
willgao/markdown-blog:latest \
-t "WuLiLing'Blog"

#启动
docker run -d --name md-blog \
-p 5060:5006 \
-v /opt/docker/md-blog/md:/md \
-v /opt/docker/md-blog/cache:/cache \
willgao/markdown-blog:latest \
-t "WuLiLing'Blog"
```



# Certs Maker (SSL 证书生成工具)

> https://github.com/soulteary/certs-maker/blob/main/README_CN.md
>
> https://hub.docker.com/r/soulteary/certs-maker
>
> 一个小巧的 SSL 证书生成工具（Docker 工具镜像）。



```
docker run --rm -it -e CERT_DNS=lab.com,*.lab.com,*.data.lab.com -v /opt/docker/ssl:/ssl soulteary/certs-maker 
```

# JetBrains License Server

```
mkdir -p /opt/docker/JetBrainsLicenseServer

docker run -d -p 8087:8000 --name jetbrains_license_server \
  -e TZ="Asia/Shanghai" \
  -e JLS_VIRTUAL_HOSTS=192.168.3.50 \
  -v /opt/docker/JetBrainsLicenseServer:/data \
  crazymax/jetbrains-license-server
  
```

# Ant-Media-Server（直播推流）

> https://github.com/ant-media/Ant-Media-Server
>
> https://github.com/ant-media/Ant-Media-Server/wiki
>
> https://hub.docker.com/r/nibrev/ant-media-server
>
> 视频直播推流应用

```markdown
# 启动
docker run -d --name ant-media-server -p 5080:5080 nibrev/ant-media-server

# 访问
http://{server_ip}:5080 
```

```markdown
# 1. 上传文件
```

![image-20221231003036916](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212310030995.png)

```markdown
# 2. 点击New Live Stream，选择Playlist，Stream Id不用手动填，点击点击Add Playlist Item，点击一次添加一个视频地址Playlist URL，这个url在Vod标签点击视频播放按钮，然后右键视频窗口，复制视频地址获取，然后点击Create
```

![image-20221231003133767](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212310031834.png)

![image-20221231003536063](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212310035147.png)

```markdown
# 3. 设置推流地址，我这里以哔哩哔哩为例，先点击Edit RTMP Endpoints填入推流地址，点击Add RTMP Endpoint添加，Close，然后点击Start Broadcast开始推流
```

![image-20221231003904970](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212310039007.png)



# pure-live（第三方观看直播程序）

> Github：https://github.com/iyear/pure-live-core
>
> 一个想让直播回归纯粹的项目，没有礼物、粉丝团、弹窗，只有直播、弹幕
>
> 一个第三方观看直播程序

```markdown
mkdir -p /opt/docker/pure-live/{data,log}

#安装
docker run -d --name pure-live -p 8800:8800 -v /opt/docker/pure-live/data:/data -v /opt/docker/pure-live/log:/log  --restart=always iyear/pure-live:latest
```



# KPlayer

> https://github.com/bytelang/kplayer-go
>
> KPlayer可以帮助你快速的在服务器上进行视频资源的循环直播推流，只需要简单对配置文件进行自定义即可开启直播推流



# LAL 

> https://github.com/q191201771/lal
>
> https://pengrl.com/lal/#/
>
> Golang 开发的流媒体（直播音视频网络传输）服务器。

```
docker run -it -p 1935:1935 -p 8080:8080 -p 4433:4433 -p 5544:5544 -p 8083:8083 -p 8084:8084 -p 30000-30100:30000-30100/udp q191201771/lal /lal/bin/lalserver -c /lal/conf/lalserver.conf.json
```

