package com.tencent.mars.xlog;

import android.os.Looper;
import android.os.Process;

public class Log {

    private static final String TAG = "mars.xlog.log";

    public static final int LEVEL_VERBOSE = 0;
    public static final int LEVEL_DEBUG = 1;
    public static final int LEVEL_INFO = 2;
    public static final int LEVEL_WARNING = 3;
    public static final int LEVEL_ERROR = 4;
    public static final int LEVEL_FATAL = 5;
    public static final int LEVEL_NONE = 6;

    // defaults to LEVEL_NONE
    private static int level = LEVEL_NONE;

    public interface LogImp {

        void logV(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);

        void logI(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);

        void logD(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);

        void logW(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);

        void logE(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);

        void logF(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log);

        int getLogLevel();

        void appenderClose();

        void appenderFlush(boolean isSync);
    }

    private static final LogImp debugLog = new LogImp() {

        @Override
        public void logV(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
            if (level <= LEVEL_VERBOSE) {
                android.util.Log.v(tag, log);
            }
        }

        @Override
        public void logI(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
            if (level <= LEVEL_INFO) {
                android.util.Log.i(tag, log);
            }
        }

        @Override
        public void logD(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
            if (level <= LEVEL_DEBUG) {
                android.util.Log.d(tag, log);
            }
        }

        @Override
        public void logW(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
            if (level <= LEVEL_WARNING) {
                android.util.Log.w(tag, log);
            }
        }

        @Override
        public void logE(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, String log) {
            if (level <= LEVEL_ERROR) {
                android.util.Log.e(tag, log);
            }
        }

        @Override
        public void logF(String tag, String filename, String funcname, int line, int pid, long tid, long maintid, final String log) {
            if (level > LEVEL_FATAL) {
                return;
            }
            android.util.Log.e(tag, log);
        }

        @Override
        public int getLogLevel() {
            return level;
        }

        @Override
        public void appenderClose() {
        }

        @Override
        public void appenderFlush(boolean isSync) {
        }
    };

    private static LogImp logImp = debugLog;

    public static void setLogImp(LogImp imp) {
        logImp = imp;
    }

    public static LogImp getImpl() {
        return logImp;
    }

    public static void appenderClose() {
        if (logImp != null) {
            logImp.appenderClose();
        }
    }

    public static void appenderFlush(boolean isSync) {
        if (logImp != null) {
            logImp.appenderFlush(isSync);
        }
    }

    public static int getLogLevel() {
        if (logImp != null) {
            return logImp.getLogLevel();
        }
        return LEVEL_NONE;
    }

    public static void setLevel(final int level, final boolean jni) {
        Log.level = level;
        android.util.Log.w(TAG, "new log level: " + level);

        if (jni) {
            Xlog.setLogLevel(level);
            // android.util.Log.e(TAG, "no jni log level support");
        }
    }

    /**
     * use f(tag, format, obj) instead
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void f(final String tag, final String msg) {
        f(tag, msg, (Object[]) null);
    }

    /**
     * use e(tag, format, obj) instead
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void e(final String tag, final String msg) {
        e(tag, msg, (Object[]) null);
    }

    /**
     * use w(tag, format, obj) instead
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void w(final String tag, final String msg) {
        w(tag, msg, (Object[]) null);
    }

    /**
     * use i(tag, format, obj) instead
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void i(final String tag, final String msg) {
        i(tag, msg, (Object[]) null);
    }

    /**
     * use d(tag, format, obj) instead
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void d(final String tag, final String msg) {
        d(tag, msg, (Object[]) null);
    }

    /**
     * use v(tag, format, obj) instead
     *
     * @param tag 标签
     * @param msg 消息
     */
    public static void v(final String tag, final String msg) {
        v(tag, msg, (Object[]) null);
    }

