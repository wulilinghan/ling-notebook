---
title: 笔记本安装CentOS 7.9
url: https://www.yuque.com/tangsanghegedan/ilyegg/xkwcr1
---

> 笔记本好久没使用了，打算作为服务器作为自己学习使用，所以Windows都不打算要了，只装个CentOS。

<a name="LLeVj"></a>

# 笔记本安装CentOS 7.9

CentOS 7.9 下载：<http://mirrors.aliyun.com/centos/7/isos/x86_64/CentOS-7-x86_64-DVD-2009.iso>

<a name="BWQ9L"></a>

## 使用Rufus制作U盘启动

> 制作U盘启动会清空数据请提前做好备份

Rufus下载：<https://rufus-usb.cn.uptodown.com/windows>
![image.png](..\assets\xkwcr1\1621157960411-2c1b028d-e5cf-422a-a996-1da252817f7f.png)
文件系统就用FAT32，CentOS默认不支持NTFS，然后开始制作，时间还挺长的，十几分钟吧。
![](..\assets\xkwcr1\1621158132406-d0493a43-0319-49b1-9583-ea193fd4d220.webp) <a name="uVdRy"></a>

## CentOS安装

进入bios设置u盘启动为第一启动
![image.png](..\assets\xkwcr1\1621158638133-330d078b-edbc-4c5c-948f-bb41ef7477a7.png)
选择第一项`**Install CentOS 7**`，回车。
小黑窗刷刷刷的一排排指令打下来，然后进入语言选择，我选的中文。
![image.png](..\assets\xkwcr1\1621158711254-ba10b9f3-951b-4291-9cf7-63680d95e877.png)
接下来 <a name="XFjFR"></a>

### 安装信息摘要

![image.png](..\assets\xkwcr1\1621158742612-a3b3cd22-e152-4d3a-bb17-a623bb06cfdf.png)

<a name="Bhqij"></a>

#### 安装位置

因为我本来是Windows系统，笔记本好久没使用了，打算作为服务器作为自己学习使用，原本有一块128G和一块500G的固态，所以直接格式化了那块128G的硬盘（选择磁盘的时候，有个回收空间的按钮），图是网上的，分区我选的自动配置分区。
![image.png](..\assets\xkwcr1\1621159026640-0cbce45d-abf5-428a-ad59-66cd974089bf.png) <a name="BVAPS"></a>

#### 软件选择

笔记本最好选择GNOME, 否则最小安装中没有无线网卡相关配置，自己配置起来比较麻烦。
![](..\assets\xkwcr1\1621159251805-d3ba5894-6a02-4947-967e-15ba624def56.webp) <a name="zplMB"></a>

#### 网络与主机名

这里配置WiFi的时候我当时出了点问题，就没配置了，安装完后进入桌面才配置的。

<a name="r4t3g"></a>

#### 开始安装

然后开始安装，安装过程中可以设置root账号密码，以及创建用户。
安装完成后有个许可信息，点进去勾上，完成配置就可以进入桌面了。
![image.png](..\assets\xkwcr1\1621160143087-2cf21838-f501-45c9-8987-c744c51cce80.png) <a name="L0hj2"></a>

## 修改默认启动模式

```xml
查看当前默认启动模式
systemctl get-default
设置默认图形化模式启动
systemctl set-default graphical.target
设置默认命令行模式启动
systemctl set-default multi-user.target
```

<a name="QqWem"></a>

## 硬盘挂载

使用df-h，发现还有块500G没挂载。
打开文件夹，+其他位置，点击DATA磁盘，报错：

> Filesystem type ntfs not configured in kernel.

