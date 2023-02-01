

操作系统: CentOS Linux release 7.6.1810 (Core)

> 查看操作系统版本: cat /etc/redhat-release

# 一、安装Gitlab

1. 安装环境

yum install -y curl policycoreutils-python openssh-server

2. 安装Gitlab

rpm -ivh gitlab-ce-13.9.2-ce.0.el7.x86\_64.rpm

```
[root@hecs-x-large-2-linux-20210304000224 opt]# rpm -ivh gitlab-ce-13.9.2-ce.0.el7.x86_64.rpm
warning: gitlab-ce-13.9.2-ce.0.el7.x86_64.rpm: Header V4 RSA/SHA1 Signature, key ID f27eab47: NOKEY
Preparing...                          ################################# [100%]
Updating / installing...
   1:gitlab-ce-13.9.2-ce.0.el7        ################################# [100%]
It looks like GitLab has not been configured yet; skipping the upgrade script.

       *.                  *.
      ***                 ***
     *****               *****
    .******             *******
    ********            ********
   ,,,,,,,,,***********,,,,,,,,,
  ,,,,,,,,,,,*********,,,,,,,,,,,
  .,,,,,,,,,,,*******,,,,,,,,,,,,
      ,,,,,,,,,*****,,,,,,,,,.
         ,,,,,,,****,,,,,,
            .,,,***,,,,
                ,*,.



     _______ __  __          __
    / ____(_) /_/ /   ____ _/ /_
   / / __/ / __/ /   / __ `/ __ \
  / /_/ / / /_/ /___/ /_/ / /_/ /
  \____/_/\__/_____/\__,_/_.___/


Thank you for installing GitLab!
GitLab was unable to detect a valid hostname for your instance.
Please configure a URL for your GitLab instance by setting `external_url`
configuration in /etc/gitlab/gitlab.rb file.
Then, you can start your GitLab instance by running the following command:
  sudo gitlab-ctl reconfigure

For a comprehensive list of configuration options please see the Omnibus GitLab readme
https://gitlab.com/gitlab-org/omnibus-gitlab/blob/master/README.md

Help us improve the installation experience, let us know how we did with a 1 minute survey:
https://gitlab.fra1.qualtrics.com/jfe/form/SV_6kVqZANThUQ1bZb?installation=omnibus&release=13-9

```

3. 更改访问地址

```shell
#编辑配置文件  
vim /etc/gitlab/gitlab.rb    

#改为自己的IP地址或域名
external_url 'http://gitlab.example.com'
   
#重新加载配置文件
gitlab-ctl reconfigure 
```



# 二、恢复备份

1. 备份

`sudo gitlab-rake gitlab:backup:create`

> 执行完备份命令后会在/var/opt/gitlab/backups目录下生成备份后的文件，如1500809139\_2017\_07\_23\_gitlab\_backup.tar。1500809139是一个时间戳，从1970年1月1日0时到当前时间的秒数。这个压缩包包含Gitlab所有数据（例如：管理员、普通账户以及仓库等等）。

2. 恢复

从指定时间戳的备份恢复（backups目录下有多个备份文件时）：
`sudo gitlab-rake gitlab:backup:restore BACKUP=1500809139_2017_07_23`

从默认备份恢复（backups目录下只有一个备份文件时）：
`sudo gitlab-rake gitlab:backup:restore` 

# 三、启动Gitlab

sudo gitlab-ctl start
sudo gitlab-ctl reconfigure
