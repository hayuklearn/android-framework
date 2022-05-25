// package com.af.lib.utils;
//
// import android.Manifest;
// import android.app.Application;
// import android.content.Context;
// import android.text.TextUtils;
// import android.view.View;
//
// import androidx.annotation.NonNull;
// import androidx.annotation.Nullable;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.Objects;
// import java.util.stream.Collectors;
//
// /**
//  * Information about the current build, extracted from system properties.
//  */
// public class Build {
//     private static final String TAG = "Build";
//
//     /** Value used for when a build property is unknown. */
//     public static final String UNKNOWN = "unknown";
//
//     /** Either a changelist number, or a label like "M4-rc20". */
//     public static final String ID = getString("ro.build.id");
//
//     /** A build ID string meant for displaying to the user */
//     public static final String DISPLAY = getString("ro.build.display.id");
//
//     /** The name of the overall product. */
//     public static final String PRODUCT = getString("ro.product.name");
//
//     /** The name of the industrial design. */
//     public static final String DEVICE = getString("ro.product.device");
//
//     /** The name of the underlying board, like "goldfish". */
//     public static final String BOARD = getString("ro.product.board");
//
//     /**
//      * The name of the instruction set (CPU type + ABI convention) of native code.
//      *
//      * @deprecated Use {@link #SUPPORTED_ABIS} instead.
//      */
//     @Deprecated
//     public static final String CPU_ABI;
//
//     /**
//      * The name of the second instruction set (CPU type + ABI convention) of native code.
//      *
//      * @deprecated Use {@link #SUPPORTED_ABIS} instead.
//      */
//     @Deprecated
//     public static final String CPU_ABI2;
//
//     /** The manufacturer of the product/hardware. */
//     public static final String MANUFACTURER = getString("ro.product.manufacturer");
//
//     /** The consumer-visible brand with which the product/hardware will be associated, if any. */
//     public static final String BRAND = getString("ro.product.brand");
//
//     /** The end-user-visible name for the end product. */
//     public static final String MODEL = getString("ro.product.model");
//
//     /** The manufacturer of the device's primary system-on-chip. */
//     @NonNull
//     public static final String SOC_MANUFACTURER = SocProperties.soc_manufacturer().orElse(UNKNOWN);
//
//     /** The model name of the device's primary system-on-chip. */
//     @NonNull
//     public static final String SOC_MODEL = SocProperties.soc_model().orElse(UNKNOWN);
//
//     /** The system bootloader version number. */
//     public static final String BOOTLOADER = getString("ro.bootloader");
//
//     /**
//      * The radio firmware version number.
//      *
//      * @deprecated The radio firmware version is frequently not
//      * available when this class is initialized, leading to a blank or
//      * "unknown" value for this string.  Use
//      * {@link #getRadioVersion} instead.
//      */
//     @Deprecated
//     public static final String RADIO = joinListOrElse(TelephonyProperties.baseband_version(), UNKNOWN);
//
//     /** The name of the hardware (from the kernel command line or /proc). */
//     public static final String HARDWARE = getString("ro.hardware");
//
//     /**
//      * The SKU of the hardware (from the kernel command line).
//      *
//      * <p>The SKU is reported by the bootloader to configure system software features.
//      * If no value is supplied by the bootloader, this is reported as {@link #UNKNOWN}.
//
//      */
//     @NonNull
//     public static final String SKU = getString("ro.boot.hardware.sku");
//
//     /**
//      * The SKU of the device as set by the original design manufacturer (ODM).
//      *
//      * <p>This is a runtime-initialized property set during startup to configure device
//      * services. If no value is set, this is reported as {@link #UNKNOWN}.
//      *
//      * <p>The ODM SKU may have multiple variants for the same system SKU in case a manufacturer
//      * produces variants of the same design. For example, the same build may be released with
//      * variations in physical keyboard and/or display hardware, each with a different ODM SKU.
//      */
//     @NonNull
//     public static final String ODM_SKU = getString("ro.boot.product.hardware.sku");
//
//     /**
//      * Whether this build was for an emulator device.
//      * @hide
//      */
//     @UnsupportedAppUsage
//     @TestApi
//     public static final boolean IS_EMULATOR = getString("ro.boot.qemu").equals("1");
//
//     /**
//      * A hardware serial number, if available. Alphanumeric only, case-insensitive.
//      * This field is always set to {@link Build#UNKNOWN}.
//      *
//      * @deprecated Use {@link #getSerial()} instead.
//      **/
//     @Deprecated
//     // IMPORTANT: This field should be initialized via a function call to
//     // prevent its value being inlined in the app during compilation because
//     // we will later set it to the value based on the app's target SDK.
//     public static final String SERIAL = getString("no.such.thing");
//
//     /**
//      * Gets the hardware serial number, if available.
//      *
//      * <p class="note"><b>Note:</b> Root access may allow you to modify device identifiers, such as
//      * the hardware serial number. If you change these identifiers, you can use
//      * <a href="/training/articles/security-key-attestation.html">key attestation</a> to obtain
//      * proof of the device's original identifiers.
//      *
//      * <p>Starting with API level 29, persistent device identifiers are guarded behind additional
//      * restrictions, and apps are recommended to use resettable identifiers (see <a
//      * href="/training/articles/user-data-ids">Best practices for unique identifiers</a>). This
//      * method can be invoked if one of the following requirements is met:
//      * <ul>
//      *     <li>If the calling app has been granted the READ_PRIVILEGED_PHONE_STATE permission; this
//      *     is a privileged permission that can only be granted to apps preloaded on the device.
//      *     <li>If the calling app has carrier privileges (see {@link
//      *     android.telephony.TelephonyManager#hasCarrierPrivileges}) on any active subscription.
//      *     <li>If the calling app is the default SMS role holder (see {@link
//      *     android.app.role.RoleManager#isRoleHeld(String)}).
//      *     <li>If the calling app is the device owner of a fully-managed device, a profile
//      *     owner of an organization-owned device, or their delegates (see {@link
//      *     android.app.admin.DevicePolicyManager#getEnrollmentSpecificId()}).
//      * </ul>
//      *
//      * <p>If the calling app does not meet one of these requirements then this method will behave
//      * as follows:
//      *
//      * <ul>
//      *     <li>If the calling app's target SDK is API level 28 or lower and the app has the
//      *     READ_PHONE_STATE permission then {@link Build#UNKNOWN} is returned.</li>
//      *     <li>If the calling app's target SDK is API level 28 or lower and the app does not have
//      *     the READ_PHONE_STATE permission, or if the calling app is targeting API level 29 or
//      *     higher, then a SecurityException is thrown.</li>
//      * </ul>
//      *
//      * @return The serial number if specified.
//      */
//     @SuppressAutoDoc // No support for device / profile owner.
//     @RequiresPermission(Manifest.permission.READ_PRIVILEGED_PHONE_STATE)
//     public static String getSerial() {
//         IDeviceIdentifiersPolicyService service = IDeviceIdentifiersPolicyService.Stub
//                 .asInterface(ServiceManager.getService(Context.DEVICE_IDENTIFIERS_SERVICE));
//         try {
//             Application application = ActivityThread.currentApplication();
//             String callingPackage = application != null ? application.getPackageName() : null;
//             return service.getSerialForPackage(callingPackage, null);
//         } catch (RemoteException e) {
//             e.rethrowFromSystemServer();
//         }
//         return UNKNOWN;
//     }
//
//     /**
//      * An ordered list of ABIs supported by this device. The most preferred ABI is the first
//      * element in the list.
//      *
//      * See {@link #SUPPORTED_32_BIT_ABIS} and {@link #SUPPORTED_64_BIT_ABIS}.
//      */
//     public static final String[] SUPPORTED_ABIS = getStringList("ro.product.cpu.abilist", ",");
//
//     /**
//      * An ordered list of <b>32 bit</b> ABIs supported by this device. The most preferred ABI
//      * is the first element in the list.
//      *
//      * See {@link #SUPPORTED_ABIS} and {@link #SUPPORTED_64_BIT_ABIS}.
//      */
//     public static final String[] SUPPORTED_32_BIT_ABIS =
//             getStringList("ro.product.cpu.abilist32", ",");
//
//     /**
//      * An ordered list of <b>64 bit</b> ABIs supported by this device. The most preferred ABI
//      * is the first element in the list.
//      *
//      * See {@link #SUPPORTED_ABIS} and {@link #SUPPORTED_32_BIT_ABIS}.
//      */
//     public static final String[] SUPPORTED_64_BIT_ABIS =
//             getStringList("ro.product.cpu.abilist64", ",");
//
//     /** {@hide} */
//     @TestApi
//     public static boolean is64BitAbi(String abi) {
//         return VMRuntime.is64BitAbi(abi);
//     }
//
//     static {
//         /*
//          * Adjusts CPU_ABI and CPU_ABI2 depending on whether or not a given process is 64 bit.
//          * 32 bit processes will always see 32 bit ABIs in these fields for backward
//          * compatibility.
//          */
//         final String[] abiList;
//         if (VMRuntime.getRuntime().is64Bit()) {
//             abiList = SUPPORTED_64_BIT_ABIS;
//         } else {
//             abiList = SUPPORTED_32_BIT_ABIS;
//         }
//
//         CPU_ABI = abiList[0];
//         if (abiList.length > 1) {
//             CPU_ABI2 = abiList[1];
//         } else {
//             CPU_ABI2 = "";
//         }
//     }
//
//     /** The type of build, like "user" or "eng". */
//     public static final String TYPE = getString("ro.build.type");
//
//     /** Comma-separated tags describing the build, like "unsigned,debug". */
//     public static final String TAGS = getString("ro.build.tags");
//
//     /** A string that uniquely identifies this build.  Do not attempt to parse this value. */
//     public static final String FINGERPRINT = deriveFingerprint();
//
//     /**
//      * Some devices split the fingerprint components between multiple
//      * partitions, so we might derive the fingerprint at runtime.
//      */
//     private static String deriveFingerprint() {
//         String finger = SystemProperties.get("ro.build.fingerprint");
//         if (TextUtils.isEmpty(finger)) {
//             finger = getString("ro.product.brand") + '/' +
//                     getString("ro.product.name") + '/' +
//                     getString("ro.product.device") + ':' +
//                     getString("ro.build.version.release") + '/' +
//                     getString("ro.build.id") + '/' +
//                     getString("ro.build.version.incremental") + ':' +
//                     getString("ro.build.type") + '/' +
//                     getString("ro.build.tags");
//         }
//         return finger;
//     }
//
//     /**
//      * Ensure that raw fingerprint system property is defined. If it was derived
//      * dynamically by {@link #deriveFingerprint()} this is where we push the
//      * derived value into the property service.
//      *
//      * @hide
//      */
//     public static void ensureFingerprintProperty() {
//         if (TextUtils.isEmpty(SystemProperties.get("ro.build.fingerprint"))) {
//             try {
//                 SystemProperties.set("ro.build.fingerprint", FINGERPRINT);
//             } catch (IllegalArgumentException e) {
//                 Slog.e(TAG, "Failed to set fingerprint property", e);
//             }
//         }
//     }
//
//     /**
//      * A multiplier for various timeouts on the system.
//      *
//      * The intent is that products targeting software emulators that are orders of magnitude slower
//      * than real hardware may set this to a large number. On real devices and hardware-accelerated
//      * virtualized devices this should not be set.
//      *
//      * @hide
//      */
//     public static final int HW_TIMEOUT_MULTIPLIER =
//             SystemProperties.getInt("ro.hw_timeout_multiplier", 1);
//
//     /**
//      * True if Treble is enabled and required for this device.
//      *
//      * @hide
//      */
//     public static final boolean IS_TREBLE_ENABLED =
//             SystemProperties.getBoolean("ro.treble.enabled", false);
//
//     /**
//      * Verifies the current flash of the device is consistent with what
//      * was expected at build time.
//      *
//      * Treble devices will verify the Vendor Interface (VINTF). A device
//      * launched without Treble:
//      *
//      * 1) Checks that device fingerprint is defined and that it matches across
//      *    various partitions.
//      * 2) Verifies radio and bootloader partitions are those expected in the build.
//      *
//      * @hide
//      */
//     public static boolean isBuildConsistent() {
//         // Don't care on eng builds.  Incremental build may trigger false negative.
//         if (IS_ENG) return true;
//
//         if (IS_TREBLE_ENABLED) {
//             // If we can run this code, the device should already pass AVB.
//             // So, we don't need to check AVB here.
//             int result = VintfObject.verifyWithoutAvb();
//
//             if (result != 0) {
//                 Slog.e(TAG, "Vendor interface is incompatible, error="
//                         + String.valueOf(result));
//             }
//
//             return result == 0;
//         }
//
//         final String system = SystemProperties.get("ro.system.build.fingerprint");
//         final String vendor = SystemProperties.get("ro.vendor.build.fingerprint");
//         final String bootimage = SystemProperties.get("ro.bootimage.build.fingerprint");
//         final String requiredBootloader = SystemProperties.get("ro.build.expect.bootloader");
//         final String currentBootloader = SystemProperties.get("ro.bootloader");
//         final String requiredRadio = SystemProperties.get("ro.build.expect.baseband");
//         final String currentRadio = joinListOrElse(
//                 TelephonyProperties.baseband_version(), "");
//
//         if (TextUtils.isEmpty(system)) {
//             Slog.e(TAG, "Required ro.system.build.fingerprint is empty!");
//             return false;
//         }
//
//         if (!TextUtils.isEmpty(vendor)) {
//             if (!Objects.equals(system, vendor)) {
//                 Slog.e(TAG, "Mismatched fingerprints; system reported " + system
//                         + " but vendor reported " + vendor);
//                 return false;
//             }
//         }
//         return true;
//     }
//
//     /** Build information for a particular device partition. */
//     public static class Partition {
//         /** The name identifying the system partition. */
//         public static final String PARTITION_NAME_SYSTEM = "system";
//
//         private final String mName;
//         private final String mFingerprint;
//         private final long mTimeMs;
//
//         private Partition(String name, String fingerprint, long timeMs) {
//             mName = name;
//             mFingerprint = fingerprint;
//             mTimeMs = timeMs;
//         }
//
//         /** The name of this partition, e.g. "system", or "vendor" */
//         @NonNull
//         public String getName() {
//             return mName;
//         }
//
//         /** The build fingerprint of this partition, see {@link Build#FINGERPRINT}. */
//         @NonNull
//         public String getFingerprint() {
//             return mFingerprint;
//         }
//
//         /** The time (ms since epoch), at which this partition was built, see {@link Build#TIME}. */
//         public long getBuildTimeMillis() {
//             return mTimeMs;
//         }
//
//         @Override
//         public boolean equals(@Nullable Object o) {
//             if (!(o instanceof Partition)) {
//                 return false;
//             }
//             Partition op = (Partition) o;
//             return mName.equals(op.mName)
//                     && mFingerprint.equals(op.mFingerprint)
//                     && mTimeMs == op.mTimeMs;
//         }
//
//         @Override
//         public int hashCode() {
//             return Objects.hash(mName, mFingerprint, mTimeMs);
//         }
//     }
//
//     /**
//      * Get build information about partitions that have a separate fingerprint defined.
//      *
//      * The list includes partitions that are suitable candidates for over-the-air updates. This is
//      * not an exhaustive list of partitions on the device.
//      */
//     @NonNull
//     public static List<Partition> getFingerprintedPartitions() {
//         ArrayList<Partition> partitions = new ArrayList<>();
//
//         String[] names = new String[] {
//                 "bootimage", "odm", "product", "system_ext", Partition.PARTITION_NAME_SYSTEM,
//                 "vendor"
//         };
//         for (String name : names) {
//             String fingerprint = SystemProperties.get("ro." + name + ".build.fingerprint");
//             if (TextUtils.isEmpty(fingerprint)) {
//                 continue;
//             }
//             long time = getLong("ro." + name + ".build.date.utc") * 1000;
//             partitions.add(new Partition(name, fingerprint, time));
//         }
//
//         return partitions;
//     }
//
//     // The following properties only make sense for internal engineering builds.
//
//     /** The time at which the build was produced, given in milliseconds since the UNIX epoch. */
//     public static final long TIME = getLong("ro.build.date.utc") * 1000;
//     public static final String USER = getString("ro.build.user");
//     public static final String HOST = getString("ro.build.host");
//
//     /**
//      * Returns true if the device is running a debuggable build such as "userdebug" or "eng".
//      *
//      * Debuggable builds allow users to gain root access via local shell, attach debuggers to any
//      * application regardless of whether they have the "debuggable" attribute set, or downgrade
//      * selinux into "permissive" mode in particular.
//      */
//     public static final boolean IS_DEBUGGABLE =
//             SystemProperties.getInt("ro.debuggable", 0) == 1;
//
//     /**
//      * Returns true if the device is running a debuggable build such as "userdebug" or "eng".
//      *
//      * Debuggable builds allow users to gain root access via local shell, attach debuggers to any
//      * application regardless of whether they have the "debuggable" attribute set, or downgrade
//      * selinux into "permissive" mode in particular.
//      */
//     public static boolean isDebuggable() {
//         return IS_DEBUGGABLE;
//     }
//
//     public static final boolean IS_ENG = "eng".equals(TYPE);
//     public static final boolean IS_USERDEBUG = "userdebug".equals(TYPE);
//     public static final boolean IS_USER = "user".equals(TYPE);
//
//     /**
//      * Whether this build is running inside a container.
//      *
//      * We should try to avoid checking this flag if possible to minimize
//      * unnecessarily diverging from non-container Android behavior.
//      * Checking this flag is acceptable when low-level resources being
//      * different, e.g. the availability of certain capabilities, access to
//      * system resources being restricted, and the fact that the host OS might
//      * handle some features for us.
//      * For higher-level behavior differences, other checks should be preferred.
//      */
//     public static final boolean IS_CONTAINER =
//             SystemProperties.getBoolean("ro.boot.container", false);
//
//     /**
//      * Specifies whether the permissions needed by a legacy app should be
//      * reviewed before any of its components can run. A legacy app is one
//      * with targetSdkVersion < 23, i.e apps using the old permission model.
//      * If review is not required, permissions are reviewed before the app
//      * is installed.
//      */
//     public static final boolean PERMISSIONS_REVIEW_REQUIRED = true;
//
//     /**
//      * Returns the version string for the radio firmware.  May return
//      * null (if, for instance, the radio is not currently on).
//      */
//     public static String getRadioVersion() {
//         return joinListOrElse(TelephonyProperties.baseband_version(), null);
//     }
//
//     private static String getString(String property) {
//         return SystemProperties.get(property, UNKNOWN);
//     }
//
//     private static String[] getStringList(String property, String separator) {
//         String value = SystemProperties.get(property);
//         if (value.isEmpty()) {
//             return new String[0];
//         } else {
//             return value.split(separator);
//         }
//     }
//
//     private static long getLong(String property) {
//         try {
//             return Long.parseLong(SystemProperties.get(property));
//         } catch (NumberFormatException e) {
//             return -1;
//         }
//     }
//
//     private static <T> String joinListOrElse(List<T> list, String defaultValue) {
//         String ret = list.stream().map(elem -> elem == null ? "" : elem.toString())
//                 .collect(Collectors.joining(","));
//         return ret.isEmpty() ? defaultValue : ret;
//     }
// }
