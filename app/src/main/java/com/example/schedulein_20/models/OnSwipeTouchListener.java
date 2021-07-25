package com.example.schedulein_20.models;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class OnSwipeTouchListener implements View.OnTouchListener {
    private final String TAG = "OnSwipeTouchListener";
    private GestureDetector gestureDetector;
    protected View view;
    protected boolean actionExecuted = false;

    public OnSwipeTouchListener(Context c) {
        gestureDetector = new GestureDetector(c, new GestureListener());
    }

    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        this.view = view;
        if (motionEvent.getAction() == MotionEvent.ACTION_UP && !actionExecuted){
            resetView();
        }
        return gestureDetector.onTouchEvent(motionEvent);
    }

    private void resetView() {
        view.setTranslationX(0);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 210;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // Determines the fling velocity and then fires the appropriate swipe event accordingly
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            boolean result = false;
            try {
                float diffX = e2.getX() - e1.getX();
                view.setTranslationX(diffX);
                if (view.getTranslationX() > SWIPE_THRESHOLD){
                    actionExecuted = true;
                    onSwipeRight();
                    Log.i(TAG, "transform x: " + String.valueOf(view.getTranslationX()) );
                }
                result = true;
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeUp() {
    }

    public void onSwipeDown() {
    }
}
