#ifndef _MTK_CAMERA_INC_COMMON_CAMUTILS_IMGBUFQUEUE_H_
#define _MTK_CAMERA_INC_COMMON_CAMUTILS_IMGBUFQUEUE_H_
//
#include <utils/threads.h>
#include <utils/List.h>
#include <utils/Vector.h>


/******************************************************************************
*
*******************************************************************************/


namespace android {
namespace MtkCamUtils {
/******************************************************************************
*
*******************************************************************************/


/******************************************************************************
*   Image Buffer Queue
*
*                   enqueProcessor --> [ ToDo Queue ] --> dequeProvider
*               -->                                                     -->
*   [Provider]                                                              [Processor]
*               <--                                                     <--
*                   dequeProcessor <-- [ Done Queue ] <-- enqueProvider
*
*******************************************************************************/
class ImgBufQueue : public IImgBufQueue
{
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//  Implementation.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
public:     ////            Instantiation.
                            ImgBufQueue(
                                int32_t const i4QueueId,
                                char const*const pszQueueName
                            );

protected:  ////            Data Members.
    int32_t const           mi4QueueId;
    char const*const        mpszQueueName;
    char const*             getQueName() const      { return mpszQueueName; }

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//  TODO Queue
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
protected:  ////
    List<ImgBufQueNode>     mTodoImgBufQue;
    mutable Mutex           mTodoImgBufQueMtx;
    String8                 mTodoImgBufQueFmt;

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//  DONE Queue
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
protected:  ////
    Vector<ImgBufQueNode>   mDoneImgBufQue;
    mutable Mutex           mDoneImgBufQueMtx;
    Condition               mDoneImgBufQueCond; //  Condition to wait: [ mDoneImgBufQue.empty() && mbIsProcessorRunning ]

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//  IImgBufProvider.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
public:     ////            Interface.
    virtual int32_t         getProviderId() const   { return mi4QueueId; }
    virtual char const*     getProviderName() const { return mpszQueueName; }

    /*
     *  REPEAT:[ dequeProvider() -> enqueProvider() ]
     *  dequeProvider() returns false immediately if empty.
     */
    virtual bool            dequeProvider(ImgBufQueNode& rNode);
    virtual bool            enqueProvider(ImgBufQueNode const& rNode);

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
    virtual bool            queryProvider(ImgBufQueNode& rNode);
    //
    virtual bool            queryFormat(String8& rFormat);

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//  IImgBufProcessor.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
public:     ////            Interface.
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
    virtual bool            enqueProcessor(ImgBufQueNode const& rNode);
    virtual bool            dequeProcessor(Vector<ImgBufQueNode>& rvNode);
    //
    virtual bool            startProcessor();
    virtual bool            pauseProcessor();
    virtual bool            stopProcessor();
    /*
     *  Move all buffers from TODO queue to DONE queue.
     *  It returns false if Processor is Running; call pauseProcessor() firstly
     *  before flushProcessor().
     */
    virtual bool            flushProcessor();
    virtual bool            isProcessorRunning();

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
protected:  ////            Data Members.
    bool volatile           mbIsProcessorRunning;

};


/******************************************************************************
*
*******************************************************************************/
};  // namespace MtkCamUtils
};  // namespace android
#endif  //_MTK_CAMERA_INC_COMMON_CAMUTILS_IMGBUFQUEUE_H_

