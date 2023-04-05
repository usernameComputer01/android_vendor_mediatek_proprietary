#ifndef _MTK_CAMERA_INC_COMMON_CAMUTILS_CAMMISC_H_
#define _MTK_CAMERA_INC_COMMON_CAMUTILS_CAMMISC_H_
//


/******************************************************************************
*
*******************************************************************************/


namespace android {
namespace MtkCamUtils {
/******************************************************************************
*
*******************************************************************************/


/******************************************************************************
*
*******************************************************************************/
template<int value> struct int2type { enum {v=value}; };


/******************************************************************************
  * @brief      make all directories in path.
  *
  * @details
  * @note
  *
  * @param[in]  path: a specified path to create.
  * @param[in]  mode: the argument specifies the permissions to use, like 0777
  *                   (the same to that in mkdir).
  *
  * @return
  *  -  true if successful; otherwise false.
  *****************************************************************************/
bool
makePath(
    char const*const path,
    uint_t const mode
);


/**
  * @brief save the buffer to the file
  *
  * @details
  *
  * @note
  *
  * @param[in] fname: The file name
  * @param[in] buf: The buffer want to save
  * @param[in] size: The size want to save
  *
  * @return
     * -   MTRUE indicates the operation is success
     * -   MFALSE indicates the operation is fail
  */
bool
saveBufToFile(
    char const*const fname,
    uint8_t*const buf,
    uint32_t const size
);


/**
  * @brief read the file to the buffer
  *
  * @details
  *
  * @note
  *
  * @param[in] fname: The input file name
  * @param[out] buf: The output buffer
  * @param[in] size: The buf size be read
  *
  *
  * @return
     * -   The read count
  */
uint32_t
loadFileToBuf(
    char const*const fname,
    uint8_t*const buf,
    uint32_t size
);

/**
  * @brief set the thread policy & priority
  *
  * @details
  *
  * @note
  *
  * @param[in] policy: The policy of the thread
  * @param[in] priority: The priority of the thread
  *
  * @return
     * -   MTRUE indicates the operation is success
     * -   MFALSE indicates the operation is fail
  */
bool
setThreadPriority(
    int policy,
    int priority
);


/**
  * @brief get the thread policy & priority
  *
  * @details
  *
  * @note
  *
  * @param[out] policy: The policy of the thread
  * @param[out] priority: The priority of the thread
  *
  * @return
     * -   MTRUE indicates the operation is success
     * -   MFALSE indicates the operation is fail
  */
bool
getThreadPriority(
    int& policy,
    int& priority
);


/**
  * @dump android call stack
  *
  */
void
dumpCallStack(void);


/******************************************************************************
*
*******************************************************************************/
};  // namespace MtkCamUtils
};  // namespace android
#endif  //_MTK_CAMERA_INC_COMMON_CAMUTILS_CAMMISC_H_

