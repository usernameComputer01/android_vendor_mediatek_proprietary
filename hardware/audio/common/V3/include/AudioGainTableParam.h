#ifndef _AUDIO_GAIN_TABLE_PARAM_H_
#define _AUDIO_GAIN_TABLE_PARAM_H_

#include <system/audio.h>
#include <vector>
#include <string>

#define PLAY_DIGI_AUDIOTYPE_NAME "PlaybackVolDigi"
#define PLAY_ANA_AUDIOTYPE_NAME "PlaybackVolAna"
#define SPEECH_VOL_AUDIOTYPE_NAME "SpeechVol"
#define REC_VOL_AUDIOTYPE_NAME "RecordVol"
#define VOIP_VOL_AUDIOTYPE_NAME "VoIPVol"
#define VOLUME_AUDIOTYPE_NAME "Volume"
#define GAIN_MAP_AUDIOTYPE_NAME "VolumeGainMap"

// VOLUME INDEX
#define GAIN_MAX_VOL_INDEX (15) // index will change from 0~18 total 19 step
#define GAIN_VOL_INDEX_SIZE (GAIN_MAX_VOL_INDEX + 1)

#define GAIN_MAX_SPEECH_VOL_INDEX (6)

// STREAM TYPE
#define GAIN_MIN_STREAM_TYPE (AUDIO_STREAM_VOICE_CALL)
#define GAIN_MAX_STREAM_TYPE (AUDIO_STREAM_ACCESSIBILITY)
#define GAIN_STREAM_TYPE_SIZE (GAIN_MAX_STREAM_TYPE + 1)

// DEVICE
enum GAIN_DEVICE{
    GAIN_DEVICE_NONE = -1,
    GAIN_DEVICE_EARPIECE = 0,
    GAIN_DEVICE_HEADSET,
    GAIN_DEVICE_SPEAKER,
    GAIN_DEVICE_HSSPK,  // headset with speaker
    GAIN_DEVICE_HEADSET_5POLE,
    GAIN_DEVICE_HAC,
    GAIN_DEVICE_BT,
    NUM_GAIN_DEVICE,
};

// GAIN_ANA_TYPE
enum GAIN_ANA_TYPE
{
    GAIN_ANA_NONE = -1,
    GAIN_ANA_HANDSET = 0,   // mtk codec voice buffer
    GAIN_ANA_HEADPHONE,     // mtk codec audio buffer
    GAIN_ANA_SPEAKER,       // mtk codec speaker amp
    GAIN_ANA_LINEOUT,       // mtk codec linout buffer
    NUM_GAIN_ANA_TYPE
};

// SPEECH
enum GAIN_SPEECH_BAND{
    GAIN_SPEECH_NB,
    GAIN_SPEECH_WB,
    NUM_GAIN_SPEECH_BAND
};

// MIC
enum GAIN_MIC_MODE{
    GAIN_MIC_INVALID = -1,
    Idle_Normal_Record = 0,     // Record, AUDIO_SOURCE_MIC, Sound recording, rcv  (else)
    Idle_Headset_Record = 1,        // Record, AUDIO_SOURCE_MIC, Sound recording, hs   (else)
    Voice_Rec_Mic_Handset = 2,      // Record, AUDIO_SOURCE_VOICE_RECOGNITION, Voice recognition & CTS verifier, rcv
    Voice_Rec_Mic_Headset = 3,      // Record, AUDIO_SOURCE_VOICE_RECOGNITION, Voice recognition & CTS verifier, hs
    Idle_Video_Record_Handset = 4,  // Record, AUDIO_SOURCE_CAMCORDER, Camera recording, rcv
    Idle_Video_Record_Headset = 5,  // Record, AUDIO_SOURCE_CAMCORDER, Camera recording, hs
    Normal_Mic = 6,                 // Speech, NB, RCV
    Headset_Mic = 7,                // Speech, NB, HS
    Handfree_Mic = 8,               // Speech, NB, SPK
    Normal_WB_Mic = 9,              // Speech, WB, RCV
    Headset_WB_Mic = 10,             // Speech, WB, HS
    Handfree_WB_Mic = 11,            // Speech, WB, SPK
    VOIP_Normal_Mic = 12,            // VoIP, Handset
    VOIP_Headset_Mic = 13,           // VoIP, Headset
    VOIP_Handfree_Mic = 14,          // VoIP, Hands-free
    TTY_CTM_Mic,                // Record,
    Level_Shift_Buffer_Gain,    // Record,
    Analog_PLay_Gain,           // Record,
    Voice_UnLock_Mic_Handset,   // Record, AUDIO_SOURCE_VOICE_UNLOCK, , rcv
    Voice_UnLock_Mic_Headset,   // Record, AUDIO_SOURCE_VOICE_UNLOCK, ,hs
    Customization1_Mic_Handset, // Record, AUDIO_SOURCE_CUSTOMIZATION1, ASR improvement,rcv
    Customization1_Mic_Headset, // Record, AUDIO_SOURCE_CUSTOMIZATION1, ASR improvement,hs
    Customization2_Mic_Handset, // Record, AUDIO_SOURCE_CUSTOMIZATION2, , rcv
    Customization2_Mic_Headset, // Record, AUDIO_SOURCE_CUSTOMIZATION2, ,hs
    Customization3_Mic_Handset, // Record, AUDIO_SOURCE_CUSTOMIZATION3, ,rcv
    Customization3_Mic_Headset, // Record, AUDIO_SOURCE_CUSTOMIZATION3, , hs
    NUM_GAIN_MIC_MODE
};

struct GainTableUnit
{
    unsigned char digital;
    unsigned char analog[NUM_GAIN_ANA_TYPE];
};

struct GainTableSidetoneUnit
{
    unsigned char gain;
};

struct GainTableMicUnit
{
    unsigned char gain;
};

struct GainTableParam
{
    GainTableUnit         streamGain[GAIN_STREAM_TYPE_SIZE][NUM_GAIN_DEVICE][GAIN_VOL_INDEX_SIZE];
    GainTableUnit         speechGain[NUM_GAIN_SPEECH_BAND][NUM_GAIN_DEVICE][GAIN_VOL_INDEX_SIZE];
    GainTableSidetoneUnit sidetoneGain[NUM_GAIN_SPEECH_BAND][NUM_GAIN_DEVICE];
    GainTableMicUnit      micGain[NUM_GAIN_MIC_MODE];
};

struct GainTableSpec
{
    int keyStepPerDb;
    float keyDbPerStep;
    float keyVolumeStep;

    int digiDbMax;
    int digiDbMin;

    int sidetoneIdxMax;
    int sidetoneIdxMin;

    int micIdxMax;
    int micIdxMin;

    unsigned int numBufferGainLevel;
    std::vector<short> bufferGainDb;
    std::vector<short> bufferGainIdx;
    std::vector<std::string> bufferGainString;
    int bufferGainPreferMaxIdx;

    unsigned int numSpkGainLevel;
    std::vector<short> spkGainDb;
    std::vector<short> spkGainIdx;
    std::vector<std::string> spkGainString;

    std::string spkLMixerName;
    std::string spkRMixerName;
    GAIN_ANA_TYPE spkAnaType;

    std::vector<short> swagcGainMap;
    std::vector<short> ulPgaGainMap;
    std::vector<std::string> ulPgaGainString;
    int ulGainOffset;
    int ulPgaGainMapMax;
    int ulHwPgaIdxMax;

    std::vector<short> stfGainMap;
};

#endif   //_AUDIO_GAIN_TABLE_PARAM_H_

