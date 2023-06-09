#ifdef MTK_LOG_ENABLE
#undef MTK_LOG_ENABLE
#endif
#define MTK_LOG_ENABLE 1

#ifndef _MTK_HARDWARE_INCLUDE_MTKCAM_LOG_H_
#define _MTK_HARDWARE_INCLUDE_MTKCAM_LOG_H_

/******************************************************************************
 *
 ******************************************************************************/
#include <inttypes.h>

/******************************************************************************
 *
 ******************************************************************************/
#ifndef USING_MTK_LDVT
//
#ifdef LOG_NDEBUG
#undef LOG_NDEBUG
#endif
#define LOG_NDEBUG 0
//
#if   defined(MTKCAM_LOG_LEVEL)
#define CAM_LOG_LEVEL       MTKCAM_LOG_LEVEL
#elif defined(MTKCAM_LOG_LEVEL_DEFAULT)
#define CAM_LOG_LEVEL       MTKCAM_LOG_LEVEL_DEFAULT
#else
#define CAM_LOG_LEVEL       4
#endif
//
#include <cutils/log.h>
#define CAM_LOGV(fmt, arg...)    do{ if(CAM_LOG_LEVEL >= 4) ALOGV(fmt, ##arg); } while(0)
#define CAM_LOGD(fmt, arg...)    do{ if(CAM_LOG_LEVEL >= 3) ALOGD(fmt, ##arg); } while(0)
#define CAM_LOGI(fmt, arg...)    do{ if(CAM_LOG_LEVEL >= 2) ALOGI(fmt, ##arg); } while(0)
#define CAM_LOGW(fmt, arg...)    do{ if(CAM_LOG_LEVEL >= 1) ALOGW(fmt, ##arg); } while(0)
#define CAM_LOGE(fmt, arg...)    ALOGE(fmt" (%s){#%d:%s}", ##arg, __FUNCTION__, __LINE__, __FILE__)
//
#else //using LDVT

#ifndef DBG_LOG_TAG
#define DBG_LOG_TAG
#endif

#include <uvvf.h>
#define NEW_LINE_CHAR   "\n"

#define CAM_LOGV(fmt, arg...)        VV_MSG(DBG_LOG_TAG "[%s] " fmt NEW_LINE_CHAR, __func__, ##arg) // <Verbose>: Show more detail debug information. E.g. Entry/exit of private function; contain of local variable in function or code block; return value of system function/API...
#define CAM_LOGD(fmt, arg...)        VV_MSG(DBG_LOG_TAG "[%s] " fmt NEW_LINE_CHAR, __func__, ##arg) // <Debug>: Show general debug information. E.g. Change of state machine; entry point or parameters of Public function or OS callback; Start/end of process thread...
#define CAM_LOGI(fmt, arg...)        VV_MSG(DBG_LOG_TAG "[%s] " fmt NEW_LINE_CHAR, __func__, ##arg) // <Info>: Show general system information. Like OS version, start/end of Service...
#define CAM_LOGW(fmt, arg...)        VV_MSG(DBG_LOG_TAG "[%s] WARNING: " fmt NEW_LINE_CHAR, __func__, ##arg)    // <Warning>: Some errors are encountered, but after exception handling, user won't notice there were errors happened.
#define CAM_LOGE(fmt, arg...)        VV_ERRMSG(DBG_LOG_TAG "[%s, %s, line%04d] ERROR: " fmt NEW_LINE_CHAR, __FILE__, __func__, __LINE__, ##arg) // When MP, will only show log of this level. // <Fatal>: Serious error that cause program can not execute. <Error>: Some error that causes some part of the functionality can not operate normally.
#define BASE_LOG_AST(cond, fmt, arg...)     \
    do {        \
        if (!(cond))        \
            VV_ERRMSG("[%s, %s, line%04d] ASSERTION FAILED! : " fmt NEW_LINE_CHAR, __FILE__, __func__, __LINE__, ##arg);        \
    } while (0)

#endif
//
//  ASSERT
#define CAM_LOGA(...) \
    do { \
        CAM_LOGE("[Assert] " __VA_ARGS__); \
        while(1) { ::usleep(500 * 1000); } \
    } while (0)
//
//
//  FATAL
#define CAM_LOGF(...) \
    do { \
        CAM_LOGE("[Fatal] " __VA_ARGS__); \
        LOG_ALWAYS_FATAL_IF(1, "(%s){#%d:%s}""\r\n", __FUNCTION__, __LINE__, __FILE__); \
    } while (0)

/******************************************************************************
 *
 ******************************************************************************/
#define CAM_LOGV_IF(cond, ...)      do { if ( (cond) ) { CAM_LOGV(__VA_ARGS__); } }while(0)
#define CAM_LOGD_IF(cond, ...)      do { if ( (cond) ) { CAM_LOGD(__VA_ARGS__); } }while(0)
#define CAM_LOGI_IF(cond, ...)      do { if ( (cond) ) { CAM_LOGI(__VA_ARGS__); } }while(0)
#define CAM_LOGW_IF(cond, ...)      do { if ( (cond) ) { CAM_LOGW(__VA_ARGS__); } }while(0)
#define CAM_LOGE_IF(cond, ...)      do { if ( (cond) ) { CAM_LOGE(__VA_ARGS__); } }while(0)
#define CAM_LOGA_IF(cond, ...)      do { if ( (cond) ) { CAM_LOGA(__VA_ARGS__); } }while(0)
#define CAM_LOGF_IF(cond, ...)      do { if ( (cond) ) { CAM_LOGF(__VA_ARGS__); } }while(0)
/******************************************************************************
 *
 ******************************************************************************/

#endif  //_MTK_HARDWARE_INCLUDE_MTKCAM_LOG_H_
