package cz.cvut.fit.poberboh.loc_share.network

import cz.cvut.fit.poberboh.loc_share.BuildConfig
import cz.cvut.fit.poberboh.loc_share.BuildConfig.BASE_URL
import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import cz.cvut.fit.poberboh.loc_share.network.api.RefreshApi
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Class responsible for building Retrofit APIs.
 */
class RemoteDataSource {

    /**
     * Builds and returns an instance of the specified Retrofit API.
     * @param api The API interface class.
     * @param appPreferences The instance of [AppPreferences] used for accessing shared preferences.
     * @return An instance of the specified Retrofit API.
     */
    fun <Api> buildApi(api: Class<Api>, appPreferences: AppPreferences): Api {
        // Create an authenticator and interceptor
        val authenticator = AppAuthenticator(buildRefreshApi(), appPreferences)
        val interceptor = AppInterceptor(appPreferences)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getClient(interceptor, authenticator))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }

    /**
     * Configures and returns an OkHttpClient instance.
     * @param interceptor The interceptor used for adding headers to requests.
     * @param authenticator The authenticator used for refreshing access tokens.
     * @return An OkHttpClient instance.
     */
    private fun getClient(
        interceptor: Interceptor,
        authenticator: Authenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .authenticator(authenticator)
            .apply {
                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    addInterceptor(logging)
                }
            }
            .build()
    }

    /**
     * Builds and returns an instance of the [RefreshApi] interface.
     * @return An instance of the [RefreshApi] interface.
     */
    private fun buildRefreshApi(): RefreshApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RefreshApi::class.java)
    }
}