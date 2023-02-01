# 一、 RDB持久化

- RDB持久化是把当前进程数据生成快照保存到硬盘的过程， 触发RDB持久化过程分为**手动触发**和**自动触发**。
- 生成的rdb文件的名称以及存储位置由redis.conf中的**dbfilename**和**dir**两个参数控制，默认生成的rdb文件是dump.rdb。
- Redis默认会采用LZF算法对生成的RDB文件做压缩处理，压缩后的文件远远小于内存大小，默认开启。 

### 手动触发

- 手动触发的命令有save和bgsave。
- save：该命令会阻塞Redis服务器，造成服务不可用直到持久化完成，线上环境不建议使用。
- bgsave：每次进行RDB过程都会fork一个子进程，由子进程完成RDB的操作，主进程只有在fork子进程是会短暂阻塞。

> bgsave是针对save阻塞主进程所做的优化，后续所有的自动触发都是使用bgsave进行操作



### 自动触发

- 除了手动触发RDB，Redis服务器内部还有如下几个场景能够自动触发RDB：
  1. redis.conf中配置**save m n**，即在m秒内有n次修改时，自动触发bgsave生成rdb文件。
  2. 主从复制时，从节点要从主节点进行全量复制时也会触发bgsave操作，生成当时的快照发送到从节点。
  3. 执行**debug reload**命令重新加载Redis时， 也会自动触发save操作。
  4. 默认情况下执行**shutdown**命令时， 如果没有开启AOF持久化功能则自动执行**bgsave**。 

### RDB执行流程

![920028540-f3ab8fc1286db26f.png](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302012109980.png)

通过上图可以很清楚RDB的执行流程，如下：

1. 执行bgsave命令后，会先判断是否存在AOF或者RDB的子进程，如果存在，直接返回。
2. 父进程fork操作创建一个子进程，fork操作中父进程会被阻塞。
3. fork完成后，子进程开始根据父进程的内存生成临时快照文件，完成后对原有的RDB文件进行替换。执行lastsave命令可以查看最近一次的RDB时间。
4. 子进程完成后发送信号给父进程，父进程更新统计信息。

### 开启/关闭rdb持久化

如果要开启/关闭rdb持久化可以用两种方法：

- 执行以下命令（redis-cli）：

```shell
config set save ""
```

- 修改配置文件

```shell
################################ SNAPSHOTTING  ################################
# 打开该行注释
save ""

# 注释掉以下内容
# save <seconds> <changes>
# save 900 1
# save 300 10
# save 60 10000

#bgsave发生错误时是否停止写入，一般为yes
stop-writes-on-bgsave-error yes

#持久化时是否使用LZF压缩字符串对象?
rdbcompression yes

#是否对rdb文件进行校验和检验，通常为yes
rdbchecksum yes

# RDB持久化文件名
dbfilename dump.rdb

#持久化文件存储目录
dir ./
```



### RDB优缺点



#### 优点

- RDB文件是某个时间节点的快照，默认使用LZF算法进行压缩，压缩后的文件体积远远小于内存大小，适用于备份、全量复制等场景；
- Redis加载RDB文件恢复数据要远远快于AOF方式； 

#### 缺点

- RDB方式实时性不够，无法做到秒级的持久化；
- 每次调用bgsave都需要fork子进程，fork子进程属于重量级操作，频繁执行成本较高；
- RDB文件是二进制的，没有可读性，AOF文件在了解其结构的情况下可以手动修改或者补全；
- 版本兼容RDB文件问题； 

# 二、 AOF持久化

AOF（append only file） 持久化： 以独立日志的方式记录每次写命令，重启时再重新执行AOF文件中的命令达到恢复数据的目的。 AOF的主要作用是解决了数据持久化的实时性， 目前已经是Redis持久化的主流方式。

### 开启/关闭AOF

- 执行以下命令（redis-cli）

```shell
# 开启aof
config set appendonly yes

# 关闭aof
config set appendonly no
```

- 修改配置文件

