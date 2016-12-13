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

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ActivityViewWrapper {

    private final String TAG = "OkienkaWrapper";
    private Constructor<?> mDefaultCtor;
    private View mActivityView;
    private Method mStartActivityMethod;

    public ActivityViewWrapper(Context ctx) {
        try {
            // Returns the Class object associated with the class or interface with the given string name.
            // 加载类，默认会执行初始化块
            Class<?> clazz = Class.forName("android.app.ActivityView");
            Log.d(TAG, "ActivityViewWrapper: clazz: " + clazz.toString());
            // java.lang.Class.getConstructor() 方法返回一个Constructor对象，它反映此Class对象所表示的类的指定公共构造。
            mDefaultCtor = clazz.getConstructor(Context.class);
            Log.d(TAG, "ActivityViewWrapper: mDefaultCtor: " + mDefaultCtor.toString());
            mActivityView = (View)mDefaultCtor.newInstance(ctx);
            Log.d(TAG, "ActivityViewWrapper: mActivityView: " + mActivityView.toString());
            // Returns a Method object that reflects the specified public member method of the class or interface represented by this Class object.
            mStartActivityMethod = clazz.getMethod("startActivity", Intent.class);
            Log.d(TAG, "ActivityViewWrapper: mStartActivityMethod: " + mStartActivityMethod);
        } catch (Exception e) {
            Log.e(TAG, "ActivityViewWrapper failed " + e);
        }
    }

    public void startActivity(Intent intent) {
        try {
            Log.d(TAG, "startActivity: intent:" + intent);
            mStartActivityMethod.invoke(mActivityView, intent);
        } catch (Exception e) {
            Log.e(TAG, "ActivityViewWrapper failed startActivity " + e);
        }
    }

    public View getActivityView() {
        return mActivityView;
    }
}
