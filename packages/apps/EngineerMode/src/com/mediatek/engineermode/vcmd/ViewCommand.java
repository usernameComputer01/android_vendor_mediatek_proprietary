

package com.mediatek.engineermode.vcmd;

import android.app.Activity;
import android.view.View;

public class ViewCommand {
    public int rid;
    public String cmdPath;
    public Adapter adapter;
    public ViewCommand() {}

    public ViewCommand(int resid, String path, Adapter adp) {
        rid = resid;
        cmdPath = path;
        adapter = adp;
    }

    public interface Adapter {
        public boolean doViewAction(Activity host, ViewCommand vc);

        public boolean setViewByCmdVal(Activity host, ViewCommand vc);

        public Class<? extends View> getViewType();
    }
}
