---
title: Zookeeper
url: https://www.yuque.com/tangsanghegedan/ilyegg/ggdggo
---

<a name="lsXAH"></a>

# PrettyZoo可视化工具

[PrettyZoo](https://github.com/vran-dev/PrettyZoo/releases) 一款基于 JavaFX 和 Curator 实现的高颜值 Zookeeper 可视化工具，提供了节点 CRUD、命令行操作模式等众多实用功能。 <a name="L3U1k"></a>

# Zookeeper数据模型

整体结构类似于 linux 文件系统的模式以树形结构存储。其中根路径以**/**开头。
zookeeper 中的所有存储的数据是由 znode 组成的，节点也称为 znode，并以 key/value 形式存储数据。
&#x20;      ![](..\assets\ggdggo\1619417413798-0b7dd3c5-30cd-4076-9064-f3baf8fedd25.png)

```shell
connecting to 127.0.0.1:2181...
connect success 
127.0.0.1:2181	$	get top.b0x0.demo
Command failed: java.lang.IllegalArgumentException: Path must start with / character
127.0.0.1:2181	$	get /top.b0x0.demo
0
cZxid = 0x45
ctime = Sun Apr 25 15:30:05 CST 2021
mZxid = 0x45
mtime = Sun Apr 25 15:30:05 CST 2021
pZxid = 0x45
cversion = 0
dataVersion = 0
aclVersion = 0
ephemeralOwner = 0x0
dataLength = 1
numChildren = 0
```

<a name="YGgLk"></a>

### Znode 的状态属性

| cZxid | 创建节点时的事务ID |
| --- | --- |
| ctime | 创建节点时的时间 |
| mZxid | 最后修改节点时的事务ID |
| mtime | 最后修改节点时的时间 |
| pZxid | 表示该节点的子节点列表最后一次修改的事务ID，添加子节点或删除子节点就会影响子节点列表，但是修改子节点的数据内容则不影响该ID
**（注意，只有子节点列表变更了才会变更pzxid，子节点内容变更不会影响pzxid）** |
| cversion | 子节点版本号，子节点每次修改版本号加1 |
| dataversion | 数据版本号，数据每次修改该版本号加1 |
| aclversion | 权限版本号，权限每次修改该版本号加1 |
| ephemeralOwner | 创建该临时节点的会话的sessionID。
**(**如果该节点是持久节点，那么这个属性值为0) |
| dataLength | 该节点的数据长度 |
| numChildren | 该节点拥有子节点的数量**（只统计直接子节点的数量）** |

<a name="mBkW6"></a>

# Zookeeper 客户端基础命令使用

<a name="EZMDs"></a>

### `ls`

> ls 命令用于查看某个路径下目录列表。

<a name="ytvWh"></a>

### `ls2`

ls2 \[path]

> ls2 命令用于查看某个路径下目录列表，它比 ls 命令列出更多的详细信息。

<a name="VjbTd"></a>

### `get`

get \[path] watch

> get 命令用于获取节点数据和状态信息。

> - **path**：代表路径。
> - **watch**：加上watch表示对节点进行事件监听。

<a name="UCCJ6"></a>

### `stat`

stat \[path] watch

> stat 命令用于查看节点状态信息。

> - **path**：代表路径。
> - **\[watch]**：加上watch表示表示对节点进行事件监听。

<a name="ZLLBT"></a>

### `create`

create \[-s] \[-e] \[path] \[data] acl

> create 命令用于创建节点并赋值。

> - **\[-s] \[-e]**：-s 和 -e 都是可选的，-s 代表顺序节点， -e 代表临时节点，注意其中 -s 和 -e 可以同时使用的，并且临时节点不能再创建子节点。
> - **path**：指定要创建节点的路径，比如**/runoob**。
> - **data**：要在此节点存储的数据。
> - acl：访问权限相关，默认是 world，相当于全世界都能访问。
>   - ZooKeeeper has the following built in schemes:
>     - **world **has a single id,*anyone*, that represents anyone.
>     - **auth **doesn't use any id, represents any authenticated user.
>     - **digest **uses a\_username:password\_string to generate MD5 hash which is then used as an ACL ID identity. Authentication is done by sending the\_username:password\_in clear text. When used in the ACL the expression will be the\_username:base64\_encoded\_SHA1\_password\_digest\_.
>     - **ip **uses the client host IP as an ACL ID identity. The ACL expression is of the form\_addr/bits\_where the most significant\_bits\_of\_addr\_are matched against the most significant\_bits\_of the client host IP.

<a name="Xbd6v"></a>

### `set`

set \[path] \[data] \[version]

> set 命令用于修改节点存储的数据。

> - **path**：节点路径。
> - **data**：需要存储的数据。
> - **version**：可选项，版本号(可用作乐观锁)。

<a name="b3KZ1"></a>

### `delete`

delete \[path] \[version]

> delete 命令用于删除某节点。

> - **path**：节点路径。
> - **\[version]**：可选项，版本号（同 set 命令）。

<a name="s414X"></a>

# Zookeeper分布式锁

[ZooKeeper分布式锁应用](http://www.yunweipai.com/37005.html)
[
](http://www.yunweipai.com/37005.html)
