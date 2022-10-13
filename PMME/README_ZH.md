中文 | [English]() 
## 目录
 * [概述](#概述)
 * [安装](#安装)
 * [环境要求](#环境要求)
 * [快速入门](#快速入门)
 * [开源协议](#开源协议)


## 概述

游友乐园游戏示例代码集成了华为游戏服务的登录和华为游戏多媒体实时语音等接口能力，提供了示例代码程序供您参考和使用。

该示例也可以通过HMS Toolkit快速启动运行，且支持各Kit一站式集成，并提供远程真机免费调测等功能。了解更多信息，请参考HMS Toolkit官方链接：https://developer.huawei.com/consumer/cn/doc/development/Tools-Guides/getting-started-0000001077381096

## 安装

在使用示例代码之前，检查Android Studio开发环境是否准备就绪。在Android Studio中打开示例代码，在安装有最新版本的HMS（华为移动服务）的手机或者模拟器上运行。

## 环境要求

建议使用19或更高的安卓SDK版本。

##快速入门

   1、检查Android Studio开发环境是否已准备好。在安装了最新华为移动服务（HMS）的设备上运行应用程序。
   
   2、注册【华为帐号】（https://developer.huawei.com/consumer/cn/）。
   
   3、创建应用，并在AppGallery Connect中配置应用信息。
   
   详细内容请参见：[HUAWEI Game Service Development Preparation](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/config-agc-0000001281025916)
   
   4.开发服务权益，在AppGallery Connect控制台打开相关服务开关。
   
   详细内容参见：https://developer.huawei.com/consumer/cn/doc/development/AppGallery-connect-Guides/gamemme-enable-0000001327201553
   
   5.要构建此demo，请首先在Android Studio (3.x+)中导入该demo。
   
   6、配置示例代码：
   
   (1)在AGC上下载应用的文件“agconnect-services.json”，并将该文件添加到示例工程的应用根目录（\app）中。
   
   (2)将示例工程的应用级build.gradle文件中的applicationid修改为您的应用的包名。
   
   (3)在示例工程里配置签名并在AGC后台配置签名证书指纹。
   
   (4)记录您自己游戏的appId、clientId、clientSecret、apiKey替换示例代码中ConfigFile中的值。
   
   7.在Android设备上运行示例。

##  开源协议
  Demo示例代码遵循以下开源协议: [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

  更多SDK开发指南，请点击以下链接：
  [Devlopment Guide](https://developer.huawei.com/consumer/cn/doc/development/AppGallery-connect-Guides/gamemme-integratingsdk-android-0000001250838246)
  [API](https://developer.huawei.com/consumer/cn/doc/development/AppGallery-connect-References/packagesummary-0000001255650673)
