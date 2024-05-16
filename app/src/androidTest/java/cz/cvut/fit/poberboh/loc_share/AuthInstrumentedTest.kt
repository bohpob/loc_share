package cz.cvut.fit.poberboh.loc_share

import android.Manifest
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import cz.cvut.fit.poberboh.loc_share.ui.auth.LoginFragment
import cz.cvut.fit.poberboh.loc_share.ui.auth.RegisterFragment
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AuthInstrumentedTest {

    @get:Rule
    var activityRule: ActivityTestRule<AuthActivity> = ActivityTestRule(AuthActivity::class.java)

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        // Grant necessary permissions for the test
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.INTERNET,
        Manifest.permission.READ_PHONE_STATE
    )

    // This test verifies that the application context is correctly initialized
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("cz.cvut.fit.poberboh.loc_share", appContext.packageName)
    }

    // This test checks the layout of AuthActivity, including the display of login and register buttons
    @Test
    fun testAuthActivityLayout() {
        // Check if the AuthActivity container and login/register buttons are displayed
        onView(withId(R.id.container_auth)).check(matches(isDisplayed()))
        onView(withId(R.id.button_login)).check(matches(isDisplayed()))
        onView(withId(R.id.button_register)).check(matches(isDisplayed()))
    }

    // This test checks the layout of the login fragment, including the display of control elements
    @Test
    fun testLoginFragmentLayout() {
        // Replace the fragment with the login fragment and check the display of its elements
        activityRule.activity.runOnUiThread {
            activityRule.activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, LoginFragment()).commit()
        }
        onView(withId(R.id.loginFragment)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_text_password)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_text_username)).check(matches(isDisplayed()))
        onView(withId(R.id.button_login)).check(matches(isDisplayed()))
        onView(withId(R.id.button_register)).check(matches(isDisplayed()))
    }

    // This test checks the layout of the register fragment, including the display of control elements
    @Test
    fun testRegisterFragmentLayout() {
        // Replace the fragment with the register fragment and check the display of its elements
        activityRule.activity.runOnUiThread {
            activityRule.activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, RegisterFragment()).commit()
        }
        onView(withId(R.id.registerFragment)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_text_username)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_text_password)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_repeat_text_password)).check(matches(isDisplayed()))
        onView(withId(R.id.button_register)).check(matches(isDisplayed()))
        onView(withId(R.id.button_login)).check(matches(isDisplayed()))
    }

    // This test checks input fields for proper functionality
    @Test
    fun testInputFields() {
        // Input text into input fields and verify that the text is displayed correctly
        onView(withId(R.id.edit_text_username)).perform(typeText("testUsername"))
        onView(withId(R.id.edit_text_password)).perform(typeText("testPassword"))

        onView(withId(R.id.edit_text_username)).check(matches(withText("testUsername")))
        onView(withId(R.id.edit_text_password)).check(matches(withText("testPassword")))
    }

    // This test checks the login button click event and navigation to the login fragment
    @Test
    fun testLoginButtonClicks() {
        // Perform a click on the login button and verify that the login fragment is displayed
        onView(withId(R.id.button_login)).perform(click())
        onView(withId(R.id.loginFragment)).check(matches(isDisplayed()))
    }

    // This test checks the register button click event and navigation to the register fragment
    @Test
    fun testRegisterButtonClicks() {
        // Perform a click on the register button and verify that the register fragment is displayed
        onView(withId(R.id.button_register)).perform(click())
        onView(withId(R.id.registerFragment)).check(matches(isDisplayed()))
    }

    // This test checks UI interactions in the login fragment
    @Test
    fun testLoginFragmentUIInteractions() {
        // Click on the login button to navigate to the login fragment
        onView(withId(R.id.button_login)).perform(click())

        // Check if the login fragment UI elements are displayed
        onView(withId(R.id.loginFragment)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_text_username)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_text_password)).check(matches(isDisplayed()))
        onView(withId(R.id.button_login)).check(matches(isDisplayed()))
        onView(withId(R.id.button_register)).check(matches(isDisplayed()))

        // Perform some UI interactions such as typing in the username and password fields
        onView(withId(R.id.edit_text_username)).perform(typeText("test_user"))
        onView(withId(R.id.edit_text_password)).perform(typeText("test_password"))

        // Click on the login button
        onView(withId(R.id.button_login)).perform(click())
    }

    // This test checks UI interactions in the register fragment
    @Test
    fun testRegisterFragmentUIInteractions() {
        // Click on the register button to navigate to the register fragment
        onView(withId(R.id.button_register)).perform(click())

        // Check if the register fragment UI elements are displayed
        onView(withId(R.id.registerFragment)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_text_username)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_text_password)).check(matches(isDisplayed()))
        onView(withId(R.id.edit_repeat_text_password)).check(matches(isDisplayed()))
        onView(withId(R.id.button_register)).check(matches(isDisplayed()))
        onView(withId(R.id.button_login)).check(matches(isDisplayed()))

        // Perform some UI interactions such as typing in the username, password, and repeat password fields
        onView(withId(R.id.edit_text_username)).perform(typeText("test_user"))
        onView(withId(R.id.edit_text_password)).perform(typeText("test_password"))
        onView(withId(R.id.edit_repeat_text_password)).perform(typeText("test_password"))

        // Click on the register button
        onView(withId(R.id.button_register)).perform(click())
    }
}