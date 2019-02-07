package test.kotlin

import android.graphics.Matrix
import android.graphics.Rect
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.simplemobiletools.camera.models.FocusArea
import com.simplemobiletools.camera.models.MySize
import org.junit.Test
import org.junit.Assert

class KotlinJunitTest {

    // Sample Tests for Travis CI
    @Test
    fun mySize_modelsTest() {
        println("Testing class MySize")
        val mMySize = MySize(16, 9)
        Assert.assertEquals(9, mMySize.height)
        Assert.assertEquals(16, mMySize.width)
    }

    @Test
    fun focusArea_modelsTest() {
        println("Testing class FocusArea")
        val mFocusArea = FocusArea(Rect(5, 5, 5, 5), 9)
        Assert.assertEquals(9, mFocusArea.weight)
    }

    @Test
    fun mock_test1() {
        println("Testing Mockito 1")
        val mockedList: MutableList<Int> = mock()
        mockedList.add(1)
        mockedList.clear()

        verify(mockedList).add(1)
        verify(mockedList).clear()
    }

    @Test
    fun mock_test2() {
        println("Testing Mockito 2")
        val mockedMatrix: Matrix = mock()
        mockedMatrix.setRotate(60.toFloat())
        verify(mockedMatrix).setRotate(60.toFloat())
    }

    @Test
    fun mySize_ratio_modelsTest() {
        println("Testing method MySize.ratio")
        val mMySize = MySize(10, 5)
        Assert.assertEquals(2.toFloat(), mMySize.ratio)
    }

    @Test
    fun mySize_isSixteenToNine_modelsTest() {
        println("Testing method MySize.isSixteenToNine")
        val mMySize = MySize(16, 9)
        Assert.assertTrue(mMySize.isSixteenToNine())

        @Test
        fun firstTest() {
            Assert.assertTrue(1 == 1)
        }

        @Test
        fun secondTest() {
            Assert.assertTrue(1 == 1)
        }
    }
}
