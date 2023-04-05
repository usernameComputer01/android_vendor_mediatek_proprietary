#ifndef __DRM_MTK_WV_UTIL_H__
#define __DRM_MTK_WV_UTIL_H__

#include <utils/String8.h>

namespace android {

class DrmMtkWVUtil {
private:
    DrmMtkWVUtil();

public:
    static int loadKeybox(unsigned char* data, unsigned int offset, unsigned int length);
    static int saveKeybox(unsigned char* data, unsigned int length);
    static String8 getDeviceId();
};

}

#endif // __DRM_MTK_WV_UTIL_H__
