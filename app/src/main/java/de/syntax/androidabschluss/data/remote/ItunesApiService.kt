package de.syntax.androidabschluss.data.remote

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import de.syntax.androidabschluss.data.models.AudioBookResponse
import de.syntax.androidabschluss.data.models.EbookResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
// Basis-URL für iTunes API
const val ITUNES_BASE_URL = "https://itunes.apple.com/"

// HTTP-Logging-Interceptor zur Protokollierung von Netzwerkanforderungen und -antworten
private val logger: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

// OkHttpClient für iTunes API-Anfragen mit dem HTTP-Logging-Interceptor
private val httpClientItunes: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(logger)
    .build()

// Moshi-Objekt zum Konvertieren von JSON-Daten
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Retrofit-Objekt für die iTunes API-Anfragen
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(ITUNES_BASE_URL)
    .client(httpClientItunes)
    .build()

// Service-Schnittstelle für iTunes API-Anfragen
interface ItunesApiService {

    // Methode zum Abrufen aller E-Books
    @GET("search?")
    suspend fun getAllBooks(
        @Query("term") term: String = "kind",
        @Query("country") country: String = "DE",
        @Query("media") media: String = "ebook",
        @Query("entity") entity: String = "ebook",
        @Query("limit") limit: Int = 100
    ): Response<EbookResponse>

    // Methode zum Abrufen aller Hörbücher
    @GET("search?")
    suspend fun getAllAudioBooks(
        @Query("term") term: String = "kind",
        @Query("country") country: String = "DE",
        @Query("media") media: String = "audiobook",
        @Query("entity") entity: String = "audiobook",
        @Query("limit") limit: Int = 100
    ): Response<AudioBookResponse>
}

// Objekt zur Bereitstellung des Retrofit-API-Services für die iTunes API
object ItunesApi { val retrofitService: ItunesApiService by lazy { retrofit.create(ItunesApiService::class.java) }
}