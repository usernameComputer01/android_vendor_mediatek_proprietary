#ifndef _MTK_HARDWARE_INCLUDE_MTKCAM_V1_SDKCLIENT_IGESTURECLIENT_H_
#define _MTK_HARDWARE_INCLUDE_MTKCAM_V1_SDKCLIENT_IGESTURECLIENT_H_
//

#include <gesture_device.h>

typedef bool   (*GestureCallback_t)(hand_detection_result_t const &result, MVOID* user);



namespace android {

/******************************************************************************
 *
 ******************************************************************************/
class IParamsManager;
class ICamClient;

namespace NSSdkClient {


/******************************************************************************
*   Preview Client Handler.
*******************************************************************************/
class IGestureClient : public ICamClient, public virtual RefBase
{
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
public:     ////                    Instantiation.
    //
    static sp<IGestureClient>       createInstance();
    //
    virtual                         ~IGestureClient() {}

    //
    virtual bool                  start() = 0;
    //
    virtual bool                  stop() = 0;
    //
    virtual void                  setCallbacks(GestureCallback_t gesture_cb, MVOID* user) = 0;
};


}; // namespace NSSdkClient
}; // namespace android
#endif  //_MTK_HARDWARE_INCLUDE_MTKCAM_V1_SDKCLIENT_IGESTURECLIENT_H_

