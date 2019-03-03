package com.simplemobiletools.camera.activities

import android.Manifest
import android.os.Build
import androidx.test.espresso.action.ViewActions.click
import androidx.test.rule.ActivityTestRule

import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith

import android.view.View
import androidx.test.espresso.ViewInteraction
import android.provider.MediaStore



enum class TestActivities {
    MAIN_ACTIVITY,
    SETTINGS_ACTIVITY,
    SPLASH_ACTIVITY
}

/** Every Acceptance and UI-based test should inherit from this class; it sets the environment correctly
 * so that the code included here does not need to be reproduced in every test class.
 *
 * Notes on acceptance testing for this application:
 *
 * Slow Emulators: Adding in Thread.sleep(milliseconds) is helpful in places where the emulator and the device-itself can
 * diverge in their time-based behavior. By putting in a Thread.sleep(milliseconds) before and after performing certain actions,
 * we give the application being run on a slow emulator the time to load and render its buttons, and get to the same state it would on the application
 * being run on an Android device.
 */
@RunWith(AndroidJUnit4::class)
open class BaseUITestSetup(activityUnderTest: TestActivities) {

    /** Grants the write and camera persmissions - crucial to allowing the initial, main activity from
     * starting
     */
    @get:Rule val r = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE)
    @get:Rule val g = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @get:Rule val p = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    /** Start the main activity before the first @Before method is called, and is released (terminated)
     * after the last @After method is called */
    @get:Rule var mMainActivity: ActivityTestRule<MainActivity>? = null
    @get:Rule var mSettingsActivity: ActivityTestRule<SettingsActivity>? = null
    @get:Rule var mSplashActivity: ActivityTestRule<SplashActivity>? = null

    /** The fade value (the value to which fading out reaches) */
    private var fadeValue = 0.5F

    init {
        /** This initializer uses the activity enum passed to the constructor in order to launch the appropriate activity */
        mMainActivity = ActivityTestRule <MainActivity>(MainActivity::class.java, false, TestActivities.MAIN_ACTIVITY == activityUnderTest)
        mSettingsActivity = ActivityTestRule<SettingsActivity>(SettingsActivity::class.java, false, TestActivities.SETTINGS_ACTIVITY == activityUnderTest)
        mSplashActivity = ActivityTestRule<SplashActivity>(SplashActivity::class.java, false, TestActivities.SPLASH_ACTIVITY == activityUnderTest)
    }

    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.startsWith("unknown") ||
            Build.MODEL.contains("google_sdk") ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK built for x86") ||
            Build.MANUFACTURER.contains("Genymotion") ||
            Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic") || "google_sdk" == Build.PRODUCT)
    }

    /** Waits for the fade to take place completely; only if in emulator mode (to compensate for slow runtime) */
    fun waitOnViewFade(someView: View) {
        /** Only wait in emulator mode */
        if (this.isEmulator()) {
            while (someView.alpha.compareTo(fadeValue) != 0) {
                Thread.sleep(50)
            }
        }
    }

    /** Sleeps the emulator for a given time in milliseconds - if using a device, use Thread.sleep(...)
     * without calling this method
     * */
    fun sleep(milliseconds: Long) {
        if (this.isEmulator())
            Thread.sleep(milliseconds)
    }

    /** There are times where the emulator will need a double click,
     * whereas the android device won't
     */
    fun performClicks(vi: ViewInteraction) {
        if (this.isEmulator())
            vi.perform(click(), click())
        else
            vi.perform(click())
    }


}