![image.png](..\assets\xkwcr1\1621168124123-c0a3cbab-c7d7-4aea-8dc5-c5ee11a0c3e8.png)
![image.png](..\assets\xkwcr1\1621168046396-240f5b7b-f57c-416e-990e-08ffa4cc2d68.png)
磁盘文件系统为NTFS，CentOS[默认不支持NTFS格式](https://access.redhat.com/discussions/687423)。
解决方法：

```xml
# 添加源
wget -O /etc/yum.repos.d/epel.repo http://mirrors.aliyun.com/repo/epel-7.repo

yum -y install ntfs-3g
```

<a name="qokGk"></a>

## 设置笔记本关闭盖子不挂起

```xml
vim /etc/systemd/logind.conf

--------------------------------------------------------------
[Login]
#NAutoVTs=6
#ReserveVT=6
#KillUserProcesses=no
#KillOnlyUsers=
#KillExcludeUsers=root
#InhibitDelayMaxSec=5
#HandlePowerKey=poweroff
#HandleSuspendKey=suspend
#HandleHibernateKey=hibernate
#HandleLidSwitch=suspend
# 关盖子不休眠
HandleLidSwitch=ignore
#HandleLidSwitchDocked=ignore
#PowerKeyIgnoreInhibited=no
#SuspendKeyIgnoreInhibited=no
#HibernateKeyIgnoreInhibited=no
#LidSwitchIgnoreInhibited=yes
#IdleAction=ignore
#IdleActionSec=30min
#RuntimeDirectorySize=10%
#RemoveIPC=no
#UserTasksMax=
--------------------------------------------------------------

#设置HandleLidSwitch为lock
systemctl restart systemd-logind
```

> 其他属性如下：
> HandlePowerKey=poweroff　　　　 按下电源键后会触发的行为
> HandleSleepKey=suspend　　 　　 按下挂起键后会触发的行为
> HandleHibernateKey=hibernate          按下休眠键后会触发的行为
> HandleLidSwitch=suspend　　 　　    关闭笔记本盖子后会触发的行为

<a name="qGZlt"></a>

## 安装VNCServer

> 方便图形化操作，就可以不用鼠标键盘切过来切过去的

<a name="qzTqj"></a>

### 安装

安装命令

```xml
yum install tigervnc tigervnc-server -y
```

```xml
rpm -qa | grep vnc
```

![image.png](..\assets\xkwcr1\1621162057670-79fef95d-5c1f-434a-954e-35078d6bcdc4.png)
按照网上资料，编辑配置文件` vim /etc/sysconfig/vncservers`
发现：
![image.png](..\assets\xkwcr1\1621162133476-534e9e00-3b5e-47e2-af51-07a9f01c4a74.png)
配置文件已被 `/lib/systemd/system/vncserver@.service` 文件取代。

```xml
vim /lib/systemd/system/vncserver@.service
```

内容开头提到：
![image.png](..\assets\xkwcr1\1621165934598-87abaff4-9446-40f0-89a9-a51d4d21fee4.png) <a name="RLdej"></a>

#### 给vnc用户开放vncserver访问

```xml
# 复制配置文件，重新命名为vncserver@:1.service，一个编号代表一个配置文件,好像只能用数字,我用英文,服务就启动不了
cp /lib/systemd/system/vncserver@.service /etc/systemd/system/vncserver@:1.service

#编辑配置文件
vim /etc/systemd/system/vncserver@:1.service
```

![image.png](..\assets\xkwcr1\1621163206479-439876f9-7eae-4b69-bd30-a0003150bb56.png)

> 这里配置的**用户**系统中必须存在。

> PIDFile=/home/mars/.vnc/%H%i.pid 我看到网上还有的往配置文件加这句，但我没加也能启动

```xml
# 给系统添加vnc账号
useradd -c "vncserver登录账号" vnc

#切换到vnc账号
su vnc

# 设置vnc账号vncserver登录密码
vncpasswd

# 输入两次密码,然后会问你是否创建一个仅浏览的密码
[vnc@musui-notebook root]$ vncpasswd
Password:
Password must be at least 6 characters - try again
Password:
Verify:
Would you like to enter a view-only password (y/n)? y
Password:
Verify:

------------------
记录下密码
123456
view-only view123
------------------

#重启systemd
systemctl daemon-reload

#设置开机自启
systemctl enable vncserver@:1.service

#启动服务
systemctl start vncserver@:1.service
```

防火墙开放5900端口

```xml
firewall-cmd --zone=public --add-port=5900/tcp --permanent
```

<a name="y4tuj"></a>

#### 给root用户开放vncserver访问

```xml
cd /etc/systemd/system

#拷贝一份配置文件
cp vncserver@\:1.service vncserver@\:2.service

#编辑配置文件
vim vncserver@\:2.service

#设置vncserver登录密码,不设置服务启动不了
vncpasswd

------------------
记录下密码
123456
view-only view123
------------------

#重启systemd
systemctl daemon-reload

#设置开机自启
systemctl enable vncserver@:2.service

#启动服务
systemctl start vncserver@:2.service
```

![image.png](..\assets\xkwcr1\1621166615200-bddd6405-f4fb-48b8-99e5-5f5ce6ccddba.png) <a name="agASc"></a>

### 设置VNCServer连接分辨率

默认是1024x768。

```xml
vim /usr/bin/vncserver

# 找到 $geometry = "1024x768"; 
# 修改为1920x1080
$geometry = "1920x1080";

# 重启服务
systemctl restart vncserver@:1.service
systemctl restart vncserver@:2.service
```

<a name="Yb5un"></a>

### VNCServer相关命令

```xml
vncserver -kill :2   #停止刚才启动的vnc
vncserver -list      #查看启动了那些，必须切到启动用户的里面看用户自己的。
systemctl start vncserver@:2.service  启动vncserver@:2.service服务
systemctl status vncserver@:2.service		查看vncserver@:2.service服务状态
systemctl stop vncserver@:2.service  停止vncserver@:2.service服务
```

<a name="RVpXp"></a>

### VNCServer连接

<a name="dkfep"></a>

#### 使用vnc账号连接

![image.png](..\assets\xkwcr1\1621168497889-13704cb8-94d4-4c67-ae11-e6c56b688219.png) <a name="o3ATO"></a>

#### 使用root账号连接

![image.png](..\assets\xkwcr1\1621167081322-044982aa-a0b0-4373-9106-8daae3d7aa17.png)

<a name="hyPaY"></a>

# 软件安装

<a name="JkFnb"></a>

## 搜狗输入法

TODO

<a name="Z8qPh"></a>

## Chrome浏览器

```xml
vim /etc/yum.repos.d/google-chrome.repo
```

添加以下内容

```xml
[google-chrome]
name=google-chrome - \$basearch  
baseurl=http://dl.google.com/linux/chrome/rpm/stable/\$basearch  
enabled=1  
gpgcheck=0 
```

wq保存退出。
稳定版Chrome安装：

```xml
yum install google-chrome-stable
```

<a name="wpG8j"></a>

## QQ

QQ Linux版 <https://im.qq.com/linuxqq/download.html>
Linux QQ依赖gtk2.0。

```xml
yum install gtk2.x86_64
```

下载QQ

```xml
cd /usr/local

wget https://down.qq.com/qqweb/LinuxQQ/linuxqq_2.0.0-b2-1089_x86_64.rpm
```

安装

```xml
rpm -ivh linuxqq_2.0.0-b2-1089_x86_64.rpm
```

卸载

```xml
rpm -e linuxqq
```
