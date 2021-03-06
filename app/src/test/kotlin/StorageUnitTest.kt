package test.kotlin

import android.os.Environment
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
        Assert.assertEquals("9.54 Mb", DeviceStorageUtil.bytesToHuman(9999999))
        Assert.assertEquals("953.67 Mb", DeviceStorageUtil.bytesToHuman(999999999))
        Assert.assertEquals("9.31 Gb", DeviceStorageUtil.bytesToHuman(9999999999))
    }

    @Test
    fun testZeroSpace() {

        // Toggle the space remaining
        mMainActivity?.config?.spaceRemainingOn = true
        Assert.assertEquals(true, mMainActivity?.config?.spaceRemainingOn)

        mMainActivity?.setRecordingState(true)
        Assert.assertEquals(true, mMainActivity?.findViewById<TextView>(R.id.space_remaining)?.isVisible())
        Assert.assertEquals("0 byte left", mMainActivity?.findViewById<TextView>(R.id.space_remaining)?.value)
    }

    @Test
    fun testFreeSpace() {
        // Toggle the space remaining
        mMainActivity?.config?.spaceRemainingOn = true
        Assert.assertEquals(true, mMainActivity?.config?.spaceRemainingOn)

        // Fake Storage Capacity
        var path = Environment.getExternalStorageDirectory().absolutePath + ""
        ShadowStatFs.registerStats(path, 100, 1230, 1120)

        mMainActivity?.setRecordingState(true)
        Assert.assertEquals(true, mMainActivity?.findViewById<TextView>(R.id.space_remaining)?.isVisible())
        Assert.assertEquals("4.38 Mb left", mMainActivity?.findViewById<TextView>(R.id.space_remaining)?.value)

        // Stop Recording
        mMainActivity?.setRecordingState(false)

        // Fake Storage Capacity
        ShadowStatFs.registerStats(path, 100, 1120, 12220)

        mMainActivity?.setRecordingState(true)
        Assert.assertEquals(true, mMainActivity?.findViewById<TextView>(R.id.space_remaining)?.isVisible())
        Assert.assertEquals("47.73 Mb left", mMainActivity?.findViewById<TextView>(R.id.space_remaining)?.value)
    }
}
