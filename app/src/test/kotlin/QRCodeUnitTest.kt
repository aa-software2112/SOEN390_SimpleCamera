package test.kotlin

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QRCodeUnitTest : KotlinRobolectric() {
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
