package org.stepik.android.exams.ui.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import org.stepik.android.exams.R;


public class ProgressLatexView extends FrameLayout {

    private LatexSupportableEnhancedFrameLayout optionText;
    private String beforeText = null;
    private ProgressBar progressBar;

    public ProgressLatexView(Context context) {
        this(context, null);
    }

    public ProgressLatexView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public ProgressLatexView(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.progressable_latex_supportable_frame_layout, this, true);
        optionText = findViewById(R.id.latex_text);
        progressBar = findViewById(R.id.loadProgressbar);

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        WebViewClient client = new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(VISIBLE);
                optionText.setVisibility(INVISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(INVISIBLE); // warning: use INVISIBLE instead of GONE for right LaTeX rendering
                optionText.setVisibility(VISIBLE);
            }
        };
        optionText.getWebView().setWebViewClient(client);
    }

    public void setPlainOrLaTeXText(String text) {
        if (beforeIsEqual(text)) {
            beforeText = text;
            optionText.setPlainOrLaTeXText(text);
        }
    }


    public void setAnyText(String text) {
        if (beforeIsEqual(text)) {
            beforeText = text;
            optionText.setText(text);
        }
    }

    private boolean beforeIsEqual(String newText) {
        return beforeText == null || newText == null || !newText.equals(beforeText);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ownState = (SavedState) state;
        super.onRestoreInstanceState(ownState.getSuperState());
        beforeText = ownState.beforeText;
        requestLayout();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ownState = new SavedState(superState);
        ownState.beforeText = beforeText;
        return ownState;
    }

    public static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        String beforeText;

        private SavedState(Parcel source) {
            super(source);
            beforeText = source.readString();
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(beforeText);
        }
    }

    public int getMeasuredHeightOfInnerLayout() {
        return optionText.getMeasuredHeight();
    }

}
