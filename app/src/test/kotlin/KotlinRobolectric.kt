package test.kotlin

import android.Manifest
import com.simplemobiletools.camera.R
import android.os.Environment
import android.view.View
import android.widget.ImageView
import com.simplemobiletools.camera.activities.MainActivity
import org.junit.* // ktlint-disable no-wildcard-imports
import org.junit.Assert
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowEnvironment
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.junit.Rule
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowApplication

/** This class should be renamed and inherited by future tests classes, so that the application
 * will always be initialized correctly before testing. This is only necessary for application-level
 * testing; basic unit testing can be done through ordinary and singular JUNIT tests
 */
@RunWith(RobolectricTestRunner::class)
open class KotlinRobolectric {

    /** Create a class variable for storing an activity (a layout from the application) */
    open var mMainActivity: MainActivity? = null /** MainActivity is a class created by the developer */

    /** A shadow application must be made in order to toggle the camera AND write persmission */
    open var application: ShadowApplication? = null

    /** It is CRUCIAL to have this line of code to set the environment to emulated mode - otherwise
     * the application will assume the environment is as in the real implementation, and fail to run
     */
    @get:Rule
    public final var env = EnvironmentVariables()
            .set("EMULATED_STORAGE_TARGET", "SOME_VALUE")

    @Before
    open fun beforeTest() {

        /** Set the environment to correspond to the testing environment */
        ShadowEnvironment.setExternalStorageState(Environment.MEDIA_MOUNTED)

        /** Displays the environment variable necessary for emulated storage (to run
         * the application using Robolectric w/o crashing
         */
        System.out.println(System.getenv("EMULATED_STORAGE_TARGET"))

        /** Displays the current external storage directory */
        System.out.println(Environment.getExternalStorageDirectory())

        /**  Gets activity controller attached to the MainActivity */
        var activityController = Robolectric.buildActivity(MainActivity::class.java)

        /** Extracts the activity */
        mMainActivity = activityController.get()

        /** Creates a shadow of the application so that permissions can be tacked-onto it */
        application = Shadows.shadowOf(mMainActivity?.getApplication())

        /** Grants the CAMERA and WRITE_EXTERNAL_STORAGE permissions */
        application?.grantPermissions(Manifest.permission.CAMERA)
        application?.grantPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        /** Create, resume, and make visible the MainActivity */
        mMainActivity = activityController.create().resume().visible().get()
    }

    @Test
    fun firstTest() {

        /** Grab the shutter image displayed when the application MainActivity is opened */
        var shutterImage = mMainActivity?.findViewById<ImageView>(R.id.shutter)

        /** Check that the image was actually obtained */
        Assert.assertNotNull(shutterImage)

        /** Assert that it is visible */
        Assert.assertTrue(shutterImage?.visibility == View.VISIBLE)
    }

    @After
    fun afterTest() {
    }
}
