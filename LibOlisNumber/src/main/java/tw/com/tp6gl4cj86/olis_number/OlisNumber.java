package tw.com.tp6gl4cj86.olis_number;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by tp6gl4cj86 on 2016/12/16.
 */

public class OlisNumber
{

    private static Context context;
    private static int     mScreenWidth;
    private static int     mScreenHeight;
    private static Float   mDensity;

    private static float mWidth = 375;

    public static void init(Context context, float width)
    {
        mWidth = width;

        init(context);
    }

    public static void init(Context context)
    {
        OlisNumber.context = context;

        final int w = context.getResources()
                             .getDisplayMetrics().widthPixels;
        final int h = context.getResources()
                             .getDisplayMetrics().heightPixels;

        mScreenWidth = Math.min(w, h);
        mScreenHeight = Math.max(w, h);
        mDensity = context.getResources()
                          .getDisplayMetrics().density;
    }

    private static float getWidthRatio()
    {
        return getScreenWidth() / mWidth;
    }

    public static int getScreenWidth()
    {
        return mScreenWidth;
    }

    public static int getScreenHeight()
    {
        return mScreenHeight;
    }

    public static int getPX(float px)
    {
        return (int) (px * getWidthRatio());
    }

    public static int dp2px(float dpValue)
    {
        return (int) (dpValue * mDensity + 0.5f);
    }


    /*
    ##     ##  ####  ########   ##      ##
	##     ##   ##   ##         ##  ##  ##
	##     ##   ##   ##         ##  ##  ##
	##     ##   ##   ######     ##  ##  ##
	 ##   ##    ##   ##         ##  ##  ##
	  ## ##     ##   ##         ##  ##  ##
	   ###     ####  ########    ###  ###
	*/

    /**
     * Ignore setWH tag is "null"
     */
    public static void initViewGroupFromXML(View view)
    {
        initViewGroupFromXML(view, null);
        //        if (view != null)
        //        {
        //            initViewFromXML(view);
        //            if (view instanceof ViewGroup)
        //            {
        //                final ViewGroup viewGroup = (ViewGroup) view;
        //                for (int i = 0; i < viewGroup.getChildCount(); i++)
        //                {
        //                    initViewGroupFromXML(viewGroup.getChildAt(i));
        //                }
        //            }
        //        }
    }

    /**
     * Ignore setWH tag is "null"
     */
    //    private static void initViewFromXML(View view)
    //    {
    //        if (view != null && view.getTag(R.id.OlisNumberInited) == null)
    //        {
    //            view.setTag(R.id.OlisNumberInited, "");
    //
    //            view.setTranslationX(OlisNumber.getPX(view.getTranslationX()));
    //            view.setTranslationY(OlisNumber.getPX(view.getTranslationY()));
    //
    //            if (view instanceof TextView)
    //            {
    //                ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, OlisNumber.getPX(((TextView) view).getTextSize()));
    //                ((TextView) view).setCompoundDrawablePadding(OlisNumber.getPX(((TextView) view).getCompoundDrawablePadding()));
    //            }
    //
    //            final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
    //            if (layoutParams != null)
    //            {
    //                if (view.getTag() == null || !"null".equals(view.getTag()))
    //                {
    //                    if (layoutParams.width > 0)
    //                    {
    //                        layoutParams.width = OlisNumber.getPX(layoutParams.width);
    //                    }
    //                    if (layoutParams.height > 0)
    //                    {
    //                        layoutParams.height = OlisNumber.getPX(layoutParams.height);
    //                    }
    //                }
    //
    //                if (layoutParams instanceof ViewGroup.MarginLayoutParams)
    //                {
    //                    ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(OlisNumber.getPX(getLeftMargin((ViewGroup.MarginLayoutParams) layoutParams)), OlisNumber.getPX(getTopMargin((ViewGroup.MarginLayoutParams) layoutParams)), OlisNumber.getPX(getRightMargin((ViewGroup.MarginLayoutParams) layoutParams)), OlisNumber.getPX(getBottomMargin((ViewGroup.MarginLayoutParams) layoutParams)));
    //                }
    //            }
    //            view.setPadding(OlisNumber.getPX(view.getPaddingLeft()), OlisNumber.getPX(view.getPaddingTop()), OlisNumber.getPX(view.getPaddingRight()), OlisNumber.getPX(view.getPaddingBottom()));
    //        }
    //    }

