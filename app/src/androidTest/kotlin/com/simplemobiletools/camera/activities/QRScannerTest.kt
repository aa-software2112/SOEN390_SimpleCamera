package com.simplemobiletools.camera.activities

import android.graphics.BitmapFactory
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import androidx.test.runner.AndroidJUnit4
import com.simplemobiletools.camera.R
import com.simplemobiletools.camera.implementations.QRScanner
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import test.kotlin.com.simplemobiletools.camera.activities.ViewActionHelper

@LargeTest
@RunWith(AndroidJUnit4::class)
class QRScannerTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.CAMERA",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )

    @Test
    fun successfulScan() {
        /**Get the QR Scanner Instance*/
        val qr = QRScanner.getInstance()

        /**Create a Bitmap from a Fake QR Code Stored in Drawable*/
        val fakeQRCode = BitmapFactory.decodeResource(qr.context.resources, R.drawable.puppy)

        /**Add it to the QR Queue*/
        qr.addQrPhoto(fakeQRCode)

        /**Perform a QR Scan*/
        onView(withId(R.id.swipe_area)).perform(ViewActionHelper.holdDown())
        Thread.sleep(1000)

        /**Verify that the Progress Bar Appears*/
        onView(Matchers.anyOf(withId(R.id.qrProgressBar))).check(ViewAssertions.matches(isDisplayed()))
        Thread.sleep(2000)

        onView(withText("QR Navigation")).check(matches(isDisplayed()))
    }

    @Test
    fun unsuccesfulScan() {
        /**Perform a QR Scan*/
        onView(withId(R.id.swipe_area)).perform(ViewActionHelper.holdDown())
        Thread.sleep(1000)

        /**Verify that the Progress Bar Appears*/
        onView(Matchers.anyOf(withId(R.id.qrProgressBar))).check(ViewAssertions.matches(isDisplayed()))
        Thread.sleep(2000)

        onView(withText("QR Navigation")).check(doesNotExist())

        onView(withId(R.id.swipe_area)).perform(ViewActionHelper.release())
    }

}
