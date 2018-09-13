package com.lyc.idverification.util;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AnimRes;
import android.support.annotation.AnyRes;
import android.support.annotation.BoolRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StringRes;
import android.support.annotation.XmlRes;
import android.util.DisplayMetrics;
import android.view.View;

import com.lyc.idverification.app.App;


public class ResourceUtil {
    public static Resources mResources;

    public static Resources getResources() {
        if (null == mResources) {
            synchronized (Resources.class) {
                if (null == mResources) {
                    mResources = App.getInstance().getResources();
                }
            }
        }
        return mResources;
    }

    //获取Drawable
    public static Drawable getDrawable(@DrawableRes int drawableId) {
        return App.getInstance().getResources().getDrawable(drawableId);
    }

    //获取Drawable
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getDrawable(@DrawableRes int drawableId, @Nullable Resources.Theme theme) {
        return App.getInstance().getResources().getDrawable(drawableId, theme);
    }

    //获取Layout
    public static XmlResourceParser getLayout(@LayoutRes int layoutId) {
        return App.getInstance().getResources().getLayout(layoutId);
    }

    //获取String
    public static String getString(@StringRes int stringId) {
        return App.getInstance().getResources().getString(stringId);
    }

    //获取Color
    public static int getColor(@ColorRes int colorId) {
        return App.getInstance().getResources().getColor(colorId);
    }

    //获取资源id
    public static int getIdentifier(String resourcesName, String resourcesType) {
        //drawable、mipmap、string、color等
        return App.getInstance().getResources().getIdentifier(resourcesName, resourcesType, App.getInstance().getPackageName());
    }

    public static int getIdentifier(String name, String defType, String defPackage) {
        return App.getInstance().getResources().getIdentifier(name, defType, defPackage);
    }

    //根据获取View
    public static View getView(@LayoutRes int resource) {
        return View.inflate(App.getInstance(), resource, null);
    }

    public static int getDimensionPixelOffset(@DimenRes int dimensionPixelOffset) {
        return App.getInstance().getResources().getDimensionPixelOffset(dimensionPixelOffset);
    }

    public static float getDimension(@DimenRes int dimension) {
        return App.getInstance().getResources().getDimension(dimension);
    }

    public static int getDimensionPixelSize(@DimenRes int dimensionPixelSize) {
        return App.getInstance().getResources().getDimensionPixelSize(dimensionPixelSize);
    }

    public static int getInteger(@IntegerRes int integer) {
        return App.getInstance().getResources().getInteger(integer);
    }

    public static XmlResourceParser getAnimation(@AnimRes int animation) {
        return App.getInstance().getResources().getAnimation(animation);
    }

    public static XmlResourceParser getXml(@XmlRes int xml) {
        return App.getInstance().getResources().getXml(xml);
    }

    public static AssetManager getAssets() {
        return App.getInstance().getResources().getAssets();
    }

    public static boolean getBoolean(@BoolRes int booleans) {
        return App.getInstance().getResources().getBoolean(booleans);
    }

    public static ColorStateList getColorStateList(@ColorRes int ColorStateList) {
        return App.getInstance().getResources().getColorStateList(ColorStateList);
    }

    public static Configuration getConfiguration() {
        return App.getInstance().getResources().getConfiguration();
    }

    public static String getResourceTypeName(@AnyRes int resid) {
        return App.getInstance().getResources().getResourceTypeName(resid);
    }

    public static DisplayMetrics getDisplayMetrics() {
        return App.getInstance().getResources().getDisplayMetrics();
    }

    //获取AndroidManifest.xml 中的meta-data值
    public static String getMetaData(String metaDataName){
        ApplicationInfo appInfo = null;
        try {
            appInfo = App.getInstance().getPackageManager().getApplicationInfo(App.getInstance().getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String metaName = appInfo.metaData.getString(metaDataName);
        return metaName;
    }
}
