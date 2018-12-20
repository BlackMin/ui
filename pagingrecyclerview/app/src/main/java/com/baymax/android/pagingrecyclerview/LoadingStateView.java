package com.baymax.android.pagingrecyclerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableInt;

public class LoadingStateView extends FrameLayout {

    private int loadingState = LOADING_STATE.STATE_LOADING;

    private ObservableInt observableLoadingState = new ObservableInt(LOADING_STATE.STATE_SUCCESS);

    private Observable.OnPropertyChangedCallback loadingStateChangedListener = null;

    private @LayoutRes int loadingLayoutRes = R.layout.view_loading;
    private @LayoutRes int emptyLayoutRes = R.layout.view_empty;
    private @LayoutRes int errorLayoutRes = R.layout.view_error;

    private View loadingView;

    private View emptyView;

    private View errorView;

    public LoadingStateView(Context context) {
        super(context);
        init(context);
    }

    public LoadingStateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.LoadingStateView);
        loadingState = ta.getInt(R.styleable.LoadingStateView_state, loadingState);
        loadingLayoutRes = ta.getResourceId(R.styleable.LoadingStateView_loading_layout, loadingLayoutRes);
        emptyLayoutRes = ta.getResourceId(R.styleable.LoadingStateView_empty_layout, emptyLayoutRes);
        errorLayoutRes = ta.getResourceId(R.styleable.LoadingStateView_error_layout, errorLayoutRes);
        ta.recycle();
        init(context);
    }

    public LoadingStateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.LoadingStateView);
        loadingState = ta.getInt(R.styleable.LoadingStateView_state, loadingState);
        loadingLayoutRes = ta.getResourceId(R.styleable.LoadingStateView_loading_layout, loadingLayoutRes);
        emptyLayoutRes = ta.getResourceId(R.styleable.LoadingStateView_empty_layout, emptyLayoutRes);
        errorLayoutRes = ta.getResourceId(R.styleable.LoadingStateView_error_layout, errorLayoutRes);
        ta.recycle();
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoadingStateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.LoadingStateView);
        loadingState = ta.getInt(R.styleable.LoadingStateView_state, loadingState);
        loadingLayoutRes = ta.getResourceId(R.styleable.LoadingStateView_loading_layout, loadingLayoutRes);
        emptyLayoutRes = ta.getResourceId(R.styleable.LoadingStateView_empty_layout, emptyLayoutRes);
        errorLayoutRes = ta.getResourceId(R.styleable.LoadingStateView_error_layout, errorLayoutRes);
        ta.recycle();
        init(context);
    }

    private void init(Context context) {
        loadingView = LayoutInflater.from(context).inflate(loadingLayoutRes,this,false);
        emptyView = LayoutInflater.from(context).inflate(emptyLayoutRes,this,false);
        errorView = LayoutInflater.from(context).inflate(emptyLayoutRes,this,false);
    }

    public void setRetryClickListener(OnClickListener retryClickListener) {
        if(errorView != null && retryClickListener != null) {
            errorView.setOnClickListener(retryClickListener);
        }
    }

    public void setLoadingState(int state) {
        loadingState = state;
        observableLoadingState.set(loadingState);
    }

    private void refreshView(int state) {
        removeAllViews();
        switch (state) {
            case LOADING_STATE.STATE_LOADING:
                addView(loadingView);
                setVisibility(VISIBLE);
                break;
            case LOADING_STATE.STATE_EMPTY:
                addView(emptyView);
                setVisibility(VISIBLE);
                break;
            case LOADING_STATE.STATE_ERROR:
                addView(errorView);
                setVisibility(VISIBLE);
                break;
            case LOADING_STATE.STATE_SUCCESS:
                setVisibility(GONE);
                break;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        observableLoadingState.addOnPropertyChangedCallback(loadingStateChangedListener = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                refreshView(observableLoadingState.get());
            }
        });
        observableLoadingState.set(loadingState);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        observableLoadingState.removeOnPropertyChangedCallback(loadingStateChangedListener);
    }


    public static class LOADING_STATE {

        public static final int STATE_SUCCESS = 4;

        public static final int STATE_ERROR = 3;

        public static final int STATE_EMPTY = 2;

        public static final int STATE_LOADING = 1;
    }
}
