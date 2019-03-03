package com.simplemobiletools.camera.activities

import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.* // ktlint-disable no-wildcard-imports
import androidx.test.filters.LargeTest
import androidx.test.runner.AndroidJUnit4
import com.simplemobiletools.camera.R
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import junit.framework.Assert.assertFalse
import kotlinx.android.synthetic.main.activity_main.view.*
import org.hamcrest.Matchers.* // ktlint-disable no-wildcard-imports
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import test.kotlin.com.simplemobiletools.camera.activities.ViewActionHelper

@LargeTest
@RunWith(AndroidJUnit4::class)
class BurstShotTest : BaseUITestSetup(TestActivities.MAIN_ACTIVITY) {

    var expectedStrings = Array<String>(3, { "" })
    var SHUTTER_TO_BURST_MODE_TIME: Long = 2200
    var ONE_SECOND: Long = 1000

    @Before
    fun setup() {

        /** Release the button */
        //onView(withId(R.id.shutter)).perform(ViewActionHelper.release())
    }

    @After
    fun tearDown()
    {
        /** Release the button */
        //onView(withId(R.id.shutter)).perform(ViewActionHelper.release())
    }

    /** Acceptance Test 7: Functional: Continuous captures on shutter button hold when not in countdown mode and not in video mode: Automatic
     * 1. Application Starts
     * 2. Hold down shutter for 3 seconds - 2seconds to activate the burst
     * 3. Verifying camera is neither in countdown mode nor video mode
     * 4. Verifying that shutter button is INVISIBLE and that burst button is VISIBLE in this state
     * 5. Photos are taken in burst mode for 1 second
     * 6. Burst button is released
     * 7. Verifying that shutter button is VISIBLE and that burst button is INVISIBLE in this state
     * 8. Verifying that more than one photo was taken
     * Written By: Laura Wheatley
     */
    @Test
    fun AcceptanceTest7() {

        /** Hold down shutter for 2 seconds */
        onView(withId(R.id.shutter)).perform(ViewActionHelper.holdDown())

        /** Verifying that the app is not in countdown mode or video mode */
        assertTrue(mMainActivity?.activity?.mIsInCountdownMode == false)
        assertTrue(mMainActivity?.activity?.mIsVideoCaptureIntent == false)

        Thread.sleep(SHUTTER_TO_BURST_MODE_TIME)

        /** Verifying button visibilities in burst shot mode */
        onView(anyOf(withId(R.id.shutter))).check(matches(not(isDisplayed())));
        onView(anyOf(withId(R.id.burst))).check(matches(isDisplayed()));

        /** Release the button */
        onView(withId(R.id.shutter)).perform(ViewActionHelper.release())

        var finalNumPhotos = getNumPhotos()

        /** Verifying button visibilities after leaving burst shot mode */
        onView(anyOf(withId(R.id.burst))).check(matches(not(isDisplayed())));
        onView(anyOf(withId(R.id.shutter))).check(matches(isDisplayed()));

        assertTrue(finalNumPhotos > 1)

    }

    /** Acceptance Test 8: Functional: When operating in burst mode, every capture taken during this duration should be saved﻿: Automatic
     * 1. Application Starts
     * 2. Hold down shutter for 3 seconds - 2seconds to activate the burst
     * 3. Photos are taken in burst mode for 1 second
     * 4. Check that 7 - 10 photos were taken
     * Written By: Anthony Andreoli
     */
    @Test
    fun AcceptanceTest8() {

        var initialNumPhotos = getNumPhotos()

        System.out.println("Initial Number of Photos: " + initialNumPhotos)

        /** Hold down shutter for 2 seconds */
        onView(withId(R.id.shutter)).perform(ViewActionHelper.holdDown())

        Thread.sleep(SHUTTER_TO_BURST_MODE_TIME)

        /** Hold down the shutter for 1 more second, take roughly 10 pictures (since the rate is 100ms/photo) */
        Thread.sleep(ONE_SECOND*3);

        /** Release the button */
        onView(withId(R.id.shutter)).perform(ViewActionHelper.release())

        var finalNumPhotos = getNumPhotos()

        System.out.println("Final Number of Photos: " + finalNumPhotos)
        System.out.println("Difference: " + (finalNumPhotos - initialNumPhotos))
        assertTrue(((finalNumPhotos - initialNumPhotos) < 5 && (finalNumPhotos - initialNumPhotos) > 2))

    }

    fun getNumPhotos() : Int {
        val columns = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID)
        val orderBy = MediaStore.Images.Media._ID
        val cursor = mMainActivity?.activity?.applicationContext?.contentResolver?.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy)
        //Total number of images
        if (cursor != null) {
            return cursor!!.getCount()
        }

        return -1
    }

}
