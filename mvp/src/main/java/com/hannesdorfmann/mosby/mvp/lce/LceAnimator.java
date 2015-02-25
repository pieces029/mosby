package com.hannesdorfmann.mosby.mvp.lce;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

/**
 * Little helper class for animating content, error and loading view
 *
 * @author Hannes Dorfmann
 * @since 1.0.0
 */
public class LceAnimator {

  /**
   * Show the loading view. No animations, because sometimes loading things is pretty fast (i.e.
   * retrieve data from memory cache).
   */
  public static void showLoading(View loadingView, View contentView, View errorView) {

    contentView.setVisibility(View.GONE);
    errorView.setVisibility(View.GONE);
    loadingView.setVisibility(View.VISIBLE);
  }

  /**
   * Shows the error view instead of the loading view
   */
  public static void showErrorView(final View loadingView, final View contentView,
      final TextView errorView) {

    if (Build.VERSION.SDK_INT < 11) {
      // Before honeycomb
      contentView.setVisibility(View.GONE);
      loadingView.setVisibility(View.GONE);
      errorView.setVisibility(View.VISIBLE);
    } else {

      contentView.setVisibility(View.GONE);

      // Not visible yet, so animate the view in
      AnimatorSet set = new AnimatorSet();
      ObjectAnimator in = ObjectAnimator.ofFloat(errorView, "alpha", 1f);
      ObjectAnimator loadingOut = ObjectAnimator.ofFloat(loadingView, "alpha", 0f);

      set.playTogether(in, loadingOut);
      set.setDuration(200);

      set.addListener(new AnimatorListenerAdapter() {

        @Override
        public void onAnimationStart(Animator animation) {
          super.onAnimationStart(animation);
          errorView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
          super.onAnimationEnd(animation);
          loadingView.setVisibility(View.GONE);
          loadingView.setAlpha(1f); // For future showLoading calls
        }
      });

      set.start();
    }
  }

  /**
   * Display the content instead of the loadingView
   */
  @TargetApi(11)
  public static void showContent(final View loadingView, final View contentView,
      final View errorView) {

    if (contentView.getVisibility() == View.VISIBLE) {
      // No Changing needed, because contentView is already visible
      errorView.setVisibility(View.GONE);
      loadingView.setVisibility(View.GONE);
    } else {

      if (Build.VERSION.SDK_INT < 14) {
        // Before honeycomb

        loadingView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
      } else {

        // ICS and above

        errorView.setVisibility(View.GONE);

        int translateDp = 40;
        // Not visible yet, so animate the view in
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator contentFadeIn = ObjectAnimator.ofFloat(contentView, "alpha", 0f, 1f);
        ObjectAnimator contentTranslateIn = ObjectAnimator.ofFloat(contentView, "translationY",
            dpToPx(loadingView.getContext(), translateDp), 0);

        ObjectAnimator loadingFadeOut = ObjectAnimator.ofFloat(loadingView, "alpha", 1f, 0f);
        ObjectAnimator loadingTranslateOut = ObjectAnimator.ofFloat(loadingView, "translationY", 0,
            -dpToPx(loadingView.getContext(), translateDp));

        set.playTogether(contentFadeIn, contentTranslateIn, loadingFadeOut, loadingTranslateOut);
        set.setDuration(500);

        set.addListener(new AnimatorListenerAdapter() {

          @Override
          public void onAnimationStart(Animator animation) {
            contentView.setTranslationY(0);
            loadingView.setTranslationY(0);
            contentView.setVisibility(View.VISIBLE);
          }

          @Override
          public void onAnimationEnd(Animator animation) {
            loadingView.setVisibility(View.GONE);
            loadingView.setAlpha(1f); // For future showLoading calls
            contentView.setTranslationY(0);
            loadingView.setTranslationY(0);
          }
        });

        set.start();
      }
    }
  }

  /**
   * Converts a dp value to a px value
   *
   * @param dp the dp value
   */
  public static int dpToPx(Context context, float dp) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return (int) ((dp * displayMetrics.density) + 0.5);
  }
}