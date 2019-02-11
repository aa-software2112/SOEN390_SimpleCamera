package test.kotlin

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.simplemobiletools.camera.helpers.TIMER_SHORT
import com.simplemobiletools.camera.helpers.TIMER_MEDIUM
import com.simplemobiletools.camera.helpers.TIMER_LONG
import com.simplemobiletools.camera.R
import org.junit.* // ktlint-disable no-wildcard-imports
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CountdownTimerJUnitTest : KotlinRobolectric() {

    /**
    ----------------------------------------------------------------------------------------------------
    /**Tests in MainActivity.kt associated to the CountdownTimer Feature*/
    ----------------------------------------------------------------------------------------------------
    */

    @Test
    fun onCreate_initVariables_mIsInCountdownModeTest() {
        println("Testing if mIsInCountdownMode is initialized as false")
        //  Verifying that initially, mIsInCountdownMode is false
        Assert.assertFalse(mMainActivity!!.mIsInCountdownMode)
    }

    @Test
    fun onCreate_initVariables_mCountdownTimeTest() {
        println("Testing if mCountdownTime is initialized as 0")
        //  Verifying that initially, mCountDownTime is 0
        Assert.assertEquals(0, mMainActivity?.mCountdownTime)
    }

    @Test
    fun initButtons_btn_short_timerTest() {
        println("Testing if btn_short_timer is initialized as TIMER_SHORT=5")
        //  Manually "turning on" the camera
        mMainActivity?.setIsCameraAvailable(true)

        //  "Clicking" to open the countdown and choose a countdown length
        mMainActivity?.findViewById<ImageView>(R.id.countdown_toggle)?.performClick()
        mMainActivity?.findViewById<Button>(R.id.btn_short_timer)?.performClick()

        Assert.assertTrue(mMainActivity!!.mIsInCountdownMode)
        Assert.assertEquals(TIMER_SHORT, mMainActivity?.mCountdownTime)
    }

    @Test
    fun initButtons_btn_medium_timerTest() {
        println("Testing if btn_medium_timer is initialized as TIMER_MEDIUM=10")
        //  Manually "turning on" the camera
        mMainActivity?.setIsCameraAvailable(true)

        //  "Clicking" to open the countdown and choose a countdown length
        mMainActivity?.findViewById<ImageView>(R.id.countdown_toggle)?.performClick()
        mMainActivity?.findViewById<Button>(R.id.btn_medium_timer)?.performClick()

        Assert.assertTrue(mMainActivity!!.mIsInCountdownMode)
        Assert.assertEquals(TIMER_MEDIUM, mMainActivity?.mCountdownTime)
    }

    @Test
    fun initButtons_btn_long_timerTest() {
        println("Testing if btn_long_timer is initialized as TIMER_LONG=15")
        //  Manually "turning on" the camera
        mMainActivity?.setIsCameraAvailable(true)

        //  "Clicking" to open the countdown and choose a countdown length
        mMainActivity?.findViewById<ImageView>(R.id.countdown_toggle)?.performClick()
        mMainActivity?.findViewById<Button>(R.id.btn_long_timer)?.performClick()

        Assert.assertTrue(mMainActivity!!.mIsInCountdownMode)
        Assert.assertEquals(TIMER_LONG, mMainActivity?.mCountdownTime)

    }

    @Test
    fun setCountdownMode_checkCameraAvailableTest() {
        println("Testing if checkCameraAvailable is true")
        //  Manually "turning on" the camera
        mMainActivity?.setIsCameraAvailable(true)
        // Asserting that checkCameraAvailable verifies the camera is on and outputs "true"
        Assert.assertTrue(mMainActivity!!.checkCameraAvailable())
    }

    @Test
    fun setCountdownMode_mCountdownTimeTest() {
        println("Testing if mCountdownTime is equal to the time chosen")
        //  Manually "turning on" the camera
        mMainActivity?.setIsCameraAvailable(true)
        //  Assigning a mock time
        var mockTime = 5
        //  Calling setCountdownMode
        mMainActivity?.setCountdownMode(mockTime)
        //  Asserting that mCountdownTime adheres to chosen mockTime
        Assert.assertEquals(mockTime, mMainActivity?.mCountdownTime)
    }

    @Test
    fun setCountdownMode_mIsInCountdownModeTest() {
        println("Testing if mIsInCountdownMode is true")
        //  Manually "turning on" the camera
        mMainActivity?.setIsCameraAvailable(true)
        //  Assigning a mock time
        var mockTime = 5
        //  Calling setCountdownMode
        mMainActivity?.setCountdownMode(mockTime)
        //  Asserting that mIsInCountdownMode has been set to true
        Assert.assertTrue(mMainActivity!!.mIsInCountdownMode)
    }

    @Test
    fun unsetCountdownMode_mCountdownTimeTest() {
        println("Testing if mCountdownTime is initialized as 0")
        mMainActivity?.unsetCountdownMode()
        //  Asserting that mCountdownTime has been set to 0
        Assert.assertEquals(0, mMainActivity?.mCountdownTime)
    }

    @Test
    fun unsetCountdownMode_mIsInCountdownModeTest() {
        println("Testing if mIsInCountdownMode is false")
    }

    @Test
    fun toggleCountdownModeIcon_countdown_cancelTest() {
        println("Testing if mIsInCountdownMode is false, then countdown_cancel is INvisible")
        var countdownCancelImageView = mMainActivity?.findViewById<ImageView>(R.id.countdown_cancel)
        Assert.assertNotNull(countdownCancelImageView)

        //  Verify if mIsInCountdownMode is false
        Assert.assertFalse(mMainActivity!!.mIsInCountdownMode)

        //  If mIsInCountdownMode is false, then verify if countdownCancelImageView is INVISIBLE
        Assert.assertTrue(countdownCancelImageView?.visibility == View.INVISIBLE)
    }

    @Test
    fun toggleCountdownModeIcon_countdown_time_selectedTest() {
        println("Testing if mIsInCountdownMode is false, then countdown_time_selected is INvisible")
        var countdownTimeSelectedTextView = mMainActivity?.findViewById<TextView>(R.id.countdown_time_selected)
        Assert.assertNotNull(countdownTimeSelectedTextView)

        //  Verify if mIsInCountdownMode is false
        Assert.assertFalse(mMainActivity!!.mIsInCountdownMode)

        //  If mIsInCountdownMode is false, then verify if countdownTimeSelectedTextView is INVISIBLE
        Assert.assertTrue(countdownTimeSelectedTextView?.visibility == View.INVISIBLE)
    }

    @Test
    fun toggleCountdownTimerDropdown_countdownDropdownTest() {
        println("Testing if countdownDropdown is INvisible, then countdownDropdown is changed to visible")
        var countdownDropdownLinearLayout = mMainActivity?.findViewById<LinearLayout>(R.id.countdown_times)
        Assert.assertNotNull(countdownDropdownLinearLayout)

        /**  Debugging purpose: in View.java, VISIBLE = 0x00000000 and INVISIBLE = 0x00000004

        if (countdownDropdownLinearLayout?.visibility == 4) {
        println("invisible")
        }

            countdownDropdownLinearLayout IS INVISIBLE
         */

        //  Verify if countdownDropdownLinearLayout is INVISIBLE at first
        Assert.assertTrue(countdownDropdownLinearLayout?.visibility == View.INVISIBLE)

        //  Call toggleCountdownTimerDropdown() method to switch countdownDropdownLinearLayout's visibility: from INVISIBLE to VISIBLE
        mMainActivity?.toggleCountdownTimerDropdown()

        //  Verify if countdownDropdownLinearLayout is now VISIBLE
        Assert.assertTrue(countdownDropdownLinearLayout?.visibility == View.VISIBLE)

        /**  Debugging purpose: in View.java, VISIBLE = 0x00000000 and INVISIBLE = 0x00000004

        if (countdownDropdownLinearLayout?.visibility == 0) {
        println("visible")
        }

            countdownDropdownLinearLayout IS INDEED VISIBLE
         */
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
