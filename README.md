# Android-PulltoRefresh
PulltoRefreshView for Android

Reference to [Android-PullToRefresh](https://github.com/chrisbanes/Android-PullToRefresh) by Chris Banes

## Import

This repo has been added to JCenter.

##### JitPack
```code
//root build.gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
        implementation 'com.github.arjinmc:Android-PulltoRefresh:2.1.1'
}
```
##### maven

```code
<dependency>
  <groupId>com.arjinmc.android</groupId>
  <artifactId>pulltorefresh</artifactId>
  <version>2.1.1</version>
  <type>pom</type>
</dependency>
```

##### gradle

```code
compile 'com.arjinmc.android:pulltorefresh:2.1.1'
```

## Features
Supports both Pulling Down from the top, and Pulling Up from the bottom (or even both).

- [x] RecyclerView
- [x] ScrollView
- [x] HorizontalScrollView
- [x] NestedScrollView

![image](https://github.com/arjinmc/Android-PulltoRefresh/blob/master/images/sample.gif)
```code
   Copyright 2018 arjinmc

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
