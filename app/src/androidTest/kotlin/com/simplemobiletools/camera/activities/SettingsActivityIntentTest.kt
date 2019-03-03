package test.kotlin.com.simplemobiletools.camera.activities

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.runner.AndroidJUnit4
import com.simplemobiletools.camera.activities.BaseUITestSetup
import com.simplemobiletools.camera.activities.SettingsActivity
import com.simplemobiletools.camera.R
import com.simplemobiletools.camera.activities.TestActivities

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityIntentTest : BaseUITestSetup(TestActivities.MAIN_ACTIVITY) {

    @Before
    fun setUp() {
    }
    /** This verifies that the application switches activity upon pressing the settings buton */
    @Test
    fun testSettingsButton() {

        /** Initalizes the intents listener - all buttons that lead to the loading of new activities
         * will be logged in this library after .init() is called
         */
        Intents.init()

        /** Sleep for 2 seconds in order to let the settings button fade-out */
        Thread.sleep(7000)

        /** The settings button is pressed  - must click twice; once only opens the submenu, the other
         * switches to the Setting activity */
        Espresso.onView(withId(R.id.settings)).perform(ViewActions.click())

        Thread.sleep(2000)
        Espresso.onView(withId(R.id.settings)).perform(ViewActions.click())

        /** Check if the settings activity intent was detected*/
        Intents.intended(IntentMatchers.hasComponent(SettingsActivity::class.java.getName()))

        /** Close the intent library */
        Intents.release()
    }

    @After
    fun tearDown() {
    }
}
