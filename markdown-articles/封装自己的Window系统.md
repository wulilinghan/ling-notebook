# （内容操作步骤有问题，待重新验证）

参考视频和帖子：

**B站 [小鱼儿yr系统](https://space.bilibili.com/691599985) UP主视频：**

**[2021年WIN10 20H2(2009)系统封装视频教程 一系列视频教程](https://www.bilibili.com/video/BV11K411M73e)**



**ES5高效封装WIN10系统教程2020系列**：

 [**ES5高效封装WIN10系统教程2020系列（一）母盘定制**](https://www.itsk.com/thread-404410-1-1.html)

[**ES5高效封装WIN10系统教程2020系列（二）准备封装环境**](https://www.itsk.com/forum.php?mod=viewthread&tid=404604)

[**ES5高效封装WIN10系统教程2020系列（三）母盘安装及系统调整**](https://www.itsk.com/thread-404740-1-1.html)

[**ES5高效封装WIN10系统教程2020系列（四）使用工具优化与清理**](https://www.itsk.com/thread-404802-1-1.html)

[**ES5高效封装WIN10系统教程2020系列（五）常用软件安装及设置**](https://www.itsk.com/thread-405051-1-1.html) 

[**ES5高效封装WIN10系统教程2020系列（六）ES5封装**](https://www.itsk.com/thread-405235-1-1.html)




# 简单的系统封装
## 1. 虚拟机安装Win10

安装Vmware16，安装完进入主页面

点击创建虚拟机，选择**自定义**，下一步，再下一步到选择安装来源，选择**稍后安装操作系统**一路点下一步，到固件类型，选择BIOS，下一步，内存给4个G，网络类型选择**不使用网络连接**，选择磁盘选择**创建新虚拟磁盘**，磁盘大小默认就行，勾选**将磁盘储存为单个文件**，下一步到已准备创建虚拟机界面，点击自定义硬件，将打印机等无用选项删除掉，下一步直到完成。

然后进入虚拟机设置选择自己下载原版ISO镜像，点击确定。

![image-20230521162218579](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212150929.png)



启动虚拟机，选择专业版安装，选择自定义安装

![image-20230521162038164](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212150084.png)

![image-20230521162051193](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212150992.png)

下一步，等他安装完成。

到创建用户界面时候，快捷键 Ctrl+Shift+F3，进入审记模式，会直接进入到桌面，系统准备工具3.14 窗口直接叉掉。



这一步的时候建议**拍摄快照**，后面操作有问题直接回滚，方便。

![image-20230521192640595](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212150354.png)



接着安装VMware Tools，装完重启，然后就可以把下好的安装包复制到虚拟中了

![image-20230521173637103](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212150409.png)





## 2. ES5进行封装

安装完成进入系统，安装自己想要安装的软件后，将ES5复制到虚拟机中执行进行系统封装，在系统更改的设置都会被封装进镜像中去。

打开ES5，然后点击设置按钮，点击封装，等他右下角提示封装完成。

![image-20230521165107217](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212151555.png)

![image-20230521175311951](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212151714.png)

我这里一阶段封装报错了

![image-20230521175642279](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212151297.png)

网上搜了下说是装的一些软件影响了例如WINRAR和国产压缩软件和搜狗拼音输入法等等，我装了个BANDIZIP，卸载后尝试封装，还是报错，然后我用系统根目录（C:\Sysprep）下生成的ES5进行封装又成功了。

封装提示成功后，要进入PE进行第二阶段封装，在此之前自己要准备好个PE ISO镜像。

## 4. 进入PE

### 4.1. 使用WinPE制作个PE镜像包

在自己本机上打开WInPE

![image-20230521013701513](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212151292.png)

自己指定iso文件输出路径

![image-20230521013731126](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212151379.png)

**这里注意**：

使用ES5提示封装完成后，先关机，然后在点击 **电源》打开电源时进入固件** ，将Boot选项中 **CD-ROM Drive**设为首选项，这里有点离谱的是，这个CD-ROM Drive开始是在最下面的我按+/-，他丝毫无反应，只能把其他的选项往下挪，他才能挪上去！逆天！

![image-20230521202456121](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212151006.png)

然后再在虚拟机设置CD/DVD选择PE ISO镜像文件，接着Exit Saving Changes，就可以进入PE了

![image-20230521202513249](C:\Users\wuliling\AppData\Roaming\Typora\typora-user-images\image-20230521202513249.png)



进了PE，点击 **C:\Sysprep\ES5S** 目录下的 ES5S.exe

![image-20230521203323605](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212151321.png)

这里可以调整一些设置，我这里就把用户里 启用Administrator 勾上了，然后在**其他**设置关掉保存镜像，最后点封装

打开 Dism++

![image-20230521205052081](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212151690.png)

点击自己的系统盘，找到系统备份，镜像名我这里命名为Win10_22H2，然后点击保存，等待执行完

![image-20230521205218611](C:\Users\wuliling\AppData\Roaming\Typora\typora-user-images\image-20230521205218611.png)

# 进阶系统封装

准备：

软件NTLite：https://www.ntlite.com/download/

基础镜像Windows下载：https://next.itellyou.cn/Original/Index#

## 1、定制母盘 

打开下载好的原版ISO镜像，将sources目录下的install.wim文件复制出来

![image-20230521011005788](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212152887.png)

打开NTLite添加镜像，选择复制出来的 install.wim 文件，双击打开想要修改的版本

![image-20230521011551752](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212152089.png)

我这里只是想保留专业版，程序或系统设置未作调整，可根据自己需求进行调整，然后点开始

![image-20230521012049302](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202305212152349.png)


## 2、准备系统封装环境 




## 3、母盘安装 



## 4、系统调整以及优化清理 



## 5、常用软件安装和设置 



## 6、封装前再次优化清理 



## 7、使用ES5进行封装
