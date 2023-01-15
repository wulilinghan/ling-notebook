>  使用自建DNS服务器实现内网域名解析

# LinuxDNS服务器设置

cat /etc/resolv.conf

# 查看53端口占用

## 1.**netstat**

**netstat -tunlp** 显示 tcp，udp 的端口和进程情况

```
netstat -ntunlp |grep 53
```

- -t (tcp) 仅显示tcp相关选项
- -u (udp)仅显示udp相关选项
- -n 拒绝显示别名，能显示数字的全部转化为数字
- -l 仅列出在Listen(监听)的服务状态
- -p 显示建立相关链接的程序名

## 2.**lsof**

```
lsof -i :53
```
# Dig工具

> Dig 工具全称为域名信息搜索器（Domain Information Groper），能够显示详细的DNS查询过程，是一个非常强大的DNS故障诊断工具。

## Windows安装 Dig工具

		下载dig安装包，下载地址：https://www.isc.org/download/ ，选择BIND，选择 Current-Stable 版本，选择 [ISC-maintained Packages](https://isc.org/blogs/bind-9-packages/)

![image-20230112204826121](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301122048179.png)

这个网站下载好像还要填写一些信息才能下载，这里我提供个网盘链接

> BIND9.11.3.x64.zip下载:https://wwwv.lanzouw.com/iHbJx0kurekh 密码:cwzs

将下载包放到自己喜欢的位置进行解压，打开目录，以管理员身份运行bindinstall.exe，修改安装目录；options选项卡中，只选tools only，其余全部去掉，最后点击安装。

![image-20230112212605876](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301122126918.png)

会提示安装visual，我这已经安装过了，点击close。![image-20230112212639608](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301122126648.png)

右键 此电脑》高级系统设置》环境变量》系统变量 ，在`Path`变量后面新增`C:\Program Files\ISC BIND 9\bin`

## 使用 Dig

>@<DNS服务器地址>：指定进行域名解析的**域名服务器**
>
>-p：指定域名服务器所使用**端口号**
>
>-t <类型>：指定要查询的DNS数据类型，默认是 A
>
>-b <ip地址>：当主机具有多个IP地址，指定使用本机的哪个IP地址向域名服务器发送域名查询请求
>
>-f <文件名称>：指定dig以批处理的方式运行，指定的文件中保存着需要批处理查询的DNS任务信息
>
>-x <IP地址>：执行逆向域名查询
>
>-4：使用IPv4
>
>-6：使用IPv6
>
>-h：显示指令帮助信息。

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

## nslookup

> ```
> //不带 -qt 参数，默认查询A记录
> nslookup -qt=[type] [domain] [dns-server]
> ```

```
//没有指定dns服务器，就采用系统默认的dns服务器。
nslookup www.baidu.com

//没有指定dns服务器，就采用系统默认的dns服务器。
nslookup www.baidu.com 192.168.3.50
```

# ~~PowerDNS~~（还有点问题）

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

---------------------------------------官方镜像-----------------------------------
# 默认 /etc/powerdns/pdns.conf
docker run -d --name pdns \
  -p 5300:53 \
  -p 5300:53/udp \
  -p 8888:8888 \
  powerdns/pdns-auth-45 
  
# ppowerdns/pdns-auth-45 
docker run -d --name pdns --link mysql\
  -p 5300:5300 \
  -p 5300:5300/udp \
  -p 8888:8888 \
  -v /etc/localtime:/etc/localtime:ro \
  -v /opt/docker/pdns/config/pdns.conf:/etc/powerdns/pdns.conf \
  powerdns/pdns-auth-45 
  
---------------------------------------------------------------------------------
# https://github.com/pschiffe/docker-pdns
#默认  /etc/pdns/pdns.conf
docker run -d --name pdns \
  -p 5300:53 \
  -p 5300:53/udp \
  -p 8888:8888 \
  pschiffe/pdns-mysql:4.5 
# pschiffe/pdns-mysql:4.5
docker run -d --name pdns --link mysql \
  -p 5300:53 \
  -p 5300:53/udp \
  -p 8888:8888 \
  -v /etc/localtime:/etc/localtime:ro \
  -v /opt/docker/pdns/config/pdns.conf:/etc/pdns/pdns.conf \
  pschiffe/pdns-mysql:4.5 
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

#pdns服务监听的地址
local-address=0.0.0.0
local-port=5300

#setgid=pdns
#setuid=pdns
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



## 2.PowerDNS Recursor

> Docker：https://hub.docker.com/r/powerdns/pdns-recursor-master

