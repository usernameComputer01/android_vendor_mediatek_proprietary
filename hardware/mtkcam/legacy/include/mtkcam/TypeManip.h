#ifndef _MTK_HARDWARE_INCLUDE_MTKCAM_TYPEMANIP_H_
#define _MTK_HARDWARE_INCLUDE_MTKCAM_TYPEMANIP_H_


/******************************************************************************
 *  Type manipulation.
 ******************************************************************************/
#ifdef __cplusplus
namespace NSCam {


/**
 * @class template Int2Type.
 * @brief Int2Type<v> converts an integral constant v into a unique type.
 */
template <int v>
struct Int2Type
{
    enum { value = v };
};


/**
 * @class template Type2Type.
 * @brief Type2Type<T> converts a type T into a unique type.
 */
template <typename T>
struct Type2Type
{
    typedef T OriginalType;
};


/******************************************************************************
 *
 ******************************************************************************/
};  //namespace NSCam
#endif //__cplusplus
#endif  //_MTK_HARDWARE_INCLUDE_MTKCAM_TYPEMANIP_H_

