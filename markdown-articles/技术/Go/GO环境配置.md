# Go环境配置
```
国内常用的GO代理
goproxy
https://goproxy.io/zh/
七牛云
https://goproxy.cn
阿里云
https://mirrors.aliyun.com/goproxy/

Go 版本是 1.13 及以上
go env -w GO111MODULE=on
go env -w GOPROXY=https://goproxy.cn,direct

其他版本
export GO111MODULE=on
export GOPROXY=https://goproxy.cn

取消代理
go env -u GOPROXY

查看GO的配置
go env
//以JSON格式输出
go env -json


Windows
使用命令执行或者手动配置系统环境变量（GO111MODULE和GOPROXY）

打开你的 PowerShell 并执行
C:\> $env:GO111MODULE = "on"
C:\> $env:GOPROXY = "https://goproxy.cn"
```