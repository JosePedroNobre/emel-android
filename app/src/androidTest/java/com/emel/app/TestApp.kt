package com.emel.app

import android.app.Application
/**
 * We use a separate App for tests to prevent initializing dependency injection.
 * @context This androidTest package is for unit tests that involve android instrumentation and uses the android framework
 *
 */
class TestApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}