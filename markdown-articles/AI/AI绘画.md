# Stable Diffusion

## Tips

1.如果是自己本地部署，有些模型是需要加关键词触发的，需要注意一下

2.推荐使用 **小尺寸分辨率 + 高清修复**（Highres. fix 高清修复）

720x1280

竖屏：540x960 to 1080x1920，270x480

3如果想自己玩AI绘画模型又没有条件本地部署的，可以试试爱作画网站(https://www.aizuohua.com/)的上传模型功能，不用部署，对电脑配置也没要求（具体教程可以看我之前的视频，操作非常简单），每天可以免费画20张图，这个网站最大的好处就是操作简单，自由度高，然后出图非常快。



## Chinese-wedding

> https://huggingface.co/zuzhe/Chinese-wedding
>
> https://www.bilibili.com/read/cv21419607

起手式关键词

```
{best quality}, {{masterpiece}}, {highres}, {an extremely delicate and beautiful}, original, extremely detailed wallpaper,1girl
```

负面词

```
(((simple background))),monochrome ,lowres, bad anatomy, bad hands, text, error, missing fingers, extra digit, fewer digits, cropped, worst quality, low quality, normal quality, jpeg artifacts, signature, watermark, username, blurry, lowres, bad anatomy, bad hands, text, error, extra digit, fewer digits, cropped, worst quality, low quality, normal quality, jpeg artifacts, signature, watermark, username, blurry, ugly,pregnant,vore,duplicate,morbid,mut ilated,tran nsexual, hermaphrodite,long neck,mutated hands,poorly drawn hands,poorly drawn face,mutation,deformed,blurry,bad anatomy,bad proportions,malformed limbs,extra limbs,cloned face,disfigured,gross proportions, (((missing arms))),(((missing legs))), (((extra arms))),(((extra legs))),pubic hair, plump,bad legs,error legs,username,blurry,bad feet
```



## chilloutmix + Lora



### Kohya-ss Additional Networks

先安装 [Kohya-ss Additional Networks](https://ghproxy.com/https://github.com/kohya-ss/sd-webui-additional-networks.git)

启动 [秋葉aaaki](https://space.bilibili.com/12566101) 的ai启动器，进入web页面选择 拓展》可用 点击 **加载自** 按钮，然后出现拓展列表 找到 Kohya-ss Additional Networks，点击安装，安装完会提醒重启，点击左侧 **已安装** 点击 **应用并重启用户界面**

![image-20230219101341032](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230219101341084.png)

![image-20230219101354160](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230219101354185.png)

![image-20230219101425147](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230219101425185.png)



###chilloutmix

> 下载地址：https://huggingface.co/TASUKU2023/Chilloutmix



chilloutmix放到根目录的 **`models\Stable-diffusion`** 目录下



###Lora

lora 放到根目录的 **`extensions\sd-webui-additional-networks\models\lora `** 目录下

#### Korean Doll Likeness

> 下载地址：https://civitai.com/models/7448/korean-doll-likeness

##### Model

##### TXT2IMG

- Prompt

```
<koreanDollLikeness_v10:0.66>, best quality, ultra high res, (photorealistic:1.4), 1woman, sleeveless white button shirt, black skirt, black choker, cute, (Kpop idol), (aegyo sal:1), (platinum blonde hair:1), ((puffy eyes)), looking at viewer, full body, facing front
```

- Negative prompt

```
paintings, sketches, (worst quality:2), (low quality:2), (normal quality:2), lowres, normal quality, ((monochrome)), ((grayscale)), skin spots, acnes, skin blemishes, age spot, glans, nsfw, nipples
```

- Sampler：DPM++ SDE Karras

- Sampling steps：28

- CFG Scale：8

- seed ：1315543661

![image-20230514223821412](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230514223821769.png)

#### Japanese Doll Likeness

> 下载地址：https://civitai.com/models/10135/japanese-doll-likeness

##### Model
chilloutmix_cilloutmixNi

##### TXT2IMG

- Prompt

```
best quality, ultra high res, (photorealistic:1.4), 1girl, white camisole, (busty), ((huge breasts)), large breasts, cleavage, (brown long hair:1.3), (looking at viewer), (full body), (closeup), <lora:japaneseDollLikeness_v10:0.66>
```

- Negative prompt

```
paintings, sketches, (worst quality:2), (low quality:2), (normal quality:2), lowres, normal quality, ((monochrome)), ((grayscale)), skin spots, acnes, skin blemishes, age spot, glans
```

- Sampler：DPM++ SDE Karras

- Sampling steps：28

- CFG Scale：8

- seed：835433766

#### Taiwan Doll Likeness

##### Model



##### TXT2IMG

- Prompt

```
<lora:taiwanDollLikeness_v10:0.66>, best quality, ultra high res, (photorealistic:1.4), 1girl, loose and oversized black jacket, white sports bra, (yoga pants:1), (light brown hair:1.2), looking at viewer, makeup, wide angle
```

- Negative prompt

```
paintings, sketches, (worst quality:2), (low quality:2), (normal quality:2), lowres, normal quality, ((monochrome)), ((grayscale)), skin spots, acnes, skin blemishes, age spot, glans
```

- Sampler：DPM++ SDE Karras

- Sampling steps：28

- CFG Scale：8

- seed：1512004616



##anything V3

> 下载地址：https://huggingface.co/Linaqruf/anything-v3.0

标签：二次元、动漫

出图效果好，但是风格比较单一，对关键词的要求不太高。



## chilloutmix + lora

<img src="https://image.civitai.com/xG1nkqKTMzGDvpLrqFT7WA/3a8e5138-b0fa-4648-bc47-67a524586e58/width=960/00931-3265293977-(8k,%20RAW%20photo,%20best%20quality,%20masterpiece_1.2),%20(realistic,%20photo-realistic_1.37),%20ultra%20highres,%20depth%20of%20field,%20chromatic%20aber.jpeg" style="height:200px" align="left"/>

所需主模型：**[chilloutmix_NiPrunedFp32Fix](https://civitai.com/models/6424/chilloutmix?modelVersionId=11745)**

Lora模型: 【**[cuteGirlMix4_v10](https://huggingface.co/luxluna/cuteGirlMix4_v10/tree/main)**】、【**[seeThroughSilhouette_v10](https://huggingface.co/samle/sd-webui-models/blob/main/seeThroughSilhouette_v10.safetensors)**】



```
extremely detailed CG unity 8k wallpaper,(masterpiece),(best quality),(ultra detailed),(ultra realistic),(Best character details:1.36),nikon d750 f/1.4 55mm,dynamic angle,professional lighting, photon mapping, radiosity, physically-based rendering,
outdoors,looking at viewer,blush,(taut shirt), jeans,
1girl,(mature female:0.2),tall body,golden proportions,(Kpop idol),(shiny skin:1.2),(oil skin:1.1),makeup,[:(high detailed face:1.2):0.2]:, <lora:cuteGirlMix4_v10:0.8>, (close up), park, depth of field, <lora:seeThroughSilhouette_v10:0.5>,( closed mouth: 0.5)
((wavy gray hair and a sophisticated sense of style)),(aegyo sal:1),(puffy eyes),(eyelashes:1.1),(parted lips:1.1),red lipstick,wide shoulders,
Negative prompt: Multiple people,More than one person,2girl,DeepNegative,
sketches,lowres,polar lowres,(worst quality:2),(low quality:2),(normal quality:2),((monochrome)),((grayscale)),blurry,cropped,mutation,deformed,text,error,signature,watermark,username,extra digit,fewer digits,jpeg artifacts,
skin spots, acnes, skin blemishes,
bad anatomy,bad anatomy,bad proportions,gross proportions,long neck,cross-eyed,malformed limbs,blurred hands,fused fingers,poorly drawn face,poorly drawn hands,
(mutated hands and fingers:1.3),(mutated legs and foots:1.3),bad body,bad limbs,bad arms,bad hands,bad fingers,bad leg,bad feet,missing limbs,missing arms,missing hands,missing fingers,missing legs,missing footextra limbs,extra arms,extra fingers,extra leg,extra foot,
Steps: 28, Sampler: DPM++ SDE Karras, CFG scale: 7.5, Seed: 1340860639, Face restoration: CodeFormer, Size: 640x960, Model hash: fc2511737a, Model: chilloutmix_NiPrunedFp32Fix, Denoising strength: 0.4, Hires upscale: 1.5, Hires steps: 30, Hires upscaler: Latent (bicubic antialiased)

```



<img src="https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/202306192212698.png" style="height:600px"/>



》 更多主模型替换：
【**[majicMIX sombre](https://civitai.com/models/62778/majicmix-sombre)**】

<img src="https://www.freedidi.com/wp-content/uploads/2023/05/00001-1362832146.jpeg" style="height:200px" align="left"/>


【**[XXMix_9realistic](https://civitai.com/models/47274/xxmix9realistic)**】

<img src="https://www.freedidi.com/wp-content/uploads/2023/05/13813-2748008393-front-focusin-the-dark_1.6_Hyperrealist-portrait-of-female-by-david-hockney-and-alphonse-muchafantasy-ar.jpeg" style="height:200px" align="left"/>

【**[majicMIX realistic](https://civitai.com/models/43331/majicmix-realistic)**】

<img src="https://www.freedidi.com/wp-content/uploads/2023/05/00006-3135271734-1.jpeg" style="height:200px" align="left"/>

【[B2](https://civitai.com/images/754726?period=AllTime&periodMode=published&sort=Newest&view=categories&modelVersionId=55911&modelId=43331&postId=204993)】

<img src="https://www.freedidi.com/wp-content/uploads/2023/05/00015-3984321575.jpeg" style="height:200px" align="left"/>



【[B3](https://civitai.com/images/753660?period=AllTime&periodMode=published&sort=Newest&view=categories&modelVersionId=55911&modelId=43331&postId=204763)】

<img src="https://www.freedidi.com/wp-content/uploads/2023/05/01589-1370404257-masterpiece-best-quality-ultra-high-res-close-up-slim-body-1girlschool-uniform-long-hair-laughing-ramen-night-Tokyo.jpeg" style="height:200px" align="left"/>

【[B4](https://civitai.com/images/714213?period=AllTime&periodMode=published&sort=Newest&view=categories&modelVersionId=55911&modelId=43331&postId=195070)】

<img src="https://www.freedidi.com/wp-content/uploads/2023/05/504-4k-1.jpg" style="height:200px" align="left"/>



## 胶片风格AI写真照片

FilmGirl 胶片风 Film Grain LoRA & LoHA

[FilmGirl 胶片风 Film Grain LoRA & LoHA - VELVIA 2.0 LoRA | Stable Diffusion LoRA | Civitai](https://civitai.com/models/33208/filmgirl-film-grain-lora-and-loha)



这是用于生成胶片风格AI写真照片的LoHA & LoRA模型。与[majicMIX realistic](https://civitai.com/models/43331/majicmix-realistic?modelVersionId=55911)**、**[MoonMIX](https://civitai.com/models/43977/moonmix)或[Chilloumix](https://civitai.com/models/6424/chilloutmix)搭配使用，可以生成逼真的胶片风格照片。同时推荐搭配TEXTUAL INVERSION**模型：**[bad_pictures](https://civitai.com/models/23178/badpictures)**、**[negative_hand Negative Embedding](https://civitai.com/models/56519/negativehand-negative-embedding)**。这两个**Negative Embedding模型不会显著影响到胶片的质感。



## Momoko

> 下载地址：https://huggingface.co/ouo/momoko

标签：二次元、动漫

基于日本画家momoko的风格训练，出图质量好。



## nice

> 下载地址：https://huggingface.co/elontrump/nice/

标签：二次元、质感、厚涂、插画

一个拥有插画和真实质感的二次元模型，有点厚涂风格的感觉，出图质量很高，我个人超喜欢这个模型的风格。



例一，男性：

- Prompt

  ```
  (Alphonse Mucha),(((extremely detailed)))((masterpiece)), (((best quality))),,(((young))),((((pretty boy)))),
  masterpiece,high quality,chiaroscuro,glowing light,1 boy,solo,jwelry,green eyes,black eyes,long hair,earrings,circlet,portrait,close-up,art by wlop,{masterpiece},{best quality},{1boy},Amazing,beautiful detailed eyes,finely detail,Depth of field,extremely detailed CG,original, extremely detailed wallpaper,1boy,light pink hair,blue eyes,
  in garden,A floating point of light,((portrait)),bust,butterfly,arms behind back
  ```

  

- Negative prompt

	```
multiple_breasts,{mutated_hands_and_fingers},{long_body},{mutation,poorly_drawn},black-white,bad_anatomy,liquid_body,liquid_tongue,disfigured,malformed,mutated,anatomical_nonsense,text_font_ui,error,malformed_hands,long_neck,blurred,lowers,lowres,bad_anatomy,bad_proportions,bad_shadow,uncoordinated_body,unnatural_body,fused_breasts,bad_breasts,huge_breasts,poorly_drawn_breasts,extra_breasts,liquid_breasts,heavy_breasts,missing_breasts,huge_haunch,huge_thighs,huge_calf,bad_hands,fused_hand,missing_hand,disappearing_arms,disappearing_thigh,disappearing_calf,disappearing_legs,fused_ears,bad_ears,poorly_drawn_ears,extra_ears,liquid_ears,heavy_ears,missing_ears,fused_animal_ears,bad_animal_ears,poorly_drawn_animal_ears,extra_animal_ears,liquid_animal_ears,heavy_animal_ears,missing_animal_ears,text,ui,error,missing_fingers,missing_limb,fused_fingers,one_hand_with_more_than_5_fingers,one_hand_with_less_than_5_fingers,one_hand_with_more_than_5_digit,one_hand_with_less_than_5_digit,extra_digit,fewer_digits,fused_digit,missing_digit,bad_digit,liquid_digit,colorful_tongue,black_tongue,cropped,watermark,username,blurry,JPEG_artifacts,signature,3D,3D_game,3D_game_scene,3D_character,malformed_feet,extra_feet,bad_feet,poorly_drawn_feet,fused_feet,missing_feet,extra_shoes,bad_shoes,fused_shoes,more_than_two_shoes,poorly_drawn_shoes,bad_gloves,poorly_drawn_gloves,fused_gloves,bad_cum,poorly_drawn_cum,fused_cum,bad_hairs,poorly_drawn_hairs,fused_hairs,big_muscles,ugly,bad_face,fused_face,poorly_drawn_face,cloned_face,big_face,long_face,bad_eyes,fused_eyes_poorly_drawn_eyes,extra_eyes,malformed_limbs,more_than_2_nipples,missing_nipples,different_nipples,fused_nipples,bad_nipples,poorly_drawn_nipples,black_nipples,colorful_nipples,gross_proportions.short_arm,{{{missing_arms}}},missing_thighs,missing_calf,missing_legs,mutation,duplicate,morbid,mutilated,poorly_drawn_hands,more_than_1_left_hand,more_than_1_right_hand,deformed,{blurry},disfigured,missing_legs,extra_arms,extra_thighs,more_than_2_thighs,extra_calf,fused_calf,extra_legs,bad_knee,extra_knee,more_than_2_legs,bad_tails,bad_mouth,fused_mouth,poorly_drawn_mouth,bad_tongue,tongue_within_mouth,too_long_tongue,black_tongue,big_mouth,cracked_mouth,bad_mouth,dirty_face,dirty_teeth,dirty_pantie,fused_pantie,poorly_drawn_pantie,fused_cloth,poorly_drawn_cloth,bad_pantie,yellow_teeth,thick_lips,bad_cameltoe,colorful_cameltoe,bad_asshole,poorly_drawn_asshole,fused_asshole,missing_asshole,bad_anus,bad_pussy,bad_crotch,bad_crotch_seam,fused_anus,fused_pussy,fused_anus,fused_crotch,poorly_drawn_crotch,fused_seam,poorly_drawn_anus,poorly_drawn_pussy,poorly_drawn_crotch,poorly_drawn_crotch_seam,bad_thigh_gap,missing_thigh_gap,fused_thigh_gap,liquid_thigh_gap,poorly_drawn_thigh_gap,poorly_drawn_anus,bad_collarbone,fused_collarbone,missing_collarbone,liquid_collarbone,strong_girl,obesity,worst_quality,low_quality,normal_quality,liquid_tentacles,bad_tentacles,poorly_drawn_tentacles,split_tentacles,fused_tentacles,missing_clit,bad_clit,fused_clit,colorful_clit,black_clit,liquid_clit,QR_code,bar_code,censored,safety_panties,safety_knickers,beard,furry_,pony,pubic_hair,mosaic,excrement,faeces,shit,futa,testis
	```

- Steps: 48
- Sampler: Euler a
- CFG scale: 7
- Seed: 2085081368
- Size: 512x512
- Model hash: 2c13a9f1
- Clip skip: 2

![image-20230514223741907](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230514223749045.png)

例二，女性：

- Prompt

```
(Alphonse Mucha),(((extremely detailed)))((masterpiece)), (((best quality))),,(((young))),((((pretty girl)))),
masterpiece,high quality,chiaroscuro,glowing light,1 girl,solo,jwelry,green eyes,black eyes,long hair,earrings,circlet,portrait,close-up,art by wlop,{masterpiece},{best quality},{1girl},Amazing,beautiful detailed eyes,finely detail,Depth of field,extremely detailed CG,original, extremely detailed wallpaper,1girl,light pink hair,blue eyes,
in garden,A floating point of light,((portrait)),bust,butterfly,arms behind back
```

- Negative prompt

```
multiple_breasts,{mutated_hands_and_fingers},{long_body},{mutation,poorly_drawn}_,black-white,bad_anatomy,liquid_body,liquid_tongue,disfigured,malformed,mutated,anatomical_nonsense,text_font_ui,error,malformed_hands,long_neck,blurred,lowers,lowres,bad_anatomy,bad_proportions,bad_shadow,uncoordinated_body,unnatural_body,fused_breasts,bad_breasts,huge_breasts,poorly_drawn_breasts,extra_breasts,liquid_breasts,heavy_breasts,missing_breasts,huge_haunch,huge_thighs,huge_calf,bad_hands,fused_hand,missing_hand,disappearing_arms,disappearing_thigh,disappearing_calf,disappearing_legs,fused_ears,bad_ears,poorly_drawn_ears,extra_ears,liquid_ears,heavy_ears,missing_ears,fused_animal_ears,bad_animal_ears,poorly_drawn_animal_ears,extra_animal_ears,liquid_animal_ears,heavy_animal_ears,missing_animal_ears,text,ui,error,missing_fingers,missing_limb,fused_fingers,one_hand_with_more_than_5_fingers,one_hand_with_less_than_5_fingers,one_hand_with_more_than_5_digit,one_hand_with_less_than_5_digit,extra_digit,fewer_digits,fused_digit,missing_digit,bad_digit,liquid_digit,colorful_tongue,black_tongue,cropped,watermark,username,blurry,JPEG_artifacts,signature,3D,3D_game,3D_game_scene,3D_character,malformed_feet,extra_feet,bad_feet,poorly_drawn_feet,fused_feet,missing_feet,extra_shoes,bad_shoes,fused_shoes,more_than_two_shoes,poorly_drawn_shoes,bad_gloves,poorly_drawn_gloves,fused_gloves,bad_cum,poorly_drawn_cum,fused_cum,bad_hairs,poorly_drawn_hairs,fused_hairs,big_muscles,ugly,bad_face,fused_face,poorly_drawn_face,cloned_face,big_face,long_face,bad_eyes,fused_eyes_poorly_drawn_eyes,extra_eyes,malformed_limbs,more_than_2_nipples,missing_nipples,different_nipples,fused_nipples,bad_nipples,poorly_drawn_nipples,black_nipples,colorful_nipples,gross_proportions._short_arm,{{{missing_arms}}},missing_thighs,missing_calf,missing_legs,mutation,duplicate,morbid,mutilated,poorly_drawn_hands,more_than_1_left_hand,more_than_1_right_hand,deformed,{blurry},disfigured,missing_legs,extra_arms,extra_thighs,more_than_2_thighs,extra_calf,fused_calf,extra_legs,bad_knee,extra_knee,more_than_2_legs,bad_tails,bad_mouth,fused_mouth,poorly_drawn_mouth,bad_tongue,tongue_within_mouth,too_long_tongue,black_tongue,big_mouth,cracked_mouth,bad_mouth,dirty_face,dirty_teeth,dirty_pantie,fused_pantie,poorly_drawn_pantie,fused_cloth,poorly_drawn_cloth,bad_pantie,yellow_teeth,thick_lips,bad_cameltoe,colorful_cameltoe,bad_asshole,poorly_drawn_asshole,fused_asshole,missing_asshole,bad_anus,bad_pussy,bad_crotch,bad_crotch_seam,fused_anus,fused_pussy,fused_anus,fused_crotch,poorly_drawn_crotch,fused_seam,poorly_drawn_anus,poorly_drawn_pussy,poorly_drawn_crotch,poorly_drawn_crotch_seam,bad_thigh_gap,missing_thigh_gap,fused_thigh_gap,liquid_thigh_gap,poorly_drawn_thigh_gap,poorly_drawn_anus,bad_collarbone,fused_collarbone,missing_collarbone,liquid_collarbone,strong_girl,obesity,worst_quality,low_quality,normal_quality,liquid_tentacles,bad_tentacles,poorly_drawn_tentacles,split_tentacles,fused_tentacles,missing_clit,bad_clit,fused_clit,colorful_clit,black_clit,liquid_clit,QR_code,bar_code,censored,safety_panties,safety_knickers,beard,furry_,pony,pubic_hair,mosaic,excrement,faeces,shit,futa,testis
```
- Steps: 48,
- Sampler: Euler a
- CFG scale: 7
- Seed: 2085081368
- Size: 512x512
- Model hash: 2c13a9f1
- Clip skip: 2

![image-20230514223800579](https://raw.githubusercontent.com/wulilh/PicBed/main/img2023/20230514223800672.png)

## Redshift Diffusion V1

> 下载地址：https://huggingface.co/nitrosocke/redshift-diffusion

标签：3D渲染

基于Stable Diffusion微调的模型，使用高品质3D渲染作品训练而来，这个模型对关键词的要求也不高，很容易出好图。



## Midjourney V4

> 下载地址：https://huggingface.co/prompthero/openjourney/tree/main

标签： Midjourney

基于Midjourney v4 图片训练的Stable Diffusion微调模型，也有一些3D的感觉



## Dreamlike Diffusion V1

> 下载地址：https://huggingface.co/dreamlike-art/dreamlike-diffusion-1.0/tree/main

标签： high quality art、渲染、动漫

基于stable diffusion 1.5微调的高品质艺术模型。



## Inkpunk Diffusion V1

> 下载地址：https://huggingface.co/Envvi/Inkpunk-Diffusion

标签：dreambooth、朋克

使用dreambooth微调训练而来的模型，受到Gorillaz，FLCL和Yoji Shinkawa作品画风影响



## Studio Ghibli V4

> 下载地址：https://huggingface.co/IShallRiseAgain/StudioGhibli

标签：动漫、吉卜力

使用吉卜力动画工作室作品图片训练而来，个人感觉风格是像的，但是色彩上没有吉卜力那种特别明丽又治愈的感觉，有点偏暗。



## Xivcine Style V1

> 下载地址：https://huggingface.co/herpritts/FFXIV-Style

标签：动漫、游戏、最终幻想

使用MMORPG Final Fantasy XIV的预告片中图片训练的模型



## PaperCut V1

> 下载地址：https://huggingface.co/Fictiverse/Stable_Diffusion_PaperCut_Model

标签：剪纸、动画

使用剪纸图片训练而来，很有趣的一个模型。







