#ifndef _MTK_CAMERA_INC_COMMON_CAMUTILS_IIMGBUFQUEUE_H_
#define _MTK_CAMERA_INC_COMMON_CAMUTILS_IIMGBUFQUEUE_H_
//
#include <utils/Vector.h>


/******************************************************************************
*
*******************************************************************************/


namespace android {
namespace MtkCamUtils {
/******************************************************************************
*
*******************************************************************************/


/*******************************************************************************
*
*******************************************************************************/
struct ImgBufQueNode
{
public:     ////                Image Buffer Operations.
    inline sp<IImgBuf>const&    getImgBuf() const   { return mpImgBuf; }

public:     ////                Status Operations.
                                enum ESTATUS
                                {
                                    eSTATUS_TODO,   // buffer is ready to be processed.
                                    eSTATUS_DONE,   // buffer is processed done.
                                    eSTATUS_CANCEL, // buffer is canceled & not processed.
                                };
    //
    inline bool                 isTODO() const                      { return mi4Status == eSTATUS_TODO; }
    inline bool                 isDONE() const                      { return mi4Status == eSTATUS_DONE; }
    inline bool                 isCANCEL() const                    { return mi4Status == eSTATUS_CANCEL; }
    //
    inline int32_t              getStatus() const                   { return mi4Status; }
    inline void                 setStatus(ESTATUS const eSTATUS)    { mi4Status = eSTATUS; }

public:     ////                Cookie Operations.
    inline int32_t              getCookieED() const                 { return mi4CookieED; }
    inline int32_t              getCookieDE() const                 { return mi4CookieDE; }
    //
    inline void                 setCookieED(int32_t const i4Cookie) { mi4CookieED = i4Cookie; }
    inline void                 setCookieDE(int32_t const i4Cookie) { mi4CookieDE = i4Cookie; }

public:     ////
    inline void                 setRotation(uint32_t const u4Rotate){ mu4Rotate = u4Rotate; }
    inline uint32_t             getRotation() const                 { return mu4Rotate; }
    inline void                 setOrientation(uint32_t const u4Orientation){ mu4Orientation = u4Orientation; }
    inline uint32_t             getOrientation() const                 { return mu4Orientation; }


public:     ////                Operators.
    bool                        operator!() const   { return mpImgBuf==0; }
    sp<IImgBuf>const&           operator->()const   { return mpImgBuf; }
    ImgBufQueNode&              operator=(ImgBufQueNode const& rhs)
                                {
                                    mpImgBuf    = rhs.mpImgBuf;
                                    mi4Status   = rhs.mi4Status;
                                    mi4CookieED = rhs.mi4CookieED;
                                    mi4CookieDE = rhs.mi4CookieDE;
                                    mu4Rotate   = rhs.mu4Rotate;
                                    mu4Orientation = rhs.mu4Orientation;
                                    return  (*this);
                                }
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
public:     ////                Instantiation.
                                ImgBufQueNode(
                                    sp<IImgBuf>const& pImgBuf   = 0,
                                    int32_t const i4Status      = 0,
                                    int32_t const i4CookieED    = 0,
                                    int32_t const i4CookieDE    = 0,
                                    uint32_t const u4Rotate     = 0,
                                    uint32_t const u4Orientation     = 0
                                )
                                    : mpImgBuf(pImgBuf)
                                    , mi4Status(i4Status)
                                    , mi4CookieED(i4CookieED)
                                    , mi4CookieDE(i4CookieDE)
                                    , mu4Rotate(u4Rotate)
                                    , mu4Orientation(u4Orientation)
                                {
                                }
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
protected:  ////                Data Members.
    sp<IImgBuf>                 mpImgBuf;
    int32_t                     mi4Status;      //  TODO, DONE, CANCEL
    int32_t                     mi4CookieED;    //  enqueuer (read/write) -> dequeuer (read only )
    int32_t                     mi4CookieDE;    //  dequeuer (read only ) -> enqueuer (read/write)
    uint32_t                    mu4Rotate;      //  rotation indicator
    uint32_t                    mu4Orientation; //  orientation for display rotation
    //
};


/******************************************************************************
*   Image Buffer Provider Interface
*******************************************************************************/
class IImgBufProvider : public virtual RefBase
{
public:     ////                Owner ID.
    enum
    {
        eID_GENERIC,            //  Generic Users
        eID_DISPLAY,            //  Display Client
        eID_REC_CB,             //  Record Callback
        eID_PRV_CB,             //  Preview Callback
        eID_FD,                 //  FD Client
        eID_OT,                 //  OT Client
        eID_AP_CLIENT,     // AP Client
        eID_TOTAL_NUM,          //  Total Num
        eID_UNKNOWN             = 0x80000000,
    };

public:     ////                Interface.
    virtual                     ~IImgBufProvider() {}
    //
    virtual int32_t             getProviderId() const                       = 0;
    virtual char const*         getProviderName() const                     = 0;

