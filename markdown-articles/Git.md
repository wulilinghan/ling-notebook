# Git用户配置

```console
# 全局信息
git config --list --show-origin

# 用户信息
git config --global user.name "xxxx"
# 假如是github，这里的邮件地址跟github的邮箱地址一致，才会有小绿格
git config --global user.email xxxx@example.com
```

## .gitattributes
项目语言统计

| .gitattributes属性分类   | 分类         | 简单说明     | 举例    |
| ------------------------ | ------------ | ---------------| ----------|
| linguist-language     | 语言转化     | 将一种语言识别为另一种语言                                   | `*.rb linguist-language=Java`        | 
| linguist-vendored     | 供应(商)代码 | 标记供应商文件，把使用的库文件标记为供应商代码（即不是自己编写的），如jQuery库文件 | jquery.js linguist-vendored | 
| linguist-generated     | 生成的代码   | 标记一些生成的文件，如压缩的js默认被忽略          | Api.elm linguist-generated=true    | 
| linguist-documentation | 文档       | 标记一些文档     | `project-docs/* linguist-documentation` | 
| linguist-detectable   | 可检测的     | 标记某文件是否检测为语言，默认（各种常见语言）都可检测，为false时，不进行语言检测 | export_bom.py linguist-detectable=false |

> 注意：
> 
> 1. 等号前后是不能有空格的！如果文件`.gitattributes`文件有错误，git add 或者git commit 时会给出提示，github上面也会有相应提示。
> 2. 对于 `jquery.js linguist-vendored=true`这种属性值为布尔值的，好像只有使用 `jquery.js linguist-vendored`这种形式才有用。



# 生成 SSH 公钥

https://git-scm.com/book/zh/v2/%E6%9C%8D%E5%8A%A1%E5%99%A8%E4%B8%8A%E7%9A%84-Git-%E7%94%9F%E6%88%90-SSH-%E5%85%AC%E9%92%A5

#  如何重命名仓库，并同步远程仓库

## 修改仓库名

以github为例，选择Setting，输入新仓库名，然后点击Rename，这里就将远程仓库进行重命名了

![image-20230127214103305](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301272141411.png)

## 修改本地仓库名

我这里使用Idea，直接右击项目，ReFactor，然后Rename，切记先Rename之后，然后再去修改项目的文件夹名称，不然会出问题。

![image-20230127214404327](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301272144362.png)

## 同步远程和本地仓库

修改完了远程仓库和本地仓库之后，我们需要使用Git进行链接更新和同步，因为原来使用的是老的仓库链接地址，可以先用下面这条指令检查当前远程仓库的信息，执行后将会列出所有远程仓库信息

```
git remote -v
```

![image-20230127214505473](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301272145507.png)

目前还是旧地址，使用以下指令，进行更新修改，`assets` 是我重命名的新仓库名

```
git remote set-url origin git@github.com:wulilinghan/assets.git

git branch --set-upstream-to=origin/main
```

改完之后可以连接本地仓库和远程仓库，同步一下，这里注意下，github之前默认`master`是主分支名称，后面是`main`了，要看下自己这个仓库对应分支名称

```
git remote -v
```

![image-20230127214915547](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301272149579.png)
