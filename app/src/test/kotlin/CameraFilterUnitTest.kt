package test.kotlin

import androidx.appcompat.widget.AppCompatButton
import com.simplemobiletools.camera.R
import com.simplemobiletools.commons.extensions.isGone
import com.simplemobiletools.commons.extensions.isVisible
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CameraFilterUnitTest : KotlinRobolectric() {

    @Test
    fun testButtonsOnCreate() {
        /*
        * Testing that all buttons for the filters are not visible on app init
         */

        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_none)?.isGone()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_mono)?.isGone()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_negative)?.isGone()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_solarize)?.isGone()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_sepia)?.isGone()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_posterize)?.isGone()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_whiteboard)?.isGone()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_blackboard)?.isGone()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_aqua)?.isGone()!!)
    }

    @Test
    fun testShowAllFilters() {
        /*
        * At runtime, the camera characteristics are obtained and only certain filters are made
        * available to the user depending on his camera device. We are testing the function that
        * make visible the camera filters.
         */

        val filterIndices: IntArray = intArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8)

        mMainActivity?.hideNotAvailableFilters(filterIndices)

        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_none)?.isVisible()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_mono)?.isVisible()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_negative)?.isVisible()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_solarize)?.isVisible()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_sepia)?.isVisible()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_posterize)?.isVisible()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_whiteboard)?.isVisible()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_blackboard)?.isVisible()!!)
        assert(mMainActivity?.findViewById<AppCompatButton>(R.id.filter_aqua)?.isVisible()!!)
    }

    @Test
    fun testPreviewFilter() {
        /*
        * Test characterizing that the previewFilter function cannot be tested with this
        * testing config.
         */
        val filterNoneIndex = 0

        assert(!mMainActivity!!.testPreviewFilterWrapper(filterNoneIndex))
    }
}
