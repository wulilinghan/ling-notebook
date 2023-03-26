---
title: AIO：all in one
author: wulilh
date: 2023-03-04 10:11:00
tags:
  - esxi
  - pve
categories: all in one
link: 
---


# 参考硬件配置

主板：华擎B150M-ITX
CPU：奔腾G4560
机箱电源：恒星存储+台达
内存：光威天策8G*2
散热：ID-cooLING IS30

![image-20230305122001768](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305122009021.png)





# ESIX（x）

## 资源下载

》https://sysin.org/blog/vmware/



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

## USB硬盘直通

》参考：https://lx-soft.net/archives/242

首先，打开ESXi的控制台页面，在 管理》硬件 里面找到USB控制器，记录USB控制器名称以及设备ID和供应商ID，或者通过此命令查找usb控制器

```
lspci -v | grep "USB controller"
```

然后修改配置文件

```
vi /etc/vmware/passthru.map
```

在文件最后添加两行，第一行在#号+空格后面照抄你的USB控制器名称，第二行一共写四组代码，第一组是供应商ID，第二组是设备ID，第三组和第四组分别照抄d3d0和false，每组之间用tab制表符隔开。

```
# USB controller
8086	4ded	d3d0	false
```

保存文件并重启esxi服务器



## ESXI社区版驱动

### Community NVMe Driver for ESXi

https://flings.vmware.com/community-nvme-driver-for-esxi

### USB Network Native Driver for ESXi

https://flings.vmware.com/usb-network-native-driver-for-esxi



## ESXI 7.0升级8.0

**1、开启ESXI SSH**

**2、下载depot包上传至esxi存储**

我这里通过web页面上传到名称是 **datastore1**的存储位置，对应的实际路径是`/vmfs/volumes/63ff8460-614ec04a-35df-60beb4044c15`

**3、登录ssh并执行升级命令**

·查看当前版本

```
vmware -lv
```

![image-20230305133757603](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305133757634.png)

升级

```
esxcli software sources profile list -d /vmfs/volumes/63ff8460-614ec04a-35df-60beb4044c15/ios_esxi8.0/VMware-ESXi-8.0-20513097-depot.zip
```

![image-20230305133541458](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305133541493.png)

```
esxcli software profile update -d /vmfs/volumes/63ff8460-614ec04a-35df-60beb4044c15/ios_esxi8.0/VMware-ESXi-8.0-20513097-depot.zip -p ESXi-8.0.0-20513097-standard

出现截图所示警告，添加 --no-hardware-warning 继续执行
esxcli software profile update -d /vmfs/volumes/63ff8460-614ec04a-35df-60beb4044c15/ios_esxi8.0/VMware-ESXi-8.0-20513097-depot.zip -p ESXi-8.0.0-20513097-standard --no-hardware-warning
```

![image-20230305133635195](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305133635238.png)

还是异常：

```
[DependencyError]
 On platform embeddedEsx, VIB VMW_bootbank_vmkusb-nic-fling_1.8-3vmw.703.0.15.51233328 requires vmkapi_incompat_2_9_0_0, but the requirement cannot be satisfied within the ImageProfile.
 Please refer to the log file for more details.

```

![image-20230305134014840](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305134014878.png)

> 参考：https://www.virten.net/2021/11/vmware-esxi-7-0-update-3-on-intel-nuc/

1.将主机置于维护模式

2.移除 Fling。根据您的安装方式，以下命令之一将删除它

```
esxcli software vib remove -n vmkusb-nic-fling
或者
esxcli software component remove -n VMware-vmkusb-nic-fling
```

![image-20230305142621699](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305142621737.png)

> 后续所需包分享：
>
> 链接：https://pan.baidu.com/s/1VSkP54WR2rjAajg6jzP3yQ?pwd=97va 
> 提取码：97va 
> 复制这段内容后打开百度网盘手机App，操作更方便哦

3.安装ESXi更新

```
esxcli software vib install -d /vmfs/volumes/63ff8460-614ec04a-35df-60beb4044c15/ios_esxi7.0/VMware-ESXi-7.0U3-18644231-depot.zip
```
![image-20230305143426065](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305143426108.png)

4.安装 vmkusb-nic-fling

```
esxcli software component apply -d /vmfs/volumes/63ff8460-614ec04a-35df-60beb4044c15/ios_esxi7.0/ESXi703-VMKUSB-NIC-FLING-55634242-component-19849370.zip
```
![image-20230305143517107](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305143517146.png)

5.重启esxi

歇逼，重启后 ssd 又不识别了，原来的虚拟机都歇菜了![image-20230305145520776](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305145520806.png)



还好还有两个机械盘正常识别，安装下v1.1NVMe驱动试下

> 下载地址：https://flings.vmware.com/community-nvme-driver-for-esxi#requirements

```
esxcli software vib install -d /vmfs/nvme-community-driver_1.0.1.0-3vmw.700.1.0.15843807-component-18902434.zip
```

没用，还是重装老版本6.7 算了



许可证

```
esxi 8.0
4V492-44210-48830-931GK-2PRJ4
```



## ESXI备份虚拟机

**需要在虚拟机关机状态下进行备份**

