输入 mail 命令，测试服务器有没有安装mailx:

```shell
[root@localhost ~]# mail
Heirloom Mail version 12.5 7/5/10.  Type ? for help.
"/var/spool/mail/root": 1 message 1 new
>N  1 Mail Delivery System  Tue Jan  3 22:58  77/2676  "Undelivered Mail Returned to Sender"
& 
```

如若提示 -bash: mail: command not found，则需要安装：

```
yum -y install mailx
```



在/etc/mail.rc文件的末行添加以下内容，不用重启：

```
set from=123456@qq.com
set smtp=smtp.qq.com  
set smtp-auth-user=123456@qq.com
set smtp-auth-password=hqmccxvzspsddjg
```

> from：用来发送邮件的邮箱
> smtp：smtp服务器的地址
> smtp-auth-user：smtp服务器认证的用户名
> smtp-auth-password：smtp服务器认证的用户密码，这里我用的QQ邮箱的授权码

测试：

```shell
echo "邮件内容" | mail -s '邮件标题' abc@163.com,abc@163.com
 
 
echo 这是一封测试邮件 | mail -s "Bash发送邮件测试" 123456@qq.com
```

![image-20230103233045386](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301032330417.png)