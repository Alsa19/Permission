# Permission
这是一个动态申请权限的示例，包含了各种可用方式。

## 版本
[![](https://jitpack.io/v/alsa20/Permission.svg)](https://jitpack.io/#alsa20/Permission)

## 使用
1. 在项目的build.gradle中加入如下代码：
```java
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
2. 在module的build.gradle中加入如下代码：
```java
dependencies {
	implementation 'com.github.alsa20:Permission:version'
}
```
