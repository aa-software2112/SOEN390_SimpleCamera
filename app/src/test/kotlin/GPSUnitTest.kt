package test.kotlin

import com.simplemobiletools.camera.R
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GPSUnitTest : KotlinRobolectric() {

    /**
     *   MAIN ACTIVITY UNIT TESTS
     * */

    @Test
    fun testmLastLocation_Null() {
        /* Testing if mLastLocation is initialized as null */
        Assert.assertEquals(null, mMainActivity?.mLastLocation)
    }

    @Test
    fun testAddressFirstLine_Null() {
        /* Testing if addressFirstLine is initialized as null */
        Assert.assertEquals("", mMainActivity?.addressFirstLine)
    }

    @Test
    fun testAddressSecondLine_Null() {
        /* Testing if addressSecondLine is initialized as null */
        Assert.assertEquals("", mMainActivity?.addressSecondLine)
    }

    @Test
    fun testAddressCoordinates_Null() {
        /* Testing if addressCoordinates is initialized as null */
        Assert.assertEquals("", mMainActivity?.addressCoordinates)
    }

    @Test
    fun testStampGPS() {
        /*
        * Testing if stampGPS method is called, then mLastLocation, addressFirstLine, addressSecondLine & addressCoordinates is NOT null
        */
        Assert.assertEquals("", mMainActivity?.addressFirstLine)
        Assert.assertEquals("", mMainActivity?.addressSecondLine)
        Assert.assertEquals("", mMainActivity?.addressCoordinates)

        mMainActivity?.addressFirstLine = "1234 Maisonneuve"
        mMainActivity?.addressSecondLine = "Montreal, QC"
        mMainActivity?.addressCoordinates = "43.00, 75.00"

        mMainActivity?.stampGPS()

        Assert.assertNotEquals("", mMainActivity?.addressFirstLine)
        Assert.assertNotEquals("", mMainActivity?.addressSecondLine)
        Assert.assertNotEquals("", mMainActivity?.addressCoordinates)
    }

}
