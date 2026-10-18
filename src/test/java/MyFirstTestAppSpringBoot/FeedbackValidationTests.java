package MyFirstTestAppSpringBoot;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "app.forwarding.enabled=false")
class FeedbackValidationTests {
    @LocalServerPort
    private int port;

    @TestFactory
    List<DynamicTest> postmanCasesOverHttp() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode collection = mapper.readTree(Files.readString(Path.of("Lab5.postman_collection.json")));
        List<DynamicTest> tests = new ArrayList<>();
        for (JsonNode item : collection.path("item").get(0).path("item")) {
            tests.add(DynamicTest.dynamicTest(item.path("name").asText(), () -> {
                HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/feedback"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(item.path("request").path("body").path("raw").asText()))
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                JsonNode body = mapper.readTree(response.body());
                String checks = item.path("event").get(0).path("script").path("exec").toString();
                var status = Pattern.compile("to.have.status\\((\\d+)\\)").matcher(checks);
                assertTrue(status.find());
                assertEquals(Integer.parseInt(status.group(1)), response.statusCode(), response.body());
                var errorCode = Pattern.compile("body.errorCode\\).to.eql\\('([^']*)'\\)").matcher(checks);
                assertTrue(errorCode.find());
                assertEquals(errorCode.group(1), body.path("errorCode").asText());
                assertEquals(response.statusCode() == 200 ? "success" : "failed", body.path("code").asText());
                if (response.statusCode() == 200) {
                    JsonNode input = mapper.readTree(item.path("request").path("body").path("raw").asText());
                    var position = MyFirstTestAppSpringBoot.model.Positions.valueOf(input.path("position").asText());
                    double expected = input.path("salary").asDouble() * input.path("bonus").asDouble()
                            * java.time.Year.now(java.time.Clock.systemUTC()).length()
                            * position.getPositionCoefficient() / input.path("workDays").asInt();
                    assertEquals(expected, body.path("annualBonus").asDouble(), 0.000001);
                    if (position.isManager()) assertEquals(expected / 4, body.path("quarterlyBonus").asDouble(), 0.000001);
                    else assertTrue(body.path("quarterlyBonus").isNull());
                }
                var fields = Pattern.compile("to.include\\('([^']*)'\\)").matcher(checks);
                while (fields.find()) {
                    assertEquals("Ошибка валидации", body.path("errorMessage").asText());
                }
            }));
        }
        return tests;
    }
}
