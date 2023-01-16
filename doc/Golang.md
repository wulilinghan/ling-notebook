# Linux安装Go

> 下载页面：https://go.dev/dl/

创建gopath

```
mkdir -p /root/gopath/{src,pkg,bin}
```

解压到`/usr/lib`目录下

```
tar -xzf go1.19.2.linux-amd64.tar.gz -C /usr/lib
```

配置环境变量

```undefined
vi /etc/profile

添加以下内容
# GOROOT
export GOROOT=/usr/lib/go
# GOPATH
export GOPATH=/root/gopath/
# GOPATH bin
export PATH=$PATH:$GOROOT/bin:$GOPATH/bin

#配置生效
source /etc/profile

# 更换为七牛云代理，提升包下载速度
go env -w GOPROXY=https://goproxy.cn,direct
```



# Go Env

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



# Go项目编译

### windows服务器

> ```
> go build [-o 输出名] [-i] [编译标记] [包名]
> ```

```
go build -o ./build/markdown-book-linux-amd64/markdown-book main.go  
```

编译后会在同级目录生成可执行文件

```
./main.exe
```

### linux服务器

需要go环境，然后将项目源码拷贝到服务器上

```
go build main.go
```

> 下载包很慢的话，看看goproxy有没有换成国内下载地址

编译后会在同级目录生成可执行文件

```
./main
```
