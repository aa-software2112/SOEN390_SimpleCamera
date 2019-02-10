package com.simplemobiletools.camera.activities

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import com.simplemobiletools.camera.R
import kotlinx.android.synthetic.main.activity_main.view.*
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CountdownTimerTest: BaseUITestSetup(TestActivities.MAIN_ACTIVITY) {

    /** This is NOT an acceptance test; it verifies that the application is laid out as expected for the
     * countdown timer feature. I.E.: The appropriate countdown buttons are apparent
     */
    @Test
    fun countdownTimerUITest()
    {

        /** Verify that the countdown toggle view is present */
        onView(withId(R.id.countdown_toggle)).check(matches(isDisplayed()))

        /** Capture the view and get the alpha value - wait until it fades out */
        var view = this.mMainActivity?.activity?.findViewById<View>(R.id.countdown_toggle)

        /** Wait until the alpha of the view fades, and perform double click */
        this.waitOnViewFade(view!!, 0.5F);

        /** Now that the button has faded, click on it once to bring it to full opacity,
         * and another time to show the time dropdown
         */
        onView(withId(R.id.countdown_toggle)).perform(click(), click())

        var expectedStrings = Array<String>(3, {""});

        expectedStrings.set(0, "5 sec")
        expectedStrings.set(1, "10 sec")
        expectedStrings.set(2, "15 sec")

        expectedStrings.forEach {
            onView(allOf(withParent(withId(R.id.countdown_times)), withText(it))).check(matches(allOf(isDisplayed())))
        }

        /** Click on the countdown button to de-select the time - sets up the app for the next loop */
        onView(withId(R.id.countdown_toggle)).perform(click())

        /** Click on each countdown button, and verify that the correct values are present in the timer view -
         * Note: we have verified that the buttons do infact exist, so withParent(...) is no longer necessary
         *
         * 0. Click on the countdown time button
         * 1. Click on the countdown time (5, 10, 15 seconds)
         * 2. Check that the number was appropriately updated in the "countdown" view
         * 3. Cancel the countdown
         * */
        expectedStrings.forEach {
            this.waitOnViewFade(view!!, 0.5F);

            onView(withId(R.id.countdown_toggle)).perform(click(), click())

            this.sleep(500)

            onView(withText(it)).perform(click())

            var strExpected = it.split(" ").get(0)

            this.sleep(1000)

            onView(withId(R.id.countdown_time_selected)).check(matches(allOf(isDisplayed(), withText(strExpected) )))

            onView(withId(R.id.countdown_toggle)).perform(click())

        }

    }

    /** Starts a 5 second countdown and takes a picture */
    /**
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
    */
    /** Starts a 5 second countdown and then cancels it during the countdown */
    /**
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
    */
    /*
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
    */
}
