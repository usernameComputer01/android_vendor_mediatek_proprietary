/*
 * Copyright (C) 2010 The Android Open Source Project
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

#define LOG_NDEBUG 1
#define LOG_TAG "DrmMtkUtil/WhiteList"
#include <utils/Log.h>

#include <drm/DrmMtkDef.h>
#include <utils/String8.h>

using namespace android;

/**
 * If the customer want to add his own app to white list
 * Please modify init() function
 * The white list is refactoryed by rui.hu
 */

Vector<String8> DrmTrustedApp::TRUSTED_APP;
bool DrmTrustedApp::sIsInited = init();

bool DrmTrustedApp::init() {
    TRUSTED_APP.push_back(String8("com.android.gallery3d"));
    TRUSTED_APP.push_back(String8("com.mediatek.videoplayer"));
    TRUSTED_APP.push_back(String8("com.mediatek.videoplayer2"));
    TRUSTED_APP.push_back(String8("com.android.music"));
    TRUSTED_APP.push_back(String8("com.android.phone"));
    TRUSTED_APP.push_back(String8("com.android.settings"));
    TRUSTED_APP.push_back(String8("com.android.deskclock"));
    TRUSTED_APP.push_back(String8("com.cooliris.media"));
    TRUSTED_APP.push_back(String8("com.android.systemui"));
    TRUSTED_APP.push_back(String8("com.mediatek.drmframeworktest"));// added for drm test case
    TRUSTED_APP.push_back(String8("com.google.android.xts.media"));
    TRUSTED_APP.push_back(String8("com.widevine.demo"));
    TRUSTED_APP.push_back(String8("com.discretix.drmassist"));// add for PlayReady
    TRUSTED_APP.push_back(String8("com.discretix.QA_ANDROID_COMMON"));//add for playReady
    TRUSTED_APP.push_back(String8("com.mediatek.dataprotection"));//add for CTA5
    TRUSTED_APP.push_back(String8("com.android.wallpapercropper"));//for launcher2, wallpapercropper
    TRUSTED_APP.push_back(String8("android.process.media"));//for mediaprocess
    TRUSTED_APP.push_back(String8("com.android.gallery3d:widgetservice"));//for gallery widget
    //Add your special process name here

    // CyanogenMod
    TRUSTED_APP.push_back(String8("org.cyanogenmod.snap"));
    TRUSTED_APP.push_back(String8("org.cyanogenmod.filemanager"));

    TRUSTED_APP.push_back(String8("com.android.wallpaper"));
    TRUSTED_APP.push_back(String8("com.cyanogenmod.wallpaper"));
    TRUSTED_APP.push_back(String8("org.cyanogenmod.wallpaperpicker"));

    TRUSTED_APP.push_back(String8("com.cyanogenmod.eleven"));

    return true;
}

bool DrmTrustedApp::IsDrmTrustedApp(const String8& procName) {
    bool result = false;
    Vector<String8>::iterator it = TRUSTED_APP.begin();
    for (; it != TRUSTED_APP.end(); ++it) {
        ALOGD("IsDrmTrustedApp: compare [%s] with [%s].",
                it->string(),
                procName.string());

        if (0 == strcmp(it->string(), procName.string())) {
            ALOGD("IsDrmTrustedApp: accepted.");
            result = true;
            break;
        }
    }
    return result;
}

Vector<String8> DrmTrustedClient::TRUSTED_PROC;
bool DrmTrustedClient::sIsInited = init();

