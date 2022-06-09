package com.af.lib.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * 传统售货机、兑币机项目 UUID 生成方法
 *
 * @author: yss
 * @Date： 2020/11/24
 *
 * 1、根据设备硬件相关信息生成稳定设备唯一码
 * 2、根据设备唯一码通过标签组合成各不相同的 appId 与服务端通讯
 * 3、管理 app 只传设备 ID 即可，业务 app 需要传设备 ID、新旧 appId 三个值
 */
public class UUIDLegacy {

    private static final String TAG = "uuid";

    private UUIDLegacy() {

    }

    /**
     * 旧的获取 uuid 的方法处理，生成 32 位唯一数
     *
     * @param context 上下文
     * @param equipmentType 设备类型
     * @param serial 序列号
     * @param isFacePay 是否刷脸设备
     * @return uniqueId
     */
    public static String getOldAppUniqueId(Context context,
                                           String equipmentType,
                                           String serial,
                                           boolean isFacePay) {
        String param;
        if (isFacePay) {
            param = serial + equipmentType;
        } else {
            @SuppressLint("HardwareIds") String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            param = androidID + serial;
        }
        try {
            return toMD5(param);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 生成设备唯一码，作为设备标识且用于关联管理 app 与业务 app
     * 调用之前必须获取 READ_PHONE_STATE 权限 ，否则 IMEI 永远为空
     *
     * @param context 上下文
     * @param serial 序列号
     * @return deviceUniqueId
     */
    public static String getDeviceUniqueId(Context context, String serial) {
        // 根据设备信息生成的 uuid
        String deviceInfo = getDeviceInfo(serial);
        // imei 唯一序列号
        // 如果没有上网模块时为空
        // 为空时不需要拼接
        String imei = getIMEI(context);
        if (!TextUtils.isEmpty(imei)) {
            deviceInfo = deviceInfo + imei;
        }
        try {
            // 耗时 1ms
            return toMD5(deviceInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 通过硬件信息生成唯一值以供拼接
     *
     * @param serial 序列号
     * @return deviceInfo
     */
    private static String getDeviceInfo(String serial) {
        // 耗时 1ms
        String t = "35"
                + (Build.BOARD.length() % 10)
                + (Build.BRAND.length() % 10)
                + (Build.CPU_ABI.length() % 10)
                + (Build.DEVICE.length() % 10)
                + (Build.MANUFACTURER.length() % 10)
                + (Build.MODEL.length() % 10)
                + (Build.PRODUCT.length() % 10);
        return new UUID(t.hashCode(), serial.hashCode()).toString();
    }


    /**
     * 生成设备唯一码，作为设备标识且用于关联管理 app 与业务 app
     *
     * @param context 上下文
     * @return deviceUniqueId
     */
    public static String getDeviceUniqueId(Context context) {
        // 自定义设备 id
        String deviceId = getDeviceInfo();
        if (TextUtils.isEmpty(deviceId)) {
            // sn 为空
            // 不创建唯一码
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getIMEI(context));
        sb.append(deviceId);
        try {
            // 耗时 1ms
            return toMD5(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     * <p>
     * 注意：AndroidQ以上 android系统不开放提供IMEI值，暂无替代可返回固定值
     *
     * @param context 上下文
     * @return imei
     */
    @SuppressLint({"ObsoleteSdkInt", "MissingPermission", "HardwareIds"})
    public static String getIMEI(Context context) {
        // 耗时 13ms
        String imei = "imei";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            try {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    imei = telephonyManager.getDeviceId();
                } else {
                    Method method = telephonyManager.getClass().getMethod("getImei");
                    imei = (String) method.invoke(telephonyManager);
                }
            } catch (Exception e) {
                return imei;
            }
        }
        return imei;
    }


    /**
     * 获取设备业务 app 的唯一码
     * 总长度不允许超过 64 位
     *
     * @param deviceUnicode 32 位设备唯一码
     * @param equipmentType 不允许超过 32 位
     * @return appUniqueId
     */
    public static String getAppUniqueId(String deviceUnicode, String equipmentType) {

        return deviceUnicode + equipmentType;
    }


    /**
     * 获取新版 app 唯一 id
     * IMEI + 设备 id + 业务类型
     * 返回值 "" 与 null 不同会导致唯一值变化
     * 请勿随意修改
     *
     * @param context 上下文
     * @param businessType 业务类型
     * @return appUniqueId
     */
    public static String getAppUniqueId(Context context, String businessType) {

        String deviceId = getDeviceUniqueId(context);
        if (TextUtils.isEmpty(deviceId)) {
            // sn 为空
            // 不创建唯一码
            return "";
        }
        return deviceId + businessType;
    }


    /**
     * 获取设备 SN 号
     *
     * 多次无法获取 SN 号可能是系统故障，尝试多次获取
     *
     * @return 序列号
     */
    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getSerial() {

        String serial;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serial = Build.getSerial();
        } else {
            serial = Build.SERIAL;
        }

        if (TextUtils.isEmpty(serial) || Build.UNKNOWN.equals(serial)) {
            String[] properties = {"ro.serialno", "ro.boot.serialno", "ro.serialno", "ro.boot.serialno"};
            for (String key : properties) {
                // 通过反射反复获取
                serial = getAndroidOsSystemProperties(key);
                if (!TextUtils.isEmpty(serial) && !Build.UNKNOWN.equals(serial)) {
                    break;
                }
                // 如果第一次循环取值失败，等待 50ms 再试
                SystemClock.sleep(50);
            }
        }
        return serial;
    }

    /**
     * 主板型号
     *
     * @return 主板型号
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }


    /**
     * 通过硬件信息生成唯一值以供拼接
     *
     * @return deviceInfo
     */
    private static String getDeviceInfo() {
        // 耗时 1ms
        String t = "35"
                + (Build.BOARD.length() % 10)
                + (Build.BRAND.length() % 10)
                + (Build.CPU_ABI.length() % 10)
                + (Build.DEVICE.length() % 10)
                + (Build.MANUFACTURER.length() % 10)
                + (Build.MODEL.length() % 10)
                + (Build.PRODUCT.length() % 10);

        String serial = getSerial();
        if (TextUtils.isEmpty(serial) || Build.UNKNOWN.equals(serial)) {
            return "";
        }
        return new UUID(t.hashCode(), serial.hashCode()).toString();
    }


    /**
     * 获取MAC值
     * 参考文章 ：https://blog.csdn.net/doris_d/article/details/102933564
     * <p>
     * 注意：
     * Android 10 以上系统随机分配 MAC 不可作为参数
     * android  7 以上设备上从未启用 WLAN，则此方法将返回一个 null 值
     * 因此 MAC 作为参数只适用 6.0 及以下
     * <p>
     * 通过 linux 底层获取 mac 方法存在一定概率失败
     * <p>
     * 与产品探讨后，决定去掉 MAC 值作为唯一码生成参数
     *
     * @return mac
     */
    public static String getMac() {
        // 耗时 1ms
        try {
            return loadFileAsString("/sys/class/net/eth0/address").toUpperCase().substring(0, 17);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 通过本地 ip 获取 mac 地址
     *
     * @return macAddress
     */
    private String getLocalMacAddressFromIp() {

        String macAddress = "";
        try {
            String ip = getLocalIpAddress();
            InetAddress ipAddress = InetAddress.getByName(ip);
            NetworkInterface ne = NetworkInterface.getByInetAddress(ipAddress);
            byte[] mac = ne.getHardwareAddress();
            if (mac.length > 0) {
                macAddress = byte2mac(mac);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return macAddress;
    }

    private String byte2mac(byte[] bytes) {

        StringBuffer sb = new StringBuffer(bytes.length);
        String temp;
        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 0xFF);
            if (temp.length() == 1) {
                sb = sb.append("0").append(temp);
            } else {
                sb = sb.append(temp);
            }
        }
        StringBuilder mac = new StringBuilder(sb);
        for (int i = 0; i < mac.length(); i++) {
            if (i % 3 == 0) {
                mac.insert(i, ':');
            }
        }
        return mac.substring(1);
    }

    /**
     * 获取本地 IP
     *
     * @return ip
     */
    private String getLocalIpAddress() {
        try {
            String ipv4;
            List<NetworkInterface> niList = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : niList) {
                List<InetAddress> iaList = Collections.list(ni.getInetAddresses());
                for (InetAddress address : iaList) {
                    ipv4 = address.getHostAddress();
                    if (!address.isLoopbackAddress() && null != ipv4 && !ipv4.contains(":")) {
                        return ipv4;
                    }
                }
            }
        } catch (SocketException exception) {
            exception.printStackTrace();
        }
        return "0.0.0.0";
    }


    /**
     * 通过 Linux 底层的方法获取以太网地址
     *
     * @return ethernet mac
     */
    public static String getLocalEthernetMac() {

        BufferedReader reader = null;
        String ethernetMac = "";
        try {
            reader = new BufferedReader(new FileReader("sys/class/net/eth0/address"));
            ethernetMac = reader.readLine();
            Log.d(TAG, "open sys/class/net/eth0/address success: " + ethernetMac);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "open sys/class/net/eth0/address failed: " + e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "close sys/class/net/eth0/address failed: " + e);
            }
        }
        return ethernetMac;
    }


    /**
     * 获取系统 android.os.SystemProperties 值通用方法
     *
     * @param key 键值
     * @return value
     */
    @SuppressLint("PrivateApi")
    static String getAndroidOsSystemProperties(String key) {
        Method get;
        String value;
        try {
            get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
            if ((value = (String) get.invoke(null, key)) != null) {
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }


    private static String loadFileAsString(String fileName) throws Exception {

        FileReader reader = new FileReader(fileName);
        String text = loadReaderAsString(reader);
        reader.close();
        return text;
    }

    private static String loadReaderAsString(Reader reader) throws Exception {

        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[4096];
        int readLength = reader.read(buffer);
        while (readLength >= 0) {
            builder.append(buffer, 0, readLength);
            readLength = reader.read(buffer);
        }
        return builder.toString();
    }

    @SuppressLint("ObsoleteSdkInt")
    public static String toMD5(String text) throws Exception {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        // 获取摘要器 MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        // 通过摘要器对字符串的二进制字节数组进行 hash 计算
        byte[] digest;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            digest = messageDigest.digest(text.getBytes(StandardCharsets.UTF_8));
        } else {
            digest = messageDigest.digest(text.getBytes("UTF-8"));
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            // 循环每个字符
            // 将计算结果转化为正整数
            int digestInt = b & 0xff;
            // 将 10 进制转化为较短的 16 进制
            String hexString = Integer.toHexString(digestInt);
            // 转化结果如果是个位数会省略 0
            // 因此判断并补 0
            if (hexString.length() < 2) {
                sb.append(0);
            }
            // 将循环结果添加到缓冲区
            sb.append(hexString);
        }
        // 返回整个结果
        return sb.toString();
    }
}