    public static void f(String tag, final String format, final Object... obj) {
        if (logImp != null) {
            String log = obj == null ? format : String.format(format, obj);
            if (log == null) {
                log = "";
            }
            logImp.logF(tag, "", "", 0, Process.myPid(), Process.myTid(), Looper.getMainLooper().getThread().getId(), log);
        }
    }

    public static void e(String tag, final String format, final Object... obj) {
        if (logImp != null) {
            String log = obj == null ? format : String.format(format, obj);
            if (log == null) {
                log = "";
            }
            logImp.logE(tag, "", "", 0, Process.myPid(), Process.myTid(), Looper.getMainLooper().getThread().getId(), log);
        }
    }

    public static void w(String tag, final String format, final Object... obj) {
        if (logImp != null) {
            String log = obj == null ? format : String.format(format, obj);
            if (log == null) {
                log = "";
            }
            logImp.logW(tag, "", "", 0, Process.myPid(), Process.myTid(), Looper.getMainLooper().getThread().getId(), log);
        }
    }

    public static void i(String tag, final String format, final Object... obj) {
        if (logImp != null) {
            String log = obj == null ? format : String.format(format, obj);
            if (log == null) {
                log = "";
            }
            logImp.logI(tag, "", "", 0, Process.myPid(), Process.myTid(), Looper.getMainLooper().getThread().getId(), log);
        }
    }

    public static void d(String tag, final String format, final Object... obj) {
        if (logImp != null) {
            String log = obj == null ? format : String.format(format, obj);
            if (log == null) {
                log = "";
            }
            logImp.logD(tag, "", "", 0, Process.myPid(), Process.myTid(), Looper.getMainLooper().getThread().getId(), log);
        }
    }

    public static void v(String tag, final String format, final Object... obj) {
        if (logImp != null) {
            String log = obj == null ? format : String.format(format, obj);
            if (log == null) {
                log = "";
            }
            logImp.logV(tag, "", "", 0, Process.myPid(), Process.myTid(), Looper.getMainLooper().getThread().getId(), log);
        }
    }

    public static void printErrStackTrace(String tag, Throwable tr, final String format, final Object... obj) {
        if (logImp != null) {
            String log = obj == null ? format : String.format(format, obj);
            if (log == null) {
                log = "";
            }
            log += "  " + android.util.Log.getStackTraceString(tr);
            logImp.logE(tag, "", "", 0, Process.myPid(), Process.myTid(), Looper.getMainLooper().getThread().getId(), log);
        }
    }

    private static final String SYS_INFO;

    static {
        final StringBuilder sb = new StringBuilder();
        try {
            sb.append("VERSION.RELEASE:[").append(android.os.Build.VERSION.RELEASE);
            sb.append("] VERSION.CODENAME:[").append(android.os.Build.VERSION.CODENAME);
            sb.append("] VERSION.INCREMENTAL:[").append(android.os.Build.VERSION.INCREMENTAL);
            sb.append("] BOARD:[").append(android.os.Build.BOARD);
            sb.append("] DEVICE:[").append(android.os.Build.DEVICE);
            sb.append("] DISPLAY:[").append(android.os.Build.DISPLAY);
            sb.append("] FINGERPRINT:[").append(android.os.Build.FINGERPRINT);
            sb.append("] HOST:[").append(android.os.Build.HOST);
            sb.append("] MANUFACTURER:[").append(android.os.Build.MANUFACTURER);
            sb.append("] MODEL:[").append(android.os.Build.MODEL);
            sb.append("] PRODUCT:[").append(android.os.Build.PRODUCT);
            sb.append("] TAGS:[").append(android.os.Build.TAGS);
            sb.append("] TYPE:[").append(android.os.Build.TYPE);
            sb.append("] USER:[").append(android.os.Build.USER).append("]");
        } catch (Throwable e) {
            e.printStackTrace();
        }

        SYS_INFO = sb.toString();
    }

    public static String getSysInfo() {
        return SYS_INFO;
    }
}
