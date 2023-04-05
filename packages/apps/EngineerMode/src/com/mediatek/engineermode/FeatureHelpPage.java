
package com.mediatek.engineermode;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class FeatureHelpPage extends Activity {

    public static final String HELP_TITLE_KEY = "helpTitle";
    public static final String HELP_MESSAGE_KEY = "helpMessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_page);
        Intent intent = getIntent();
        Resources resources = getResources();
        String helpTitle = "";
        String helpMsg = "";
        if (intent != null) {
            helpTitle =
                resources.getString(intent.getIntExtra(HELP_TITLE_KEY,
                    R.string.help));
            helpMsg =
                resources.getString(intent.getIntExtra(HELP_MESSAGE_KEY,
                    R.string.help));
        }
        TextView mTitleView = (TextView) findViewById(R.id.textview_title);
        mTitleView.setText(helpTitle);
        TextView mMessageView = (TextView) findViewById(R.id.textview_help);
        mMessageView.setText(helpMsg);
        mMessageView.setHorizontallyScrolling(false);
        // Make text view can vertical scrolling.
        mMessageView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }
}
