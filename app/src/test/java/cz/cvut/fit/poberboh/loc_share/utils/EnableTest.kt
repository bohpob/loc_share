package cz.cvut.fit.poberboh.loc_share.utils

import android.view.View
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class EnableTest {

    @Test
    fun enableShouldEnableTheViewAndSetAlphaTo1WhenEnabledIsTrue() {
        // Arrange
        val view = Mockito.mock(View::class.java)

        // Act
        view.enable(true)

        // Assert
        Mockito.verify(view).isEnabled = true
        Mockito.verify(view).alpha = 1f
    }

    @Test
    fun enableShouldDisableTheViewAndSetAlphaTo0_5WhenEnabledIsFalse() {
        // Arrange
        val view = Mockito.mock(View::class.java)

        // Act
        view.enable(false)

        // Assert
        Mockito.verify(view).isEnabled = false
        Mockito.verify(view).alpha = 0.5f
    }
}
