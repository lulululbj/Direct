package io.github.dreierf.materialintroscreen.animations.wrappers;

import android.view.View;

import io.github.dreierf.materialintroscreen.animations.ViewTranslationWrapper;
import io.github.dreierf.materialintroscreen.animations.translations.AlphaTranslation;
import io.github.dreierf.materialintroscreen.animations.translations.DefaultAlphaTranslation;

public class ViewPagerTranslationWrapper extends ViewTranslationWrapper {
    public ViewPagerTranslationWrapper(View view) {
        super(view);

        setDefaultTranslation(new DefaultAlphaTranslation())
                .setExitTranslation(new AlphaTranslation());
    }
}