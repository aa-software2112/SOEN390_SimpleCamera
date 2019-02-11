package com.simplemobiletools.camera.activities

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import com.simplemobiletools.camera.R
import com.simplemobiletools.commons.helpers.BROADCAST_REFRESH_MEDIA
import junit.framework.Assert.assertTrue
import kotlinx.android.synthetic.main.activity_main.view.*
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class CountdownTimerTest: BaseUITestSetup(TestActivities.MAIN_ACTIVITY) {

    var view: View? = null;
    var expectedStrings = Array<String>(3, {""});

    @Before
    fun setup()
    {
        /** Capture the view  */
        view = this.mMainActivity?.activity?.findViewById<View>(R.id.countdown_toggle)

        /** Initialize expected strings */
        expectedStrings.set(0, "5 sec")
        expectedStrings.set(1, "10 sec")
        expectedStrings.set(2, "15 sec")
    }


    /** This is NOT an acceptance test; it verifies that the application is laid out as expected for the
     * countdown timer feature. I.E.: The appropriate countdown buttons are apparent
     *
     * Written By: Anthony Andreoli
     */
    @Test
    fun countdownTimerUITest()
    {

        /** Verify that the countdown toggle view is present */
        onView(withId(R.id.countdown_toggle)).check(matches(isDisplayed()))

        /** Wait until the alpha of the view fades, and perform double click */
        this.waitOnViewFade(view!!);

        /** Now that the button has faded, click on it once to bring it to full opacity,
         * and another time to show the time dropdown
         */
        this.performClicks(onView(withId(R.id.countdown_toggle)))

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
            this.waitOnViewFade(view!!);

            this.performClicks(onView(withId(R.id.countdown_toggle)))

            this.sleep(500)

            onView(withText(it)).perform(click())

            var strExpected = it.split(" ").get(0)

            this.sleep(1000)

            onView(withId(R.id.countdown_time_selected)).check(matches(allOf(isDisplayed(), withText(strExpected) )))

            onView(withId(R.id.countdown_toggle)).perform(click())

        }

    }

    /** Acceptance Test 2: Photo taken after selected countdown time: Automatic
     * Starts a 10 second countdown and takes a picture
     * 1. Application starts
     * 2. Countdown is toggled to ON
     * 3. A countdown of 10 seconds is selected
     * 4. The shutter is pressed
     * 5. Assert that a photo was taken
     *
     * Written By: Anthony Andreoli & Krishna Patel
     * */
    @Test
    fun photoTakenAfter10Seconds() {

        /** Wait for the button to fade out */
        this.waitOnViewFade(view!!);

        /** Select countdown */
        this.performClicks(onView(withId(R.id.countdown_toggle)))

        /** Select 10 second countdown */
        onView(withText(expectedStrings[1])).perform(click())

        this.sleep(1000)

        /** Press the shutter */
        onView(withId(R.id.shutter)).perform(click())

        Thread.sleep(((expectedStrings[1].split(" ")[0]).toLong() + 2)*1000)

        assertTrue(this.mMainActivity?.activity?.getPhotoTaken() == true)

    }

    /** Acceptance Test 3: Functional: Photo canceled midway through the countdown: Automatic
     * Starts a 10 second countdown and cancels photo before countdown reaches 0
     * 1. Application starts
     * 2. Countdown is toggled to ON
     * 3. A countdown of 10 seconds is selected
     * 4. The shutter is pressed
     * 5. 1 second in, photo is cancelled
     * 6. Assert that photo was not taken
     *
     * Written By: Anthony Andreoli & Krishna Patel
     * */
    @Test
    fun countdownTimerCancel() {
        /** Wait for the button to fade out */
        this.waitOnViewFade(view!!);

        /** Select countdown */
        this.performClicks(onView(withId(R.id.countdown_toggle)))

        /** Select 10 second countdown */
        onView(withText(expectedStrings[1])).perform(click())

        this.sleep(1000)

        /** Press the shutter */
        onView(withId(R.id.shutter)).perform(click())

        Thread.sleep(2000)

        /** Cancel the photo */
        onView(withId(R.id.countdown_toggle)).perform(click())

        /** Assert that photo was NOT taken */
        assertTrue(this.mMainActivity?.activity?.getPhotoTaken() == false)

        /** Check that display returned to original state - the countdown timer is no longer visible */
        onView(withId(R.id.countdown_time_selected)).check(matches(not(isDisplayed())))
    }

}
