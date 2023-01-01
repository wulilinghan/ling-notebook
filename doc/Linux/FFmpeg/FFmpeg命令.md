# FFmpeg介绍

> 官网地址：https://www.ffmpeg.org/
>
> Github：https://github.com/FFmpeg/FFmpeg

FFmpeg 是特别强大的专门用于处理音视频的开源库。你既可以使用它的 API 对音视频进行处理，也可以使用它提供的工具，如 ffmpeg, ffplay, ffprobe，来编辑你的音视频文件。

**ffmpeg：** ffmpeg 是一个命令行工具，用于在命令行实现 FFmpeg 具有的功能。

**ffplay：**  ffplay 是一个使用 FFmpeg 库和 SDL 库的非常简单和便携的媒体播放器。它主要用作各种 FFmpeg API 的测试平台。

**ffprobe：**  ffprobe 是一个多媒体流分析工具。它从多媒体流中收集信息，并且以人类和机器可读的形式打印出来。它可以用来检测多媒体流的容器类型，以及每一个多媒体流的格式和类型。它可以作为一个独立的应用来使用，也可以结合文本过滤器执行更复杂的处理。

# FFmpeg源码目录及作用

libavcodec： 提供了一系列编码器的实现。

libavformat： 实现在流协议，容器格式及其本 IO 访问。

libavutil： 包括了 hash 器，解码器和各利工具函数。

libavfilter： 提供了各种音视频过滤器。

libavdevice： 提供了访问捕获设备和回放设备的接口。

libswresample： 实现了混音和重采样。

libswscale： 实现了色彩转换和缩放工能。

# FFmpeg工作流

FFmpeg对一个输入文件的处理流程可以用下图来进行对照分析：

