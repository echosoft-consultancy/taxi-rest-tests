package taxi.rest.tests

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.httpPost
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Test

class PassengerTest {

    init {
        RestAssured.port = 4567
    }

    @After
    fun clean() {
        "http://localhost:9200/taxi/_delete_by_query?refresh=wait_for".httpPost().body("""
            {
                "query" : {
                    "match_all": {}
                }
            }
        """.trimIndent())
            .authenticate("elastic", "changeme")
            .response()
    }

    @Test
    fun shouldAcceptPassengerLocation() {
        given().
                body(jacksonObjectMapper().writeValueAsString(mapOf("lat" to 10, "lon" to 10)))
        .`when`()
                .post("/passenger/coordinate")
        .then()
                .assertThat()
                .statusCode(201)
    }

    @Test
    fun shouldRequestRide() {
        given().
                body(jacksonObjectMapper().writeValueAsString(mapOf("lat" to 10, "lon" to 10)))
        .`when`()
                .post("/passenger/ride")
        .then()
                .assertThat()
                .statusCode(201)
    }

    @Test
    fun shouldFindRideRequest() {
        // Given
        // Passenger request ride
        "http://localhost:4567/passenger/ride".httpPost().body("""
            {
                "lat": 10,
                "lon": 10
            }
        """).response()
        val body = given()
                    .queryParam("lat", 10)
                    .queryParam("lon", 10)
                .`when`()
                    .get("/driver/ride")
                .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .jsonPath()
        assertThat(body.getInt("$.size()")).isEqualTo(1)
        assertThat(body.getString("[0].email")).isEqualTo("test@example.com")
        assertThat(body.getString("[0].coordinates.lat")).isEqualTo("10.0")
        assertThat(body.getString("[0].coordinates.lon")).isEqualTo("10.0")
    }

    @Test
    fun shouldLatestCoordinatesOfRideRequest() {
        // Given
        // Passenger request ride
        "http://localhost:4567/passenger/ride".httpPost().body("""
            {
                "lat": 10,
                "lon": 10
            }
        """).response()
        "http://localhost:4567/passenger/ride".httpPost().body("""
            {
                "lat": 10.01,
                "lon": 10.01
            }
        """).response()
        val body = given()
                    .queryParam("lat", 10)
                    .queryParam("lon", 10)
                .`when`()
                    .get("/driver/ride")
                .then()
                    .assertThat()
                    .statusCode(200)
                    .extract()
                    .body()
                    .jsonPath()
        assertThat(body.getInt("$.size()")).isEqualTo(1)
        assertThat(body.getString("[0].email")).isEqualTo("test@example.com")
        assertThat(body.getString("[0].coordinates.lat")).isEqualTo("10.01")
        assertThat(body.getString("[0].coordinates.lon")).isEqualTo("10.01")
    }

    @Test
    fun shouldOfferRide() {
        // Given
        // Passenger request ride
        "http://localhost:4567/passenger/ride".httpPost().body("""
            {
                "lat": 10,
                "lon": 10
            }
        """).response()
        given().
                body(jacksonObjectMapper().writeValueAsString(mapOf("email" to "fred@example.com")))
        .`when`()
                .post("/driver/ride/offer")
        .then()
                .assertThat()
                .statusCode(201)
    }

}