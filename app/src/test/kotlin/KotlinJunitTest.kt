package test.kotlin

import org.junit.Test
import org.junit.Assert

class KotlinJunitTest {

    @Test
    fun firstTest() {
        Assert.assertTrue(1 == 1)
    }

    @Test
    fun secondTest() {
        Assert.assertTrue(1 == 1)
    }
}
