/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package my.darkenk.okienkatest;

import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * <p>
 * A {@link android.app.Presentation} used to demonstrate interaction between primary and
 * secondary screens.
 * </p>
 * <p>
 * It displays the name of the display in which it has been embedded (see
 * {@link android.app.Presentation#getDisplay()}) and exposes a facility to change its
 * background color and display its text.
 * </p>
 */
public class SamplePresentation extends Presentation {

    ViewGroup mDesktop;
    Context mContext;

    public SamplePresentation(Context outerContext, Display display) {
        super(outerContext, display);
        mContext = outerContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content view to the custom layout
        // Inflate a layout.
        setContentView(R.layout.okienko);
        mDesktop = (ViewGroup)findViewById(R.id.activity_view);
    }

    /**
     * Set the application to run on the Presentation display.
     *
     * @param intent Application start Intent
     */
    public Okienko setApp(Intent intent) {
        return new Okienko(mContext, mDesktop, intent);
    }
}
