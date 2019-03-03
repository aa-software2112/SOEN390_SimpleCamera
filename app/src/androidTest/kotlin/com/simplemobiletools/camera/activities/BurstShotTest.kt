package com.simplemobiletools.camera.activities

import android.provider.MediaStore
import android.view.View
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
        assertTrue(((finalNumPhotos - initialNumPhotos) < 10 && (finalNumPhotos - initialNumPhotos) > 5))
        //assertEquals(5.0, (finalNumPhotos - initialNumPhotos) as Double, 2.0)

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