package test.kotlin

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
    fun mock_test() {
        println("Testing Mockito")
        val mockedList: MutableList<Int> = mock()
        mockedList.add(1)
        mockedList.clear()

        verify(mockedList).add(1)
        verify(mockedList).clear()
    }
}
