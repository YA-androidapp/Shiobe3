ShiobeForAndroidWear
====

## Overview
---

Twitter app for *Android* and *Android Wear*
[https://play.google.com/store/apps/details?id=jp.gr.java_conf.ya.shiobeforandroid3](https://play.google.com/store/apps/details?id=jp.gr.java_conf.ya.shiobeforandroid3)

## Install
---

Download the APK package from [github](https://github.com/YA-androidapp/Shiobe3/blob/master/mobile/mobile-release.apk?raw=true) ~~or [Google Play](https://play.google.com/store/apps/details?id=jp.gr.java_conf.ya.shiobeforandroid3)~~

### Wear:

1. cd "C:\android-sdk\platform-tools"
2. adb forward tcp:5555 localabstract:/adb-hub
3. adb connect localhost:5555
4. adb -s localhost:5555 install wear-release.apk

## Libraries
---

### [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt)

* [Twitter4J](http://twitter4j.org/)
* [twitter / twitter-text-java](https://github.com/twitter/twitter-text-java)
* [platform/frameworks/volley](https://android.googlesource.com/platform/frameworks/volley/)

## Licence
---

[Apache License, 2.0](http://www.apache.org/licenses/LICENSE-2.0)

## Author
---

[YA-androidapp](https://github.com/YA-androidapp)

---

Copyright (c) 2016 YA-androidapp(https://github.com/YA-androidapp) All rights reserved.
