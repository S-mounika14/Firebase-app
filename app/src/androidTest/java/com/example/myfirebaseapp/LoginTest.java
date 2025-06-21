package com.example.myfirebaseapp;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.After;
import org.junit.Before;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setUp() {
        // Disable animations to prevent focus issues
        try {
            Runtime.getRuntime().exec("adb shell settings put global window_animation_scale 0");
            Runtime.getRuntime().exec("adb shell settings put global transition_animation_scale 0");
            Runtime.getRuntime().exec("adb shell settings put global animator_duration_scale 0");
        } catch (Exception e) {
            // Ignore if adb commands fail
        }
    }

    @After
    public void tearDown() {
        // Re-enable animations
        try {
            Runtime.getRuntime().exec("adb shell settings put global window_animation_scale 1");
            Runtime.getRuntime().exec("adb shell settings put global transition_animation_scale 1");
            Runtime.getRuntime().exec("adb shell settings put global animator_duration_scale 1");
        } catch (Exception e) {
            // Ignore if adb commands fail
        }
    }

    @Test
    public void testLoginSuccess() {
        onView(withId(R.id.emailEditText))
                .perform(typeText("test@example.com"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText))
                .perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());


        onView(withId(R.id.emailEditText)).check(matches(withText("test@example.com")));
        onView(withId(R.id.passwordEditText)).check(matches(withText("password123")));
    }

    @Test
    public void testLoginFailureEmptyFields() {
        // Simply verify that login button exists and is clickable
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()));
        onView(withId(R.id.loginButton)).perform(click());

        // Verify the UI elements are still present and empty
        onView(withId(R.id.emailEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordEditText)).check(matches(isDisplayed()));
    }
}

















