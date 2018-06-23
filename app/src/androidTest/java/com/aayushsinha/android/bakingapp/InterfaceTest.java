package com.aayushsinha.android.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class InterfaceTest {
    @Rule public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private IdlingResource idlingResource;

    @Before
    public void RegisterIdilingResource() {
        idlingResource = mainActivityActivityTestRule.getActivity().getIdilingResource();
        Espresso.registerIdlingResources(idlingResource);
    }

    @Test
    public void clickRecipe_opensRecipeActivity() {
        onView(withId(R.id.recipesRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
    }

    @After
    public void UnregisterIdilingResource() {
        if(idlingResource != null) {
            Espresso.unregisterIdlingResources(idlingResource);
        }
    }
}
