package com.langlearn.app

import android.app.Application
import com.langlearn.app.data.database.AppDatabase
import com.langlearn.app.data.seed.SeedDataLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LangLearnApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        instance = this

        applicationScope.launch {
            SeedDataLoader().loadSeedData(this@LangLearnApp, database)
        }
    }

    companion object {
        lateinit var instance: LangLearnApp
            private set
    }
}
