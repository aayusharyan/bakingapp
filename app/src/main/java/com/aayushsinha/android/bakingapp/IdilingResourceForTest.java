package com.aayushsinha.android.bakingapp;

import android.support.annotation.Nullable;
import android.support.test.espresso.IdlingResource;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class IdilingResourceForTest implements IdlingResource {

    @Nullable
    private volatile ResourceCallback callback;

    // Idleness is controlled with this boolean.
    private AtomicBoolean isIdleNow = new AtomicBoolean(true);

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }

    public void setIdleState(boolean isIdleNow) {
        this.isIdleNow.set(isIdleNow);
        if (isIdleNow && callback != null) {
            Objects.requireNonNull(callback).onTransitionToIdle();
        }
    }
}
