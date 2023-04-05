#ifndef __DRM_MTK_UTIL_H__
#define __DRM_MTK_UTIL_H__

#include <drm/drm_framework_common.h>
#include <utils/RefBase.h>
#include <utils/String8.h>
#include <utils/threads.h>
#include <stdio.h>

namespace android {

class DrmRights;
class DecryptHandle;
class DrmManagerClient;

class DrmMtkUtil {
private:
    DrmMtkUtil();

public:
    static bool installDrmMsg(const String8& filePath);
    static String8 getContentId(const DrmRights& rights);
    //M: Add to avoid writing sdcard in native layer
    static bool installDrmMsg(int fd_dm, int fd_dcf);

private:
    static bool getNextNELineContain(FILE* fp, String8& line, String8 contained);
    static int getNextNELine(FILE* fp, char* line);
    static bool getNextNELineTrimR(FILE* fp, String8& line);
    static bool getLineX(FILE* fp, String8& line);
    static bool getLineXTrimR(FILE* fp, String8& line);
    static bool getFLSignature(FILE* fp, int offset, int dataLen, char* sig);
    static void getDcfTmpFileName(String8 dm, String8& dcf);
    static void renameDcfTmp(String8 dcfTmp);
    static int correctDcf(const String8& dmPath);
    static bool decryptBase64(FILE* fp, long offset, long& length);
    static bool getContentIndex(FILE* fp, const String8 boundary, long& startIndex, long& endIndex);
    static bool getLines(FILE* fp, int lineNo, bool trim, Vector<String8>& headers);
    static bool getRoIndex(FILE* fp, const String8 boundary, long& startIndex, long& endIndex);
    static bool installCd(FILE* fp_dm, const String8 boundary, FILE* fp_dcf);
    static bool installContent(FILE* fp_dm, long contentOffset, long contentLength,
            const String8 mime, const char* cid, const String8 headers, FILE * fp_dcf);
    static bool installFl(FILE* fp_dm, const String8 boundary, const String8 encoding,
            const String8 mime, FILE* fp_dcf);
    static bool installFlDcf(FILE* fp_dm, const String8 boundary, const String8 encoding,
            FILE* fp_dcf);
    static bool installRo(FILE* fp, long offset, long length, char* cid);
    static bool isForwardlockSet();
    static bool parseHeaders(const Vector<String8> headers, String8& encoding, String8& mime,
            int& drmMethod);
    static bool preInstall(FILE* fp, const String8 boundary, const String8 encoding,
            long& contentOffset, long& contentLength);
    //M: Add to avoid writing sdcard in native layer
    static bool correctDcf(int fd_dm, int fd_dcf);
    static bool copyFile(int fd_in, int fd_out, int length);
public:
    static Mutex mROLock;

public:
    static int saveIMEI(const String8& imei);
    static int saveId(const String8& id);
    static String8 loadId();
    static bool isDcf(int fd);
    static bool isDcf(const String8& path);
    static bool isDcf(const String8& path, int fd);
    static String8 getDcfMime(int fd);
    static String8 getDcfMime(const String8& path);
    static String8 toCommonMime(const char* mime);
    static String8 getProcessName(pid_t pid);
    static bool isTrustedClient(const String8& procName);
    static bool isTrustedVideoClient(const String8& procName);
    static long getContentSize(DecryptHandle* handle);
    static long getContentRawSize(sp<DecryptHandle> handle, DrmManagerClient* client);
    static long getContentLength(DecryptHandle* handle, DrmManagerClient* client);
};

}

#endif // __DRM_MTK_UTIL_H__
