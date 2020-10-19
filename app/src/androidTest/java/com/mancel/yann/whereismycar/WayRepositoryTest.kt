package com.mancel.yann.whereismycar

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mancel.yann.whereismycar.models.Location
import com.mancel.yann.whereismycar.repositories.GoogleWayRepository
import com.mancel.yann.whereismycar.repositories.WayRepository
import com.mancel.yann.whereismycar.states.WayState
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Yann MANCEL on 19/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar
 *
 * An android test on [WayRepository].
 */
@RunWith(AndroidJUnit4::class)
class WayRepositoryTest {

    // FIELDS --------------------------------------------------------------------------------------

    private val _repository: WayRepository = GoogleWayRepository()

    private val _key =
        ApplicationProvider.getApplicationContext<Context>()
            .resources.getString(R.string.google_maps_key)

    // METHODS -------------------------------------------------------------------------------------

    @Test
    fun getWay_shouldBeSuccess() = runBlocking {
        // BEFORE: Retrieve data
        val deferredData = async {
            this@WayRepositoryTest._repository.getWay(
                origin = "45.9922107,4.7198888",
                destination = "45.9935308,4.7187473",
                mode = "driving",
                key = this@WayRepositoryTest._key
            )
        }
        val data = deferredData.await() as WayState.Success

        // TEST
        assertEquals(3, data._way.size)
        assertEquals(Location(45.9921573, 4.7198871), data._way[0])
        assertEquals(Location(45.9921982, 4.7188211), data._way[1])
        assertEquals(Location(45.9935126, 4.7189461), data._way[2])
    }
}

/*
    JSON RESULT:

    {
        "geocoded_waypoints": [
            {
                "geocoder_status": "OK",
                "place_id": "ChIJI8Gi1COF9EcRVWgiizHLlmE",
                "types": [
                    "street_address"
                ]
            },
            {
                "geocoder_status": "OK",
                "place_id": "ChIJQfoplSGF9EcRQheBXqCsMzo",
                "types": [
                    "street_address"
                ]
            }
        ],
        "routes": [
            {
                "bounds": {
                    "northeast": {
                        "lat": 45.9935126,
                        "lng": 4.7198871
                    },
                    "southwest": {
                        "lat": 45.9921573,
                        "lng": 4.7188211
                    }
                },
                "copyrights": "Map data ©2020",
                "legs": [
                    {
                        "distance": {
                            "text": "0.2 km",
                            "value": 230
                        },
                        "duration": {
                            "text": "1 min",
                            "value": 76
                        },
                        "end_address": "198 Rue nationale, 69400 Villefranche-sur-Saône, France",
                        "end_location": {
                            "lat": 45.9935126,
                            "lng": 4.7189461
                        },
                        "start_address": "69 Rue Alsace Lorraine, 69400 Villefranche-sur-Saône, France",
                        "start_location": {
                            "lat": 45.9921573,
                            "lng": 4.7198871
                        },
                        "steps": [
                            {
                                "distance": {
                                    "text": "83 m",
                                    "value": 83
                                },
                                "duration": {
                                    "text": "1 min",
                                    "value": 24
                                },
                                "end_location": {
                                    "lat": 45.9921982,
                                    "lng": 4.7188211
                                },
                                "html_instructions": "Head <b>west</b> on <b>Rue Alsace Lorraine</b> toward <b>Rue Etienne Poulet</b>",
                                "polyline": {
                                    "points": "_zuwGizx[?FAXAPC`D"
                                },
                                "start_location": {
                                    "lat": 45.9921573,
                                    "lng": 4.7198871
                                },
                                "travel_mode": "DRIVING"
                            },
                            {
                                "distance": {
                                    "text": "0.1 km",
                                    "value": 147
                                },
                                "duration": {
                                    "text": "1 min",
                                    "value": 52
                                },
                                "end_location": {
                                    "lat": 45.9935126,
                                    "lng": 4.7189461
                                },
                                "html_instructions": "Turn <b>right</b> onto <b>Rue nationale</b>/<wbr/><b>D686</b><div style=\"font-size:0.9em\">Destination will be on the left</div>",
                                "maneuver": "turn-right",
                                "polyline": {
                                    "points": "gzuwGssx[{BGKAuAGYCMC"
                                },
                                "start_location": {
                                    "lat": 45.9921982,
                                    "lng": 4.7188211
                                },
                                "travel_mode": "DRIVING"
                            }
                        ],
                        "traffic_speed_entry": [],
                        "via_waypoint": []
                    }
                ],
                "overview_polyline": {
                    "points": "_zuwGizx[GtEgCIoBKMC"
                },
                "summary": "Rue Alsace Lorraine and Rue nationale/D686",
                "warnings": [],
                "waypoint_order": []
            }
        ],
        "status": "OK"
    }
 */