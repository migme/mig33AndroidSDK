/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mig33.android.sdk.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.View;

/**
 * An assortment of UI helpers.
 */
public class UIUtils {

	private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;

    @SuppressWarnings("unused")
	private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setActivatedCompat(View view, boolean activated) {
        if (hasHoneycomb()) {
            view.setActivated(activated);
        }
    }

    public static boolean isGoogleTV(Context context) {
	return context.getPackageManager().hasSystemFeature("com.google.android.tv");
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasHoneycombMR2() {
	return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2;
    }

    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isHoneycombTablet(Context context) {
        return hasHoneycomb() && isTablet(context);
    }

//    @SuppressWarnings("deprecation")
//	public static void setBackground(View view, Drawable drawable) {
//	if (view == null) return;
//
//		if (UIUtils.hasJellyBean()) {
//			view.setBackground(drawable);
//		} else {
//			view.setBackgroundDrawable(drawable);
//		}
//	}
//
//    @SuppressWarnings("deprecation")
//	public static void setPluginState(WebSettings websettings, boolean state) {
//		if (websettings == null)
//			return;
//
//		if (UIUtils.hasFroyo()) {
//			websettings.setPluginState(state ? PluginState.ON : PluginState.OFF);
//		} else {
//			websettings.setPluginsEnabled(state);
//		}
//	}
//
//    @SuppressWarnings("deprecation")
//	public static Point getDisplaySize(Display display) {
//		Point outSize = new Point();
//
//		if (UIUtils.hasHoneycombMR2()) {
//			display.getSize(outSize);
//		} else {
//			outSize.x = display.getWidth();
//			outSize.y = display.getHeight();
//		}
//
//		return outSize;
//    }
}
