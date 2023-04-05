#ifndef _MTK_HARDWARE_INCLUDE_MTKCAM_DEVICE_ICAMDEVICE_H_
#define _MTK_HARDWARE_INCLUDE_MTKCAM_DEVICE_ICAMDEVICE_H_
//
#include <utils/Errors.h>
#include <utils/RefBase.h>
#include <hardware/camera_common.h>
#include <common.h>


/******************************************************************************
 *
 ******************************************************************************/
namespace NSCam {


/******************************************************************************
 *
 ******************************************************************************/
class ICamDeviceManager;


/******************************************************************************
 *
 ******************************************************************************/
class ICamDevice : public virtual android::RefBase
{
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//  Instantiation.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
protected:  ////                    Destructor.

    //  Disallowed to directly delete a raw pointer.
    virtual                         ~ICamDevice() {}

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//  Interfaces.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
public:     ////

    virtual char const*             getDevName() const                      = 0;

    virtual int32_t                 getOpenId() const                       = 0;

    virtual hw_device_t const*      get_hw_device() const                   = 0;

    virtual void                    set_hw_module(hw_module_t const* module)= 0;

    virtual void                    set_module_callbacks(
                                        camera_module_callbacks_t const* callbacks
                                    )                                       = 0;

    virtual void                    setDeviceManager(
                                        ICamDeviceManager* manager
                                    )                                       = 0;

};


/******************************************************************************
 *
 ******************************************************************************/
};  //namespace NSCam
#endif  //_MTK_HARDWARE_INCLUDE_MTKCAM_DEVICE_ICAMDEVICE_H_

