package au.com.emjeek.heychatgpt;

import android.app.Activity;
import android.os.Bundle;

public class LaunchActivity extends Activity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        setShowWhenLocked(true);
        setTurnScreenOn(true);
        ChatGptLauncher.launch(this);
        finish();
        overridePendingTransition(0, 0);
    }
}
