package widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deft on 2016/7/6.
 */

public final class ImeObserver {

    private ImeObserver() {
    }

    public static void observer(final Activity activity) {
        if (null == activity) {
            return;
        }
        final View root = activity.getWindow().getDecorView();
        if (root instanceof ViewGroup) {
            final ViewGroup decorView = (ViewGroup) root;
            if (decorView.getChildCount() > 0) {
                final View child = decorView.getChildAt(0);
                decorView.removeAllViews();
                ViewGroup.LayoutParams params = child.getLayoutParams();
                ImeObserverLayout observerLayout = new ImeObserverLayout(activity.getApplicationContext());
                observerLayout.addView(child, params);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                decorView.addView(observerLayout, lp);
            }
        }
    }

    private static class ImeObserverLayout extends FrameLayout {

        private List<EditText> mEditTexts;

        public ImeObserverLayout(Context context) {
            super(context);
        }

        public ImeObserverLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ImeObserverLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }


        public ImeObserverLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            collectEditText(this);
        }

        @Override
        protected void onDetachedFromWindow() {
            clearEditText();
            super.onDetachedFromWindow();
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            if (MotionEvent.ACTION_DOWN == ev.getAction() && shouldHideSoftInput(ev)) {
                hideSoftInput();
            }
            return super.onInterceptTouchEvent(ev);
        }

        private void collectEditText(View child) {
            if (null == mEditTexts) {
                mEditTexts = new ArrayList<EditText>();
            }
            if (child instanceof ViewGroup) {
                final ViewGroup parent = (ViewGroup) child;
                final int childCount = parent.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childView = parent.getChildAt(i);
                    collectEditText(childView);
                }
            } else if (child instanceof EditText) {
                final EditText editText = (EditText) child;
                if (!mEditTexts.contains(editText)) {
                    mEditTexts.add(editText);
                }
            }
        }

        private void clearEditText() {
            if (null != mEditTexts) {
                mEditTexts.clear();
                mEditTexts = null;
            }
        }

        private void hideSoftInput() {
            final Context context = getContext().getApplicationContext();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }

        private boolean shouldHideSoftInput(MotionEvent ev) {
            if (null == mEditTexts || mEditTexts.isEmpty()) {
                return false;
            }
            final int x = (int) ev.getX();
            final int y = (int) ev.getY();
            Rect r = new Rect();
            for (EditText editText : mEditTexts) {
                editText.getGlobalVisibleRect(r);
                if (r.contains(x, y)) {
                    return false;
                }
            }
            return true;
        }
    }
}

