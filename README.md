# UberProgressView
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-UberProgressView-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/3567)

A simple progress animation developed after taking inspiration from the Uber app.

# Demo
![UberProgressView Demo](https://raw.githubusercontent.com/lazysource/uberprogressview/master/UberProgressViewDemo.gif)

# Download

Add this to your root `build.gradle` file
```
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```
Add this to your app module's `build.gradle` file
```
dependencies {
	        compile 'com.github.lazysource:uberprogressview:1.0.0'
	}
```

# Usage

In your Layout XML add this (all the app:.... attributes are optional and have default values

```
    <org.lazysource.uberprogressview.UberProgressView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:fading_circle_color="@android:color/holo_red_dark"
        app:stationary_circle_color="@android:color/holo_red_dark"
        app:orbiting_circle_color="@android:color/holo_red_dark"
        app:direction="counterclockwise"
        app:orbiting_circle_radius="6dp"
        app:stationary_circle_radius="12dp" />

```

| Property                | Description                                              | Format    | Default   |
|-------------------------|----------------------------------------------------------|-----------|-----------|
| fading_circle_color     | Color of the circle that grows and fades.                | color     | #29B6F6   |
| orbiting_circle_color   | Color of the circle that orbit around the central circle.| color     | #29B6F6   |
| stationary_circle_color | Color of the stationary circle in the center.            | color     | #29B6F6   |
| orbiting_circle_radius  | Radius of the orbiting circles.                          | dimension | 2dp       |
| stationary_circle_radius| Radius of the stationary circle in the center.           | dimension | 4dp       |
| direction               | Direction of rotation of outer dot                       | enum      | clockwise |


# Design Inspiration

Uber App

# Developers

* [Ishan Khanna](https://github.com/ishan1604)

# License

```
Copyright 2015 Ishan Khanna

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
