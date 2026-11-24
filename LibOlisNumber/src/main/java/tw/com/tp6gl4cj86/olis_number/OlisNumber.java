package tw.com.tp6gl4cj86.olis_number;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.TextView;

/**
 * Created by tp6gl4cj86 on 2016/12/16.
 * <p>
 * 全域的尺寸縮放工具。寬高、縮放比例等邏輯都在 {@link OlisNumberObject}，
 * 這裡的靜態方法皆委派給內部的預設實例，與另外建立的 OlisNumberObject 共用同一份實作。
 */
public class OlisNumber
{

    /**
     * 預設實例：OlisNumber 靜態方法使用的縮放基準
     */
    private static final OlisNumberObject sDefault = new OlisNumberObject();

    /**
     * 僅保存 Application Context，避免靜態持有 Activity 導致重建時記憶體洩漏
     */
    private static Context context;

    // ===== 初始化 =====

    /**
     * 直向：以 DisplayMetrics 計算，寬取短邊、高取長邊
     */
    public static void init(Context context, float width)
    {
        setContext(context);
        sDefault.init(context, width);
    }

    /**
     * 直向：以 DisplayMetrics 計算，寬取短邊、高取長邊；沿用目前的設計寬度
     */
    public static void init(Context context)
    {
        setContext(context);
        sDefault.init(context);
    }

    /**
     * 橫向：以 DisplayMetrics 計算，寬取長邊、高取短邊
     */
    public static void initLandscape(Context context, float width)
    {
        setContext(context);
        sDefault.initLandscape(context, width);
    }

    /**
     * 橫向：以實體螢幕大小（含系統列）計算，寬取長邊、高取短邊。
     * 建議傳入 Activity；傳入 Application 等非 UI context 時改由 DisplayManager 取得預設螢幕大小。
     */
    public static void initRealLandscape(Context context, float width)
    {
        setContext(context);
        sDefault.initRealLandscape(context, width);
    }

    /**
     * 以指定寬高初始化，例如 App 實際可用區域（edge-to-edge 時扣掉狀態列 / 瀏海後的大小）。
     * getScreenWidth / getScreenHeight 直接回傳傳入值，getPX 的縮放比例 = width / designWidth。
     *
     * @param width       可用寬度（px）
     * @param height      可用高度（px）
     * @param designWidth width 所對應的設計寬度
     */
    public static void initWithSize(Context context, int width, int height, float designWidth)
    {
        setContext(context);
        sDefault.initWithSize(context, width, height, designWidth);
    }

    /**
     * 只保存 Application Context；context 為 null 時保留原值（各 init 也會忽略該次初始化）
     */
    private static void setContext(Context context)
    {
        if (context == null)
        {
            return;
        }
        final Context applicationContext = context.getApplicationContext();
        OlisNumber.context = applicationContext != null ? applicationContext : context;
    }

    // ===== 取值與換算 =====

    public static int getScreenWidth()
    {
        return sDefault.getScreenWidth();
    }

    public static int getScreenHeight()
    {
        return sDefault.getScreenHeight();
    }

    public static float getDensity()
    {
        return sDefault.getDensity();
    }

    /**
     * 設計稿數值（px）依螢幕寬度等比換算
     */
    public static int getPX(float px)
    {
        return sDefault.getPX(px);
    }

    public static int dp2px(float dpValue)
    {
        return sDefault.dp2px(dpValue);
    }

    // ===== View 縮放 =====

    /**
     * 依預設實例縮放 view 及其所有子 view。tag 為 "null" 的 view 不縮放寬高。
     */
    public static void initViewGroupFromXML(View view)
    {
        initViewGroupFromXML(view, null, 1);
    }

    /**
     * 依預設實例縮放 view 及其所有子 view，並額外乘上 scale。tag 為 "null" 的 view 不縮放寬高。
     */
    public static void initViewGroupFromXML(View view, float scale)
    {
        initViewGroupFromXML(view, null, scale);
    }

