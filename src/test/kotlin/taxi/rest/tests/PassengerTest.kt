package taxi.rest.tests

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.kittinunf.fuel.httpPost
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.hamcrest.Matchers.equalToIgnoringWhiteSpace
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
                                  "email":"fred@example.com",
                                  "lat":"10",
                                  "lon":"12"
                               },
                               {
                                  "email":"jon@example.com",
                                  "lat":"11",
                                  "lon":"10"
                               }
                            ]
                        """
        // Given
        "http://localhost:4567/passenger/ride".httpPost().body("""
            {
                "lat": 10,
                "lon:" 12
            }
        """)
        "http://localhost:4567/passenger/ride".httpPost().body("""
            {
                "lat": 11,
                "lon:" 10
            }
        """)
        given()
                .param("lat", 10)
                .param("lon", 10)
        .`when`()
                .get("/driver/ride")
        .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body(equalToIgnoringWhiteSpace(response))
    }

    @Test
    fun shouldOfferRide() {
        // Given
        "http://localhost:4567/passenger/ride".httpPost().body("""
            {
                "lat": 10,
                "lon:" 12
            }
        """)
        given().
                body(jacksonObjectMapper().writeValueAsString(mapOf("email" to "fred@example.com")))
        .`when`()
                .post("/driver/ride/offer")
        .then()
                .assertThat()
                .statusCode(201)
    }

}