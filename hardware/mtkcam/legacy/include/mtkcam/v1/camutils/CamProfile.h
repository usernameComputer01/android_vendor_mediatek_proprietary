#ifndef _MTK_CAMERA_INC_COMMON_CAMUTILS_CAMPROFILE_H_
#define _MTK_CAMERA_INC_COMMON_CAMUTILS_CAMPROFILE_H_
/******************************************************************************
 *
 ******************************************************************************/
#include <utils/String8.h>
#include <utils/Timers.h>


namespace android {
namespace MtkCamUtils {
/******************************************************************************
 *
 ******************************************************************************/


/******************************************************************************
 *  get the time in micro-seconds
 ******************************************************************************/
inline
int64_t
getTimeInUs()
{
    return  ::ns2us(::systemTime());
}


/******************************************************************************
 *  get the time in milli-seconds
 ******************************************************************************/
inline
int64_t
getTimeInMs()
{
    return  ::ns2ms(::systemTime());
}


/******************************************************************************
 *
 ******************************************************************************/
class DurationTool
{
public:     ////        Interfaces.
                        DurationTool(char const*const szSubjectName);
                        DurationTool(char const*const szSubjectName, nsecs_t nsInitTimestamp);
                        //
    void                reset();
    void                reset(nsecs_t nsInitTimestamp);
                        //
    void                update();
    void                update(nsecs_t nsTimestamp);
                        //
    void                showFps() const;
                        //
    int32_t             getCount() const        { return mi4Count; }
    nsecs_t             getDuration() const     { return mnsEnd - mnsStart; }
                        //
protected:  ////        Data Members.
    String8             ms8SubjectName;
    //
    int32_t             mi4Count;
    nsecs_t             mnsStart;
    nsecs_t             mnsEnd;
};


/******************************************************************************
 *
 ******************************************************************************/
class CamProfile
{
public:     ////        Interfaces.
                        CamProfile(
                            char const*const pszFuncName,
                            char const*const pszClassName = ""
                        );
                        //
                        inline
                        void
                        enable(bool fgEnable)
                        {
                            mfgIsProfile = fgEnable;
                        }
                        //
                        bool
                        print(
                            char const*const fmt = "",
                            ...
                        ) const;
                        //
                        bool
                        print_overtime(
                            int32_t const msTimeInterval,
                            char const*const fmt = "",
                            ...
                        ) const;

protected:  ////        Data Members.
    char const*const    mpszClassName;
    char const*const    mpszFuncName;
    mutable int32_t     mIdx;
    int32_t const       mi4StartUs;
    mutable int32_t     mi4LastUs;
    bool                mfgIsProfile;
};


/******************************************************************************
*
*******************************************************************************/
};  // namespace MtkCamUtils
};  // namespace android
#endif  //  _MTK_CAMERA_INC_COMMON_CAMUTILS_CAMPROFILE_H_

