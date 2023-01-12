# PowerDNS

使用自建DNS服务器实现内网域名解析



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

#pdns服务监听的地址
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
#local-port=53

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

	点击域名记录右侧 Mange 按钮，点击 Add Record 添加 CNAME 和 A解析，然后点击 Apply Changes保存。



## 验证

### Windows安装 dig工具

		下载dig安装包，下载地址：https://www.isc.org/download/ ，选择BIND，选择 Current-Stable 版本，选择 [ISC-maintained Packages](https://isc.org/blogs/bind-9-packages/)

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

### 4.2 recursor递归服务器使用53作为监听端口启动报错

![image-20230113001712031](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301130017061.png)



## 参考

> [内网私有域名解析](https://www.u.tsukuba.ac.jp/~s2036012/tech/webmaster/internal-dns.html#%E7%A7%81%E6%9C%89%E5%9F%9F%E5%90%8D%E8%BD%AC%E5%8F%91)
>
> [使用PowerDNS实现内网DNS解析](https://www.cnblogs.com/charnet1019/p/16005184.html)

