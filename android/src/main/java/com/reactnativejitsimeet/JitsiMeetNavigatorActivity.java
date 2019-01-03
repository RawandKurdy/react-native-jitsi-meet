package com.reactnativejitsimeet;

import java.util.Map;
import java.util.HashMap;

import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.core.PermissionListener;

import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.JitsiMeetViewListener;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.ReactActivityLifecycleCallbacks;

public class JitsiMeetNavigatorActivity extends AppCompatActivity implements JitsiMeetViewListener, JitsiMeetActivityInterface {
    private JitsiMeetView view;

    @Override
    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        ReactActivityLifecycleCallbacks.requestPermissions(this, permissions, requestCode, listener);
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            final String[] permissions,
            final int[] grantResults) {
        ReactActivityLifecycleCallbacks.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {
        ReactActivityLifecycleCallbacks.onActivityResult(
                this, requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (!ReactActivityLifecycleCallbacks.onBackPressed()) {
            // Invoke the default handler if it wasn't handled by React.
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String url = getIntent().getStringExtra("url");
        String jwt = getIntent().getStringExtra("jwt");
        view = new JitsiMeetView(this);
        view.setListener(this);
        Bundle config = new Bundle();
        config.putBoolean("startWithAudioMuted", false);
        config.putBoolean("startWithVideoMuted", false);
        Bundle urlObject = new Bundle();
        urlObject.putBundle("config", config);
        urlObject.putString("url", url);
        urlObject.putString("jwt", jwt);
        view.loadURLObject(urlObject);

        setContentView(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        view.dispose();
        view = null;

        ReactActivityLifecycleCallbacks.onHostDestroy(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        ReactActivityLifecycleCallbacks.onNewIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ReactActivityLifecycleCallbacks.onHostResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        ReactActivityLifecycleCallbacks.onHostPause(this);
    }

    private void on(String name, Map<String, Object> data) {
        UiThreadUtil.assertOnUiThread();

        // Log with the tag "ReactNative" in order to have the log
        // visible in react-native log-android as well.
        Log.d(
            "JitsiMeet",
            JitsiMeetViewListener.class.getSimpleName() + " "
                + name + " "
                + data);
        Intent intent = new Intent(name);
        intent.putExtra("data", (HashMap<String, Object>) data);
        sendBroadcast(intent, getApplication().getPackageName() + ".permission.JITSI_BROADCAST");
    }

    public void onConferenceFailed(Map<String, Object> data) {
        on("CONFERENCE_FAILED", data);
    }

    public void onConferenceJoined(Map<String, Object> data) {
        on("CONFERENCE_JOINED", data);
    }

    public void onConferenceLeft(Map<String, Object> data) {
        this.onBackPressed();
        on("CONFERENCE_LEFT", data);
    }

    public void onConferenceWillJoin(Map<String, Object> data) {
        on("CONFERENCE_WILL_JOIN", data);
    }

    public void onConferenceWillLeave(Map<String, Object> data) {
        on("CONFERENCE_WILL_LEAVE", data);
    }

    public void onLoadConfigError(Map<String, Object> data) {
        on("LOAD_CONFIG_ERROR", data);
    }
}
