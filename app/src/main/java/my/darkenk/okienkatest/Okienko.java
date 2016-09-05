/**
 * Copyright (C) 2014, Dariusz Kluska <darkenk@gmail.com>
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

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class Okienko extends RelativeLayout {

    ActivityViewWrapper mActivityViewWrapper;
    ViewGroup mWindow;

    /**
     *
     * @param context ctx
     * @param root parent of component
     * @param intent activity to start in Okienko
     */
    // context（本应用）调用intent（要打开的应用），将该layout（要打开的应用）挂载在root（哪个布局下，可以在不同的屏下）下
    public Okienko(Context context, ViewGroup root, final Intent intent) {
        super(context);
        // LayoutInflater是用来找layout下xml布局文件
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        mWindow = (ViewGroup)inflater.inflate(R.layout.okienko_layout, root).findViewById(R.id.okienko_root);
        mWindow.setId(View.generateViewId());
        mActivityViewWrapper = new ActivityViewWrapper(context);
        ((ViewGroup)mWindow.findViewById(R.id.activity)).addView(mActivityViewWrapper.getActivityView());

        this.post(new Runnable() {
            @Override
            public void run() {
                mActivityViewWrapper.startActivity(intent);
            }
        });
    }

    public View getView() {
        return mActivityViewWrapper.getActivityView();
    }
}
