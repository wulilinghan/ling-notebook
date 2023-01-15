# 环境变量

打印环境变量

```
go env
```

### GOPROXY

`GOPROXY`就是设置`Golang`的全局代理。在下载依赖包的时候，一般是访问`github`的仓库，国内的环境很容易被墙，所以最好设置一个速度快的代理。`Go`在此版本中`GOPROXY`的默认值为`https://proxy.golang.org`，国内是无法访问的，这里使用七牛云代理。

```
go env -w GOPROXY=https://goproxy.cn,direct
```

### GOPATH

`GOPATH`目录约定由三个子目录：

![image-20230115184804144](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301151848169.png)

- src 
> 存放源代码，go run、go install等命令就是在当前的工作路径中执行（也就是这些命令执行的目标文件夹就是这个src文件夹） 

- pkg
> 存放编译时生成的中间文件  

- bin
> 存放编译后生成的可执行文件

```
go env -w GOPATH=D:\work-go
```

