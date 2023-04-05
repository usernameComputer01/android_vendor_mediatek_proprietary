#ifndef _MTK_CAMERA_INC_COMMON_CAMUTILS_IBUFFER_H_
#define _MTK_CAMERA_INC_COMMON_CAMUTILS_IBUFFER_H_
//
#include <sys/types.h>
//
#include <utils/RefBase.h>
#include <utils/String8.h>
#if (PLATFORM_VERSION_MAJOR == 2)
#include <utils/RefBase.h>
#else
#include <utils/StrongPointer.h>
#endif


/******************************************************************************
 *
 ******************************************************************************/


namespace android {
namespace MtkCamUtils {
/******************************************************************************
 *
 ******************************************************************************/


/******************************************************************************
 *  Memory Buffer Interface.
 ******************************************************************************/
class IMemBuf : public virtual RefBase
{
public:     ////                Attributes.
    //
    virtual int64_t             getTimestamp() const                    = 0;
    virtual void                setTimestamp(int64_t const timestamp)   = 0;
    //
public:     ////                Attributes.
    virtual const char*         getBufName() const                      = 0;
    virtual size_t              getBufSize() const                      = 0;
    //
    virtual void*               getVirAddr() const                      = 0;
    virtual void*               getPhyAddr() const                      = 0;
    //
    virtual int                 getIonFd() const                        { return -1; }
    virtual int                 getBufSecu() const                      { return 0; }
    virtual int                 getBufCohe() const                      { return 0; }
    //
public:     ////
    //
    virtual                     ~IMemBuf() {};
};


/******************************************************************************
 *  Image Buffer Interface.
 ******************************************************************************/
class IImgBuf : public IMemBuf
{
public:     ////                Attributes.
    virtual String8 const&      getImgFormat()      const               = 0;
    virtual uint32_t            getImgWidth()       const               = 0;
    virtual uint32_t            getImgHeight()      const               = 0;

    //
    //  planeIndex
    //      [I] plane's index; 0, 1, and 2 refer to 1st-, 2nd-, and 3rd planes,
    //          respectively.
    //
    //  return
    //      return its corresponding plane's stride, in pixel
    //
    virtual uint32_t            getImgWidthStride(
                                    uint_t const uPlaneIndex = 0
                                )   const                               = 0;

    virtual uint32_t            getBitsPerPixel()   const               = 0;
    //
public:     ////
    //
    virtual                     ~IImgBuf() {};
};


/******************************************************************************
 *
 ******************************************************************************/
};  // namespace MtkCamUtils
};  // namespace android
#endif  //_MTK_CAMERA_INC_COMMON_CAMUTILS_IBUFFER_H_

