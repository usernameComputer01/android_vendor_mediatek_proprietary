#ifndef _MTK_CAMERA_INC_COMMON_CAMUTILS_CAMPROPERTY_H_
#define _MTK_CAMERA_INC_COMMON_CAMUTILS_CAMPROPERTY_H_
//
#include <utils/String8.h>


namespace android {
namespace MtkCamUtils {
namespace Property {


/******************************************************************************
 *  Clears the all property map.
 *
 *  return
 *      N/A
 *
 ******************************************************************************/
void    clear();


/******************************************************************************
 *  Set a property.
 *  Replaces the property with the same key if it is already present.
 *
 *  return
 *      N/A
 *
 ******************************************************************************/
void    set(String8 const& key, String8 const& value);


/******************************************************************************
 *  Test if a specified key exists or not.
 *
 *  return
 *      Returns true if the property map contains the specified key.
 *
 ******************************************************************************/
bool    hasKey(String8 const& key);


/******************************************************************************
 *  Gets the value of a property and parses it.
 *
 *  return
 *      Returns true and sets outValue if the key was found and its value was
 *      parsed successfully.
 *      Otherwise returns false and does not modify outValue.
 *
 ******************************************************************************/
bool    tryGet(const String8& key, String8& outValue);
bool    tryGet(const String8& key, bool& outValue);
bool    tryGet(const String8& key, int32_t& outValue);
bool    tryGet(const String8& key, float& outValue);


};  // namespace Property
};  // namespace MtkCamUtils
};  // namespace android
#endif  //_MTK_CAMERA_INC_COMMON_CAMUTILS_CAMPROPERTY_H_

