package test.kotlin

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
class QRCodeUnitTest : KotlinRobolectric() {


    @Test
    fun testQRScannerInited() {
        Assert.assertNotNull(mMainActivity?.mQrScanner)
    }

    // Once the MainActivity has been launched, the singleton should have had the methods "setContext",
    // "setApplication", "build" and "setCameraPreview" called (you can do .verify on these).
    @Test
    fun testQRmethodsCalled() {

    }
    //Once you know QRScanner.qr_requested is true, wait for it to turn false; you should check that addQrPhoto//(...) was called at this point, along with scanPhotos().
    @Test
    fun testAddQrPhoto_Called() {
    }

    //Get the singleton instance of QRScanner and call ".isAlive()" and make sure it is NOT alive.
    @Test
    fun testQrScannerAlive_Null() {

    }

    //Check that the bitmap queue is empty at the end of all these tests. (.bitmapsLeftToScan())
    @Test
    fun bitmapsLeftToScan_Empty() {

    }
}