bool DrmTrustedClient::init() {
    TRUSTED_PROC.push_back(String8("com.android.gallery"));// gallery 2d
    TRUSTED_PROC.push_back(String8("com.android.gallery:CropImage"));
    TRUSTED_PROC.push_back(String8("com.cooliris.media"));// gallery 3d (2.3)
    TRUSTED_PROC.push_back(String8("com.mediatek.drmfileinstaller"));
    TRUSTED_PROC.push_back(String8("com.android.phone"));// ringtone playing
    TRUSTED_PROC.push_back(String8("com.android.gallery3d"));// gallery (4.0)
    TRUSTED_PROC.push_back(String8("com.android.gallery3d:crop"));
    TRUSTED_PROC.push_back(String8("com.mediatek.drmframeworktest"));// added for drm test case
    TRUSTED_PROC.push_back(String8("com.google.android.xts.media"));
    TRUSTED_PROC.push_back(String8("com.widevine.demo"));
    TRUSTED_PROC.push_back(String8("com.android.launcher3:wallpaper_chooser"));//KK add
    TRUSTED_PROC.push_back(String8("com.discretix.drmassist"));// add for PlayReady
    TRUSTED_PROC.push_back(String8("com.discretix.QA_ANDROID_COMMON"));//add for playReady
    TRUSTED_PROC.push_back(String8("com.mediatek.dataprotection"));//add for CTA5
    TRUSTED_PROC.push_back(String8("com.android.wallpapercropper"));//for launcher2, wallpapercropper
    TRUSTED_PROC.push_back(String8("android.process.media"));//for mediaprocess
    TRUSTED_PROC.push_back(String8("com.android.gallery3d:widgetservice"));//for gallery widget 
    //Add your special process name here

    // CyanogenMod
    TRUSTED_PROC.push_back(String8("org.cyanogenmod.snap"));
    TRUSTED_PROC.push_back(String8("org.cyanogenmod.filemanager"));

    TRUSTED_PROC.push_back(String8("com.android.wallpaper"));
    TRUSTED_PROC.push_back(String8("com.cyanogenmod.wallpaper"));
    TRUSTED_PROC.push_back(String8("org.cyanogenmod.wallpaperpicker"));

    TRUSTED_PROC.push_back(String8("com.cyanogenmod.eleven"));

    return true;
}

bool DrmTrustedClient::IsDrmTrustedClient(const String8& procName) {
    bool result = false;
    Vector<String8>::iterator it = TRUSTED_PROC.begin();
    for (; it != TRUSTED_PROC.end(); ++it) {
        ALOGD("IsDrmTrustedClient: compare [%s] with [%s].",
                it->string(),
                procName.string());

        if (0 == strcmp(it->string(), procName.string())) {
            ALOGD("IsDrmTrustedClient: accepted.");
            result = true;
            break;
        }
    }
    return result;
}

Vector<String8> DrmTrustedVideoApp::TRUSTED_VIDEO_APP;
bool DrmTrustedVideoApp::sIsInited = init();

bool DrmTrustedVideoApp::init() {
    TRUSTED_VIDEO_APP.push_back(String8("com.android.gallery3d"));
    TRUSTED_VIDEO_APP.push_back(String8("com.mediatek.videoplayer"));
    TRUSTED_VIDEO_APP.push_back(String8("com.mediatek.videoplayer2"));
    TRUSTED_VIDEO_APP.push_back(String8("com.mediatek.dataprotection"));
    //Add your special process name here

    // CyanogenMod
    TRUSTED_VIDEO_APP.push_back(String8("org.cyanogenmod.snap"));
    TRUSTED_VIDEO_APP.push_back(String8("org.cyanogenmod.filemanager"));

    return true;
}

bool DrmTrustedVideoApp::IsDrmTrustedVideoApp(const String8& procName) {
    bool result = false;
    Vector<String8>::iterator it = TRUSTED_VIDEO_APP.begin();
    for (; it != TRUSTED_VIDEO_APP.end(); ++it) {
        ALOGD("IsDrmTrustedVideoApp: compare [%s] with [%s].",
                it->string(),
                procName.string());

        if (0 == strcmp(it->string(), procName.string())) {
            ALOGD("DrmTrustedVideoApp: accepted.");
            result = true;
            break;
        }
    }
    return result;
}

