

# ESIX

## 账号密码
root tlh@123

##  ESXI 安装时无法识别部分NVME SSD 的处理
 [vSphere系列--关于 ESXi 安装时无法识别部分NVME SSD 的处理](https://www.cnblogs.com/suguangti/p/15769476.html)

ESXi 7移除了很多nvme驱动，所以有些nvme ssd在安装的时候无法识别，很是恼火！

解决思路：

给安装镜像添加并替换nvme驱动文件，因为原7.*版本的NVME_PCI.V00大小为31kb，而原来6.5版本的NVME_PCI.V00为78kb，很显然nvme驱动被删减了...

解决过程

1. 下载ESXi 6.5u2的iso文件，把里面的NVME.V00提取出来，把这个文件名改为：NVME_PCI.V00
2. 然后将NVME_PCI.V00替换到ESXi 7.*的ISO镜像文件中，然后在写入U盘启动镜像（当然已经写入的U盘启动可以去U盘直接替换这个文件）

附件分享：

这里我提供下我提取的完整驱动的NVME.V00(已命名为NVME_PCI.V00)，可以直接替换，下载附件 -> [NVME_PCI.zip](https://doc-pic.oss-cn-hangzhou.aliyuncs.com/vSphere/NVME_PCI.zip)

## 添加USB硬盘作为数据存储

通常在ESXi插上USB硬盘，都是映射给虚拟机使用，但是有些情况下我们需要提供给ESXi使用，直接扫描存储是无法扫描到USB硬盘的，需要做如下处理才能被识别。

1、开启主机SSH功能：主机-配置-服务，找到SSH服务，启用；

2、先不要插USB硬盘，输入以下命令：

```
/etc/init.d/usbarbitrator stop
chkconfig usbarbitrator off
```

3、插入USB硬盘，输入以下命令，可以看到有Is USB:true字样，表示USB设备已经识别出来。

```
esxcli storage core device list |grep -i usb
```

![image-20230304095901629](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230304095901661.png)

可以看到设备名为：mpx.vmhba34:C0:T0:L0 和 mpx.vmhba34:C0:T0:L1（注意，每款设备编号都会不同），这是我用绿联硬盘柜安装的两块机械，用usb连接esxi主机的。

4、输入以下命令，会列出所有的disk，确认一下是否正确识别该设备名。

```
ls /dev/disks/
```

![image-20230304100339426](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230304100339453.png)

5、陆续以下输入命令，注意替换（如果是 mpx.vmhba34:C0:T0:L0 则变成 mpx.vmhba34\:C0\:T0\:L0）

```
partedUtil mklabel /dev/disks/mpx.vmhba34\:C0\:T0\:L0 gpt 
partedUtil getptbl /dev/disks/mpx.vmhba34\:C0\:T0\:L0
```

![img](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230304100457858.png)

> 如果名字中有`:`号的话，前面加个\，也即是 “`:`” 变为“`\:`”

6、如上图，得到一串硬盘的数值，这里是4000731136，注意每个硬盘数值都不同。

输入以下命令换算硬盘的二进制数值，这里记得硬盘名有`:`也要加\：

```
eval expr $(partedUtil getptbl /dev/disks/mpx.vmhba34\:C0\:T0\:L0 | tail -1 | awk '{print $1 " \\* " $2 " \\* " $3}') - 1
```

![img](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230304100655023.png)

如上，会换算出另外一个数值：4000715144，运行如下命令：

```
partedUtil setptbl /dev/disks/mpx.vmhba34\:C0\:T0\:L0 gpt "1 2048 4000715144 AA31E02A400F11DB9590000C2911D1B8 0"
```

> 注意：（AA31E02A400F11DB9590000C2911D1B8）是VMFS数据存储分区的GUID，可以通过esxi来查看获取，后面的0是属性，不要更改。

8、挂载USB硬盘，注意后面有个:1

```
vmkfstools -C vmfs5 -S USB_Datastore /dev/disks/mpx.vmhba34\:C0\:T0\:L0:1
```

![img](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230304100832071.png)

> 说明：vmfs5是指用vmfs5格式，如果在其它esxi版本不支持vmfs5时，可以改为vmfs6，-S USB_Datastore是存储的名字，可自行定义。

9、这样USB_Datastore就已经创建好了，从ESXi数据存储那里就可以看到多了一个USB_Datastore了。

![img](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230304100859388.png)



## 硬盘直通

```
vmkfstools -r /vmfs/devices/disks/[硬盘ID] [SSD路径]/[硬盘名].vmdk

vmkfstools -r /vmfs/devices/disks/disks/mpx.vmhba34:C0:T0:L0 [SSD路径]/[硬盘名].vmdk
vmkfstools -r /vmfs/devices/disks/[硬盘ID] [SSD路径]/[硬盘名].vmdk
```

# ESXI备份虚拟机

先安装 [VMware-ovftool](https://pan.quark.cn/s/ca73fd74d5e8) ，进入安装目录下执行此备份命令：

192.168.3.2 是esxi ip地址，openwrt是虚拟机的名字，后面是指定备份文件路径

```

ovftool vi://root:@192.168.3.2/openwrt D:\我的服务器备份\esxi\openwrt.ova

ovftool vi://root:@192.168.3.2/OpenMediaVault D:\我的服务器备份\esxi\OpenMediaVault.ova
```

恢复虚拟机，在 esxi 新建虚拟机的时候选择 **`从OVF或OVA文件部署虚拟机`**

# OpenWRT

> https://openwrt.org/zh-cn/doc/uci/network

修改配置文件：/etc/config/network

下载地址：

## 账号密码
root root



# Windows Server 2019

**Windows Server 2019批量授权版KMS客户端安装密钥Volume:GVLK**

Windows Server 2019 Datacenter

[Key]：WMDGN-G9PQG-XVVXX-R3X43-63DFG

Windows Server 2019 Standard

[Key]：N69G4-B89J2-4G8F4-WWYCC-J464C

Windows Server 2019 Essential

[Key]：WVDHN-86M7X-466 P 6-VHXV7-YY726



## 账号密码
administrator tlh@123

## 关掉Ctrl+Alt+Delete 解锁屏幕功能

控制面板》管理工具》本地安全策略

本地安全策略》安全选项》交互式登录：无需按Ctrl+Alt+Del 双击或右键属性

启用此属性

# OpenMediaVault

[官网]: https://www.openmediavault.org/
[ESXI安装OpenMediaVault]: https://blog.51cto.com/bruceou/4853118



## 账号密码

ssh账号密码：root tlh@123

艹 ，刚安装时候，这个ssh登录账号不能用来web登录！web登录有默认的账号密码登录，我tm找问题找了一天，吐血！

web登录默认账号密码：admin openmediavault



## 配置静态IP

网络》接口》编辑

![image-20230304214358834](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230304214358906.png)

## 配置共享服务

### **开启FTP服务**

![image-20230304214543509](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230304214543563.png)



### **开启SMB/CIFS**

![image-20230304214642352](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230304214642416.png)



## 安装Docker



## ~~问题一~~

通过ssh能够登录omv，但通过web去访问，输入正确的用户名和密码后不能成功登录。

查看 /var/log/syslog 日志信息，`tail -fn 200 /var/log/syslog`

![image-20230304120204205](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230304120204233.png)

一操作登录就会刷这个日志  unknown or deprecated time zone [tz=]

```
more /etc/timezone
omv-confdbadm read conf.system.time
```

![image-20230304120757803](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230304120757829.png)

执行一下命令然后重启解决：

```
sudo hwclock --systohc
```

> https://forum.openmediavault.org/index.php?thread/34021-can-t-access-gui-openmediavault-engine-status-unknown-or-deprecated-time-zone/

重新设置时区

> https://linuxize.com/post/how-to-set-or-change-timezone-on-debian-10/

查看当前服务器时区

```
timedatectl
```

系统时区是通过将 /etc/localtime 符号链接到 /usr/share/zoneinfo 目录中的二进制时区标识符来配置的。您还可以使用 ls 命令检查符号链接指向的路径来查找时区：

```
ls -l /etc/localtime
```

修改Debian时区

查看时区列表

```
timedatectl list-timezones
```

```
# 设置时区
sudo timedatectl set-timezone Asia/Shanghai
