package com.simplemobiletools.camera.activities

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import com.simplemobiletools.camera.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CountdownTimerTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule = GrantPermissionRule.grant("android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE")


    /** Starts a 5 second countdown and takes a picture */
    @Test
    fun countdownTimer() {

        val appCompatImageView = onView(
            allOf(withId(R.id.countdown_toggle), withContentDescription("TODO"),
                childAtPosition(allOf(withId(R.id.view_holder),childAtPosition(withId(android.R.id.content),0)),5),isDisplayed()))
        appCompatImageView.perform(click())

        val appCompatButton = onView(
            allOf(withId(R.id.btn_short_timer), withText("5 sec"), childAtPosition(allOf(withId(R.id.countdown_times),
                childAtPosition(withId(R.id.view_holder),7)),0), isDisplayed()))
        appCompatButton.perform(click())

        val appCompatImageView2 = onView(
            allOf(withId(R.id.shutter), withContentDescription("TODO"),
                childAtPosition(allOf(withId(R.id.btn_holder), childAtPosition(withId(R.id.view_holder),8)),1), isDisplayed()))
        appCompatImageView2.perform(click())

        Thread.sleep(5000)
    }

    /** Starts a 5 second countdown and then cancels it during the countdown */
    @Test
    fun countdownTimerCancel() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html

        val appCompatImageView = onView(
            allOf(withId(R.id.countdown_toggle), withContentDescription("TODO"),
                childAtPosition(allOf(withId(R.id.view_holder),childAtPosition(withId(android.R.id.content),0)),5),isDisplayed()))
        appCompatImageView.perform(click())

        val appCompatButton = onView(
            allOf(withId(R.id.btn_short_timer), withText("5 sec"), childAtPosition(allOf(withId(R.id.countdown_times),
                childAtPosition(withId(R.id.view_holder),7)),0), isDisplayed()))
        appCompatButton.perform(click())

        val appCompatImageView2 = onView(
            allOf(withId(R.id.shutter), withContentDescription("TODO"),
                childAtPosition(allOf(withId(R.id.btn_holder), childAtPosition(withId(R.id.view_holder),8)),1), isDisplayed()))
        appCompatImageView2.perform(click())

        // Wait a few seconds to confirm the countdown has started
        Thread.sleep(2000)

        // Cancel countdown
        appCompatImageView.perform(click())

        Thread.sleep(2000)
    }

    private fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                    && view == parent.getChildAt(position)
            }
        }
    }
}
