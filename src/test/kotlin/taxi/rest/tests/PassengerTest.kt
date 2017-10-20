package taxi.rest.tests

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.junit.Test

class PassengerTest {

    @Test
    fun shouldWork() {
        RestAssured.port = 4567
        given().
                body(jacksonObjectMapper().writeValueAsString(mapOf("lat" to 10, "lon" to 10)))
                .`when`()
                .post("/passenger/coordinate")
                .then()
                .assertThat()
                .statusCode(201)
    }

}