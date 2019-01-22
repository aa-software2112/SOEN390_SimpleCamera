package test.kotlin

import android.graphics.Rect

import com.simplemobiletools.camera.models.FocusArea
import com.simplemobiletools.camera.models.MySize
import org.junit.Test
import org.junit.Assert

class KotlinJunitTest {


    //Sample Tests for Travis CI
    @Test
    fun mySize_modelsTest() {
        println("Testing class MySize")
        val mMySize = MySize(16,9)
        Assert.assertEquals(9, mMySize.height)
        Assert.assertEquals(16, mMySize.width)
    }

    @Test
    fun focusArea_modelsTest() {
        println("Testing class FocusArea")
        val mFocusArea = FocusArea(Rect(5,5,5,5),9)
        Assert.assertEquals(9, mFocusArea.weight)
    }
}
