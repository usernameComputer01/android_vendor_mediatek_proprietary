
package com.mediatek.engineermode.audio;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioSystem;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mediatek.engineermode.Elog;
import com.mediatek.engineermode.R;

import java.util.ArrayList;

/** The Audio activity which is a entry of Audio debug functions. */
public class Audio extends Activity implements OnItemClickListener {
    /**
     * . CURRENT_MODE is in order to distinguish the headset, normal and loud
     * speaker mode
     */
    public static final String CURRENT_MODE = "CurrentMode";
    public static final String ENHANCE_MODE = "is_enhance";
    public static final String AUDIO_VERSION_COMMAND = "GET_AUDIO_VOLUME_VERSION";
    public static final String AUDIO_VERSION_1 = "GET_AUDIO_VOLUME_VERSION=1";
    private static final String AUDIO_TURNING_VER = "ro.mtk_audio_tuning_tool_ver";
    private static final String AUDIO_TURNING_VER_V2_1 = "V2.1";
    /** Log Tag. */
    public static final String TAG = "EM/Audio";
    /** Used for start NORMAL MODE activity. */
    private static final int NORMAL_MODE = 0;
    /** Used for start HEADSET MODE activity. */
    private static final int HEADSET_MODE = 1;
    /** Used for start LOUDSPEAKER MODE activity. */
    private static final int LOUDSPEAKER_MODE = 2;
    /** Used for start HEADSET LOUDSPEAKER MODE activity. */
    private static final int HEADSET_LOUDSPEAKER_MODE = 3;
    /** Used for start SPEECH ENHANCE activity. */
    private static final int SPEECH_ENHANCE = 4;
    /** Used for start DEBUG INFO activity. */
    private static final int DEBUG_INFO = 5;
    private static final int DEBUG_SESSION = 6;
    /** Used for start SPEECH LOGGER activity. */
    private static final int SPEECH_LOGGER = 7;
    /** Used for start AUDIO LOGGER activity. */
    private static final int AUDIO_LOGGER = 8;
    private static final int AUDIO_WAKELOCK = 9;
    private String mVersion;
    private boolean mAudioTurnV2 = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio);
        mAudioTurnV2 = initAudioTurnVer();

        ListView mAudioFunctionList = (ListView) findViewById(R.id.ListView_Audio);
        String[] strArr = getResources().getStringArray(R.array.audio_functions);
        ArrayList<String> list = new ArrayList<String>();
        for (String str : strArr) {
            list.add(str);
        }
        if (mAudioTurnV2) {
            list.remove(DEBUG_INFO);
        }
        mVersion = AudioSystem.getParameters(AUDIO_VERSION_COMMAND);
        Elog.d(TAG, mVersion);
        if (!AUDIO_VERSION_1.equals(mVersion)) {
            list.remove(HEADSET_LOUDSPEAKER_MODE);
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);
        mAudioFunctionList.setAdapter(adapter);
        mAudioFunctionList.setOnItemClickListener(this);
    }

    private boolean initAudioTurnVer() {
        String value = SystemProperties.get(AUDIO_TURNING_VER);
        if ((value != null) && (AUDIO_TURNING_VER_V2_1.equalsIgnoreCase(value))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Click List view items to start different activity.
     *
     * @param arg0
     *            : View adapter.
     * @param arg1
     *            : Selected view.
     * @param arg2
     *            : Selected view's position.
     * @param arg3
     *            : Selected view's id
     */
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        final Intent intent = new Intent();
        if (AUDIO_VERSION_1.equals(mVersion)) {
            // New chip: normal mode, headset mode, loudspeaker mode and headset_loudspeaker mode
            if (arg2 <= HEADSET_LOUDSPEAKER_MODE) {
                intent.setClass(this, AudioModeSetting.class);
                intent.putExtra(CURRENT_MODE, arg2);
                intent.putExtra(ENHANCE_MODE, true);
                startActivity(intent);
                return;
            }
        } else {
            // Normal mode, headset mode and loudspeaker mode
            if (arg2 <= LOUDSPEAKER_MODE) {
                intent.setClass(this, AudioModeSetting.class);
                intent.putExtra(CURRENT_MODE, arg2);
                intent.putExtra(ENHANCE_MODE, false);
                startActivity(intent);
                return;
            } else {
                // Tricky. Shift the index because we have removed a list entry in onCreate()
                arg2++;
            }
        }

        if ((mAudioTurnV2 && (arg2 >= DEBUG_INFO))) {
                arg2++;
        }
        switch (arg2) {
        case SPEECH_ENHANCE:
            if (mAudioTurnV2) {
                intent.setClass(this, AudioSpeechEnhancementV2.class);

            } else {
                intent.setClass(this, AudioSpeechEnhancementV1.class);
            } 
            this.startActivity(intent);
            break;
        //case DEBUG_INFO:
          //  break;
        //case DEBUG_SESSION:
          //  break;
        //case SPEECH_LOGGER:
          //  break;
        // to make user mode can dump audio data
        //case AUDIO_LOGGER:
          //  break;
        //case AUDIO_WAKELOCK:
          //  intent.setClass(this, AudioWakeLock.class);
            //this.startActivity(intent);
            //break;
        default:
            break;
        }
    }

}
