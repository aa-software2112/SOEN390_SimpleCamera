package test.kotlin

import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.simplemobiletools.camera.R
import com.simplemobiletools.commons.extensions.isGone
import kotlinx.android.synthetic.main.activity_main.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CameraFilterUnitTest: KotlinRobolectric() {

    @Test
    fun testButtonsOnCreate(){
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
}
