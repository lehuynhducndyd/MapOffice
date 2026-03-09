package com.example.mapoffice

import android.app.Application
import com.example.mapoffice.di.initKoin

class MapOfficeApp(): Application()  {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}