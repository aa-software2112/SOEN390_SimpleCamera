package com.simplemobiletools.camera.activities

import android.Manifest
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import org.junit.After
import org.junit.Before
import com.simplemobiletools.camera.R

import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    /** Grants the write and camera persmissions - crucial to allowing the initial, main activity from
     * starting
     */
    @get:Rule val g = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @get:Rule val p = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    /** Start the main activity */
    @get:Rule val mActivityTestRule = ActivityTestRule <MainActivity>(MainActivity::class.java)

    @Before
    fun setUp() {
    }

    @Test
    fun testSettingsButton() {

        /** Initalizes the intents listener - all buttons that lead to the loading of new activities
         * will be logged in this library after .init() is called
         */
        Intents.init()

        /** The settings button is pressed  - must click twice; once only opens the submenu, the other
         * switches to the Setting activity */
        Espresso.onView(withId(R.id.settings)).perform(click(), click())

        /** Check if the settings activity intent was detected*/
        intended(hasComponent(SettingsActivity::class.java.getName()))

        /** Close the intent library */
        Intents.release()
    }

    @After
    fun tearDown() {
    }
}
