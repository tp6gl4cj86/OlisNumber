package tw.com.tp6gl4cj86.olis_number;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

/**
 * Created by tp6gl4cj86 on 2016/12/16.
 * <p>
 * 依設計寬度等比縮放尺寸的核心實作。
 * OlisNumber 的靜態方法內部使用一個預設實例；需要不同縮放基準時（例如局部版面），可另外建立實例傳給
 * {@link OlisNumber#initViewGroupFromXML(android.view.View, OlisNumberObject, float)}。
 * <p>
 * 所有 init 系列方法只負責算出寬高，最後都交給 {@link #initWithSize(Context, int, int, float)} 設定。
 */
public class OlisNumberObject
{

    private static final String TAG = "OlisNumber";

    private int mScreenWidth;
    private int mScreenHeight;

    /**
     * 設計寬度：getPX 的縮放比例 = 螢幕寬 / 設計寬度
     */
    private float mWidth = 375;

    /**
     * 預設為系統 density，未初始化前呼叫 getDensity / dp2px 也不會出錯
     */
    private float mDensity = Resources.getSystem()
                                      .getDisplayMetrics().density;

    // ===== 初始化 =====

    /**
     * 直向：以 DisplayMetrics 計算，寬取短邊、高取長邊
     */
    public void init(Context context, float width)
    {
        initFromScreen(context, width, false, false);
    }

    /**
     * 直向：以 DisplayMetrics 計算，寬取短邊、高取長邊；沿用目前的設計寬度
     */
    public void init(Context context)
    {
        init(context, mWidth);
    }

    /**
     * 橫向：以 DisplayMetrics 計算，寬取長邊、高取短邊
     */
    public void initLandscape(Context context, float width)
    {
        initFromScreen(context, width, true, false);
    }

    /**
     * 橫向：以實體螢幕大小（含系統列）計算，寬取長邊、高取短邊。
     * 建議傳入 Activity；傳入 Application 等非 UI context 時改由 DisplayManager 取得預設螢幕大小（見 {@link #getRealSize}）。
     */
    public void initRealLandscape(Context context, float width)
    {
        initFromScreen(context, width, true, true);
    }

    /**
     * init 系列共用：取得螢幕大小後依方向決定寬高（直向寬取短邊、橫向寬取長邊），再交給 initWithSize
     *
     * @param landscape 是否為橫向
     * @param realSize  true 用實體螢幕大小（含系統列），false 用 DisplayMetrics
     */
    private void initFromScreen(Context context, float designWidth, boolean landscape, boolean realSize)
    {
        if (context == null)
        {
            Log.w(TAG, "init: context is null, ignored");
            return;
        }
        final Point size = realSize ? getRealSize(context) : getMetricsSize(context);
        final int longSide = Math.max(size.x, size.y);
        final int shortSide = Math.min(size.x, size.y);
        initWithSize(context, landscape ? longSide : shortSide, landscape ? shortSide : longSide, designWidth);
    }

    /**
     * 以指定寬高初始化，例如 App 實際可用區域（edge-to-edge 時扣掉狀態列 / 瀏海後的大小）。
     * getScreenWidth / getScreenHeight 直接回傳傳入值，getPX 的縮放比例 = width / designWidth。
     * <p>
     * width、height、designWidth 任一 &lt;= 0（或 designWidth 非有限值）時整次忽略並保留原本設定，
     * 避免縮放比例變成 0、Infinity 或 NaN 讓整個版面跑掉；context 為 null 時 density 維持原值。
     *
     * @param width       可用寬度（px）
     * @param height      可用高度（px）
     * @param designWidth width 所對應的設計寬度
     */
    public void initWithSize(Context context, int width, int height, float designWidth)
    {
        if (width <= 0 || height <= 0 || !(designWidth > 0) || Float.isInfinite(designWidth))
        {
            Log.w(TAG, "initWithSize: invalid size width=" + width + ", height=" + height + ", designWidth=" + designWidth + ", ignored");
            return;
        }

        mWidth = designWidth;
        mScreenWidth = width;
        mScreenHeight = height;
        if (context != null)
        {
            mDensity = context.getResources()
                              .getDisplayMetrics().density;
        }
    }

