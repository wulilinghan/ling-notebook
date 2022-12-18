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

项目地址：https://github.com/UnblockNeteaseMusic/server

网易云历史版本：https://blog.amarea.cn/archives/netease-cloudmusic-history-version.html

```markdown
# 1.拉取命令
docker pull pan93412/unblock-netease-music-enhanced

# 2.启动命令
docker run --name unblock-netease-music --restart always -p 18080:8080 -e ENABLE_FLAC=true -e ENABLE_LOCAL_VIP=true -e JSON_LOG=true -e LOG_LEVEL=debug -d pan93412/unblock-netease-music-enhanced  -o bilibili kugou kuwo 


```

# AdGuardHome

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

# Frp

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
# cockpit(TODO)

> https://github.com/cockpit-project/cockpit
>
> Cockpit is a web-based graphical interface for servers.



# Dashy

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
```
# Keycloak

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



# File Browser

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

## 1. node-exporter
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



## 2. grafana

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

# cadvisor

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

# ntopng

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

