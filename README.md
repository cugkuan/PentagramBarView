
[![](https://jitpack.io/v/cugkuan/PentagramBarView.svg)](https://jitpack.io/#cugkuan/PentagramBarView)


# PentagramBarView

五角星进度条。

在使用过程中，有什么问题，发邮件到 cugkuan@163.com

# 使用
## 添加依赖的地址

**根据是否使用了AndroidX选择不同的依赖版本**

如果使用AndroidX，那么
```
implementation 'com.github.cugkuan:PentagramBarView:1.2'  
```
**没有使用AndroidX,那么**
```
implementation 'com.github.cugkuan:PentagramBarView:1.02'  
```

## 使用代码
请注意：
> 一个五角星，是无法用一个正方形“装”的，因此，在使用的时候，如果宽度和高度设置一致，那个五角星将绘制不全。使用的时候建议最好不要固定宽度和高度，你只需指定一个宽度或者高度，另外的宽度或者高度通过计算得到。
```
 <com.cugkuan.widget.PentagramBarView
            android:layout_width="60dp"
            app:fillColor="@android:color/holo_red_dark"
            app:progress="50"
            app:max="100"
            app:lineWidth="2dp"
            app:lineColor="@android:color/black"
            app:progressColor="@color/colorPrimary"
            android:layout_height="wrap_content" />

```

# 使用属性说明

|属性|说明|备注|
|-----|----|----|
|max|最大进度|默认的是100|
|progress|当前进度|0~max|
|CrRatio|五角星内切圆与外接圆的半径的比率；值越大，五角星就越“胖”，反之就越“瘦”，默认的是正五角星的比率| |
|fillColor|五角星填充的颜色| |
|progressColor|五角星进度条填充颜色| |
|lineColor|五角星线条颜色||
|lineWidth|五角星线条的宽度|如果不显示线条，设置为0|
|measure_style|WIDTH:以宽度为测量标准计算高度。<br>HEIGHT:以高度为计算标准，计算宽度| |

## demo效果
![image](image/WechatIMG1.png)


# 补充知识
关于五角星的角度，内切圆，外接圆的计算；需要你就有数学功底，不过，初中的数学知识就够用了。下面的一张图，是网上找的，在计算的时候，会有用。


![image](https://camo.githubusercontent.com/4890aa0cbf8aa07d04212bbfea92df09e15ac68e/68747470733a2f2f75706c6f61642d696d616765732e6a69616e7368752e696f2f75706c6f61645f696d616765732f393431343334342d646566666233343861353266616138382e6a70673f696d6167654d6f6772322f6175746f2d6f7269656e742f7374726970253743696d61676556696577322f322f772f31323430)