    /*
     *  REPEAT:[ dequeProvider() -> enqueProvider() ]
     *  dequeProvider() returns false immediately if empty.
     */
    virtual bool                dequeProvider(ImgBufQueNode& rNode)         = 0;
    virtual bool                enqueProvider(ImgBufQueNode const& rNode)   = 0;

    /*
     *  Arguments:
     *      rNode
     *          [I] If this function returns true, rNode is a copy of the first
     *          node in th queue. Unlike dequeProvider(), the first node is not
     *          removed from th equeue.
     *  Return:
     *      false if the queue is empty.
     *      true if the queue is not empty.
     */
    virtual bool                queryProvider(ImgBufQueNode& rNode)         = 0;
    //
    virtual bool                queryFormat(String8& rFormat)               = 0;

};


/******************************************************************************
*   Image Buffer Provider Client Interface
*******************************************************************************/
class IImgBufProviderClient : public virtual RefBase
{
public:     ////                Interface.
    virtual                     ~IImgBufProviderClient() {}
    //
    /**
     * Notify when IImgBufProvider is created.
     */
    virtual bool                onImgBufProviderCreated(sp<IImgBufProvider>const& rpProvider)   = 0;
    /**
     * Notify when IImgBufProvider is destroyed.
     */
    virtual void                onImgBufProviderDestroyed(int32_t const i4ProviderId)           = 0;
};


/******************************************************************************
*   Image Buffer Processor Interface
*******************************************************************************/
class IImgBufProcessor : public virtual RefBase
{
public:     ////                Interface.
    virtual                     ~IImgBufProcessor() {}
    //
    /*
     *  Usage:
     *         enqueProcessor()                     <============> if needed
     *      -> startProcessor()
     *      -> REPEAT:[ enqueProcessor() -> dequeProcessor() ]
     *      -> pauseProcessor() -> flushProcessor() <============> if needed
     *      -> stopProcessor()
     *
     *  dequeProcessor() will block until:
     *  (1) queue is not empty, (2) pauseProcessor(), or (3) stopProcessor().
     */
    virtual bool                enqueProcessor(ImgBufQueNode const& rNode)      = 0;
    virtual bool                dequeProcessor(Vector<ImgBufQueNode>& rvNode)   = 0;
    //
    virtual bool                startProcessor()                                = 0;
    virtual bool                pauseProcessor()                                = 0;
    virtual bool                flushProcessor()                                = 0;
    virtual bool                stopProcessor()                                 = 0;
    // add to examine if the processor is paused.
    virtual bool                isProcessorRunning()                            = 0;
};


/******************************************************************************
*   Image Buffer Queue Interface
*******************************************************************************/
class IImgBufQueue : public IImgBufProvider, public IImgBufProcessor
{
};


/******************************************************************************
*
*******************************************************************************/
};  // namespace MtkCamUtils
};  // namespace android
#endif  //_MTK_CAMERA_INC_COMMON_CAMUTILS_IIMGBUFQUEUE_H_

