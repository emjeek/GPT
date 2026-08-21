package com.emjeek.chatgptvoicebridge;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String GPT_PACKAGE = "com.openai.chatgpt";
    private static final ComponentName GPT_VOICE_COMPONENT = new ComponentName(
            GPT_PACKAGE, "com.openai.voice.assistant.AssistantActivity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        launchVoice();
        finishAndRemoveTask();
    }

    private void launchVoice() {
        try {
            PackageManager pm = getPackageManager();
            ActivityInfo info = pm.getActivityInfo(GPT_VOICE_COMPONENT, PackageManager.ComponentInfoFlags.of(0));
            if (info.exported && info.permission == null) {
                startActivity(new Intent()
                        .setComponent(GPT_VOICE_COMPONENT)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return;
            }
        } catch (Throwable ignored) {
        }

        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://chat.com/?mode=voice"))
                    .setPackage(GPT_PACKAGE)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            return;
        } catch (Throwable ignored) {
        }

        Toast.makeText(this, "Could not launch ChatGPT Voice. Make sure ChatGPT is installed.", Toast.LENGTH_LONG).show();
    }
}
