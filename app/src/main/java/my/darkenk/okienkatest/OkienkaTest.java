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

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_long_name);
        setContentView(R.layout.okienko);
        mDesktop = (ViewGroup)findViewById(R.id.activity_view);
        mMediaRouter = (MediaRouter)getSystemService(Context.MEDIA_ROUTER_SERVICE);
        mDisplayManager = (DisplayManager)getSystemService(Context.DISPLAY_SERVICE);
        // List of the available displays
        while (true) {
            Display display = mDisplayManager.getDisplay(mTotalDisplays);
            if (display == null)
                break;
            mTotalDisplays++;
            Log.v(TAG, "Found display " + display);
        }
//        mSecondaryTouch = SystemProperties.get("persist.secondary.touch", "none");
        mSecondaryTouch = System.getProperty("persist.secondary.touch", "none");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Intent filter = new Intent(Intent.ACTION_MAIN, null);
        filter.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(filter, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            ApplicationInfo ai = resolveInfo.activityInfo.applicationInfo;
            MenuItem mi = menu.add(ai.loadLabel(getPackageManager()));
            mi.setIntent(getPackageManager().getLaunchIntentForPackage(ai.packageName));
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mCount == 0) {
            Log.v(TAG, "Primary display: " + item.getIntent());
            mPrimaryApp = new Okienko(OkienkaTest.this, mDesktop, item.getIntent());
        } else if ((mCount == 1) && (mTotalDisplays >= 2)) {
            Log.v(TAG, "Secondary display: " + item.getIntent());
            mSecondaryApp = mPresentation.setApp(item.getIntent());
            // Compute secondary input device scaling factor
            mScaleX = (float)mDisplayManager.getDisplay(1).getWidth() /
                mDisplayManager.getDisplay(0).getWidth();
            mScaleY = (float)mDisplayManager.getDisplay(1).getHeight() /
                mDisplayManager.getDisplay(0).getHeight();
        } else {
            Log.v(TAG, "Discard: " + item.getIntent());
        }
        mCount++;
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!mSecondaryTouch.equals("none")) {
            if ((mSecondaryApp != null) && (event.getDevice().getName().equals(mSecondaryTouch))) {
                // Scale resolution from primary to secondary display
                float x, y;
                x = event.getX() * mScaleX;
                y = event.getY() * mScaleY;
                event.setLocation(x, y);
                return mSecondaryApp.getView().dispatchTouchEvent(event);
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