    /**
     * 依指定的 OlisNumberObject（null 時使用預設實例）縮放 view 及其所有子 view，並額外乘上 scale。
     * tag 為 "null" 的 view 不縮放寬高。
     */
    public static void initViewGroupFromXML(View view, OlisNumberObject olisNumberObject, float scale)
    {
        if (view != null)
        {
            initViewFromXML(view, olisNumberObject != null ? olisNumberObject : sDefault, scale);
            if (view instanceof ViewGroup viewGroup)
            {
                for (int i = 0; i < viewGroup.getChildCount(); i++)
                {
                    initViewGroupFromXML(viewGroup.getChildAt(i), olisNumberObject, scale);
                }
            }
        }
    }

    /**
     * 縮放單一 view；每個 view 只會處理一次（以 R.id.OlisNumberInited tag 標記）。
     * <p>
     * 縮放是一次性的：之後再呼叫 init 改變比例（例如多視窗 / 折疊機改變視窗大小），已縮放過的 view 不會重新套用，
     * 需重建 Activity（或重新 inflate 版面）才會以新比例縮放。
     */
    private static void initViewFromXML(View view, OlisNumberObject number, float scale)
    {
        if (view != null && view.getTag(R.id.OlisNumberInited) == null)
        {
            view.setTag(R.id.OlisNumberInited, Boolean.TRUE);

            // scale 先乘進去再交給 getPX，只做一次四捨五入
            view.setTranslationX(number.getPX(view.getTranslationX() * scale));
            view.setTranslationY(number.getPX(view.getTranslationY() * scale));

            if (view instanceof TextView textView)
            {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, number.getPX(textView.getTextSize() * scale));
                textView.setCompoundDrawablePadding(number.getPX(textView.getCompoundDrawablePadding() * scale));
            }

            final ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams != null)
            {
                final Object tag = view.getTag();
                if (!"null".equals(tag))
                {
                    if (layoutParams.width > 0)
                    {
                        layoutParams.width = number.getPX(layoutParams.width * scale);
                    }
                    // tag 為 "StatusBar" 時高度改為狀態列高度，寬度仍照常縮放
                    if ("StatusBar".equals(tag))
                    {
                        layoutParams.height = getStatusBarHeight(view);
                    }
                    else if (layoutParams.height > 0)
                    {
                        layoutParams.height = number.getPX(layoutParams.height * scale);
                    }
                }

                if (layoutParams instanceof ViewGroup.MarginLayoutParams marginLayoutParams)
                {
                    scaleMargins(marginLayoutParams, number, scale);
                }
            }
            scalePadding(view, number, scale);
        }
    }

    /**
     * 縮放 margin（含 layout_marginStart / layout_marginEnd）。
     * <p>
     * 有設定 start / end 時，系統排版會以 start / end 決定左右（supportsRtl 時未設定的一邊視為 0），
     * 因此需在 setMargins() 之前先讀出 start / end 並一起縮放。
     * 回傳 0 的一邊（未設定或本來就是 0）不寫回：0 縮放後仍是 0，且可避免把沒設定的一邊變成「明確設定」。
     */
    private static void scaleMargins(ViewGroup.MarginLayoutParams params, OlisNumberObject number, float scale)
    {
        final boolean isMarginRelative = params.isMarginRelative();
        // 注意：getMarginStart / getMarginEnd 可能觸發 margin 解析而更新 leftMargin / rightMargin，需在讀取左右之前呼叫
        final int marginStart = isMarginRelative ? params.getMarginStart() : 0;
        final int marginEnd = isMarginRelative ? params.getMarginEnd() : 0;

        params.setMargins(number.getPX(getHorizontalMargin(number, params.leftMargin) * scale),
                          number.getPX(getVerticalMargin(number, params.topMargin) * scale),
                          number.getPX(getHorizontalMargin(number, params.rightMargin) * scale),
                          number.getPX(getVerticalMargin(number, params.bottomMargin) * scale));

        if (marginStart != 0)
        {
            params.setMarginStart(number.getPX(getHorizontalMargin(number, marginStart) * scale));
        }
        if (marginEnd != 0)
        {
            params.setMarginEnd(number.getPX(getHorizontalMargin(number, marginEnd) * scale));
        }
    }

    /**
     * 縮放 padding。XML 用 paddingStart / paddingEnd 時改走 setPaddingRelative，
     * 避免 setPadding(left, …, right, …) 讓 start / end 失效（RTL 時左右對調的語意也會遺失）。
     */
    private static void scalePadding(View view, OlisNumberObject number, float scale)
    {
        final int top = number.getPX(view.getPaddingTop() * scale);
        final int bottom = number.getPX(view.getPaddingBottom() * scale);
        if (view.isPaddingRelative())
        {
            view.setPaddingRelative(number.getPX(view.getPaddingStart() * scale), top, number.getPX(view.getPaddingEnd() * scale), bottom);
        }
        else
        {
            view.setPadding(number.getPX(view.getPaddingLeft() * scale), top, number.getPX(view.getPaddingRight() * scale), bottom);
        }
    }

    /**
     * 左右 margin 超出 ±設計稿寬度視為異常值（例如未解析的 DEFAULT_MARGIN_RELATIVE = Integer.MIN_VALUE），以 0 處理。
     * margin 是尚未縮放的設計稿數值，因此與設計稿寬度比較，不與實際像素比較（小視窗時實際像素會比設計稿小）。
     */
    private static int getHorizontalMargin(OlisNumberObject number, int margin)
    {
        return isAbnormalMargin(margin, number.getDesignWidth()) ? 0 : margin;
    }

    /**
     * 上下 margin 超出 ±設計稿高度視為異常值，以 0 處理（與 {@link #getHorizontalMargin} 相同，以設計稿單位比較）
     */
    private static int getVerticalMargin(OlisNumberObject number, int margin)
    {
        return isAbnormalMargin(margin, number.getDesignHeight()) ? 0 : margin;
    }

    private static boolean isAbnormalMargin(int margin, float designLength)
    {
        if (margin == Integer.MIN_VALUE || margin == Integer.MAX_VALUE)
        {
            return true;
        }
        // 尚未初始化（設計稿長度未知）時只排除極端值
        return designLength > 0 && Math.abs((float) margin) > designLength;
    }

    // ===== 狀態列 =====

    /**
     * status_bar_height 的資源 id（0 = 尚未查詢，-1 = 查不到）。只快取 id，數值每次重讀，
     * 讓橫直向、折疊機展開、顯示大小改變後能取得對應設定下的值。
     */
    private static int statusBarHeightResId = 0;

    /**
     * 系統資源定義的狀態列高度（每次依目前設定重讀，不快取數值）。取不到資源時以 24dp 估算；尚未初始化時回傳 0。
     *
     * @deprecated 此值不含瀏海 / 挖孔；edge-to-edge 時請改用 {@link #getStatusBarHeight(View)} 或 WindowInsets。
     */
    @Deprecated
    public static int getStatusBarHeight()
    {
        if (context == null)
        {
            return 0;
        }
        final Resources res = context.getResources();
        if (statusBarHeightResId == 0)
        {
            @SuppressLint("DiscouragedApi")
            final int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
            statusBarHeightResId = resourceId > 0 ? resourceId : -1;
        }
        if (statusBarHeightResId > 0)
        {
            return Math.max(res.getDimensionPixelSize(statusBarHeightResId), 0);
        }
        return Math.round(24 * res.getDisplayMetrics().density);
    }

    /**
     * 實際的狀態列（含瀏海 / 挖孔）上方內縮高度，取自 view 所在 window 的 WindowInsets。
     * 與無參數版本一致，狀態列暫時隱藏時仍回傳其高度。
     * view 尚未 attach 到 window（拿不到 insets）或 API 23 以下時，退回 {@link #getStatusBarHeight()}。
     */
    @SuppressWarnings("deprecation") // getStableInsetTop 僅在 API 30 以下使用
    public static int getStatusBarHeight(View view)
    {
        if (view != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            final WindowInsets insets = view.getRootWindowInsets();
            if (insets != null)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                {
                    // 與 API 30 以下的 stable inset 一致：不論狀態列目前是否顯示都計入
                    return insets.getInsetsIgnoringVisibility(WindowInsets.Type.statusBars() | WindowInsets.Type.displayCutout()).top;
                }

                // stable inset 在狀態列暫時隱藏時仍包含其高度
                int top = insets.getStableInsetTop();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && insets.getDisplayCutout() != null)
                {
                    top = Math.max(top, insets.getDisplayCutout()
                                              .getSafeInsetTop());
                }
                return top;
            }
        }
        return getStatusBarHeight();
    }

}
