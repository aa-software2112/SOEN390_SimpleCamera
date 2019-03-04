package test.kotlin.com.simplemobiletools.camera.activities

import android.view.MotionEvent
import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.Press
import androidx.test.espresso.matcher.ViewMatchers.isDisplayingAtLeast
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import org.hamcrest.Matcher

class ViewActionHelper {

    companion object {
        var holdActionMap: HashMap<View?, MotionEvent> ? = HashMap<View?, MotionEvent>()

        fun holdDown(): ViewAction {
            return HoldAction()
        }

        fun release(): ViewAction {
            return ReleaseAction()
        }
    }

    class HoldAction : ViewAction {
        override fun getConstraints(): Matcher<View> {
            return isDisplayingAtLeast(90)
        }

        override fun perform(uiController: UiController?, view: View?) {
            /** View is already being held */
            if (ViewActionHelper.holdActionMap?.get(view) != null) {
                System.out.println("View is already being held!")
            }

            var precision: FloatArray = FloatArray(2)
            var viewCoordinates: FloatArray = FloatArray(2)

            /** Press the hold button on the view */
            precision = Press.FINGER.describePrecision()
            viewCoordinates = GeneralLocation.CENTER.calculateCoordinates(view)
            holdActionMap?.put(view, MotionEvents.sendDown(uiController, viewCoordinates, precision).down)
        }

        override fun getDescription(): String {
            return "Holding"
        }
    }

    class ReleaseAction : ViewAction {
        override fun getDescription(): String {
            return "Releasing"
        }

        override fun getConstraints(): Matcher<View> {
            return isEnabled()
        }

        override fun perform(uiController: UiController?, view: View?) {
            if (ViewActionHelper.holdActionMap?.get(view) == null) {
                System.out.println("View has not been pressed yet! Cannot release!")
            }

            var viewCoordinates: FloatArray = FloatArray(2)

            /** Release */
            viewCoordinates = GeneralLocation.CENTER.calculateCoordinates(view)
            MotionEvents.sendUp(uiController, ViewActionHelper.holdActionMap?.get(view), viewCoordinates)
            ViewActionHelper.holdActionMap?.remove(view)
        }
    }
}
