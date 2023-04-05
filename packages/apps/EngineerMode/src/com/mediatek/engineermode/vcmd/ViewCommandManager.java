

package com.mediatek.engineermode.vcmd;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.mediatek.engineermode.Elog;

import android.app.Activity;



public class ViewCommandManager {
    private static final String TAG = "ViewCommandManager";
    private Activity mHost;
    private HashMap<Integer, ViewCommand> mViewCmdSet;
    public ViewCommandManager(Activity activity) {
        mHost = activity;
        mViewCmdSet = new HashMap<Integer, ViewCommand>();
    }

    public void initViewCmdFromList(List<ViewCommand> list) {
        for (ViewCommand vc : list) {
            if (vc == null) {
                continue;
            }
            mViewCmdSet.put(vc.rid, vc);
        }
    }

    public void initViewCmdFromArray(ViewCommand[] viewCmds) {
        if (viewCmds == null) {
            return;
        }
        for (int i = 0; i < viewCmds.length; i++) {
            if (viewCmds[i] == null) {
                continue;
            }

            ViewCommand vc = viewCmds[i];
            mViewCmdSet.put(vc.rid, vc);
        }
    }

    public boolean handleViewAction(int rid) {
        ViewCommand vc = mViewCmdSet.get(rid);
        if (vc == null) {
            Elog.d(TAG, "Invalid rid:" + rid);
            return false;
        }
        if (vc.adapter == null) {
            return false;
        }
        return vc.adapter.doViewAction(mHost, vc);
    }

    public boolean handleAllViewAction() {
        Collection<ViewCommand> vcs = mViewCmdSet.values();
        boolean flag = true;
        for (ViewCommand vc : vcs) {
            if (vc.adapter == null) {
                flag = false;
                continue;
            }
            if (!vc.adapter.doViewAction(mHost, vc)) {
                flag = false;
            }
        }
        return flag;
    }

    public boolean setViewByVal(int rid) {
        ViewCommand vc = mViewCmdSet.get(rid);
        if (vc == null) {
            Elog.d(TAG, "Invalid rid:" + rid);
            return false;
        }
        if (vc.adapter == null) {
            return false;
        }
        return vc.adapter.setViewByCmdVal(mHost, vc);
    }

    public boolean setAllViewByVal() {
        Collection<ViewCommand> vcs = mViewCmdSet.values();
        boolean flag = true;
        for (ViewCommand vc : vcs) {
            if (vc.adapter == null) {
                Elog.d(TAG, "adapter is null; vc.cmdPath:" + vc.cmdPath);
                flag = false;
                continue;
            }
            if (!vc.adapter.setViewByCmdVal(mHost, vc)) {
                Elog.d(TAG, "SetViewByCmdVal fail; vc.cmdPath:" + vc.cmdPath);
                flag = false;
            }
        }
        return flag;
    }

    public void clearRes() {
        mViewCmdSet.clear();
    }

    public  Collection<ViewCommand> getViewCommandSet() {
        return mViewCmdSet.values();
    }

}
