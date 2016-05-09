# Oh, you see!
> EasyEcard, also named **Oh, you see!**, is an open source app for <http://card.ouc.edu.cn> based on Android platform.

<center>
	<img src="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="icon"> 
</center>
“Oh, you see!” 是一款第三方开源应用，旨在帮助皇家每羊学院的师生更好地使用[校园卡电子服务平台](http://card.ouc.edu.cn)。能够实现在Android手机客户端查询校园卡余额、流水信息，发布和浏览丢卡、拾卡信息，挂失校园卡等功能。  
Demo下载地址：<https://www.pgyer.com/ohyousee>  
GitHub源码地址：[EasyEcard](https://github.com/SunGoodBoy/EasyEcard)  
当前版本：[v1.0.0](https://github.com/SunGoodBoy/EasyEcard/releases)
开源协议: [Apache License 2.0](https://github.com/SunGoodBoy/EasyEcard/)

<!-- more -->
## 应用截图
<figure class="eight">
	<a href="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/signin.png"><img src="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/signin.png" width="270" height="455" alt="登录界面"></a>
	<a href="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/manage.png"><img src="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/manage.png" width="270" height="455" alt="校园卡管理"></a>
	<a href="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/basic_information.png"><img src="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/basic_information.png" width="270" height="455" alt="基本信息"></a>
	<a href="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/trading_inquiry_list.png"><img src="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/trading_inquiry_list.png" width="270" height="455" alt="流水查询"></a>
	<a href="ttps://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/lostandfound.png"><img src="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/lostandfound.png" width="270" height="455" alt="丢失和拾获"></a>
	<a href="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/lost_found.png"><img src="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/lost_found.png" width="270" height="455" alt="浏览丢卡信息"></a>
	<a href="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/notice.png"><img src="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/notice.png" width="270" height="455" alt="消息通知"></a>
	<a href="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/settings.png"><img src="https://raw.githubusercontent.com/SunGoodBoy/EasyEcard/AS/screenshots/settings.png" width="270" height="455" alt="设置"></a>
</figure>

## Oh, you see! 的由来
大二时需要做[SRDP](http://baike.baidu.com/link?url=cXMSJtK4ZjXo1PIJQ8eUd85iFC-MxrRxS2DvZROZSw3BSJgLM9ntVcHwgiDWYV7JSkU2iRmLQzoSDTRkWJzYdK)拿创新创业学分，于是和同学商量，想做一个Android版的校园卡查询APP，实现通过手机查询校园卡余额、查询流水、挂失校园卡、失卡招领等功能。  

想法有了，说干就干。虽然成功立项了，但进展得并不顺利。一来组内的几个人都对Android开发一无所知，二是因为没办法接入学校的数据库。开始的一段时间，我们也就从[慕课网](http://www.imooc.com)学习了一些Android开发知识。中期时也就按照慕课网上的一个教程，做出了一个[类似微信6.0的主界面](https://github.com/SunGoodBoy/ChangeColorTab)。后来有一段时间太忙，还有一些乱七八糟的事搞得我很烦，这个项目也就基本处于搁置状态。直到2015年的国庆节假期期间，考虑了很多，还有最后两三个月的时间，我决定不管最后做成什么样，把这个项目结题。  

囿于自身能力，学习得也不到位，最后仅仅做出了一个本地数据库版的[Oh-you-see](https://github.com/SunGoodBoy/Oh-you-see)。可能是最后答辩时用手机演示得比较好吧，项目最终成功结题了，还意外地获得了优秀项目，啊哈哈（o(≧v≦)o~~）。

## Oh, you see! 的基本原理
但我不太甘心，因为没有实现我想要的效果——根本不能用啊！虽然拿学分只要能看就行了，但还是想做个能用的东西出来。于是自己便用业余时间继续对它的开发，将其作为自己的Side Project。  

为了实现预期的功能，在不能获得接入学校数据库的情况下，我尝试放弃通过和服务器直接交互的方式实现，转而通过学校的[校园卡电子服务平台](http://card.ouc.edu.cn/)获取数据。
>这些都源于我对超级课程表原理的考虑——我感觉超级课程表并没有获得各大高校的教务数据库，而是拿到用户提供的账号和密码进行访问学校网站，得到网页数据后进行解析的。并且我从[《android超级课程表原理》](http://blog.csdn.net/u010858238/article/details/9029653)这篇博客中得到了很多灵感，基本上我的APP就是按照这篇博客介绍的原理来实现的。 

SRDP结题后一周，我找到了在APP中登录学校网站的方法。这让我我坚信这种笨方法是可行的。于是我在APP中利用HttpClien对该网站进行模拟登录，然后利用Jsoup解析网站返回的数据，最后显示到Android设备，基本上实现了预期的功能。这种方式本质上与通过浏览器登录校园卡网站没有任何区别，只是在移动端的显示会更加美观、操作更为方便。但也要知道，这并不是一个真正的APP产品，这更像一个有很大弊端的小工具：只要网页改版APP就崩溃；获取Html文本比获取Json数据费流量。但通过这种方法确实实现了自己想要的功能，而且界面还算漂亮。更重要的是，自己从中受益良多。  


## 写在后面
因为学校已经有一个类似的官方[APP](http://card.ouc.edu.cn:8070/DownLoad/Main),在征求学校的建议后，这个应用不会在应用商店发布。但为了方便同学们交流学习，也为了响应开源的潮流，我开放了[源码](https://github.com/SunGoodBoy/EasyEcard)，并在蒲公英内测平台上放了一个[Demo](https://www.pgyer.com/ohyousee)。因为我并不是专业的Android的开发人员，所以这个APP出现一些问题也在所难免，希望发现问题的童鞋可以帮忙指出来。欢迎大家来下载试用、查阅源码、Star、提Issue、或者Fork。

## License
> Copyright 2015-2016 SunGoodBoy

> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at

>   http://www.apache.org/licenses/LICENSE-2.0

> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.