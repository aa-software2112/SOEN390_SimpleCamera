package test.kotlin

import org.junit.*
import org.junit.Assert
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
public class KotlinRobolectric {

    @Before
    fun beforeTest()
    {
        System.out.println("In the \"Before method \"")
    }

    @Test
    fun firstTest() {
        Assert.assertTrue(1 == 1)
    }

    @After
    fun AfterTest()
    {
        System.out.println("In the \"After method \"")
    }

}
