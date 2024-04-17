package cz.cvut.fit.poberboh.loc_share.network

import cz.cvut.fit.poberboh.loc_share.data.AppPreferences
import cz.cvut.fit.poberboh.loc_share.network.api.RefreshApi
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.osmdroid.library.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {
    companion object {
        private const val HOST = "192.168.0.101"
        private const val PORT = "8080"
        private const val BASE_URL = "http://${HOST}:${PORT}"
    }

    fun <Api> buildApi(api: Class<Api>, appPreferences: AppPreferences): Api {
        val authenticator = AppAuthenticator(buildRefreshApi(), appPreferences)
        val interceptor = AppInterceptor(appPreferences)

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getClient(interceptor, authenticator))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }

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

    private fun buildRefreshApi(): RefreshApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RefreshApi::class.java)
    }
}