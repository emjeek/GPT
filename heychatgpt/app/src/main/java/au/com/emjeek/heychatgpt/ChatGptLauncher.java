package au.com.emjeek.heychatgpt;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public final class ChatGptLauncher {
    private static final String PKG = "com.openai.chatgpt";
    private static final ComponentName VOICE = new ComponentName(PKG, "com.openai.voice.assistant.AssistantActivity");

    public static void launch(Context context) {
        try {
            Intent direct = new Intent().setComponent(VOICE).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(direct);
            return;
        } catch (Throwable ignored) {}

        try {
            Intent deep = new Intent(Intent.ACTION_VIEW, Uri.parse("https://chat.com/?mode=voice"))
                    .setPackage(PKG)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(deep);
        } catch (Throwable ignored) {}
    }

    private ChatGptLauncher() {}
}
