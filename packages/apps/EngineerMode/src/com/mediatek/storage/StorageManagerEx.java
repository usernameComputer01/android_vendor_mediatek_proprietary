


/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mediatek.storage;

import android.os.Environment;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.SystemProperties;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.storage.VolumeInfo;
import android.content.Context;
import android.util.Log;

import java.io.File;


public class StorageManagerEx {
    private static final String TAG = "StorageManagerEx";

    public static final String PROP_SD_DEFAULT_PATH = "persist.sys.sd.defaultpath";
    private static final String PROP_DEVICE_TYPE = "ro.build.characteristics";
    private static final String PROP_DEVICE_TABLET = "tablet";

    private static final String STORAGE_PATH_SD1 = "/storage/sdcard0";
    private static final String STORAGE_PATH_SD2 = "/storage/sdcard1";
    private static final String STORAGE_PATH_EMULATED = "/storage/emulated/";
    private static final String STORAGE_PATH_SD1_ICS = "/mnt/sdcard";
    private static final String STORAGE_PATH_SD2_ICS = "/mnt/sdcard2";

    private static final String DIR_ANDROID = "Android";
    private static final String DIR_DATA = "data";
    private static final String DIR_CACHE = "cache";

    /**
     * Returns default path for writing.
     * @hide
     * @internal
     */
    public static String getDefaultPath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        return path + "/EngineerMode";
    }

    /**
     * Generates the path to Gallery/mms.
     * @hide
     * @internal
     */
    public static File getExternalCacheDir(String packageName) {
        if (null == packageName) {
            Log.w(TAG, "packageName = null!");
            return null;
        }

        File externalCacheDir = new File(getDefaultPath());
        externalCacheDir = Environment.buildPath(externalCacheDir, DIR_ANDROID, DIR_DATA,
                                                 packageName, DIR_CACHE);
        Log.d(TAG, "getExternalCacheDir path = " + externalCacheDir);
        return externalCacheDir;
    }

    /**
     * Returns external SD card path.
     * SD card might have multi partitions
     * will return first partition path
     * @hide
     * @internal
     */
    public static String getExternalStoragePath() {
        String path = "";
        try {
            IMountService mountService =
              IMountService.Stub.asInterface(ServiceManager.getService("mount"));
            if (mountService == null) {
                Log.e(TAG, "mount service is not initialized!");
                return "";
            }
            int userId = UserHandle.myUserId();
            boolean isEMMCProject = SystemProperties.get("ro.mtk_emmc_support").equals("1");
            VolumeInfo[] volumeInfos = mountService.getVolumes(0);
            for (int i = 0; i < volumeInfos.length; ++i) {
                VolumeInfo vol = volumeInfos[i];
                String diskID = vol.getDiskId();
                Log.d(TAG, "getExternalStoragePath, diskID=" + diskID);
                if (diskID != null) {
                    // portable sd card
                    if (vol.isVisibleForWrite(userId)
                            && vol.getState() == VolumeInfo.STATE_MOUNTED) {
                        if (isEMMCProject) {
                            // sd card disk id is "179,128" or "179,xxx", but not "179,0"
                            if (diskID.startsWith("disk:179") && !diskID.endsWith(",0")) {
                                path = vol.getPathForUser(userId).getAbsolutePath();
                                break;
                            }
                        } else {
                            // sd card disk id is "179,0"
                            if (diskID.equals("disk:179,0")) {
                                path = vol.getPathForUser(userId).getAbsolutePath();
                                break;
                            }
                        }
                    }
                } else {
                    // sd card is adopted and migrate data
                    if (vol.getType() == VolumeInfo.TYPE_EMULATED
                            && vol.getState() == VolumeInfo.STATE_MOUNTED) {
                        String emulatedPath = vol.getPathForUser(userId).getAbsolutePath();
                        File internalPathFile = vol.getInternalPath();
                        if (internalPathFile != null
                                && !internalPathFile.getAbsolutePath().equals("/data/media")) {
                            path = emulatedPath;
                            break;
                        } else {
                            Log.d(TAG, "getExternalStoragePath, igore path=" + emulatedPath);
                        }
                    }
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException when getExternalStoragePath:" + e);
        }
        Log.d(TAG, "getExternalStoragePath path=" + path);
        return path ;
    }

    /**
     * Return if this path is external sd card.
     * @hide
     * @internal
     */
    public static boolean isExternalSDCard(String path) {
        Log.e(TAG, "isExternalSDcard path=" + path);
        boolean result = false;
        if (path == null) {
            return false;
        }
        String externalStoragePath = getExternalStoragePath();
        if (externalStoragePath.equals("")) {
            return false;
        }
        if (!path.equals(externalStoragePath)) {
            Log.e(TAG, "path=" + path
                    + ", externalStoragePath=" + externalStoragePath
                    + ", return false");
            return false;
        }

        try {
            IMountService mountService =
              IMountService.Stub.asInterface(ServiceManager.getService("mount"));
            if (mountService == null) {
                Log.e(TAG, "mount service is not initialized!");
                return false;
            }
            int userId = UserHandle.myUserId();
            VolumeInfo[] volumeInfos = mountService.getVolumes(0);
            for (int i = 0; i < volumeInfos.length; ++i) {
                VolumeInfo vol = volumeInfos[i];
                if (vol.isVisibleForWrite(userId) && vol.getState() == VolumeInfo.STATE_MOUNTED) {
                    String volPath = vol.getPathForUser(userId).getAbsolutePath();
                    if (volPath.equals(path)) {
                        File internalPathFile = vol.getInternalPath();
                        if (internalPathFile != null
                                && !internalPathFile.getAbsolutePath().equals("/data/media")) {
                            result = true;
                            break;
                        }
                    }
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException when invoke isExternalSDcard:" + e);
        }
        Log.d(TAG, "isExternalSDcard path=" + path + ", return " + result);
        return result ;
    }

    /**
     * Returns internal Storage path.
     * @hide
     * @internal
     */
    @Deprecated
    public static String getInternalStoragePath() {
        String path = "";
        Log.d(TAG, "getInternalStoragePath path=" + path);
        return path ;
    }

    /**
     * Returns the sd swap state.
     * @hide
     * @internal
     */
    @Deprecated
    public static boolean getSdSwapState() {
        return false;
    }

    /**
     * For log tool only.
     * modify internal path to "/storage/emulated/0" for multi user
     * @hide
     * @internal
     */
    @Deprecated
    public static String getInternalStoragePathForLogger() {
        String path = getInternalStoragePath();
        Log.i(TAG, "getInternalStoragePathForLogger raw path=" + path);
        // if path start with "/storage/emulated/"
        // means MTK_SHARED_SDCARD==true, MTK_2SDCARD_SWAP==false
        // so just check path directly
        if (path != null && path.startsWith(STORAGE_PATH_EMULATED)) {
            path = "/storage/emulated/0";
        }
        Log.i(TAG, "getInternalStoragePathForLogger path=" + path);
        return path;
    }


    /**
     * Find the phone storage volume info.
     * @hide
     * @internal
     */
    public static VolumeInfo findPhoneStorage() {
        Log.d(TAG, "findPhoneStorage VolumeInfo");
        VolumeInfo phoneStorage = null;
        try {
            IMountService mountService =
              IMountService.Stub.asInterface(ServiceManager.getService("mount"));
            if (mountService == null) {
                Log.e(TAG, "mount service is not initialized!");
                return null;
            }
            VolumeInfo[] volumeInfos = mountService.getVolumes(0);
            for (int i = 0; i < volumeInfos.length; ++i) {
                VolumeInfo vol = volumeInfos[i];
                if (isPhoneStorage(vol.getDiskId())) {
                    phoneStorage = vol;
                    break;
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "RemoteException when invoke findPhoneStorage VolumeInfo:" + e);
        }
        return phoneStorage;
    }



    /**
     * check if this Disk is phone storage
     * eMMC storage diskid is "disk:179,0"
     * NAND storage diskid is "disk:7,1"
     */
    public static boolean isPhoneStorage(String diskId) {
        boolean result = false;
        if (diskId != null) {
            boolean isEMMCProject = SystemProperties.get("ro.mtk_emmc_support").equals("1");
            if (isEMMCProject) {
                if (diskId.equals("disk:179,0")) {
                    result = true;
                }
            } else {
                if (diskId.equals("disk:7,1")) {
                    result = true;
                }
            }
        }
        return result;
    }

}