```markdown
# 创建数据目录 
mkdir -p /opt/docker/pdns-recursor/config && touch /opt/docker/pdns-recursor/config/forward-zones.conf && touch /opt/docker/pdns-recursor/config/recursor.conf

---------------------------------------官方镜像-----------------------------------
# powerdns/pdns-recursor-45
docker run -d --name pdns-recursor \
  -p 53:53 \
  -p 53:53/udp \
  -p 8889:8889 \
  -v /etc/localtime:/etc/localtime:ro \
  -v /opt/docker/pdns-recursor/config/forward-zones.conf:/etc/powerdns/forward-zones.conf \
  -v /opt/docker/pdns-recursor/config/recursor.conf:/etc/powerdns/recursor.conf \
  powerdns/pdns-recursor-45

----------------------------------------------------------------------------------
# pschiffe/pdns-recursor:4.5
# 容器配置文件在/etc/pdns文件夹中，recursor.conf 修改 forward-zones-file 路径
docker run -d --name pdns-recursor \
  -p 53:5301 \
  -p 53:5301/udp \
  -p 8889:8889 \
  -v /etc/localtime:/etc/localtime:ro \
  -v /opt/docker/pdns-recursor/config/forward-zones.conf:/etc/pdns/forward-zones.conf \
  -v /opt/docker/pdns-recursor/config/recursor.conf:/etc/pdns/recursor.conf \
  pschiffe/pdns-recursor:4.5
```

> Powerdns Recursor的配置文件为`/etc/powerdns/recursor.conf`
>
> 上面配置映射不行就用这个试试`/etc/pdns-recursor/recursor.conf`

### recursor.conf

> recursor.conf：https://docs.powerdns.com/recursor/settings.html#

```properties
api-key=qwerty
loglevel=7

#对应权威服务器的allow-recursion 允许哪些ip进行递归 (允许所有用户端请求 0.0.0.0/0)
allow-from=0.0.0.0/0     
#哪些域名需要自己的权威服务器来解析，格式：域名=权威服务器ip:端口
#forward-zones=+ling.pub=192.168.3.50:5300    
#也可以使用配置文件来配置哪些域名强制走内网dns解析
forward-zones-file=/etc/powerdns/forward-zones.conf
# 除forward-zones外其他所有的请求转发到以下DNS服务器
forward-zones-recurse=.=223.5.5.5,.=119.29.29.29,.=114.114.114.114,=8.8.8.8,.=1.1.1.1     

#服务监听地址
local-address=0.0.0.0
local-port=5301
#local-port=53

webserver=yes
webserver-address=0.0.0.0
webserver-allow-from=0.0.0.0/0,::/0
webserver-port=8889

#setgid=pdns-recursor
#setuid=pdns-recursor
```

### forward-zones.conf

