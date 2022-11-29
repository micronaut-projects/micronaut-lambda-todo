package example.micronaut;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.Random;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.constantConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.rampConcurrentUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.header;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class TodoSimulation extends Simulation {
    private static final String API_URI = "API_URL";
    private static final String TEST_SUITE = "TEST_SUITE";
    {
        String apiUrl = System.getenv(API_URI);
        if (apiUrl == null || apiUrl.isBlank()) {
            System.out.println("Environment variable " + API_URI + " does not exist");
        } else {
            HttpProtocolBuilder httpProtocol = http.baseUrl(apiUrl);
            String name = System.getenv(TEST_SUITE) != null ? System.getenv(TEST_SUITE) : "Simple";
            ScenarioBuilder scn = scenario(name)
                    .exec(http("HelloWorld")
                                    .get("/")
                                    .asJson()
                                    .check(
                                            status().is(200)
                                    )
                    );
            setUp(scn.injectClosed(
                            constantConcurrentUsers(50).during(30),
                            rampConcurrentUsers(50).to(100).during(30)
                            )
                    .protocols(httpProtocol)
            );
        }
    }

}
