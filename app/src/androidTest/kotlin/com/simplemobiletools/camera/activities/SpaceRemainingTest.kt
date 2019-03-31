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
class SpaceRemainingTest : BaseUITestSetup(TestActivities.MAIN_ACTIVITY) {

    var view: View? = null

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
    fun spaceRemainingVideoModeTest() {

        /** Verify that the settings view is present */
        onView(withId(R.id.settings)).check(matches(isDisplayed()))

        /** Navigate to Settings */
        this.performClicks(onView(withId(R.id.settings)))

        Thread.sleep(1000);

        /** Verify if the space remaining toggle view is present */
        onView(withId(R.id.settings_space_remaining_holder)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_space_remaining)).check(matches(isDisplayed()))

        /** Toggle ON the space remaining feature */
        onView(withId(R.id.settings_space_remaining)).perform(click())

        /** Verify if the space remaining feature was toggled */
        onView(withId(R.id.settings_space_remaining)).check(matches(isChecked()))
        Thread.sleep(3000)

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

        /** Verifying that the app is IN photo mode */
        if (mMainActivity?.activity?.mIsInPhotoMode == true) {

            /** Switch to video mode */
            onView(withId(R.id.toggle_photo_video)).perform(click())
            Thread.sleep(500)
        }

        /** Open the shutter */
        onView(withId(R.id.shutter)).perform(click())

        /** Verify if the space remaining feature is present */
        onView(withId(R.id.space_remaining)).check(matches(isDisplayed()))
        Thread.sleep(1500)

        /** Close the shutter */
        onView(withId(R.id.shutter)).perform(click())

        /** Navigate to Settings */
        this.performClicks(onView(withId(R.id.settings)))

        Thread.sleep(2000);

        /** Toggle OFF the space remaining feature */
        onView(withId(R.id.settings_space_remaining)).perform(click())
    }

    @Test
    fun spaceRemainingPhotoModeTest() {

        /** Verify that the settings view is present */
        onView(withId(R.id.settings)).check(matches(isDisplayed()))

        /** Navigate to Settings */
        this.performClicks(onView(withId(R.id.settings)))

        Thread.sleep(1000)

        /** Verify if the space remaining toggle view is present */
        onView(withId(R.id.settings_space_remaining_holder)).check(matches(isDisplayed()))
        onView(withId(R.id.settings_space_remaining)).check(matches(isDisplayed()))

        /** Toggle ON the space remaining feature */
        onView(withId(R.id.settings_space_remaining)).perform(click())

        /** Verify if the space remaining feature was toggled */
        onView(withId(R.id.settings_space_remaining)).check(matches(isChecked()))
        Thread.sleep(3000)

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

        /** Verifying that the app is IN video mode */
        if (mMainActivity?.activity?.mIsInPhotoMode == false) {

            /** Switch to photo mode */
            onView(withId(R.id.toggle_photo_video)).perform(click())
            Thread.sleep(500)
        }

        /** Verify that the space remaining feature is NOT present by checking if countdown toggle is present */
        onView(withId(R.id.countdown_toggle)).check(matches(isDisplayed()))

        /** Verify if the space remaining feature is not present */
        onView(withId(R.id.space_remaining)).check(matches(not(isDisplayed())))

        /** Take a photo */
        onView(withId(R.id.shutter)).perform(click())
        Thread.sleep(1500)

        /** Verify that the space remaining feature is NOT present by checking if countdown toggle is present */
        onView(withId(R.id.countdown_toggle)).check(matches(isDisplayed()))

        /** Verify if the space remaining feature is not present */
        onView(withId(R.id.space_remaining)).check(matches(not(isDisplayed())))

        /** Navigate to Settings */
        this.performClicks(onView(withId(R.id.settings)))

        Thread.sleep(2000);

        /** Toggle OFF the space remaining feature */
        onView(withId(R.id.settings_space_remaining)).perform(click())
    }
}