> [forward-zones-file](https://docs.powerdns.com/recursor/settings.html#forward-zones-file)
>
> 后面的地址对应自建的权威服务器ip:端口

```
+ling.pub=192.168.3.50:5300
```

> 带 + 号开头为递归解析，不带 + 号开头为迭代解析



## 3.PowerDNS-Admin

> Github：https://github.com/PowerDNS-Admin/PowerDNS-Admin
>
> Github Doc：https://github.com/PowerDNS-Admin/PowerDNS-Admin/tree/master/docs/wiki
>
> Docker：https://hub.docker.com/r/powerdnsadmin/pda-legacy

```markdown
#  powerdnsadmin/pda-legacy
docker run -d --name pdns-admin \
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

		初次进去PowerDNS-Admin，要创建个账号然后设置powerdns地址，PDNS API KEY 是powerdns配置文件中设置的api-key

![image-20230112193257480](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301121932525.png)

![image-20230112193615699](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301121936735.png)

		配置后进入主页，点击侧边导航中的 PDNS 就可以看到 PDNS 的各项配置信息。如果填写有误，则没有任何信息。

![image-20230112193938954](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301121939045.png)

### 新增域名

		点击左侧 New Domain 来新增私有域名， Submit 提交。

![image-20230112194241736](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301121942810.png)

![image-20230112202626766](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301122026851.png)

### 新增解析记录

		点击域名记录右侧 Mange 按钮，点击 Add Record 添加 A 解析，然后点击 Apply Changes保存。



## 4.PowerDNS API

> 文档：https://doc.powerdns.com/md/httpapi/README/
>
> `8888`端口对应 pdns-api，`8889` 端口对应 pdns-recursor-api

可用postman 调用接口，请求头设置  X-API-Key = qwerty

```markdown
curl  -H 'X-API-Key: qwerty' http://192.168.3.50:8889/api/v1/servers/localhost
curl  -H 'X-API-Key: qwerty' http://192.168.3.50:8889/api/v1/servers/localhost/zones

#查看 PowerDNS 版本
curl http://192.168.3.50:8888 | grep PowerDNS

#查看添加的域名（qwerty 是配置文件中的api-key）
curl -H 'X-API-Key: qwerty' http://192.168.3.50:8888/api/v1/servers/localhost/zones

curl -H 'X-API-Key: qwerty' \
    192.168.3.50:8888/api/v1/servers/localhost/zones/ling.pub. | jq .
```



## 4.验证



### 验证私有域名

验证权威 DNS 是否正常

```
dig @192.168.3.50 -p 53 www.ling.pub
dig @192.168.3.50 -p 53 www.portainer.ling.pub

dig www.ling.pub
dig www.portainner.ling.pub
```


验证递归 DNS 是否正常

```cmd
dig @192.168.3.50 -p 53 www.baidu.com
```



## 5.问题

### 5.1添加域名报错

powerdns-admin错误日志：

![image-20230112001842808](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301120018933.png)

查看powerdns日志，发现有条SQL执行报错：

![image-20230112002614011](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301120026054.png)

~~解决，删除原表，新建~~

> 表结构：https://doc.powerdns.com/md/authoritative/backend-generic-mysql/

​		发现官方文档给的表结构也没日志中报错的表字段，看来不能用 latest 版本镜像，更换镜像版本为 powerdns/pdns-auth-45，powerdns/pdns-recursor-45



## 参考

> [内网私有域名解析](https://www.u.tsukuba.ac.jp/~s2036012/tech/webmaster/internal-dns.html#%E7%A7%81%E6%9C%89%E5%9F%9F%E5%90%8D%E8%BD%AC%E5%8F%91)
>
> [使用PowerDNS实现内网DNS解析](https://www.cnblogs.com/charnet1019/p/16005184.html)
>
> [10.1 PowerDNS安装部署](https://book.gxd88.cn/dns/install-powerdns)
>
> [自建 DNS 解析利器：PowerDNS+PowerDNS-Admin](https://ccav.me/self-built-dns-resolution-tool-powerdns-powerdns-admin.html)



# Bind

## 安装

> Dockerhub：https://hub.docker.com/r/sameersbn/bind

```markdown
#安装
docker run --name bind -d --restart=always \
  --publish 53:53/tcp \
  --publish 53:53/udp \
  --publish 10000:10000/tcp \
  -v /etc/localtime:/etc/localtime \
  -v /opt/docker/bind/data/:/data \
  -e WEBMIN_INIT_SSL_ENABLED=false  \
  sameersbn/bind:9.16.1-20200524 
  
#访问，默认账号密码：root/password
https://192.168.3.50:10000/ 
```

> 设置中文：登录Webmin后，依次点击 Webmin》Webmin Configuration，右侧点击 Language and Locale，下拉框选择Simplified Chinese(ZH_CN.UTF-8) 即可。

## 新增私有域名,并配置dns解析

选择dns

![image-20230115003631191](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301150036286.png)



新增域名

![image-20230115003932182](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301150039225.png)

填写内网中想要解析的域名，点击Create

![image-20230115004045628](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301150040676.png)

添加映射，添加完解析记录后要点击右上角 循环按钮 （applay configration） 生效

![image-20230115004355080](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301150043174.png)

![image-20230115004521956](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301150045996.png)

## 验证

```
dig @192.168.3.50 -p 53 portainer.home.pub
```

![image-20230115005236544](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301150052586.png)

## Bind配置文件

| 描述                                              |项 |
| --------------------------------------------------- | -------------- |
| 主配置文件，用于定义全局设置，DNS的zone等相关配置。 | /etc/named.conf    |
|named.conf子配置文件	|/etc/bind/named.conf.options|
|					|/etc/bind/named.conf.local|
|					|/etc/bind/named.conf.default-zones|
|                     |                  |



## Bind配置优化

1.配置文件添加 

> 指定哪些主机（或网络）可以查询这台名字服务器。

修改的内容对应宿主机配置文件：**/opt/docker/bind/data/bind/etc/named.conf.options** 

```
 allow-query { any;	};
```

![image-20230115030108627](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301150302588.png)



2.Forward转发（添加上游DNS服务器）

> 客户端设置自建内网DNS服务器后，当访问别的网站，内网DNS解析不了的就转发给如下添加的DNS服务器解析，保证能正常上网

![image-20230115012808621](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301150153430.png)

> 修改的内容对应宿主机配置文件：**/opt/docker/bind/data/bind/etc/named.conf.options** 



3..配置递归解析

> 递归查询：是指 DNS 服务器在收到用户发起的请求时，必须向用户返回一个准确的查询结果。如果 DNS 服务器本地没有存储与之对应的信息， 则该服务器需要询问其他服务器， 并将返回的查询结果提交给用户。
>
> 迭代查询：是指，DNS 服务器在收到用户发起的请求时，并不直接回复查询结果，而是告诉另一台 DNS 服务器的地址，用户再向这台 DNS 服务器提交请求，这样依次反复，直到返回查询结果。

修改宿主机配置文件 **/opt/docker/bind/data/bind/etc/named.conf.options** 

新增以下内容：

```
 recursion yes;                 # enables resursive queries
```

![image-20230115014444525](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301150144568.png)

## 配置文件

### **etc/bind/named.conf** 

```
// This is the primary configuration file for the BIND DNS server named.
//
// Please read /usr/share/doc/bind9/README.Debian.gz for information on the 
// structure of BIND configuration files in Debian, *BEFORE* you customize 
// this configuration file.
//
// If you are just adding zones, please do that in /etc/bind/named.conf.local

include "/etc/bind/named.conf.options";
include "/etc/bind/named.conf.local";
include "/etc/bind/named.conf.default-zones";
```

### **etc/bind/named.conf.options** 

```
options {
	directory "/var/cache/bind";

	// If there is a firewall between you and nameservers you want
	// to talk to, you may need to fix the firewall to allow multiple
	// ports to talk.  See http://www.kb.cert.org/vuls/id/800113

	// If your ISP provided one or more IP addresses for stable 
	// nameservers, you probably want to use them as forwarders.  
	// Uncomment the following block, and insert the addresses replacing 
	// the all-0's placeholder.

	// forwarders {
	// 	0.0.0.0;
	// };

	//========================================================================
	// If BIND logs error messages about the root key being expired,
	// you will need to update your keys.  See https://www.isc.org/bind-keys
	//========================================================================
	dnssec-validation auto;

      listen-on port 53 { any; };        //默认是any，表示允许所有网段的主机。可以改成自己所在的内网网段
      listen-on-v6 { any; };			//表示接收任何来源的查询请求
      
  	allow-query { any;	};
  
  	recursion yes;                 # enables resursive queries
 
	
    forward first;
	forwarders { 	// 指定上层DNS服务器
		223.5.5.5;
		119.29.29.29;
        180.76.76.76;
		114.114.114.114;
		8.8.8.8;
		1.1.1.1;
		};
};
```

### **etc/bind/named.conf.local** 

```
//
// Do any local configuration here
//

// Consider adding the 1918 zones here, if they are not used in your
// organization
//include "/etc/bind/zones.rfc1918";

zone "home.pub" {
	type master;
	file "/var/lib/bind/home.pub.hosts";
	};
```

#### **home.pub.hosts**

```
$ttl 38400
home.pub.	IN	SOA	9b5964243488. 1902325071.qq.com. (
			1673716753
			10800
			3600
			604800
			38400 )
home.pub.	IN	NS	9b5964243488.
portainer.home.pub.	IN	A	192.168.3.50
openwrt.home.pub.	IN	A	192.168.3.50
kibana.home.pub.	IN	A	192.168.3.50
```

### **etc/bind/named.conf.default-zones** 

```
// prime the server with knowledge of the root servers
zone "." {
	type hint;
	file "/usr/share/dns/root.hints";
};

// be authoritative for the localhost forward and reverse zones, and for
// broadcast zones as per RFC 1912

zone "localhost" {
	type master;
	file "/etc/bind/db.local";
};

zone "127.in-addr.arpa" {
	type master;
	file "/etc/bind/db.127";
};

zone "0.in-addr.arpa" {
	type master;
	file "/etc/bind/db.0";
};

zone "255.in-addr.arpa" {
	type master;
	file "/etc/bind/db.255";
};
```



# dnsmasq-china-list

> Github：https://github.com/felixonmars/dnsmasq-china-list
>
> Chinese-specific configuration to improve your favorite DNS server. Best partner for chnroutes.

```markdown
# 安装
wget https://raw.githubusercontent.com/felixonmars/dnsmasq-china-list/master/install.sh
chmod +x install.sh
sudo ./install.sh

# 查看运行状态，启动，关闭
systemctl status dnsmasq
systemctl start dnsmasq
systemctl stop dnsmasq
#dnsmasq生成配置文件目录
/etc/dnsmasq.d/

#查看端口状态
netstat -ntunlp |grep 53

```



# smartdns（√）

> Doc：https://pymumu.github.io/smartdns/
>
> Github：https://github.com/pymumu/smartdns
>
> Dockerhub：https://hub.docker.com/r/ghostry/smartdns

```
docker run --name gsmartdns -d --restart=always \
	-p 53:53 \
	-p 53:53/udp \
  	-v /opt/docker/gsmartdns:/smartdns \
  	--network op_net \
  	ghostry/smartdns
```

## 添加dnsmasq-china-list配置

> Github：https://github.com/felixonmars/dnsmasq-china-list
>
> 可以提高中文域名的解析速度

1.生成dnsmasq-china-list配置

```
git clone https://github.com/felixonmars/dnsmasq-china-list
cd dnsmasq-china-list
#使用make替换关键字效果  生成对应的conf文件
make smartdns SERVER=china
```
2.拷贝生成的配置文件到smartdns映射宿主机目录 /opt/docker/gsmartdns

```
cp *.smartdns.conf /opt/docker/gsmartdns
```

## smartdns.conf

> /opt/docker/gsmartdns/smartdns.conf

```
# https://github.com/pymumu/smartdns/blob/master/etc/smartdns/smartdns.conf
bind-tcp [::]:53
bind [::]:53
tcp-idle-time 3
#域名结果缓存个数
cache-size 4096
# 开启域名预取，smartdns将在域名ttl即将超时的时候，再次发送查询请求，并缓存查询结果供后续使用
prefetch-domain yes
serve-expired yes
serve-expired-ttl 0
# 上游DNS返回多个结果时，使用ping方式作为测速方法
speed-check-mode tcp:80,tcp:443,ping
rr-ttl-min 60
rr-ttl-max 86400

#日志记录等级
log-level debug

#定义dnsmasq-china-list  快速解析功能
#conf-file accelerated-domains.china.smartdns.conf
#conf-file apple.china.smartdns.conf
#conf-file google.china.smartdns.conf

#使用server  配置上游服务器
server 8.8.8.8 -blacklist-ip -check-edns
server-tcp 119.29.29.29
server-tcp 64.6.64.6
server-tcp 114.114.114.119
server-tcp 223.5.5.5
server-tls 1.1.1.1
server-tls 8.8.4.4
server-tls 9.9.9.9
server-https https://cloudflare-dns.com/dns-query

# 配置address 定义某个指定的域名IP解析关系
address /kibana.home.pub/192.168.3.50
address /halo.home.pub/192.168.3.50
address /openwrt.home.pub/192.168.3.50
address /portainer.home.pub/192.168.3.50
```

## 内网域名解析验证

```
# 192.168.3.3 是smartdns访问ip，使用dns默认端口53
dig @192.168.3.3 -p 53 portainer.home.pub
```

![image-20230115181630657](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301151816701.png)

### nginx配置示例

```yaml
http {
	
	# 其他省略
	....
	
	# portainer.home.pub
    server{
      listen 80;
      server_name  portainer.home.pub;
      #server_name  192.168.3.50;
    
      location / {
        proxy_pass  http://192.168.3.50:9000;
        #proxy_pass  http://www.baidu.com;
        proxy_set_header Host $proxy_host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      }
    }
    
    # openwrt.home.pub
    server{
      listen 80;
      server_name  openwrt.home.pub;
    
      location / {
        proxy_pass  http://192.168.3.4;
        proxy_set_header Host $proxy_host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      }
      location /favicon.ico {
        proxy_pass  http://192.168.3.4/luci-static/argon/favicon.ico;
        proxy_set_header Host $proxy_host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      }
    }
    
    # kibana.home.pub
    server{
      listen 80;
      server_name  kibana.home.pub;
    
      location / {
        proxy_pass  http://192.168.3.50:5601;
        proxy_set_header Host $proxy_host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
      }
    }
}
```



### openwrt

http://openwrt.home.pub/，openwrt有点问题

nginx错误日志：

![image-20230115181149733](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301151811774.png)



### portainer

http://portainer.home.pub/，portainer映射访问正常

![image-20230115180430875](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301151804966.png)

### kibana

http://kibana.home.pub/，kibana正常

![image-20230115180510010](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301151805074.png)
