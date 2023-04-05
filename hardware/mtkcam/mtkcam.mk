

################################################################################
#
################################################################################
-include $(TOP)/$(MTK_PATH_CUSTOM)/hal/mtkcam/mtkcam.mk
PLATFORM := $(shell echo $(MTK_PLATFORM) | tr A-Z a-z)

#-----------------------------------------------------------
# Camera ip base design version control
#-----------------------------------------------------------
ifeq ($(TARGET_BOARD_PLATFORM), mt6797)
IS_LEGACY = 0
MTKCAM_HAL_VERSION := v1.4
MTKCAM_3A_VERSION := IPv1.1
MTKCAM_DRV_VERSION := $(PLATFORM)
MTKCAM_ALGO_VERSION := $(PLATFORM)
else
IS_LEGACY = 1
endif

#-----------------------------------------------------------
# Camera ip base design include path
#-----------------------------------------------------------
ifeq ($(IS_LEGACY), 0)
#Middleware
MTKCAM_HAL_INCLUDE := $(MTK_PATH_SOURCE)/hardware/mtkcam/middleware/$(MTKCAM_HAL_VERSION)/include
#3A
MTKCAM_3A_INCLUDE := $(MTK_PATH_SOURCE)/hardware/mtkcam/aaa/include/$(MTKCAM_3A_VERSION)
#Drv
MTKCAM_DRV_INCLUDE := $(MTK_PATH_SOURCE)/hardware/mtkcam/drv/include/$(MTKCAM_DRV_VERSION)
#Algo
MTKCAM_ALGO_INCLUDE := $(MTK_PATH_SOURCE)/hardware/mtkcam/include/algorithm/$(MTKCAM_ALGO_VERSION)

MTKCAM_C_INCLUDES := $(TOP)/$(MTK_PATH_SOURCE)/hardware/mtkcam/common/include

# this define is used by cpp & h to separate legacy path & MTKCAM ip base path
LOCAL_CFLAGS += -DMTK_CAMERA_IP_BASE

else
MTKCAM_C_INCLUDES := $(TOP)/$(MTK_PATH_SOURCE)/hardware/mtkcam/legacy/include/mtkcam

endif

# path for legacy platform
MTK_MTKCAM_PLATFORM    := $(MTK_PATH_SOURCE)/hardware/mtkcam/legacy/platform/$(PLATFORM)
ifneq (,$(filter $(strip $(TARGET_BOARD_PLATFORM)), mt6735m mt6737m))
MTK_MTKCAM_PLATFORM    := $(MTK_PATH_SOURCE)/hardware/mtkcam/legacy/platform/mt6735m
endif

ifneq (,$(filter $(strip $(TARGET_BOARD_PLATFORM)), mt6737t))
MTK_MTKCAM_PLATFORM    := $(MTK_PATH_SOURCE)/hardware/mtkcam/legacy/platform/mt6735
endif


#-----------------------------------------------------------
# MTKCAM_CFLAGS define
# In Android.mk, add followed words to use it:
# LOCAL_CFLAGS + = MTKCAM_CFLAGS
#-----------------------------------------------------------
# MTKCAM_HAVE_AEE_FEATURE
ifeq "yes" "$(strip $(HAVE_AEE_FEATURE))"
    MTKCAM_HAVE_AEE_FEATURE ?= 1
else
    MTKCAM_HAVE_AEE_FEATURE := 0
endif
MTKCAM_CFLAGS += -DMTKCAM_HAVE_AEE_FEATURE=$(MTKCAM_HAVE_AEE_FEATURE)
#-----------------------------------------------------------
# MTK_BASIC_PACKAGE
ifneq ($(MTK_BASIC_PACKAGE), yes)
    MTKCAM_BASIC_PACKAGE := 0
else
    MTKCAM_BASIC_PACKAGE := 1
endif
MTKCAM_CFLAGS += -DMTKCAM_BASIC_PACKAGE=$(MTKCAM_BASIC_PACKAGE)
#-----------------------------------------------------------
# ZSD+MFLL
ifeq "yes" "$(strip $(MTK_CAM_ZSDMFB_SUPPORT))"
    MTK_CAM_HAVE_ZSDMFB_SUPPORT := 1
else
    MTK_CAM_HAVE_ZSDMFB_SUPPORT := 0
endif
MTKCAM_CFLAGS += -DMTK_CAM_HAVE_ZSDMFB_SUPPORT=$(MTK_CAM_HAVE_ZSDMFB_SUPPORT)
#-----------------------------------------------------------
# ZSD+HDR
ifeq "yes" "$(strip $(MTK_CAM_ZSDHDR_SUPPORT))"
    MTK_CAM_HAVE_ZSDHDR_SUPPORT := 1
else
    MTK_CAM_HAVE_ZSDHDR_SUPPORT := 0
endif
MTKCAM_CFLAGS += -DMTK_CAM_HAVE_ZSDHDR_SUPPORT=$(MTK_CAM_HAVE_ZSDHDR_SUPPORT)
#-----------------------------------------------------------
# MTKCAM_HAVE_RR_PRIORITY
MTKCAM_HAVE_RR_PRIORITY      ?= 0  # built-in if '1' ; otherwise not built-in
MTKCAM_CFLAGS += -DMTKCAM_HAVE_RR_PRIORITY=$(MTKCAM_HAVE_RR_PRIORITY)
#-----------------------------------------------------------
# L1_CACHE_BYTES for 32-byte cache line
MTKCAM_CFLAGS += -DL1_CACHE_BYTES=32
#-----------------------------------------------------------
# MTKCAM_LOG_LEVEL_DEFAULT for compile-time loglevel control
ifeq ($(TARGET_BUILD_VARIANT), user)
    MTKCAM_LOG_LEVEL_DEFAULT   := 2
else ifeq ($(TARGET_BUILD_VARIANT), userdebug)
    MTKCAM_LOG_LEVEL_DEFAULT   := 3
else
    MTKCAM_LOG_LEVEL_DEFAULT   := 4
endif
MTKCAM_CFLAGS += -DMTKCAM_LOG_LEVEL_DEFAULT=$(MTKCAM_LOG_LEVEL_DEFAULT)
