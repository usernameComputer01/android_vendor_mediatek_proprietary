#ifndef _MTK_CAMERA_INC_COMMON_CAMUTILS_CAMFORMAT_TRANSFORM_H_
#define _MTK_CAMERA_INC_COMMON_CAMUTILS_CAMFORMAT_TRANSFORM_H_
//
/******************************************************************************
*
*******************************************************************************/
#include <utils/String8.h>


namespace android {
namespace MtkCamUtils {
namespace FmtUtils {


class CamFormatTransform
{
private:

    struct buffer
    {
       String8 fmt;
       unsigned char* addr;
       int size;
       int width;
       int height;
       int stride1;
       int stride2;
       int stride3;
       //
       buffer()
           : fmt("")
           , addr(NULL)
           , size(0)
           , width(0)
           , height(0)
           , stride1(0)
           , stride2(0)
           , stride3(0)
           {}
    };

    buffer src;
    buffer dst;
    //
    bool   check();

public:
    void   setSrc(String8 fmt, unsigned char *addr, int size, int width, int height, int stride1 = 0, int stride2 = 0, int stride3 = 0);
    void   setDst(String8 fmt, unsigned char *addr, int size, int width, int height, int stride1 = 0, int stride2 = 0, int stride3 = 0);

public:
    bool   convert();
};




};  // namespace FmtUtils
};  // namespace MtkCamUtils
};  // namespace android
#endif  //_MTK_CAMERA_INC_COMMON_CAMUTILS_CAMFORMAT_H_

