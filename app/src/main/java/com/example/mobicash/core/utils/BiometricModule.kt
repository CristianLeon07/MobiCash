package com.example.mobicash.core.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity
import com.example.mobicash.data.biometric.BiometricAuthenticator
import com.example.mobicash.data.biometric.BiometricAuthenticatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFragmentActivity(@ApplicationContext context: Context): FragmentActivity? {

        return context.findActivity() as? FragmentActivity
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}