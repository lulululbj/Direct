package io.github.dreierf.materialintroscreen.animations.translations;

import androidx.annotation.FloatRange;
import android.view.View;

import io.github.dreierf.materialintroscreen.R;
import io.github.dreierf.materialintroscreen.animations.IViewTranslation;

public class EnterDefaultTranslation implements IViewTranslation {
    @Override
    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
        view.setTranslationY((1f - percentage) * view.getResources().getDimensionPixelOffset(R.dimen.y_offset));
    }
}
