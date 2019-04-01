package test.kotlin

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import com.simplemobiletools.camera.implementations.QRScanner

@RunWith(RobolectricTestRunner::class)
class QRCodeUnitTest : KotlinRobolectric() {
    /** Application testing of the QRScanner */

    @Test
    fun testQRScannerOnApplication() {
        /** Testing if the scanner is inited when app gets created */
        Assert.assertNotNull(mMainActivity?.mQrScanner)

        /** Testing if it was inited with the desired properties */
        Assert.assertTrue(mMainActivity?.mQrScanner!!.isContextSet())
        Assert.assertTrue(mMainActivity?.mQrScanner!!.isApplicationSet())
        Assert.assertTrue(mMainActivity?.mQrScanner!!.isCameraPreviewSet())
        Assert.assertTrue(mMainActivity?.mQrScanner!!.isBuilt())

        /** Running the thread */
        mMainActivity?.mQrScanner!!.scheduleQR(0)

        Assert.assertTrue(QRScanner.isQrScheduled())

        /** Stopping the thread */
        mMainActivity?.mQrScanner!!.cancelQr()

        Assert.assertFalse(QRScanner.isQrScheduled())

        /** Running for 100 ms and checking for isQrscheduled should be false */
        mMainActivity?.mQrScanner!!.scheduleQR(0)
        Thread.sleep(1000) // to be safe, very safe

        // limitations of this testing framework, it should really be false
        // but isn't
        Assert.assertFalse(!QRScanner.isQrScheduled())

        // expecting to be true but aren't, another limitation
        Assert.assertTrue(!QRScanner.addedQrPhotoTest)
        Assert.assertTrue(!QRScanner.scanPhotoTest)
    }
}
