package com.mancel.yann.whereismycar.api

import com.mancel.yann.whereismycar.models.DirectionsByGoogle
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Yann MANCEL on 15/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.api
 */
interface GoogleMapsService {

    // FIELDS --------------------------------------------------------------------------------------

    companion object {
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/"

        val googleMapsService: GoogleMapsService =
            Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(GoogleMapsService::class.java)
    }

    // METHODS -------------------------------------------------------------------------------------

    /** Gets a Directions request is an HTTP URL in Json format */
    @GET("directions/json?")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("key") key: String
    ) : DirectionsByGoogle
}