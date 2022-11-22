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
                    .exec(http("CreateTodo")
                            .post("/api/v1/todo")
                            .body(StringBody("{ \"task\": \" " + randomAlphanumeric(20) + " \" }"))
                            .asJson()
                            .check(
                                    status().is(201),
                                    header("Location").saveAs("loc")
                            )
                    ).exec(http("ListTodo")
                                    .get("/api/v1/todo")
                                    .asJson()
                                    .check(
                                            status().is(200)
                                    )
                    ).exec(http("DeleteTodo")
                            .delete(session -> session.get("loc"))
                            .asJson()
                            .check(
                                    status().is(204)
                            ));

            setUp(scn.injectClosed(
                            constantConcurrentUsers(50).during(7200), // 1
                            rampConcurrentUsers(50).to(100).during(120) // 2
                            )
                    .protocols(httpProtocol)
            );
        }
    }

    private static String randomAlphanumeric(int length) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
}
