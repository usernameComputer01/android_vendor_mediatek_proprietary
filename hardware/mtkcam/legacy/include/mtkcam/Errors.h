#ifndef _MTK_HARDWARE_INCLUDE_MTKCAM_ERRORS_H_
#define _MTK_HARDWARE_INCLUDE_MTKCAM_ERRORS_H_
/******************************************************************************
 *
 ******************************************************************************/
#include <utils/Errors.h>


/******************************************************************************
 *
 ******************************************************************************/
namespace NSCam {


/******************************************************************************
 * Error codes.
 * All error codes are negative values.
 ******************************************************************************/
typedef int                     MERROR;


/******************************************************************************
 * Error codes.
 * All error codes are negative values.
 ******************************************************************************/
enum
{
#define OK                      android::OK                     // 0: Everything's swell.
#define UNKNOWN_ERROR           android::UNKNOWN_ERROR          // 0x80000000,

#define NO_MEMORY               android::NO_MEMORY
#define INVALID_OPERATION       android::INVALID_OPERATION
#define BAD_VALUE               android::BAD_VALUE
#define BAD_TYPE                android::BAD_TYPE
#define NAME_NOT_FOUND          android::NAME_NOT_FOUND
#define PERMISSION_DENIED       android::PERMISSION_DENIED
#define NO_INIT                 android::NO_INIT
#define ALREADY_EXISTS          android::ALREADY_EXISTS
#define DEAD_OBJECT             android::DEAD_OBJECT
#define FAILED_TRANSACTION      android::FAILED_TRANSACTION
#define JPARKS_BROKE_IT         android::JPARKS_BROKE_IT        //-EPIPE

#define BAD_INDEX               android::BAD_INDEX
#define NOT_ENOUGH_DATA         android::NOT_ENOUGH_DATA
#define WOULD_BLOCK             android::WOULD_BLOCK
#define TIMED_OUT               android::TIMED_OUT
#define UNKNOWN_TRANSACTION     android::UNKNOWN_TRANSACTION
#define FDS_NOT_ALLOWED         android::FDS_NOT_ALLOWED

};


/******************************************************************************
 *
 ******************************************************************************/
};  //namespace NSCam
#endif  //_MTK_HARDWARE_INCLUDE_MTKCAM_ERRORS_H_

