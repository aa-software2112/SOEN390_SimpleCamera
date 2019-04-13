package test.kotlin

import android.widget.TextView
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.simplemobiletools.camera.R
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.simplemobiletools.camera.implementations.CaptionStamper
import com.simplemobiletools.commons.extensions.isVisible
import org.junit.* // ktlint-disable no-wildcard-imports


@RunWith(RobolectricTestRunner::class)
class StampUnitTest : KotlinRobolectric() {

    @Test
    fun stampTest() {
        //Mocks
        var mockCaptionStamper = mock<CaptionStamper>()

        // Add Mocks
        mMainActivity?.setCaptionStamper(mockCaptionStamper)

        //Check initial value of caption stamp
        Assert.assertEquals(true, mMainActivity?.findViewById<TextView>(R.id.caption_toggle)?.isVisible())
        Assert.assertEquals(false, mMainActivity?.findViewById<TextView>(R.id.caption_stamp)?.isVisible())
        Assert.assertEquals("", mMainActivity?.findViewById<TextView>(R.id.caption_input)?.text.toString())

        //Toggle the stamp
        mMainActivity?.findViewById<TextView>(R.id.caption_toggle)?.performClick()

        //Set stamp text
        mMainActivity?.findViewById<TextView>(R.id.caption_input)?.text = "Caption Test"
        Assert.assertEquals("Caption Test", mMainActivity?.findViewById<TextView>(R.id.caption_input)?.text.toString())

        //Capture stamp
        mMainActivity?.findViewById<TextView>(R.id.caption_stamp)?.performClick()


        verify(mMainActivity?.mCaptionStamper)?.performStamp("Caption Test")

    }
}
