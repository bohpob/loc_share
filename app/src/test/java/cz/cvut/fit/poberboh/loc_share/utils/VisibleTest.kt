package cz.cvut.fit.poberboh.loc_share.utils

import android.view.View
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class VisibleTest {
    @Test
    fun visibleShouldSetVisibilityToVisibleWhenIsVisibleIsTrue() {
        // Arrange
        val view = Mockito.mock(View::class.java)

        // Act
        view.visible(true)

        // Assert
        Mockito.verify(view).visibility = View.VISIBLE
    }

    @Test
    fun visibleShouldSetVisibilityToGoneWhenIsVisibleIsFalse() {
        // Arrange
        val view = Mockito.mock(View::class.java)

        // Act
        view.visible(false)

        // Assert
        Mockito.verify(view).visibility = View.GONE
    }
}