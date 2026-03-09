package com.example.mapoffice.di

import com.example.mapoffice.data.KtorMapsApi
import com.example.mapoffice.data.MapsApi
import com.example.mapoffice.screens.canvas.EditorViewModel
import com.example.mapoffice.screens.group.GroupScreenViewModel
import com.example.mapoffice.screens.main.MainViewModel
import com.example.mapoffice.screens.product.ProductScreenViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val dataModule = module {
    single {
        val json = Json { ignoreUnknownKeys = true }
        HttpClient {
            install(ContentNegotiation) {
                // TODO Fix API so it serves application/json
                json(json, contentType = ContentType.Any)
            }
        }
    }

    single<MapsApi> { KtorMapsApi(get()) }
//    single<MuseumApi> { FakeApi() }
//    single<MuseumStorage> { InMemoryMuseumStorage() }
//    single {
//        MuseumRepository(get(), get()).apply {
//            initialize()
//        }
//    }
}

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::GroupScreenViewModel)
    viewModelOf(::EditorViewModel)
    viewModelOf(::ProductScreenViewModel)
}

fun initKoin() {
    startKoin {
        modules(
            dataModule,
            viewModelModule,
        )
    }
}