    /**
     * Ignore setWH tag is "null"
     */
    public static void initViewGroupFromXML(View view, OlisNumberObject mOlisNumberObject)
    {
        if (view != null)
        {
            initViewFromXML(view, mOlisNumberObject);
            if (view instanceof ViewGroup)
            {
                final ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++)
                {
                    initViewGroupFromXML(viewGroup.getChildAt(i), mOlisNumberObject);
                }
            }
        }
    }

    /**
     * Ignore setWH tag is "null"
     */
    private static void initViewFromXML(View view, OlisNumberObject mOlisNumberObject)
    {
        if (view != null && view.getTag(R.id.OlisNumberInited) == null)
        {
            view.setTag(R.id.OlisNumberInited, "");

            view.setTranslationX(getPX(mOlisNumberObject, view.getTranslationX()));
            view.setTranslationY(getPX(mOlisNumberObject, view.getTranslationY()));

            if (view instanceof TextView)
            {
                ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, getPX(mOlisNumberObject, ((TextView) view).getTextSize()));
                ((TextView) view).setCompoundDrawablePadding(getPX(mOlisNumberObject, ((TextView) view).getCompoundDrawablePadding()));
            }

            final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null)
            {
                if (view.getTag() == null || !"null".equals(view.getTag()))
                {
                    if (layoutParams.width > 0)
                    {
                        layoutParams.width = getPX(mOlisNumberObject, layoutParams.width);
                    }
                    if (layoutParams.height > 0)
                    {
                        layoutParams.height = getPX(mOlisNumberObject, layoutParams.height);
                    }
                }
                else if ("StatusBar".equals(view.getTag()))
                {
                    layoutParams.height = getStatusBarHeight();
                }

                if (layoutParams instanceof ViewGroup.MarginLayoutParams)
                {
                    ((ViewGroup.MarginLayoutParams) layoutParams).setMargins(getPX(mOlisNumberObject, getLeftMargin((ViewGroup.MarginLayoutParams) layoutParams)), getPX(mOlisNumberObject, getTopMargin((ViewGroup.MarginLayoutParams) layoutParams)), getPX(mOlisNumberObject, getRightMargin((ViewGroup.MarginLayoutParams) layoutParams)), getPX(mOlisNumberObject, getBottomMargin((ViewGroup.MarginLayoutParams) layoutParams)));
                }
            }
            view.setPadding(getPX(mOlisNumberObject, view.getPaddingLeft()), getPX(mOlisNumberObject, view.getPaddingTop()), getPX(mOlisNumberObject, view.getPaddingRight()), getPX(mOlisNumberObject, view.getPaddingBottom()));
        }
    }

    private static int statusBarHeight = -1;

    public static int getStatusBarHeight()
    {
        if (statusBarHeight < 0)
        {
            if (context != null)
            {
                final int resourceId = context.getResources()
                                              .getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0)
                {
                    statusBarHeight = context.getResources()
                                             .getDimensionPixelSize(resourceId);
                }
            }
        }
        return statusBarHeight;
    }

    private static int getPX(OlisNumberObject mOlisNumberObject, float px)
    {
        if (mOlisNumberObject != null)
        {
            return mOlisNumberObject.getPX(px);
        }
        else
        {
            return getPX(px);
        }
    }

    private static int getLeftMargin(ViewGroup.MarginLayoutParams layoutParams)
    {
        if (layoutParams.leftMargin < -OlisNumber.getScreenWidth() || layoutParams.leftMargin > OlisNumber.getScreenWidth())
        {
            return 0;
        }
        return layoutParams.leftMargin;
    }

    private static int getTopMargin(ViewGroup.MarginLayoutParams layoutParams)
    {
        if (layoutParams.topMargin < -OlisNumber.getScreenHeight() || layoutParams.topMargin > OlisNumber.getScreenHeight())
        {
            return 0;
        }
        return layoutParams.topMargin;
    }

    private static int getRightMargin(ViewGroup.MarginLayoutParams layoutParams)
    {
        if (layoutParams.rightMargin < -OlisNumber.getScreenWidth() || layoutParams.rightMargin > OlisNumber.getScreenWidth())
        {
            return 0;
        }
        return layoutParams.rightMargin;
    }

    private static int getBottomMargin(ViewGroup.MarginLayoutParams layoutParams)
    {
        if (layoutParams.bottomMargin < -OlisNumber.getScreenHeight() || layoutParams.bottomMargin > OlisNumber.getScreenHeight())
        {
            return 0;
        }
        return layoutParams.bottomMargin;
    }

}
