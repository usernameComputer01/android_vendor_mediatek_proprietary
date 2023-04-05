#ifndef _MTK_HARDWARE_INCLUDE_MTKCAM_BASICTYPES_H_
#define _MTK_HARDWARE_INCLUDE_MTKCAM_BASICTYPES_H_


/******************************************************************************
 *
 ******************************************************************************/
namespace NSCam {


/******************************************************************************
 *  Rational
 ******************************************************************************/
struct MRational
{
    MINT32  numerator;
    MINT32  denominator;
            //
            MRational(MINT32 _numerator = 0, MINT32 _denominator = 1)
                : numerator(_numerator)
                , denominator(_denominator)
            {
            }
};


/******************************************************************************
 *
 * @brief Sensor type enumeration.
 *
 ******************************************************************************/
namespace NSSensorType
{
    enum Type
    {
        eUNKNOWN        = 0xFFFFFFFF,               /*!< Unknown */
        eRAW            = 0,                        /*!< RAW */
        eYUV,                                       /*!< YUV */
    };
};


/******************************************************************************
 *
 ******************************************************************************/
};  //namespace NSCam
#endif  //_MTK_HARDWARE_INCLUDE_MTKCAM_BASICTYPES_H_

