Quake Report App
===================================
·学完课程4：Preferences之后的版本

增加了设置里面以时间还是震级排序、震级的选择等
执行顺序是：
启动app时：
Earthquake Activity onCreate()
    -> LoaderManager的initLoader()
        -> onCreateLoader()
           uri:https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=magnitude&minmag=5&limit=10
           其中orderby=是根据设置变动的
                -> EarthQuakeLoader的onStartLoading()
                -> EarthQuakeLoader的loadInBackground()
                        -> fetchEarthquakeData()
        -> onLoadFinished()

如果点击设置： 
-> bindPreferenceSummaryToValue(), Preference: Minimum Magnitude:选择的震级
    -> onPreferenceChange()
-> bindPreferenceSummaryToValue(), Preference: Order By: 排序顺序
    -> onPreferenceChange()

点击了之后会再次执行一次onPreferenceChange()
点击返回会执行 -> onLoaderReset()
接着再从启动app时顺序开始执行

========================================================

·学习完课程3:线程与并行 之后的版本

增加了列表的空状态、检查网络连接状态、加载指使符等情况的处理

改用Loader之后重新启动的执行顺序是：
Earthquake Activity onCreate() 
            -> LoaderManager的initLoader() 
                            -> onCreateLoader()
                                    -> EarthQuakeLoader的onStartLoading()
                                    -> EarthQuakeLoader的loadInBackground()
                                            -> fetchEarthquakeData()
                            -> onLoadFinished()

旋转屏幕, 再旋转回来：
Earthquake Activity onCreate()
            -> LoaderManager的initLoader()   
                            -> onLoadFinished()

按下home键，再回到app:
    ->EarthQuakeLoader的onStartLoading()
    -> EarthQuakeLoader的loadInBackground()
            -> fetchEarthquakeData()
-> onLoadFinished()



This app displays a list of recent earthquakes in the world
from the U.S. Geological Survey (USGS) organization.

Used in a Udacity course in the Beginning Android Nanodegree.

More info on the USGS Earthquake API available at:
https://earthquake.usgs.gov/fdsnws/event/1/

Pre-requisites
--------------

- Android SDK v23
- Android Build Tools v23.0.2
- Android Support Repository v23.3.0

Getting Started
---------------

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

Support
-------

- Google+ Community: https://plus.google.com/communities/105153134372062985968
- Stack Overflow: http://stackoverflow.com/questions/tagged/android

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub. Please see CONTRIBUTING.md for more details.

License
-------

Copyright 2016 The Android Open Source Project, Inc.

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
