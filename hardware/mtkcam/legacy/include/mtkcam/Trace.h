#ifndef _MTK_HARDWARE_INCLUDE_MTKCAM_TRACE_H_
#define _MTK_HARDWARE_INCLUDE_MTKCAM_TRACE_H_


/******************************************************************************
 *
 ******************************************************************************/
#ifndef ATRACE_TAG
#define ATRACE_TAG                              ATRACE_TAG_CAMERA
#endif
#if 1
#include <utils/Trace.h>
#define CAM_TRACE_NAME_LENGTH                   32
#define CAM_TRACE_CALL()                        ATRACE_CALL()
#define CAM_TRACE_NAME(name)                    ATRACE_NAME(name)
#define CAM_TRACE_INT(name, value)              ATRACE_INT(name, value)
#define CAM_TRACE_BEGIN(name)                   ATRACE_BEGIN(name)
#define CAM_TRACE_END()                         ATRACE_END()
#define CAM_TRACE_ASYNC_BEGIN(name, cookie)     ATRACE_ASYNC_BEGIN(name, cookie)
#define CAM_TRACE_ASYNC_END(name, cookie)       ATRACE_ASYNC_END(name, cookie)
#define CAM_TRACE_FMT_BEGIN(fmt, arg...)                    \
do{                                                         \
    if( ATRACE_ENABLED() )                                  \
    {                                                       \
        char buf[CAM_TRACE_NAME_LENGTH];                    \
        snprintf(buf, CAM_TRACE_NAME_LENGTH, fmt, ##arg);   \
        CAM_TRACE_BEGIN(buf);                               \
    }                                                       \
}while(0)
#define CAM_TRACE_FMT_END()                     CAM_TRACE_END()
#else
#define CAM_TRACE_CALL()
#define CAM_TRACE_NAME(name)
#define CAM_TRACE_INT(name, value)
#define CAM_TRACE_BEGIN(name)
#define CAM_TRACE_END()
#define CAM_TRACE_ASYNC_BEGIN(name, cookie)
#define CAM_TRACE_ASYNC_END(name, cookie)
#define CAM_TRACE_FMT_BEGIN(fmt, arg...)
#define CAM_TRACE_FMT_END()
#endif


/******************************************************************************
 *
 ******************************************************************************/
#endif  //_MTK_HARDWARE_INCLUDE_MTKCAM_TRACE_H_

