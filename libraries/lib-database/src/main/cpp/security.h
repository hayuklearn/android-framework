#include <android/log.h>

#ifndef OPENSSL_SECURITY_H
#define OPENSSL_SECURITY_H
#endif // OPENSSL_SECURITY_H

// 导入 android 日志

#define TAG "security"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL, TAG ,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,  TAG ,__VA_ARGS__)

// APK 签名 hash1
const char *APK_HASH1 = "76778217DECC3B448F1CDDF613F418165A564D4B";
const char digest[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

// 定义是否验证签名信息，如果默认为 false，调用 jni 函数前需要验证签名，如果签名验证失败，调用 jni 函数时抛出异常
#define BOOL int
#define TRUE 1
#define FALSE 0
BOOL IS_SIGNED_FLAG = TRUE;