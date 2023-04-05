

package com.mediatek.engineermode.vcmd;

import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public abstract class AbstrViewCmdActivity extends Activity implements OnCheckedChangeListener {

    private ViewCommandManager mVcMngr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVcMngr = new ViewCommandManager(this);
        mVcMngr.initViewCmdFromList(initViewCmds());
    }

    protected ViewCommandManager getViewCommandManager() {
        if (mVcMngr == null) {
            throw new RuntimeException("please get ViewCommandManger after supper activity was created");
        }
        return mVcMngr;
    }

    abstract protected List<ViewCommand> initViewCmds();

    protected void registListeners() {
        Collection<ViewCommand> viewCmds = mVcMngr.getViewCommandSet();
        for (ViewCommand vc : viewCmds) {
            if (vc.adapter == null) {
                continue;
            }
            if (vc.adapter.getViewType() == CheckBox.class) {
                CheckBox cb = (CheckBox) findViewById(vc.rid);
                cb.setOnCheckedChangeListener(this);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mVcMngr.setAllViewByVal()) {
            Toast.makeText(this, "fail to load data", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        mVcMngr.clearRes();
        super.onDestroy();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (mVcMngr.handleViewAction(id)) {
            Toast.makeText(this, "set success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "set fail", Toast.LENGTH_SHORT).show();
        }
    }

}
