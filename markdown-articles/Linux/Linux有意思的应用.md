# cockpit

> https://github.com/cockpit-project/cockpit
>
> [How to install Cockpit on CentOS 7](https://www.techrepublic.com/article/how-to-install-cockpit-on-centos-7/)
>
> Cockpit is a web-based graphical interface for servers.

```markdown
# step 1
sudo yum install epel-release

# step 2
sudo yum install cockpit

# step 3
sudo systemctl start cockpit
sudo systemctl enable cockpit.socket

# step 4
sudo firewall-cmd --add-service=cockpit
sudo firewall-cmd --add-service=cockpit --permanent
sudo firewall-cmd --reload

# 访问
https://{SERVER_IP}:9090 
```

![image-20221222000437997](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202212220004070.png)



# CasaOS

> CasaOS - 一个简单、易用、优雅的开源家庭云系统。
>
> https://github.com/IceWhaleTech/CasaOS
>
> https://casaos.io/



```markdown
# 安装
wget -qO- https://get.casaos.io | sudo bash
或者
curl -fsSL https://get.casaos.io | sudo bash

# 卸载
# 1. v0.3.3版本或者更新新版本用此命令卸载
casaos-uninstall

# 2. v0.3.3以前的版本卸载命令
curl -fsSL https://get.icewhale.io/casaos-uninstall.sh | sudo bash


# 貌似不支持centos

```

