package io.github.dreierf.materialintroscreen.animations.wrappers;

import android.view.View;

import io.github.dreierf.materialintroscreen.R;
import io.github.dreierf.materialintroscreen.animations.ViewTranslationWrapper;
import io.github.dreierf.materialintroscreen.animations.translations.DefaultPositionTranslation;
import io.github.dreierf.materialintroscreen.animations.translations.ExitDefaultTranslation;

public class NextButtonTranslationWrapper extends ViewTranslationWrapper {
    public NextButtonTranslationWrapper(View view) {
        super(view);

        setExitTranslation(new ExitDefaultTranslation())
                .setDefaultTranslation(new DefaultPositionTranslation())
                .setErrorAnimation(R.anim.shake_it);
    }
}