package com.mancel.yann.whereismycar.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by Yann MANCEL on 18/10/2020.
 * Name of the project: WhereIsMyCar
 * Name of the package: com.mancel.yann.whereismycar.models
 */

/*
    POJO STRUCTURE:

    DirectionsByGoogle
    |
    +--- geocoded_waypoints: List<GeocodedWaypoint>
    |    |
    |    +--- GeocodedWaypoint
    |         |
    |         +--- geocoder_status: String
    |         |
    |         +--- place_id: String
    |         |
    |         +--- types: List<String>
    |
    +--- routes: List<Route>
    |    |
    |    +--- Route
    |         |
    |         +--- bounds: Bounds
    |         |    |
    |         |    +--- northeast: Northeast
    |         |    |    |
    |         |    |    +--- lat: Double
    |         |    |    |
    |         |    |    +--- lng: Double
    |         |    |
    |         |    +--- southwest: Southwest
    |         |         |
    |         |         +--- lat: Double
    |         |         |
    |         |         +--- lng: Double
    |         |
    |         +--- copyrights: String
    |         |
    |         +--- legs: List<Leg>
    |         |    |
    |         |    +--- Leg
    |         |         |
    |         |         +--- distance: Distance
    |         |         |    |
    |         |         |    +--- text: String
    |         |         |    |
    |         |         |    +--- value: Int
    |         |         |
    |         |         +--- duration: Duration
    |         |         |    |
    |         |         |    +--- text: String
    |         |         |    |
    |         |         |    +--- value: Int
    |         |         |
    |         |         +--- end_address: String
    |         |         |
    |         |         +--- end_location: EndLocation
    |         |         |    |
    |         |         |    +--- lat: Double
    |         |         |    |
    |         |         |    +--- lng: Double
    |         |         |
    |         |         +--- start_address: String
    |         |         |
    |         |         +--- start_location: StartLocation
    |         |         |    |
    |         |         |    +--- lat: Double
    |         |         |    |
    |         |         |    +--- lng: Double
    |         |         |
    |         |         +--- steps: List<Step>
    |         |         |    |
    |         |         |    +--- Step
    |         |         |         |
    |         |         |         +--- distance: Distance
    |         |         |         |    |
    |         |         |         |    +--- text: String
    |         |         |         |    |
    |         |         |         |    +--- value: Int
    |         |         |         |
    |         |         |         +--- duration: Duration
    |         |         |         |    |
    |         |         |         |    +--- text: String
    |         |         |         |    |
    |         |         |         |    +--- value: Int
    |         |         |         |
    |         |         |         +--- end_location: EndLocation
    |         |         |         |    |
    |         |         |         |    +--- lat: Double
    |         |         |         |    |
    |         |         |         |    +--- lng: Double
    |         |         |         |
    |         |         |         +--- html_instructions: String
    |         |         |         |
    |         |         |         +--- polyline: Polyline
    |         |         |         |    |
    |         |         |         |    +--- points: String
    |         |         |         |
    |         |         |         +--- start_location: StartLocation
    |         |         |         |    |
    |         |         |         |    +--- lat: Double
    |         |         |         |    |
    |         |         |         |    +--- lng: Double
    |         |         |         |
    |         |         |         +--- travel_mode: String
    |         |         |         |
    |         |         |         +--- maneuver: String
    |         |         |
    |         |         +--- traffic_speed_entry: List<Any>
    |         |         |
    |         |         +--- via_waypoint: List<Any>
    |         |
    |         +--- overview_polyline: OverviewPolyline
    |         |    |
    |         |    +--- points: String
    |         |
    |         +--- summary: String
    |         |
    |         +--- warnings: List<String>
    |         |
    |         +--- waypoint_order: List<Any>
    |
    +--- status: String
*/

@JsonClass(generateAdapter = true)
data class DirectionsByGoogle(
    @Json(name = "geocoded_waypoints") var geocodedWaypoints: List<GeocodedWaypoint>? = null,
    @Json(name = "routes") var routes: List<Route>?  = null,
    @Json(name = "status") var status: String? = null
)

@JsonClass(generateAdapter = true)
data class GeocodedWaypoint(
    @Json(name = "geocoder_status") var geocoderStatus:String? = null,
    @Json(name = "place_id") var placeId:String? = null,
    @Json(name = "types") var types: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class Route(
    @Json(name = "bounds") var bounds: Bounds? = null,
    @Json(name = "copyrights") var copyrights:String? = null,
    @Json(name = "legs") var legs: List<Leg>? = null,
    @Json(name = "overview_polyline") var overviewPolyline: OverviewPolyline? = null,
    @Json(name = "summary") var summary:String? = null,
    @Json(name = "warnings") var warnings: List<String>? = null,
    @Json(name = "waypoint_order") var waypointOrder: List<Any>? = null
)

@JsonClass(generateAdapter = true)
data class Bounds(
    @Json(name = "northeast") var northeast: Northeast? = null,
    @Json(name = "southwest") var southwest: Southwest? = null
)

@JsonClass(generateAdapter = true)
data class Northeast(
    @Json(name = "lat") var lat: Double? = null,
    @Json(name = "lng") var lng: Double? = null
)

@JsonClass(generateAdapter = true)
data class Southwest(
    @Json(name = "lat") var lat: Double? = null,
    @Json(name = "lng") var lng: Double? = null
)

@JsonClass(generateAdapter = true)
data class Leg(
    @Json(name = "distance") var distance: Distance? = null,
    @Json(name = "duration") var duration: Duration? = null,
    @Json(name = "end_address") var endAddress: String? = null,
    @Json(name = "end_location") var endLocation: EndLocation? = null,
    @Json(name = "start_address") var startAddress: String? = null,
    @Json(name = "start_location") var startLocation: StartLocation? = null,
    @Json(name = "steps") var steps: List<Step>? = null,
    @Json(name = "traffic_speed_entry") var trafficSpeedEntry: List<Any>? = null,
    @Json(name = "via_waypoint") var viaWaypoint: List<Any>? = null
)

@JsonClass(generateAdapter = true)
data class Distance(
    @Json(name = "text") var text: String? = null,
    @Json(name = "value") var value: Int? = null
)

@JsonClass(generateAdapter = true)
data class Duration(
    @Json(name = "text") var text: String? = null,
    @Json(name = "value") var value: Int? = null
)

@JsonClass(generateAdapter = true)
data class EndLocation(
    @Json(name = "lat") var lat: Double? = null,
    @Json(name = "lng") var lng: Double? = null
)

@JsonClass(generateAdapter = true)
data class StartLocation(
    @Json(name = "lat") var lat: Double? = null,
    @Json(name = "lng") var lng: Double? = null
)

@JsonClass(generateAdapter = true)
data class Step(
    @Json(name = "distance") var distance: Distance? = null,
    @Json(name = "duration") var duration: Duration? = null,
    @Json(name = "end_location") var endLocation: EndLocation? = null,
    @Json(name = "html_instructions") var htmlInstructions: String? = null,
    @Json(name = "polyline") var polyline: Polyline? = null,
    @Json(name = "start_location") var startLocation: StartLocation? = null,
    @Json(name = "travel_mode") var travelMode: String? = null,
    @Json(name = "maneuver") var maneuver: String? = null
)

@JsonClass(generateAdapter = true)
data class Polyline(
    @Json(name = "points") var points: String? = null
)

@JsonClass(generateAdapter = true)
data class OverviewPolyline(
    @Json(name = "points") var points: String? = null
)