    /**
     * 實體螢幕大小（含系統列）。
     * <ul>
     * <li>Activity（或包著 Activity 的 ContextWrapper）：API 30 起用 WindowMetrics，舊版用 Display.getRealSize</li>
     * <li>非 UI context（例如 Application）：改用 DisplayManager 的預設螢幕，
     * 不透過 WindowManager，避免 API 30 起觸發 StrictMode IncorrectContextUseViolation</li>
     * <li>以上都取不到或發生例外時，退回 DisplayMetrics</li>
     * </ul>
     */
    @SuppressWarnings("deprecation") // getDefaultDisplay / getRealSize：API 30 以下，或非 UI context 時使用
    private static Point getRealSize(Context context)
    {
        try
        {
            final Activity activity = findActivity(context);
            if (activity != null)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                {
                    final Rect bounds = activity.getWindowManager()
                                                .getMaximumWindowMetrics()
                                                .getBounds();
                    return new Point(bounds.width(), bounds.height());
                }
                final Point size = new Point();
                activity.getWindowManager()
                        .getDefaultDisplay()
                        .getRealSize(size);
                return size;
            }

            final DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            final Display display = displayManager != null ? displayManager.getDisplay(Display.DEFAULT_DISPLAY) : null;
            if (display != null)
            {
                final Point size = new Point();
                display.getRealSize(size);
                return size;
            }
        }
        catch (RuntimeException e)
        {
            Log.w(TAG, "getRealSize failed, fallback to DisplayMetrics", e);
        }
        return getMetricsSize(context);
    }

    private static Point getMetricsSize(Context context)
    {
        final DisplayMetrics metrics = context.getResources()
                                              .getDisplayMetrics();
        return new Point(metrics.widthPixels, metrics.heightPixels);
    }

    /**
     * 從 context（含層層包裝的 ContextWrapper）找出 Activity，找不到回傳 null
     */
    private static Activity findActivity(Context context)
    {
        Context current = context;
        while (current instanceof ContextWrapper wrapper)
        {
            if (current instanceof Activity activity)
            {
                return activity;
            }
            current = wrapper.getBaseContext();
        }
        return null;
    }

    // ===== 取值與換算 =====

    private float getWidthRatio()
    {
        return mScreenWidth / mWidth;
    }

    /**
     * 設計稿寬度（與 getPX 輸入值同單位）
     */
    float getDesignWidth()
    {
        return mWidth;
    }

    /**
     * 設計稿高度（與 getPX 輸入值同單位）：實際高度依縮放比例換回設計稿單位；尚未初始化時回傳 0
     */
    float getDesignHeight()
    {
        return mScreenWidth > 0 ? mScreenHeight * mWidth / mScreenWidth : 0;
    }

    public int getScreenWidth()
    {
        return mScreenWidth;
    }

    public int getScreenHeight()
    {
        return mScreenHeight;
    }

    public float getDensity()
    {
        return mDensity;
    }

    /**
     * 設計稿數值（px）依螢幕寬度等比換算。
     * 四捨五入（正負對稱）；非 0 的值換算後至少保留 ±1px，避免縮小時細線消失
     * （與 TypedValue.complexToDimensionPixelSize 相同規則）。尚未初始化時回傳 0。
     */
    public int getPX(float px)
    {
        if (mScreenWidth <= 0)
        {
            return 0;
        }
        final float value = px * getWidthRatio();
        final int result = (int) (value >= 0 ? value + 0.5f : value - 0.5f);
        if (result != 0 || px == 0)
        {
            return result;
        }
        return px > 0 ? 1 : -1;
    }

    public int dp2px(float dpValue)
    {
        return (int) (dpValue * mDensity + 0.5f);
    }

}
