# ELK

[ELK(ElasticSearch, Logstash, Kibana)搭建实时日志分析平台](https://my.oschina.net/itblog/blog/547250)
[ELK快速入门二-通过logstash收集日志](https://www.cnblogs.com/yanjieli/p/11187573.html)

[ELK之日志查询、收集与分析系统](https://www.cnblogs.com/zlslch/p/6621794.html)
[ELK实时日志分析平台环境部署--完整记录](https://www.cnblogs.com/kevingrace/p/5919021.html)

[Filebeat简单配置](https://blog.csdn.net/junxuezheng/article/details/108411022) 

# Graylog

[比ELK更简洁、高效！企业级日志平台后起之秀Graylog](https://baijiahao.baidu.com/s?id=1693945682253383428)




# [Elasticsearch与JDK版本对应关系](https://blog.csdn.net/gaogzhen/article/details/105966687)

![ELK流程图.png](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302012118410.png)



# 一、Windows安装ELK

JDK版本：jdk1.8
ElasticSearch版本：elasticsearch-7.12.1
Logstash版本：logstash-7.12.1
Filebeat版本：filebeat-7.12.1



## 1. 安裝elasticsearch

下载地址：<https://www.elastic.co/cn/downloads/elasticsearch>



### 1.1 修改配置文件

下载解压。

#### elasticsearch-env.bat

使用es自带jdk
打开bin/elasticsearch-env.bat文件
将

```xml
if defined ES_JAVA_HOME (
  set JAVA="%ES_JAVA_HOME%\bin\java.exe"
  set JAVA_TYPE=ES_JAVA_HOME
) else if defined JAVA_HOME (
  rem fallback to JAVA_HOME
  echo "warning: usage of JAVA_HOME is deprecated, use ES_JAVA_HOME" >&2
  set JAVA="%JAVA_HOME%\bin\java.exe"
  set "ES_JAVA_HOME=%JAVA_HOME%"
  set JAVA_TYPE=JAVA_HOME
) else (
  rem use the bundled JDK (default)
  set JAVA="%ES_HOME%\jdk\bin\java.exe"
  set "ES_JAVA_HOME=%ES_HOME%\jdk"
  set JAVA_TYPE=bundled JDK
)
```

改成

```xml
set JAVA="%ES_HOME%\jdk\bin\java.exe" 
set JAVA_HOME="%ES_HOME%\jdk" 
```



#### elasticsearch.yml

修改config/elasticsearch.yml，自定义属性

```xml
cluster.name: elk-app
node.name: node-1

#0.0.0.0 代表可以从任何地方访问es
network.host: 0.0.0.0
http.port: 9200
#discovery.seed_hosts,cluster.initial_master_nodes任选一个打开注释
# 机器ip 
discovery.seed_hosts: ["192.168.8.106"]
或者
#这个内容跟上面的node.name保持一致
cluster.initial_master_nodes: ["node-1"]
```

启动bin/elasticsearch.bat。
\----------------------------------------------------------

> 问题一：
> Exception org.elasticsearch.ElasticsearchException: X-Pack is not supported and Machine Learning is not available for \[windows-x86]; you can use the other X-Pack features (unsupported) by setting xpack.ml.enabled: false in elasticsearch.yml

解决：
在elasticsearch-7.6.1\config\elasticsearch.yml文件中，加一行xpack.ml.enabled: false
![es.png](..\assets\vkbefo\1620379639218-d1e5e5b8-bfe5-4df8-8d68-9d1a73e86be7.png)
\----------------------------------------------------------

访问：<http://localhost:9200/> 

### 1.2. 安装elasticsearch-head-master插件

下载：<https://github.com/mobz/elasticsearch-head>

先安装nodejs

然后解压进入head目录

1. 设置成淘宝的镜像重新安装 npm config set registry [https://registry.npm.taobao.org](https://registry.npm.taobao.org/)
2. 执行命令：npm install(此处是为安装进行安装pathomjs)
3. 安装完成之后**npm run start**，启动head插件

修改es参数，修改elasticsearch-7.6.1\config\elasticsearch.yml文件

```bash
# 增加新的参数，这样head插件可以访问es
http.cors.enabled: true 
http.cors.allow-origin: "*"
```

![es2.png](..\assets\vkbefo\1620380440168-0d4e1f71-6835-475a-8591-0a4981925c1c.png) <a name="qv4th"></a>

## 2. 安装Kibana



### 2.1 修改配置文件

config/kibana.yml。

```xml
server.port: 5601
server.host: "localhost"
elasticsearch.hosts: ["http://localhost:9200"]
i18n.locale: "zh-CN"
```



### 2.2 启动Kibana

bin/kibana.bat

访问地址
<http://localhost:5601/> 

## 3. 安装logstash

下载：<https://www.elastic.co/cn/downloads/logstash>
下载后解压。

### 3.1 创建Logstash配置文件

在logstash-7.12.1\config\conf.d目录下创建文件tomcat\_to\_es.conf：

```xml
input {
  file {
    path => "D:/dev-env/apache-tomcat-8.5.61/logs/catalina.*.log"
	type => "tomcat"
	tags => ["node1"]
	
    start_position => "end"	#文件读取位置, end 从尾开始读 beginning从头开始读
	#sincedb_path => "/dev/null"	#每次重启,重新读日志文件从头读到尾
  }
  
   file {
    path => "D:/dev-env/apache-tomcat-8079/logs/catalina.*.log"
	type => "tomcat"
	tags => ["node2"]
    start_position => "end"	#文件读取位置, end 从尾开始读  
	#sincedb_path => "/dev/null"	#每次重启,重新读日志文件从头读到尾  
  }
}

filter {
#******************* nginx ***********************
#todo

#******************* tomcat ***********************
	if [type] == "tomcat" {
		mutate {
			remove_field => ["@version","prospector","input","beat","offset"]
		}
		grok {
			match => {
				"message" => "%{TIMESTAMP_ISO8601:access_time} %{LOGLEVEL:loglevel} \[%{DATA:exception_info}\] - \<%{MESSAGE:message}\>"
			}
			pattern_definitions => {
				"MESSAGE" => "[\s\S]*"
			}
		}
		geoip {
			source => "clientip"    #将ip地址检索出来
		}
		date {
			match => [ "access_time","yyyy-MM-dd HH:mm:ss,SSS" ]
		}
		mutate {
			remove_field => ["access_time","[message][0]"]
		}
	}
}

output {

 if [type] == "tomcat" { 
	  elasticsearch {
		hosts => ["127.0.0.1:9200"]
		index => "%{[tags][0]}-%{+YYYY-MM-dd}"
	  }
	  stdout { codec => rubydebug }
	}
}
```



### 3.2 启动Logstash

在安装bin目录下打开命令窗口，执行命令(指定配置文件启动)：

```xml
logstash -f D:\dev-env\elk\logstash-7.12.1\config\conf.d\log_to_es.conf
```



## 安装Filebeat

解压。

#### 1. 更改配置文件



# 二、Linux安装ELK



## ELK版本

环境JDK版本：jdk1.8
ElasticSearch版本：elasticsearch-7.12.1
Logstash版本：logstash-7.12.1
Kibana版本：logstash-7.12.1

## 1. [安装ElasticSearch](https://www.cnblogs.com/blogjun/articles/82fb37eb418a2ed8dc6adf3d306aa2ef.html)



### 1.1 上传文件

使用压缩文件进行安装使用。
mkdir elk&#x20;
将文件丢到新建的文件夹中
![image.png](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302012124399.png) 

### 1.2 创建新用户

elasticsearch不用root账号启动，所以要新建用户启动。
用root账号启动时报错：

> java.lang.RuntimeException: can not run elasticsearch as root

创建新用户

```bash
useradd elkuser
# 更改解压后es文件夹的归属者cd
chown -R elkuser.elkuser elasticsearch-7.12.1
```



### 1.3 修改配置文件

[es使用自带jdk](https://blog.csdn.net/xiaoxiong_web/article/details/105597150)，修改elk/elasticsearch-7.12.1/bin/elasticsearch-env文件
将

```bash
# now set the path to java
if [ ! -z "$ES_JAVA_HOME" ]; then
  JAVA="$ES_JAVA_HOME/bin/java"
  JAVA_TYPE="ES_JAVA_HOME"
elif [ ! -z "$JAVA_HOME" ]; then
  # fallback to JAVA_HOME
  echo "warning: usage of JAVA_HOME is deprecated, use ES_JAVA_HOME" >&2
  JAVA="$JAVA_HOME/bin/java"
  JAVA_TYPE="JAVA_HOME"
else
  # use the bundled JDK (default)
  if [ "$(uname -s)" = "Darwin" ]; then
    # macOS has a different structure
    JAVA="$ES_HOME/jdk.app/Contents/Home/bin/java"
  else
    JAVA="$ES_HOME/jdk/bin/java"
  fi
  JAVA_TYPE="bundled JDK"
fi

```

修改为

```bash
# use the bundled JDK (default)
  if [ "$(uname -s)" = "Darwin" ]; then
    # macOS has a different structure
    JAVA="$ES_HOME/jdk.app/Contents/Home/bin/java"
  else
    JAVA="$ES_HOME/jdk/bin/java"
  fi
  JAVA_TYPE="bundled JDK"

```

修改完启动，报了个文件权限拒绝问题，找到那个文件重新赋权限

```bash
# 先切换到root用户
su

#修改权限
chown -R elkuser.elkuser elasticsearch.keystore

# 切换到elkuser
su elkuser
```

调整内存占用大小，修改config/jvm.options文件

```bash
################################################################
## IMPORTANT: JVM heap size
################################################################
##
## The heap size is automatically configured by Elasticsearch
## based on the available memory in your system and the roles
## each node is configured to fulfill. If specifying heap is
## required, it should be done through a file in jvm.options.d,
## and the min and max should be set to the same value. For
## example, to set the heap to 4 GB, create a new file in the
## jvm.options.d directory containing these lines:
##
-Xms2g
-Xmx2g
```



### 1.4 启动elasticsearch(运行端口：9200)

后台启动
`./elasticsearch -d`
又报错

> ERROR: \[3] bootstrap checks failed. You must address the points described in the following \[3] lines before starting Elasticsearch.bootstrap check failure \[1] of \[3]:
> max file descriptors \[4096] for elasticsearch process is too low, increase to at least \[65535]
>
> bootstrap check failure \[2] of \[3]:
> max virtual memory areas vm.max\_map\_count \[65530] is too low, increase to at least \[262144]
>
> bootstrap check failure \[3] of \[3]:
> the default discovery settings are unsuitable for production use; at least one of \[discovery.seed\_hosts, discovery.seed\_providers, cluster.initial\_master\_nodes] must be configured
>
> ERROR: Elasticsearch did not exit normally - check the logs at /usr/local/elk/elasticsearch-7.12.1/logs/my-application.log

说明：linux中elasticsearch最大文件打开数太小，需要我们修改到对应的数值。

```bash
---------------------------------------------------------------------
问题一：max file descriptors [4096] for elasticsearch process is too low, increase to at least [65535]
# 修改/etc/security/limits.conf文件,可能会提示这是个只读文件,用root账户修改文件权限,添加可写权限
vim /etc/security/limits.conf
或者在/etc/security/limits.d目录下新增子文件
cd /etc/security/limits.d
touch elasticsearch.conf
#添加或修改如下行：
elkuser        hard    nofile           65536
elkuser        soft    nofile           65536

---------------------------------------------------------------------
问题二：max virtual memory areas vm.max_map_count...
# 修改/etc/sysctl.conf文件
vim /etc/sysctl.conf
# 添加：
vm.max_map_count=262144
# 重启内核参数配置
/sbin/sysctl -p

---------------------------------------------------------------------
问题三：the default discovery settings are unsuitable for production use; at least one of [discovery.seed_hosts,。。。
# 修改elk/elasticsearch-7.12.1/config/elasticsearch.yml
#配置以下三者，最少其一
#[discovery.seed_hosts, discovery.seed_providers, cluster.initial_master_nodes]
cluster.initial_master_nodes: ["node-1"] #这里的node-1为node-name配置的值

```

还有一些文件权限异常

```bash
# 切root用户
su
#修改文件夹归属者
chown -R elkuser.elkuser elasticsearch-7.12.1
#切换到elkuser
su elkuser
```

修改完再重新启动
`./elasticsearch -d`
![image.png](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302012124642.png)

附带上修改好的配置文件
[elasticsearch.yml](https://www.yuque.com/attachments/yuque/0/2021/yml/1235436/1620467469334-043d870b-9548-4ee7-a53b-b148502b38f7.yml?_lake_card=%7B%22src%22%3A%22https%3A%2F%2Fwww.yuque.com%2Fattachments%2Fyuque%2F0%2F2021%2Fyml%2F1235436%2F1620467469334-043d870b-9548-4ee7-a53b-b148502b38f7.yml%22%2C%22name%22%3A%22elasticsearch.yml%22%2C%22size%22%3A2828%2C%22type%22%3A%22%22%2C%22ext%22%3A%22yml%22%2C%22status%22%3A%22done%22%2C%22taskId%22%3A%22u9b05d70f-c04b-4bf1-8da9-865bf114d55%22%2C%22taskType%22%3A%22upload%22%2C%22id%22%3A%22ud5a19124%22%2C%22card%22%3A%22file%22%7D)



## 2. 安装Logstash

> grok提供多个常用正则表达式可供使用，这些正则表达式定义在
> /usr/local/elk/logstash-7.12.1/vendor/bundle/jruby/2.5.0/gems/logstash-patterns-core-4.3.1/patterns/legacy/grok-patterns文件中



### 2.1 修改配置文件



#### 2.1.1 [修改tomcat.out日志文件格式](https://blog.csdn.net/recotone/article/details/81032769?utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromMachineLearnPai2%7Edefault-10.vipsorttest\&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFromMachineLearnPai2%7Edefault-10.vipsorttest)

```bash
原本格式:
14-Apr-2021 17:51:59.140 信息 [main] org.apache.catalina.startup.VersionLoggerListener.log Server.服务器版本: Apache
想要的时间格式:
2017-07-10 21:08:44.658 [INFO] [main] org.apache.catalina.startup.Catalina.start Server startup in 36051 ms
```

修改 **${tomcatHome}/conf/logging.properties** 文件
在`1catalina.org.apache.juli.AsyncFileHandler.prefix = catalina.`
后面加上

```bash
############### 在后面加上这一句即可 ########
1catalina.org.apache.juli.AsyncFileHandler.formatter = java.util.logging.SimpleFormatter
java.util.logging.SimpleFormatter.format = %1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL [%4$s] [%3$s] %2$s %5$s %6$s%n
```

```bash
handlers = 1catalina.org.apache.juli.AsyncFileHandler, 2localhost.org.apache.juli.AsyncFileHandler, 3manager.org.apache.juli.AsyncFileHandler, 4host-manager.org.apache.juli.AsyncFileHandler, java.util.logging.ConsoleHandler

.handlers = 1catalina.org.apache.juli.AsyncFileHandler, java.util.logging.ConsoleHandler

############################################################
# Handler specific properties.
# Describes specific configuration info for Handlers.
############################################################

1catalina.org.apache.juli.AsyncFileHandler.level = FINE
1catalina.org.apache.juli.AsyncFileHandler.directory = ${catalina.base}/logs
1catalina.org.apache.juli.AsyncFileHandler.prefix = catalina.
#################### 自定义时间格式 #####################
1catalina.org.apache.juli.AsyncFileHandler.formatter = java.util.logging.SimpleFormatter
java.util.logging.SimpleFormatter.format = %1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL [%4$s] [%3$s] %2$s %5$s %6$s%n
#################### 自定义时间格式 #####################
```

![image.png](..\assets\vkbefo\1620613485613-2769cedd-46cb-409c-813b-6f7eede922ff.png) 

#### 2.1.2 修改logstash配置文件

自定义grok patterns文件
在/usr/local/elk/logstash/mypatterns\_dir创建文件mypatterns\_file，内容如下：

```bash
MY_PATTERN [A-Z]+
```

修改filter

```bash
filter {
	grok{
    patterns_dir=>["/usr/local/elk/logstash/mypatterns_dir"]
    match=>{
      "message"=>"%{IP:clientip}\s+%{MY_PATTERN:mypattern}"
    }
  }
}
```

解压进入config文件夹
`mkdir conf.d`
进入conf.d文件夹，创建配置文件
`touch log_to_es.conf`

```bash
###############################################################
#
#		Logstash 安装位置: /usr/local/elk/logstash-7.12.1
#
###############################################################

input {

  # 购花APP日志
  file {
    path => ["/usr/local/app-trade/app-31000/logs/catalina.out"]
    type => "tomcat"
	add_field => {"log_ip" => "10.10.66.155"}	#添加自定义的字段
	tags => "gouhua"
  sincedb_path => "/dev/null"
  }
  
  # Filebeat
  #beats {port => "5044"}
}

filter {

	ruby {
		# logstash timestamp记录的时间戳为UTC时间。我们的时区早8个小时
		# 解决
		code => "event.timestamp.time.localtime"
	}

 #******************* tomcat ***********************
	if [type] == "tomcat" {
	
		#合并多行异常信息
		#multiline {
		#	pattern => "^\s" 
		#	what => "previous"
		#}

		
		grok {
		
		#	pattern_definitions => {
		#		"MESSAGE" => "[\s\S]*"
		#	}
		
			# 自定义正则文件
			patterns_dir=>["/usr/local/elk/logstash-7.12.1/mypatterns_dir"]
			
			match => {
				"message" => "%{TOMCAT_DATETIME_CN:datetime} %{LOGLEVEL:level} %{JAVALOGMESSAGE:msg}"
			}
		
		}
		
		#geoip {
		#	source => "clientip"    #将ip地址检索出来
		#}
		#date {
		#	match => [ "access_time","yyyy-MM-dd HH:mm:ss,SSS" ]
		#}
		
		#date{
		#	match => ["datetime", "yyyy-MM-dd HH:mm:ss"]
		#	target => "@timestamp"
		#}
		
		#mutate {
		#	remove_field => ["access_time","[message][0]"]
		#}
		
		mutate {
			remove_field => ["@version","_score","_type","host","prospector","input","beat","offset"]
		}
	}
 
}

output {
 
	if [type] == "tomcat" { 
		elasticsearch {
			hosts => ["10.10.66.154:9200"]
			#index => "%{tags}-%{+YYYY-MM-dd}"
			index => "%{tags}-%{+YYYY-MM}"
      #user => "elastic"
      #password => "elastic_password"
		}
	}
	stdout { codec => rubydebug }
}

```



### 2.2 启动Logstash(运行端口：5044)

进入bin目录指定配置文件，后台启动logstash(运行端口：5044)

- 后台启动 不打印日志
- nohup ./logstash -f /usr/local/elk/logstash-7.12.1/config/conf.d/log\_to\_es.conf >/dev/null &
- 后台启动 打印日志
- nohup ./logstash -f /usr/local/elk/logstash-7.12.1/config/conf.d/log\_to\_es.conf &>logstash.log &



#### 常用启动参数

各个启动参数的作用：

- \-e #立即启动实例，例如：./logstash -e "input {stdin {}} output {stdout {}}"
- \-f #指定启动实例的配置文件，例如：./logstash -f config/test.conf
- \-t #测试配置文件的正确性，例如：./logstash -f config/test.conf -t
- \-l #指定日志文件名称，例如：./logstash -f config/test.conf -l logs/test.log
- \-w #指定filter线程数量，不指定默认是5，例如：./logstash-f config/test.conf -w 8



## 3. 安装Kibana



### 3.1 修改配置文件

vi config/kibana.yml
修改以下几项

```bash
server.port: 5601
server.host: "10.10.66.154"
elasticsearch.hosts: ["http://10.10.66.154:9200"]
kibana.index: ".kibana"
i18n.locale: "zh-CN"
```



### 3.2 启动Kibana(运行端口：5601)

nohup ./kibana &>kibana.log &

问题:

> Kibana should not be run as root.  Use --allow-root to continue.

kibana也不能用root账号启动

```bash
# 切换到root用户
su
#修改文件夹归属者
chown -R elkuser.elkuser kibana-7.12.1-linux-x86_64
#切换到elkuser
su elkuser
# 用elkuser用户启动
nohup ./kibana &>kibana.log &
```

访问Kibana地址
<http://10.10.66.154:5601/>



## 4. 安装Filebeat



### 4.1 修改配置

filebeat配置文件filebeat.yml

```bash
filebeat.inputs:
- type: log
  enabled: true
  paths:
    - /data/Application/tomcat-portal/logs/catalina.out
  #fields:
  #  log_source: tomcatlog_200
  # 包含警告和错误日志
  #include_lines: [".*WARN.*", ".*ERROR.*"]
  ### Multiline options 将日志中的错误日志合并成一行 
  multiline.pattern: ^\d{4}-\d{2}-\d{2}
  multiline.negate: true
  multiline.match: after
  
# ======================= Elasticsearch template setting =======================

setup.template.settings:
  index.number_of_shards: 1
  #index.codec: best_compression
  #_source.enabled: false
 
# =================================== Kibana ===================================

# Starting with Beats version 6.0.0, the dashboards are loaded via the Kibana API.
# This requires a Kibana endpoint configuration.
setup.kibana:

  # Kibana Host
  # Scheme and port can be left out and will be set to the default (http and 5601)
  # In case you specify and additional path, the scheme is required: http://localhost:5601/path
  # IPv6 addresses should always be defined as: https://[2001:db8::1]:5601
  host: "10.10.66.154:5601"
  
# ------------------------------ Logstash Output -------------------------------
output.logstash:
  # The Logstash hosts
  hosts: ["10.10.66.154:5044"]
```

logstash配置文件

```bash
# 监听5044端口作为输入
input {
  beats {port => "5044"}
}

filter {
  #Only matched data are send to output.
}

output {
 
 if [type] == "app-gouhua-log" { 
		  elasticsearch {
			hosts => ["10.10.66.154:9200"]
			index => "app-gouhua-log-%{+YYYY-MM-dd}"
		  }
	}
}
```



### 4.2 启动filebeat

- 测试配置文件的正确性，例如：./logstash -f config/test.conf -t
- 启动

nohup ./filebeat -e -c /usr/local/elk/filebeat-7.12.1-linux-x86\_64/filebeat.yml &>filebeat.log &
问题：

> Failed to connect to backoff(elasticsearch(http://10.10.66.154:5044)): Get "http://10.10.66.154:5044": read tcp 10.10.66.154:60270->10.10.66.154:5044: read: connection reset by peer

filebeat -》 logstash 连接失败

## 5. 关闭服务

1. 关闭es

ps -ef | grep elasticsearch
kill -9  pid

2. 关闭Logstash

ps -ef | grep logstash

3. 关闭Kibana

netstat -nap | grep 5601
kill -9 pid

4. 关闭Filebeat

ps -ef | grep filebeat
