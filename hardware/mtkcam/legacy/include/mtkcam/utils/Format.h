#ifndef _MTK_HARDWARE_INCLUDE_MTKCAM_UTILS_FORMAT_H_
#define _MTK_HARDWARE_INCLUDE_MTKCAM_UTILS_FORMAT_H_
//


/******************************************************************************
 *
 ******************************************************************************/
namespace NSCam {
namespace Utils {
namespace Format {

bool
checkValidFormat(
    int const imageFormat
);

/*****************************************************************************
 * @brief Query the plane count.
 *
 * @details Given a format of type EImageFormat, return its corresponding
 * plane count.
 *
 * @note
 *
 * @param[in] imageFormat: A format of type EImageFormat.
 *
 * @return its corresponding plane count.
 *
 ******************************************************************************/
size_t
queryPlaneCount(
    int const imageFormat
);


/*****************************************************************************
 * @brief Query the width in pixels of a specific plane.
 *
 * @details Given a format of type EImageFormat, a plane index, and the width in
 * in pixels of the 0-th plane, return the width in pixels of the given plane.
 *
 * @note
 *
 * @param[in] imageFormat: A format of type EImageFormat.
 * @param[in] planeIndex: a specific plane index.
 * @param[in] widthInPixels: the width in pixels of the 0-th plane.
 *
 * @return the width in pixels of the given plane.
 *
 ******************************************************************************/
size_t
queryPlaneWidthInPixels(
    int const imageFormat,
    size_t planeIndex,
    size_t widthInPixels
);


/*****************************************************************************
 * @brief Query the height in pixels of a specific plane.
 *
 * @details Given a format of type EImageFormat, a plane index, and the height
 * in pixels of the 0-th plane, return the height in pixels of the given plane.
 *
 * @note
 *
 * @param[in] imageFormat: A format of type EImageFormat.
 * @param[in] planeIndex: a specific plane index.
 * @param[in] heightInPixels: the height in pixels of the 0-th plane.
 *
 * @return the height in pixels of the given plane.
 *
 ******************************************************************************/
size_t
queryPlaneHeightInPixels(
    int const imageFormat,
    size_t planeIndex,
    size_t heightInPixels
);


/*****************************************************************************
 * @brief Query the bits per pixel of a specific plane.
 *
 * @details Given a format of type EImageFormat and a plane index, return
 * the bits per pixel of the given plane.
 *
 * @note
 *
 * @param[in] imageFormat: A format of type EImageFormat.
 * @param[in] planeIndex: a specific plane index.
 *
 * @return the bits per pixel of the given plane.
 *
 ******************************************************************************/
int
queryPlaneBitsPerPixel(
    int const imageFormat,
    size_t planeIndex
);


/*****************************************************************************
 * @brief Query the bits per pixel of a specific format.
 *
 * @details Given a format of type EImageFormat, return the bits per pixel.
 *
 * @note
 *
 * @param[in] imageFormat: A format of type EImageFormat.
 *
 * @return the bits per pixel.
 *
 ******************************************************************************/
int
queryImageBitsPerPixel(
    int const imageFormat
);


/*****************************************************************************
 * @brief Query the image format constant.
 *
 * @details Given a MtkCameraParameters::PIXEL_FORMAT_xxx, return its
 * corresponding image format constant.
 *
 * @note
 *
 * @param[in] szPixelFormat: A null-terminated string for pixel format (i.e.
 * MtkCameraParameters::PIXEL_FORMAT_xxx)
 *
 * @return its corresponding image format.
 *
 ******************************************************************************/
int
queryImageFormat(
    char const* szPixelFormat
);

/*****************************************************************************
 * @Dump the information of Image map.
 ******************************************************************************/
void
dumpMapInformation();

/******************************************************************************
 *
 ******************************************************************************/
};  // namespace Format
};  // namespace Utils
};  // namespace NSCam
#endif  //_MTK_HARDWARE_INCLUDE_MTKCAM_UTILS_FORMAT_H_

