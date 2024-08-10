package io.github.dreierf.materialintroscreen.animations.wrappers;

import android.view.View;

import io.github.dreierf.materialintroscreen.animations.ViewTranslationWrapper;
import io.github.dreierf.materialintroscreen.animations.translations.DefaultPositionTranslation;
import io.github.dreierf.materialintroscreen.animations.translations.ExitDefaultTranslation;

public class PageIndicatorTranslationWrapper extends ViewTranslationWrapper {
    public PageIndicatorTranslationWrapper(View view) {
        super(view);

        setDefaultTranslation(new DefaultPositionTranslation())
                .setExitTranslation(new ExitDefaultTranslation());
    }
}