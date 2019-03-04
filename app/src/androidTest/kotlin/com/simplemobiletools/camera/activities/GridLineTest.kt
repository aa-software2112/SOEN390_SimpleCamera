package com.simplemobiletools.camera.activities

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.* // ktlint-disable no-wildcard-imports
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import com.simplemobiletools.camera.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.* // ktlint-disable no-wildcard-imports
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class GridLineTest : BaseUITestSetup(TestActivities.MAIN_ACTIVITY) {

    var view: View? = null
    val countdown = "5 sec"

    private fun childAtPosition(parentMatcher: Matcher<View>, position: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent) && view == parent.getChildAt(position)
            }
        }
    }

    @Before
    fun setup() {
        /** Capture the view  */
        view = this.mMainActivity?.activity?.findViewById<View>(R.id.settings)
    }

    @Test
    fun gridLinePhotoModeTest() {

        /** Verify that the settings view is present */
        onView(withId(R.id.settings)).check(matches(isDisplayed()))

        /** Navigate to Settings */
        this.performClicks(onView(withId(R.id.settings)))

        /** Verify if the grid line toggle view is present */
        onView(withId(R.id.settings_grid_line_holder)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_grid_line)).check(matches(isDisplayed()))

        /** Toggle ON the grid line feature */
        onView(withId(R.id.settings_grid_line)).perform(click())

        /** Verify if the grid line feature was toggled */
        onView(withId(R.id.settings_grid_line)).check(matches(isChecked()))
        Thread.sleep(1000)

        /** Return to home screen */
        val appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()))
        appCompatImageButton.perform(click())

        /** Verify if the grid line feature is present */
        onView(withId(R.id.gridline)).check(matches(isDisplayed()))
        Thread.sleep(1500)

        /** Toggle camera front/rear */
        onView(withId(R.id.toggle_camera)).perform(click())

        /** Verify if the grid line feature is present */
        onView(withId(R.id.gridline)).check(matches(isDisplayed()))
        Thread.sleep(1500)

        /** Navigate to Settings */
        this.performClicks(onView(withId(R.id.settings)))

        /** Toggle OFF the grid line feature */
        onView(withId(R.id.settings_grid_line)).perform(click())
    }

    @Test
    fun gridLineCountdownModeTest() {

        /** Verify that the settings view is present */
        onView(withId(R.id.settings)).check(matches(isDisplayed()))

        /** Navigate to Settings */
        this.performClicks(onView(withId(R.id.settings)))

        /** Verify if the grid line toggle view is present */
        onView(withId(R.id.settings_grid_line_holder)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_grid_line)).check(matches(isDisplayed()))

        /** Toggle ON the grid line feature */
        onView(withId(R.id.settings_grid_line)).perform(click())

        /** Verify if the grid line feature was toggled */
        onView(withId(R.id.settings_grid_line)).check(matches(isChecked()))
        Thread.sleep(1000)

        /** Return to home screen */
        val appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()))
        appCompatImageButton.perform(click())

        /** Select countdown */
        this.performClicks(onView(withId(R.id.countdown_toggle)))

        /** Select 5 second countdown */
        onView(withText(countdown)).perform(click())
        this.sleep(500)

        /** Press the shutter */
        onView(withId(R.id.shutter)).perform(click())
        Thread.sleep(((countdown.split(" ")[0]).toLong() + 2)*500)

        /** Verify if the grid line feature is present */
        onView(withId(R.id.gridline)).check(matches(isDisplayed()))
        Thread.sleep(4000)

        /** First click to unfade button Settings */
        this.performClicks(onView(withId(R.id.settings)))
        Thread.sleep(2000)

        /** Navigate to Settings */
        this.performClicks(onView(withId(R.id.settings)))

        /** Toggle OFF the grid line feature */
        onView(withId(R.id.settings_grid_line)).perform(click())
    }

    @Test
    fun gridLineVideoModeTest() {

        /** Verify that the settings view is present */
        onView(withId(R.id.settings)).check(matches(isDisplayed()))

        /** Navigate to Settings */
        this.performClicks(onView(withId(R.id.settings)))

        /** Verify if the grid line toggle view is present */
        onView(withId(R.id.settings_grid_line_holder)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_grid_line)).check(matches(isDisplayed()))

        /** Toggle ON the grid line feature */
        onView(withId(R.id.settings_grid_line)).perform(click())

        /** Verify if the grid line feature was toggled */
        onView(withId(R.id.settings_grid_line)).check(matches(isChecked()))
        Thread.sleep(1000)

        /** Return to home screen */
        val appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.action_bar),
                                        childAtPosition(
                                                withId(R.id.action_bar_container),
                                                0)),
                                1),
                        isDisplayed()))
        appCompatImageButton.perform(click())

        /** Toggle camera for video mode */
        Thread.sleep(2000)
        onView(withId(R.id.toggle_photo_video)).perform(click())
        Thread.sleep(2000)

        /** Verify if the grid line feature is present */
        onView(withId(R.id.gridline)).check(matches(isDisplayed()))
        Thread.sleep(1500)

        /** Toggle camera front/rear */
        onView(withId(R.id.toggle_camera)).perform(click())

        /** Verify if the grid line feature is present */
        onView(withId(R.id.gridline)).check(matches(isDisplayed()))
        Thread.sleep(1500)

        /** Toggle camera for video mode */
        this.performClicks(onView(withId(R.id.settings)))
        Thread.sleep(1000)
        onView(withId(R.id.toggle_photo_video)).perform(click())
        Thread.sleep(1000)

        /** Navigate to Settings */
        this.performClicks(onView(withId(R.id.settings)))
        Thread.sleep(1000)

        /** Toggle OFF the grid line feature */
        onView(withId(R.id.settings_grid_line)).perform(click())
    }
}
