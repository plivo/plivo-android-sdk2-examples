package com.plivo.voicecalling.Helpers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;


public class AnimationsUtil {

    // To reveal a previously invisible view using this effect:
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void show(final View view, long duration) {
        // get the center for the clipping circle
        int cx = (view.getLeft() + view.getRight()) / 17;
        int cy = (view.getTop() + view.getBottom()) / 17;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(view.getWidth(), view.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy,
                0, finalRadius);
        anim.setDuration(duration);

        // make the view visible and start the animation
        view.setVisibility(View.VISIBLE);
        anim.start();
    }

    // To hide a previously visible view using this effect:
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void hide(Activity mActivity, final View view, long duration) {

        // get the center for the clipping circle
        int cx = (view.getLeft() + view.getRight()) / 17;
        int cy = (view.getTop() + view.getBottom()) / 17;

        // get the initial radius for the clipping circle
        int initialRadius = view.getWidth();

        // create the animation (the final radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy,
                initialRadius, 0);
        anim.setDuration(duration);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.INVISIBLE);
                mActivity.finish();
            }
        });

        // start the animation
        anim.start();
    }


    public static void AnimationLoader(Context context, View view, long duration, int id) {
        final Animation animTranslatePassword = android.view.animation.AnimationUtils.loadAnimation(context, id);
        animTranslatePassword.setDuration(duration);
        view.startAnimation(animTranslatePassword);
    }

    public static void expandToolbar(CoordinatorLayout rootLayout, Bitmap bmp, AppBarLayout appBar) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBar.getLayoutParams();
        AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
        behavior.setTopAndBottomOffset(0);
        int height = AppHelper.pxToDp(350);
        behavior.onNestedPreScroll(rootLayout, appBar, null, 0, bmp.getHeight() - height, new int[2]);
        params.setBehavior(behavior);
        appBar.setLayoutParams(params);
    }

    public static void startAlphaAnimation(View v, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE) ? new AlphaAnimation(0f, 1f) : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(1000);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    public static void animateY(RecyclerView.ViewHolder holder, boolean goesDown) {

        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(holder.itemView, "translationY", goesDown == true ? 100 : -100, 0);
        animatorTranslateY.setDuration(300);
        animatorTranslateY.start();
    }

    public static void rotationY(final View viewToFlip) {
        ObjectAnimator rotationY = ObjectAnimator.ofFloat(viewToFlip, "rotationY", 0f, 360f);
        rotationY.setDuration(2000);
        rotationY.start();

    }

    public static void slideStatus(View view) {
        ObjectAnimator objectanimator = ObjectAnimator.ofFloat(view, "x", -200);
        objectanimator.setDuration(1500);
        objectanimator.start();
        new Handler().postDelayed(() -> {
            ObjectAnimator objectanimator2 = ObjectAnimator.ofFloat(view, "x", 0);
            objectanimator2.setDuration(1500);
            objectanimator2.start();
        }, 1500);
    }


//    public static void ShakeAnimation(Context mContext, View view) {
//        Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
//        view.startAnimation(shake);
//    }
}
