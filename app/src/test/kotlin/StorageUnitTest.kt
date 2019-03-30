package test.kotlin

import android.Manifest
import android.os.Environment
import android.os.StatFs
import android.widget.TextView
import com.simplemobiletools.camera.R
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.simplemobiletools.camera.extensions.config
import com.simplemobiletools.camera.helpers.DeviceStorageUtil
import com.simplemobiletools.commons.extensions.isVisible
import com.simplemobiletools.commons.extensions.value
import org.junit.* // ktlint-disable no-wildcard-imports
import org.robolectric.shadows.ShadowStatFs


@RunWith(RobolectricTestRunner::class)
class StorageUnitTest : KotlinRobolectric() {

    @Test
    fun testBytesToHuman() {
        Assert.assertEquals("1.18 Mb", DeviceStorageUtil.bytesToHuman(1233234))
    }

    @Test
    fun testZeroSpace() {

        //Toggle the space remaining
        mMainActivity?.config?.spaceRemainingOn = true
        Assert.assertEquals(true, mMainActivity?.config?.spaceRemainingOn)

        mMainActivity?.setRecordingState(true)
        Assert.assertEquals(true, mMainActivity?.findViewById<TextView>(R.id.space_remaining)?.isVisible())
        Assert.assertEquals("0 byte left", mMainActivity?.findViewById<TextView>(R.id.space_remaining)?.value)
    }

    @Test
    fun testFreeSpace() {
        //Toggle the space remaining
        mMainActivity?.config?.spaceRemainingOn = true
        Assert.assertEquals(true, mMainActivity?.config?.spaceRemainingOn)

        ShadowStatFs.registerStats("/tmp", 100, 1230, 1120)
        val statsFs = StatFs("/tmp")

        mMainActivity?.setRecordingState(true)
        Assert.assertEquals(true, mMainActivity?.findViewById<TextView>(R.id.space_remaining)?.isVisible())
        Assert.assertEquals("4.38 Mb left", mMainActivity?.findViewById<TextView>(R.id.space_remaining)?.value)
    }
}
