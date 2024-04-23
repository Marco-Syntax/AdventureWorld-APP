package de.syntax.androidabschluss.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL = "https://api.openai.com/v1/"


// Objekt zum Erstellen und Bereitstellen des Retrofit-Clients für die OpenAI-API
object OpAiClient {
    // Konfiguration des OkHttpClient
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES) // Verbindungstimeout
        .readTimeout(60, TimeUnit.SECONDS) // Lese-Timeout
        .writeTimeout(60, TimeUnit.SECONDS) // Schreib-Timeout
        .build()

    // Konfiguration des Retrofit-Clients
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // Basis-URL für die API-Endpunkte
        .client(okHttpClient) // Verwendeter OkHttpClient
        .addConverterFactory(MoshiConverterFactory.create()) // Konverter für JSON-Daten
        .build()

    // Singleton-Instanz des Retrofit-API-Services
    @Volatile
    private var INSTANCE : OpenAiApiService? = null

    // Methode zur Rückgabe der Singleton-Instanz des Retrofit-API-Services
    fun getInstance() : OpenAiApiService {
        synchronized(this){
            // Erzeugt eine neue Instanz, wenn noch keine vorhanden ist
            return INSTANCE ?: retrofit.create(OpenAiApiService::class.java).also {
                INSTANCE = it
            }
        }
    }
}