package tw.com.tp6gl4cj86.olis_number;

import android.content.Context;

/**
 * Created by tp6gl4cj86 on 2016/12/16.
 */

public class OlisNumberObject
{

    private int mScreenWidth;
    private int mScreenHeight;

    private float mWidth = 375;

    public void init(Context context, float width)
    {
        mWidth = width;

        init(context);
    }

    public void init(Context context)
    {
        final int w = context.getResources()
                             .getDisplayMetrics().widthPixels;
        final int h = context.getResources()
                             .getDisplayMetrics().heightPixels;

        mScreenWidth = Math.min(w, h);
        mScreenHeight = Math.max(w, h);
    }

    private float getWidthRatio()
    {
        return getScreenWidth() / mWidth;
    }

    public int getScreenWidth()
    {
        return mScreenWidth;
    }

    public int getScreenHeight()
    {
        return mScreenHeight;
    }

    public int getPX(float px)
    {
        return (int) (px * getWidthRatio());
    }

}
