# docker端口映射或启动容器时报错

> [docker端口映射或启动容器时报错](https://blog.csdn.net/whatday/article/details/86762264)

服务器重启后，docker里面有些应用起不来，然后重装出现以下问题，重启docker后，之前重启不了的应用就可以重新启动了

![image-20230129222642773](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301292226815.png)

解决：

重启docker服务后再启动容器

````
systemctl restart docker
````



