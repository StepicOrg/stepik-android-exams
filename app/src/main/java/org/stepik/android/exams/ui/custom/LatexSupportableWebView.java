package org.stepik.android.exams.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import org.stepik.android.exams.App;
import org.stepik.android.exams.R;
import org.stepik.android.exams.configuration.Config;
import org.stepik.android.exams.util.DpPixelsHelper;
import org.stepik.android.exams.util.HtmlHelper;

import java.util.Calendar;

import javax.inject.Inject;

public class LatexSupportableWebView extends WebView implements View.OnClickListener, View.OnTouchListener, View.OnLongClickListener {
    private static final String mimeType = "text/html";
    private static final String encoding = "UTF-8";

    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;
    OnWebViewImageClicked listener;

    @ColorInt
    private int textColorHighlight;

    @Inject
    Config config;

    private final static String assetUrl = "file:///android_asset/";

    public LatexSupportableWebView(Context context) {
        this(context, null);
    }

    public LatexSupportableWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LatexSupportableWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int[] set = {
                android.R.attr.textColorHighlight
        };

        TypedArray a = context.obtainStyledAttributes(attrs, set);

        try {
            textColorHighlight = a.getColor(0, ContextCompat.getColor(getContext(), R.color.text_color_highlight));
        } finally {
            a.recycle();
        }
        init();
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void init() {
        App.Companion.component().inject(this);
        setBackgroundColor(Color.argb(1, 0, 0, 0));
        setOnLongClickListener(v -> true);

        setOnClickListener(this);
        setOnTouchListener(this);


        WebSettings webSettings = getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        addJavascriptInterface(new OnScrollWebListener(), HtmlHelper.HORIZONTAL_SCROLL_LISTENER);
    }

    public void setTextIsSelectable(boolean isSelectable) {
        if (isSelectable) {
            setOnLongClickListener(this);
        } else {
            setOnLongClickListener(null);
        }
    }


    public void setText(CharSequence text) {
        setText(text, false); //by default we do not want latex -> try to optimize
    }

    public void setText(CharSequence text, boolean wantLaTeX) {
        setText(text, wantLaTeX, null);
    }

    public void setText(CharSequence text, boolean wantLaTeX, String fontPath) {

        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((AppCompatActivity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        String textString = text.toString();

        final String html;
        if (fontPath != null) {
            html = HtmlHelper.buildPageWithCustomFont(text, fontPath, textColorHighlight, width, config.getHost());
        } else if (wantLaTeX || HtmlHelper.hasLaTeX(textString)) {
            html = HtmlHelper.buildMathPage(text, textColorHighlight, width, config.getHost());
        } else {
            html = HtmlHelper.buildPageWithAdjustingTextAndImage(text, textColorHighlight, width, config.getHost());
        }

        postDelayed(() -> loadDataWithBaseURL(assetUrl, html, mimeType, encoding, ""), 0);
    }


    @Override
    public void onClick(View v) {
        WebView.HitTestResult hr = getHitTestResult();
        try {
            if (listener != null && hr.getType() == HitTestResult.IMAGE_TYPE) {
                listener.onClick(hr.getExtra());
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                startClickTime = Calendar.getInstance().getTimeInMillis();
                break;
            }
            case MotionEvent.ACTION_UP: {
                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                if (clickDuration < MAX_CLICK_DURATION) {
                    performClick();
                }
            }
        }
        return false;
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }

    private float startX = 0;
    private float startY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();

                final float dpx = DpPixelsHelper.convertPixelsToDp(event.getX(), getContext());
                final float dpy = DpPixelsHelper.convertPixelsToDp(event.getY(), getContext());
                evalScript("measureScroll(" + dpx + ", " + dpy + ")");

                break;
            case MotionEvent.ACTION_MOVE:
                float dx = startX - event.getX();
                float dy = startY - event.getY();
                event.setLocation(event.getX(), startY);

                if (Math.abs(dx) > Math.abs(dy) && canScrollHorizontally((int) dx)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                scrollState.reset();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean canScrollHorizontally(int dx) {
        return super.canScrollHorizontally(dx) ||
                (dx < 0 && scrollState.canScrollLeft) ||
                (dx > 0 && scrollState.canScrollRight);
    }

    private void evalScript(String code) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript(code, null);
        } else {
            loadUrl("javascript: " + code);
        }
    }

    public void setOnWebViewClickListener(OnWebViewImageClicked listener) {
        this.listener = listener;
    }

    public interface OnWebViewImageClicked {
        void onClick(String path);
    }

    public final class OnScrollWebListener {
        @JavascriptInterface
        public void onScroll(float offsetWidth, float scrollWidth, float scrollLeft) {
            scrollState.canScrollLeft = scrollLeft > 0;
            scrollState.canScrollRight = offsetWidth + scrollLeft < scrollWidth;
        }
    }

    private static class ScrollState {
        private boolean canScrollLeft = false;
        private boolean canScrollRight = false;

        private void reset() {
            canScrollLeft = false;
            canScrollRight = false;
        }
    }

    private final ScrollState scrollState = new ScrollState();

}
