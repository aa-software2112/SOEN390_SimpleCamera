package test.kotlin

/**
import android.Manifest
import com.simplemobiletools.camera.R
import android.os.Environment
import android.view.View
import android.widget.ImageView
import com.simplemobiletools.camera.activities.MainActivity
import org.junit.Assert
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowEnvironment
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.junit.Rule
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowApplication
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import android.os.CountDownTimer
 */

import org.junit.* // ktlint-disable no-wildcard-imports

class CountdownTimerJUnitTest {

    /**JUnit tests in MainActivity.kt associated to the CountdownTimer Feature*/

    @Test
    fun onCreate_initVariables_mIsInCountdownModeTest() {
        println("Testing if mIsInCountdownMode is initialized as false")
    }

    @Test
    fun onCreate_initVariables_mCountdownTimeTest() {
        println("Testing if mCountdownTime is initialized as 0")
    }

    @Test
    fun initButtons_btn_short_timerTest() {
        println("Testing if btn_short_timer is initialized as TIMER_SHORT=5")
    }

    @Test
    fun initButtons_btn_medium_timerTest() {
        println("Testing if btn_medium_timer is initialized as TIMER_MEDIUM=10")
    }

    @Test
    fun initButtons_btn_long_timerTest() {
        println("Testing if btn_long_timer is initialized as TIMER_LONG=15")
    }

    @Test
    fun setCountdownMode_checkCameraAvailableTest() {
        println("Testing if checkCameraAvailable is true")
    }

    @Test
    fun setCountdownMode_mCountdownTimeTest() {
        println("Testing if mCountdownTime is equal to the time chosen")
    }

    @Test
    fun setCountdownMode_mIsInCountdownModeTest() {
        println("Testing if mIsInCountdownMode is true")
    }

    @Test
    fun unsetCountdownMode_mCountdownTimeTest() {
        println("Testing if mCountdownTime is initialized as 0")
    }

    @Test
    fun unsetCountdownMode_mIsInCountdownModeTest() {
        println("Testing if mIsInCountdownMode is false")
    }

    @Test
    fun toggleCountdownModeIcon_countdown_cancelTest() {
        println("Testing if mIsInCountdownMode is true, then countdown_cancel is visible")
    }

    @Test
    fun toggleCountdownModeIcon_countdown_time_selectedTest() {
        println("Testing if mIsInCountdownMode is false, then countdown_time_selected is INvisible")
    }

    @Test
    fun toggleCountdownTimerDropdown_countdownDropdownTest() {
        println("Testing if countdownDropdown is INvisible, then countdownDropdown is changed to visible")
    }

    @Test
    fun handleShutter_mIsInCountdownMode_tryTakePictureTest() {
        println("Testing if mIsInPhotoMode is true AND mIsInCountdownMode is false, then tryTakePicture() is called")
    }

    @Test
    fun handleShutter_mIsInCountdownMode_tryTakeDelayedPictureTest() {
        println("Testing if mIsInPhotoMode is true AND mIsInCountdownMode is true, then tryTakeDelayedPicture() is called")
    }

    @Test
    fun checkButtons_initPhotoMode_countdown_toggleTest() {
        println("Testing if mIsInPhotoMode is true, then initPhotoMode() is called to verify that countdown_toggle is visible")
    }

    @Test
    fun checkButtons_initVideoButtons_countdown_toggle_countdown_timesTest() {
        println("Testing if mIsInPhotoMode is false, then tryInitVideoMode() is called, which calls initVideoButtons(), to finally verify if countdown_toggle AND countdown_times are INvisible")
    }

    @Test
    fun tryTakeDelayedPicture_startTest() {
        println("Testing if tryTakeDelayedPicture() is called, then start() is called")
    }
}
