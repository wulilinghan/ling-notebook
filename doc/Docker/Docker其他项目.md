# qinglong

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

> 内网穿透
> 官方配置文档：https://gofrp.org/docs/reference/server-configures/

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

# Dashy

> 一个开源、高度可定制、易于使用、尊重隐私的仪表板应用程序
>
> https://github.com/lissy93/dashy

```markdown
# 初次启动生成默认配置文件
docker run -d -p 4000:80 --name dashy lissy93/dashy

#复制配置文件
docker cp dashy:/app/public/conf.yml /opt/docker/dashy

# 删除默认容器
docker stop dashy
docker rm dashy

# 设置访问密码，修改配置文件，新增一下内容（待验证）
appConfig:
  auth:
    users:
    - user: alicia
      hash: 4D1E58C90B3B94BCAD9848ECCACD6D2A8C9FBC5CA913304BBA5CDEAB36FEEFA3
      type: admin
      
# 重新启动
docker run -d \
  -p 4000:80 \
  -v /opt/docker/dashy/conf.yml:/app/public/conf.yml \
  --name dashy \
  --restart=always \
  lissy93/dashy
```



