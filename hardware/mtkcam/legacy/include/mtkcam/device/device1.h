#ifndef _MTK_HARDWARE_INCLUDE_MTKCAM_DEVICE_OPS_H_
#define _MTK_HARDWARE_INCLUDE_MTKCAM_DEVICE_OPS_H_
//
#include <hardware/camera.h>
#include <system/camera_metadata.h>


/******************************************************************************
 *
 ******************************************************************************/
typedef void (*mtk_camera_metadata_callback)(
        int32_t msg_type,
        camera_metadata_t *result,
        camera_metadata_t *charateristic,
        void *user);

typedef struct mtk_camera_device_ops {
    camera_device_ops ops;

    /** Set the notification and data callbacks */
    void (*mtk_set_callbacks)(
            struct camera_device *,
            mtk_camera_metadata_callback metadata_cb,
            void *user);

} mtk_camera_device_ops_t;

/******************************************************************************
 *
 ******************************************************************************/
#endif  //_MTK_HARDWARE_INCLUDE_MTKCAM_DEVICE_OPS_H_

