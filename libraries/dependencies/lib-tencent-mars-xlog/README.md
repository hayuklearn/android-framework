# Tencent Mars Xlog 编译

1. 选择编译平台

修改 mars 下 build_android.py

```archs = set(['armeabi-v7a','arm64-v8a'])```

[参考](https://blog.csdn.net/q610098308/article/details/104973276)

2. 修改日志打印样式

```mars\log\src\appender.cc```

```mars\log\src\formater.cc```

[参考](https://blog.csdn.net/EthanCo/article/details/104378841)

