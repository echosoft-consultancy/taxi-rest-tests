package taxi.rest.tests

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.httpPost
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class PassengerTest {

    init {
        RestAssured.port = 4567
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
        val response = """
                            [
                               {
                                  "email":"test@example.com",
                                  "lat":"10",
                                  "lon":"12"
                               },
                               {
                                  "email":"test@example.com",
                                  "lat":"11",
                                  "lon":"10"
                               }
                            ]
                        """
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
        assertThat(body.getString("[0].email")).isEqualTo("test@example.com")
        assertThat(body.getString("[0].coordinates.lat")).isEqualTo("10.0")
        assertThat(body.getString("[0].coordinates.lon")).isEqualTo("10.0")
        assertThat(body.getString("[1].email")).isEqualTo("test@example.com")
        assertThat(body.getString("[1].coordinates.lat")).isEqualTo("10.0")
        assertThat(body.getString("[1].coordinates.lon")).isEqualTo("10.0")
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