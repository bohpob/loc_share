package cz.cvut.fit.poberboh.loc_share.network

import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor class for adding the Authorization header to API requests.
 * @param appPreferences The instance of [AppPreferences] used for accessing shared preferences.
 */
class AppInterceptor(private val appPreferences: AppPreferences) : Interceptor {
    /**
     * Intercepts the request and adds the Authorization header with the access token.
     * @param chain The chain of interceptors.
     * @return The response from the server.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        // Get the access token from the shared preferences
        val token = runBlocking {
            appPreferences.accessToken.first()
        }
        // Add the Authorization header to the request
        val request = chain.request().newBuilder()
        request.addHeader("Authorization", "Bearer $token")
        request.addHeader("Content-Type", "application/json")
        return chain.proceed(request.build())
    }
}