package test.kotlin

import android.view.View
import android.widget.ImageView
import com.simplemobiletools.camera.R
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BurstShotUnitTest : KotlinRobolectric() {

    @Test
    fun testInitBurstEnabled() {
        /* Testing if mBurstEnable is initialized as false */
        Assert.assertEquals(false, mMainActivity?.mBurstEnabled)
    }

    @Test
    fun testRegularClickOnShutterDoesNotChangeBurstEnabled() {
        /*
        * Tests that when performing a regular click on the shutter, mBurstEnabled will not change.
        */

        /* Before pressing the shutter, mBurstEnabled should be False */
        Assert.assertEquals(false, mMainActivity?.mBurstEnabled)

        /* Perform a regular click on shutter */
        mMainActivity?.findViewById<ImageView>(R.id.shutter)?.performClick()

        /* After pressing the shutter, mBurstEnabled should still be False */
        Assert.assertEquals(false, mMainActivity?.mBurstEnabled)
    }

    @Test
    fun testBurstModeSetupRunnableProceeds() {
        /*
        * Tests that the mBurstModeSetup runnable will set mBurstEnabled to true if not in countdown mode and in photo mode
        */

        /* Initially, mBurstEnabled is false, mIsInCountdownMode is false and mIsInPhotoMode is true */
        Assert.assertEquals(false, mMainActivity?.mBurstEnabled)
        Assert.assertEquals(false, mMainActivity?.mIsInCountdownMode)
        Assert.assertEquals(true, mMainActivity?.mIsInPhotoMode)

        /* Call the mBurstModeSetup runnable, which attempts to set mBurstEnabled to true and call handleShutter() */
        mMainActivity?.mBurstHandler?.post(mMainActivity?.mBurstModeSetup)

        /* mBurstEnabled should be true */
        Assert.assertEquals(true, mMainActivity?.mBurstEnabled)
    }

    @Test
    fun testBurstModeSetupRunnableDoesNotProceedIfInCountdownMode() {
        /*
        * Tests that the mBurstModeSetup runnable does not set mBurstEnabled to true when mIsInCountdownMode is true.
        */

        /* Initially, mBurstEnabled is false and we set mIsInCountdownMode to true */
        Assert.assertEquals(false, mMainActivity?.mBurstEnabled)
        mMainActivity?.mIsInCountdownMode = true

        /* Call the mBurstModeSetup runnable, which attempts to set mBurstEnabled to true and call handleShutter() */
        mMainActivity?.mBurstHandler?.post(mMainActivity?.mBurstModeSetup)

        /* Since we're in countdown mode, mBurstEnabled should still be false */
        Assert.assertEquals(false, mMainActivity?.mBurstEnabled)
    }

    @Test
    fun testBurstModeSetupRunnableDoesNotProceedIfNotInPhotoMode() {
        /*
        * Tests that the mBurstModeSetup runnable does not set mBurstEnabled to true when mIsInPhotoMode is false.
        */

        /* Initially, mBurstEnabled is false and we set mIsInPhotoMode to false */
        Assert.assertEquals(false, mMainActivity?.mBurstEnabled)
        mMainActivity?.mIsInPhotoMode = false

        /* Call the mBurstModeSetup runnable, which attempts to set mBurstEnabled to true and call handleShutter() */
        mMainActivity?.mBurstHandler?.post(mMainActivity?.mBurstModeSetup)

        /* Since we're in countdown mode, mBurstEnabled should still be false */
        Assert.assertEquals(false, mMainActivity?.mBurstEnabled)
    }

    @Test
    fun testToggleBurstButton() {
        /*
        * Tests that toggleBurstButton() will set the burst icon to visible and the shutter icon to invisible if mBurstEnabled is true
        */

        var burstIcon = mMainActivity?.findViewById<ImageView>(R.id.burst)
        var shutter = mMainActivity?.findViewById<ImageView>(R.id.shutter)

        /* Initially the burstIcon should be GONE, and the shutter icon should VISIBLE */
        Assert.assertTrue(burstIcon?.visibility == View.GONE)
        Assert.assertTrue(shutter?.visibility == View.VISIBLE)

        mMainActivity?.mBurstEnabled = true
        mMainActivity?.toggleBurstModeButton()

        /* After toggling when mBurstEnable is true, burstIcon becomes VISIBLE and shutter becomes GONE */
        Assert.assertTrue(burstIcon?.visibility == View.VISIBLE)
        Assert.assertTrue(shutter?.visibility == View.GONE)
    }

    @Test
    fun testHandleShutterToggleBurstMode() {
        /* Tests that handleShutter() will call toggleBurstButton() if mIsInPhotoMode is true,  mBurstEnabled is true and mIsInCountdownMode is false */

        var burstIcon = mMainActivity?.findViewById<ImageView>(R.id.burst)
        var shutter = mMainActivity?.findViewById<ImageView>(R.id.shutter)

        /* Initially the burstIcon should be GONE, and the shutter icon should VISIBLE */
        Assert.assertTrue(burstIcon?.visibility == View.GONE)
        Assert.assertTrue(shutter?.visibility == View.VISIBLE)

        mMainActivity?.mBurstEnabled = true
        Assert.assertEquals(true, mMainActivity?.mIsInPhotoMode)
        Assert.assertEquals(false, mMainActivity?.mIsInCountdownMode)

        mMainActivity?.handleShutter()

        /* After calling handleShutter(), it will call toggleBurstModeIcon() */
        Assert.assertTrue(burstIcon?.visibility == View.VISIBLE)
        Assert.assertTrue(shutter?.visibility == View.GONE)
    }
}
