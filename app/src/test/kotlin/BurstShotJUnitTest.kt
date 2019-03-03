package test.kotlin

import android.widget.ImageView
import com.simplemobiletools.camera.R
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BurstShotJUnitTest : KotlinRobolectric() {

    @Test
    fun testInit_mBurstEnabled() {
        /* Testing if mBurstEnable is initialized as false */
        Assert.assertEquals(false, mMainActivity?.mBurstEnabled)
    }

    @Test
    fun performRegularClickOnShutterDoesNotChangeBurstEnabled() {
        /*
        * Tests that when performing a regular click on the shutter, mBurstEnabled does not change
        */

        /* Before pressing the shutter, mBurstEnabled should be False */
        Assert.assertEquals(false, mMainActivity?.mBurstEnabled)

        mMainActivity?.findViewById<ImageView>(R.id.shutter)?.performClick()

        /* After pressing the shutter, mBurstEnabled should still be False */
        Assert.assertEquals(false, mMainActivity?.mBurstEnabled)

    }

    @Test
    fun mBurstModeSetupRunnableDoesNotStartIfInCountdownMode() {
        /*
        * Tests that the mBurstModeSetup runnable does not call handleShutter() when mIsInCountdownMode is true.
        */
    }

    @Test
    fun mBurstModeSetupRunnableDoesNotStartIfInVideoMode() {
        /*
        * Tests that the mBurstModeSetup runnable does not call handleShutter() when mIsInPhotoMode is false.
        * */

    }


}

