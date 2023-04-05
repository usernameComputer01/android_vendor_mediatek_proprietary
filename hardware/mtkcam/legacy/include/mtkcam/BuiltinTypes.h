#ifndef _MTK_HARDWARE_INCLUDE_MTKCAM_BUILTTYPES_H_
#define _MTK_HARDWARE_INCLUDE_MTKCAM_BUILTTYPES_H_

/******************************************************************************
 *
 ******************************************************************************/
#include <stdint.h>


/******************************************************************************
 *
 ******************************************************************************/
//
typedef signed char             MINT8;
typedef unsigned char           MUINT8;
//
typedef signed short            MINT16;
typedef unsigned short          MUINT16;
//
typedef signed int              MINT32;
typedef unsigned int            MUINT32;
//
typedef int64_t                 MINT64;
typedef uint64_t                MUINT64;
//
typedef signed int              MINT;
typedef unsigned int            MUINT;
//
typedef intptr_t                MINTPTR;
typedef uintptr_t               MUINTPTR;

/******************************************************************************
 *
 ******************************************************************************/
//
typedef void                    MVOID;
typedef float                   MFLOAT;
typedef double                  MDOUBLE;
//
typedef int                     MBOOL;
//
#ifndef MTRUE
    #define MTRUE               1
#endif
#ifndef MFALSE
    #define MFALSE              0
#endif
#ifndef MNULL
    #define MNULL               0
#endif
/******************************************************************************
 *
 ******************************************************************************/
#endif  //_MTK_HARDWARE_INCLUDE_MTKCAM_BUILTTYPES_H_