![image-20230101153728879](https://raw.githubusercontent.com/wulilinghan/PicBed/main/img/202301011537914.png)

```
 _______              ______________
|       |            |              |
| input |  demuxer   | encoded data |   decoder
| file  | ---------> | packets      | -----+
|_______|            |______________|      |
                                           v
                                       _________
                                      |         |
                                      | decoded |
                                      | frames  |
                                      |_________|
 ________             ______________       |
|        |           |              |      |
| output | <-------- | encoded data | <----+
| file   |   muxer   | packets      |   encoder
|________|           |______________|
```

这个图的知识背景简要概括就是，我们平时接触到叫“视频”的东西，其实是视频和音频，准确说是视频流和音频流的混合物。混合的方式就是封装，英文叫muxing。我们耳熟能详的flv、mp4、avi等视频扩展名，其实都是代表不同的封装方式。ffmpeg要处理这种混合物，首先需要做demuxing，也就是解封装。解封装后，会得到独立的视频和音频流。但此时的视频和音频流都是经过编码的，无法直接被ffmpeg操作修改。为什么音视频需要编码？因为音视频存在时空冗余问题，不经编码压缩的话存储开销和传输带宽开销会很大。所以ffmpeg需要先按照音频流视频流的编码方式，使用对应的解码器进行解码后，才能得到原始的帧，用于ffmpeg的后续处理。“后续处理”其实一般是叫做使用filter，把原始的音视频流经过filter处理后得到期望的转换后的音视频流。之后还会对处理后的原始音视频流进行编码，对编码后的音视频流进行再封装，得到我们通常意义上的音视频文件。

filter：filter完全可以类比数字信号课程中filter的概念。ffmpeg把filter做了simple和complex的分类。

- simple：就是指一输入一输出，输入输出的流种类相同的filter。
- complex：指输入和输出类型不同，或存在多输入或输出情况的filter。

copy：可以对输入文件的某一种流的输出指定copy参数。指定copy参数后，会跳过对该流的解码->filter->编码阶段，解封装后的这一路流直接置入输出文件对应的流中。

# FFmpeg

> https://cheatography.com/thetartankilt/cheat-sheets/ffmpeg/
>
> Many of the audio/­video file formats [such as WAV, AIFF, MP4, MKV, AVI, etc.] are envelope containers for multiple streams of data content such as audio, video, subtitle text, still images, fonts, etc.

Command syntax：

```sh
ffmpeg [global_options] \
{[input_options_1] -i [input_url_1]} ... {[...n] -i [...n]} \
{[output_options_1] output_url_1} ... {[output_options_n] output_url_n}
```

Display input stream inform­ation:

```sh
ffmpeg -i video.avi
```



## 基础参数

```sh
ffmpeg -version											# 查看ffmpeg版本
ffmpeg -codecs                                          # 列出libavcodec中的编解码器
ffmpeg -decoders 										# 显示可用的解码器。
ffmpeg -encoders 										# 显示所有可用的编码器。
ffmpeg -formats                                         # 列出支持的格式
ffmpeg -protocols                                       # 列出支持的协议
ffmpeg -filters 										# 列出libavfilter中的过滤器。
```

## 通用参数

```sh
#不改变音频和视频的编码格式，直接拷贝，这样会快很多。
-c copy
-c:a copy								
-c:v copy
```



## 视频参数

```sh
-c:v libx264               	# 输入/输出，指定libx264视频编码器。c 是 codec 的缩写，v 是 video 缩写
-vcodec libx264            	# 输出，指定libx264视频编码器(旧写法)

-b:v 1M                    	# 设置视频码率 1mbps/s
-b:v 5000k

-aspect 16:9               	# 输出，设置视频宽高比，例如4:3，16:9，16:10，5:4
-r 60                      	# 输入/输出，每秒60帧率，缺省25
-s 1280x720                	# 输入/输出，宽x高，视频尺寸：640x480（默认与源相同）
-vn                        	# 输出，禁用视频
-f mp3 output.mp3			# 输出文件格式。

-preset ultrafast			# 指定输出的视频质量，会影响文件的生成速。预设值有包括：ultrafast，superfast，veryfast，faster，fast，medium，slow，slower，veryslow和placebo。ultrafast编码速度最快，但压缩率低，生成的文件更大，placebo则正好相反。x264所取的默认值为medium。需要说明的是，preset主要是影响编码的速度，并不会很大的影响编码出来的结果的质量。压缩高清电影时，我一般用slow或者slower，当你的机器性能很好时也可以使用veryslow，不过一般并不会带来很大的好处。

-crf 25					# 这是最重要的一个选项，用于指定输出视频的质量，取值范围是0-51，默认值为23，数字越小输出视频的质量越高。这个选项会直接影响到输出视频的码率。一般来说，压制480p我会用20左右，压制720p我会用16-18，1080p我没尝试过。个人觉得，一般情况下没有必要低于16。最好的办法是大家可以多尝试几个值，每个都压几分钟，看看最后的输出质量和文件大小，自己再按需选择。
```

## 音频参数

```sh
-c:a aac                   	# 输入/输出，指定aac音频编码器。c 是 codec 的缩写，a 是 audio 缩写
-acodec aac                	# 指定aac音频编码器(旧写法)

-b:a 1M                    	# 设置音频码率 1mbps/s,b 是 bitrate 的缩写, a 是 audio 的缩与。
-b:a 192k

-aq QUALITY                	# 音频质量，编码器相关
-ar 44100                  	# 输入/输出，音频采样率，PSP只认24000,通常使用的值是22050 Hz、44100 Hz、48000 Hz。
-ac 2                      	# 输入/输出，audio channel音频声道数量，1就是单声道，2就是立体声
-an                        	# 输出，禁止音频
-vol 512                   	# 设定音量，改变音量为 200%
```

## overlay滤镜用法-水印及画中画 

```
描述：前景窗口(第二输入)覆盖在背景窗口(第一输入)的指定位置。

语法：overlay[=x:y[[:rgb={0, 1}]]
    参数 x 和 y 是可选的，默认为 0。
    参数 rgb 参数也是可选的，其值为 0 或 1，默认为 0。

参数说明：
    x                   从左上角的水平坐标，默认值为 0
    y                   从左上角的垂直坐标，默认值为 0
    rgb                 值为 0 表示输入颜色空间不改变，默认为 0；值为 1 表示将输入的颜色空间设置为 RGB

变量说明：如下变量可用在 x 和 y 的表达式中
    main_w 或 W          主输入(背景窗口)宽度
    main_h 或 H          主输入(背景窗口)高度
    overlay_w 或 w       overlay 输入(前景窗口)宽度(水印)
    overlay_h 或 h       overlay 输入(前景窗口)高度(水印)
    
```



## 使用案例

####  视频转码
```sh
ffmpeg -i input.mov output.mp4                              # 转码为 MP4
ffmpeg -i input.mov -c:v libx264 -c:a aac -2 out.mp4        # 指定编码参数
ffmpeg -i input.mov -c:v libvpx -c:a libvorbis out.webm     # 转换 webm
ffmpeg -i input.mp4 -ab 56 -ar 44100 -b 200 -f flv out.flv  # 转换 flv
ffmpeg -i input.mp4 -an animated.gif                        # 转换 GIF
```

####  提取视频中的音频文件
```
ffmpeg -i input.mp4 -vn -c:a copy output.aac       
ffmpeg -i input.mp4 -vn -c:a mp3 output.mp3   
```
#### 压缩视频文件

```
ffmpeg -i input.mp4 -vf scale=1920:-1 -c:v libx264 -preset veryslow -crf 24 output.mp4
```

> -vf scale 指定使用简单过滤器 scale，1920:-1 中的 1920 指定取视频的宽度。-1 表示高度随宽度一起变化。

#### 压缩音频文件

例如，你有一个 320 kbps 比特率的音频文件。你想通过更改比特率到任意较低的值来压缩它，像下面。
```
ffmpeg -i input.mp3 -ab 128 output.mp3
```
各种各样可用的音频比特率列表是：96kbps，112kbps，128kbps，160kbps，192kbps，256kbps，320kbps

#### 视频加图片水印

```markdown
# 水印视频必须要重新编码

# 左上角
ffmpeg -i input.mp4 -i logo.png -filter_complex overlay=0:0  -c:v libx264 -c:a copy -y -f mp4 temp.mp4
# 左下角
ffmpeg -i input.mp4 -i logo.png -filter_complex overlay=0:H-h -c:v libx264 -c:a copy -y -f mp4 temp.mp4
# 右上角
ffmpeg -i input.mp4 -i logo.png -filter_complex overlay=W-w:0 -c:v libx264 -c:a copy -y -f mp4 temp.mp4
# 右下角
ffmpeg -i input.mp4 -i logo.png -filter_complex overlay=W-w:H-h -c:v libx264 -c:a copy -y -f mp4 temp.mp4
```
> 左上角	overlay=0:0
>
> 左下角	overlay=0:H-h
>
> 右上角	overlay=W-w:0
>
> 右下角	overlay=W-w:H-h
>
> 上面的0可以改为5，或10像素，以便多留出一些空白。

#### 切分视频
```
ffmpeg -i input.mp4 -ss 0 -t 60 first-1-min.mp4             # 切割开头一分钟
ffmpeg -i input.mp4 -ss 60 -t 60 second-1-min.mp4           # 一分钟到两分钟
ffmpeg -i input.mp4 -ss 00:01:23.000 -t 60 first-1-min.mp4  # 另一种时间格式
```
####视频尺寸
```
ffmpeg -i input.mp4 -vf "scale=640:320" output.mp4          # 视频尺寸缩放
ffmpeg -i input.mp4 -vf "crop=400:300:10:10" output.mp4     # 视频尺寸裁剪
```

#### 其他用法
```
ffmpeg -i sub.srt sub.ass                                   # 字幕格式转换
ffmpeg -i input.mp4 -vf ass=sub.ass out.mp4                 # 烧录字幕进视频
ffmpeg -i "<url>" out.mp4                                   # 下载视频
```