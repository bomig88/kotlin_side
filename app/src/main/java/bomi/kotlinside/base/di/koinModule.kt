package bomi.kotlinside.base.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.Keep
import bomi.kotlinside.BuildConfig
import bomi.kotlinside.MainApplication
import bomi.kotlinside.R
import bomi.kotlinside.base.api.ApiDataModel
import bomi.kotlinside.base.api.ApiDataModelImpl
import bomi.kotlinside.base.api.ApiService
import bomi.kotlinside.base.exception.NoConnectivityException
import bomi.kotlinside.base.exception.NullOnEmptyConverterFactory
import bomi.kotlinside.ui.MainViewModel
import bomi.kotlinside.ui.home.HomeTutorialViewModel
import bomi.kotlinside.ui.home.HomeViewModel
import bomi.kotlinside.ui.home.PopupViewModel
import bomi.kotlinside.ui.intro.IntroViewModel
import bomi.kotlinside.util.PreferenceUtil
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

///////// Network /////////////
@Keep
private class ConnectivityInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (isNetOffline()) {
            throw NoConnectivityException()
        }

        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

    /**
     * Network State Checking
     *
     * @return true = Network On
     */
    @Suppress("DEPRECATION")
    private fun isNetOffline(): Boolean {
        val connectivityManager = MainApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return true
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return true
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> false
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> false
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> false
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> false
                else -> true
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return true
            return !nwInfo.isConnected
        }
    }
}

@Keep
private fun provideOkHttpClient(): OkHttpClient {
    val headerInterceptor = Interceptor { chain ->
        val builder = chain.request().newBuilder()
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
        chain.proceed(builder.build())
    }

    val okHttpClientBuilder = OkHttpClient.Builder()
    okHttpClientBuilder.connectTimeout(5, TimeUnit.MINUTES)
    okHttpClientBuilder.readTimeout(3, TimeUnit.MINUTES)
    okHttpClientBuilder.writeTimeout(3, TimeUnit.MINUTES)
    okHttpClientBuilder.callTimeout(3, TimeUnit.MINUTES)
    okHttpClientBuilder.addInterceptor(headerInterceptor) //Header
    okHttpClientBuilder.addInterceptor(ConnectivityInterceptor()) //Network Connect
    okHttpClientBuilder.addInterceptor(
        HttpLoggingInterceptor().setLevel( //Logger
            if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        )
    )
    return okHttpClientBuilder.build()
}

var retrofitMainPart = module {
    single<ApiService> {
        Retrofit.Builder()
            .baseUrl(androidContext().getString(R.string.api_server))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(
                GsonBuilder()
                    .serializeNulls()
                    .create())
            )
            .client(provideOkHttpClient())
            .build()
            .create(ApiService::class.java)
    }
}

var preferencePart = module {
    single {
        PreferenceUtil(androidContext())
    }
}

var modelMainPart = module {
    factory<ApiDataModel> {
        ApiDataModelImpl(get())
    }
}

///////////////////View Model/////////////////////////
var vmMainPart = module {
    viewModel {
        MainViewModel()
    }
}

var vmIntroPart = module {
    viewModel {
        IntroViewModel(get())
    }
}

var vmHomeTutorialPart = module {
    viewModel {
        HomeTutorialViewModel()
    }
}

var vmHomePart = module {
    viewModel {
        HomeViewModel(get())
    }
}

var vmPopupPart = module {
    viewModel {
        PopupViewModel()
    }
}

var mainModule = listOf(
    //Network
    retrofitMainPart,
    preferencePart,
    modelMainPart,

    vmMainPart,
    vmIntroPart,
    vmHomeTutorialPart,
    vmHomePart,
    vmPopupPart
)