package au.com.emjeek.heychatgpt;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import java.util.ArrayList;
import java.util.Locale;

public class WakeService extends Service implements RecognitionListener {
    private static final String CHANNEL = "hey_chatgpt_listening";
    private static final int NOTIFICATION_ID = 77;
    private static final String PHRASE = "hey chatgpt";
    private final Handler main = new Handler(Looper.getMainLooper());
    private SpeechRecognizer recognizer;
    private Intent recognizerIntent;
    private boolean stopping;
    private boolean launching;

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
        startForeground(NOTIFICATION_ID, buildNotification("Listening for “Hey ChatGPT”"));
        main.post(this::initRecognizer);
    }

    private void initRecognizer() {
        if (stopping) return;
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            updateNotification("Speech recognition is unavailable on this phone");
            stopSelf();
            return;
        }
        try {
            recognizer = SpeechRecognizer.createSpeechRecognizer(this);
            recognizer.setRecognitionListener(this);
            recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    .putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
                    .putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    .putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                    .putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
            startCycle(250);
        } catch (Throwable t) {
            updateNotification("Could not start Android speech recognition");
            stopSelf();
        }
    }

    private void startCycle(long delayMs) {
        main.postDelayed(() -> {
            if (stopping || launching || recognizer == null) return;
            try {
                recognizer.cancel();
            } catch (Throwable ignored) {}
            main.postDelayed(() -> {
                if (stopping || launching || recognizer == null) return;
                try {
                    recognizer.startListening(recognizerIntent);
                    updateNotification("Listening for “Hey ChatGPT”");
                } catch (Throwable t) {
                    startCycle(1000);
                }
            }, 120);
        }, delayMs);
    }

    private void inspect(Bundle results) {
        if (results == null || launching) return;
        ArrayList<String> texts = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (texts == null) return;
        for (String text : texts) {
            if (text == null) continue;
            String normalized = text.toLowerCase(Locale.US)
                    .replaceAll("[^a-z0-9 ]", " ")
                    .replaceAll("\\s+", " ")
                    .trim();
            if (normalized.contains(PHRASE) || normalized.contains("hey chat gpt") || normalized.contains("hey gpt")) {
                trigger();
                return;
            }
        }
    }

    private void trigger() {
        if (launching) return;
        launching = true;
        updateNotification("Opening ChatGPT Voice…");
        try { recognizer.cancel(); } catch (Throwable ignored) {}
        main.postDelayed(() -> {
            try {
                Intent i = new Intent(this, LaunchActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            } catch (Throwable t) {
                ChatGptLauncher.launch(this);
            }
            main.postDelayed(() -> {
                launching = false;
                startCycle(1200);
            }, 5000);
        }, 250);
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) { return START_STICKY; }
    @Override public IBinder onBind(Intent intent) { return null; }

    @Override public void onDestroy() {
        stopping = true;
        main.removeCallbacksAndMessages(null);
        if (recognizer != null) {
            try { recognizer.cancel(); } catch (Throwable ignored) {}
            try { recognizer.destroy(); } catch (Throwable ignored) {}
            recognizer = null;
        }
        super.onDestroy();
    }

    private void createChannel() {
        NotificationChannel c = new NotificationChannel(CHANNEL, "Hey ChatGPT listening", NotificationManager.IMPORTANCE_LOW);
        c.setDescription("Keeps the microphone wake phrase service running");
        getSystemService(NotificationManager.class).createNotificationChannel(c);
    }

    private Notification buildNotification(String text) {
        return new Notification.Builder(this, CHANNEL)
                .setContentTitle("Hey ChatGPT")
                .setContentText(text)
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setOngoing(true)
                .build();
    }

    private void updateNotification(String text) {
        getSystemService(NotificationManager.class).notify(NOTIFICATION_ID, buildNotification(text));
    }

    @Override public void onReadyForSpeech(Bundle params) {}
    @Override public void onBeginningOfSpeech() {}
    @Override public void onRmsChanged(float rmsdB) {}
    @Override public void onBufferReceived(byte[] buffer) {}
    @Override public void onEndOfSpeech() { if (!launching) startCycle(250); }
    @Override public void onError(int error) {
        if (stopping || launching) return;
        long delay = (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) ? 1200 : 350;
        startCycle(delay);
    }
    @Override public void onResults(Bundle results) { inspect(results); if (!launching) startCycle(250); }
    @Override public void onPartialResults(Bundle partialResults) { inspect(partialResults); }
    @Override public void onEvent(int eventType, Bundle params) {}
}
