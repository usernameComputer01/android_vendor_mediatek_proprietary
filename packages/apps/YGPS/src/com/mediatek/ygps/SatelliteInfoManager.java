
package com.mediatek.ygps;

import java.util.ArrayList;
import java.util.List;



public class SatelliteInfoManager {

    private static final String TAG = "SatelliteInfoManager";
    public static final int PRN_ANY = -1;
    public static final int PRN_ALL = -2;

    List<SatelliteInfo> mSatelInfoList;

    public SatelliteInfoManager() {
        mSatelInfoList = new ArrayList<SatelliteInfo>();
    }

    public void updateSatelliteInfo(SatelliteInfoAdapter adapter) {
        if (mSatelInfoList != null) {
            mSatelInfoList.clear();
        } else {
            mSatelInfoList = new ArrayList<SatelliteInfo>();
        }
        for (SatelliteInfo si : adapter) {
            mSatelInfoList.add(si);
        }
    }

    public List<SatelliteInfo> getSatelInfoList() {
        return mSatelInfoList;
    }

    public SatelliteInfo getSatelliteInfo(int prn) {
        for (SatelliteInfo si : mSatelInfoList) {
            if (si.prn == prn) {
                return si;
            }
        }
        return null;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{Satellite Count:").append(mSatelInfoList.size());
        for (SatelliteInfo info : mSatelInfoList) {
            builder.append(info.toString());
        }
        builder.append("}");
        return builder.toString();
    }

    public void clearSatelInfos() {
        mSatelInfoList.clear();
    }

    public boolean isUsedInFix(int prn) {
        boolean result = false;
        if (prn == PRN_ALL && mSatelInfoList.size() > 0) {
            result = true;
        }
        for (SatelliteInfo si : mSatelInfoList) {
            if (prn == PRN_ALL) {
                if (!si.usedInFix) {
                    result = false;
                    break;
                }
            } else if (prn == PRN_ANY) {
                if (si.usedInFix) {
                    result = true;
                    break;
                }
            } else if (prn == si.prn) {
                result = si.usedInFix;
                break;
            }
        }
        return result;
    }
}
