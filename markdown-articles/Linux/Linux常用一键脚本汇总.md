---
title: Linux常用一键脚本汇总
author: wulilh
date: 2023-03-06 23:39:13
tags:
  - linux
  - centos
categories: Linux
link: 
references:
  - https://aoarasi.com/archives/onekey
---



# 常用脚本

## 0.系统优化脚本(初始化)

```none
bash -c "$(curl -L s.aaa.al/init.sh)"
```

## 1.获取公网地址

```none
curl ip.sb
```

## 2.多合一脚本

```none
wget -O box.sh https://raw.githubusercontent.com/BlueSkyXN/SKY-BOX/main/box.sh && chmod +x box.sh && clear && ./box.sh
```

## 3.IO测试+测速

```none
wget -qO- https://raw.githubusercontent.com/oooldking/script/master/superbench.sh | bash
```

## 4.回程路由脚本一

```none
wget -qO- git.io/besttrace | bash
```

## 5.回程路由脚本二

```none
curl https://raw.githubusercontent.com/zhucaidan/mtr_trace/main/mtr_trace.sh|bash
```

## 6.流媒体解锁查看

```none
bash -c "$(curl -L mcnb.top/netflix.sh)"
```

## 7.BBR

```none
wget -N --no-check-certificate "https://raw.githubusercontent.com/chiakge/Linux-NetSpeed/master/tcp.sh" && chmod +x tcp.sh && ./tcp.sh 
```

