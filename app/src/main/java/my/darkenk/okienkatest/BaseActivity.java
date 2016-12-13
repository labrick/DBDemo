package my.darkenk.okienkatest;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.io.IOException;

public abstract class BaseActivity extends AppCompatActivity {
    private String TAG = "BaseActivity";
    //    UserTimer userTimer = null;
    private static int timerOutCount = 0;
    private int screenWidth;
    private int screenHeight;
    protected AudioManager mAudioManager;
    /**
     * 最大声音
     */
    private int mMaxVolume;
    /**
     * 当前声音
     */
    private int mVolume = -1;
    private GestureDetector mGestureDetector;

    public static void clearTimerCount() {
        timerOutCount = 0;
    }

    public static void addTimerCount() {
        timerOutCount++;
    }

    public static int getTimerCount() {
        return timerOutCount;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();       // 屏幕宽（像素，如：480px）
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();      // 屏幕高（像素，如：800p）

        View decorView = getWindow().getDecorView();
//        Hide both the navigation bar and the status bar.
//        SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
//        a general rule, you should design your app to hide the status bar whenever you
//        hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: ");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        clearTimerCount();
        Log.d(TAG, "onResume: ");
        View decorView = getWindow().getDecorView();
//        Hide both the navigation bar and the status bar.
//        SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
//        a general rule, you should design your app to hide the status bar whenever you
//        hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;

        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 手势结束
     */
    private void endGesture() {
        mVolume = -1;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        long[] mHitsL = new long[5];
        long[] mHitsR = new long[5];

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            Log.d(TAG, "onTouch: x:" + x + "y:" + y);

            if (y < screenHeight / 2) {
                if (x < screenWidth / 2) {
                    System.arraycopy(mHitsL, 1, mHitsL, 0, mHitsL.length - 1);
                    mHitsL[mHitsL.length - 1] = SystemClock.uptimeMillis();
                    //LogUtil.d(TAG, "onPreferenceClick:mHits" + mHits[4]+ ","+mHits[3]+"," + mHits[2]+"," + mHits[1]+"," + mHits[0]);
                    if (mHitsL[0] >= (SystemClock.uptimeMillis() - 3000)) {
                        Log.d(TAG,"onPreferenceClick:shutdown");
                        onShutdown();
                    }
                } else {
                    System.arraycopy(mHitsR, 1, mHitsR, 0, mHitsR.length - 1);
                    mHitsR[mHitsR.length - 1] = SystemClock.uptimeMillis();
                    //LogUtil.d(TAG, "onPreferenceClick:mHits" + mHits[4]+ ","+mHits[3]+"," + mHits[2]+"," + mHits[1]+"," + mHits[0]);
                    if (mHitsR[0] >= (SystemClock.uptimeMillis() - 3000)) {
                        Log.d(TAG,"onPreferenceClick:reboot");
                        onReboot();
                    }
                }
            }
            return true;

        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            int y = (int) e2.getRawY();
            Display disp = getWindowManager().getDefaultDisplay();
            int windowWidth = disp.getWidth();
            int windowHeight = disp.getHeight();

//            if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
            onVolumeSlide((mOldY - y) / windowHeight);
//            else if (mOldX < windowWidth / 5.0)// 左边滑动
//                onBrightnessSlide((mOldY - y) / windowHeight);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;

            // 显示
//            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
//            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }

        int nextVolume = (int) (percent * mMaxVolume) + mVolume;
        if (nextVolume > mMaxVolume) {
            nextVolume = mMaxVolume;
        } else if (nextVolume < 0) {
            nextVolume = 0;
        }

        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, nextVolume, 0);

        // 变更进度条
//        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
//        lp.width = findViewById(R.id.operation_full).getLayoutParams().width
//                * nextVolume / mMaxVolume;
//        mOperationPercent.setLayoutParams(lp);
    }

    public void onReboot() {
        try {
            Runtime.getRuntime().exec("su -c \"/system/bin/reboot\"");
        } catch (IOException e) {
            e.printStackTrace();
        }
//        LogUtil.d(TAG, "onReboot: start to reboot");
//        PowerManager pManager=(PowerManager) getSystemService(Context.POWER_SERVICE);
//        pManager.reboot("");
    }

    public void onShutdown() {
//        try {
//            Runtime.getRuntime().exec("su -c \"/system/bin/reboot -p\"");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Intent intent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
//        intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
//        //其中false换成true,会弹出是否关机的确认窗口
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);

//        Intent intent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
//        intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
    }
}