const char* DrmSntpServer::NTP_SERVER_1 = "hshh.org";
const char* DrmSntpServer::NTP_SERVER_2 = "t1.hshh.org";
const char* DrmSntpServer::NTP_SERVER_3 = "t2.hshh.org";
const char* DrmSntpServer::NTP_SERVER_4 = "t3.hshh.org";
const char* DrmSntpServer::NTP_SERVER_5 = "clock.via.net";

//For CTA5 feature
Vector<String8> CtaTrustedClient::TRUSTED_PROC;
Vector<String8> CtaTrustedClient::TRUSTED_GETTOKEN_PROC;
Vector<String8> CtaTrustedClient::TRUSTED_CHECKTOKEN_PROC;
bool CtaTrustedClient::sIsInited = init();

bool CtaTrustedClient::init() {
    bool ret = initTrustedClient();
    ret |= initTrustedGetTokenClient();
    ret |= initTrustedCheckTokenClient();
    return ret;
}

bool CtaTrustedClient::initTrustedClient() {
    TRUSTED_PROC.push_back(String8("com.android.gallery3d"));
    TRUSTED_PROC.push_back(String8("com.android.music"));
    TRUSTED_PROC.push_back(String8("com.mediatek.dataprotection"));
    TRUSTED_PROC.push_back(String8("/system/bin/mediaserver"));
    //Add your special process name here

    // CyanogenMod
    TRUSTED_PROC.push_back(String8("com.cyanogenmod.eleven"));

    return true;
}

bool CtaTrustedClient::initTrustedGetTokenClient() {
    TRUSTED_GETTOKEN_PROC.push_back(String8("com.mediatek.dataprotection"));
    //Add your special process name here

    return true;
}

bool CtaTrustedClient::initTrustedCheckTokenClient() {
    TRUSTED_CHECKTOKEN_PROC.push_back(String8("com.android.gallery3d"));
    TRUSTED_CHECKTOKEN_PROC.push_back(String8("com.android.music"));
    //Add your special process name here

    // CyanogenMod
    TRUSTED_CHECKTOKEN_PROC.push_back(String8("com.cyanogenmod.eleven"));

    return true;
}

bool CtaTrustedClient::IsCtaTrustedClient(const String8& procName) {
    bool result = false;
    Vector<String8>::iterator it = TRUSTED_PROC.begin();
    for (; it != TRUSTED_PROC.end(); ++it) {
        ALOGD("IsCtaTrustedClient: compare [%s] with [%s].", it->string(), procName.string());

        if (0 == strcmp(it->string(), procName.string()))
        {
            ALOGD("IsCtaTrustedClient: accepted.");
            result = true;
            break;
        }
    }
    return result;
}

bool CtaTrustedClient::IsCtaTrustedGetTokenClient(const String8& procName) {
    bool result = false;
    Vector<String8>::iterator it = TRUSTED_GETTOKEN_PROC.begin();
    for (; it != TRUSTED_GETTOKEN_PROC.end(); ++it) {
        ALOGD("IsCtaTrustedGetTokenClient: compare [%s] with [%s].", it->string(), procName.string());

        if (0 == strcmp(it->string(), procName.string()))
        {
            ALOGD("IsCtaTrustedGetTokenClient: accepted.");
            result = true;
            break;
        }
    }
    return result;
}

bool CtaTrustedClient::IsCtaTrustedCheckTokenClient(const String8& procName) {
    bool result = false;
    Vector<String8>::iterator it = TRUSTED_CHECKTOKEN_PROC.begin();
    for (; it != TRUSTED_CHECKTOKEN_PROC.end(); ++it) {
        ALOGD("IsCtaTrustedCheckTokenClient: compare [%s] with [%s].", it->string(), procName.string());

        if (0 == strcmp(it->string(), procName.string()))
        {
            ALOGD("IsCtaTrustedCheckTokenClient: accepted.");
            result = true;
            break;
        }
    }
    return result;
}
