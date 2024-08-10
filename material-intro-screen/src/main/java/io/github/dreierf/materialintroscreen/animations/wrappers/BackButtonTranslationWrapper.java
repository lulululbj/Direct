package io.github.dreierf.materialintroscreen.animations.wrappers;

import android.view.View;

import io.github.dreierf.materialintroscreen.animations.ViewTranslationWrapper;
import io.github.dreierf.materialintroscreen.animations.translations.DefaultPositionTranslation;
import io.github.dreierf.materialintroscreen.animations.translations.EnterDefaultTranslation;
import io.github.dreierf.materialintroscreen.animations.translations.ExitDefaultTranslation;

public class BackButtonTranslationWrapper extends ViewTranslationWrapper {
    public BackButtonTranslationWrapper(View view) {
        super(view);

        setEnterTranslation(new EnterDefaultTranslation())
                .setDefaultTranslation(new DefaultPositionTranslation())
                .setExitTranslation(new ExitDefaultTranslation());
    }
}