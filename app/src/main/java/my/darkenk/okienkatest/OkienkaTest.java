/**
 * Copyright (C) 2015, Dariusz Kluska <darkenk@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of the {organization} nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package my.darkenk.okienkatest;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.hardware.display.DisplayManager;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.os.Bundle;
//import android.os.SystemProperties;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

public class OkienkaTest extends Activity {

    private final String TAG = "OkienkaTest";
    private ActivityViewWrapper mActivityViewWrapper;
    private ViewGroup mDesktop;
    private MediaRouter mMediaRouter;
    private DisplayManager mDisplayManager;
    private SamplePresentation mPresentation;
    private Okienko mPrimaryApp;
    private Okienko mSecondaryApp;
    private int mCount = 0;
    private int mTotalDisplays = 0;
    private float mScaleX, mScaleY;
    private String mSecondaryTouch;
    ActionBar actionBar;

    Button button = null;
    Button button1 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_long_name);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//横屏
        setContentView(R.layout.okienko);
        mDesktop = (ViewGroup)findViewById(R.id.activity_view);
        mMediaRouter = (MediaRouter)getSystemService(Context.MEDIA_ROUTER_SERVICE);         // 控制和管理路由的媒体服务
        mDisplayManager = (DisplayManager)getSystemService(Context.DISPLAY_SERVICE);        // 与显示设备交互服务

//         List of the available displays
        while (true) {
            Display display = mDisplayManager.getDisplay(mTotalDisplays);   // Gets information about a logical display.
            if (display == null)
                break;
            mTotalDisplays++;
            Log.v(TAG, "Found display " + display);
        }
        mSecondaryTouch = System.getProperty("persist.secondary.touch", "ft5x06");
        Log.d(TAG, "onCreate: "+mSecondaryTouch);

        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(OkienkaTest.this, MainActivity.class);
//                startActivity(intent);
                Intent tIntent = new Intent(OkienkaTest.this, MainActivity.class);
                Log.v(TAG, "Primary display add " + tIntent.toString());
                mPrimaryApp = new Okienko(OkienkaTest.this, mDesktop, tIntent);
                Log.d(TAG, "onCreate: "+mPrimaryApp);
            }
        });
        button1 = (Button)findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tIntent = new Intent();
                ComponentName tComp = new ComponentName("com.brick.robotctrl", "com.brick.robotctrl.MainActivity");
                tIntent.setComponent(tComp);
//                Intent tIntent = new Intent(OkienkaTest.this, MainActivity.class);
//                Log.v(TAG, "secondary display add " + tIntent.toString());
                mSecondaryApp = mPresentation.setApp(tIntent);
                Log.d(TAG, "onResume: mSecondaryApp" + mSecondaryApp.toString());
            }
        });
//        Timer timer = new Timer(true);
//        timer.schedule(queryTask, 2000, 2000);
    }
//    TimerTask queryTask = new TimerTask() {
//        @Override
//        public void run() {
////            Intent tIntent = new Intent();
////            ComponentName tComp = new ComponentName("com.brick.robotctrl", "com.brick.robotctrl.MainActivity");
////            tIntent.setComponent(tComp);
//                Intent tIntent = new Intent(OkienkaTest.this, MainActivity.class);
//                Log.v(TAG, "secondary display add " + tIntent.toString());
//            mSecondaryApp = mPresentation.setApp(tIntent);
//            Log.d(TAG, "onResume: mSecondaryApp" + mSecondaryApp.toString());
//        }
//    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!mSecondaryTouch.equals("none")) {
            if ((mSecondaryApp != null) && (event.getDevice().getName().equals(mSecondaryTouch))) {
                // Scale resolution from primary to secondary display
//                Log.d(TAG, "dispatchTouchEvent: 1234");
//                float x, y;
//                 x = event.getX();
//                Log.d(TAG, "dispatchTouchEvent: x="+x+"mscalex:"+mScaleX+"getX"+event.getX());
//                y = event.getY();
//                Log.d(TAG, "dispatchTouchEvent: y="+y+"mscaley:"+mScaleY+"getY"+event.getY());
              //  event.setLocation(x, y);
//                return mSecondaryApp.getView().dispatchTouchEvent(event);
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private final MediaRouter.SimpleCallback mMediaRouterCallback =
            new MediaRouter.SimpleCallback() {
                @Override
                public void onRouteSelected(MediaRouter router, int type, RouteInfo info) {
                    updatePresentation();
                }

                @Override
                public void onRouteUnselected(MediaRouter router, int type, RouteInfo info) {
                    updatePresentation();
                }

                @Override
                public void onRoutePresentationDisplayChanged(MediaRouter router, RouteInfo info) {
                    updatePresentation();
                }
            };

    private final DialogInterface.OnDismissListener mOnDismissListener =
            new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (dialog == mPresentation) {
                        mPresentation = null;
                    }
                }
            };

    private void updatePresentation() {
        RouteInfo selectedRoute = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);

        Display selectedDisplay = null;
        if (selectedRoute != null) {
            selectedDisplay = selectedRoute.getPresentationDisplay();
        }

        if (mPresentation != null && mPresentation.getDisplay() != selectedDisplay) {
            mPresentation.dismiss();
            mPresentation = null;
        }

        if (mPresentation == null && selectedDisplay != null) {

            // Initialise a new Presentation for the Display
            mPresentation = new SamplePresentation(this, selectedDisplay);
            mPresentation.setOnDismissListener(mOnDismissListener);

            // Try to show the presentation, this might fail if the display has
            // gone away in the mean time
            try {
                mPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                // Couldn't show presentation - display was already removed
                mPresentation = null;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register a callback for all events related to live video devices
        mMediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, mMediaRouterCallback);
        // Update the displays based on the currently active routes
        updatePresentation();

//        Intent tIntent = new Intent();
//        ComponentName tComp = new ComponentName("com.brick.robotctrl", "com.brick.robotctrl.MainActivity");
//        tIntent.setComponent(tComp);
//        Intent tIntent = new Intent(OkienkaTest.this, MainActivity.class);
//        Log.v(TAG, "secondary display add " + tIntent.toString());
//        mSecondaryApp = mPresentation.setApp(tIntent);
//        Log.d(TAG, "onResume: mSecondaryApp" + mSecondaryApp.toString());
//        mSecondaryApp = mPresentation.setApp(tIntent);
//        Log.d(TAG, "onResume: mSecondaryApp" + mSecondaryApp.toString());
//        Intent tIntent = new Intent();
//        ComponentName tComp = new ComponentName("com.brick.robotctrl", "com.brick.robotctrl.MainActivity");
//        tIntent.setComponent(tComp);
        //Log.v(TAG, "Primary display add" + tIntent.toString());
        button.callOnClick();
        button1.performClick();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop listening for changes to media routes.
        mMediaRouter.removeCallback(mMediaRouterCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Dismiss the presentation when the activity is not visible.
        if (mPresentation != null) {
            mPresentation.dismiss();
            mPresentation = null;
        }
    }
}
