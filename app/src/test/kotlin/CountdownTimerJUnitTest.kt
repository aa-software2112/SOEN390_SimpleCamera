package test.kotlin

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
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
        mMainActivity?.unsetCountdownMode()
        // Verify if mIsInCountdownMode is false
        Assert.assertFalse(mMainActivity!!.mIsInCountdownMode)
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
        println("Testing if countdownDropdown is Invisible, then countdownDropdown is changed to visible")
        var countdownDropdownRelativeLayout = mMainActivity?.findViewById<RelativeLayout>(R.id.countdown_times)
        Assert.assertNotNull(countdownDropdownRelativeLayout)

        //  Verify if countdownDropdownRelativeLayout is INVISIBLE at first
        Assert.assertTrue(countdownDropdownRelativeLayout?.visibility == View.INVISIBLE)

        //  Call toggleCountdownTimerDropdown() method to switch countdownDropdownRelativeLayout's visibility: from INVISIBLE to VISIBLE
        mMainActivity?.toggleCountdownTimerDropdown()

        //  Verify if countdownDropdownRelativeLayout is now VISIBLE
        Assert.assertTrue(countdownDropdownRelativeLayout?.visibility == View.VISIBLE)
    }

    @Test
    fun handleShutter_mIsInCountdownMode_toggleBottomButtons() {
        println("Testing if mIsInPhotoMode is true AND mIsInCountdownMode is false, then toggleBottomButtons() is called which will make the shutter button Clickable")
        //  Manually "turning on" the camera
        mMainActivity?.setIsCameraAvailable(true)

        //  Verify if mIsCameraAvailable is true
        Assert.assertTrue(mMainActivity!!.mIsCameraAvailable)

        //  Call handleShutter()
        mMainActivity?.handleShutter()

        //  Verify if camera is IsInPhotoMode and not InCountdownMode
        Assert.assertTrue(mMainActivity!!.mIsInPhotoMode)
        Assert.assertFalse(mMainActivity!!.mIsInCountdownMode)

        //  Get the shutter image
        var shutterImage = mMainActivity?.findViewById<ImageView>(R.id.shutter)
        Assert.assertNotNull(shutterImage)

        // Call toggleBottomButtons()
        mMainActivity?.toggleBottomButtons(false)

        // Verify if the shutter is indeed clickable
        Assert.assertTrue(shutterImage!!.isClickable)
    }

    @Test
    fun checkButtons_initPhotoMode_countdown_toggleTest() {
        println("Testing if mIsInPhotoMode is true, then initPhotoMode() is called to verify that countdown_toggle is visible")
        //  Verify if initialized InPhotoMode
        Assert.assertTrue(mMainActivity!!.mIsInPhotoMode)

        // Call checkButtons() which will then call initPhotoMode() since
        mMainActivity?.checkButtons()

        //  Get the shutter image
        var countdownToggleImageView = mMainActivity?.findViewById<ImageView>(R.id.countdown_toggle)
        Assert.assertNotNull(countdownToggleImageView)

        //  Verify if countdownToggleImageView is VISIBLE
        Assert.assertTrue(countdownToggleImageView?.visibility == View.VISIBLE)
    }

    @Test
    fun checkButtons_tryInitVideoMode_initVideoButtons_countdown_toggleTest() {
        println("Testing if mIsInPhotoMode is false, then tryInitVideoMode() is called, which calls initVideoButtons(), to finally verify if countdown_toggle AND countdown_times are INvisible")
        var countdownToggleImageView = mMainActivity?.findViewById<ImageView>(R.id.countdown_toggle)
        Assert.assertNotNull(countdownToggleImageView)

        println(countdownToggleImageView?.visibility)

        //  Manually "turning on" the camera
        mMainActivity?.setIsCameraAvailable(true)

        //  "Clicking" to open switch into Video mode
        mMainActivity?.findViewById<ImageView>(R.id.toggle_photo_video)?.performClick()

        // Call tryInitVideoMode() which then calls initVideoButtons()
        mMainActivity?.tryInitVideoMode()

        //  Verify if countdownToggleImageView is now INVISIBLE
        Assert.assertTrue(countdownToggleImageView?.visibility == View.INVISIBLE)
    }

    @Test
    fun startCountdown_startTest() {
        println("Testing if startCountdown() is called, then unsetCountdownMode() is called")
        //  Manually "turning on" the camera
        mMainActivity?.setIsCameraAvailable(true)

        //  Verify if mIsCameraAvailable is true
        Assert.assertTrue(mMainActivity!!.mIsCameraAvailable)

        //  "Clicking" to open the countdown and choose a countdown length
        mMainActivity?.findViewById<ImageView>(R.id.countdown_toggle)?.performClick()
        mMainActivity?.findViewById<Button>(R.id.btn_short_timer)?.performClick()

        //  Verify if camera IsInPhotoMode and is InCountdownMode
        Assert.assertTrue(mMainActivity!!.mIsInPhotoMode)
        Assert.assertTrue(mMainActivity!!.mIsInCountdownMode)

        // Call startCountdown()
        mMainActivity?.startCountdown()

        /** Want to use CountDownTimer to wait 10 seconds to see if unsetCountdownMode() is called, and verify mIsInCountdownMode is false */

        mMainActivity?.unsetCountdownMode()

        // Verify if mIsInCountdownMode is false
        Assert.assertFalse(mMainActivity!!.mIsInCountdownMode)
    }
}
