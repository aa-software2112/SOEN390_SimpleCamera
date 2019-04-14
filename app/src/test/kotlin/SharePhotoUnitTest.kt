package test.kotlin

import android.widget.ImageView
import com.simplemobiletools.camera.R
import com.simplemobiletools.commons.extensions.isVisible
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SharePhotoUnitTest : KotlinRobolectric() {

    @Test
    fun testShareButtonOnCreate() {
        // Testing that share button is visible on app init
        assert(mMainActivity?.findViewById<ImageView>(R.id.share)?.isVisible()!!)
    }
    @Test
    fun onCreate_initVariables_mWillShareNextMedia () {
        //  Verifying that initially, mWillShareNextMedia  is false
        println("Testing if mWillShareNextMedia is initialized as false")
        Assert.assertFalse(mMainActivity!!.mWillShareNextMedia )
    }
    @Test
    fun testButton_onClick_mWillShareNextMedia() {
        // Verifying that, on clicking "share" mWillShareNextMedia becomes true
        println("Testing if mWillShareNextMedia is true on clicking share button")
        mMainActivity?.findViewById<ImageView>(R.id.share)?.performClick()
        Assert.assertTrue(mMainActivity!!.mWillShareNextMedia )
    }

}