```shell
############################## APPEND ONLY MODE ###############################
# 开启aof
appendonly true

# aof文件名称
appendfilename "appendonly.aof"

# aof文件存储位置
dir ./

# appendfsync always
appendfsync everysec
# appendfsync no

# 是否在执行重写时不同步数据到AOF文件
no-appendfsync-on-rewrite no

# 触发AOF文件执行重写的增长率 代表当前AOF文件空间（aof_current_size） 和上一次重写后AOF文件空间（aof_base_size） 的比值。
auto-aof-rewrite-percentage 100
# 触发AOF文件执行重写的最小size
auto-aof-rewrite-min-size 64mb

#redis在恢复时，会忽略最后一条可能存在问题的指令
aof-load-truncated yes

#是否打开混合开关
aof-use-rdb-preamble yes
```

> Redis提供了多种AOF缓冲区同步文件策略， 由参数appendfsync控制，如下：
>
> - 配置为always时， 每次写入都要同步AOF文件， 在一般的SATA硬盘上，Redis只能支持大约几百TPS写入， 显然跟Redis高性能特性背道而驰，不建议配置。
> - 配置为no，由于操作系统每次同步AOF文件的周期不可控，而且会加大每次同步硬盘的数据量，虽然提升了性能，但数据安全性无法保证。
> - 配置为everysec（默认的配置），是**建议的同步策略**， 也是默认配置，做到兼顾性能和数据安全性。理论上只有在系统突然宕机的情况下丢失1秒的数据（当然，这是不太准确的）。

![1485143995-3108788b307e57de.png](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302012112157.png)

### AOF文件重写机制

- 随着命令不断写入AOF， 文件会越来越大， 为了解决这个问题， Redis引入AOF重写机制压缩文件体积。 AOF文件重写是把Redis进程内的数据转化为写命令同步到新AOF文件的过程。
- **为什么要文件重写呢？**因为文件重写能够使得AOF文件的体积变得更小，从而使得可以更快的被Redis加载。
- 重写过程分为手动触发和自动触发。

手动触发直接使用**bgrewriteaof**命令。
根据auto-aof-rewrite-min-size和auto-aof-rewrite-percentage参数确定自动触发时机。

> 自动触发时机相当于**aof\_current\_size>auto-aof-rewrite-minsize&&（aof\_current\_size-aof\_base\_size） /aof\_base\_size>=auto-aof-rewritepercentage**。其中aof\_current\_size和aof\_base\_size可以在info Persistence统计信息中查看。

> auto-aof-rewrite-min-size: 表示触发aof重写时aof文件的最小体积，默认64mauto-aof-rewrite-percentage： 表示当前aof文件空间和上一次重写后aof文件空间的比值，默认是aof文件体积翻倍时触发重写

auto-aof-rewrite-percentage的计算方法：
auto-aof-rewrite-percentage =（当前aof文件体积 - 上次重写后aof文件体积）/ 上次重写后aof文件体积 * 100% 

### AOP文件重写流程

![2159277-20201218230022086-946569922.png](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302012112003.png) 

### AOF优缺点



#### 优点

- 数据安全性较高，每隔1秒同步一次数据到aof文件，最多丢失1秒数据；
- aof文件相比rdb文件可读性较高，便于灾难恢复；

#### 缺点

- 虽然经过文件重写，但是aof文件的体积仍然比rdb文件体积大了很多，不便于传输且数据恢复速度也较慢
- aof的恢复速度要比rdb的恢复速度慢

# 三、 AOF和RDB的区别

- RDB持久化是指在指定的时间间隔内将内存中的数据集快照写入磁盘，实际操作过程是fork一个子进程，先将数据集写入临时文件，写入成功后，再替换之前的文件，用二进制压缩存储。
- AOF持久化以日志的形式记录服务器所处理的每一个写、删除操作，查询操作不会记录，以文本的方式记录，可以打开文件看到详细的操作记录。 

# 四、 如何重启恢复数据？

![2159277-20201218230041019-113334894.png](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img2023/202302012113368.png) 

# 五、 持久化性能问题和解决方案

建议同时使用两种持久化功能，定时生成 RDB 快照（snapshot）非常便于进行数据库备份， 并且 RDB 恢复数据集的速度也要比 AOF 恢复的速度要快， 除此之外， 使用 RDB 还可以避免之前提到的 AOF 程序的 bug 。
