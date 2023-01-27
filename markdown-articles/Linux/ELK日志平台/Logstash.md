---
title: Logstash
url: https://www.yuque.com/tangsanghegedan/ilyegg/bl89t5
---

<a name="zN46y"></a>

# [ELK技术栈-Logstash的详细使用](https://www.jianshu.com/p/f0410eb0082c)

<a name="UwgtO"></a>

# [ELK（六）：LOGSTASH——INPUT/FILE使用详解](https://www.freesion.com/article/8799311470/)

<a name="xt88e"></a>

# [logstash-input-file](https://leokongwq.github.io/2015/07/13/logstash-input-file.html)

<a name="rA6bX"></a>

# [logstash神器之grok](https://www.jianshu.com/p/d3042a08eb5e)

<a name="n5uYd"></a>

# [logstash.conf](https://gist.github.com/cgswong/95c86e0b16e2c6fb6721)

<a name="V7box"></a>

# [ELK對Tomcat日誌雙管齊下](https://www.twblogs.net/a/5ba115b02b71771a4da89aaf)

<a name="qMwe9"></a>

# [logstash解析tomcat的catalina.out日志字段](https://blog.51cto.com/liuzhengwei521/2156303)

配置文件参考

```xml
input {
    file {
            type => "tzclaim"
            path => "/app/MT/tomcat-tzclaim/logs/catalina.out"
                        tags => ["aaa"]
    }
    file {
            type => "interface"
            path => "/app/MT/tomcat-interface/logs/catalina.out"
                        tags => ["bbb"]
    }
}
filter {
        grok {
                match => { "message" => "(\[\s?%{LOGLEVEL:level}\] %{DATA:class} %{DATA:logtime} - )?%{GREEDYDATA:msg}" }
        }
}

output {
    if "aaa" in [tags] {
                elasticsearch {
                        hosts => ["10.10.0.xx:9200"]
                        index => "tomcat-36-tz-%{+YYYY.MM.dd}"
                        id => "output_3"
                              }
                       }
    if "bbb" in [tags] {
                elasticsearch {
                        hosts => ["10.10.0.xx:9200"]
                        index => "tomcat-36-it-%{+YYYY.MM.dd}"
                        id => "output_4"
                              }
                      }
}
```
