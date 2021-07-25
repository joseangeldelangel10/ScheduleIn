package com.example.schedulein_20.models;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class OnPinchListener implements View.OnTouchListener {
    protected View view;
    protected ScaleGestureDetector gestureScale;
    protected boolean inScale = false;

    public OnPinchListener (Context c){
        gestureScale = new ScaleGestureDetector(c, new GestureListener());
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        this.view = view;
        gestureScale.onTouchEvent(event);
        return true;
    }

    public class GestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            onPinchZoom();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            inScale = true;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            inScale = false;
        }
    }

    public void onPinchZoom(){

    }

}
