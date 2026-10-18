package MyFirstTestAppSpringBoot.service;

import MyFirstTestAppSpringBoot.model.Request;
import MyFirstTestAppSpringBoot.model.Systems;
import MyFirstTestAppSpringBoot.model.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service("ModifySystemNameRequestService")
public class ModifySystemNameRequestService implements ModifyRequestService {
    private final ModifyRequestService sourceModifier;
    private final HttpServletRequest servletRequest;
    private final String targetUrl;
    private final RestTemplate restTemplate;

    public ModifySystemNameRequestService(
            @Qualifier("ModifySourceRequestService") ModifyRequestService sourceModifier,
            HttpServletRequest servletRequest,
            @Value("${service2.url:http://localhost:8084/feedback}") String targetUrl) {
        this.sourceModifier = sourceModifier;
        this.servletRequest = servletRequest;
        this.targetUrl = targetUrl;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(factory);
    }

    @Override
    public void modify(Request request) {
        Systems previous = request.getSystemName();
        request.setSystemName(Systems.SERVICE1);
        log.info("Изменён request.systemName: {} -> {}", previous, request.getSystemName());
        sourceModifier.modify(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Service1-Received-At", String.valueOf(servletRequest.getAttribute("service1ReceivedAt")));
        log.info("Отправка изменённого request Сервису 2: {}", request);
        ResponseEntity<Response> result = restTemplate.exchange(targetUrl, HttpMethod.POST,
                new HttpEntity<>(request, headers), Response.class);
        log.info("Ответ Сервиса 2: HTTP {}, {}", result.getStatusCode().value(), result.getBody());
        if (result.getBody() == null || result.getBody().getCode() != MyFirstTestAppSpringBoot.model.Codes.SUCCESS) {
            throw new IllegalStateException("Сервис 2 не подтвердил успешную обработку");
        }
    }
}