先安装 [VMware-ovftool](https://pan.quark.cn/s/ca73fd74d5e8) ，**`D:\Backups\esxi\op，D:\Backups\esxi\omv`**等文件夹要先创建，进入安装目录下执行此备份命令：

192.168.3.2 是esxi ip地址，OpenWRT是虚拟机的名字，后面是指定备份文件路径

```

ovftool vi://root:@192.168.3.2/OpenWRT D:\Backups\esxi\op\OpenWRT.ova

ovftool vi://root:@192.168.3.2/OpenMediaVault D:\Backups\esxi\omv\OpenMediaVault.ova
```

![image-20230305032757778](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305032757849.png)

恢复虚拟机，在 esxi 新建虚拟机的时候选择 **`从OVF或OVA文件部署虚拟机`**

## OpenWRT

> https://openwrt.org/zh-cn/doc/uci/network

修改配置文件：/etc/config/network

下载地址：



## Windows Server 2019

**Windows Server 2019批量授权版KMS客户端安装密钥Volume:GVLK**

Windows Server 2019 Datacenter

[Key]：WMDGN-G9PQG-XVVXX-R3X43-63DFG

Windows Server 2019 Standard

[Key]：N69G4-B89J2-4G8F4-WWYCC-J464C

Windows Server 2019 Essential

[Key]：WVDHN-86M7X-466 P 6-VHXV7-YY726



### 关掉Ctrl+Alt+Delete 解锁屏幕功能

控制面板》管理工具》本地安全策略

本地安全策略》安全选项》交互式登录：无需按Ctrl+Alt+Del 双击或右键属性

启用此属性

## OpenMediaVault

[官网]: https://www.openmediavault.org/
[ESXI安装OpenMediaVault]: https://blog.51cto.com/bruceou/4853118



用esxi重装omv每次重新配置硬盘都要给硬盘新建文件系统（会进行格式化）？还是所有的nas系统都这样？



我这里安装的是6.0.24版本

`dpkg -l | grep openmediavault `：检查当前系统版本

### 账号密码



艹 ，刚安装时候，安装时配置的登录账号不能用来web登录！web登录有默认的账号密码登录，我tm找问题找了一天，吐血！

web登录默认账号密码：admin openmediavault



### 配置静态IP

网络》接口》编辑

![image-20230304214358834](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230304214358906.png)

### 配置共享服务

#### 开启FTP服务

![image-20230304214543509](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230304214543563.png)



#### 开启SMB/CIFS

![image-20230304214642352](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230304214642416.png)



### 安装Docker

> [how-to-install-docker-on-openmediavault](https://www.wundertech.net/how-to-install-docker-on-openmediavault/)

先要安装 OMV-Extras ，ssh登录openmediavault，发现我这omv连外网都没法访问

先配置下dns

```
vi /etc/resolv.conf
```

发现无法编辑，找到源文件，点击 resolv.conf 跳转到了 `/run/systemd/resolve/ `目录下，编辑 resolv.conf
文件尾部加上了以下内容

```
nameserver 114.114.114.114
```

发现这个dns的配置会自动重置，不知道咋回事，只能遇到dns问题是再修改一次了，后面安装portainer时就遇到报错，提示 dns异常，返回这里一看发现重置成默认的了，这里只能再改一次dns了



页面也可以配置dns，网络》接口 编辑网卡底部高级设置，设置dns服务器，我这里设置的114.114.114.114



#### **修改 Open Media Vault 镜像源**

》https://mirrors.tuna.tsinghua.edu.cn/help/openmediavault/

》https://tvtv.fun/pc-to-nas/16th.html

**sources.list**

```mar
# 备份配置文件
cp /etc/apt/sources.list /etc/apt/sources.list.bak
# 清空配置文件
sh -c 'echo > /etc/apt/sources.list'
# 编辑配置文件
nano /etc/apt/sources.list

复制并粘贴以下内容：
deb http://mirrors.tuna.tsinghua.edu.cn/debian/ buster main
# deb-src http://mirrors.tuna.tsinghua.edu.cn/debian/ buster main
deb http://mirrors.tuna.tsinghua.edu.cn/debian-security buster/updates main contrib non-free
# deb-src http://mirrors.tuna.tsinghua.edu.cn/debian-security buster/updates main contrib non-free
deb http://mirrors.tuna.tsinghua.edu.cn/debian/ buster-updates main contrib non-free
# deb-src http://mirrors.tuna.tsinghua.edu.cn/debian/ buster-updates main contrib non-free
```

**openmediavault.list**

```
# 查看源
cat /etc/apt/sources.list.d/openmediavault.list

# 备份配置文件
cp /etc/apt/sources.list.d/openmediavault.list /etc/apt/sources.list.d/openmediavault.list.bak
# 清空配置文件
sh -c 'echo > /etc/apt/sources.list.d/openmediavault.list'
# 编辑配置文件
nano /etc/apt/sources.list.d/openmediavault.list
复制并粘贴以下内容：

deb https://mirrors.tuna.tsinghua.edu.cn/OpenMediaVault/public shaitan main
deb https://mirrors.tuna.tsinghua.edu.cn/OpenMediaVault/packages shaitan main
## Uncomment the following line to add software from the proposed repository.
# deb https://mirrors.tuna.tsinghua.edu.cn/OpenMediaVault/public shaitan-proposed main
# deb https://mirrors.tuna.tsinghua.edu.cn/OpenMediaVault/packages shaitan-proposed main
## This software is not part of OpenMediaVault, but is offered by third-party
## developers as a service to OpenMediaVault users.
# deb https://mirrors.tuna.tsinghua.edu.cn/OpenMediaVault/public shaitan partner
# deb https://mirrors.tuna.tsinghua.edu.cn/OpenMediaVault/packages shaitan partner

```

**omvextras.list**

```
# 备份配置文件
cp /etc/apt/sources.list.d/omvextras.list /etc/apt/sources.list.d/omvextras.list.bak
# 清空配置文件
sh -c 'echo > /etc/apt/sources.list.d/omvextras.list'
# 编辑配置文件
nano /etc/apt/sources.list.d/omvextras.list

复制并粘贴以下内容：
deb https://mirrors.tuna.tsinghua.edu.cn/OpenMediaVault/openmediavault-plugin-developers usul main
deb [arch=amd64] https://mirrors.tuna.tsinghua.edu.cn/docker-ce/linux/debian buster stable
deb http://linux.teamviewer.com/deb stable main
```



#### 安装 OMV-Extras

```markdown
# 先装这个 后面安装portainer会用到
apt install apparmor apparmor-utils auditd 

# 安装依赖
apt --yes --no-install-recommends install dirmngr gnupg

# 安装omv-extras-plugins，建议给omv网关配成openwrt的梯子，不然有点折磨
wget -O - https://github.com/OpenMediaVault-Plugin-Developers/packages/raw/master/install | bash

或者手动下载包，上传至omv服务器上，安装
https://mirrors.tuna.tsinghua.edu.cn/OpenMediaVault/openmediavault-plugin-developers/pool/main/o/openmediavault-omvextrasorg/

dpkg -i openmediavault-omvextrasorg_6.1.1_all.deb
```

返回页面刷新，系统》插件 下方会出现 omv-extras 的按钮，选择 docker 和 portainer 安装

portainer：admin tlh@12345678

Yacht 默认账号密码：admin@yacht.local pass

安装portainer时报错（这个问题在我重装vmv后没事了，不知是不是我第二次安装先安装的那几个依赖发挥作用了，第一次装的时候遇到各钟版本依赖问题，吧omv都搞崩了）

![image-20230305020744343](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305020744402.png)

https://forum.openmediavault.org/index.php?thread/40928-something-went-wrong-trying-to-pull-and-start-portainer-on-omv-6/

根据以上链接，需要安装一些依赖

```
apt install apparmor apparmor-utils auditd
```

然后又报错

![image-20230305021050012](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305021050053.png)



```
查看依赖关系
dpkg --info apache2_2.4.7-1ubuntu4.14_amd64.deb | grep Depends
```

安装aptitude工具。使用 aptitude 安装 debian依赖

```
apt install aptitude

aptitude install apparmor apparmor-utils auditd

apt show -a python3
apt remove python3.9

歇逼 把python3.9卸载了 系统崩了
```



### ~~问题一~~

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

```

# Proxmox VE（√）

参考：

https://tvtv.fun/pc-to-nas/



使用 ventoy 制作U盘启动，启动软件选择要制作的U盘即可。

访问页面：https://192.168.3.3:8006

![image-20230305170753373](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305170753469.png)



~~ssh登录：~~ 

```
lvremove pve/data 

lvextend -l +100%FREE -r pve/root  
```

![image-20230305170924703](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305170924742.png)



## Pvetools

上传 pvetools.sh

> **[PveTools](https://github.com/ivanhao/pvetools)** 这是一个为 proxmox ve 写的工具脚本（理论上debian9+可以用）。包括配置邮件，samba，NFS，zfs，嵌套虚拟化，docker，硬盘直通等功能。

```
# 下载
wget https://gitee.com/pandapenguin/pvetools/raw/master/pvetools.sh

# 授予执行权限
chmod +x pvetools.sh

# 执行当前目录此脚本
./pvetools.sh
```



![image-20230305171412917](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305171412991.png)



![image-20230305225635575](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305225635698.png)

## PVE软件源

点击「数据中心-pve」，中间菜单有「更新-存储库」。首先在列表里选中 `https://enterprise.promox.com/debian/pve`，然后点击上方的「禁用」，这样就禁用了需要付费订阅源。再点击旁边的「添加」，在列表里面选「`Non-Subscription`」就好了。这样，我们既可以收到 PVE 的安全更新，又不用付费。

然后呢，点击「数据中心 -> pve」，中间菜单有「Shell」，放心大胆地 `apt update` 和 `apt upgrade -y` 吧～

## Qemu Guest Agent

对于任何一个虚拟化平台，总有一个客户机软件和宿主机通信，例如vmware的vmtools。KVM下的就是qemu-guest-agent

因为PVE是基于KVM的，所以客户端依旧是 qemu-guest-agent 。

#### Linux

```bash
#for rehat
yum  install qemu-guest-agent -y
#for debian
apt install qemu-guest-agent -y
```

qm agent
```bash
 qm agent <vmid> <cmd>
 
 fsfreeze-freeze 
 fsfreeze-status
 fsfreeze-thaw
 fstrim                      #查看ssd——trim
 get-fsinfo                  #查看磁盘信息
 get-host-name               #查看主机名
 get-memory-block-info       #查看内存块 信息
 get-memory-blocks           #查看您内存
 get-osinfo                  #查看系统信息
 get-time                    #查看时间
 get-timezone                #查看时区
 get-users                   #用户
 get-vcpus                   #查看CPU数量
 info                        #查看支持的命令
 network-get-interfaces      #查看网络
 ping                        #不明    
 shutdown                    #关机
 suspend-disk                #休眠，储存到硬盘
 suspend-hybrid              #休眠，混合
 suspend-ram                 #挂起/休眠 内存
 
#查看vm的网卡信息
qm agent 101 network-get-interfaces
```

## CT

点击「数据中心-pve-local」，右边有「CT 模板」（在 PVE 里，「CT」好像是「Container/容器」的简称）。点击「模板」，里面列出了一大堆模板，我觉得叫「基础 OS」是不是更合适啊……找一个顺眼一点的（例如 `debian-11-standard`），点击「下载」就完事儿了。

还是由于你懂得的网络问题，在国内下载这些模板似乎并不顺利，几分钟才下载 2KB 的数据。解决办法么……你看，下载 Log 窗口里第一句话写的是啥？是不是「将 XXX 下载到 XXX」？哎这就好办了。我们将这个东西复制出来，将下载停掉，回到节点 Shell 里面，设置好代理，然后进入到目标文件夹（`/var/lib/vz/template/cache/`），直接用 wget 下载就好了。当然～如果你有旁路由网关，也可以在「系统-网络」那里设置一下网关，走自动代理。好像记得还有办法手动设置走代理来着，写这个文章的时候突然找不到了。

然后就是「新建 CT」。步骤其实和新建虚拟机是一模一样的。我尝试后发现，需要**不勾选**「无特权容器」，新建完成后，在「选项」里面开启「嵌套」就能正常跑 Docker 了；如果选上「无特权容器」，那么后续不管是在 feature 里开启「嵌套（nesting）」还是开启「按键（keyctl）」，Docker 都报错。

进入系统后，你会得到一个最小化安装的纯净版 Linux，我们需要自己安 Docker。先 `apt update` 然后再 `apt upgrade -y` ，最后再用 `apt install docker` 安装 docker，或者[按照 Docker 的官方教程](https://docs.docker.com/engine/install/#server)来走。由于我们自己就是 root，所以无需再把自己添加到 docker 组。如果发现 `apt update` 疯狂报错，先别急着换源和挂代理，先去这个 LCX 虚拟机的「网络」设置看看，eth0 那地方的 IP 和网关填了没，没填的话补上，或者选 DHCP 让自动分配也行。

对于我自己来说，老套路，先安个 `docker-compose`，再起个 portainer。这下爱咋折腾咋折腾吧，应该「我们都一样～」了。



## 虚拟机备份

以我这个openwrt为例：

![image-20230305195304918](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305195305019.png)

备份结果

![image-20230305195348620](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305195348668.png)

这是备份文件路径 `/var/lib/vz/dump/vzdump-qemu-100-2023_03_05-19_53_28.vma.zst`

## 恢复虚拟机

- 登录pve节点
- 切换至相应的上传存储（local的备份默认路径为：/var/lib/vz/dump）



## 硬件直通

### 硬盘映射（×）

磁盘映射性能损失大，不推荐。

[为什么PVE硬盘直通性能损失这么大]: https://www.right.com.cn/forum/thread-8248323-1-1.html



PVE 硬盘映射的步骤如下：

1. 进入 PVE 的 SSH 或者网页 Shell，查看你现在的存储设备的序列号：
```
ls /dev/disk/by-id
```

红框的是硬盘ID，复制下来

![image-20230306214100151](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230306214107252.png)

2. 运行直通命令：

````
qm set <vmid> -sata<X> /dev/disk/by-id/<disk-id>
````

> `<vmid>` 是你想要映射给的虚拟机ID 号，
>
> `-sata<X>` 是你想要分配给虚拟机的 SATA 接口编号，自定义的（从 0 开始）
>
> > 你也可以使用 ide 或者 scsi 形式映射硬盘，需要注意的是，scsi会有序号，如scsi1，scsi0。在操作之前，应该要知道哪些scsi号是空的。
> >
> > 对于pve来说，sata最多有6个设备。如果要使用sata类型直通，请勿超过sata5
>
> `<disk-id>` 是你想要映射的硬盘ID。

例如：

```
qm set 101 -sata0 /dev/disk/by-id/ata-WDC_XXXX_XXXX_XXXX
```

如果返回以下信息,说明已成功映射

> update VM 101: -sata0 /dev/disk/by-id/ata-WDC_XXXX_XXXX_XXXX


3. 重启虚拟机，就可以在虚拟机中看到并使用你映射的硬盘了。

4. 取消映射

```
qm set <vmid> --delete scsi<X>
```



### 硬盘直通 （√）

[Proxmox VE pve硬盘直通]: https://foxi.buduanwang.vip/virtualization/1754.html/
[PVE开启硬件直通功能]: https://foxi.buduanwang.vip/virtualization/pve/561.html/


1.查询控制器

```
lspci
```

![image-20230306220048697](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230306220048743.png)

其中 00:14 是USB 控制器，00:17 是 sata 控制器，02:00 是 scsi 直通卡。一般来说，带USB，SCSI，SATA字样的都是磁盘控制器。

2.查看硬盘所属硬盘控制器

如果你有多块硬盘，且不知道硬盘属于哪个控制器，你可以通过下面命令查看。

```
ls -la /sys/dev/block/|grep -v loop |grep -v dm
```

![image-20230306220455333](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230306220455374.png)

如上面所示，`pci0000:00`是Pci桥，`0000:00:14.0`就是对应的控制器，后面是硬盘的盘号




以上内容网上搜的，因为我是买了个绿联硬盘柜，用USB连接主机的，这里可以直接通过界面操作直通

![image-20230306221426017](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230306221426086.png)



## 问题

### 1.can’t lock file ‘/var/lock/qemu-server/lock-xxx.conf’ – got timeout

我用 CT模板 centos-7-default 创建了个ct启动，然后进入shell发现无法做任何操作，不能登录，没法输命令，就想删除它，然后就提示此内容

![image-20230305232307677](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305232307711.png)

1.先在shell里输入 `rm /var/lock/qemu-server/lock-102.conf  `

> 102是你的虚拟机编号

2.再输入`qm stop 102 `就可以立即关闭你的102号虚拟机了



以上内容网上搜的，但是没用，我是重启pve后删除的

### 2.VM quit/powerdown failed - got timeout

给虚拟机扩充了内存，想重启，操作通过pve重启功能后提示标题内容，

需要安装acpid，**解决方法如下**：
centos系：
`yum -y install acpid && /etc/init.d/acpid start`

debian系：
`apt-get install acpid`

## 扩充虚拟机磁盘（x）

我这里以openmediavault为例，初始是10G现在我给他分到50G

![image-20230311141645325](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230311141645370.png)

在页面上操作完，ssh登录到omv，执行以下命令，看到`/dev/sda`总大小是50G了，但是下面分区小没变

```
fdisk -l
```

![image-20230311141805779](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230311141805836.png)

执行以下命令

```
parted /dev/sda
```

执行后，会出现(parted)没有往后继续运行，这是要自己再后面输入命令，这里先输入下 `print` 打印分区信息

![image-20230311142723948](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230311142723989.png)

我这里会启动盘增加空间，接着输入以下命令

```
resizepart 1 100%
```

![image-20230311143241898](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230311143241934.png)

然后又找到可以直接使用`resize2fs `命令，但还是报错

The filesystem is already 2371072 (4k) blocks long.  Nothing to do!

![image-20230311145816946](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230311145816981.png)

>  df -Th 查看磁盘类型
>
> lsblk
>
> fdisk -l
>
> lvm格式：lvresize -l +100%FREE /dev/mapper/centos-root   # 直接把所有剩余空间都分配给centos-root这个LV
>
> xfs文件格式：xfs_growfs /dev/mapper/centos-root  
>
> ext4文件格式：resize2fs /dev/mapper/centos-root



`resizepart 2 100% `我把剩余的空间扩充到type为extends的盘上，又成功了，重新用parted命令进去打印分区，能看到 number 为2 的分区size为40多G左右，但是lsblk这些命令又看不到，不知道这个新加40G空间去哪了，心态小蹦，我接受不了，备份下omv上的容器数据，重装omv得了



## 应用

###  OpenWRT

img2vm，将上传镜像文件转换一下

```
qm importdisk 100 /var/lib/vz/template/iso/openwrt-03.04.2023-x86-64-generic-squashfs-combined.img local-lvm
```

![image-20230305175129456](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305175129498.png)



回到页面继续操作

![image-20230305175520654](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305175520720.png)

然后 选项》引导顺序 ，勾上并挪到第一顺序

![image-20230305175642700](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230305175642758.png)

启动。

修改配置文件：/etc/config/network

### OpenMediaVault



查看版本信息

 hostnamectl

 uname -a

```
root@openmediavault:/# hostnamectl
   Static hostname: openmediavault
         Icon name: computer-vm
           Chassis: vm
        Machine ID: 2ec90954e6074540b4df3c57cb28cac6
           Boot ID: c34dbb40654b4ae89f85ad620dc1f3af
    Virtualization: kvm
  Operating System: Debian GNU/Linux 11 (bullseye)
            Kernel: Linux 5.16.0-0.bpo.4-amd64
      Architecture: x86-64
```





#### ovm配置

##### 安装 OMV-Extras（docker）

```markdown
# 先装这个 后面安装portainer会用到
apt install apparmor apparmor-utils auditd -y

# 安装依赖
apt -y --no-install-recommends install dirmngr gnupg

# 一键脚本安装omv-extras-plugins，建议给omv网关配成openwrt的梯子，不然有点折磨
wget -O - https://github.com/OpenMediaVault-Plugin-Developers/packages/raw/master/install | bash

# 或者 手动下载包，上传至omv服务器上，手动安装
下载路径：
https://mirrors.tuna.tsinghua.edu.cn/OpenMediaVault/openmediavault-plugin-developers/pool/main/o/openmediavault-omvextrasorg/
安装命令
dpkg -i openmediavault-omvextrasorg_6.1.1_all.deb
```

返回页面刷新，系统》插件 下方会出现 omv-extras 的按钮，选择 docker 和 portainer 安装

Yacht 默认账号密码：admin@yacht.local pass

##### 配置ipv6网络

```
sudo omv-firstaid
```

选择网卡，重新配置下ipv4和ipv6网络

要注意ipv4也要设置，我本来ipv4是能正常访问的，因为这一步的时候，没有操作ipv4，结束之后ipv4访问被关了《不过还好可以通过pve进去，不然要重装了

重新配置后，会重新分配和ipv4地址，要重新设置下

##### [域名解析](https://cloud.tencent.com/product/cns?from=10680)

- 解析 AAAA 记录，值为 IPv6 地址，之后可以使用解析的[域名](https://cloud.tencent.com/act/pro/domain-sales?from=10680)访问自己的 OMV管理系统

![img](https://ask.qcloudimg.com/http-save/yehe-8585088/3bf3439ee3f1a023a50ac99a33698e43.png?imageView2/2/w/2560/h/7000)

##### ~~安装图形化~~

> 装完后发现 omv访问不了了 403 ，然后卸载桌面，又歇菜了，没备份，又得重新安装omv了

```
sudo apt -y install task-gnome-desktop
```

1. 完成 GNOME 桌面安装后，下一步是分配`graphical runlevel`.

通过执行以下命令将默认引导更改为图形运行级别，以确保下一次系统引导进入 GUI。

```
sudo systemctl set-default graphical.target
```

![image-20230312160158573](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230312160158620.png)

2. 默认情况下，通过 GNOME 显示管理器 (GDM) 禁用 root 用户登录。要启用 root 访问，请按照以下步骤操作：

通过 SSH 登录到服务器并使用任何选择的编辑器编辑 GDM 配置文件 **/etc/pam.d/gdm-password** 。

```
vi /etc/pam.d/gdm-password
```

找到如下所示的行：

```
auth  required  pam_succeed_if.so user != root quiet_success
```

通过添加前缀删除/注释掉这一行`#`，然后保存并关闭编辑器。

3. 最后重启下 `reboot`

#### Jellyfin

> 官方安装文档：https://jellyfin.org/docs/general/installation/container
>
> [Jellyfin中国特供版+Docker镜像，含驱动，免折腾开箱即用](https://www.bilibili.com/read/cv14514123?msource=smzdm_937_0_184__f491cddbd1618c25)
>
> **Jellyfin中国特供版+Docker镜像**：[nyanmisaka/jellyfin - Docker Image | Docker Hub](https://hub.docker.com/r/nyanmisaka/jellyfin)

##### 安装Jellyfin

开源的多媒体管理软件，用来管理电影非常方便，并且有全平台的客户端支持，我的博客已经多次介绍，可以点右上角搜索试试

```
version: '2.1'
services:
  doubanos:
    image: caryyu/douban-openapi-server:latest
    container_name: doubanos
    network_mode: bridge
    restart: unless-stopped
    
  jellyfin:
    image: linuxserver/jellyfin
    container_name: jellyfin
    privileged: true
    environment:
      - PUID=0
      - PGID=0
      - TZ=Asia/Shanghai  
    volumes:
      - /AppData/jellyfin/config:/config
      - /srv/dev-disk-by-uuid-1ab6cfe6-3277-4e8a-8dd2-d81e8362c4c0/homevedio:/homevedio
    devices:
      - /dev/dri:/dev/dri
    ports:
      - 8096:8096
    restart: unless-stopped
    network_mode: bridge
    hostname: jellyfin
```

`/srv/dev-disk-by-uuid-1ab6cfe6-3277-4e8a-8dd2-d81e8362c4c0/h1` 这个目录要在omv **`存储器》共享文件夹 `**共享出来。

volumes 是主机目录映射至docker容器中的路径，<主机目录>:<容器目录>

dev-disk-by-uuid-1ab6cfe6-3277-4e8a-8dd2-d81e8362c4c0 是磁盘，使用`df -h`查看，建议后面的文件夹路径跟共享文件夹路径一致，方便在Windows上查看文件

![image-20230307232900444](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230307232900503.png)



##### 硬件解码

不开硬解时播放个4K视频，CPU拉满。

```
ls /dev/dri/
```

在omv虚拟机汇总执行命令发现没有**renderD128**驱动文件，但是在宿主机pve上有

![image-20230308204239060](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230308204246154.png)

这是宿主机pve 执行命令结果：

![image-20230326134452597](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230326134459698.png)

> `/dev/dri/card0` 和 `/dev/dri/renderD128` 都是 Linux 系统中与图形显示相关的设备文件。



~~参考： **[【PVE7.1-8】LXC容器下Jellyfin服务器配置显卡硬件加速](https://www.bilibili.com/read/cv14489336)**~~



先在宿主机PVE安装inter核显驱动

```
apt install intel-media-va-driver-non-free

# 安装vainfo程序
apt install vainfo
```

安装完成后，查询显卡信息，显示如下，说明驱动安装完成。

```
vainfo
```

![image-20230326141317904](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230326141317963.png)

```
# 查看所有pci设备
lspci

# 查看核显设备
lspci -knn | grep -i -A 2 vga
```
红框圈起来的就是我的核显设备
![image-20230326151922551](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230326151922630.png)
在pve虚拟机 硬件》添加 ，添加pci设备

![image-20230326152048768](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230326152048824.png)


控制台》播放》硬件加速，选择 **Video Acceleration API(VAAPI)**，在启用硬件解码下方勾选所有媒体文件格式，然后保存

![image-20230308204725143](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230308204725224.png)



#### kodi

> [linuxserver/kodi-headless - Docker Image | Docker Hub](https://hub.docker.com/r/linuxserver/kodi-headless)

媒体播放器
```
docker run -d --restart always --name=kodi-headless \
-e PGID=0 -e PUID=0 \
-e TZ=Asia/Shanghai \
-p 8095:8080 \
-p 9090:9090 \
-p 9777:9777/udp \
-v /data/docker_volume/kodi:/config/.kodi \
linuxserver/kodi-headless
```



默认账号密码：kodi / kodi



#### Emby

> [zishuo/embyserver - Docker Image | Docker Hub](https://hub.docker.com/r/zishuo/embyserver/)
>
> [lovechen/embyserver - Docker Image | Docker Hub](https://hub.docker.com/r/lovechen/embyserver)

Emby Media Server 学习版
```
docker run -d --restart always --name emby --privileged=true \
-p '8097:8096' \
-p '8920:8920' \
-p '1900:1900/udp' \
-p '7359:7359/udp' \
-v /AppData/emby/config:/config \
-v /srv/dev-disk-by-uuid-1ab6cfe6-3277-4e8a-8dd2-d81e8362c4c0/homevedio:/data \
-e TZ="Asia/Shanghai" \
--device /dev/dri:/dev/dri \
-e UID=0 \
-e GID=0 \
-e GIDLIST=0 \
--network=bridge \
lovechen/embyserver
```



#### ddns-go

> Dockerhub：https://hub.docker.com/r/jeessy/ddns-go

动态DNS解析，支持WEB界面设置



#### transmission

> https://hub.docker.com/r/linuxserver/transmission

```yaml
version: "2.1"
services:
  transmission:
    image: lscr.io/linuxserver/transmission:latest
    container_name: transmission
    environment:
      - PUID=0
      - PGID=0    
      - TZ=Asia/Shanghai
    volumes:
      - /AppData/transmission/data:/config
      - /AppData/transmission/watch:/watch
      - /srv/dev-disk-by-uuid-1ab6cfe6-3277-4e8a-8dd2-d81e8362c4c0/h1/downloads:/downloads
    ports:
      - 9091:9091
      - 51413:51413
      - 51413:51413/udp
    restart: unless-stopped
    network_mode: bridge
```

#### QBittorrent

> https://hub.docker.com/r/linuxserver/qbittorrent

初始用户名 **admin** ，密码 adminadmin

```yaml
version: "2.1"
services:
  qbittorrent:
    image: lscr.io/linuxserver/qbittorrent:latest
    container_name: qbittorrent
    environment:
      - PUID=0
      - PGID=0
      - TZ=Asia/Shanghai
    volumes:
      - /AppData/qbittorrent/config:/config
      - /srv/dev-disk-by-uuid-1ab6cfe6-3277-4e8a-8dd2-d81e8362c4c0/homevedio:/homevedio
    ports:
      - 8080:8080
      - 6881:6881
      - 6881:6881/udp
    restart: unless-stopped
    network_mode: bridge
```

##### 重置密码

配置文件qBittorrent.conf，删除`WebUI\Password_PBKDF2`这一行，然后重启QB，即可重置密码为默认密码。



#### QBittorrent-Enhanced-Edition

> https://hub.docker.com/r/superng6/qbittorrentee

QBittorrent增强版

```
version: "2"
services:
  qbittorrentee:
    image: superng6/qbittorrentee
    container_name: qbittorrentee
    environment:
      - PUID=0
      - PGID=0
      - TZ=Asia/Shanghai
    volumes:
      - /AppData/qbittorrentee/config:/config
      - /srv/dev-disk-by-uuid-1ab6cfe6-3277-4e8a-8dd2-d81e8362c4c0/homevedio:/homevedio
    ports:
      - 6881:6881
      - 6881:6881/udp
      - 8080:8080
    restart: unless-stopped
    network_mode: bridge
```

#### tracker list

https://github.com/ngosang/trackerslist

#### Aria2 Pro & Alist

Aria2 Pro 的官方部署文档: https://github.com/P3TERX/Aria2-Pro-Docker/blob/master/docker-compose.yml

  - RPC_SECRET=ARIA2_RPC_SECRET 

    > 配置 Aria2 的 RPC secret 密钥，它将被用于 Alist 和 AriaNg 连接 Aria2

```yaml
version: "3.8"
services:
  Aria2-Pro:
    container_name: aria2-pro
    image: p3terx/aria2-pro
    environment:
      - PUID=0
      - PGID=0
      - UMASK_SET=022
      - RPC_SECRET=ARIA2_RPC_SECRET
      - RPC_PORT=6800
      - LISTEN_PORT=6888
      - DISK_CACHE=64M
      - IPV6_MODE=false
      - UPDATE_TRACKERS=true
      - CUSTOM_TRACKER_URL=
      - TZ=Asia/Shanghai
    volumes:
      - /AppData/aria2/config:/config
      - /srv/dev-disk-by-uuid-1ab6cfe6-3277-4e8a-8dd2-d81e8362c4c0/h1/downloads:/downloads
    ports:
      - 6800:6800
      - 6888:6888
      - 6888:6888/udp
    restart: unless-stopped
    network_mode: host
    logging:
      driver: json-file
      options:
        max-size: 1m
```
Aria2 的 Web UI
```yaml
version: "3.8"
services:
  AriaNg:
    container_name: ariang
    image: p3terx/ariang
    command: --port 6880 --ipv6
    ports:
      - 6880:6880
    restart: unless-stopped
    network_mode: host
    logging:
      driver: json-file
      options:
        max-size: 1m
```
Alist 的官方部署文档

[使用 Docker | AList文档 (nn.ci)](https://alist.nn.ci/zh/guide/install/docker.html)

```yaml
version: "3.8"
services:     
  alist:
    image: xhofe/alist
    container_name: alist
    ports:
      - 5244:5244
    restart: always
    volumes:
      - /srv/dev-disk-by-uuid-1ab6cfe6-3277-4e8a-8dd2-d81e8362c4c0/h1/alist:/opt/alist/data
```

| APP          | URL                      |
| :----------- | :----------------------- |
| Alist        | http://192.168.3.49:5244 |
| Aria2-RPO    | http://192.168.3.49:6800 |
| AriaNg-WebUI | http://192.168.3.49:6880 |

##### AriaNg

通过 AriaNg 查看和管理 Aria2 的下载任务，AriaNg 是 Aria2 的 Web UI，需要配置 Aria2 的 RPC 地址和 RPC 密钥，才能正常使用。

- 从浏览器中访问`http://192.168.3.49:6880/#!/settings/ariang`。
- 在`Aria2 RPC 密钥`中填写你的`RPC_SECRET`，即`docker-compose.yml`文件中`Aria2-Pro`服务的`environment`-`RPC_SECRET`，我这里设置的`ARIA2_RPC_SECRET`。
- 点击弹出的`重载`按钮便能完成配置。



##### Alist配置

- 在配置 Alist 之前需要，需要先取到 Alist 的`初始管理密码`，可以通过以下命令获取：

```bash
docker exec -it alist ./alist admin
```

- 取到密码后，打开浏览器，访问`http://192.168.3.49:5244`，找到并进入管理入口并输入`初始管理密码`，即可进入 Alist 的管理界面。

连接 Aria2 Pro

- 请在`Alist`的设置》其他》 Aria2 设置。
- `Aria2 url`中填入`Aria2 RPC`的地址，即`http://192.168.3.49:6800/jsonrpc`
- `RPC secret`，即`docker-compose.yml`文件中`Aria2-Pro`服务的`environment`-`RPC_SECRET`我这里设置 的`ARIA2_RPC_SECRET`。

挂载网盘

- 在`Alist`的设置界面中找到[添加账号](https://alist-doc.nn.ci/docs/driver/base)，以挂载网盘。



#### nas-tools

> 官方版本：[jxxghp/nas-tools - Docker Image | Docker Hub](https://hub.docker.com/r/jxxghp/nas-tools)
>
> 2.9.1版本：[19970688/nastools-bt - Docker Image | Docker Hub](https://hub.docker.com/r/19970688/nastools-bt)
>
> 我也备份了下2.9.1版本：[vling/nas-tools - Docker Image | Docker Hub](https://hub.docker.com/r/vling/nas-tools)

官方新版本有些内容阉割了，这里用的历史版本。

##### 安装nastools

3.x.x

```
version: "2.1"
services:
  nas-tools:
    image: jxxghp/nas-tools
    container_name: nas-tools
    environment:
      - PUID=0
      - PGID=0
      - TZ=Asia/Shanghai
      - NASTOOL_AUTO_UPDATE=false  
      - NASTOOL_CN_UPDATE=false
    volumes:
      - /AppData/nastools/config:/config
      - /srv/dev-disk-by-uuid-1ab6cfe6-3277-4e8a-8dd2-d81e8362c4c0/homevedio:/homevedio
    ports:
      - 3000:3000
    restart: unless-stopped
    network_mode: bridge
    hostname: nas-tools
```



###### 2.9.1

```yaml
version: "2.1"
services:
  nas-tools:
    image: vling/nas-tools:2.9.1
    container_name: nas-tools291
    environment:
      - PUID=0
      - PGID=0
      - TZ=Asia/Shanghai
      - NASTOOL_AUTO_UPDATE=false  
      - NASTOOL_CN_UPDATE=false
    volumes:
      - /AppData/nastools2.9.1/config:/config
      - /srv/dev-disk-by-uuid-1ab6cfe6-3277-4e8a-8dd2-d81e8362c4c0/homevedio:/homevedio
    ports:
      - 2999:3000
    restart: unless-stopped
    network_mode: bridge
    hostname: nas-tools291
```



默认账号密码：admin password

宿主机目录

```
/srv/dev-disk-by-uuid-1ab6cfe6-3277-4e8a-8dd2-d81e8362c4c0/media
```

展示目录树

```
# 安装tree命令
sudo apt-get install tree

tree /srv/dev-disk-by-uuid-1ab6cfe6-3277-4e8a-8dd2-d81e8362c4c0/homevedio -d
```

![image-20230312174333596](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230312174333659.png)



这是宿主机目录，homevedio文件夹就有media，pt等目录，pt是qb等下载器的下载目录，media目录在就是jellyfin要读取的目录。



##### 添加媒体库

![image-20230312174531093](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230312174531180.png)

##### 目录同步

![image-20230312174557269](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230312174557353.png)

##### 下载库

我这里用的 Qbittorrent 填上 ip端口，登录账号即可，我这里 Qbittorrent 前面就已经安装了

下载目录设置

![image-20230312174632908](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230312174632966.png)

##### 索引器

###### jackett

> Dockerhub：https://hub.docker.com/r/linuxserver/jackett



```yaml
version: "2.1"
services:
  jackett:
    image: lscr.io/linuxserver/jackett:latest
    container_name: jackett
    environment:
      - PUID=0
      - PGID=0
      - TZ=Asia/Shanghai
    volumes:
      - /AppData/jackett/config:/config
    ports:
      - 9117:9117
    restart: unless-stopped
    network_mode: bridge
```

很多公开站点被墙，翻不出去的其实还有其它地址没有被墙的，例如

海盗湾另外的网址：https://tpb.skynetcloud.site/，

X1337站点，网址：https://x1337x.ws/

如果不能自行编辑，希望将这些网址加入到相关公开站点中。我们这些墙内出墙很麻烦，用了很多的机场关的很快，稳定的机场又价格太高。



# SMB服务

https://www.samba.org/samba/docs/current/man-html/smb.conf.5.html



## 配置文件

```
cat /etc/samba/smb.conf
```



```
systemctl status smbd 查看服务状态

systemctl start smbd 开启服务

systemctl restart smbd 重启服务

systemctl stop smbd 停止
```

### client max protocol

```
Default: server max protocol = SMB3

Example: server max protocol = LANMAN1
```

### client min protocol
```
Default: server min protocol = SMB2_02

Example: server min protocol = NT1
```



## 查看服务端SMB协议版本

OPENMEDIAVAULT 是我的nas服务主机名称，不带上 `-ServerName OPENMEDIAVAULT`也行

```
Get-SmbConnection -ServerName OPENMEDIAVAULT
```

Dialect版本就是SMB版本，我这里是3.1.1

![image-20230319111402776](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230319111402823.png)



# 测试硬盘读写速度

利用win10自带的硬盘测试工具测读写速度。

`win+s`打开搜索框，输入 cmd 找到命令提示符，右击以管理员身份运行。

输入以下命令测试

```
winsat disk -drive <盘符>
```



![image-20230319105651660](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230319105651726.png)
